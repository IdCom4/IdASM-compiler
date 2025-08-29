package com.idcom4.exceptions;

public class NotAKeywordException extends CompilationException {

    public NotAKeywordException(String message) {
        super(message);
    }

    public NotAKeywordException(Throwable cause) {
        super(cause);
    }

    public NotAKeywordException(String message, Throwable cause) {
        super(message, cause);
    }
}
