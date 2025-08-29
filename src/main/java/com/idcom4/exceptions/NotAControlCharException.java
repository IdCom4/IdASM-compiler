package com.idcom4.exceptions;

public class NotAControlCharException extends CompilationException {

    public NotAControlCharException(String message) {
        super(message);
    }

    public NotAControlCharException(Throwable cause) {
        super(cause);
    }

    public NotAControlCharException(String message, Throwable cause) {
        super(message, cause);
    }
}
