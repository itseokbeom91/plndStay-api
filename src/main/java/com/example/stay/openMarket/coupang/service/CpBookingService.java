package com.example.stay.openMarket.coupang.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.LogWriter;
import com.example.stay.openMarket.common.mapper.CommonMapper;
import com.example.stay.openMarket.coupang.Api.CoupangApi;
import com.example.stay.openMarket.coupang.mapper.CoupangMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class CpBookingService {

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private CoupangMapper coupangMapper;

    @Autowired
    private CoupangApi coupangApi;

    CommonFunction commonFunction = new CommonFunction();

    private static int intOmkIdx = 12;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // TODO : 기간제한 확인
    // 예약 목록 조회 -> 기간 제한 : 1달 , 제공 데이터 수 제한 : 최대 10,000개
    public String getBookingList(String dataType, String strStartDate, String strEndDate,
                                 HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";

        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                                                httpServletRequest.getQueryString(), System.currentTimeMillis());

        JSONObject dataJson = new JSONObject();
        try{
            JSONObject returnJson = coupangApi.coupangGetApi("reservations?startDate=" + strStartDate + "&endDate=" + strEndDate);
            // 응답값 처리
            String returnCode = returnJson.get("code").toString();

            if(returnCode.equals("200")){
                dataJson = (JSONObject) returnJson.get("data");

                message = "예약 목록 조회 완료";
            }else{
                message = "쿠팡 api 호출 실패";
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        return commonFunction.makeReturn(dataType, statusCode, message, dataJson);
    }

    // 예약 단일 조회 -> 티켓번호, 주문번호 둘 다 주는지 확인
    public String getBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";

        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        JSONObject dataJson = new JSONObject();
        try{
            JSONObject returnJson = coupangApi.coupangGetApi("reservations/" + intRsvID);
            // 응답값 처리
            String returnCode = returnJson.get("code").toString();

            if(returnCode.equals("200")){
                dataJson = (JSONObject) returnJson.get("data");

                message = "예약 조회 완료";
            }else{
                message = "쿠팡 api 호출 실패";
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        return commonFunction.makeReturn(dataType, statusCode, message, dataJson);
    }
}
