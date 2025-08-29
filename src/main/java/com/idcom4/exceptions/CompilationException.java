package com.idcom4.exceptions;

public class CompilationException extends IDException {
    public CompilationException(String message) {
        super(message);
    }

    public CompilationException(Throwable cause) {
        super(cause);
    }

    public CompilationException(String message, Throwable cause) {
        super(message, cause);
    }   
}
