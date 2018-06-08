package xyz.ccc2340.speedy.data.annotation;

import java.lang.annotation.*;

/**
 * @Description 用于标记对应的实体类型
 * @Author chenguangxue
 * @CreateDate 2018/06/08 15:12
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface MappingClass {

    Class<?> clazz();

}
