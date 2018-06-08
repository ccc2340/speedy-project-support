package xyz.ccc2340.speedy.data.repository;

import org.springframework.data.domain.Page;
import xyz.ccc2340.speedy.data.model.PageModel;
import xyz.ccc2340.speedy.data.model.QueryCondition;

import javax.persistence.criteria.Order;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/08 09:42
 */
public interface CustomBaseRepository {

    /* 保存新对象，返回的对象为保存之后的对象，其中包含主键 */
    <T> Optional<T> insert(T t);

    /* 根据主键删除数据 */
    <T> void deleteByPrimary(Class<T> clazz, Serializable primary);

    /* 根据对象删除数据，但是必须包含主键，否则会抛出异常 */
    <T> void deleteByObject(T t);

    /* 以主键为条件，修改对象的其他数据 */
    <T> void update(T t);

    /* 以指定对象为条件，查询符合条件的对象集合 */
    <T> List<T> selectByObject(T example);

    /* 以主键为条件，查询符合条件的对象 */
    <T> Optional<T> selectByPrimary(Class<T> clazz, Serializable primary);

    /* 分页数据查询，以对象为条件，不指定排序 */
    <T> PageModel pageByObject(T example, int index, int cpp);

    /* 分页数据查询，以对象为条件，指定排序 */
    <T> PageModel pageByObject(T example, int index, int cpp, Order... orders);

    /* 分页数据查询，以复杂条件为条件，不指定排序 */
    <T> PageModel pageByCondition(QueryCondition condition, int index, int cpp);

    /* 分页数据查询，以复杂条件为条件，指定排序 */
    <T> PageModel pageByCondition(QueryCondition condition, int index, int cpp, Order... orders);
}
