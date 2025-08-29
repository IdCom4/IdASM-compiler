package com.idcom4.exceptions;

public class MissingClosureException extends CompilationException {
    public MissingClosureException(String message) {
        super(message);
    }

    public MissingClosureException(Throwable cause) {
        super(cause);
    }

    public MissingClosureException(String message, Throwable cause) {
        super(message, cause);
    }
}
