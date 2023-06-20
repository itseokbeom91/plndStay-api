//package com.example.stay.common.util;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.google.gson.JsonObject;
//import lombok.Data;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.springframework.lang.Nullable;
//
//import java.io.PrintWriter;
//
//@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
//public class ResponseResult<T> {
//
//    private String statusCode;
//    private String message;
//    private T result;
//    private JSONObject jsonResult;
////    private String jsonpCallback = "cd24";
//
////    public ResponseResult(){
////
////    }
//
//    // status, message 보내는 경우
//    public ResponseResult(String statusCode, String message){
//        this.statusCode = statusCode;
//        this.message = message;
//    }
//
//    public ResponseResult(String statusCode, String message, T result){
//        this.statusCode = statusCode;
//        this.message = message;
//        this.result = result;
//    }
//
////    public String makeReturn(ResponseResult responseResult){
////
////        String strResult = "";
////        try{
////            JSONObject jsonObject = new JSONObject();
////            jsonObject.put("code", responseResult.getStatusCode());
////            jsonObject.put("message", responseResult.getMessage());
////            jsonObject.put("result", responseResult.getResult());
////
////            strResult = "cd24(" + jsonObject.toJSONString() + ")";
////
////        }catch (Exception e){
////            e.printStackTrace();
////        }
////
////        return strResult;
////    }
//
////    public String makeReturn(String statusCode, String message){
////
////        String strResult = "";
////        try{
////            JSONObject jsonObject = new JSONObject();
////            jsonObject.put("code", statusCode);
////            jsonObject.put("message", message);
////
////            strResult = jsonpCallback + "(" + jsonObject.toJSONString() + ")";
////
////        }catch (Exception e){
////            e.printStackTrace();
////        }
////
////        return strResult;
////    }
////
////    public String makeReturn(String statusCode, String message, T result){
////
////        String strResult = "";
////        try{
////            JSONObject jsonObject = new JSONObject();
////            jsonObject.put("code", statusCode);
////            jsonObject.put("message", message);
////            jsonObject.put("result", result);
////
////            strResult = jsonpCallback + "(" + jsonObject.toJSONString() + ")";
////
////        }catch (Exception e){
////            e.printStackTrace();
////        }
////
////        return strResult;
////    }
//
//
//}
