package com.example.stay.openMarket.gmarket.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.openMarket.gmarket.hmac.HmacGenerater;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class GmkCustomerService {

    CommonFunction commonFunction = new CommonFunction();

    // 판매자문의 목록 조회
    // 7일 단위로 조회 가능
    public String getCustomerQList(String dataType, String strDateFrom, String strDateTo, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        JSONObject QuestionJson = new JSONObject();

        try{
            JSONObject requestJson = new JSONObject();
            requestJson.put("qnaType", 3); // 3 : G마켓 게시판
            requestJson.put("status", "1"); // 1 : 전체, 2 : 미처리, 3 : 처리완료, 4 : 처리중, 5 : 중복문의
            requestJson.put("type", 1); // 조회 기준 구분 1 : 접수일
            requestJson.put("startDate", strDateFrom);
            requestJson.put("endDate", strDateTo); // 당일 조회시 시작일 +1일로 조회

            // api 호출
            String authorization = HmacGenerater.generate("customer");
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, requestJson, Constants.gmkUrl + "item/v1/communications/customer/bulletin-board", "post");
            
            // TODO : resultCode 주는지 확인 필요
            String code = jsonNode.get("resultCode").toString();
            String resultMsg = jsonNode.get("message").toString();
            if(code.equals("0")) {
                message = "판매자문의 목록 조회 완료";

//                JSONObject dataJson = (JSONObject) new JSONParser().parse(jsonNode.get("Data").toString());
//                JSONArray orderArr = (JSONArray) dataJson.get("RequestOrders");
//
//                for(Object order : orderArr){
//                    JSONObject orderJson = (JSONObject) order;
//
//                    String orderStatus = orderJson.get("OrderStatus").toString();
//                    if(orderStatus.equals("1")){ // 1 : 신규주문

//                    }
//                }
            }else{
                message = "지마켓 api 호출 실패";
                logWriter.add(resultMsg);
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

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 판매자문의 답변
    public String QAnswerProcess(String dataType, int intCSID, HttpServletRequest httpServletRequest){
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
//            requestJson.put("title", ); // 답변 내용

            // api 호출
            String authorization = HmacGenerater.generate("customer");
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, requestJson, Constants.gmkUrl + "item/v1/communications/customer/bulletin-board/qna", "post");
            // TODO : resultCode 주는지 확인 필요
            String code = jsonNode.get("resultCode").toString();
            String resultMsg = jsonNode.get("message").toString();
            if(code.equals("0")) {
                message = "판매자문의 답변 완료";

            }else{
                message = "지마켓 api 호출 실패";
                logWriter.add(resultMsg);
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
    public String getEmergAlarm(String dataType, String strDateFrom, String strDateTo, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        JSONObject dataJson = new JSONObject();

        try{
            JSONObject requestJson = new JSONObject();
            requestJson.put("status", 2); // 처리상태 1 : 전체, 2 : 미처리, 3 : 처리완료
            requestJson.put("status", 1); // 1 : 접수일
            requestJson.put("startDate", strDateFrom);
            requestJson.put("endDate", strDateTo);

            // api 호출
            String authorization = HmacGenerater.generate(""); // ???
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, requestJson, Constants.gmkUrl + "assist/v1/Selling/GetEmergencyInformList", "post");

            String code = jsonNode.get("ResultCode").toString();
            String resultMsg = jsonNode.get("message").toString();
            if(code.equals("0")) {
                message = "긴급알리미 조회 완료";

                JSONArray dataArr = (JSONArray) new JSONParser().parse(jsonNode.get("Data").toString());

                for(Object data : dataArr){
                    dataJson = (JSONObject) data;

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

        return commonFunction.makeReturn(dataType, statusCode, message, dataJson);
    }

    // 긴급알리미 답변
    public String emergAlarmAnswer(String dataType, int intEmergAlarmID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        JSONObject dataJson = new JSONObject();

        try{
            JSONObject requestJson = new JSONObject();
            requestJson.put("EmerMessageNo", 2); // 조회에서 발급받은 문의번호
            requestJson.put("token", 1); // 문의번호 별 token
//            requestJson.put("answerStatus", ); // 답변상태 1 : 처리중, 2 : 처리완료
            requestJson.put("Comments", ""); // 답변내용

            // api 호출
            String authorization = HmacGenerater.generate(""); // ???
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, requestJson, Constants.gmkUrl + "assist/v1/Selling/AddEmergencyInformReply", "post");

            String code = jsonNode.get("ResultCode").toString();
            String resultMsg = jsonNode.get("message").toString();
            if(code.equals("0")) {
                message = "긴급알리미 조회 완료";
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

        return commonFunction.makeReturn(dataType, statusCode, message, dataJson);
    }

    // ESM 공지사항 목록 조회
    // 1개월 단위로 조회 가능
    public String getNoticeList(String dataType, String strDateFrom, String strDateTo, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        JSONObject dataJson = new JSONObject();

        try{
            JSONObject requestJson = new JSONObject();
            requestJson.put("SiteType", 3); // 1 : ESM+(전체), 2 : dhrtus, 3 : 지마켓
            requestJson.put("StartDate", strDateFrom);
            requestJson.put("EndDate", strDateTo);

            // api 호출
            String authorization = HmacGenerater.generate("notices");
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, requestJson, Constants.gmkUrl + "item/v1/communications/notices", "post");

            // TODO : 성공시 resultCode가 오는지 확인 필요
            String code = jsonNode.get("resultCode").toString();
            String resultMsg = jsonNode.get("Message").toString();
            if(code.equals("0")) {
                message = "ESM 공지사항 목록 조회 실패";
            }else{
                message = "지마켓 api 호출 실패";
                logWriter.add(resultMsg);
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

        return commonFunction.makeReturn(dataType, statusCode, message, dataJson);
    }



}
