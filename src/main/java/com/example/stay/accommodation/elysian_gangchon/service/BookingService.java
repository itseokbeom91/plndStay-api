package com.example.stay.accommodation.elysian_gangchon.service;

import com.example.stay.accommodation.elysian_gangchon.mapper.BookingMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
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
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service("elysian_gangchon.BookingService")
public class BookingService {

    @Autowired
    private BookingMapper bookingMapper;

    CommonFunction commonFunction = new CommonFunction();

    // 재고 등록 및 수정
    public String updateGoods(HttpServletRequest httpServletRequest, String sdate, String edate, int intRmIdx){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            String pcode = "90004884";
            String pcode_seq = "1";

            String elysUrl = "type=SB&pcode=" + pcode + "&pcode_seq=" + pcode_seq + "&sdate=" + sdate + "&edate=" + edate;

            String strResponse = callElysAPI(elysUrl);

            if(strResponse != null && !strResponse.equals("")){
                int intAID = bookingMapper.getIntAID(intRmIdx);

                String strStockDatas = "";
                String[] responseArr = strResponse.split("#");
                for(String arr : responseArr){
                    String[] dataArr = arr.split(";");
//                    String apiStatus = dataArr[0];
//                    String pkgCode = dataArr[1];

                    String dateSales = dataArr[2];
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    dateSales = sdf.format(simpleDateFormat.parse(dateSales));

//                    String avail = dataArr[3];
                    int intStock = Integer.parseInt(dataArr[4]);
                    int intOmkStock = intStock;

                    int intCost = 0, intSales = 0, intExtraA = 0, intExtraB = 0, intExtraC = 0, intOmkSales = 0;

                    strStockDatas += dateSales + "|^|" + intStock + "|^|" + intCost + "|^|" + intSales + "|^|"
                            + intExtraA + "|^|" + intExtraC + "|^|" + intExtraB + "|^|" + intOmkStock + "|^|"  + intOmkSales+ "{{|}}";

                }
                strStockDatas = strStockDatas.substring(0, strStockDatas.length()-5);

                String result = bookingMapper.updateGoods(intAID, intRmIdx, strStockDatas);
                String strResult = result.substring(result.length()-4);

                if(strResult.equals("저장완료")){
                    message = "재고 등록 및 수정 완료";
                }else{
                    message = " 재고 등록 및 수정 실패";
                }

            }else{
                message = "엘리시안 API 호출 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            message = "재고 등록 및 수정 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
            e.printStackTrace();
        }

        return commonFunction.makeReturn(statusCode, message);
    }

