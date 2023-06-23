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


    /*
    한글 지역명을 코드로
    지역이 같아도 지역명이 다른경우가 있어 매칭 선처리
    ex) 강원, 강원특별자치도, 강원도 => 42
     */
    public String addressToDistrictCode(String address) {
        String result = "";
        if ("서울".equals(address) || "서울시".equals(address) || "서울특별시".equals(address)) {
            result = "11";
        } else if ( "부산".equals(address) ||  "부산시".equals(address) || "부산광역시".equals(address)) {
            result = "26";
        } else if ( "대구".equals(address) ||  "대구시".equals(address) || "대구광역시".equals(address)) {
            result = "27";
        } else if ( "인천".equals(address) ||  "인천시".equals(address) || "인천광역시".equals(address)) {
            result = "28";
        } else if ( "광주".equals(address) ||  "광주시".equals(address) || "광주광역시".equals(address)) {
            result = "29";
        } else if ( "대전".equals(address) ||  "대전시".equals(address) || "대전광역시".equals(address)) {
            result = "30";
        } else if ( "울산".equals(address) ||  "울산시".equals(address) || "울산광역시".equals(address)) {
            result = "31";
        } else if ( "경기".equals(address) ||  "경기도".equals(address)) {
            result = "41";
        } else if ( "강원".equals(address) ||  "강원도".equals(address) || "강원특별자치도".equals(address)) {
            result = "42";
        } else if ( "충북".equals(address) ||  "충청북도".equals(address)) {
            result = "43";
        } else if ( "충남".equals(address) ||  "충청남도".equals(address)) {
            result = "44";
        } else if ( "전북".equals(address) ||  "전라북도".equals(address)) {
            result = "45";
        } else if ( "전남".equals(address) ||  "전라남도".equals(address)) {
            result = "46";
        } else if ( "경북".equals(address) ||  "경상북도".equals(address)) {
            result = "47";
        } else if ( "경남".equals(address) ||  "경상남도".equals(address)) {
            result = "48";
        } else if ( "제주".equals(address) ||  "제주도".equals(address) || "제주특별자치도".equals(address)) {
            result = "50";
        }

        return result;
    }
}
