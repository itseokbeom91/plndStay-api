package com.example.stay.common.controller;


import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.mapper.CommonMapper;
import com.example.stay.openMarket.eland.service.ElandCookieService;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class Scheduler {

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private ElandCookieService elandCookieService;

    CommonFunction commonFunction = new CommonFunction();

    // 이랜드 예약 가져오기
    //@Scheduled(cron = "0 0/3 * * * *")
    public void bookingEland(){
        try {
            System.out.println("scheduler booking ELAND");

            URL url = new URL("http://localhost:8080/eland/getRsvList");
//            URL url = new URL("https://dmapi.condo24.com/eland/getRsvList");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 이랜드 취소신청 가져오기
    //@Scheduled(cron = "0 0/3 * * * *")
    public void bookingCancelEland(){
        try {
            System.out.println("scheduler booking cancel ELAND");

//            URL url = new URL("http://localhost:8080/eland/getCancelList");
            URL url = new URL("https://dmapi.condo24.com/eland/getCancelList");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // SSG 예약 가져오기
    //@Scheduled(cron = "0 0/3 * * * *")
    public void bookingSSG(){
        try {
            System.out.println("scheduler booking SSG");

//            URL url = new URL("http://localhost:8080/SSG/getRsvList");
            URL url = new URL("https://dmapi.condo24.com/SSG/getRsvList");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // SSG 취소신청 가져오기
    //@Scheduled(cron = "0 0/3 * * * *")
    public void bookingCancelSSG(){
        try {
            System.out.println("scheduler booking cancel SSG");

//            URL url = new URL("http://localhost:8080/SSG/getCancelList");
            URL url = new URL("https://dmapi.condo24.com/SSG/getCancelList");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 지마켓 예약 가져오기
    //@Scheduled(cron = "0 0/3 * * * *")
    public void bookingGMK(){
        try {
            System.out.println("scheduler booking GMK");

//            URL url = new URL("http://localhost:8080/gmk/booking/getBookingList?dataType=json&startDate=2023-10-11&endDate=2023-10-30");
            URL url = new URL("https://dmapi.condo24.com/gmk/booking/getRsvList");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    
    //@Scheduled(cron = "0/10 * * * * *")
    public void test(){
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String serverAddress = localHost.getHostAddress();
            System.out.println(serverAddress);
            URL url = new URL("/SSG/getRsvList");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /*
    @Scheduled(cron = "5 * * * * *")
    public void scd(){
        System.out.println("go sheduler");
        try {
            // 호출할 URL 생성
            URL url = new URL("http://localhost:8080/eland/testSCD"); // 호출하고자 하는 URL로 변경

            // URL 연결 설정
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // HTTP 요청 메소드 설정 (GET, POST, 등)
            conn.setRequestMethod("GET");

            // 응답 코드 확인
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // URL에 대한 연결이 성공했을 때 응답 데이터를 읽어옴
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // 응답 데이터 출력
                System.out.println(response.toString());
            } else {
                System.out.println("HTTP 요청 실패: " + responseCode);
            }

            // 연결 닫기
            conn.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
*/
    public String test(HttpServletRequest request, HttpServletResponse response){
        String result = "";

        try {

            String accessToken = elandCookieService.getCookie(request, response);

            int intAID = 11471; // 프로시저로 뽑아내야함 list 반복문사용
            AccommDto accommDto = commonMapper.getAcmInfo(intAID, 9);

            String goodsNo = accommDto.getStrPdtCode();

            JsonNode infoJsonNode = commonFunction.callJsonApi("eland", "Bearer " + accessToken, new JSONObject(), Constants.elandPath + "/goods/searchGoodsView.action?goods_no=" + goodsNo, "POST");
            JSONArray stockArray = (JSONArray) new JSONParser().parse(infoJsonNode.get("itemList").toString());

            List<Integer> intItemNoList = new ArrayList<Integer>();
            for(Object object : stockArray){
                JSONObject jsonObject = new JSONObject((Map) object);
                intItemNoList.add(Integer.parseInt(jsonObject.get("ITEM_NO").toString()));
            }
            System.out.println(stockArray);
            System.out.println(intItemNoList);

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }


}
