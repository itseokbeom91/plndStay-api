package com.example.stay.accommodation.resom.service;

import com.example.stay.accommodation.kumho.mapper.BookingMapper;
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
/*
                for (int i = 0;i<=resultList.size();i++) {
                    Map<String, Object> packageMap = resultList.get(i);
                    String var1 = (String) packageMap.get("pkgNo");
                    String var2 = (String) packageMap.get("nights");
                    String var3 = (String) packageMap.get("maxNights");
                    String var4 = (String) packageMap.get("rmCnt");
                    String var5 = (String) packageMap.get("maxRmCnt");
                    List<Map<String, Object>> roomList = (List<Map<String, Object>>) packageMap.get("roomList");
                    for (int j = 0; j<=roomList.size();j++) {
                        Map<String, Object> roomMap = roomList.get(i);
                        String var6 = (String) roomMap.get("rmTypeCd");
                        System.out.println(var1 + " and " + var2 + " and " + var3 + " and " + var4 + " and " + var5 + " and " + var6);
                        //패키지 밀어넣기

                    }
                }*/


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

                for(int i=0;i<resultList.size();i++) {
                    System.out.print(resultList.get(i).get("ciYmd") + "의 재고는 ");
                    System.out.println(resultList.get(i).get("leaveCnt")+"입니다.");
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
                /*List< Map<String, Object> > resultList = (List<Map<String, Object>>) responseJson.get("resultList");

                for(int i=0;i<resultList.size();i++) {
                    System.out.print(resultList.get(i).get("ciYmd") + "의 원가는 ");
                    System.out.println(resultList.get(i).get("orgRmAmt")+"입니다.");
                }*/


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
}
