package com.example.stay.openMarket.eland.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class ElandCookieService {

    CommonFunction commonFunction = new CommonFunction();

    // cookie 체크
    public String getCookie(HttpServletRequest request, HttpServletResponse response){

        boolean result = false;

        String ElandCookie = "empty";

        try {

            Cookie[] cookies = request.getCookies(); // 모든 쿠키 가져오기
            if(cookies!=null){
                for (Cookie c : cookies) {
                    String key = c.getName(); // 쿠키 이름 가져오기
                    String value = c.getValue(); // 쿠키 값 가져오기

                    if (key.equals("elandToken")) {
                        result = true;
                        ElandCookie = value;
                    }
                }
            }

            if(result == true){
                // 유효성 검사 & success
                String isToken = checkToken(ElandCookie);

                if(isToken.equals("00")){
                    // success
                }else{
                    //발급
                    String strNewToken = createToken(response);
                    if(strNewToken.equals("fail")){
                        // fail
                        ElandCookie = "empty";
                    }else{
                        ElandCookie = strNewToken;
                    }
                }

            }else{
                // 토큰 생성
                String strNewToken = createToken(response);
                if(strNewToken.equals("fail")){
                    // fail
                    ElandCookie = "empty";
                }else{
                    ElandCookie = strNewToken;
                }
            }

            if(ElandCookie.equals("empty")){
                // error 반환

            }

        }catch (Exception e){
            e.printStackTrace();
        }


        return ElandCookie;

    }

    // 토큰 조회
    public String checkToken(String accessToken){

        String result = "";

        try {

            JsonNode jsonNode = commonFunction.callJsonApi("eland", "Bearer "+ accessToken, new JSONObject(), Constants.elandPath + "/token/checkAccessTokenValidation.action", "POST");

            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonNode.toString());
            System.out.println(jsonObject);
            result = jsonObject.get("error").toString();


        }catch (Exception e){
            e.printStackTrace();
            System.out.println("AccessToken 유효성체크 실패");
        }

        return result;
    }


    // 토큰 생성
    public String createToken(HttpServletResponse response){
        String result = "";

        try {

            JsonNode jsonNode = commonFunction.callJsonApi("eland", Constants.base64EncodedAuth, new JSONObject(), Constants.elandPath + "/auth/requestToken.action", "POST");

            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonNode.toString());
            String error = jsonObject.get("error").toString();
            if (error.equals("99")){
                result = "fail";
            }else{

                result = jsonObject.get("access_token").toString();

                // 쿠키 생성
                Cookie cookie = new Cookie("elandToken", result);
                cookie.setMaxAge(60*60*24);
                cookie.setPath("/");
                response.addCookie(cookie);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }


}
