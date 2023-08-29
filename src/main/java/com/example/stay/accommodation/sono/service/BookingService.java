package com.example.stay.accommodation.sono.service;

import com.example.stay.accommodation.sono.mapper.BookingMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webservices.client.WebServiceTemplateBuilder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
//import javax.xml.ws.WebServiceClient;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service("sono.BookingService")
public class BookingService {

    @Autowired
    private BookingMapper bookingMapper;

    
    CommonFunction commonFunction = new CommonFunction();

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    //패키지 목록 조회
    public String getPackageList(HttpServletRequest httpServletRequest) {
        long startTime = System.currentTimeMillis();
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String statusCode ="";
        String msg ="";
        String result = "";
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
                return commonFunction.makeReturn("jsonp", statusCode, msg, responseJson);
            } else {
                return commonFunction.makeReturn("jsonp", String.valueOf(response.code()), response.message());
            }

        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage(), result);
        }

    }
    //패키지 상세 조회
    public String getPackageInfo(String pkgNo, HttpServletRequest httpServletRequest) {
        long startTime = System.currentTimeMillis();
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String statusCode ="";
        String msg ="";
        String result = "";

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
            //response 파싱
            String responseBody = response.body().string();
            JSONParser jsonParser = new JSONParser();
            JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

            if(response.isSuccessful()) {
                System.out.println(responseJson);

                return commonFunction.makeReturn("jsonp", "","", responseJson);
            } else {
                return commonFunction.makeReturn("jsonp", String.valueOf(response.code()), response.message(), responseJson);
            }

        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage(), result);
        }

    }
    //패키지 현황 조회
    public String getPackageStatus(String pkgNo, String storeCd, String sDate, String rmTypeCd, String ciYmd, HttpServletRequest httpServletRequest) {
        long startTime = System.currentTimeMillis();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100,TimeUnit.SECONDS)
                .writeTimeout(100,TimeUnit.SECONDS).build();

        String statusCode ="";
        String msg ="";
        String result = "";
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

                //재고 Update
                /*
                재고는 STOCK_REAL 테이블에서 관리
                RMTYPE테이블에 등록된 ID로 rmIdx 조회...?
                재고, 원가, 판매가
                 */

                return commonFunction.makeReturn("jsonp", "","", responseJson);
            } else {
                //예약 실패시
                //response 파싱
                String responseBody = response.body().string();
                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                return commonFunction.makeReturn("jsonp", "","", responseJson);
            }

        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");
        }
        return commonFunction.makeReturn("jsonp", statusCode, msg, result);
    }
    //패키지 요금 조회
    public String getPackageAmount(String pkgNo, String storeCd, String sDate, String rmTypeCd, String ciYmd, String nights, String rmCnt, HttpServletRequest httpServletRequest) {
        long startTime = System.currentTimeMillis();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100,TimeUnit.SECONDS)
                .writeTimeout(100,TimeUnit.SECONDS).build();

        String statusCode ="";
        String msg ="";
        String result = "";
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
            //response 파싱
            String responseBody = response.body().string();
            JSONParser jsonParser = new JSONParser();
            JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);


            if(response.isSuccessful() && responseJson.get("resultMsg").equals("SUCCESS")) {

                List<Map<String, Object>> resultList = (List<Map<String, Object>>) responseJson.get("resultList");
                for (int i = 0 ; i < resultList.size();i++) {
//                    String reStoreCd = resultList.get(i).get("storeCd").toString();
//                    String rmTypeNm = resultList.get(i).get("rmTypeNm").toString();
//                    String orgRmAmt = resultList.get(i).get("orgRmAmt").toString();
//                    String saleRmAmt = resultList.get(i).get("saleRmAmt").toString();
                }

                return commonFunction.makeReturn("jsonp", statusCode, msg, responseJson);



            } else {
                statusCode = String.valueOf(response.code());
                msg = (String) responseJson.get("error");
                return commonFunction.makeReturn("jsonp", statusCode,msg, responseJson);
            }

        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");
            return commonFunction.makeReturn("jsonp", "500", e.getMessage(), result);

        }


    }
    //예약
    public String reservation(String pkgNo, String storeCd, String ciYmd, String rmTypeCd, String comRsvNo, String userName, String userTel, String payAmt, String adultCnt, String childCnt ,HttpServletRequest httpServletRequest) {
        long startTime = System.currentTimeMillis();
        OkHttpClient client = new OkHttpClient().newBuilder().build();


        String statusCode ="";
        String msg ="";
        String result = "";

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


                return commonFunction.makeReturn("jsonp", "","", responseJson);
            }

        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");
            return commonFunction.makeReturn("jsonp", "500", e.getMessage(), result);
        }

        return commonFunction.makeReturn("jsonp", statusCode, msg, result);

    }
    //영업장 목록조회
    public String getRoomList(HttpServletRequest httpServletRequest) {
        long startTime = System.currentTimeMillis();
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String statusCode ="";
        String msg ="";
        String result = "";
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

                return commonFunction.makeReturn("jsonp", "","", responseJson);

            }

        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");
            return commonFunction.makeReturn("jsonp", "500", e.getMessage(), result);
        }

        return commonFunction.makeReturn("jsonp", statusCode, msg, result);

    }

    //객실 요금 조회
    public String getRoomAmount(HttpServletRequest httpServletRequest, String storeCd, String sMonth) {
        long startTime = System.currentTimeMillis();
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String statusCode ="";
        String msg ="";
        String result = "";
        String pkgData = "";

        JSONObject requestJson = new JSONObject();
        requestJson.put("businessId", Constants.sonoRoomId);
        requestJson.put("language", Constants.sonoLanguage);
        requestJson.put("storeCd", storeCd);
        requestJson.put("sMonth", sMonth);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.sonoRoomPath + "/amountList01")
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.sonoRoomAuth)
                .addHeader("Content-Type", "application/json")
                .build();

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);

        try {
            Response response = client.newCall(request).execute();
            //response 파싱
            String responseBody = response.body().string();
            JSONParser jsonParser = new JSONParser();
            JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);


            if(response.isSuccessful() && responseJson.get("resultMsg").equals("SUCCESS")) {

                List<Map<String, Object>> resultList = (List<Map<String, Object>>) responseJson.get("resultList");
                return commonFunction.makeReturn("jsonp", statusCode, msg, responseJson);


            }
            return commonFunction.makeReturn("jsonp", statusCode, responseJson.get("resultMsg").toString(), responseJson);

        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");
            return commonFunction.makeReturn("jsonp", "500", e.getMessage(), result);
        }

    }

    //객실 현황 조회
    public String getRoomStatus(HttpServletRequest httpServletRequest, String storeCd, String sDate) {
        long startTime = System.currentTimeMillis();
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String statusCode ="";
        String msg ="";
        String result = "";
        String pkgData = "";

        JSONObject requestJson = new JSONObject();
        requestJson.put("businessId", Constants.sonoRoomId);
        requestJson.put("language", Constants.sonoLanguage);
        requestJson.put("storeCd", storeCd);
        requestJson.put("sDate", sDate);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.sonoRoomPath + "/statusList01")
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
                return commonFunction.makeReturn("jsonp", statusCode, msg, responseJson);


            } else {

                //response 파싱
                String responseBody = response.body().string();
                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                List<Map<String, Object>> resultList = (List<Map<String, Object>>) responseJson.get("resultList");
                return commonFunction.makeReturn("jsonp", statusCode, msg, responseJson);
            }

        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }

        //return commonFunction.makeReturn("jsonp", statusCode, msg, result);

    }

    //TO-DO 재고현황과 요금 가져와서 하나의 데이터로 조립해줘야 함
    public String getStockAndInsert(HttpServletRequest httpServletRequest) {

        //패키지의 경우 패키지번호, 영업장코드, 조회시작일자로 (가장 많은 데이터 추출하기위함)
        //룸온리의 경우 영업장코드, 조회시작일자
        //패키지 리스트에서 패키지번호, 영업장번호 빼오고(strPkgCode, strStoreCode)
        //조회 시작일자는 오늘부터 하면 될듯!
        String pkgNo = "";
        String storeCd = "";
        String strType = "01";
        String packageStockDatas = "";
        Date nowDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String sDate = dateFormat.format(nowDate);
        List<Map<String, Object>> stockList = new ArrayList<>();
        JSONObject stockResultJson = new JSONObject();

        List<Map<String, Object>> pkgcdAndStorecd = bookingMapper.getPackageCodeAndStoreCode("01");

        //ResponseResult roomStatus = getRoomStatus(httpServletRequest, storeCd, sDate);
        //ResponseResult roomAmount = getRoomAmount(httpServletRequest, storeCd, sDate);

        try {
            for (int i = 0 ; i < pkgcdAndStorecd.size() ; i++){
                int oldStockListsize = stockList.size();
                pkgNo = (String) pkgcdAndStorecd.get(i).get("strPkgCode");
                storeCd = (String) pkgcdAndStorecd.get(i).get("strStoreCode");
                String packStatus = getPackageStatus(pkgNo, storeCd, sDate, null, null, httpServletRequest);
                String packAmount = getPackageAmount(pkgNo, storeCd, sDate, null, null, "1", "1", httpServletRequest);
                JSONParser jsonParser = new JSONParser();
                JSONObject packStatusJson = (JSONObject) jsonParser.parse(packStatus.substring(5, packStatus.length()-1));
                JSONObject packAmountJson = (JSONObject) jsonParser.parse(packAmount.substring(5, packAmount.length()-1));
                if(!packStatusJson.get("code").equals("200") || !packAmountJson.get("code").equals("200")) {
                    System.out.println("재고조회 메시지 : " + packStatusJson.get("result").toString() + " 가격조회 메시지 : " + packAmountJson.get("result").toString());
                    System.out.println(i);
                    continue;
                }
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
                    String rmTypeCd = (String) packStatusList.get(j).get("rmTypeCd");
                    String intStock = packStatusList.get(j).get("leaveCnt").toString();
                    String moneyCost = "";
                    String moneySales = "";
                    for (int k = 0 ; k < packAmountList.size() ; k++) {
                        if (packAmountList.get(k).get("rmTypeCd").equals(rmTypeCd) && packAmountList.get(k).get("ciYmd").equals(dateSales)) {
                            moneyCost = String.valueOf(packAmountList.get(k).get("orgRmAmt"));
                            moneySales = String.valueOf(packAmountList.get(k).get("saleRmAmt"));
                            break;
                        }
                    }
                    packageStockDatas += dateSales + "|^|" + intStock + "|^|" + moneyCost + "|^|" + moneySales + "|^|" + pkgNo + "|^|" + rmTypeCd;
                    if (j != packStatusList.size()-1) {
                        packageStockDatas += "{{^}}";
                    }
                }
                packageStockDatas += "{{|}}";

            }
            if(packageStockDatas.length() > 5) packageStockDatas = packageStockDatas.substring(0, packageStockDatas.length()-5);

//            stockResultJson.put("resultList", stockList);

//            System.out.println(packageStockDatas);
//            System.out.println(packStatusList);
            System.out.println(packageStockDatas);
//            String insertResult = bookingMapper.insertRoom("", "", packageStockDatas, "", strType);
//            stockResultJson.put("insertResult", insertResult);
            return commonFunction.makeReturn("jsonp", "", "", stockResultJson);

        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }
    }

    public String getSettlement(String stndDt){
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String statusCode ="";
        String msg ="";
        String result = "";
        String pkgData = "";

        JSONObject requestJson = new JSONObject();
        requestJson.put("businessId", Constants.sonoPackId);
        requestJson.put("language", Constants.sonoLanguage);
        requestJson.put("type", "S");
        requestJson.put("stndDt", stndDt);
        String contents = requestJson.toJSONString();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, contents);

        Request request = new Request.Builder()
                .url(Constants.sonoPackPath + "/settlementNopkgNo")
                .method("POST", body)
                .addHeader("X-AUTH-TOKEN", Constants.sonoPackAuth)
                .addHeader("Content-Type", "application/json")
                .build();

