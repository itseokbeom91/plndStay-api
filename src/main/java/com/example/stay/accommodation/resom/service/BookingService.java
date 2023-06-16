package com.example.stay.accommodation.resom.service;

import com.example.stay.accommodation.resom.mapper.BookingMapper;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.common.util.ResponseResult;
import com.example.stay.common.util.XmlUtility;
import jdk.jfr.internal.consumer.OngoingStream;
import okhttp3.*;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service("resom.BookingService")
public class BookingService {

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private XmlUtility xmlUtility;

    //패키지 목록 조회
    public ResponseResult getPackageList() {
        long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String pkgData = "";

        JSONObject test = new JSONObject();
        JSONObject requestJson = new JSONObject();
        requestJson.put("businessId", Constants.resomId);
        requestJson.put("language", Constants.resomLanguage);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json;");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.resomPath)
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.resomAuth)
                .build();

        System.out.println(request.body());

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);
        try {
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {
                //response 파싱
                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                System.out.println(responseJson);

                List<Map<String, Object>> resultList = (List<Map<String, Object>>) responseJson.get("resultList");
                for (int i = 0;i<resultList.size();i++) {
                    Map<String, Object> packageMap = resultList.get(i);
                    String pkgNo = (String) packageMap.get("pkgNo");
                    String pkgNm = (String) packageMap.get("pkgNm");
                    String saleStartDt = (String) packageMap.get("saleStartDt");
                    String saleEndDt = (String) packageMap.get("saleEndDt");
                    String todaySaleYn = (String) packageMap.get("todaySaleYn");
                    String curRsvYn = (String) packageMap.get("curRsvYN");
                    String curRsvTime = (String) packageMap.get("curRsvTime");
                    String nights = (String) packageMap.get("nights");
                    String maxNights = (String) packageMap.get("maxNights");
                    String rmCnt = (String) packageMap.get("rmCnt");
                    String maxRmCnt = (String) packageMap.get("maxRmCnt");
                    //pkgData = 패키지구분|^|패키지번호|^|패키지명|^|지역코드|^|지역명|^|판매시작일자|^|판매종료일자|^|즉시판매여부|^|예약가능시간|^|박수|^|최대예약가능객실수|^|roomList
                    pkgData += "RESOM" + "|^|" + pkgNo + "|^|" + pkgNm + "|^|" + "" + "|^|" + "" + "|^|" + saleStartDt + "|^|" +
                            saleEndDt + "|^|" + curRsvYn + "|^|" + curRsvTime + "|^|" + nights + "|^|" + maxRmCnt + "|^|";
                    List<Map<String, Object>> roomList = (List<Map<String, Object>>) packageMap.get("roomList");
                    for (int j = 0; j<roomList.size();j++) {
                        String rmTypeCd = (String) roomList.get(i).get("storeCd");
                        String rmTypeNm = (String) roomList.get(i).get("storeNm");
                        if (j == roomList.size()-1){
                            pkgData += rmTypeCd + "|~|" + rmTypeNm;
                        } else {
                            pkgData += rmTypeCd + "|~|" + rmTypeNm + "{{^}}";
                        }


                    }
                    if(i != resultList.size()-1){
                        pkgData += "{{|}}";
                    }




                }
                System.out.println(pkgData);

                //String insertResult = bookingMapper.insertRoom(pkgData, "", "","","");
                //System.out.println(insertResult);

                return  new ResponseResult<>("","", responseJson);


                //logWriter.add(dates);
                //logWriter.log(0);
            }
        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");

        }

        return new ResponseResult<>("", "");

    }

    //영업장 목록 조회
    public ResponseResult getStoreList() {
        long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        JSONObject test = new JSONObject();
        JSONObject requestJson = new JSONObject();
        requestJson.put("businessId", Constants.resomId);
        requestJson.put("language", Constants.resomLanguage);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json;");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.resomPath + "/store/list" + "?businessId=" + Constants.resomId + "&language=" + Constants.resomLanguage)
                .get()
                .addHeader("X-AUTH-TOKEN", Constants.resomAuth)
                .build();

        System.out.println(request.body());

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


                //logWriter.add(dates);
                //logWriter.log(0);
            }
        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");

        }

        return new ResponseResult<>("", "");

    }

    //패키지 상세목록 조회
    public ResponseResult getPackageInfo(String pkgNo) {
        long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        JSONObject test = new JSONObject();
        JSONObject requestJson = new JSONObject();
        requestJson.put("pkgNo", pkgNo);
        requestJson.put("businessId", Constants.resomId);
        requestJson.put("language", Constants.resomLanguage);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json;");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.resomPath + "/detail")
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.resomAuth)
                .build();

        System.out.println(request.body());

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


                //logWriter.add(dates);
                //logWriter.log(0);
            }
        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");

        }

        return new ResponseResult<>("", "");

    }

    //패키지 현황 조회 (영업장별)
    public ResponseResult getPackageStatus(String pkgNo, String storeCd, String sDate) {
        long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        JSONObject test = new JSONObject();
        JSONObject requestJson = new JSONObject();
        requestJson.put("pkgNo", pkgNo);
        requestJson.put("storeCd", storeCd);
        requestJson.put("sDate", sDate);
        requestJson.put("businessId", Constants.resomId);
        requestJson.put("language", Constants.resomLanguage);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json;");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.resomPath + "/statusList01")
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.resomAuth)
                .build();

        System.out.println(request.body());

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


                //logWriter.add(dates);
                //logWriter.log(0);
            }
        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");

        }

        return new ResponseResult<>("", "");

    }

    //패키지 현황 조회 (영업장, 객실유형별)
    public ResponseResult getPackageStatus(String pkgNo, String storeCd, String sDate, String rmTypeCd) {
        long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        JSONObject test = new JSONObject();
        JSONObject requestJson = new JSONObject();
        requestJson.put("pkgNo", pkgNo);
        requestJson.put("storeCd", storeCd);
        requestJson.put("sDate", sDate);
        requestJson.put("rmTypeCd", rmTypeCd);
        requestJson.put("businessId", Constants.resomId);
        requestJson.put("language", Constants.resomLanguage);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json;");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.resomPath + "/statusList02")
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.resomAuth)
                .build();

        System.out.println(request.body());

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);
        try {
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {
                //response 파싱
                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                System.out.println(responseJson);

                List< Map<String, Object> > resultList = (List<Map<String, Object>>) responseJson.get("resultList");

                return  new ResponseResult<>("","", responseJson);


                //logWriter.add(dates);
                //logWriter.log(0);
            }
        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");

        }

        return new ResponseResult<>("", "");

    }

    //패키지 현황 조회 (영업장, 객실유형, 이용일자별)
    public ResponseResult getPackageStatus(String pkgNo, String storeCd, String rmTypeCd, String sDate, String nights) {
        long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        JSONObject test = new JSONObject();
        JSONObject requestJson = new JSONObject();
        requestJson.put("pkgNo", pkgNo);
        requestJson.put("storeCd", storeCd);
        requestJson.put("rmTypeCd", rmTypeCd);
        requestJson.put("sDate", sDate);
        requestJson.put("nights", nights);
        requestJson.put("businessId", Constants.resomId);
        requestJson.put("language", Constants.resomLanguage);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json;");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.resomPath + "/statusList04")
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.resomAuth)
                .build();

        System.out.println(request.body());

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


                //logWriter.add(dates);
                //logWriter.log(0);
            }
        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");
            System.out.println();

        }

        return new ResponseResult<>("", "");

    }

    //패키지 현황 조회 (영업장별 월 시작과 종료)
    public ResponseResult getPackageStatusMonth(String pkgNo, String storeCd, String sDate, String nights) {
        long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        JSONObject test = new JSONObject();
        JSONObject requestJson = new JSONObject();
        requestJson.put("pkgNo", pkgNo);
        requestJson.put("storeCd", storeCd);
        requestJson.put("sDate", sDate);
        requestJson.put("nights", nights);
        requestJson.put("businessId", Constants.resomId);
        requestJson.put("language", Constants.resomLanguage);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json;");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.resomPath + "/statusList03")
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.resomAuth)
                .build();

        System.out.println(request.body());

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


                //logWriter.add(dates);
                //logWriter.log(0);
            }
        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");

        }

        return new ResponseResult<>("", "");

    }

    //패키지 요금 조회 (영업장별)
    public ResponseResult getPackageAmount(String pkgNo, String storeCd, String sDate) {
        long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        JSONObject requestJson = new JSONObject();
        requestJson.put("pkgNo", pkgNo);
        requestJson.put("storeCd", storeCd);
        requestJson.put("sDate", sDate);
        requestJson.put("businessId", Constants.resomId);
        requestJson.put("language", Constants.resomLanguage);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json;");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.resomPath + "/amountList01")
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.resomAuth)
                .build();

        System.out.println(request.body());

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


                //logWriter.add(dates);
                //logWriter.log(0);
            }
        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");

        }

        return new ResponseResult<>("", "");

    }

    //패키지 요금 조회 (영업장, 객실유형별)
    public ResponseResult getPackageAmount(String pkgNo, String storeCd, String sDate, String rmTypeCd) {
        long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        JSONObject test = new JSONObject();
        JSONObject requestJson = new JSONObject();
        requestJson.put("pkgNo", pkgNo);
        requestJson.put("storeCd", storeCd);
        requestJson.put("sDate", sDate);
        requestJson.put("rmTypeCd", rmTypeCd);
        requestJson.put("businessId", Constants.resomId);
        requestJson.put("language", Constants.resomLanguage);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json;");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.resomPath + "/amountList02")
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.resomAuth)
                .build();

        System.out.println(request.body());

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);
        try {
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {
                //response 파싱
                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                if(!responseJson.get("resultCode").toString().equals("0000")) {
                    //조회 실패했을때
                    System.out.println("이곳에 조회실패시에 대한 로직 구현 필요!");


                    return new ResponseResult("", "", responseJson);
                }
                List< Map<String, Object> > resultList = (List<Map<String, Object>>) responseJson.get("resultList");

                for(int i=0;i<resultList.size();i++) {
                    System.out.print(resultList.get(i).get("ciYmd") + "의 원가는 ");
                    System.out.println(resultList.get(i).get("orgRmAmt")+"입니다.");
                }


                return  new ResponseResult<>("","", responseJson);


                //logWriter.add(dates);
                //logWriter.log(0);
            }
        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");

        }

        return new ResponseResult<>("", "");

    }

    //패키지 요금 조회 (영업장, 객실유형, 이용일자별)
    public ResponseResult getPackageAmount(String pkgNo, String storeCd, String sDate, String rmTypeCd, String nights) {
        long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        JSONObject test = new JSONObject();
        JSONObject requestJson = new JSONObject();
        requestJson.put("pkgNo", pkgNo);
        requestJson.put("storeCd", storeCd);
        requestJson.put("sDate", sDate);
        requestJson.put("rmTypeCd", rmTypeCd);
        requestJson.put("nights", nights);
        requestJson.put("businessId", Constants.resomId);
        requestJson.put("language", Constants.resomLanguage);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json;");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.resomPath + "/amountList02")
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.resomAuth)
                .build();

        System.out.println(request.body());

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


                //logWriter.add(dates);
                //logWriter.log(0);
            }
        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");

        }

        return new ResponseResult<>("", "");

    }

    //패키지 예약
    public ResponseResult createBooking(String pkgNo, String storeCd, String ciYmd, String rmTypeCd, String comRsvNo, String userName, String userTel, String payAmt, String adultCnt, String childCnt, String channelCd, String channelNm) {
        long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        JSONObject test = new JSONObject();
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
        requestJson.put("channelCd", channelCd);
        requestJson.put("channelNm", channelNm);
        requestJson.put("businessId", Constants.resomId);
        requestJson.put("language", Constants.resomLanguage);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json;");
        RequestBody body = RequestBody.create(mediaType, contents);

        //TO-DO 에약전 현황조회로 공실여부 먼저 파악해야함
        ResponseResult roomCount = getPackageStatus(pkgNo, storeCd, ciYmd, rmTypeCd);
        Map<String, Object> resultMap =(Map<String, Object>) roomCount.getResult();
        List<Map<String, Object>> roomList = (List<Map<String, Object>>) resultMap.get("resultList");

        for (Map<String, Object> listMap : roomList) {
            if(listMap.get("ciYmd").equals(ciYmd)) {
                if (listMap.get("leaveCnt").equals("-1")) {
                    //예약가능한 잔여객실 없음
                    System.out.println("예약 가능한 객실 없음!");
                }
            }
        }

        ResponseResult statusCount = getPackageStatus(pkgNo, storeCd, ciYmd);

        Request request = new Request.Builder()
                .url(Constants.resomPath + "/reservation")
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.resomAuth)
                .build();

        System.out.println(request.body());

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);
        try {
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {

                //response 파싱
                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                if(responseJson.get("resultCode").toString().equals("0000")){
                    return  new ResponseResult<>("","", responseJson);
                    //TO-DO 예약성공시 아래에 DB update 로직 추가
                } else {
                    //예약 실패시

                }
                System.out.println(responseJson);



                //logWriter.add(dates);
                //logWriter.log(0);
            }
        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");

        }

        return new ResponseResult<>("", "");

    }

    //패키지 연박예약
    public ResponseResult createBooking(String pkgNo, String storeCd, String ciYmd, String rmTypeCd, String comRsvNo, String userName, String userTel, String payAmt, String adultCnt, String childCnt, String channelCd, String channelNm, String nights, String rmCnt) {
        long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        JSONObject test = new JSONObject();
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
        requestJson.put("channelCd", channelCd);
        requestJson.put("channelNm", channelNm);
        requestJson.put("nights", nights);
        requestJson.put("rmCnt", rmCnt);
        requestJson.put("businessId", Constants.resomId);
        requestJson.put("language", Constants.resomLanguage);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json;");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.resomPath + "/reservation02")
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.resomAuth)
                .build();

        System.out.println(request.body());

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


                //logWriter.add(dates);
                //logWriter.log(0);
            }
        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");

        }

        return new ResponseResult<>("", "");

    }

    //예약 취소
    public ResponseResult cancelBooking(String roomRsvNo, String pkgSaleSeq, String roomRsvSeq, String comRsvNo) {
        long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        JSONObject test = new JSONObject();
        JSONObject requestJson = new JSONObject();
        requestJson.put("comRsvNo", comRsvNo);
        requestJson.put("roomRsvNo", roomRsvNo);
        requestJson.put("pkgSaleSeq", pkgSaleSeq);
        requestJson.put("roomRsvSeq", roomRsvSeq);
        requestJson.put("businessId", Constants.resomId);
        requestJson.put("language", Constants.resomLanguage);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json;");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.resomPath + "/cancel")
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.resomAuth)
                .build();

        System.out.println(request.body());

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


                //logWriter.add(dates);
                //logWriter.log(0);
            }
        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");

        }

        return new ResponseResult<>("", "");

    }

    //예약 대사
    public ResponseResult reservationList(String stndDt) {
        long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        JSONObject test = new JSONObject();
        JSONObject requestJson = new JSONObject();
        String requestUrl = "?stndDt=" + stndDt + "&businessId=" + Constants.resomId + "&language=" + Constants.resomLanguage;
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json;");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.resomPath + "/reservation/list" + requestUrl)
                .get()
                .addHeader("X-AUTH-TOKEN", Constants.resomAuth)
                .build();

        System.out.println(request.body());

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);
        try {
            Response response = client.newCall(request).execute();


            if(response.isSuccessful()) {
                //response 파싱
                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                System.out.println(responseJson);
                if(responseJson.get("resultCode").toString().equals("0000")){
                    //예약조회의 경우 rsvList로 반환됨 참고 요망
                    
                    return  new ResponseResult<>("","", responseJson);

                } else {

                }

                return  new ResponseResult<>("","", responseJson);

            } else {
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

        return new ResponseResult<>("", "");

    }

    //이용자 정보 변경
    public ResponseResult updateGuest(String roomRsvSeq, String pkgSaleSeq, String guestNm, String mpNo) {
        long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        JSONObject test = new JSONObject();
        JSONObject requestJson = new JSONObject();
        requestJson.put("roomRsvSeq", roomRsvSeq);
        requestJson.put("pkgSaleSeq", pkgSaleSeq);
        requestJson.put("guestNm", guestNm);
        requestJson.put("mpNo", mpNo);
        requestJson.put("businessId", Constants.resomId);
        requestJson.put("language", Constants.resomLanguage);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json;");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.resomPath + "/reservation/user/change")
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.resomAuth)
                .build();

        System.out.println(request.body());

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


                //logWriter.add(dates);
                //logWriter.log(0);
            }
        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");

        }

        return new ResponseResult<>("", "");

    }

    //패키지 예약조회
    public ResponseResult getPackageBookingInfo(String ciYmd, String roomRsvNo, String guestNm, String mpNo) {
        long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        JSONObject test = new JSONObject();
        JSONObject requestJson = new JSONObject();
        String requestUrl = "?ciYmd=" + ciYmd + "&businessId=" + Constants.resomId + "&language=" + Constants.resomLanguage + "&roomRsvNo=" + roomRsvNo + "&guestNm=" + guestNm + "&mpNo=" + mpNo;
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json;");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.resomPath + "/reservation/detail" + requestUrl)
                .get()
                .addHeader("X-AUTH-TOKEN", Constants.resomAuth)
                .build();

        System.out.println(request.body());

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


                //logWriter.add(dates);
                //logWriter.log(0);
            } else {

                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                System.out.println(responseJson);
            }
        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");

        }

        return new ResponseResult<>("", "");

    }

    //패키지 예약취소 (고객정보)
    public ResponseResult cancelPackage(String ciYmd, String roomRsvNo, String guestNm, String mpNo) {
        long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        JSONObject test = new JSONObject();
        JSONObject requestJson = new JSONObject();
        requestJson.put("ciYmd", ciYmd);
        requestJson.put("roomRsvNo", roomRsvNo);
        requestJson.put("guestNm", guestNm);
        requestJson.put("mpNo", mpNo);
        requestJson.put("businessId", Constants.resomId);
        requestJson.put("language", Constants.resomLanguage);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json;");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.resomPath + "/reservation/user/change")
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.resomAuth)
                .build();

        System.out.println(request.body());

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


                //logWriter.add(dates);
                //logWriter.log(0);
            }
        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");

        }

        return new ResponseResult<>("", "");

    }
    //insert
    public String insertRESOM(HttpServletRequest httpServletRequest) {
        String result = "";

        ResponseResult packageResponseResult = getPackageList();
        ResponseResult RoomResponseResult = getStoreList();

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

                String pkgNo = (String) packageResultList.get(i).get("pkgNo");
                String pkgNm = (String) packageResultList.get(i).get("pkgNm");
                String lcalCd = (String) packageResultList.get(i).get("lcalCd");
                String lcalNm = (String) packageResultList.get(i).get("lcalNm");
                String saleStartDt = (String) packageResultList.get(i).get("saleStartDt");
                String saleEndDT = (String) packageResultList.get(i).get("saleEndDt");
                String curRsvYN = (String) packageResultList.get(i).get("curRsvYN");
                String curRsvTime = (String) packageResultList.get(i).get("curRsvTime");
                String nights = (String) packageResultList.get(i).get("nights");
                String maxNights = (String) packageResultList.get(i).get("maxNights");

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
                    roomData += nights + "|^|" + maxNights + "|^|" + "" + "|^|" + "2" + "|^|" + "" + "|^|" + pkgNm + "{{|}}";
                } else {
                    roomData += nights + "|^|" + maxNights + "|^|" + "" + "|^|" + "2" + "|^|" + "" + "|^|" + pkgNm;
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
