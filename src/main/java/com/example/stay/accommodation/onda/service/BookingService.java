package com.example.stay.accommodation.onda.service;

import com.example.stay.accommodation.onda.mapper.BookingMapper;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
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
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Service
public class BookingService {

    @Autowired
    private BookingMapper bookingMapper;

//    private Logger logger = LoggerFactory.getLogger(BookingService.class);

//    LogWriter logWriter = new LogWriter();


    // 예약 전 예약 가능 여부 조회
    public boolean checkAvailBooking(String propertyId, String roomTypeId, String ratePlanId, String checkInDate, String checkOutDate){
        boolean availability = false;
        long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://dapi.tport.dev/gds/diglett/properties/" + propertyId + "/roomtypes/" + roomTypeId + "/rateplans/" + ratePlanId + "/checkavail?checkin=" + checkInDate + "&checkout=" + checkOutDate)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", Constants.ondaAuth)
                .build();

        LogWriter logWriter = new LogWriter("", request.url().toString(), startTime);

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

//                    dates += "예약 가능 여부 : " + availability
//                            + "예약 일자 : " + date
//                            + "예약 가능 재고 : " + vacancy + "개";

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    dates += gson.toJson(datesJson);

                }

                logWriter.add(dates);
                logWriter.log(0);
            }
        }catch (Exception e){
            e.printStackTrace();
            logWriter.add("예약 가능 여부 조회 실패");
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
//            System.out.println("예약 가능 여부 조회 실패");

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

    // 온다에 예약정보 전송
    public void createBookingInfo(int intBookingID){
        try{
            // intOrderID로 필요한 정보 조회
            BookingDto bookingDto = bookingMapper.getBookingByIntBookingID(intBookingID);

            // 온다에 보낼 데이터 생성
            JSONObject requestJson = new JSONObject();

            String propertyId = bookingDto.getAccommId();

            String currency = "KRW";
            String checkInDate = bookingDto.getCheckInDate();
            String checkOutDate = bookingDto.getCheckOutDate();
            requestJson.put("currency", currency);
            requestJson.put("channel_booking_number", intBookingID);
//            requestJson.put("channel_booking_number", "27731622");
            requestJson.put("checkin", checkInDate);
            requestJson.put("checkout", checkOutDate);

            int intRoomCount = bookingDto.getIntRoomCount();

            JSONArray rateplans = new JSONArray();
            // 객실 수에 따라서
            for(int i=0; i<intRoomCount; i++){
                JSONObject rateplanJson = new JSONObject();

                String strRatePlanId = bookingDto.getStrRatePlanId();
                int intSalePrice = bookingDto.getIntSalePrice();
                int intPersonCount = bookingDto.getIntPersonCount();
                rateplanJson.put("rateplan_id", strRatePlanId);
                rateplanJson.put("amount", intSalePrice); // 판매가를 가져오는게...맞나?

                JSONObject number_of_guest = new JSONObject();
                number_of_guest.put("adult", intPersonCount);
                rateplanJson.put("number_of_guest", number_of_guest);

                JSONArray guests = new JSONArray();
                JSONObject guestsJson = new JSONObject();
                String strRecvName = bookingDto.getStrRecvName();
                String strRecvEmail = bookingDto.getStrRecvEmail();
//                String strRecvEmail = "dbwjd@naver.com";
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
//            String strOrdEmail = "dbwjd@naver.com";
            String strOrdPhone = bookingDto.getStrOrdPhone();
            String strOrdNationality = "KR";
            String timezone = "Asia/Seoul";
            booker.put("name", strOrdName);
            booker.put("email", strOrdEmail);
            booker.put("phone", strOrdPhone);
            booker.put("nationality", strOrdNationality);
            booker.put("timezone", timezone);

            requestJson.put("booker", booker);

//            Gson gson = new GsonBuilder().setPrettyPrinting().create();
//            String contetns = gson.toJson(requestJson);

            String contetns = requestJson.toJSONString();

            System.out.println(contetns);

            createBooking(propertyId, contetns);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void createBooking(String propertyId, String contents){
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(contents, mediaType);
        Request request = new Request.Builder()
                .url("https://dapi.tport.dev/gds/diglett/properties/" + propertyId + "/bookings")
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("Authorization", Constants.ondaAuth)
                .build();
        try{
            Response response = client.newCall(request).execute();

            System.out.println("response : " + response);
            String responseBody = response.body().string();
            JSONParser jsonParser = new JSONParser();
            JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

            System.out.println("responseJson : " + responseJson);
            if(response.isSuccessful()){
                int intBookingID = Integer.parseInt(responseJson.get("channel_booking_number").toString());
                String strSpBookingId = responseJson.get("booking_number").toString();
                String status = responseJson.get("status").toString();
                String strBookingProcess = "";
                if(status.equals("pending")){
                    strBookingProcess = "2"; // 번호대기
                }else if(status.equals("confirmed")){
                    strBookingProcess = "4"; // 완료
                }else if(status.equals("canceled")){
                    strBookingProcess = "5"; // 취소대기
                }

                JSONArray rateplanArr = (JSONArray) responseJson.get("rateplans");

                String strRefundPolicys = "";
                for(int i=0; i< rateplanArr.size(); i++){
                    JSONObject rateplanJson = (JSONObject) rateplanArr.get(i);

                    boolean refundable = (boolean) rateplanJson.get("refundable");
                    String strRefundYN = "";
                    if(refundable){
                        strRefundYN = "Y";
                    }else{
                        strRefundYN = "N";
                    }

                    String strOndaRefundType = rateplanJson.get("refund_type").toString();

                    JSONArray refundPolicyArr = (JSONArray) rateplanJson.get("refund_policy");


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
                        strRefundPolicys += (i+1) + "|^|" + strUntilDate + "|^|" + intPercent + "|^|" + intRefundPrice + "|^|" + intRefundFee  + "|^|" + strRefundYN  + "|^|" + strOndaRefundType + "|^|" + "{{|}}";
                    }

                    strRefundPolicys = strRefundPolicys.substring(0, strRefundPolicys.length()-5);
                }

                // booking 테이블 UPDATE, refund_policy 테이블 INSERT
                bookingMapper.updateBooking(intBookingID, strSpBookingId, strBookingProcess, strRefundPolicys);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
