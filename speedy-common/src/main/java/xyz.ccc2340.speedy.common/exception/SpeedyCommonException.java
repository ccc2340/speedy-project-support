package xyz.ccc2340.speedy.common.exception;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/08 10:29
 */
public class SpeedyCommonException extends RuntimeException {

    public SpeedyCommonException(String message) {
        super(message);
    }

    public SpeedyCommonException(Throwable e) {
        this(e, null);
    }

    public SpeedyCommonException(Throwable e, String message) {
        super(message, e);
    }
}
