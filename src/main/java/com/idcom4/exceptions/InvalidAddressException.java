package com.idcom4.exceptions;

public class InvalidAddressException extends TokenParsingException {
    public InvalidAddressException(String message) {
        super(message);
    }

    public InvalidAddressException(Throwable cause) {
        super(cause);
    }

    public InvalidAddressException(String message, Throwable cause) {
        super(message, cause);
    }
}
