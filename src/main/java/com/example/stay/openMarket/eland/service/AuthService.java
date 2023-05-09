package com.example.stay.openMarket.eland.service;

import com.example.stay.common.util.Constants;
import com.example.stay.openMarket.eland.mapper.AuthMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Service
public class AuthService {

    @Autowired
    private AuthMapper authMapper;


    public String requestToken(HttpServletResponse httpResponse){
        String accessToken = "";
        BufferedReader br = null;
        try {
            // API 호출
            long APIStart = System.currentTimeMillis();

            URL url = new URL(Constants.elandPath + "/auth/requestToken.action");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Authorization", Constants.base64EncodedAuth);
            conn.setRequestProperty("grant_type", "client_credentials");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Accept-Charset", "UTF-8");

            String strResponse = "";

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuffer sb = new StringBuffer();
                while ((strResponse = br.readLine()) != null) {
                    sb.append(strResponse);
                }
                strResponse = sb.toString();

                JSONParser jsonParser = new JSONParser();
                Object objData = jsonParser.parse(strResponse);
                JSONObject resultJson = (JSONObject) objData;

                System.out.println(resultJson);
                accessToken = resultJson.get("access_token").toString();

                // 발급받은 AccessToken DB에 INSERT----------------------------------------------------------------------
                int result = insertAccessToken(accessToken);
                if(result > 0){
                    System.out.println("AccessToken DB에 INSERT 성공");
                }else{
                    System.out.println("AccessToken DB에 INSERT 실패");
                }
                System.out.println("---------------------------------------------------------------------------------\n");
                // -----------------------------------------------------------------------------------------------------

                // 쿠키로 사용시
                Cookie cookie = new Cookie("AssessToken", accessToken);
                cookie.setMaxAge(60*60*24);
                cookie.setPath("/");
                httpResponse.addCookie(cookie);
            }else{
                strResponse = conn.getResponseMessage();
                System.out.println(strResponse);

                System.out.println("AccessToken이 만료 혹은 미발급 상태입니다.");
            }
            conn.disconnect();
            System.out.println("이랜드 API 호출 실행 시간 : " + (System.currentTimeMillis()-APIStart)/1000.0);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("AccessToken 발급 실패");
        }finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "Bearer " + accessToken;
    }

    // 발급받은 AccessToken DB에 INSERT
    public int insertAccessToken (String token){
        int result = authMapper.insertAccessToken(token);
        return result;
    }

    //eland api 호출
    public JSONArray elandApi(HttpServletResponse httpResponse, String path, String strPostBody){
        path = Constants.elandPath + path;
//        JSONObject jsonResult = new JSONObject();
        JSONArray jsonArrayData = new JSONArray();
        BufferedReader br = null;
        try{
            // AccessToken 발급
            String accessToken = requestToken(httpResponse);

            long APIStart = System.currentTimeMillis();

            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Authorization", accessToken);
            conn.setRequestProperty("grant_type", "client_credentials");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
//            conn.setDoInput(true);

            if(strPostBody != null && !strPostBody.equals("")){
                conn.setDoOutput(true);

                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                bw.write(strPostBody);
                bw.flush();
                bw.close();

                System.out.println("OutputStream : " + conn.getOutputStream());

            }else{
                System.out.println("요청 파라미터값이 없습니다.");
            }

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                String strResponse = "";
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuffer sb = new StringBuffer();
                while ((strResponse = br.readLine()) != null) {
                    sb.append(strResponse);
                }
                strResponse = sb.toString();

                // 응답값을 json으로 파싱
                JSONParser jsonParser = new JSONParser();
                Object objData = jsonParser.parse(strResponse);
                JSONObject jsonData = (JSONObject) objData;

                jsonArrayData = (JSONArray) jsonData.get("data");

                System.out.println(jsonArrayData);

            }else{
                System.out.println("이랜드 api 통신 실패");
                System.out.println("responseCode : " + conn.getResponseCode() + "\nresponseMessage : " + conn.getResponseMessage());
            }

            conn.disconnect();
            System.out.println("이랜드 API 호출 실행 시간 : " + (System.currentTimeMillis()-APIStart)/1000.0);

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("이랜드 api 호출 실패");
        }finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return jsonArrayData;
    }




}
