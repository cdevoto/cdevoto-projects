package json;

import java.io.IOException;

public class JsonLogFormatException extends IOException {

    private static final long serialVersionUID = 1L;

    public JsonLogFormatException() {
        super();
    }

    public JsonLogFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonLogFormatException(String message) {
        super(message);
    }

    public JsonLogFormatException(Throwable cause) {
        super(cause);
    }

}
