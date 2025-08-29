package com.idcom4.exceptions;

public class EmptyScopeException extends CompilationException {
    public EmptyScopeException(String message) {
        super(message);
    }

    public EmptyScopeException(Throwable cause) {
        super(cause);
    }

    public EmptyScopeException(String message, Throwable cause) {
        super(message, cause);
    }
}
