package com.example.stay.common.util;

import lombok.Data;

@Data
public class ResponseResult<T> {
    private boolean status;
    private T result;
    private String message;

//    public ResponseResult(){
//
//    }

    // status, message 보내는 경우
    public ResponseResult(boolean status, String message){
        this.status = status;
        this.message = message;
    }

    public ResponseResult(T result){
        this.status = true;
        this.result = result;
        this.message = null;
    }



}
