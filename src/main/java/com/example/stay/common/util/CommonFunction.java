package com.example.stay.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public JsonNode callJsonApi(String strAccomm, String strType, JSONObject requestJson, String strUrl, String method){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.createObjectNode();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        // API 호출 정보
        if(strUrl.equals("")){
            if(strAccomm.equals("hanwha")){
                strUrl = Constants.hanwhaUrl;
            }else if(strAccomm.equals("YPB")){
                if(strType.equals("stock")){
                    strUrl = Constants.ypUrl+"getRsrvMm";
                }else if(strType.equals("booking")){
                    strUrl = Constants.ypUrl+"joinSalesPkgRsrvInfo";
                }else if(strType.equals("bookingInfo")){
                    strUrl = Constants.ypUrl+"getSalesRsrvList";
                }else if(strType.equals("bookingCancel")){
                    strUrl = Constants.ypUrl+"joinSalesPkgRsrvCncl";
                }else if(strType.equals("bookingList")){
                    strUrl = Constants.ypUrl+"getArrvRsrvList";
                }
            }
        }

        LogWriter logWriter = new LogWriter(method, strUrl, System.currentTimeMillis());
        try{
            URL url = new URL(strUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            if(strAccomm.equals("YPB")){
                conn.setRequestProperty("X-Yobiss-AuthToken", Constants.ypTokenKey);
            }

            if(!requestJson.isEmpty()){
                conn.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                writer.write(requestJson.toJSONString());
                writer.close();

                logWriter.addRequest(gson.toJson(requestJson));
            }

            String strResult = "";
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                strResult = sb.toString();
            }else{
                strResult = conn.getResponseMessage();
            }

            conn.disconnect();

            // JSON 파싱
            jsonNode = objectMapper.readTree(strResult);

            logWriter.add(gson.toJson(jsonNode));
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return jsonNode;
    }
}
