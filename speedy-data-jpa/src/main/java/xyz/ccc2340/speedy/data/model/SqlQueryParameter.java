package xyz.ccc2340.speedy.data.model;

import lombok.Getter;
import xyz.ccc2340.speedy.data.annotation.MappingClass;

import javax.persistence.criteria.Order;

/**
 * @Description SQL语句执行查询时的参数集合
 * @Author chenguangxue
 * @CreateDate 2018/06/07 23:31
 */
@Getter
public class SqlQueryParameter {
    /* 包含条件数据的对象 */
    private Object parameterObject;
    private ParameterType parameterType;
    private Class<?> parameterClass;

    /* 分页数据 */
    private boolean page;
    private int index;
    private int cpp;
    private int offset;

    /* 排序数据 */
    private Order[] orders;

    /* 是否显示不重复数据 */
    private boolean distinct;

    /* 构造方法私有化，防止直接创建对象，必须通过builder创建对象 */
    private SqlQueryParameter() {
        this.orders = new Order[0];
    }

    /* 提供常见参数的静态方法1 */
    public static SqlQueryParameter ofExample(Object example) {
        return Builder.start().withExample(example).complete();
    }

    /* 提供常见参数的静态方法2 */
    public static SqlQueryParameter ofCondition(QueryCondition condition) {
        return Builder.start().withCondition(condition).complete();
    }

    public static enum ParameterType {
        EXAMPLE, CONDITION,
    }

    /* 构造器 */
    public static class Builder {
        // 构造器也不允许直接创建对象
        private Builder() {
        }

        private SqlQueryParameter sqlQueryParameter;

        public Builder withExample(Object example) {
            this.sqlQueryParameter.parameterObject = example;
            this.sqlQueryParameter.parameterType = ParameterType.EXAMPLE;
            this.sqlQueryParameter.parameterClass = example.getClass();
            return this;
        }

        public Builder withCondition(QueryCondition condition) {
            this.sqlQueryParameter.parameterObject = condition;
            this.sqlQueryParameter.parameterType = ParameterType.CONDITION;
            MappingClass annotation = condition.getClass().getAnnotation(MappingClass.class);
            this.sqlQueryParameter.parameterClass = annotation.clazz();
            return this;
        }

        public Builder initPageInfo(int index, int cpp) {
            this.sqlQueryParameter.index = index;
            this.sqlQueryParameter.cpp = cpp;
            this.sqlQueryParameter.offset = (index - 1) * cpp;
            this.sqlQueryParameter.page = true;
            return this;
        }

        public Builder withOrders(Order... orders) {
            if (orders != null && orders.length > 0) {
                this.sqlQueryParameter.orders = orders;
            }
            return this;
        }

        public Builder needDistinct() {
            this.sqlQueryParameter.distinct = true;
            return this;
        }

        public SqlQueryParameter complete() {
            return this.sqlQueryParameter;
        }

        public static Builder start() {
            Builder builder = new Builder();
            builder.sqlQueryParameter = new SqlQueryParameter();
            return builder;
        }
    }
}
