package com.example.stay.accommodation.kumho.service;

import com.example.stay.accommodation.kumho.mapper.SpavisMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.common.util.XmlUtility;
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class SpavisService {
    @Autowired
    private SpavisMapper spavisMapper;

    @Autowired
    private XmlUtility xmlUtility;

    // 쿠폰 사용여부 조회
    public String checkCouponStatus(HttpServletRequest httpServletRequest, String couponNo){
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

                    if(couponStatus.equals("P")){
                        // 발행
                    }else if(couponStatus.equals("I")){
                        // 사용
                    }

                    logWriter.add(coupon_no);
                    logWriter.add(couponStatus);
                    logWriter.add(useDate);
                    logWriter.add(returnMessage);
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
        return commonFunction.makeReturn(statusCode, message);
    }

    // 티켓 주문
    public String orderTicket(HttpServletRequest httpServletRequest, int intBookingIdx){
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
        return commonFunction.makeReturn(statusCode, message);
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

            LogWriter logWriter = new LogWriter(conn.getRequestMethod(), conn.getURL().toString(), startTime);
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
