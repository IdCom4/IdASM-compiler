package com.idcom4.exceptions;

public class UnknownStaticAddressException extends TokenParsingException {
    public UnknownStaticAddressException(String message) {
        super(message);
    }

    public UnknownStaticAddressException(Throwable cause) {
        super(cause);
    }

    public UnknownStaticAddressException(String message, Throwable cause) {
        super(message, cause);
    }
}