//        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), startTime);

        try {
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {
                //response 파싱
                String responseBody = response.body().string();
                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                List<Map<String, Object>> resultList = (List<Map<String, Object>>) responseJson.get("resultList");
                return commonFunction.makeReturn("jsonp", statusCode, msg, responseJson);


            } else {

                //response 파싱
                String responseBody = response.body().string();
                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                List<Map<String, Object>> resultList = (List<Map<String, Object>>) responseJson.get("resultList");
                return commonFunction.makeReturn("jsonp", statusCode, msg, responseJson);
            }

        } catch (Exception e) {
            System.out.println("e ::: 에러 출력! == " + e);
            System.out.println(e.getMessage());
            System.out.println("responseJson ::: 에러 출력!");
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }
    }


    //insert
    //패키지, 룸, 시설정보
    public String insertSONO(HttpServletRequest httpServletRequest) {
        String statusCode = "";
        String msg = "";
        String result = "";

        String packageResponseResult = getPackageList(httpServletRequest);
        String RoomResponseResult = getRoomList(httpServletRequest);



        JSONParser jsonParser = new JSONParser();
        try {
            String pkgData = "";
            String roomData = "";

            String accommData = "";
            String strType = "01";
            String propertyID = "";

            JSONObject packageResponseJson = (JSONObject) jsonParser.parse(packageResponseResult.substring(5, packageResponseResult.length()-1));
            JSONObject roomResponseJson = (JSONObject) jsonParser.parse(RoomResponseResult.substring(5, RoomResponseResult.length()-1));
            roomResponseJson = (JSONObject) roomResponseJson.get("result");
            packageResponseJson = (JSONObject) packageResponseJson.get("result");
            JSONObject resultResponseJson = new JSONObject();
            List<Map<String, Object>> packageResultList = (List<Map<String, Object>>) packageResponseJson.get("resultList");
            List<Map<String, Object>> roomResultList = (List<Map<String, Object>>) roomResponseJson.get("resultList");

            for (int i = 0 ; i<roomResultList.size() ; i++) {

                String storeCd = (String) roomResultList.get(i).get("storeCd");
                String storeNm = (String) roomResultList.get(i).get("storeNm");
                String lcalCd = (String) roomResultList.get(i).get("lcalCd");
                String lcalNm = (String) roomResultList.get(i).get("lcalNm");
                String saleStartDt = (String) roomResultList.get(i).get("saleBgnYmd");
                String saleEndDT = (String) roomResultList.get(i).get("saleEndYmd");
                String curRsvYN = (String) roomResultList.get(i).get("curRsvYN");
                String curRsvTime = (String) roomResultList.get(i).get("todayRsvTime");
                String stayNights = String.valueOf(roomResultList.get(i).get("stayNights"));
                if(curRsvTime.length() == 4){curRsvTime = curRsvTime.substring(0, 2) + ":" + curRsvTime.substring(2, 4);}

                //roomData = 삭제여부 |^| 사용여부 |^| 기준인원 |^| 최대인원 |^| 룸데이터 |^| 최소숙박 |^| 최대숙박일 |^| 조식 |^| depth |^| 환불여부

                roomData += "N" + "|^|" + "Y" + "|^|" + "1" + "|^|" + "99" + "|^|" + lcalCd + "|^|" ;


                List<Map<String, Object>> roomList = (List<Map<String, Object>>) roomResultList.get(i).get("roomTypeList");

                for (int j = 0 ; j<roomList.size() ; j++) {
                    String rmTypeCd = (String) roomList.get(j).get("rmTypeCd");
                    String rmTypeNm = (String) roomList.get(j).get("rmTypeNm");

                    if ( j != roomList.size()-1){
                        roomData += "|~|" + rmTypeCd + "|~|" + rmTypeNm + "{{^}}";
                    } else {
                        roomData += "|~|" + rmTypeCd + "|~|" + rmTypeNm + "|^|";
                    }
                }
                if (i != roomResultList.size()-1){
                    roomData += "1" + "|^|" + stayNights + "|^|" + "|^|" + "1" + "|^|" + "|^|" + "{{|}}";
                } else {
                    roomData += "1" + "|^|" + stayNights + "|^|" + "|^|" + "1" + "|^|" + "|^|";
                }

            }

            for (int i = 0 ; i<packageResultList.size() ; i++) {
                    /*
                    pkgNo, saleStartDt, curRsvYN, curRsvTime, nights, rmCnt, pkgNm, todaySaleYn, lcalNm, saleEndDT, lcalCd
                     */
                String pkgNo = (String) packageResultList.get(i).get("pkgNo");
                String pkgNm = (String) packageResultList.get(i).get("pkgNm");
                String lcalCd = (String) packageResultList.get(i).get("lcalCd");
                String lcalNm = (String) packageResultList.get(i).get("lcalNm");
                String saleStartDt = (String) packageResultList.get(i).get("saleStartDt");
                String saleEndDT = (String) packageResultList.get(i).get("saleEndDt");
                String curRsvYN = (String) packageResultList.get(i).get("curRsvYN");
                String curRsvTime = (String) packageResultList.get(i).get("curRsvTime");
                String nights = (String) packageResultList.get(i).get("nights");
                if(curRsvTime.length() == 4){curRsvTime = curRsvTime.substring(0, 2) + ":" + curRsvTime.substring(2, 4);}

                //pkgData = 패키지구분(소노호텔앤리조트:01)|^|패키지번호|^|패키지명|^|지역코드|^|지역명|^|판매시작일자|^|판매종료일자|^|즉시판매여부|^|예약가능시간|^|박수|^|최대예약가능객실수|^|roomList
                pkgData += "01" + "|^|" + pkgNo + "|^|" + pkgNm + "|^|" + lcalCd + "|^|" + lcalNm + "|^|" + saleStartDt+ "|^|" + saleEndDT + "|^|"
                        + curRsvYN + "|^|" + curRsvTime + "|^|" + nights + "|^|"  + "|^|";

                //roomData = 삭제여부 |^| 사용여부 |^| 기준인원 |^| 최대인원 |^| 룸데이터 |^| 최소숙박 |^| 최대숙박일 |^| 조식 |^| depth |^| 환불여부

                //패키지 조회시 숙박가능한 객실들을 RMTYPE에 인입시키되 depth:2 로 인입시켜 룸온리가아닌 패키지를 넣도록
//                roomData += "N" + "|^|" + "Y" + "|^|" + "1" + "|^|" + "99" + "|^|" ;

                List<Map<String, Object>> pkgRoomList = (List<Map<String, Object>>) packageResultList.get(i).get("roomList");

                for (int j = 0 ; j<pkgRoomList.size() ; j++) {
                    String storeCd = pkgRoomList.get(j).get("storeCd").toString();
                    String storeNm = pkgRoomList.get(j).get("storeNm").toString();
                    String rmTypeCd = pkgRoomList.get(j).get("rmTypeCd").toString();
                    String rmTypeNm = pkgRoomList.get(j).get("rmTypeNm").toString();
                    accommData += "C" + "|^|" + "01" + "|^|" + lcalCd + "|^|" + lcalNm.trim();
                    accommData += "{{|}}";
//                    roomData += storeCd + "|^|";

                    if ( j != pkgRoomList.size()-1){
                        pkgData += storeCd + "|~|" + storeNm.trim() + "{{^}}";
//                        roomData += pkgNo + "|~|" + rmTypeCd + "|~|" + rmTypeNm + "{{^}}";
                    } else {
                        pkgData += storeCd + "|~|" + storeNm.trim();
//                        roomData += pkgNo + "|~|" + rmTypeCd + "|~|" + rmTypeNm + "|^|";
                    }
                }
                if (i != packageResultList.size()-1){
                    pkgData += "{{|}}";
//                    roomData += "1" + "|^|" +"99" + "|^|" + "" + "|^|" + "2" + "|^|" + "" + "|^|" + pkgNm.trim() + "{{|}}";
                } else {
//                    roomData += "1" + "|^|" +"99" + "|^|" + "" + "|^|" + "2" + "|^|" + "" + "|^|" + pkgNm.trim();
                    accommData = accommData.substring(0, accommData.length()-5);
                }

            }




            System.out.println(pkgData);
//            System.out.println(roomData);
//            System.out.println(accommData);

//            String insertResult = bookingMapper.insertRoom(pkgData, "", "", "", strType);
            String insertResult = bookingMapper.insertRoom(pkgData, roomData, "", accommData, strType);
            System.out.println(insertResult);
            result = "SUCCESS";
            resultResponseJson.put("insertResult", insertResult);


            return commonFunction.makeReturn("jsonp", statusCode, msg, resultResponseJson);

        } catch (Exception e) {

            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }




        //result = bookingMapper.insertRoom("","","", storeCD, "01");



    }

}
