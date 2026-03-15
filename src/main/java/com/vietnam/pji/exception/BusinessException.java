package com.vietnam.pji.exception;

public class BusinessException extends RuntimeException{
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String number, String unableToUploadFile, Exception ex) {
    }
}
