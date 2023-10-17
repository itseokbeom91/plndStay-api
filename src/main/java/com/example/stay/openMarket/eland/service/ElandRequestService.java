package com.example.stay.openMarket.eland.service;

import com.example.stay.common.util.CommonFunction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ElandRequestService {

    CommonFunction commonFunction = new CommonFunction();

    public JsonNode callApi(String StrUrl, Map<String, String> hashMap, String strAuthorization){

        String result = "";
        JsonNode jsonNode = null;
        int responseCode = 500;

        try {
            URL obj = new URL(StrUrl);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            // HTTP 메소드 설정
            connection.setRequestMethod("POST");

            // URL 인코딩된 형식으로 데이터 생성
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> param : hashMap.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            // Content-Type 설정
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", strAuthorization);
            connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

            // 데이터 전송
            connection.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(postDataBytes);
            }

            // 응답 받기
            responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                result = response.toString();
                System.out.println("Response: " + response.toString());
            }

            // JSON 문자열을 JsonNode로 파싱하여 반환
            ObjectMapper objectMapper = new ObjectMapper();

            jsonNode = objectMapper.readTree(result);


        } catch (IOException e) {
            e.printStackTrace();
        }


        return jsonNode;
    }

    public JsonNode callApiTest(String StrUrl, Map<String, List<String>> paramMap, String strAuthorization){

        String result = "";
        JsonNode jsonNode = null;
        int responseCode = 500;

        try {
            URL obj = new URL(StrUrl);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            // HTTP 메소드 설정
            connection.setRequestMethod("POST");

            // URL 인코딩된 형식으로 데이터 생성
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, List<String>> param : paramMap.entrySet()) {
                String key = param.getKey();
                for (String value : param.getValue()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(key, "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(value, "UTF-8"));
                }
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            // Content-Type 설정
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", strAuthorization);
            connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

            // 데이터 전송
            connection.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(postDataBytes);
            }

            // 응답 받기
            responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                result = response.toString();
                System.out.println("Response: " + response.toString());
            }

            // JSON 문자열을 JsonNode로 파싱하여 반환
            ObjectMapper objectMapper = new ObjectMapper();
            jsonNode = objectMapper.readTree(result);


        } catch (IOException e) {
            e.printStackTrace();
        }


        return jsonNode;
    }
}
