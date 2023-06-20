package com.example.stay.accommodation.elysian_gangchon.service;

import com.example.stay.accommodation.elysian_gangchon.mapper.BookingMapper;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.common.util.ResponseResult;
import com.google.gson.JsonObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

@Service("elysian_gangchon.BookingService")
public class BookingService {

    @Autowired
    private BookingMapper bookingMapper;

    // 재고 등록 및 수정
    public String updateGoods(HttpServletRequest httpServletRequest, String pcode, String pcode_seq,
                                      String sdate, String edate, String strRmtypeID){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            String elysUrl = "type=SB&pcode=" + pcode + "&pcode_seq=" + pcode_seq + "&sdate=" + sdate + "&edate=" + edate;

            String strResponse = callElysAPI(elysUrl);

            int failCount = 0; // 재고 등록 및 수정에 실패한 경우 카운트
            String[] responseArr = strResponse.split("#");
            for(String arr : responseArr){
                String[] dataArr = arr.split(";"); // OK;90002942;20210301;N;204
                String apiStatus = dataArr[0];
                String pkgCode = dataArr[1];

                String dateSales = dataArr[2];
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                dateSales = sdf.format(simpleDateFormat.parse(dateSales));

                String avail = dataArr[3];
                int intStock = Integer.parseInt(dataArr[4]);
                int intOmkStock = intStock;

                if(apiStatus.equals("OK")){
                    String strPropertyID = bookingMapper.getStrPropertyID(strRmtypeID);
                    String result = bookingMapper.updateGoods(strRmtypeID, strPropertyID, dateSales, intStock, intOmkStock);

                    String strResult = result.substring(result.length()-4);
                    if(!strResult.equals("저장완료")){
                        failCount += 1;
                    }

                }else{
                    failCount += 1;
                    message += dateSales + " -> 재고 등록 및 수정 실패\n";
                }
            }

            if(failCount == 0){
                message = "재고 등록 및 수정 완료";
            }else{
                message = " 재고 등록 및 수정 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            message = "재고 등록 및 수정 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        ResponseResult responseResult = new ResponseResult(statusCode, message);
        String strResult = responseResult.makeReturn(statusCode, message, "result!!!~!");

//        return new ResponseResult<>(statusCode, message);
        return strResult;
    }

    public String callElysAPI(String elysUrl){
        String method = "";
        String strUrl = "";
        String message = "";
        String strResponse = "";
        long startTime = System.currentTimeMillis();

        try{
            URL url = new URL(Constants.elysUrl + elysUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept-Charset", "UTF-8");

            if(conn.getResponseCode() == 200){
                method = conn.getRequestMethod();
                strUrl = conn.getURL().toString();

                strResponse = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuffer sb = new StringBuffer();
                while ((strResponse = br.readLine()) != null) {
                    sb.append(strResponse);
                }
                strResponse = sb.toString();
                message = strResponse;

            }else{
                message = "엘리시안 강촌 API 호출 실패";
            }

            conn.disconnect();

            LogWriter logWriter = new LogWriter(conn.getRequestMethod(), conn.getURL().toString(), startTime);
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            LogWriter logWriter = new LogWriter(method, strUrl, startTime);
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return strResponse;
    }





}
