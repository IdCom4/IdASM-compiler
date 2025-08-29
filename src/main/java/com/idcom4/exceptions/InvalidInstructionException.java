package com.idcom4.exceptions;

public class InvalidInstructionException extends TokenParsingException {
    public InvalidInstructionException(String message) {
        super(message);
    }

    public InvalidInstructionException(Throwable cause) {
        super(cause);
    }

    public InvalidInstructionException(String message, Throwable cause) {
        super(message, cause);
    }
}
