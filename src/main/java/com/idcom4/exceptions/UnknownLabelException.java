package com.idcom4.exceptions;

public class UnknownLabelException extends TokenParsingException {

    public UnknownLabelException(String message) {
        super(message);
    }

    public UnknownLabelException(Throwable cause) {
        super(cause);
    }

    public UnknownLabelException(String message, Throwable cause) {
        super(message, cause);
    }
}
