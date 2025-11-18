package com.educamais.app.exceptions;

// Dados inválidos
public class InvalidDataException extends BusinessException {
    public InvalidDataException(String message) {
        super(message);
    }
}