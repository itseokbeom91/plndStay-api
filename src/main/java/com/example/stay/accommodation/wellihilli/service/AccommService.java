package com.example.stay.accommodation.wellihilli.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.OutputStreamWriter;
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

        LogWriter logWriter = new LogWriter("GET", "https://vapi.wellihillipark.com:8070/api/facilities/condo_room/list", System.currentTimeMillis());
        try{
            /**
             * TODO : 리턴값 확인 후 호출 메서드로 빼기
             */
            URL url = new URL("https://vapi.wellihillipark.com:8070/api/facilities/condo_room/list");
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

    public JSONObject callWellihilliAPI(String method, String wellihilliUrl, String strPostBody){
        JSONObject jsonObject = new JSONObject();
        String message = "";
        LogWriter logWriter = new LogWriter(method, Constants.wellihilliUrl + wellihilliUrl, System.currentTimeMillis());
        try{
            URL url = new URL(Constants.wellihilliUrl + wellihilliUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            if(strPostBody != null && !strPostBody.equals("")){
                conn.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                writer.write(strPostBody);
                writer.close();

                logWriter.addRequest(strPostBody);
            }

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

        return jsonObject;
    }

    

}