    // 예약 가능여부 조회
    public boolean checkAvailBooking(String pcode, String pcode_seq, String sdate){
        LogWriter logWriter = new LogWriter(System.currentTimeMillis());
        String message = "";
        boolean avail = false;
        try{
            String elysUrl = "type=SB&pcode=" + pcode + "&pcode_seq=" + pcode_seq + "&sdate=" + sdate + "&edate=" + sdate;

            String strResponse = callElysAPI(elysUrl);

            if(strResponse != null && !strResponse.equals("")){
                String[] responseArr = strResponse.split("#");
                for(String arr : responseArr){
                    String[] dataArr = arr.split(";");
                    String strAvail = dataArr[3];
                    if(strAvail.equals("Y")){
                        avail = true;
                    }
                }
            }else{
                message = "엘리시안 API 호출 실패";
            }
        }catch (Exception e){
            message = "예약 가능여부 조회 실패";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        logWriter.add(message);
        logWriter.log(0);

        return avail;
    }

    // 예약
    public String createBooking(int intBookingIdx, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());

        try{
            String mdn  = "01011111111";
            String name  = "개발테스트";
            String pcode  = "90004891";
            String pcode_seq  = "1";
            String bdate  = "20230807";
            int cnt  = 1;
            String tseq  = "980";
            String DH_CODE1 = "1030";
            String DH_CODE2 = "9999";
            String PASS = "1234";
            String AMT_YN = "N";

            // 예약가능 여부 확인
            boolean avail = checkAvailBooking(pcode, pcode_seq, bdate);
            if(avail){
                String elysUrl = "type=RO&mdn=" + mdn + "&name=" + URLDecoder.decode(name, "EUC-KR") + "&pcode=" + pcode + "&pcode_seq=" + pcode_seq +
                        "&bdate="+ bdate + "&cnt="+ cnt + "&tseq="+ tseq + "&DH_CODE1=" + DH_CODE1 + "&PASS=" + PASS + "&DH_CODE2=" + DH_CODE2 + "&AMT_YN=" + AMT_YN;
                String strResponse = callElysAPI(elysUrl);

                if(strResponse != null && !strResponse.equals("")){
                    if(strResponse.substring(0,5).equals("ERROR")){
                        message = strResponse;
                    }else{
                        message = "예약 성공";
                    }

                }else{
                    message = "엘리시안 API 호출 실패";
                }
            }else{
                message = "예약 불가";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            message = "예약 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
            e.printStackTrace();
        }
        return commonFunction.makeReturn(statusCode, message);
    }

    // 예약 조회
    public String checkBooking(int intBookingIdx, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        try{
            /**
             * TODO : intBookingIdx로 엘리시안 예약번호 조회 프로세스 추가
             */
            String bno = "751FK0PE";

            String elysUrl = "type=SO&bno=" + bno;
            String strResponse = callElysAPI(elysUrl);

            if(strResponse != null && !strResponse.equals("")){
                if(strResponse.substring(0,4).equals("error")){
                    message = strResponse;
                }else{
                    /**
                     * TODO : 추후 예약정보 어떻게 내려줄건지
                     */
                    message = "예약 조회 완료 : " + strResponse;
                }
            }else{
                message = "엘리시안 API 호출 실패";
            }

            logWriter.add(message);
            logWriter.log(0);

        }catch (Exception e){
            message = "예약 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
            e.printStackTrace();
        }
        return commonFunction.makeReturn(statusCode, message);
    }

    // 예약 취소
    public String cancelBooking(int intBookingIdx, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        try{
            // intBookingIdx로 엘리시안 예약번호 조회 프로세스 추가
            String bno = "751FK0PC";

            String elysUrl = "type=CO&bno=" + bno;
            String strResponse = callElysAPI(elysUrl);

            if(strResponse != null && !strResponse.equals("")){
                if(strResponse.substring(0,4).equals("error")){
                    message = strResponse;
                }else{
                    message = "예약 취소 완료";
                }
            }else{
                message = "엘리시안 API 호출 실패";
            }

            logWriter.add(message);
            logWriter.log(0);

        }catch (Exception e){
            message = "예약 취소 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
            e.printStackTrace();
        }
        return commonFunction.makeReturn(statusCode, message);
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
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.setRequestProperty("Accept-Charset", "UTF-8");

            System.out.println("code : " + conn.getResponseCode());

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                method = conn.getRequestMethod();
                strUrl = conn.getURL().toString();

                strResponse = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "EUC-KR"));
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
            e.printStackTrace();
        }
        return strResponse;
    }


//    public String callPostElysAPI(String elysUrl){
//        String method = "";
//        String strUrl = "";
//        String message = "";
//        String strResponse = "";
//        long startTime = System.currentTimeMillis();
//
//        try{
//            URL url = new URL(Constants.elysUrl + elysUrl);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setConnectTimeout(10000);
//            conn.setReadTimeout(10000);
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//            conn.setRequestProperty("Accept-Charset", "UTF-8");
//
//            byte[] postDataBytes = elysUrl.getBytes("UTF-8");
//            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
//
//            conn.setDoOutput(true);
//            conn.getOutputStream().write(postDataBytes);
//
//            System.out.println("~~~~~~~~~~~~");
//            System.out.println("code : " + conn.getResponseCode());
//            System.out.println(conn.getURL());
//            System.out.println("~~~~~~~~~~~~");
//
////            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
//                method = conn.getRequestMethod();
//                strUrl = conn.getURL().toString();
//
//                strResponse = "";
//                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "EUC-KR"));
//                StringBuffer sb = new StringBuffer();
//                while ((strResponse = br.readLine()) != null) {
//                    sb.append(strResponse);
//                }
//                strResponse = sb.toString();
//                message = strResponse;
//
////            }else{
////                message = "엘리시안 강촌 API 호출 실패";
////            }
//
//            conn.disconnect();
//
//            LogWriter logWriter = new LogWriter(conn.getRequestMethod(), conn.getURL().toString(), startTime);
//            logWriter.add(message);
//            logWriter.log(0);
//        }catch (Exception e){
//            LogWriter logWriter = new LogWriter(method, strUrl, startTime);
//            logWriter.add("error : " + e.getMessage());
//            logWriter.log(0);
//    e.printStackTrace();
//        }
//        return strResponse;
//    }




}
