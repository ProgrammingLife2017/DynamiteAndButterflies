package exceptions;

/**
 * Created by TUDelft SID on 20-6-2017.
 */
public class NotInRangeException extends Exception {

    public NotInRangeException() {}

    public NotInRangeException(String message) {
        super(message);
    }
}