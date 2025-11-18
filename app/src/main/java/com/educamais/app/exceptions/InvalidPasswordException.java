package com.educamais.app.exceptions;

// Senha inválida
public class InvalidPasswordException extends BusinessException {
    public InvalidPasswordException() {
        super("Senha incorreta");
    }
    
    public InvalidPasswordException(String message) {
        super(message);
    }
}