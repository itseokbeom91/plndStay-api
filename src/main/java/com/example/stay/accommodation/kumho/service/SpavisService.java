package com.example.stay.accommodation.kumho.service;

import com.example.stay.accommodation.kumho.mapper.SpavisMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.common.util.XmlUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class SpavisService {
    @Autowired
    private SpavisMapper spavisMapper;

    @Autowired
    private XmlUtility xmlUtility;

    // 쿠폰 사용여부 조회 - 1개씩
    public String checkCouponStatus(String dataType, HttpServletRequest httpServletRequest, String couponNo){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        try{
            String spavisUrl = "social_interface/couponif03.asp?COUPON_NO=" + couponNo + "&CUST_ID=" + Constants.customerID;

            Document document = callSpavisAPI(spavisUrl);
            if(document != null){
                String coupon_no = document.getElementsByTagName("rtn_coupon_no").item(0).getChildNodes().item(0).getNodeValue();
                String successYn = document.getElementsByTagName("rtn_div").item(0).getChildNodes().item(0).getNodeValue();
                String returnMessage = document.getElementsByTagName("rtn_msg").item(0).getChildNodes().item(0).getNodeValue();
                if(successYn.equals("S")){
                    String couponStatus = document.getElementsByTagName("rtn_status_div").item(0).getChildNodes().item(0).getNodeValue();
                    String useDate = document.getElementsByTagName("rtn_result_date").item(0).getChildNodes().item(0).getNodeValue();

                    // 발행
                    if(couponStatus.equals("P")){

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        Date purchaseDate = sdf.parse(useDate.substring(0,8));

                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(purchaseDate);
                        cal.add(Calendar.DATE, 364);
                        String dateExpired = sdf2.format(cal.getTime());

                        String datePurchase = sdf2.format(purchaseDate);

                       spavisMapper.updateCouponDates(datePurchase, dateExpired, couponNo);
                    }

//                    logWriter.add(coupon_no);
//                    logWriter.add(couponStatus);
//                    logWriter.add(useDate);
//                    logWriter.add(returnMessage);
                }else{
                    message = returnMessage;
                }

            }else{
                message = "아산 스파비스 API 호출 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            statusCode = "500";
            message = "쿠폰 사용여부 조회 실패";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        CommonFunction commonFunction = new CommonFunction();
        return commonFunction.makeReturn(dataType, statusCode, message);
    }



    // 쿠폰 사용여부 조회 - 여러개(동기)
    public String checkCouponListStatus(String dataType, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        int failCount = 0;
        try{
            List<String> couponList = spavisMapper.couponList();
            for(int i=0; i< couponList.size(); i++){
                String couponNo = couponList.get(i);
                String spavisUrl = "social_interface/couponif03.asp?COUPON_NO=" + couponNo + "&CUST_ID=" + Constants.customerID;

                Document document = callSpavisAPI(spavisUrl);
                if(document != null){
                    String coupon_no = document.getElementsByTagName("rtn_coupon_no").item(0).getChildNodes().item(0).getNodeValue();
                    String successYn = document.getElementsByTagName("rtn_div").item(0).getChildNodes().item(0).getNodeValue();
                    String returnMessage = document.getElementsByTagName("rtn_msg").item(0).getChildNodes().item(0).getNodeValue();
                    if(successYn.equals("S")){
                        String couponStatus = document.getElementsByTagName("rtn_status_div").item(0).getChildNodes().item(0).getNodeValue();
                        String useDate = document.getElementsByTagName("rtn_result_date").item(0).getChildNodes().item(0).getNodeValue();

                        // 발행
                        if(couponStatus.equals("P")){

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                            Date purchaseDate = sdf.parse(useDate.substring(0,8));

                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

                            Calendar cal = Calendar.getInstance();
                            cal.setTime(purchaseDate);
                            cal.add(Calendar.DATE, 364);
                            String dateExpired = sdf2.format(cal.getTime());

                            String datePurchase = sdf2.format(purchaseDate);

                            int updateResult = spavisMapper.updateCouponDates(datePurchase, dateExpired, couponNo);
                            if(updateResult < 0){
                                failCount++;
                            }
                        }

//                    logWriter.add(coupon_no);
//                    logWriter.add(couponStatus);
//                    logWriter.add(useDate);
//                    logWriter.add(returnMessage);
                    }else{ // response successYn이 F일 경우
                        failCount += 1;
                    }

                }else{ // 응답값이 없을 경우 -> 호출 실패
                    failCount += 1;
                }
            }

            if(failCount == 0){
                message = "쿠폰 사용여부 업데이트 완료";
            }else{
                message = failCount + " 건 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            statusCode = "500";
            message = "쿠폰 사용여부 조회 실패";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        CommonFunction commonFunction = new CommonFunction();
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 쿠폰 사용여부 조회 - 여러개(비동기)
    @Async
    public int checkCouponListStatus2(HttpServletRequest httpServletRequest, String couponNo){
        int updateResult = 0;
        try{
           String spavisUrl = "social_interface/couponif03.asp?COUPON_NO=" + couponNo + "&CUST_ID=" + Constants.customerID;

            Document document = callSpavisAPI(spavisUrl);
            if(document != null){
                String coupon_no = document.getElementsByTagName("rtn_coupon_no").item(0).getChildNodes().item(0).getNodeValue();
                String successYn = document.getElementsByTagName("rtn_div").item(0).getChildNodes().item(0).getNodeValue();
                String returnMessage = document.getElementsByTagName("rtn_msg").item(0).getChildNodes().item(0).getNodeValue();
                if(successYn.equals("S")){
                    String couponStatus = document.getElementsByTagName("rtn_status_div").item(0).getChildNodes().item(0).getNodeValue();
                    String useDate = document.getElementsByTagName("rtn_result_date").item(0).getChildNodes().item(0).getNodeValue();

                    // 발행
                    if(couponStatus.equals("P")){

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        Date purchaseDate = sdf.parse(useDate.substring(0,8));

                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(purchaseDate);
                        cal.add(Calendar.DATE, 364);
                        String dateExpired = sdf2.format(cal.getTime());

                        String datePurchase = sdf2.format(purchaseDate);

                        updateResult = spavisMapper.updateCouponDates(datePurchase, dateExpired, couponNo);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return updateResult;
    }


    // 티켓 주문
    public String orderTicket(String dataType, HttpServletRequest httpServletRequest, int intBookingIdx){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
//            String spavisUrl = "social_interface/couponif03.asp?order_no=" + intBookingIdx;
        }catch (Exception e){
            statusCode = "500";
            message = "티켓 주문 실패";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        CommonFunction commonFunction = new CommonFunction();
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    public Document callSpavisAPI(String spavisUrl){
        Document document = null;
        String method = "";
        String strUrl = "";
        String message = "";
        long startTime = System.currentTimeMillis();
        try{
            URL url = new URL(Constants.spavisUrl + spavisUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("Content-Type", "application/xml");
            conn.setRequestProperty("Accept-Charset", "UTF-8");

            LogWriter logWriter = new LogWriter(conn.getRequestMethod(), conn.getURL().toString(), startTime);
            if(conn.getResponseCode() == 200){
                method = conn.getRequestMethod();
                strUrl = conn.getURL().toString();

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

                document = dBuilder.parse(conn.getInputStream());
                document.getDocumentElement().normalize();
                String result = xmlUtility.parsingXml(document);

                message = URLDecoder.decode(result, "utf-8");
            }else{
                message = "아산 스파비스 API 호출 실패";
            }

            conn.disconnect();

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            document = null;

            LogWriter logWriter = new LogWriter(method, strUrl, startTime);
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return document;
    }

}
