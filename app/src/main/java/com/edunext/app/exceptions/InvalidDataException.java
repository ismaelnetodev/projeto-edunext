package com.edunext.app.exceptions;

// Dados inválidos
public class InvalidDataException extends BusinessException {
    public InvalidDataException(String message) {
        super(message);
    }
}