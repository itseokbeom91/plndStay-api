package com.example.stay.accommodation.onda.service;

import com.example.stay.accommodation.onda.mapper.BookingMapper;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.common.util.ResponseResult;
import com.example.stay.openMarket.common.dto.BookingDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

@Service
public class BookingService {

    @Autowired
    private BookingMapper bookingMapper;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // 예약 전 예약 가능 여부 조회
    public boolean checkAvailBooking(String propertyId, String roomTypeId, String ratePlanId, String checkInDate, String checkOutDate){
        boolean availability = false;
        long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constants.ondaPath + "properties/" + propertyId + "/roomtypes/" + roomTypeId + "/rateplans/" + ratePlanId + "/checkavail?checkin=" + checkInDate + "&checkout=" + checkOutDate)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", Constants.ondaAuth)
                .build();

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);

        try{
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();
//                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                availability = (boolean) responseJson.get("availability");
                JSONArray datesArr = (JSONArray) responseJson.get("dates");
                String dates = "";
                for(int i=0; i<datesArr.size(); i++){
                    JSONObject datesJson = (JSONObject) datesArr.get(i);
                    String date = datesJson.get("date").toString();
                    int vacancy = Integer.parseInt(datesJson.get("vacancy").toString());

                    dates += gson.toJson(datesJson);

                }

                logWriter.add(dates);
                logWriter.log(0);
            }
        }catch (Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return availability;
    }

    // 예약 전 취소, 환불 정책 조회
