package com.idcom4.exceptions;

public class DuplicateIdentifierException extends CompilationException {
    public DuplicateIdentifierException(String message) {
        super(message);
    }

    public DuplicateIdentifierException(Throwable cause) {
        super(cause);
    }

    public DuplicateIdentifierException(String message, Throwable cause) {
        super(message, cause);
    }
}
