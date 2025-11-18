package com.educamais.app.exceptions;

// Operação não autorizada
public class UnauthorizedOperationException extends BusinessException {
    public UnauthorizedOperationException(String message) {
        super(message);
    }
    
    public UnauthorizedOperationException() {
        super("Você não tem permissão para realizar esta operação");
    }
}