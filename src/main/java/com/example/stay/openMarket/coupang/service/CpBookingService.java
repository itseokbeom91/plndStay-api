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

    // TODO : 기간제한 확인
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String startDate = sdf.format(simpleDateFormat.parse(strStartDate));
            String endDate = sdf.format(simpleDateFormat.parse(strEndDate));

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
        return commonFunction.makeReturn(dataType, statusCode, message, resultJson);
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
