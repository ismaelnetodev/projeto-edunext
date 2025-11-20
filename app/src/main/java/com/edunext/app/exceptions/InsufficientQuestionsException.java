package com.edunext.app.exceptions;

// Questões insuficientes
public class InsufficientQuestionsException extends BusinessException {
    public InsufficientQuestionsException(int required, int available) {
        super(String.format("Questões insuficientes. Necessário: %d, Disponível: %d", required, available));
    }
}