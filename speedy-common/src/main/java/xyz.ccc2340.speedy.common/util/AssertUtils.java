package xyz.ccc2340.speedy.common.util;

import org.springframework.util.Assert;

/**
 * @Description 断言工具包
 * @Author chenguangxue
 * @CreateDate 2018/06/08 10:20
 */
public class AssertUtils {

    public static void notNull(Object... objects) {
        for (Object o : objects) {
            Assert.notNull(o, "[Assertion failed] - this argument must not be null");
        }
    }
}
