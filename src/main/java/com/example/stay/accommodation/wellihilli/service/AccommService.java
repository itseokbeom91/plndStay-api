package com.example.stay.accommodation.wellihilli.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service("wellihilli.AccommService")
public class AccommService {

    // 객실 정보 조회
    public List<JSONObject> getRoomType(){
        String message = "";
        List<JSONObject> roomTypeList = new ArrayList<>();

        LogWriter logWriter = new LogWriter("GET", "url", System.currentTimeMillis());
        try{
            URL url = new URL("url");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept-Charset", "UTF-8");

            if(conn.getResponseCode() == 200){
                
            }else{
                message = "객실 정보 조회 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return roomTypeList;
    }

    public String insertRoomType(HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";



        CommonFunction commonFunction = new CommonFunction();
        return commonFunction.makeReturn(statusCode, message);
    }
}
