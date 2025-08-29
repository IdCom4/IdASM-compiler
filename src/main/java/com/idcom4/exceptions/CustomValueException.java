package com.idcom4.exceptions;

public class CustomValueException extends TokenParsingException {
    public CustomValueException(String message) {
        super(message);
    }

    public CustomValueException(Throwable cause) {
        super(cause);
    }

    public CustomValueException(String message, Throwable cause) {
        super(message, cause);
    }
}
