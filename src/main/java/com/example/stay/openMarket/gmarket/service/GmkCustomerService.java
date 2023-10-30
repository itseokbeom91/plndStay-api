package com.example.stay.openMarket.gmarket.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.openMarket.gmarket.GmkUtil.GmkApi;
import com.example.stay.openMarket.gmarket.GmkUtil.HmacGenerater;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class GmkCustomerService {

    CommonFunction commonFunction = new CommonFunction();

    // 판매자문의 목록 조회
    // 7일 단위로 조회 가능
    public String getCustomerQList(String dataType, String startDate, String endDate, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        JSONArray resultArr = new JSONArray();
        try{
            JSONObject requestJson = new JSONObject();
            requestJson.put("qnaType", 3); // 3 : G마켓 게시판
            requestJson.put("status", "1"); // 1 : 전체, 2 : 미처리, 3 : 처리완료, 4 : 처리중, 5 : 중복문의
            requestJson.put("type", 1); // 조회 기준 구분 1 : 접수일
            requestJson.put("startDate", startDate);
            requestJson.put("endDate", endDate); // 당일 조회시 시작일 +1일로 조회

            // api 호출
            String authorization = HmacGenerater.generate("sell", "G");
            JSONArray resultJsonArr = GmkApi.callGmkArrApi(Constants.gmkUrl + "item/v1/communications/customer/bulletin-board", "POST", authorization, requestJson);

            for(Object arr : resultJsonArr){
                JSONObject resultJson = (JSONObject) arr;

                if(resultJson.get("resultCode") == null){
                    resultArr.add(resultJson);
                }else{
                    if(resultJson.get("resultCode").toString().equals("1000")){
                        message = resultJson.get("message").toString();
                    }else{
                        message = "지마켓 api 호출 실패";
                    }
                }
            }


            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "판매자문의 목록 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message, resultArr);
    }

    // 판매자문의 답변
    public String answerCustomerQ(String dataType, int intCSID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            JSONObject requestJson = new JSONObject();
//            requestJson.put("MessageNo", ); // 문의 번호
            requestJson.put("token", ""); // 문의번호 별 token
//            requestJson.put("answerStatus", ); // 답변 상태 1 : 처리중, 2 : 처리완료
//            requestJson.put("title", ); // 답변 제목
//            requestJson.put("Comments", ); // 답변 내용

            // api 호출
            String authorization = HmacGenerater.generate("sell", "G");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/communications/customer/bulletin-board/qna", "POST", authorization, requestJson);

            if(resultJson.get("resultCode").toString() == null) {
                message = "판매자문의 답변 완료";

            }else{
                message = "지마켓 api 호출 실패";
                logWriter.add(resultJson.get("message").toString());
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "판매자문의 답변 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 긴급알리미 조회
    public String getEmergAlarm(String dataType, String startDate, String endDate, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        JSONArray resultJsonArr = new JSONArray();

        try{
            JSONObject requestJson = new JSONObject();
            requestJson.put("status", 1); // 처리상태 1 : 전체, 2 : 미처리, 3 : 처리완료
            requestJson.put("type", 1); // 조회기준 구분 1 : 접수일
            requestJson.put("startDate", startDate);
            requestJson.put("endDate", endDate);

            // api 호출
            String authorization = HmacGenerater.generate("sell", "G");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "assist/v1/Selling/GetEmergencyInformList", "POST", authorization, requestJson);

            String code = resultJson.get("ResultCode").toString();
            String resultMsg = resultJson.get("Message").toString();
            if(code.equals("0")) {
                if(resultJson.get("Data") == null){
                    message = "조회 기간 내 긴급알리미 데이터 없음";
                }else{
                    resultJsonArr = (JSONArray) new JSONParser().parse(resultJson.get("Data").toString());
                    message = "긴급알리미 조회 완료";
                }
            }else{
                message = "지마켓 api 호출 실패";
                logWriter.add(resultMsg);
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "긴급알리미 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message, resultJsonArr);
    }

    // 긴급알리미 답변
    public String answerEmergAlarm(String dataType, int intCSID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            JSONObject requestJson = new JSONObject();
//            requestJson.put("EmerMessageNo", ); // 문의 번호
            requestJson.put("token", ""); // 문의번호 별 token
//            requestJson.put("answerStatus", ); // 답변 상태 1 : 처리중, 2 : 처리완료
//            requestJson.put("Comments", ); // 답변 제목

            // api 호출
            String authorization = HmacGenerater.generate("sell", "G");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "assist/v1/Selling/AddEmergencyInformReply", "POST", authorization, requestJson);

            String code = resultJson.get("ResultCode").toString();

            if(code.equals("0")) {
                message = "긴급알리미 답변 완료";

            }else{
                message = "지마켓 api 호출 실패";
                logWriter.add(resultJson.get("message").toString());
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "긴급알리미 답변 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // ESM 공지사항 목록 조회
    // 1개월 단위로 조회 가능
    public String getESMNoticeList(String dataType, String startDate, String endDate, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        JSONArray resultArr = new JSONArray();
        try{
            JSONObject requestJson = new JSONObject();
            requestJson.put("SiteType", 3); // 1 : ESM+(전체), 2 : dhrtus, 3 : 지마켓

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateStart = sdf.parse(startDate);
            Date dateEnd = sdf.parse(endDate);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            startDate = simpleDateFormat.format(dateStart);
            endDate = simpleDateFormat.format(dateEnd);

            requestJson.put("StartDate", startDate);
            requestJson.put("EndDate", endDate);

            // api 호출
            String authorization = HmacGenerater.generate("sell", "G");
            JSONArray resultJsonArr = GmkApi.callGmkArrApi(Constants.gmkUrl + "item/v1/communications/notices", "POST", authorization, requestJson);

            for(Object arr : resultJsonArr){
                JSONObject resultJson = (JSONObject) arr;

                if(resultJson.get("resultCode") == null){
                    resultArr.add(resultJson);
                }else{
                    // 조회된 기간에 공지사항이 없는 경우
                    if(resultJson.get("resultCode").toString().equals("1000")){
                        message = "조회된 기간에 공지사항이 없습니다";
                    }else{
                        message = "지마켓 api 호출 실패";
                    }
                }
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "ESM 공지사항 목록 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message, resultArr);
    }



}
