package com.idcom4.exceptions;

public class InvalidOperatorException extends TokenParsingException {
    public InvalidOperatorException(String message) {
        super(message);
    }

    public InvalidOperatorException(Throwable cause) {
        super(cause);
    }

    public InvalidOperatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
