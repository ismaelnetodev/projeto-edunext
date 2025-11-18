package com.educamais.app.exceptions;

//Exceção personzalizada Base
public class BusinessException extends RuntimeException{
    public BusinessException(String message){
        super(message);
    }

    public BusinessException(String message, Throwable cause){
        super(message, cause);
    }
}
