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
    public String getCancelList(String dataType, String strDateFrom, String strDateTo, HttpServletRequest httpServletRequest){
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

    // 반품신청 목록 조회
    // 일주일 단위 조회 가능
    public String getReturnList(String dataType, String strDateFrom, String strDateTo, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            JSONObject requestJson = new JSONObject();
            requestJson.put("SiteType", 3);
            requestJson.put("ReturnStatus", 1); // 1 : 반품요청
            requestJson.put("Type", 2); // 조회기준 구분 2 : 반품 신청일
            requestJson.put("StartDate", strDateFrom);
            requestJson.put("EndDate", strDateTo);

            // api 호출
            String authorization = HmacGenerater.generate("Returns");
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, requestJson, Constants.gmkUrl + "claim/v1/sa/Returns", "post");
            String code = jsonNode.get("resultCode").toString();
            String resultMsg = jsonNode.get("message").toString();
            if(code.equals("0")) {
                message = "반품신청 목록 조회 완료";

                JSONObject dataJson = (JSONObject) new JSONParser().parse(jsonNode.get("Data").toString());
                JSONArray orderArr = (JSONArray) dataJson.get("RequestOrders");

                for(Object order : orderArr){
                    JSONObject orderJson = (JSONObject) order;

                    String orderStatus = orderJson.get("OrderStatus").toString();
                    if(orderStatus.equals("1")){ // 1 : 반품요청
                        // TODO : 예약 테이블 update?
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
            message = "반품신청 목록 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

//    // 미수령 신고 목록 조회 api
//    // 미수령신고된 주문은 정산되지 않기 때문에 구매자와 연락하여 배송사고가 발생했는지 등 확인해야 함
//    public String getNotReceivedList(String dataType, String strDateFrom, String strDateTo, HttpServletRequest httpServletRequest){
//        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
//                httpServletRequest.getQueryString(), System.currentTimeMillis());
//        String statusCode = "200";
//        String message = "";
//
//        try{
//            JSONObject requestJson = new JSONObject();
//            requestJson.put("searchType", 1); // 조회기준구분 0 : 주문번호 조회, 1 : 미수령신고일 조회
//            requestJson.put("StartDate", strDateFrom);
//            requestJson.put("EndDate", strDateTo);
//
//            // api 호출
//            String authorization = HmacGenerater.generate("Delivery");
//            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, requestJson, Constants.gmkUrl + "shipping/v1/Delivery/ClaimList", "post");
//            String code = jsonNode.get("resultCode").toString();
//            String resultMsg = jsonNode.get("message").toString();
//            if(code.equals("0")) {
//                message = "미수령 신고 목록 조회 완료";
//
//                JSONObject dataJson = (JSONObject) new JSONParser().parse(jsonNode.get("Data").toString());
//                JSONArray orderArr = (JSONArray) dataJson.get("RequestOrders");
//
//                for(Object order : orderArr){
//                    JSONObject orderJson = (JSONObject) order;
//
//                    String orderStatus = orderJson.get("OrderStatus").toString();
//                    if(orderStatus.equals("1")){ // 1 : 반품요청
//                        // TODO : 예약 테이블 update?
//                    }
//                }
//            }else{
//                message = "지마켓 api 호출 실패";
//                logWriter.add(resultMsg);
//            }
//            logWriter.add(message);
//            logWriter.log(0);
//        }catch (Exception e){
//            e.printStackTrace();
//            message = "미수령 신고 목록 조회 실패";
//            statusCode = "500";
//            logWriter.add("error : " + e.getMessage());
//            logWriter.log(0);
//        }
//
//        return commonFunction.makeReturn(dataType, statusCode, message);
//    }

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
//            requestJson.put("ShippingDate", ); // 현재 일시?
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
            requestJson.put("SiteType", 2);

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

    // 판매취소
    // 재고가 부족하거나 발송을 할 수 없는 주문을 해야 할 경우 판매취소 API를 호출 -> 해당 주문의 고객 환불되고 해당 상품(또는 옵션)은 품절처리
    // 발송처리 전 주문상태 경우만 가능
    public String soldOutProcess(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try {
            String strOrderNo = "";

            JSONObject requestJson = new JSONObject();
            requestJson.put("SiteType", 2);

            // api 호출
            String authorization = HmacGenerater.generate("Cancel");
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, requestJson, Constants.gmkUrl + "claim/v1/sa/Cancel/" + strOrderNo +"/SoldOut", "post");
            String code = jsonNode.get("ResultCode").toString();
            String resultMsg = jsonNode.get("Message").toString();
            if (code.equals("0")) {
                // TODO : 예약 테이블 상태 변경
                message = "판매취소 완료";
            } else {
                message = "지마켓 api 호출 실패";
                logWriter.add(resultMsg);
            }
            logWriter.add(message);
            logWriter.log(0);
        } catch (Exception e) {
            e.printStackTrace();
            message = "판매취소 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 반품승인
    // 반품 승인 시 자동으로 반품 보류 해제 및 반품수거송장의 배송 완료가 처리되며 구매자 환불 진행
    // 바로 환불 처리 X, 고객 결제 수단 및 타 장바구니 클레임 진행 여부에 따라 실제 고객 환불 진행
    public String returnProcess(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try {
            String strOrderNo = "";

            JSONObject requestJson = new JSONObject();
            requestJson.put("SiteType", 2);

            // api 호출
            String authorization = HmacGenerater.generate("Cancel");
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, requestJson, Constants.gmkUrl + "claim/v1/sa/return/" + strOrderNo, "put");
            String code = jsonNode.get("ResultCode").toString();
            String resultMsg = jsonNode.get("Message").toString();
            if (code.equals("0")) {
                // TODO : 예약 테이블 상태 변경?
                message = "반품승인 완료";
            } else {
                message = "지마켓 api 호출 실패";
                logWriter.add(resultMsg);
            }
            logWriter.add(message);
            logWriter.log(0);
        } catch (Exception e) {
            e.printStackTrace();
            message = "반품승인 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 판매자 직접 반품 신청
    // 반품 요청상태로 주문상태 변경되며 반품 완료 처리시에는 반품승인 api 호출 되어야 함
    public String requestReturn(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try {
            String strOrderNo = "";

            JSONObject requestJson = new JSONObject();
            requestJson.put("orderNo",strOrderNo);
            requestJson.put("SiteType", 2);
//            requestJson.put("payNo", ); // 장바구니(결제) 번호
//            requestJson.put("reason", ); // NC : 구매자 변심, BG : 선택사항 변경, RG : 상품 파손, OG : 다른 상품 오배송, DD : 상품 미도착
//            requestJson.put("itemStatus", ); // OP : 개봉후 미사용, NP : 미개봉, UP : 개봉 후 사용
//            requestJson.put("alreadySent", ); // 구매자 이미 보냄 여부 true : 고객 이미 보냄, false : 수거요청

            // api 호출
            String authorization = HmacGenerater.generate("Return");
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, requestJson, Constants.gmkUrl + "claim/v1/sa/Return/"+ strOrderNo + "/Request", "post");
            String code = jsonNode.get("ResultCode").toString();
            String resultMsg = jsonNode.get("Message").toString();
            if (code.equals("0")) {
                // TODO : 예약 테이블 상태 변경?
                message = "반품신청 완료";
            } else {
                message = "지마켓 api 호출 실패";
                logWriter.add(resultMsg);
            }
            logWriter.add(message);
            logWriter.log(0);
        } catch (Exception e) {
            e.printStackTrace();
            message = "반품신청 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 판매대금 정산 목록 조회
    // 환불된 주문 건의 금액은 정상 송금된 주문 건 금액의 반대 부호로 내려옴
    public String getCalculationList(String dataType, String strDateFrom, String strDateTo, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        JSONObject dataJson = new JSONObject();
        try{
            JSONObject requestJson = new JSONObject();
            requestJson.put("siteType", "G");
            // D1 : 입금확인일(정상), D2 : 배송일(정상), D3 : 배송완료일(정상), D4 : 구매결정일(정상), D5 : 정산예정일, D6 : 송금일(당일데이터는 영업일 기준 D+1일 조회가능), D7 : 환불일(환불), D8 : 입금확인일+환불일, D9 : 배송완료일 + 배송완료일 있는 환불일
//            requestJson.put("SrchType", "");
            requestJson.put("SrchStartDate", strDateFrom);
            requestJson.put("SrchEndDate", strDateTo);
//            requestJson.put("PageNo", 0);
//            requestJson.put("PageRowCnt", 0);

            // api 호출
            String authorization = HmacGenerater.generate("account");
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, requestJson, Constants.gmkUrl + "account/v1/settle/getsettleorder", "post");
            String code = jsonNode.get("ResultCode").toString();
            String resultMsg = jsonNode.get("Message").toString();
            if(code.equals("0")) {
                message = "판매대금 정산목록 조회 완료";

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
            message = "판매대금 정산목록 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message, dataJson);
    }

    // 배송비 정산 목록 조회
    public String getDeliveryFeeList(String dataType, String strDateFrom, String strDateTo, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        JSONObject dataJson = new JSONObject();
        try{
            JSONObject requestJson = new JSONObject();
            requestJson.put("siteType", "G");
            // D1 : 입금확인일(정상), D3 : 매출마감일(일반적으로는 배송완료일 익일, 환불건일 경우 차이발생 가능), D6 : 송금일(당일데이터는 영업일 기준 D+1일로 조회가능함), D7 : 환불일(환불), D8 : 입금확인일(옥션은 입금확인되었으면서 실제 송금이 발생된 주문건 기준)+환불일
//            requestJson.put("SrchType", ""); // 기간검색 구분
            requestJson.put("SrchStartDate", strDateFrom);
            requestJson.put("SrchEndDate", strDateTo);
//            requestJson.put("PageNo", 0);
//            requestJson.put("PageRowCnt", 0);

            // api 호출
            String authorization = HmacGenerater.generate("account");
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, requestJson, Constants.gmkUrl + "account/v1/settle/getsettledeliveryfee", "post");
            String code = jsonNode.get("ResultCode").toString();
            String resultMsg = jsonNode.get("Message").toString();
            if(code.equals("0")) {
                message = "배송비 정산목록 조회 완료";

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
            message = "배송비 정산목록 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message, dataJson);
    }

}
