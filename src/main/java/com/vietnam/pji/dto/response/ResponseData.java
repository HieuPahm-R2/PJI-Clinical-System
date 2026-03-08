package com.vietnam.pji.dto.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
public class ResponseData<T> implements Serializable {
    private final int status;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL) // if data get null, not show in response!
    private T data;

    /**
     * Response data for the API to retrieve data successfully. For GET, POST only
     * 
     * @param status
     * @param message
     * @param data
     */
    public ResponseData(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    /**
     * Response data when the API executes successfully or getting error. For PUT,
     * PATCH, DELETE
     * 
     * @param status
     * @param message
     */
    public ResponseData(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
