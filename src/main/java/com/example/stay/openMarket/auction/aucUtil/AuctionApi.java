package com.example.stay.openMarket.auction.aucUtil;

import com.example.stay.common.util.LogWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

//@Component("auction.aucUtil.AuctionApi")
public class AuctionApi {
    public static JSONObject callAucApi(String strUrl, String method, String authorization, JSONObject requestJson){
        LogWriter logWriter = new LogWriter(method, strUrl, System.currentTimeMillis());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JSONObject responseJson = new JSONObject();
        try{
            URL url = new URL(strUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Authorization", authorization);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            if(requestJson != null){
                conn.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
                writer.write(requestJson.toJSONString());
                writer.close();

                logWriter.addRequest(gson.toJson(requestJson));
            }

            String strJson = "";
            BufferedReader br = null;
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            }else{
                logWriter.add("responseCode : " + conn.getResponseCode());
                if(conn.getErrorStream() == null){
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                }else{
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
                }
            }
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            strJson = sb.toString();

            JSONParser jsonParser = new JSONParser();
            Object obj = jsonParser.parse(strJson);
            responseJson = (JSONObject) obj;

            conn.disconnect();

            logWriter.add(gson.toJson(responseJson));
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return responseJson;
    }

    public static JSONArray callAucArrApi(String strUrl, String method, String authorization, JSONObject requestJson){
        LogWriter logWriter = new LogWriter(method, strUrl, System.currentTimeMillis());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JSONArray responseJsonArr = new JSONArray();
        try{
            URL url = new URL(strUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Authorization", authorization);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            if(requestJson != null){
                conn.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
                writer.write(requestJson.toJSONString());
                writer.close();

                logWriter.addRequest(gson.toJson(requestJson));
            }

            String strJson = "";
            BufferedReader br = null;
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            }else{
                logWriter.add("responseCode : " + conn.getResponseCode());
                if(conn.getErrorStream() == null){
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                }else{
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
                }
            }
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            strJson = sb.toString();

            JSONParser jsonParser = new JSONParser();
            Object obj = jsonParser.parse(strJson);

            if (obj instanceof JSONObject){
                JSONObject resultJson = (JSONObject) obj;
                responseJsonArr.add(resultJson);
            } else{
                responseJsonArr = (JSONArray) obj;
            }

            conn.disconnect();

            logWriter.add(gson.toJson(responseJsonArr));
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return responseJsonArr;
    }
}
