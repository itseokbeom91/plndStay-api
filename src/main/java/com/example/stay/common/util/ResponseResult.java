package com.example.stay.common.util;

import lombok.Data;

@Data
public class ResponseResult<T> {

    private T result;
    private String message;
    private String statusCode;

//    public ResponseResult(){
//
//    }

    // status, message 보내는 경우
    public ResponseResult(String statusCode, String message){
        this.statusCode = statusCode;
        this.message = message;
    }

    public ResponseResult(String statusCode, T result){
        this.statusCode = statusCode;
        this.result = result;
    }



}
