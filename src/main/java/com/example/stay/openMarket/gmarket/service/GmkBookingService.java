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
public class GmkBookingService {

    CommonFunction commonFunction = new CommonFunction();

    // 결제 완료된 주문 데이터 조회 - 클레임(취소, 반품, 교환, 미수령신고) 주문은 조회 X
    public String getBookingList(String dataType, String strDateFrom, String strDateTo, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            JSONObject requestJson = new JSONObject();
            requestJson.put("siteType", 3);
            requestJson.put("orderStatus", 1); // 1 : 결제완료(주문 확인 전)
            requestJson.put("requestDateType", 1);
            requestJson.put("requestDateFrom", strDateFrom);
            requestJson.put("requestDateTo", strDateTo);

            // api 호출
            String authorization = HmacGenerater.generate("Order");
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, requestJson, Constants.gmkUrl + "shipping/v1/Order/RequestOrders", "post");
            String code = jsonNode.get("resultCode").toString();
            String resultMsg = jsonNode.get("message").toString();
            if(code.equals("0")) {
                message = "결제 완료된 주문 조회 완료";

                JSONObject dataJson = (JSONObject) new JSONParser().parse(jsonNode.get("Data").toString());
                JSONArray orderArr = (JSONArray) dataJson.get("RequestOrders");

                for(Object order : orderArr){
                    JSONObject orderJson = (JSONObject) order;

                    String orderStatus = orderJson.get("OrderStatus").toString();
                    if(orderStatus.equals("1")){ // 1 : 신규주문
                        // TODO : 예약 테이블 insert
                    }
                }
            }else{
                message = "지마켓 api 호출 실패";
                logWriter.add(resultMsg);
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "결제 완료된 주문 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 취소주문 목록 조회
    // 일주일 단위 조회 가능
    public String getCancelBookingList(String dataType, String strDateFrom, String strDateTo, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            JSONObject requestJson = new JSONObject();
            requestJson.put("SiteType", 3);
            requestJson.put("CancelStatus", 0); // 0 : 전체
            requestJson.put("Type", 4); // 조회기준 구분 : 결제 완료일
            requestJson.put("StartDate", strDateFrom);
            requestJson.put("EndDate", strDateTo);

            // api 호출
            String authorization = HmacGenerater.generate("Cancels");
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, requestJson, Constants.gmkUrl + "claim/v1/sa/Cancels", "post");
            String code = jsonNode.get("resultCode").toString();
            String resultMsg = jsonNode.get("message").toString();
            if(code.equals("0")) {
                message = "취소주문 목록 조회 완료";

                JSONObject dataJson = (JSONObject) new JSONParser().parse(jsonNode.get("Data").toString());
                JSONArray orderArr = (JSONArray) dataJson.get("RequestOrders");

                for(Object order : orderArr){
                    JSONObject orderJson = (JSONObject) order;

                    String orderStatus = orderJson.get("OrderStatus").toString();
                    if(orderStatus.equals("1")){ // 1 : 신규주문
                        // TODO : 예약 테이블 insert
                    }
                }
            }else{
                message = "지마켓 api 호출 실패";
                logWriter.add(resultMsg);
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "취소주문 목록 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 발송처리
    // TODO : 발송처리랑 배송완료처리를 같이 해도되는건지 확인 필요
    public String sendProcess(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            JSONObject requestJson = new JSONObject();
//            requestJson.put("orderNo", );
//            requestJson.put("ShippingDate", );
//            requestJson.put("DeliveryCompanyCode", );
//            requestJson.put("InvoiceNo", ); // 날짜?

            // api 호출
            String authorization = HmacGenerater.generate("Delivery");
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, requestJson, Constants.gmkUrl + "shipping/v1/Delivery/ShippingInfo", "put");
            String code = jsonNode.get("resultCode").toString();
            String resultMsg = jsonNode.get("message").toString();
            if(code.equals("0")) {
                // TODO : 예약 테이블 상태 변경?
                message = "발송처리 완료";
            }else{
                message = "지마켓 api 호출 실패";
                logWriter.add(resultMsg);
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "발송처리 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 배송완료 처리
    public String deliveryComplete(String dataType, int intRsvID, HttpServletRequest httpServletRequest) {
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try {
            String strOrderNo = "";

            // api 호출
            String authorization = HmacGenerater.generate("Delivery");
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, new JSONObject(), Constants.gmkUrl + "shipping/v1/Delivery/AddShippingCompleteInfo/" + strOrderNo, "post");
            String code = jsonNode.get("resultCode").toString();
            String resultMsg = jsonNode.get("message").toString();
            if (code.equals("0")) {
                // TODO : 예약 테이블 상태 변경?
                message = "배송완료 처리 완료";
            } else {
                message = "지마켓 api 호출 실패";
                logWriter.add(resultMsg);
            }
            logWriter.add(message);
            logWriter.log(0);
        } catch (Exception e) {
            e.printStackTrace();
            message = "배송완료 처리 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 취소 요청 건에 대해 취소 승인
    // 이미 배송이 되거나 제작중이어서 취소승인할 수 없을 경우 발송처리 API를 호출하면 취소거부됨
    public String cancelBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try {
            String strOrderNo = "";

            JSONObject requestJson = new JSONObject();
            requestJson.put("SiteType", 2); //
            requestJson.put("OrderNo", strOrderNo);

            // api 호출
            String authorization = HmacGenerater.generate("Cancel");
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, requestJson, Constants.gmkUrl + "claim/v1/sa/Cancel/" + strOrderNo, "post");
            String code = jsonNode.get("resultCode").toString();
            String resultMsg = jsonNode.get("message").toString();
            if (code.equals("0")) {
                // TODO : 예약 테이블 상태 변경
                message = "취소 완료";
            } else {
                message = "지마켓 api 호출 실패";
                logWriter.add(resultMsg);
            }
            logWriter.add(message);
            logWriter.log(0);
        } catch (Exception e) {
            e.printStackTrace();
            message = "취소 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

}
