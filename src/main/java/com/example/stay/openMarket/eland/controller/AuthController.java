package com.example.stay.openMarket.eland.controller;

import com.example.stay.common.util.Constants;
import com.example.stay.openMarket.eland.service.AuthService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.tomcat.util.bcel.Const;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Scanner;

@Controller
@RequestMapping("/eland/auth/*")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 인증키 요청
     * 추후 발급받은 ACCESS TOKEN DB에 저장 X -> 쿠키로 사용할 것
     * elandmall_openapi_guide
     */
    @GetMapping("/requestToken")
    public void requestToken(HttpServletResponse httpResponse){
        authService.requestToken(httpResponse);
    }


    /**
     * 인증키 유효성 확인
     * 이랜드몰_OPEN API 연동표준안_공통 000_(인증키유효성확인)
     */
    @GetMapping("/TokenValidation")
    public void accessTokenValidation(String accessToken){
        BufferedReader br = null;
        try {
            // API 호출 정보
            URL url = new URL(Constants.elandPath + "/token/checkAccessTokenValidation.action");

            // API 호출
            long APIStart = System.currentTimeMillis();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("grant_type", "client_credentials");
            conn.setRequestProperty("Authorization", accessToken);
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Accept-Charset", "UTF-8");

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                String strResponse = "";
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuffer sb = new StringBuffer();
                while ((strResponse = br.readLine()) != null) {
                    sb.append(strResponse);
                }
                strResponse = sb.toString();
                JSONParser jsonParser = new JSONParser();
                Object objData = jsonParser.parse(strResponse);
                JSONObject resultJson = (JSONObject) objData;

                if(resultJson.get("error").equals("98")){ // Access Token 유효함
                    System.out.println("AccessToken이 유효하지 않습니다.");
                }

                System.out.println(resultJson);
            }else{
                System.out.println("AccessToken 유효성체크 api 통신 실패");
                System.out.println("responseCode : " + conn.getResponseCode() + "\nresponseMessage : " + conn.getResponseMessage());
            }

            conn.disconnect();
            System.out.println("이랜드 API 호출 실행 시간 : " + (System.currentTimeMillis()-APIStart)/1000.0);

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("AccessToken 유효성체크 실패");
        }finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
