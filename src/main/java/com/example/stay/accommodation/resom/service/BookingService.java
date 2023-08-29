package com.example.stay.accommodation.resom.service;

import com.example.stay.accommodation.resom.mapper.BookingMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.common.util.XmlUtility;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service("resom.BookingService")
public class BookingService {

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private XmlUtility xmlUtility;

    CommonFunction commonFunction = new CommonFunction();

    //패키지 목록 조회
    public String getPackageList() {
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
                    pkgData += "RESOM" + "|^|" + pkgNo + "|^|" + pkgNm + "|^|" + "|^|" + "|^|" + saleStartDt + "|^|" +
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


                return  commonFunction.makeReturn("jsonp","", "", responseJson);
            } else {
                return  commonFunction.makeReturn("jsonp",String.valueOf(response.code()), response.message());
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","500", e.getMessage());

        }


    }

    //영업장 목록 조회
    public String getStoreList() {
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

                return  commonFunction.makeReturn("jsonp","","", responseJson);
            } else {
                return  commonFunction.makeReturn("jsonp",String.valueOf(response.code()), response.message());
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","500", e.getMessage());

        }


    }

    //패키지 상세목록 조회
    public String getPackageInfo(String pkgNo) {
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

                return  commonFunction.makeReturn("jsonp","","", responseJson);
            } else {
                return  commonFunction.makeReturn("jsonp",String.valueOf(response.code()), response.message());
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","500", e.getMessage());

        }


    }

    //패키지 현황 조회 (영업장별)
    public String getPackageStatus(String pkgNo, String storeCd, String sDate) {
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

                return  commonFunction.makeReturn("jsonp","","", responseJson);
            } else {
                return  commonFunction.makeReturn("jsonp",String.valueOf(response.code()), response.message());
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","500", e.getMessage());

        }


    }

    //패키지 현황 조회 (영업장, 객실유형별)
    public String getPackageStatus(String pkgNo, String storeCd, String sDate, String rmTypeCd) {
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

                return  commonFunction.makeReturn("jsonp","","", responseJson);
            } else {
                return  commonFunction.makeReturn("jsonp",String.valueOf(response.code()), response.message());
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","500", e.getMessage());

        }


    }

    //패키지 현황 조회 (영업장, 객실유형, 이용일자별)
    public String getPackageStatus(String pkgNo, String storeCd, String rmTypeCd, String sDate, String nights) {
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

                return  commonFunction.makeReturn("jsonp","","", responseJson);
            } else {
                return  commonFunction.makeReturn("jsonp",String.valueOf(response.code()), response.message());
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","500", e.getMessage());

        }


    }

    //패키지 현황 조회 (영업장별 월 시작과 종료)
    public String getPackageStatusMonth(String pkgNo, String storeCd, String sDate, String nights) {
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

                return  commonFunction.makeReturn("jsonp","","", responseJson);
            } else {
                return  commonFunction.makeReturn("jsonp",String.valueOf(response.code()), response.message());
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","500", e.getMessage());

        }


    }

    //패키지 요금 조회 (영업장별)
    public String getPackageAmount(String pkgNo, String storeCd, String sDate) {
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

                return  commonFunction.makeReturn("jsonp","","", responseJson);
            } else {
                return  commonFunction.makeReturn("jsonp",String.valueOf(response.code()), response.message());
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","500", e.getMessage());
        }

    }

    //패키지 요금 조회 (영업장, 객실유형별)
    public String getPackageAmount(String pkgNo, String storeCd, String sDate, String rmTypeCd) {
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


                    return commonFunction.makeReturn("jsonp","", "", responseJson);
                }
                List< Map<String, Object> > resultList = (List<Map<String, Object>>) responseJson.get("resultList");

                for(int i=0;i<resultList.size();i++) {
                    System.out.print(resultList.get(i).get("ciYmd") + "의 원가는 ");
                    System.out.println(resultList.get(i).get("orgRmAmt")+"입니다.");
                }


                return  commonFunction.makeReturn("jsonp","","", responseJson);
            } else {
                return  commonFunction.makeReturn("jsonp",String.valueOf(response.code()), response.message());
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","500", e.getMessage());
        }

    }

    //패키지 요금 조회 (영업장, 객실유형, 이용일자별)
    public String getPackageAmount(String pkgNo, String storeCd, String sDate, String rmTypeCd, String nights) {
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

                return  commonFunction.makeReturn("jsonp","","", responseJson);
            } else {
                return  commonFunction.makeReturn("jsonp",String.valueOf(response.code()), response.message());
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","500", e.getMessage());
        }

    }

    //패키지 예약
    public String createBooking(String pkgNo, String storeCd, String ciYmd, String rmTypeCd, String comRsvNo, String userName, String userTel, String payAmt, String adultCnt, String childCnt, String channelCd, String channelNm) {
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
        // API 호출말고 DB select로 구현할것
        String roomCount = getPackageStatus(pkgNo, storeCd, ciYmd, rmTypeCd);

        String statusCount = getPackageStatus(pkgNo, storeCd, ciYmd);

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
                    return  commonFunction.makeReturn("jsonp","","", responseJson);
                    //TO-DO 예약성공시 아래에 DB update 로직 추가
                } else {
                    //예약 실패시
                    return  commonFunction.makeReturn("jsonp","","", responseJson);
                }
            } else {
                return  commonFunction.makeReturn("jsonp",String.valueOf(response.code()), response.message());
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","500", e.getMessage());

        }

    }

    //패키지 연박예약
    public String createBooking(String pkgNo, String storeCd, String ciYmd, String rmTypeCd, String comRsvNo, String userName, String userTel, String payAmt, String adultCnt, String childCnt, String channelCd, String channelNm, String nights, String rmCnt) {
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

                return  commonFunction.makeReturn("jsonp","","", responseJson);
            } else {
                return  commonFunction.makeReturn("jsonp",String.valueOf(response.code()), response.message());
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","500", e.getMessage());
        }

    }

    //예약 취소
    public String cancelBooking(String roomRsvNo, String pkgSaleSeq, String roomRsvSeq, String comRsvNo) {
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

                return  commonFunction.makeReturn("jsonp","","", responseJson);

            } else {
                return  commonFunction.makeReturn("jsonp",String.valueOf(response.code()), response.message());
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","500", e.getMessage());

        }

    }

    //예약 대사
    public String reservationList(String stndDt) {
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
                    return  commonFunction.makeReturn("jsonp","","", responseJson);

                } else {

                }

                return  commonFunction.makeReturn("jsonp","","", responseJson);

            } else {
                //response 파싱
                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                System.out.println(responseJson);
                return  commonFunction.makeReturn("jsonp",String.valueOf(response.code()), response.message());

            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","500", e.getMessage());

        }

    }

    //이용자 정보 변경
    public String updateGuest(String roomRsvSeq, String pkgSaleSeq, String guestNm, String mpNo) {
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

                return  commonFunction.makeReturn("jsonp","","", responseJson);

            } else {
                return  commonFunction.makeReturn("jsonp", String.valueOf(response.code()), response.message());
            }
        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");
            return commonFunction.makeReturn("jsonp","500", e.getMessage());

        }


    }

    //패키지 예약조회
    public String getPackageBookingInfo(String ciYmd, String roomRsvNo, String guestNm, String mpNo) {
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

                return  commonFunction.makeReturn("jsonp","","", responseJson);

            } else {

                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                System.out.println(responseJson);
                return  commonFunction.makeReturn("jsonp", String.valueOf(response.code()), response.message());
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","500", e.getMessage());

        }

    }

    //패키지 예약취소 (고객정보)
    public String cancelPackage(String ciYmd, String roomRsvNo, String guestNm, String mpNo) {
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
                //TO-DO 이용자정보 변경 성공시 예약테이블 수정해야함

                return  commonFunction.makeReturn("jsonp","","", responseJson);

            } else {
                return  commonFunction.makeReturn("jsonp", String.valueOf(response.code()), response.message());
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","500", e.getMessage());

        }

    }

    //TO-DO 재고현황과 요금 가져와서 하나의 데이터로 조립해줘야 함
    public String getStockAndInsert(HttpServletRequest httpServletRequest) {

        //패키지의 경우 패키지번호, 영업장코드, 조회시작일자로 (가장 많은 데이터 추출하기위함)
        //패키지 리스트에서 패키지번호, 영업장번호 빼오고(strPkgCode, strStoreCode)
        //조회 시작일자는 오늘부터!
        String pkgNo = "";
        String storeCd = "";
        String strType = "01";
        String packageStockDatas = "";
        Date nowDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String sDate = dateFormat.format(nowDate);
        List<Map<String, Object>> stockList = new ArrayList<>();
        JSONObject stockResultJson = new JSONObject();

        List<Map<String, Object>> pkgcdAndStorecd = (List<Map<String, Object>>) bookingMapper.getPackageCodeAndStoreCode("01");

        try {
            for (int i = 0 ; i < pkgcdAndStorecd.size() ; i++){
                int oldStockListsize = stockList.size();
                pkgNo = (String) pkgcdAndStorecd.get(i).get("strPkgCode");
                storeCd = (String) pkgcdAndStorecd.get(i).get("strStoreCode");
                String packStatus = getPackageStatus(pkgNo, storeCd, sDate);
                String packAmount = getPackageAmount(pkgNo, storeCd, sDate);
                JSONParser jsonParser = new JSONParser();
                JSONObject packStatusJson = (JSONObject) jsonParser.parse(packStatus.substring(5, packStatus.length()-1));
                JSONObject packAmountJson = (JSONObject) jsonParser.parse(packAmount.substring(5, packAmount.length()-1));
                packStatusJson = (JSONObject) packStatusJson.get("result");
                packAmountJson = (JSONObject) packAmountJson.get("result");
                List<Map<String, Object>> packStatusList = (List<Map<String, Object>>) packStatusJson.get("resultList");
                List<Map<String, Object>> packAmountList = (List<Map<String, Object>>) packAmountJson.get("resultList");

                if (packStatusList==null || packAmountList==null) {
                    continue;
                }
                System.out.println(i + "번째 재고 :: " + packStatusList.size());
                System.out.println(i + "번째 가격 :: " + packAmountList.size());


                for (int j = 0 ; j < packStatusList.size() ; j++) {
                    String dateSales = (String) packStatusList.get(j).get("ciYmd");
                    String intStock = packStatusList.get(j).get("leaveCnt").toString();
                    String moneyCost = String.valueOf(packAmountList.get(j).get("orgRmAmt"));
                    String moneySales = String.valueOf(packAmountList.get(j).get("orgRmAmt")); // TO-DO 리솜은 원가에서 13%정도 붙인다고함 관리자단에서 수정할지 여기서 수정할지는 차후
                    String rmTypeCd = String.valueOf(packStatusList.get(j).get("rmTypeCd"));
                    packageStockDatas += dateSales + "|^|" + intStock + "|^|" + moneyCost + "|^|" + moneySales + "|^|" + pkgNo + "|^|" + rmTypeCd;
                    if (j != packStatusList.size()-1) {
                        packageStockDatas += "{{^}}";
                    }
                }
                packageStockDatas += "{{|}}";

            }
            packageStockDatas = packageStockDatas.substring(0, packageStockDatas.length()-5);


            System.out.println(packageStockDatas);
//            String insertResult = bookingMapper.insertRoom("", "", packageStockDatas, "", strType);
//            stockResultJson.put("insertResult", insertResult);
            return commonFunction.makeReturn("jsonp","", "", stockResultJson);

        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","500", e.getMessage());
        }
    }

    //insert
    public String insertRESOM(HttpServletRequest httpServletRequest) {
        String result = "";

        String packageResponseResult = getPackageList();
        String RoomResponseResult = getStoreList();
        JSONObject resultResponseJson = new JSONObject();

        JSONParser jsonParser = new JSONParser();
        try {
            String pkgData = "";
            String roomData = "";

            String accommData = "";
            String strType = "RE";

            JSONObject packageResponseJson = (JSONObject) jsonParser.parse(packageResponseResult.substring(5, packageResponseResult.length()-1));
            JSONObject roomResponseJson = (JSONObject) jsonParser.parse(RoomResponseResult.substring(5, RoomResponseResult.length()-1));
            roomResponseJson = (JSONObject) roomResponseJson.get("result");
            packageResponseJson = (JSONObject) packageResponseJson.get("result");
            List<Map<String, Object>> packageResultList = (List<Map<String, Object>>) packageResponseJson.get("resultList");
            List<Map<String, Object>> roomResultList = (List<Map<String, Object>>) roomResponseJson.get("storeList");

            for (int i = 0 ; i<roomResultList.size() ; i++) {
                String rmTypeCd = (String) roomResultList.get(i).get("rmTypeCd");
                String rmTypeNm = (String) roomResultList.get(i).get("rmTypeNm");
                String rmUseYn = (String) roomResultList.get(i).get("rmUseYn");
                String storeCd = (String) roomResultList.get(i).get("storeCd");

                //roomData = 삭제여부 |^| 사용여부 |^| 기준인원 |^| 최대인원 |^| 룸데이터 |^| 최소숙박 |^| 최대숙박일 |^| 조식 |^| depth |^| 환불여부

                roomData += "N" + "|^|" + rmUseYn + "|^|" + "1" + "|^|" + "99" + "|^|";
                roomData += rmTypeNm + "|^|" + "|^|" + rmTypeCd + "|^|" + "|^|";

                roomData += "1" + "|^|" +"99" + "|^|" + "|^|" + "1" + "|^|" + "|^|" + storeCd + "{{|}}";


            }

            for (int i = 0 ; i<packageResultList.size() ; i++) {

                String pkgNo = (String) packageResultList.get(i).get("pkgNo");
                String pkgNm = (String) packageResultList.get(i).get("pkgNm");
                String saleStartDt = (String) packageResultList.get(i).get("saleStartDt");
                String saleEndDT = (String) packageResultList.get(i).get("saleEndDt");
                String curRsvYN = (String) packageResultList.get(i).get("curRsvYN");
                String curRsvTime = (String) packageResultList.get(i).get("curRsvTime");
                String nights = (String) packageResultList.get(i).get("nights");
                String maxNights = (String) packageResultList.get(i).get("maxNights");
                String maxRmCnt = (String) packageResultList.get(i).get("maxRmCnt");

                //pkgData = 패키지구분(리솜 없음)|^|패키지번호|^|패키지명|^|지역코드|^|지역명|^|판매시작일자|^|판매종료일자|^|즉시판매여부|^|예약가능시간|^|박수|^|최대예약가능객실수|^|roomList
                pkgData += "RE" + "|^|" + pkgNo + "|^|" + pkgNm + "|^|" + "|^|" + "|^|" + saleStartDt+ "|^|" + saleEndDT + "|^|"
                        + curRsvYN + "|^|" + curRsvTime + "|^|" + nights + "|^|"  + maxRmCnt + "|^|";

                //roomData = 삭제여부 |^| 사용여부 |^| 기준인원 |^| 최대인원 |^| 룸데이터 |^| 최소숙박 |^| 최대숙박일 |^| 조식 |^| depth |^| 환불여부



                List<Map<String, Object>> pkgRoomList = (List<Map<String, Object>>) packageResultList.get(i).get("roomList");

                String storeCd = "";
                for (int j = 0 ; j<pkgRoomList.size() ; j++) {
                    storeCd = pkgRoomList.get(j).get("storeCd").toString();
                    String storeNm = pkgRoomList.get(j).get("storeNm").toString();
                    String rmTypeCd = pkgRoomList.get(j).get("rmTypeCd").toString();
                    String rmTypeNm = pkgRoomList.get(j).get("rmTypeNm").toString();

                    accommData += "C" + "|^|" + "RE" + "|^|" + storeCd + "|^|" + storeNm.trim();
                    accommData += "{{|}}";
                    roomData += "N" + "|^|" + "Y" + "|^|" + "1" + "|^|" + "99" + "|^|";
                    roomData += rmTypeNm + "|^|" + "|^|" + rmTypeCd + "|^|" + pkgNo + "|^|";
                    roomData += nights + "|^|" + maxNights + "|^|" + "|^|" + "2" + "|^|" + "|^|" + storeCd + "{{|}}";

                    if ( j != pkgRoomList.size()-1){
                        pkgData += storeCd + "|~|" + storeNm + "{{^}}";
                    } else {
                        pkgData += storeCd + "|~|" + storeNm;
                    }
                }

                if (i != packageResultList.size()-1){
                    pkgData += "{{|}}";
                } else {
                    accommData += accommData.substring(0, accommData.length()-5);
                }

            }
            roomData = roomData.substring(0, roomData.length()-5);

            String insertResult = bookingMapper.insertRoom(pkgData, roomData, "", accommData, strType);
//            String insertResult = bookingMapper.insertRoom(pkgData, "", "", accommData, strType);
//            System.out.println(insertResult);
//            System.out.println(pkgData);
//            System.out.println(accommData);
            System.out.println(roomData);
            result = insertResult;
            resultResponseJson.put("insertResult", result);
            return commonFunction.makeReturn("jsonp","", "", resultResponseJson);
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","500", e.getMessage());

        }


    }
}
