package com.idcom4.exceptions;

public class ReservedKeywordException extends TokenParsingException {

    public ReservedKeywordException(String message) {
        super(message);
    }

    public ReservedKeywordException(Throwable cause) {
        super(cause);
    }

    public ReservedKeywordException(String message, Throwable cause) {
        super(message, cause);
    }
}
