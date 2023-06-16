package com.example.stay.accommodation.sono.service;

import com.example.stay.accommodation.sono.mapper.BookingMapper;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.common.util.ResponseResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service("sono.BookingService")
public class BookingService {

    @Autowired
    private BookingMapper bookingMapper;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    //패키지 목록 조회
    public ResponseResult getPackageList(HttpServletRequest httpServletRequest) {
        long startTime = System.currentTimeMillis();
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String statusCode ="";
        String msg ="";
        String result = new String();
        String pkgData = "";

        JSONObject requestJson = new JSONObject();
        requestJson.put("businessId", Constants.sonoPackId);
        requestJson.put("language", Constants.sonoLanguage);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.sonoPackPath)
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.sonoPackAuth)
                .addHeader("Content-Type", "application/json")
                .build();

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);

        try {
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {
                //response 파싱
                String responseBody = response.body().string();
                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                List<Map<String, Object>> resultList = (List<Map<String, Object>>) responseJson.get("resultList");
                return new ResponseResult<>(statusCode, msg, responseJson);


            }

        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");

        }

        return new ResponseResult<>(statusCode, msg, result);

    }
    //패키지 상세 조회
    public ResponseResult getPackageInfo(String pkgNo, HttpServletRequest httpServletRequest) {
        long startTime = System.currentTimeMillis();
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String statusCode ="";
        String msg ="";
        String result = new String();

        JSONObject requestJson = new JSONObject();
        requestJson.put("pkgNo", pkgNo);
        requestJson.put("businessId", Constants.sonoPackId);
        requestJson.put("language", Constants.sonoLanguage);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.sonoPackPath + "/detail")
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.sonoPackAuth)
                .addHeader("Content-Type", "application/json")
                .build();

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);

        try {
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {
                //response 파싱
                String responseBody = response.body().string();
                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                System.out.println(responseJson);

                return  new ResponseResult<>("","", responseJson);
            }

        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");
        }

        return new ResponseResult<>(statusCode, msg, result);

    }
    //패키지 현황 조회
    public ResponseResult getPackageStatus(String pkgNo, String storeCd, String sDate, String rmTypeCd, String ciYmd, HttpServletRequest httpServletRequest) {
        long startTime = System.currentTimeMillis();
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String statusCode ="";
        String msg ="";
        String result = new String();
        String detailPath = "";

        /*
        파라미터별 조회 url 분기처리
         */
        if(storeCd == null){
            //이용일자별
            detailPath = "05";
        } else {
            if(rmTypeCd != null) {
                if(ciYmd != null) {
                    //영업장, 객실유형, 이용일자별
                    detailPath = "03";
                } else {
                    //영업장, 객실유형별
                    detailPath = "02";
                }
            } else {
                if(ciYmd != null) {
                    //영업장, 이용일자별
                    detailPath = "04";
                } else {
                    //영업장별
                    detailPath = "01";
                }

            }
        }

        JSONObject requestJson = new JSONObject();
        if(pkgNo != null) requestJson.put("pkgNo", pkgNo);
        if(storeCd != null) requestJson.put("storeCd", storeCd);
        if(sDate != null) requestJson.put("sDate", sDate);
        if(rmTypeCd != null) requestJson.put("rmTypeCd", rmTypeCd);
        if(ciYmd != null) requestJson.put("ciYmd", ciYmd);
        requestJson.put("language", Constants.sonoLanguage);
        requestJson.put("businessId", Constants.sonoPackId);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.sonoPackPath + "/statusList" + detailPath )
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.sonoPackAuth)
                .addHeader("Content-Type", "application/json")
                .build();

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);

        try {
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {
                //response 파싱
                String responseBody = response.body().string();
                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                System.out.println(responseJson);

                //재고 Update
                /*
                재고는 STOCK_REAL 테이블에서 관리
                RMTYPE테이블에 등록된 ID로 rmIdx 조회...?
                재고, 원가, 판매가
                 */

                return  new ResponseResult<>("","", responseJson);
            } else {
                //예약 실패시
                //response 파싱
                String responseBody = response.body().string();
                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                return  new ResponseResult<>("","", responseJson);
            }

        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");
        }
        return new ResponseResult<>(statusCode, msg, result);
    }
    //패키지 요금 조회
    public ResponseResult getPackageAmount(String pkgNo, String storeCd, String sDate, String rmTypeCd, String ciYmd, String nights, String rmCnt, HttpServletRequest httpServletRequest) {
        long startTime = System.currentTimeMillis();
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String statusCode ="";
        String msg ="";
        String result = new String();
        String detailPath = "";     //요금조회 상세 경로

        /*
        파라미터별 조회 url 분기처리
         */
        if(storeCd == null){
            //이용일자별
            detailPath = "05";
        } else {
            if(rmTypeCd != null) {
                if(ciYmd != null) {
                    //영업장, 객실유형, 이용일자별
                    detailPath = "03";
                } else {
                    //영업장, 객실유형별
                    detailPath = "02";
                }
            } else {
                if(ciYmd != null) {
                    //영업장, 이용일자별
                    detailPath = "04";
                } else {
                    //영업장별
                    detailPath = "01";
                }

            }
        }


        JSONObject requestJson = new JSONObject();
        requestJson.put("pkgNo", pkgNo);
        requestJson.put("storeCd", storeCd);
        requestJson.put("sDate", sDate);
        requestJson.put("rmTypeCd", rmTypeCd);
        requestJson.put("ciYmd", ciYmd);
        requestJson.put("nights", nights);
        requestJson.put("rmCnt", rmCnt);
        requestJson.put("businessId", Constants.sonoPackId);
        requestJson.put("language", Constants.sonoLanguage);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.sonoPackPath + "/amountList" + detailPath + "?businessId="+Constants.sonoPackId)
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.sonoPackAuth)
                .addHeader("Content-Type", "application/json")
                .build();

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);

        try {
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {
                //response 파싱
                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                List<Map<String, Object>> resultList = (List<Map<String, Object>>) responseJson.get("resultList");
                for (int i = 0 ; i < resultList.size();i++) {
                    String reStoreCd = resultList.get(i).get("storeCd").toString();
                    String rmTypeNm = resultList.get(i).get("rmTypeNm").toString();
                    String orgRmAmt = resultList.get(i).get("orgRmAmt").toString();
                    String saleRmAmt = resultList.get(i).get("saleRmAmt").toString();
                }


                return  new ResponseResult<>("","", responseJson);
            }
            /* RMTYPE insert를 위한 조립
            String strRmtypeData = strDeleteYn + "|^|" + strIngYn  + "|^|" + intQuanStd + "|^|" +
                    intQuanMax + "|^|" + intMinSleep + "|^|" + intMaxSleep + "|^|" + strSubject + "|^|" +
                     strDescription + "|^|" + strRmtypeID + "|^|" + strRateplanID + "|^|" ;
            */

        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");

        }

        return new ResponseResult<>(statusCode, msg, result);

    }
    //예약
    public ResponseResult reservation(String pkgNo, String storeCd, String ciYmd, String rmTypeCd, String comRsvNo, String userName, String userTel, String payAmt, String adultCnt, String childCnt ,HttpServletRequest httpServletRequest) {
        long startTime = System.currentTimeMillis();
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String statusCode ="";
        String msg ="";
        String result = new String();

        JSONObject requestJson = new JSONObject();
        requestJson.put("pkgNo", pkgNo);
        requestJson.put("storeCd", storeCd);
        requestJson.put("ciYmd", ciYmd);
        requestJson.put("rmTypeCd", rmTypeCd);
        requestJson.put("comRsvNo", comRsvNo);
        requestJson.put("userName", userName);
        requestJson.put("userTel", userTel);
        requestJson.put("payAmt", payAmt);
        requestJson.put("adultCnt", adultCnt);
        requestJson.put("childCnt", childCnt);
        requestJson.put("businessId", Constants.sonoPackId);
        requestJson.put("language", Constants.sonoLanguage);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.sonoPackPath + "/amountList")
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.sonoPackAuth)
                .addHeader("Content-Type", "application/json")
                .build();

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);

        try {
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {
                //response 파싱
                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                return  new ResponseResult<>("","", responseJson);
            }

        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");
        }

        return new ResponseResult<>(statusCode, msg, result);

    }
    //영업장 목록조회
    public ResponseResult getRoomList(HttpServletRequest httpServletRequest) {
        long startTime = System.currentTimeMillis();
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String statusCode ="";
        String msg ="";
        String result = new String();
        String roomData = "";

        JSONObject requestJson = new JSONObject();
        requestJson.put("businessId", Constants.sonoRoomId);
        requestJson.put("language", Constants.sonoLanguage);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.sonoRoomPath + "/stores")
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.sonoRoomAuth)
                .addHeader("Content-Type", "application/json")
                .build();

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);

        try {
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {
                //response 파싱
                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                List<Map<String, Object>> resultList = (List<Map<String, Object>>) responseJson.get("resultList");

                return  new ResponseResult<>("","", responseJson);

            }

        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");

        }

        return new ResponseResult<>(statusCode, msg, result);

    }


    //insert
    public String insertSONO(HttpServletRequest httpServletRequest) {
        String result = "";

        ResponseResult packageResponseResult = getPackageList(httpServletRequest);
        ResponseResult RoomResponseResult = getRoomList(httpServletRequest);

        packageResponseResult.toString();
        JSONParser jsonParser = new JSONParser();
        try {
            String pkgData = "";
            String roomData = "";

            String accommData = "";
            String strType = "01";
            String propertyID = "";

            JSONObject packageResponseJson = (JSONObject) jsonParser.parse(packageResponseResult.getResult().toString());
            JSONObject roomResponseJson = (JSONObject) jsonParser.parse(RoomResponseResult.getResult().toString());
            List<Map<String, Object>> packageResultList = (List<Map<String, Object>>) packageResponseJson.get("resultList");
            List<Map<String, Object>> roomResultList = (List<Map<String, Object>>) roomResponseJson.get("resultList");

            for (int i = 0 ; i<roomResultList.size() ; i++) {

                String pkgNo = (String) roomResultList.get(i).get("pkgNo");
                String pkgNm = (String) roomResultList.get(i).get("pkgNm");
                String lcalCd = (String) roomResultList.get(i).get("lcalCd");
                String lcalNm = (String) roomResultList.get(i).get("lcalNm");
                String saleStartDt = (String) roomResultList.get(i).get("saleStartDt");
                String saleEndDT = (String) roomResultList.get(i).get("saleEndDt");
                String curRsvYN = (String) roomResultList.get(i).get("curRsvYN");
                String curRsvTime = (String) roomResultList.get(i).get("curRsvTime");
                String nights = (String) roomResultList.get(i).get("nights");

                //roomData = 삭제여부 |^| 사용여부 |^| 기준인원 |^| 최대인원 |^| 룸데이터 |^| 최소숙박 |^| 최대숙박일 |^| 조식 |^| depth |^| 환불여부

                roomData += "N" + "|^|" + "Y" + "|^|" + "1" + "|^|" + "77" + "|^|";


                List<Map<String, Object>> roomList = (List<Map<String, Object>>) roomResultList.get(i).get("roomTypeList");

                for (int j = 0 ; j<roomList.size() ; j++) {
                    String storeCd = (String) roomList.get(j).get("storeCd");
                    String storeNm = (String) roomList.get(j).get("storeNm");
                    String rmTypeCd = (String) roomList.get(j).get("rmTypeCd");
                    String rmTypeNm = (String) roomList.get(j).get("rmTypeNm");

                    if ( j != roomList.size()-1){
                        roomData += "" + "|~|" + rmTypeCd + "|~|" + rmTypeNm + "|~|" + "" + "{{^}}";
                    } else {
                        roomData += "" + "|~|" + rmTypeCd + "|~|" + rmTypeNm + "|~|" + "" + "|^|";
                    }
                }
                if (i != roomResultList.size()-1){
                    roomData += "1" + "|^|" +"77" + "|^|" + "" + "|^|" + "1" + "|^|" + "" + "|^|" + "" + "{{|}}";
                } else {
                    roomData += "1" + "|^|" +"77" + "|^|" + "" + "|^|" + "1" + "|^|" + "" + "|^|" + "";
                }

            }

            for (int i = 0 ; i<packageResultList.size() ; i++) {
                    /*
                    pkgNo, saleStartDt, curRsvYN, curRsvTime, nights, rmCnt, pkgNm, todaySaleYn, lcalNm, saleEndDT, lcalCd
                     */
                String pkgNo = (String) packageResultList.get(i).get("pkgNo");//.toString();
                String pkgNm = (String) packageResultList.get(i).get("pkgNm");//.toString();
                String lcalCd = (String) packageResultList.get(i).get("lcalCd");//.toString();
                String lcalNm = (String) packageResultList.get(i).get("lcalNm");//.toString();
                String saleStartDt = (String) packageResultList.get(i).get("saleStartDt");//.toString();
                String saleEndDT = (String) packageResultList.get(i).get("saleEndDt");//.toString();
                String curRsvYN = (String) packageResultList.get(i).get("curRsvYN");//.toString();
                String curRsvTime = (String) packageResultList.get(i).get("curRsvTime");//.toString();
                String nights = (String) packageResultList.get(i).get("nights");//.toString();

                //pkgData = 패키지구분(소노:01)|^|패키지번호|^|패키지명|^|지역코드|^|지역명|^|판매시작일자|^|판매종료일자|^|즉시판매여부|^|예약가능시간|^|박수|^|최대예약가능객실수|^|roomList
                pkgData += "01" + "|^|" + pkgNo + "|^|" + pkgNm + "|^|" + lcalCd + "|^|" + lcalNm + "|^|" + saleStartDt+ "|^|" + saleEndDT + "|^|"
                        + curRsvYN + "|^|" + curRsvTime + "|^|" + nights + "|^|"  + "" + "|^|";

                //roomData = 삭제여부 |^| 사용여부 |^| 기준인원 |^| 최대인원 |^| 룸데이터 |^| 최소숙박 |^| 최대숙박일 |^| 조식 |^| depth |^| 환불여부

                roomData += "N" + "|^|" + "Y" + "|^|" + "1" + "|^|" + "77" + "|^|";

                List<Map<String, Object>> pkgRoomList = (List<Map<String, Object>>) packageResultList.get(i).get("roomList");

                for (int j = 0 ; j<pkgRoomList.size() ; j++) {
                    String storeCd = pkgRoomList.get(j).get("storeCd").toString();
                    String storeNm = pkgRoomList.get(j).get("storeNm").toString();
                    String rmTypeCd = pkgRoomList.get(j).get("rmTypeCd").toString();
                    String rmTypeNm = pkgRoomList.get(j).get("rmTypeNm").toString();

                    if ( j != pkgRoomList.size()-1){
                        pkgData += storeCd + "|~|" + storeNm + "{{^}}";
                        roomData += pkgNo + "|~|" + rmTypeCd + "|~|" + rmTypeNm + "|~|" + storeCd + "{{^}}";
                    } else {
                        pkgData += storeCd + "|~|" + storeNm;
                        roomData += pkgNo + "|~|" + rmTypeCd + "|~|" + rmTypeNm + "|~|" + storeCd + "|^|";
                    }
                }
                if (i != packageResultList.size()-1){
                    pkgData += "{{|}}";
                    roomData += "1" + "|^|" +"77" + "|^|" + "" + "|^|" + "2" + "|^|" + "" + "|^|" + pkgNm + "{{|}}";
                } else {
                    roomData += "1" + "|^|" +"77" + "|^|" + "" + "|^|" + "2" + "|^|" + "" + "|^|" + pkgNm;
                }

            }




            System.out.println(pkgData);
            System.out.println(roomData);
            //System.out.println(accommData);

            //String insertResult = bookingMapper.insertRoom("", "", "", accommData, strType);
            String insertResult = bookingMapper.insertRoom(pkgData, roomData, "", accommData, strType);
            System.out.println(insertResult);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }




        //result = bookingMapper.insertRoom("","","", storeCD, "01");


        return result;

    }

}
