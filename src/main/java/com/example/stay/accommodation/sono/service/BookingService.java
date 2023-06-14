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
                System.out.println(responseJson);

                List<Map<String, Object>> resultList = (List<Map<String, Object>>) responseJson.get("resultList");
                for (int i = 0 ; i<resultList.size() ; i++) {
                    /*
                    pkgNo, saleStartDt, curRsvYN, curRsvTime, nights, rmCnt, pkgNm, todaySaleYn, lcalNm, saleEndDT, lcalCd
                     */
                    String pkgNo = resultList.get(i).get("pkgNo").toString();
                    String pkgNm = resultList.get(i).get("pkgNm").toString();
                    String lcalCd = resultList.get(i).get("lcalCd").toString();
                    String lcalNm = resultList.get(i).get("lcalNm").toString();
                    String saleStartDt = resultList.get(i).get("saleStartDt").toString();
                    String saleEndDT = resultList.get(i).get("saleEndDT").toString();
                    String curRsvYN = resultList.get(i).get("curRsvYN").toString();
                    String curRsvTime = resultList.get(i).get("curRsvTime").toString();
                    String nights = resultList.get(i).get("nights").toString();

                    //pkgData = 패키지구분|^|패키지번호|^|패키지명|^|지역코드|^|지역명|^|판매시작일자|^|판매종료일자|^|즉시판매여부|^|예약가능시간|^|박수|^|최대예약가능객실수|^|roomList
                    pkgData += "SONO" + "|^|" + pkgNo + "|^|" + pkgNm + "|^|" + lcalCd + "|^|" + lcalNm + "|^|" + saleStartDt + "|^|" + saleEndDT + "|^|"
                            + curRsvYN + "|^|" + curRsvTime + "|^|" + nights + "|^|"  + "" + "|^|";

                    List<Map<String, Object>> roomList = (List<Map<String, Object>>) resultList.get(i).get("roomList");
                    for (int j = 0 ; j<roomList.size() ; j++) {
                        /*
                        storeCd, storeNm, rmTypeNm, rmTypeCd
                         */
                        String storeCd = roomList.get(j).get("storeCd").toString();
                        String storeNm = roomList.get(j).get("storeNm").toString();
                        pkgData += storeCd + "|^|" + storeNm + "|^|" + "{{^}}";
                    }
                    pkgData += "{{|}}";
                }

                return  new ResponseResult<>("","", responseJson);

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
                for(int i = 0;i<resultList.size();i++) {
                    //ACCOMM 에 들어갈 정보 처리
                    String storeNm = resultList.get(i).get("storeNm").toString();
                    String storeCd = resultList.get(i).get("storeCd").toString();
                    String maxNights = resultList.get(i).get("stayNights").toString();
                    String maxRoomCnt = resultList.get(i).get("rmCnt").toString();
                    List<Map<String, Object>> roomList = (List<Map<String, Object>>) resultList.get(i).get("roomTypeList");
                    for(int j = 0;j<roomList.size();j++) {
                        //RMTYPE에 들어갈 정보 처리 minsleep은 1로 고정되어있다
                        String rmTypeCd = roomList.get(j).get("rmTypeCd").toString();
                        String rmTypeNm = roomList.get(j).get("rmTypeNm").toString();

                        System.out.println("storeName :: " + storeNm);
                        System.out.println("storeCd :: " + storeCd);
                        System.out.println("maxNights :: " + maxNights);
                        System.out.println("maxRoomCnt :: " + maxRoomCnt);
                        System.out.println("rmTypeCd :: " + rmTypeCd);
                        System.out.println("rmTypeNm :: " + rmTypeNm);
                        System.out.println("");

                        /*
                        rmTypeCd => strRmtypeID
                        pkgNo => strRateplanID
                        rmTypeNm => strSubject
                        maxNights => intMaxSleep

                         */


                    }

                    //TO-DO RMTYPE테이블, ACCOMM테이블 인서트 작업
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

        return new ResponseResult<>(statusCode, msg, result);

    }

}
