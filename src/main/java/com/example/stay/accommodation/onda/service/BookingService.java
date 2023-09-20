package com.example.stay.accommodation.onda.service;

import com.example.stay.accommodation.onda.mapper.OndaMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.openMarket.common.dto.CancelRulesDto;
import com.example.stay.openMarket.common.dto.RsvStayDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
public class BookingService {

    @Autowired
    private OndaMapper ondaMapper;

    CommonFunction commonFunction = new CommonFunction();

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // 예약 전 예약 가능 여부 조회
    public boolean checkAvailBooking(String strPropertyID, String strRmtypeID, String strRateplanID, String strCheckIn, String strCheckOut){
        boolean availability = false;
        try{
            String url = "properties/" + strPropertyID + "/roomtypes/" + strRmtypeID + "/rateplans/" + strRateplanID + "/checkavail?checkin=" + strCheckIn + "&checkout=" + strCheckOut;

            JSONObject responseJson = callOndaGetAPI(url);
            if(responseJson != null){
                availability = (boolean) responseJson.get("availability");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return availability;
    }

    // 예약 전 취소, 환불 정책 조회
//    public void getRefundPolicy(String strPropertyID, String roomTypeId, String ratePlanId, String checkInDate, String checkOutDate){
//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .url("https://dapi.tport.dev/gds/diglett/properties/" + strPropertyID + "/roomtypes/" + roomTypeId + "/rateplans/" + ratePlanId + "/refund_policy?checkin=" + checkInDate + "&checkout=" + checkOutDate)
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
    public String createBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        String statusCode = "200";
        String message = "";
        try{
            RsvStayDto rsvStayDto = ondaMapper.getReservation(intRsvID);

            String strStatusCode = rsvStayDto.getStrStatusCode();
            String strPropertyID = rsvStayDto.getStrPropertyID();
            String strRsvRmNum = "";

            if(strStatusCode.equals("0")){
                // 온다에 보낼 데이터 생성
                JSONObject requestJson = new JSONObject();

                String strRmtypeID = rsvStayDto.getStrRmtypeID();
                String strRateplanID = rsvStayDto.getStrRateplanID();

                Date dateCheckIn = rsvStayDto.getDateCheckIn();
                Date dateCheckOut = rsvStayDto.getDateCheckOut();
                
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String strCheckIn = simpleDateFormat.format(dateCheckIn);
                String strCheckOut = simpleDateFormat.format(dateCheckOut);

                requestJson.put("currency", "KRW");
                requestJson.put("channel_booking_number", intRsvID);
                requestJson.put("checkin", strCheckIn);
                requestJson.put("checkout", strCheckOut);


                // 예약 가능한지 확인
                boolean availBooking = checkAvailBooking(strPropertyID, strRmtypeID, strRateplanID, strCheckIn, strCheckOut);
                if(availBooking){
                    int intRmCnt = rsvStayDto.getIntRmCnt();

                    // 몇박인지 구하기
                    int stayDays = (int)((dateCheckOut.getTime() - dateCheckIn.getTime()) / 1000) / (24*60*60);

                    JSONArray rateplans = new JSONArray();
                    // 객실 수에 따라서
                    for(int i=0; i<intRmCnt; i++){
                        JSONObject rateplanJson = new JSONObject();

                        rateplanJson.put("rateplan_id", strRateplanID);

                        int intTotalCost = (int) rsvStayDto.getMoneyCost() * stayDays;
                        rateplanJson.put("amount", intTotalCost);

                        int intQuantityA = rsvStayDto.getIntQuantityA();
                        int intQuantityC = rsvStayDto.getIntQuantityC();
                        int intQuantityB = rsvStayDto.getIntQuantityB();

                        JSONObject number_of_guest = new JSONObject();
                        number_of_guest.put("adult", intQuantityA);

                        // 영유아, 아동 나이/인원
                        ArrayList childList = new ArrayList();
                        if(intQuantityC != 0 || intQuantityB != 0){
                            for(int j=0; j<intQuantityC; j++){
                                childList.add(5);
                            }

                            for(int j=0; j<intQuantityB; j++){
                                childList.add(1);
                            }

                            number_of_guest.put("child_age", childList);
                        }
                        rateplanJson.put("number_of_guest", number_of_guest);

                        JSONArray guests = new JSONArray();
                        JSONObject guestsJson = new JSONObject();
                        String strRcvName = rsvStayDto.getStrRcvName();
                        String strRcvEmail = rsvStayDto.getStrRcvEmail();
                        String strRcvPhone = rsvStayDto.getStrRcvPhone();
                        guestsJson.put("name", strRcvName);
                        guestsJson.put("email", strRcvEmail);
                        guestsJson.put("phone", strRcvPhone);
                        guestsJson.put("nationality", "KR");
                        guests.add(guestsJson);

                        rateplanJson.put("guests", guests);

                        rateplans.add(rateplanJson);
                    }

                    requestJson.put("rateplans", rateplans);

                    JSONObject booker = new JSONObject();
                    String strOrdName = rsvStayDto.getStrOrdName();
                    String strOrdPhone = rsvStayDto.getStrOrdPhone();
                    booker.put("name", strOrdName);
                    booker.put("phone", strOrdPhone);

                    String strOrdEmail = rsvStayDto.getStrOrdEmail();
                    if(strOrdEmail == null){
                        strOrdEmail = "condo24@condo24.com";
                    }
                    booker.put("email", strOrdEmail);

                    booker.put("nationality", "KR");
                    booker.put("timezone", "Asia/Seoul");

                    requestJson.put("booker", booker);

                    // api 호출
                    String url = "properties/" + strPropertyID + "/bookings";
                    JSONObject responseJson = callOndaPostAPI(url, requestJson);

                    if(responseJson != null){
                        strRsvRmNum = responseJson.get("booking_number").toString();

                        JSONArray rateplanArr = (JSONArray) responseJson.get("rateplans");
                        JSONObject rateplanJson = (JSONObject) rateplanArr.get(0);

//                        boolean refundable = (boolean) rateplanJson.get("refundable");
//                        String strRefundYN = "";
//                        if(refundable){
//                            strRefundYN = "Y";
//                        }else{
//                            strRefundYN = "N";
//                        }

//                        String strOndaRefundType = rateplanJson.get("refund_type").toString();
                        JSONArray refundPolicyArr = (JSONArray) rateplanJson.get("refund_policy");

                        String strPenaltyDatas = makeCancelRules(refundPolicyArr, dateCheckIn);

                        // 예약 테이블 업데이트 & 취소규정 INSERT
                        String updateResult = ondaMapper.updateRsvStay(intRsvID, "4", strRsvRmNum, strPenaltyDatas);
                        if(updateResult.equals("저장완료")){
                            message = "예약완료";
                        }else{
                            message = "예약실패";
                        }
                    }

                }else{
                    message = "예약 불가";
                }
            }else if(strStatusCode.equals("2")) { // 번호대기였을 경우
                // 예약 상태 조회
                String url = "properties/" + strPropertyID + "/bookings/" + intRsvID;
                JSONObject responseJson = callOndaGetAPI(url);

                if(responseJson != null) {
                    String status = responseJson.get("status").toString();
                    if (status.equals("pending")) { // 예약 확정 대기 상태
                        message = "예약이 아직 확정되지 않았습니다";
                    } else if (status.equals("confirmed")) { // 예약 확정 상태
                        // 재고, 환불규정은 예약 생성할 때 업데이트 했으니까 상태값만 업데이트하면됨
//                        String updateResult = ondaMapper.updateRsvStay(intRsvID, "4", strRsvRmNum);
                        int updateResult = ondaMapper.updateRsvStatus(intRsvID, "4");
                        if(updateResult > 0){
                            message = "예약완료";
                        }else {
                            message = "예약실패";
                        }
                    } else if (status.equals("canceled")) { // 취소된 상태
                        message = "예약이 취소된 상태입니다";
                    }

                }


            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            message = "예약 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }


        return commonFunction.makeReturn(dataType, statusCode, message);
    }

//    // 예약 상태 조회
//    public String checkBooking(String strPropertyID, int intRsvID){
//        String strStatus = "";
//
//        try{
//            String url = "properties/" + strPropertyID + "/bookings/" + intRsvID;
//
//            JSONObject responseJson = callOndaGetAPI(url);
//
//            if(responseJson != null){
//                String status = responseJson.get("status").toString();
//                if (status.equals("pending")) {
//                    strStatus = "2"; // 예약 확정 대기 상태
//                } else if (status.equals("confirmed")) {
//                    strStatus = "4"; // 예약 확정 상태
//                } else if (status.equals("canceled")) {
//                    strStatus = "5"; // 취소된 상태
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return strStatus;
//    }

    // 예약 취소
    public String cancelBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        String statusCode = "200";
        String message = "";

        try{
            RsvStayDto rsvStayDto = ondaMapper.getReservation(intRsvID);

            String strPropertyID = rsvStayDto.getStrPropertyID();
            String strRsvRmNum = rsvStayDto.getStrRsvRmNum();

            Date dateCheckIn = rsvStayDto.getDateCheckIn();
            Date dateCheckOut = rsvStayDto.getDateCheckOut();

            //몇박인지 계산
            int stayDays = (int) ((dateCheckOut.getTime() - dateCheckIn.getTime()) / 1000) / (24*60*60);

            // 온다에서 준 판매가 총금액
            int intTotalCost = (int) rsvStayDto.getMoneyCost() * stayDays;

            // 온다에 보낼 데이터 생성
            JSONObject requestJson = new JSONObject();
            requestJson.put("currency", "KRW");
            requestJson.put("total_amount", intTotalCost); // 총 결제금액

            // 취소하는게 체크인 전 며칠인지 계산해서 penalty 테이블에 있는 데이터면 가져와서 넣기
            int diffDay = (int) ((dateCheckIn.getTime() - new Date().getTime()) / 1000) / (24*60*60);

            double refund = ondaMapper.getMoneyRefund(intRsvID, diffDay);
            if(refund != 0){
                requestJson.put("refund_amount", refund * rsvStayDto.getIntRmCnt()); // 환불금액
            }

            // api 호출
            String url = "properties/" + strPropertyID + "/bookings/" + strRsvRmNum + "/cancel";

            JSONObject responseJson = callOndaPutAPI(url, requestJson);

            if(responseJson != null){
                // 예약 테이블 상태값 업데이트
                String updateResult = ondaMapper.updateRsvStay(intRsvID, "5", strRsvRmNum, "");
                if(updateResult.equals("저장완료")){
                    message = "예약 취소 완료";
                }else{
                    message = "예약 취소 실패";
                }
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            message = "예약 취소 실패";
            statusCode = "500";

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 취소/환불 규정 조회
    // TODO : 언제 사용할건지 정해서 return 어떻게할지 정해야함
    public void getCancelPolicy(String strPropertyID, String roomTypeId, String ratePlanId,
                                String strCheckInDate, String strCheckOutDate, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String message = "";

        JSONObject responseJson = new JSONObject();
        try{
            String url = "properties/" + strPropertyID + "/roomtypes/" + roomTypeId + "/rateplans/" + ratePlanId
                    + "/refund_policy?checkin=" + strCheckInDate + "&checkout=" + strCheckOutDate;
            responseJson = callOndaGetAPI(url);

            if(responseJson != null){
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
            }else{
                message = "onda api 호출 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

    }

    // 예약 대사자료 조회
    public String getBookingList(String dataType, String option, String strFrom, String strTo, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        String statusCode = "200";
        String message = "";

        JSONObject responseJson = new JSONObject();
        try{
            // TODO : 예약 검색 기준 어떤거로?, limit 몇 개?
            String url = "bookings?limit=500&option=" + option + "&from=" + strFrom + "&to=" + strTo;
            responseJson = callOndaGetAPI(url);

            if(responseJson != null){
                message = "예약 대사자료 조회 완료";
            }else{
                message = "onda api 호출 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message, responseJson);
    }

    // TODO : 수정해야함
    public String makeCancelRules(JSONArray refundPolicyArr, Date dateCheckIn){
        String strPenaltyDatas = "";
        try{
            String strRateFlag = "P";
            for(int j=0; j<refundPolicyArr.size(); j++){
                JSONObject refundPolicyJson = (JSONObject) refundPolicyArr.get(j);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ssXXX"); // 2023-05-25T15:00:23+09:00
                SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");

                Date originDate = sdf.parse(refundPolicyJson.get("until").toString());

                System.out.println("originDate : " + originDate);

                // 한국날짜시간으로 변경
                Calendar cal = Calendar.getInstance();
                cal.setTime(originDate);
                cal.add(Calendar.HOUR, 9);

                Date untilDate = sdf.parse(cal.getTime().toString());

                System.out.println("untilDate : " + untilDate);

                int diffDay = (int) ((dateCheckIn.getTime() - untilDate.getTime()) / 1000) / (24*60*60);

                System.out.println("diffDay : " + diffDay);

                Date time = sdf2.parse(cal.getTime().toString());

                System.out.println("time : " + time);

                int intPercent = Integer.parseInt(refundPolicyJson.get("percent").toString());
                int intRefundPrice = Integer.parseInt(refundPolicyJson.get("refund_amount").toString());
                int intRefundFee = Integer.parseInt(refundPolicyJson.get("charge_amount").toString());

                strPenaltyDatas += strRateFlag + "|^|" + intPercent + "|^|" + diffDay + "|^|" + time + "|^|" + intRefundPrice + "|^|" + intRefundFee + "{{|}}";
            }
            strPenaltyDatas = strPenaltyDatas.substring(0, strPenaltyDatas.length()-5);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }

        return strPenaltyDatas;
    }

    public JSONObject callOndaGetAPI(String url){
        long startTime = System.currentTimeMillis();
        JSONObject responseJson = new JSONObject();
        String message = "";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constants.ondaPath + url)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", Constants.ondaAuth)
                .build();

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);
        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                responseJson = (JSONObject) jsonParser.parse(responseBody);
                message = gson.toJson(responseJson);

            }else{
                message = "response code : " + response.code();
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return responseJson;
    }

    public JSONObject callOndaPostAPI(String url, JSONObject requestJson){
        long startTime = System.currentTimeMillis();
        JSONObject responseJson = new JSONObject();
        String message = "";

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(gson.toJson(requestJson), mediaType);
        Request request = new Request.Builder()
                .url(Constants.ondaPath + url)
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("Authorization", Constants.ondaAuth)
                .build();

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);

        try {
            logWriter.addRequest(gson.toJson(requestJson));

            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                responseJson = (JSONObject) jsonParser.parse(responseBody);
                message = gson.toJson(responseJson);

            }else{
                message = response.toString();
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return responseJson;
    }

    public JSONObject callOndaPutAPI(String url, JSONObject requestJson){
        long startTime = System.currentTimeMillis();
        JSONObject responseJson = new JSONObject();
        String message = "";

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(gson.toJson(requestJson), mediaType);
        Request request = new Request.Builder()
                .url(Constants.ondaPath + url)
                .put(body)
                .addHeader("accept", "application/json")
                .addHeader("Authorization", Constants.ondaAuth)
                .build();

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);

        try {
            logWriter.addRequest(gson.toJson(requestJson));

            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                responseJson = (JSONObject) jsonParser.parse(responseBody);
                message = gson.toJson(responseJson);

            }else{
                message = "response code : " + response.code();
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return responseJson;
    }
}
