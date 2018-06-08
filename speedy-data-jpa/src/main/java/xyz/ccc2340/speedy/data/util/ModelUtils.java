package xyz.ccc2340.speedy.data.util;

import xyz.ccc2340.speedy.common.exception.SpeedyCommonException;
import xyz.ccc2340.speedy.common.util.ReflectUtils;

import javax.persistence.Id;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Optional;

/**
 * @Description 操作Model的工具类
 * @Author chenguangxue
 * @CreateDate 2018/06/08 11:39
 */
public class ModelUtils {

    /* 查找对象中的主键 */
    /* 查询顺序：1、带有@Id的字段；2、名称为id的字段；3、名称为类名+Id的字段 */
    public static Field getPrimaryField(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Field[] primaryFields = new Field[3];

        String idFieldName = "id";
        String classNameIdFieldName = clazz.getSimpleName() + "Id";
        for (Field f : fields) {
            if (f.isAnnotationPresent(Id.class)) {
                primaryFields[0] = f;
                break;
            }
            if (idFieldName.equals(f.getName())) {
                primaryFields[1] = f;
            }
            if (classNameIdFieldName.equals(f.getName())) {
                primaryFields[2] = f;
            }
        }

        for (Field f : primaryFields) {
            if (f != null) {
                return f;
            }
        }
        return null;
    }

    /* 获取对象中主键的值 */
    public static Serializable getPrimaryValue(Object object) {
        Field primaryField = getPrimaryField(object.getClass());
        return (Serializable) ReflectUtils.directGetFieldValue(object, primaryField);
    }

    /* 创建一个包含主键的对象 */
    public static <T> T createObjectWithPrimary(Class<T> clazz, Serializable primary) {
        Field primaryField = getPrimaryField(clazz);
        T t = ReflectUtils.newInstance(clazz);
        ReflectUtils.methodSetFieldValue(t, primaryField, primary);
        return t;
    }

    /* 检查对象中是否包含主键 */
    public static boolean containPrimaryField(Class<?> clazz) {
        Field primaryField = getPrimaryField(clazz);
        return primaryField != null;
    }
}
