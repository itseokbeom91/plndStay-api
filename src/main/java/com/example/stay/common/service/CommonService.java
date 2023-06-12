package com.example.stay.common.service;

import com.example.stay.common.util.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Service
public class CommonService {

    // json API 호출
    public JsonNode callJsonApi(String strOmk, JSONObject object) throws Exception{

        // API 호출 정보
        String strUrl = "";
        if(strOmk.equals("hanwha")){
            strUrl = Constants.hanwhaUrl;
        }
        URL url = new URL(strUrl);

        // API 호출
        String response = ConnectionApi(url, strOmk, object);

        // JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);

        return jsonNode;
    }

    // API 호출
    public String ConnectionApi(URL url, String strOmk, JSONObject object){
        String result = "";

        try {

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setDoOutput(true);

            if(!object.isEmpty()){
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                writer.write(object.toJSONString());
                writer.close();
            }

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                result = sb.toString();
            }else{
                result = conn.getResponseMessage();
            }

            conn.disconnect();

        } catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }

    // Authorization 반환
    public String returnAuthorization(String strOmk){
        String result = "";

        try {
//            if(strOmk.equals("hanwha")){
//                result = Constants.hanwhaIP;
//            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }
}
