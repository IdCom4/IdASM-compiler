package com.idcom4.exceptions;

public class InvalidLabelException extends TokenParsingException {
    public InvalidLabelException(String message) {
        super(message);
    }

    public InvalidLabelException(Throwable cause) {
        super(cause);
    }

    public InvalidLabelException(String message, Throwable cause) {
        super(message, cause);
    }
}
