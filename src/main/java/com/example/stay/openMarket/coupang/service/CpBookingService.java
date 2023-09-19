package com.example.stay.openMarket.coupang.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.openMarket.common.mapper.CommonMapper;
import com.example.stay.openMarket.coupang.Api.CoupangApi;
import com.example.stay.openMarket.coupang.hmac.HmacGenerater;
import com.example.stay.openMarket.coupang.mapper.CoupangMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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

    // 예약 목록 조회 -> 기간 제한 : 1달 , 제공 데이터 수 제한 : 최대 10,000개
    public String getBookingList(String dataType, String strStartDate, String strEndDate,
                                 HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";

        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                                                httpServletRequest.getQueryString(), System.currentTimeMillis());

        JSONArray resultArr = new JSONArray();
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String startDate = simpleDateFormat.parse(strStartDate).toString() + " 00:00:00";
            String endDate = simpleDateFormat.parse(strEndDate).toString() + " 00:00:00";

            URIBuilder uriBuilder = new URIBuilder()
                    .setPath(Constants.cpUrl + "reservations")
                    .addParameter("offset", "0")
                    .addParameter("limit", "100")
                    .addParameter("startDate", startDate)
                    .addParameter("endDate", endDate);

            LogWriter apiLogWriter = new LogWriter("GET", uriBuilder.getPath(), uriBuilder.getQueryParams().toString(), System.currentTimeMillis());

            String authorization = HmacGenerater.generate("GET", uriBuilder.build().toString(), Constants.cpSecretKey, Constants.cpAccessKey);

            uriBuilder.setScheme("https").setHost(Constants.cpHost).setPort(Constants.cpPort);

            HttpGet get = new HttpGet(uriBuilder.build().toString());
            get.addHeader("Authorization", authorization);
            get.addHeader("Content-type", "application/json; charset=UTF-8");
            get.addHeader("Request-Vendor-Id", Constants.cpVendorId);

            JSONObject returnJson = httpExecute(get);

            apiLogWriter.add(gson.toJson(returnJson));
            apiLogWriter.log(0);

            // 응답값 처리
            String returnCode = returnJson.get("code").toString();

            if(returnCode.equals("200")){
                JSONObject dataJson = (JSONObject) returnJson.get("data");
                JSONObject resultJson = (JSONObject) dataJson.get("results");
                resultArr = (JSONArray) resultJson.get("content");

                // 예약 테이블 INSERT
                String bookingDatas = "";
                for(Object result : resultArr){
                    JSONObject jsonObject = (JSONObject) result;
//                    String strOrderCode = jsonObject.get("").toString();
                    String strPdtCode = jsonObject.get("travelProductId").toString();
                    String strCpItemCode = jsonObject.get("travelItemId").toString();
                    String strCpRateCode = jsonObject.get("travelRateId").toString();
                    String strCheckIn = jsonObject.get("checkInDate").toString();
                    String strCheckOut = jsonObject.get("checkOutDate").toString();
//                    String strPenaltyPrice = jsonObject.get("penaltyPrice").toString();
                    String strRoomRequest = jsonObject.get("roomRequest").toString();
                    String strRmtypeName = jsonObject.get("vendorItemName").toString();
                    String strOrdPhone = jsonObject.get("userPhoneNumber").toString();
                    String strOrdName = jsonObject.get("userName").toString();
                    String strOrdEmail = jsonObject.get("userEmail").toString();
                    int intRmCnt = Integer.parseInt(jsonObject.get("totalQuantity").toString());

                    String strStatusCode = jsonObject.get("ticketStatusType").toString();
                    if(strStatusCode.equals("CONFIRM_PENDING")){ // 예약 대기
                        strStatusCode = "0";
                    }else if(strStatusCode.equals("CONFIRMED")){ // 예약 확정
                        strStatusCode = "4";
                    }else if(strStatusCode.equals("CANCEL_RECEIPT")){ // 취소 접수
                        strStatusCode = "14";
                    }else if(strStatusCode.equals("CANCEL_PROCEEDING")){ // 취소중
                        strStatusCode = "14";
                    }else if(strStatusCode.equals("CANCEL_COMPLETE")){ // 취소 완료
                        strStatusCode = "5";
                    }else if(strStatusCode.equals("USED")){ // 발행 완료
                        strStatusCode = "4";
                    }

                    String ticketNumber = jsonObject.get("ticketNumber").toString();
                    double salePrice = (double) Integer.parseInt(jsonObject.get("salePrice").toString());
//                    String refundedAt = jsonObject.get("refundedAt").toString();
                    String strCreated = jsonObject.get("purcahsedAt").toString();
//                    String productId = jsonObject.get("productId").toString();
                    String strOrderCode = jsonObject.get("orderId").toString();

                    int intAID = coupangMapper.getIntAID(strPdtCode);
                    int intRmIdx = coupangMapper.getIntRmIdx(strCpItemCode);

//                    bookingDatas = strStatusCode + "|^|OMK|^|" + intAID  + "|^|" + intRmIdx + "|^|" + intRmCnt + "|^|" + strCheckIn + "|^|" + strCheckOut + "|^|44|^|" + intSupplier + "|^|" + strRmtypeName + "|^|" +  + "{{|}}";
                }
                bookingDatas = bookingDatas.substring(0, bookingDatas.length()-5);

                message = "예약 목록 조회 완료";
            }else{
                message = "쿠팡 api 호출 실패";
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            message = "예약 목록 조회 실패";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message, resultArr);
    }

    // 예약 단일 조회 -> 티켓번호, 주문번호 둘 다 주는지 확인
    public String getBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";

        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        JSONObject resultJson = new JSONObject();
        try{
            URIBuilder uriBuilder = new URIBuilder()
                    .setPath(Constants.cpUrl + "reservations/" + intRsvID)
                    .addParameter("offset", "0")
                    .addParameter("limit", "100");

            LogWriter apiLogWriter = new LogWriter("GET", uriBuilder.getPath(), uriBuilder.getQueryParams().toString(), System.currentTimeMillis());

            String authorization = HmacGenerater.generate("GET", uriBuilder.build().toString(), Constants.cpSecretKey, Constants.cpAccessKey);

            uriBuilder.setScheme("https").setHost(Constants.cpHost).setPort(Constants.cpPort);

            HttpGet get = new HttpGet(uriBuilder.build().toString());
            get.addHeader("Authorization", authorization);
            get.addHeader("Content-type", "application/json; charset=UTF-8");
            get.addHeader("Request-Vendor-Id", Constants.cpVendorId);

            JSONObject returnJson = httpExecute(get);

            apiLogWriter.add(gson.toJson(returnJson));
            apiLogWriter.log(0);

            // 응답값 처리
            String returnCode = returnJson.get("code").toString();

            if(returnCode.equals("200")){
                JSONObject dataJson = (JSONObject) returnJson.get("data");
                JSONArray resultsArr = (JSONArray) dataJson.get("results");
                for(Object r : resultsArr){
                    resultJson = (JSONObject) r;
                }

                message = "예약 조회 완료";
            }else{
                message = "쿠팡 api 호출 실패";
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            message = "예약 조회 실패";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message, resultJson);
    }

    // 예약 확정
    public String confirmBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";

        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        try{
            // TODO : 주문번호로 티켓번호 찾기
            // 주문번호로 티켓번호 찾기
            String ticketNumber = "";

            // API 호출
            JSONObject returnJson = coupangApi.coupangPostApi(null, "reservation/request/confirmation?ticketNumber=" + ticketNumber);

            // 응답값 처리
            String returnCode = returnJson.get("code").toString();
            if(returnCode.equals("200")){
                JSONArray dataArr = (JSONArray) returnJson.get("data");
                for(Object d : dataArr){
                    JSONObject dataObject = (JSONObject) d;
                    if(dataObject.get("isSuccessfulRequest").equals("true")){
                        message = "예약 확정 완료";
                    }else{
                        message = "예약 확정 실패";
                        break;
                    }
                }
            }else{
                message = "쿠팡 api 호출 실패";
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            message = "예약 확정 실패";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 예약 불가 처리(예약 대기중인 티켓 상태를 예약불가로 변경 -> 자동으로 취소 완료 처리됨)
    public String rejectBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";

        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        try{
            // TODO : 주문번호로 티켓번호 찾기
            // 주문번호로 티켓번호 찾기
            String ticketNumber = "";

            // API 호출
            JSONObject returnJson = coupangApi.coupangPostApi(null, "reservation/request/rejection?ticketNumber=" + ticketNumber);

            // 응답값 처리
            String returnCode = returnJson.get("code").toString();
            if(returnCode.equals("200")){
                JSONArray dataArr = (JSONArray) returnJson.get("data");
                for(Object d : dataArr){
                    JSONObject dataObject = (JSONObject) d;
                    if(dataObject.get("isSuccessfulRequest").equals("true")){
                        message = "예약 취소 완료";
                    }else{
                        message = "예약 취소 실패";
                        break;
                    }
                }
            }else{
                message = "쿠팡 api 호출 실패";
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            message = "예약 취소 실패";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 예약 취소 승인(취소 대기중인 티켓 상태를 취소 승인으로 변경 -> 자동으로 취소 완료 처리됨)
    public String cancelBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";

        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        try{
            // TODO : 주문번호로 티켓번호 찾기
            // 주문번호로 티켓번호 찾기
            String ticketNumber = "";

            // API 호출
            JSONObject returnJson = coupangApi.coupangPostApi(null, "reservation/request/cancellation?ticketNumber=" + ticketNumber);

            // 응답값 처리
            String returnCode = returnJson.get("code").toString();
            if(returnCode.equals("200")){
                JSONArray dataArr = (JSONArray) returnJson.get("data");
                for(Object d : dataArr){
                    JSONObject dataObject = (JSONObject) d;
                    if(dataObject.get("isSuccessfulRequest").equals("true")){
                        message = "예약 취소 완료";
                    }else{
                        message = "예약 취소 실패";
                        break;
                    }
                }
            }else{
                message = "쿠팡 api 호출 실패";
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            message = "예약 취소 실패";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 주문 상태 이력 조회(예약대기, 예약 확정을 조회 가능)
    public String getBookingStatus(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";

        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        List<JSONObject> resultList = new ArrayList<>();
        try{
            // TODO : 주문번호로 티켓번호 찾기
            // 주문번호로 티켓번호 찾기
            String ticketNumber = "";

            // API 호출
            JSONObject returnJson = coupangApi.coupangPostApi(null, "reservation/request/search/confirmation/history?ticketNumber=" + ticketNumber);

            // 응답값 처리
            String returnCode = returnJson.get("code").toString();
            if(returnCode.equals("200")){
                JSONObject dataJson = (JSONObject) returnJson.get("data");
                JSONArray dataArr = (JSONArray) dataJson.get(ticketNumber);
                for(Object data : dataArr){
                    JSONObject jsonObject = (JSONObject) data;
                    resultList.add(jsonObject);
                }
            }else{
                message = "쿠팡 api 호출 실패";
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            message = "예약 취소 실패";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message, resultList);
    }





    public JSONObject httpExecute(HttpUriRequest request) {
        CloseableHttpClient client = null;
        Object objData = null;
        JSONObject returnJson = null;
        try {
            client = HttpClients.createDefault();
            CloseableHttpResponse response = null;
            try {
                response = client.execute(request);

                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);

                JSONParser jsonParser = new JSONParser();
                objData = jsonParser.parse(result);
                returnJson = (JSONObject) objData;

            } catch ( Exception e ) {
                e.printStackTrace ( );
            } finally {
                if (response != null) {
                    try {
                        response.close ( );
                    } catch ( IOException e ) {
                        e.printStackTrace ( );
                    }
                }
            }

        } catch ( Exception e ) {
            e.printStackTrace ( );
        } finally {
            if (client != null) {
                try {
                    client.close ( );
                } catch ( IOException e ) {
                    e.printStackTrace ( );
                }
            }
        }

        return returnJson;
    }
}
