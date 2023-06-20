package com.example.stay.common.util;

import org.json.simple.JSONObject;

public class CommonFunction<T> {

    public String makeReturn(String statusCode, String message){

        String strResult = "";
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", statusCode);
            jsonObject.put("message", message);

            strResult = Constants.jsonpCallback + "(" + jsonObject.toJSONString() + ")";

        }catch (Exception e){
            e.printStackTrace();
        }

        return strResult;
    }

    public String makeReturn(String statusCode, String message, T result){

        String strResult = "";
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", statusCode);
            jsonObject.put("message", message);
            jsonObject.put("result", result);

            strResult = Constants.jsonpCallback + "(" + jsonObject.toJSONString() + ")";

        }catch (Exception e){
            e.printStackTrace();
        }

        return strResult;
    }
}
