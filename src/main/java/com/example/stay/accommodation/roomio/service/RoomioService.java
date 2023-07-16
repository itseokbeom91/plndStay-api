package com.example.stay.accommodation.roomio.service;

import com.example.stay.accommodation.roomio.mapper.RoomioMapper;
import com.example.stay.common.util.CommonFunction;
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

        }catch (Exception e){
            message = " 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn("json", statusCode, message);
    }

    public String getRoom(){

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
