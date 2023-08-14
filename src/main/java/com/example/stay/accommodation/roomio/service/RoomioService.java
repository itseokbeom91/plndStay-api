package com.example.stay.accommodation.roomio.service;

import com.example.stay.accommodation.roomio.mapper.RoomioMapper;
import com.example.stay.common.util.CommonFunction;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
public class RoomioService {
    
    @Autowired
    private RoomioMapper roomioMapper;

    CommonFunction commonFunction = new CommonFunction();


    public String getAccomm(){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("m","getList");
            jsonObject.put("cd","7634");
            jsonObject.put("status","Y");

            JsonNode jsonNode = commonFunction.callJsonApi("roomio","", jsonObject, "http://api.roomio.co.kr/", "POST");

            JSONArray jsonArray = (JSONArray) new JSONParser().parse(jsonNode.get("list").toString());

            for(Object object : jsonArray){

                JSONObject forObject = (JSONObject) new JSONParser().parse(object.toString());
                if(forObject.get("hotel_id").equals("7815")){
                    result = forObject.toJSONString();
                }
            }
            System.out.println(result);

        }catch (Exception e){
            message = " 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn("json", statusCode, message, result);
    }

    public String getRoom(String strHotelId){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("m","getRoomList");
            jsonObject.put("cd","7634");
            jsonObject.put("hotel_id",strHotelId);

            JsonNode jsonNode = commonFunction.callJsonApi("roomio","", jsonObject, "http://api.roomio.co.kr/", "POST");

            System.out.println(jsonNode);

        }catch (Exception e){
            message = " 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn("json", statusCode, message);
    }

    public String bookingState(){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

        }catch (Exception e){
            message = " 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn("json", statusCode, message);
    }

    public String getPrice(){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

        }catch (Exception e){
            message = " 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn("json", statusCode, message);
    }

    public String booking(){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

        }catch (Exception e){
            message = " 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn("json", statusCode, message);
    }

    public String bookingInfo(){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

        }catch (Exception e){
            message = " 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn("json", statusCode, message);
    }

    public String bookingList(){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

        }catch (Exception e){
            message = " 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn("json", statusCode, message);
    }

    public String bookingCancel(){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

        }catch (Exception e){
            message = " 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn("json", statusCode, message);
    }
    
}
