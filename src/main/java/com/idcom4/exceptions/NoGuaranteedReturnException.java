package com.idcom4.exceptions;

public class NoGuaranteedReturnException extends CompilationException {
    public NoGuaranteedReturnException(String message) {
        super(message);
    }

    public NoGuaranteedReturnException(Throwable cause) {
        super(cause);
    }

    public NoGuaranteedReturnException(String message, Throwable cause) {
        super(message, cause);
    }
}
