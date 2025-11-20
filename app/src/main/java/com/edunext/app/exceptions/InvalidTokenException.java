package com.edunext.app.exceptions;

// Token inválido
public class InvalidTokenException extends BusinessException {
    public InvalidTokenException() {
        super("Token inválido ou expirado");
    }
    
    public InvalidTokenException(String message) {
        super(message);
    }
}