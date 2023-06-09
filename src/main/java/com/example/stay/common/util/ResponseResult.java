package com.example.stay.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> {

    private String statusCode;
    private String message;
    private T result;

//    public ResponseResult(){
//
//    }

    // status, message 보내는 경우
    public ResponseResult(String statusCode, String message){
        this.statusCode = statusCode;
        this.message = message;
    }

    public ResponseResult(String statusCode, String message, T result){
        this.statusCode = statusCode;
        this.message = message;
        this.result = result;
    }



}
