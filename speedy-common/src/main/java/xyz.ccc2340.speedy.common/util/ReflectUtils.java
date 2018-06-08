package xyz.ccc2340.speedy.common.util;

import xyz.ccc2340.speedy.common.exception.SpeedyCommonException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Description 反射工具包
 * @Author chenguangxue
 * @CreateDate 2018/06/08 10:05
 */
public class ReflectUtils {

    private static final Class<?>[] EMPTY_PARAMETER_TYPES = new Class<?>[0];

    /* 通过反射，直接获取属性值 */
    public static Object directGetFieldValue(Object object, Field field) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new SpeedyCommonException(e);
        }
    }

    /* 通过getter方法，获取属性值 */
    public static Object methodGetFieldValue(Object object, Field field) {
        Method getterMethod = findGetterMethod(object.getClass(), field);
        if (getterMethod == null) {
            String message = String.format("field [%s] no getter method find in class [%s]", field.getName(), object.getClass());
            throw new NullPointerException(message);
        }
        try {
            return getterMethod.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new SpeedyCommonException(e);
        }
    }

    /* 通过反射，直接设置属性值 */
    public static void directSetFieldValue(Object object, Field field, Object fieldValue) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            field.set(object, fieldValue);
        } catch (IllegalAccessException e) {
            throw new SpeedyCommonException(e);
        }
    }

    /* 通过setter方法，设置属性值 */
    public static void methodSetFieldValue(Object object, Field field, Object fieldValue) {
        Method setterMethod = findSetterMethod(object.getClass(), field);
        if (setterMethod == null) {
            String message = String.format("field [%s] no setter method find in class [%s]", field.getName(), object.getClass());
            throw new NullPointerException(message);
        }
        try {
            setterMethod.invoke(object, fieldValue);
        } catch (Exception e) {
            throw new SpeedyCommonException(e);
        }
    }

    /* 获取属性的getter方法 */
    public static Method findGetterMethod(Class<?> clazz, Field field) {
        String fieldName = field.getName();
        // 选择getter方法前缀，如果是boolean类型的字段，则以is开头
        String prefix = "get";
        if (field.getType() == boolean.class) {
            prefix = "is";
        }
        String getterMethodName = String.format("%s%s%s", prefix, fieldName.toUpperCase().charAt(0), fieldName.substring(1));
        return findMethod(clazz, getterMethodName, EMPTY_PARAMETER_TYPES);
    }

    /* 获取属性的setter方法 */
    public static Method findSetterMethod(Class<?> clazz, Field field) {
        String fieldName = field.getName();
        String setterMethodName = String.format("%s%s%s", "set", fieldName.toUpperCase().charAt(0), fieldName.substring(1));
        return findMethod(clazz, setterMethodName, field.getType());
    }

    /* 通过反射，查找指定名称的方法 */
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new SpeedyCommonException(e);
        }
    }

    /* 通过反射创建对象 */
    public static <T> T newInstance(Class<T> clazz, Object... args) {
        // 整理参数的类型列表
        Class<?>[] argsTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            argsTypes[i] = args[i].getClass();
        }

        // 根据参数，查找对应的构造方法
        Constructor constructor = null;
        try {
            constructor = clazz.getConstructor(argsTypes);
        } catch (NoSuchMethodException e) {
            throw new SpeedyCommonException(e);
        }

        // 使用构造方法创建对象
        try {
            return (T) constructor.newInstance(args);
        } catch (Exception e) {
            throw new SpeedyCommonException(e);
        }
    }
}
