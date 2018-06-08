package xyz.ccc2340.speedy.data.exception;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/08 14:29
 */
public class SpeedyDataException extends RuntimeException {

    public SpeedyDataException(Throwable e) {
        this(e, null);
    }

    public SpeedyDataException(String message) {
        super(message);
    }

    public SpeedyDataException(Throwable e, String message) {
        super(message, e);
    }
}
