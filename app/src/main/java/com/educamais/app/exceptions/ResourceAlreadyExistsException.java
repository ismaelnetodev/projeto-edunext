package com.educamais.app.exceptions;

//Recurso já existe
public class ResourceAlreadyExistsException extends BusinessException {
    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s já existe com %s: '%s'", resourceName, fieldName, fieldValue));
    }
    
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}