//    public void getRefundPolicy(String propertyId, String roomTypeId, String ratePlanId, String checkInDate, String checkOutDate){
//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .url("https://dapi.tport.dev/gds/diglett/properties/" + propertyId + "/roomtypes/" + roomTypeId + "/rateplans/" + ratePlanId + "/refund_policy?checkin=" + checkInDate + "&checkout=" + checkOutDate)
//                .get()
//                .addHeader("accept", "application/json")
//                .addHeader("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGllbnRfa2V5IjoiM2YzNDY3MTIzNTc1NTc3OWEyMDliOTczZDlkM2NhODJmYzgyY2IwMzk5OTkzOTNlYzk3ZmJhMWExN2I2NjUzYyIsInRpbWVzdGFtcCI6MTY4MzA4MDU1ODUzOCwic2VydmljZV9pZCI6MSwidGFyZ2V0IjoiY2hhbm5lbCIsInRhcmdldF9pZCI6MTcyLCJpYXQiOjE2ODMwODA1NTgsImV4cCI6MTc0NjE1MjU1OH0.E8Tjjpl4C3wNxLlHvIFk3o6MACCVtI36_MYEoricsKM")
//                .build();
//
//        try{
//            Response response = client.newCall(request).execute();
//
//
//        }catch (Exception e){
//            e.printStackTrace();
//
//        }
//    }

    // 온다에 예약정보 전송데이터 생성
    public ResponseResult createBookingInfo(int intBookingID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());

        String statusCode = "200";
        String message = "";
        try{
            // intOrderID로 필요한 정보 조회
            BookingDto bookingDto = bookingMapper.getBookingByIntBookingID(intBookingID);

            String strBookingProcess = bookingDto.getStrBookingProcess();
            String propertyId = bookingDto.getAccommId();

            if(strBookingProcess.equals("0")){
                // 온다에 보낼 데이터 생성
                JSONObject requestJson = new JSONObject();

                String strRoomTypeId = bookingDto.getStrRoomTypeId();
                String strRatePlanId = bookingDto.getStrRatePlanId();

                int intCondoID = bookingDto.getIntCondoID();
                int intRoomID = bookingDto.getIntRoomID();
                int intRateID = bookingDto.getIntRateID();

                String currency = "KRW";
                String checkInDate = bookingDto.getCheckInDate();
                String checkOutDate = bookingDto.getCheckOutDate();
                requestJson.put("currency", currency);
                requestJson.put("channel_booking_number", intBookingID);
                requestJson.put("checkin", checkInDate);
                requestJson.put("checkout", checkOutDate);


                // 예약 가능한지 확인
                boolean availBooking = checkAvailBooking(propertyId, strRoomTypeId, strRatePlanId, checkInDate, checkOutDate);
                if(availBooking){
                    int intRoomCount = bookingDto.getIntRoomCount();

                    // 몇박인지 구하기
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date inDate = sdf.parse(checkInDate);
                    Date outDate = sdf.parse(checkOutDate);
                    long stayDays = ((outDate.getTime() - inDate.getTime()) / 1000) / (24*60*60);

                    JSONArray rateplans = new JSONArray();
                    // 객실 수에 따라서
                    for(int i=0; i<intRoomCount; i++){
                        JSONObject rateplanJson = new JSONObject();

                        long intTotalSalePrice = bookingDto.getIntTotalSalePrice();
                        int intPersonCount = bookingDto.getIntPersonCount();
                        rateplanJson.put("rateplan_id", strRatePlanId);
                        rateplanJson.put("amount", intTotalSalePrice);

                        JSONObject number_of_guest = new JSONObject();
                        number_of_guest.put("adult", intPersonCount);
                        rateplanJson.put("number_of_guest", number_of_guest);

                        JSONArray guests = new JSONArray();
                        JSONObject guestsJson = new JSONObject();
                        String strRecvName = bookingDto.getStrRecvName();
                        String strRecvEmail = bookingDto.getStrRecvEmail();
                        String strRecvPhone = bookingDto.getStrRecvPhone();
                        String strRecvNationality = "KR";
                        guestsJson.put("name", strRecvName);
                        guestsJson.put("email", strRecvEmail);
                        guestsJson.put("phone", strRecvPhone);
                        guestsJson.put("nationality", strRecvNationality);
                        guests.add(guestsJson);

                        rateplanJson.put("guests", guests);

                        rateplans.add(rateplanJson);
                    }

                    requestJson.put("rateplans", rateplans);

                    JSONObject booker = new JSONObject();
                    String strOrdName = bookingDto.getStrOrdName();
                    String strOrdEmail = bookingDto.getStrOrdEmail();
                    String strOrdPhone = bookingDto.getStrOrdPhone();
                    String strOrdNationality = "KR";
                    String timezone = "Asia/Seoul";
                    booker.put("name", strOrdName);
                    booker.put("email", strOrdEmail);
                    booker.put("phone", strOrdPhone);
                    booker.put("nationality", strOrdNationality);
                    booker.put("timezone", timezone);

                    requestJson.put("booker", booker);

//                    String contetns = requestJson.toJSONString();

                    boolean result = createBooking(propertyId, requestJson, intCondoID, intRoomID, intRateID, stayDays);
                    if(result){
                        message = "예약 완료";
                    }else{
                        message = "예약 실패";
                    }
                }else{
                    message = "예약 불가";
                }
            }else if(strBookingProcess.equals("2")) { // 번호대기
                String strSpBookingId = bookingDto.getStrSpBookingId();
                // 예약 확인으로 보내서
                String strStatus = checkBooking(propertyId, strSpBookingId);

                // 상태값이 확정으로 바꼈으면 업데이트
                // 재고, 환불규정은 예약 생성할 때 업데이트 했으니까 상태값만 업데이트하면됨
                if (strStatus.equals("4")) { // 예약 완료처리
                    int result = bookingMapper.updateBookingStatus(strBookingProcess, intBookingID);
                    if(result > 0){
                        message = "예약 완료";
                    }else{
                        message = "예약 실패";
                    }
                } else {
                    message = "아직 예약이 확정되지 않았습니다";
                }
            }

            logWriter.add("BookingID : " + intBookingID + " -> " + message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            message = "예약 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }


        return new ResponseResult<>(statusCode, message);
    }

    public boolean createBooking(String propertyId, JSONObject requestJson, int intCondoID, int intRoomID, int intRateID, long stayDays){
        boolean result = false;
        String message = "";
        long startTime = System.currentTimeMillis();
        String contents = requestJson.toJSONString();

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(contents, mediaType);
        Request request = new Request.Builder()
                .url(Constants.ondaPath + "properties/" + propertyId + "/bookings")
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("Authorization", Constants.ondaAuth)
                .build();

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);
        logWriter.addRequest(gson.toJson(requestJson));

        String bookingID = "";
        try{
            Response response = client.newCall(request).execute();

            String responseBody = response.body().string();
            JSONParser jsonParser = new JSONParser();
            JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

            if(response.isSuccessful()){
                int intBookingID = Integer.parseInt(responseJson.get("channel_booking_number").toString());
                String strSpBookingId = responseJson.get("booking_number").toString();

                JSONArray rateplanArr = (JSONArray) responseJson.get("rateplans");
                JSONObject rateplanJson = (JSONObject) rateplanArr.get(0);

                boolean refundable = (boolean) rateplanJson.get("refundable");
                String strRefundYN = "";
                if(refundable){
                    strRefundYN = "Y";
                }else{
                    strRefundYN = "N";
                }

                String strOndaRefundType = rateplanJson.get("refund_type").toString();
                JSONArray refundPolicyArr = (JSONArray) rateplanJson.get("refund_policy");

                String strRefundPolicies = "";
                for(int j=0; j<refundPolicyArr.size(); j++){
                    JSONObject refundPolicyJson = (JSONObject) refundPolicyArr.get(j);

                    // 한국날짜시간으로 변경
                    SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ssXXX"); // 2023-05-25T15:00:23+09:00
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    Date originDate = sdf.parse(refundPolicyJson.get("until").toString());

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(originDate);
                    cal.add(Calendar.HOUR, 9);
                    String strUntilDate = sdf2.format(cal.getTime());

                    int intPercent = Integer.parseInt(refundPolicyJson.get("percent").toString());
                    int intRefundPrice = Integer.parseInt(refundPolicyJson.get("refund_amount").toString());
                    int intRefundFee = Integer.parseInt(refundPolicyJson.get("charge_amount").toString());

                    // refund_policy 테이블에 insert
                    strRefundPolicies += strUntilDate + "|^|" + intPercent + "|^|" + intRefundPrice + "|^|" + intRefundFee  + "|^|" + strRefundYN  + "|^|" + strOndaRefundType + "|^|" + "{{|}}";
                }

                strRefundPolicies = strRefundPolicies.substring(0, strRefundPolicies.length()-5);

                // booking 테이블 UPDATE, refund_policy 테이블 INSERT
                bookingID = bookingMapper.updateBooking(intBookingID, intCondoID, intRoomID, intRateID, strSpBookingId, strRefundPolicies, stayDays);
                if(bookingID != null){
                    result = true;
                }
            }

            message = gson.toJson(responseJson);
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return result;
    }

    // 예약 확인
    public String checkBooking(String propertyId, String strSpBookingId){
        long startTime = System.currentTimeMillis();
        String strStatus = "";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constants.ondaPath + "properties/" + propertyId + "/bookings/" + strSpBookingId)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", Constants.ondaAuth)
                .build();

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);

        try{
            Response response = client.newCall(request).execute();

            String responseBody = response.body().string();
            JSONParser jsonParser = new JSONParser();
            JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

            System.out.println("responseJson : " + responseJson);

            if(response.isSuccessful()) {
                String status = responseJson.get("status").toString();
                if (status.equals("pending")) {
                    strStatus = "2"; // 번호대기
                } else if (status.equals("confirmed")) {
                    strStatus = "4"; // 완료
                } else if (status.equals("canceled")) {
                    strStatus = "14"; // 취소대기
                }
            }

            logWriter.add("ONDA 현재 예약 상태 : " + strStatus);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return strStatus;
    }


    public ResponseResult cancelBookingInfo(int intBookingID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());

        String statusCode = "200";
        String message = "";

        try{
            // intOrderID로 필요한 정보 조회
            BookingDto bookingDto = bookingMapper.getBookingByIntBookingID(intBookingID);

            String propertyId = bookingDto.getAccommId();
            String strSpBookingId = bookingDto.getStrSpBookingId();

            String checkInDate = bookingDto.getCheckInDate();
            String checkOutDate = bookingDto.getCheckOutDate();

            //몇박인지 계산
            long stayDays = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date inDate = sdf.parse(checkInDate);
            Date outDate = sdf.parse(checkOutDate);
            stayDays = ((outDate.getTime() - inDate.getTime()) / 1000) / (24*60*60);

            // 온다에서 준 판매가 총금액
            long totalSalePrice = bookingDto.getIntTotalSalePrice() * stayDays;

            // 온다에 보낼 데이터 생성
            JSONObject requestJson = new JSONObject();
//            requestJson.put("bcanceled_by", "user");
//            requestJson.put("currency", "KRW");
            requestJson.put("total_amount", totalSalePrice);

            boolean result = cancelBooking(intBookingID, propertyId, strSpBookingId, requestJson);
            if(result){
                message = "예약 취소 완료";
            }else{
                message = "예약 취소 실패";
            }

            logWriter.add("BookingID : " + intBookingID + " -> " + message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            message = "예약 취소 실패";
            statusCode = "500";

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return new ResponseResult<>(statusCode, message);
    }

    // 예약 취소
    public boolean cancelBooking(int intBookingID, String propertyId, String strSpBookingId, JSONObject requestJson){
        long startTime = System.currentTimeMillis();
        boolean result = false;
        String message = "";

        OkHttpClient client = new OkHttpClient();

        String contents = requestJson.toJSONString();

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(contents, mediaType);
        Request request = new Request.Builder()
                .url(Constants.ondaPath + "properties/" + propertyId + "/bookings/" + strSpBookingId + "/cancel")
                .put(body)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("Authorization", Constants.ondaAuth)
                .build();

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);
        logWriter.addRequest(gson.toJson(requestJson));
        try{
            Response response = client.newCall(request).execute();

            String responseBody = response.body().string();
            JSONParser jsonParser = new JSONParser();
            JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

            if(response.isSuccessful()) {
                // Booking 상태값 업데이트 -> 취소대기로
                int updateResult = bookingMapper.updateBookingStatus("14", intBookingID);
                if(updateResult > 0){
                    result = true;
                }
            }

            message = gson.toJson(responseJson);
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return result;
    }

    // 취소/환불 규정 가져오기 -> 언제 사용할건지 정해서 return 어떻게할지 정해야함
    public void getCancelPolicy(String propertyId, String roomTypeId, String ratePlanId,
                                String strCheckInDate, String strCheckOutDate, HttpServletRequest httpServletRequest){
        long startTime = System.currentTimeMillis();
        String message = "";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constants.ondaPath + "properties/" + propertyId + "/roomtypes/" + roomTypeId + "/rateplans/" + ratePlanId
                        + "/refund_policy?checkin=" + strCheckInDate + "&checkout=" + strCheckOutDate)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", Constants.ondaAuth)
                .build();
        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);
        try{
            Response response = client.newCall(request).execute();

            String responseBody = response.body().string();
            JSONParser jsonParser = new JSONParser();
            JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

            message = gson.toJson(responseJson);

            String refundType = responseJson.get("refund_type").toString();
            JSONArray refundPolicyArr = (JSONArray) responseJson.get("refund_policy");
            for(int i=0; i< refundPolicyArr.size(); i++){
                JSONObject refundPolicyJson = (JSONObject) refundPolicyArr.get(i);

                // 한국날짜시간으로 변경
                SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ssXXX"); // 2023-05-25T15:00:23+09:00
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                Date originDate = sdf.parse(refundPolicyJson.get("until").toString());

                Calendar cal = Calendar.getInstance();
                cal.setTime(originDate);
                cal.add(Calendar.HOUR, 9);
                String strUntilDate = sdf2.format(cal.getTime());

                int intPercent = Integer.parseInt(refundPolicyJson.get("percent").toString());
                int intRefundPrice = Integer.parseInt(refundPolicyJson.get("refund_amount").toString());
                int intRefundFee = Integer.parseInt(refundPolicyJson.get("charge_amount").toString());
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

    }

}
