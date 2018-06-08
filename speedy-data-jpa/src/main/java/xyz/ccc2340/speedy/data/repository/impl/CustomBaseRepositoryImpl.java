package xyz.ccc2340.speedy.data.repository.impl;

import xyz.ccc2340.speedy.common.util.ReflectUtils;
import xyz.ccc2340.speedy.data.exception.SpeedyDataException;
import xyz.ccc2340.speedy.data.model.PageModel;
import xyz.ccc2340.speedy.data.model.QueryCondition;
import xyz.ccc2340.speedy.data.model.SqlQueryParameter;
import xyz.ccc2340.speedy.data.repository.CustomBaseRepository;
import xyz.ccc2340.speedy.data.util.ModelUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/08 09:58
 */
public class CustomBaseRepositoryImpl implements CustomBaseRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public <T> Optional<T> insert(T t) {
        T mergeT = em.merge(t);
        return Optional.ofNullable(mergeT);
    }

    @Override
    public <T> void deleteByPrimary(Class<T> clazz, Serializable primary) {
        T t = ModelUtils.createObjectWithPrimary(clazz, primary);
        deleteByObject(t);
    }

    @Override
    public <T> void deleteByObject(T t) {
        checkPrimary(t);
        em.remove(t);
    }

    /* 执行删除、修改时必须包含主键 */
    private void checkPrimary(Object object) {
        if (!ModelUtils.containPrimaryField(object.getClass())) {
            String message = String.format("class [%s] has no primary field", object.getClass());
            throw new SpeedyDataException(message);
        }
    }

    @Override
    public <T> void update(T t) {
        checkPrimary(t);
        em.refresh(t);
    }

    @Override
    public <T> List<T> selectByObject(T example) {
        SqlQueryParameter sqlQueryParameter = SqlQueryParameter.ofExample(example);
        return (List<T>) criteriaDataQuery(sqlQueryParameter).getData();
    }

    @Override
    public <T> Optional<T> selectByPrimary(Class<T> clazz, Serializable primary) {
        T t = ModelUtils.createObjectWithPrimary(clazz, primary);
        List<T> list = selectByObject(t);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list.get(0));
    }

    @Override
    public <T> PageModel pageByObject(T example, int index, int cpp) {
        SqlQueryParameter sqlQueryParameter = SqlQueryParameter.Builder.
                start().withExample(example).initPageInfo(index, cpp).complete();
        return criteriaDataQuery(sqlQueryParameter);
    }

    @Override
    public <T> PageModel pageByObject(T example, int index, int cpp, Order... orders) {
        SqlQueryParameter sqlQueryParameter = SqlQueryParameter.Builder.
                start().withExample(example).initPageInfo(index, cpp).withOrders(orders).complete();
        return criteriaDataQuery(sqlQueryParameter);
    }

    @Override
    public <T> PageModel pageByCondition(QueryCondition condition, int index, int cpp) {
        SqlQueryParameter sqlQueryParameter = SqlQueryParameter.Builder.
                start().withCondition(condition).initPageInfo(index, cpp).complete();
        return criteriaDataQuery(sqlQueryParameter);
    }

    @Override
    public <T> PageModel pageByCondition(QueryCondition condition, int index, int cpp, Order... orders) {
        SqlQueryParameter sqlQueryParameter = SqlQueryParameter.Builder.
                start().withCondition(condition).initPageInfo(index, cpp).withOrders(orders).complete();
        return criteriaDataQuery(sqlQueryParameter);
    }

    /* 将查询条件综合起来执行数据查询操作 */
    private PageModel criteriaDataQuery(SqlQueryParameter queryParameter) {
        CriteriaBuilder sqlBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<Object> dataSql = sqlBuilder.createQuery();

        Root<?> root = rootInfo(queryParameter, dataSql);
        Predicate[] predicates = predicate(queryParameter, sqlBuilder, root);

        dataSql.where(predicates);
        dataSql.select(root);
        dataSql.orderBy(queryParameter.getOrders());

        PageModel pm = new PageModel();

        TypedQuery<Object> dataQuery = em.createQuery(dataSql);
        Long count = 0L;
        if (queryParameter.isPage()) {
            dataQuery.setFirstResult(queryParameter.getOffset()).setMaxResults(queryParameter.getCpp());
            count = criteriaCountQuery(root, predicates);
            pm.setTotalCount(count);
            pm.setIndex(queryParameter.getIndex());
            pm.setCpp(queryParameter.getCpp());
        }

        List<Object> dataList = dataQuery.getResultList();
        pm.setData(dataList);

        return pm;
    }

    private Long criteriaCountQuery(Root<?> root, Predicate[] predicates) {
        CriteriaBuilder sqlBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> countSql = sqlBuilder.createQuery(Long.class);
        countSql.select(sqlBuilder.countDistinct(root));
        countSql.where(predicates);
        return em.createQuery(countSql).getSingleResult();
    }

    // 获取类信息
    private Root<?> rootInfo(SqlQueryParameter parameter, CriteriaQuery<?> sql) {
        return sql.from(parameter.getParameterClass());
    }

    // 提取条件数据
    private Predicate[] predicate(SqlQueryParameter parameter, CriteriaBuilder sqlBuilder, Root<?> root) {
        Object object = parameter.getParameterObject();
        switch (parameter.getParameterType()) {
            case EXAMPLE: {
                return examplePredicate(object, sqlBuilder, root);
            }
            case CONDITION: {
                return conditionPredicate(object, sqlBuilder, root);
            }
            default:
                return new Predicate[0];
        }
    }

    // 对应实体类作为条件对象，则将非空的值全部提取出，使用eq
    private Predicate[] examplePredicate(Object object, CriteriaBuilder sqlBuilder, Root<?> root) {
        Field[] fields = object.getClass().getDeclaredFields();
        List<Predicate> predicates = new ArrayList<>(fields.length);
        for (Field f : fields) {
            Object fieldValue = ReflectUtils.directGetFieldValue(object, f);
            if (fieldValue != null) {
                String fieldName = f.getName();
                Predicate predicate = sqlBuilder.equal(root.get(fieldName), fieldValue);
                predicates.add(predicate);
            }
        }
        return predicates.toArray(new Predicate[]{});
    }

    // 对应条件类作为条件对象，则将非空的值全部提取出，按照值的方式使用比较类型
    private Predicate[] conditionPredicate(Object object, CriteriaBuilder sqlBuilder, Root<?> root) {
        return new Predicate[0];
    }
}
