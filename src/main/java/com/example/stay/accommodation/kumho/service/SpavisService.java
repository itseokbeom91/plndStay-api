package com.example.stay.accommodation.kumho.service;

import com.example.stay.accommodation.kumho.mapper.SpavisMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.common.util.XmlUtility;
import org.apache.ibatis.javassist.Loader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SpavisService {
    @Autowired
    private SpavisMapper spavisMapper;

    @Autowired
    private XmlUtility xmlUtility;

    CommonFunction commonFunction = new CommonFunction();

    // 선납권 사용여부 조회 - 1개씩
    public String checkCouponStatus(String dataType, HttpServletRequest httpServletRequest, String strCouponNo){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        try{
            String spavisUrl = "social_interface/couponif03.asp?COUPON_NO=" + strCouponNo + "&CUST_ID=" + Constants.cpCustomerID;

            Document document = callSpavisAPI(spavisUrl);
            if(document != null){
                String coupon_no = document.getElementsByTagName("rtn_coupon_no").item(0).getChildNodes().item(0).getNodeValue();
                String resultCode = document.getElementsByTagName("rtn_div").item(0).getChildNodes().item(0).getNodeValue();
                String returnMessage = document.getElementsByTagName("rtn_msg").item(0).getChildNodes().item(0).getNodeValue();
                if(resultCode.equals("S")){
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

                       spavisMapper.updateCouponDates(datePurchase, dateExpired, strCouponNo);
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
            e.printStackTrace();
            statusCode = "500";
            message = "쿠폰 사용여부 조회 실패";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 선납권 사용여부 조회 - 여러개(동기)
    public String checkCouponListStatus(String dataType, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        int failCount = 0;
        try{
            List<String> couponList = spavisMapper.couponList();
            for(int i=0; i< couponList.size(); i++){
                String strCouponNo = couponList.get(i);
                String spavisUrl = "social_interface/couponif03.asp?COUPON_NO=" + strCouponNo + "&CUST_ID=" + Constants.cpCustomerID;

                Document document = callSpavisAPI(spavisUrl);
                if(document != null){
                    String coupon_no = document.getElementsByTagName("rtn_coupon_no").item(0).getChildNodes().item(0).getNodeValue();
                    String resultCode = document.getElementsByTagName("rtn_div").item(0).getChildNodes().item(0).getNodeValue();
                    String returnMessage = document.getElementsByTagName("rtn_msg").item(0).getChildNodes().item(0).getNodeValue();
                    if(resultCode.equals("S")){
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

                            int updateResult = spavisMapper.updateCouponDates(datePurchase, dateExpired, strCouponNo);
                            if(updateResult < 0){
                                failCount++;
                            }
                        }

//                    logWriter.add(coupon_no);
//                    logWriter.add(couponStatus);
//                    logWriter.add(useDate);
//                    logWriter.add(returnMessage);
                    }else{ // response resultCode이 F일 경우
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
            e.printStackTrace();
            statusCode = "500";
            message = "쿠폰 사용여부 조회 실패";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 선납권 사용여부 조회 - 여러개(비동기)
    @Async
    public int checkCouponListStatus2(HttpServletRequest httpServletRequest, String strCouponNo){
        int updateResult = 0;
        try{
           String spavisUrl = "social_interface/couponif03.asp?COUPON_NO=" + strCouponNo + "&CUST_ID=" + Constants.cpCustomerID;

            Document document = callSpavisAPI(spavisUrl);
            if(document != null){
                String resultCode = document.getElementsByTagName("rtn_div").item(0).getChildNodes().item(0).getNodeValue();
                String returnMessage = document.getElementsByTagName("rtn_msg").item(0).getChildNodes().item(0).getNodeValue();

                String strNote = "";
                String datePurchase = "";
                String dateExpired = "";
                if(resultCode.equals("S")) {
//                    String coupon_no = document.getElementsByTagName("rtn_coupon_no").item(0).getChildNodes().item(0).getNodeValue();
                    String couponStatus = document.getElementsByTagName("rtn_status_div").item(0).getChildNodes().item(0).getNodeValue();
                    String useDate = document.getElementsByTagName("rtn_result_date").item(0).getChildNodes().item(0).getNodeValue();

                    // 사용 안한 선납권만
                    if (couponStatus.equals("P")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        Date purchaseDate = sdf.parse(useDate.substring(0, 8));

                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(purchaseDate);
                        cal.add(Calendar.DATE, 364);
                        dateExpired = sdf2.format(cal.getTime());

                        datePurchase = sdf2.format(purchaseDate);

                        updateResult = spavisMapper.updateCouponDates(datePurchase, dateExpired, strCouponNo);
                    }

                }else{ // 유효기간 지난 것
                    strNote = returnMessage;

                    updateResult = spavisMapper.updateStrNote(strNote, strCouponNo);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return updateResult;
    }


    // 티켓 발권
    public String orderTicket(String dataType, HttpServletRequest httpServletRequest, int intRsvID){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            // TODO : 예약, RM_OPTION(금액) 테이블에서 정보 가져오기 -> 주문번호 하나에 여러개일 수도 있음
            String strRsvName = "개발테스트";
            String strRsvPhone = "01029405275";
            String strRsvTel = "01029405275";
            String strRsvEmail = "condo24@condo24.com";
            String orderDate = "20230706";
            String strSalesDate = "2023-12-12";
            String strExpiredDate = "2023-12-13";
            String classDiv = "A"; // 성인 : A, 소아 : B
            String useCount = "1";
            int intCost = 19000;
            int intSales = 22000;
            String strTicketNo = "TR_50000"; // TR_ + 티켓 테이블의 MAX(idx) + 1 로 생성
            String useSeason = "0";

            // 티켓테이블에 정보 INSERT
            int insertResult = spavisMapper.insertTicket(strTicketNo, intRsvID, strSalesDate, strExpiredDate, intCost, intSales);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dateSales = simpleDateFormat.parse(strSalesDate);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String strDateSales = sdf.format(dateSales);


            // 구매한 티켓 개수만큼 insert가 됐으면
            // 티켓 발권 API 호출
            String spavisUrl = "social_interface/socif01.asp?order_no=" + intRsvID + "&guest_name=" + URLEncoder.encode(strRsvName, "utf-8") +
                                "&guest_cell_phone=" + strRsvPhone + "&guest_tele_no=" + strRsvTel + "&guest_email=" + strRsvEmail +
                                "&cust_id=" + Constants.tkCustomerID + "&approv_date=" + orderDate + "&goods_code=" + Constants.goodsCode +
                                "&use_f_date=" + strDateSales + "&use_t_date=" + strExpiredDate + "&class_div=" + classDiv +
                                "&use_cnt=" + useCount + "&use_amt=" + intCost + "&coupon_no=" + strTicketNo + "&use_season=" + useSeason;

            Document document = callSpavisAPI(spavisUrl);
            if(document != null){
                String resultCode = document.getElementsByTagName("rtn_div").item(0).getChildNodes().item(0).getNodeValue();
                String returnMessage = document.getElementsByTagName("rtn_msg").item(0).getChildNodes().item(0).getNodeValue();
                if(resultCode.equals("S")){
//                    String couponStatus = document.getElementsByTagName("rtn_status_div").item(0).getChildNodes().item(0).getNodeValue();

                    // TODO : 예약테이블 상태값 업데이트


                    // TODO : 카톡 발송

                    message = "티켓 발권 완료";
                }else{
                    message = "티켓 발권 실패";
                    logWriter.add(returnMessage);
                }
            }else{
                message = "아산 스파비스 API 호출 실패";
            }

//


            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            message = "티켓 발권 실패";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 티켓 취소(현재 부분취소 기능은 사용X)
    public String cancelTicket(String dataType, HttpServletRequest httpServletRequest, int intRsvID, String strTicketNo){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            // TODO : 예약 테이블에서 정보 가져오기

            // 특정한 티켓번호가 없으면 한 주문 번호에 해당하는 전체 티켓 취소
            if(strTicketNo == null){
                strTicketNo = "ALL";
            }

            String spavisUrl = "social_interface/socif04.asp?order_no=" + intRsvID + "&COUPON_NO=" + strTicketNo + "&cust_id=" + Constants.tkCustomerID;

            Document document = callSpavisAPI(spavisUrl);
            if(document != null){
                String resultCode = document.getElementsByTagName("rtn_div").item(0).getChildNodes().item(0).getNodeValue();
                String returnMessage = document.getElementsByTagName("rtn_msg").item(0).getChildNodes().item(0).getNodeValue();
                if(resultCode.equals("S")){
                    // TODO : 예약테이블 상태값 업데이트

                    // 티켓 테이블 상태값 업데이트
                    // 전체 티켓 취소일 경우
                    int cancelResult = 0;
                    if(strTicketNo == null || strTicketNo.equals("")){
                        cancelResult = spavisMapper.cancelAllTicket(intRsvID);
                    }else{ // 부분 취소일 경우
                        cancelResult = spavisMapper.updateTicketStatus("C", null, strTicketNo, intRsvID);
                    }

                    if(cancelResult > 0){
                        message = "티켓 취소 완료";
                    }else{
                        message = "티켓 취소 실패";
                    }
                }else{
                    message = "티켓 취소 실패";
                    logWriter.add(returnMessage);
                }
            }else{
                message = "아산 스파비스 API 호출 실패";
            }
        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            message = "티켓 취소 실패";
            logWriter.add("error : " + e.getMessage());
        }

        logWriter.add(message);
        logWriter.log(0);

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 티켓 사용여부 조회(건별)
    public String checkTicketStatus(String dataType, HttpServletRequest httpServletRequest, int intRsvID){
        String statusCode = "200";
        String message = "";
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());

        try{
            /**
             * TODO : 예약번호로 정보 가져오기
             */
            String strOrderID = "2023-0701-10927498071";
            String strTicketNo = "TR_30432,TR_30431";

            String spavisUrl = "social_interface/socif03.asp?order_no=" + intRsvID + "&coupon_no=" + strTicketNo +
                                "&Cust_id=" + Constants.tkCustomerID;

            Document document = callSpavisAPI(spavisUrl);
            if(document != null){
                String resultCode = document.getElementsByTagName("rtn_div").item(0).getChildNodes().item(0).getNodeValue();
                String returnMessage = document.getElementsByTagName("rtn_msg").item(0).getChildNodes().item(0).getNodeValue();
                if(resultCode.equals("S")){
                    NodeList reservList = document.getElementsByTagName("rtn_coupon");
                    for (int i = 0; i < reservList.getLength(); i++) {
                        Node node = reservList.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;

                            // 예약(R), 미사용(N), 사용(I), 취소(C)
                            String strUseStatus = document.getElementsByTagName("rtn_status_div").item(0).getChildNodes().item(0).getNodeValue();
                            String dateUsed = document.getElementsByTagName("rtn_result_date").item(0).getChildNodes().item(0).getNodeValue();

                            message = "티켓 사용여부 조회 완료";
                        }
                    }
                }else{
                    message = "티켓 사용여부 조회 실패";
                    logWriter.add(returnMessage);
                }
            }else{
                message = "아산 스파비스 API 호출 실패";
            }

        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            message = "티켓 사용여부 조회 실패";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 티켓 사용여부 조회(일별)
    public String checkTicketStatusByDate(String dataType, HttpServletRequest httpServletRequest, String searchDate){
        String statusCode = "200";
        String message = "";
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());

        try{
            String spavisUrl = "social_interface/socif05.asp?cust_id=" + Constants.tkCustomerID + "&result_date=" + searchDate;

            Document document = callSpavisAPI(spavisUrl);
            if(document != null){
                String resultCode = document.getElementsByTagName("rtn_div").item(0).getChildNodes().item(0).getNodeValue();
                String resultMsg = URLDecoder.decode(document.getElementsByTagName("rtn_msg").item(0).getChildNodes().item(0).getNodeValue(), "utf-8");
                if(resultCode.equals("S")) {
                    NodeList reservList = document.getElementsByTagName("rtn_coupon");
                    for (int i = 0; i < reservList.getLength(); i++) {
                        Node node = reservList.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;

                            // 예약(R), 미사용(N), 사용(I), 취소(C)
                            String strTicketStatus = xmlUtility.getTagValue("rtn_status_div", element);

                            String strResultDate = xmlUtility.getTagValue("rtn_result_date", element); // 사용 안했으면 null값이 옴
//                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            Date resultDate = sdf.parse(strResultDate);

                            System.out.println("strTicketStatus : " + strTicketStatus);
                            System.out.println("resultDate : " + strResultDate);

                            String ss = null;
                            if(ss.equals("ss")){

                            }

                        }
                    }
                    message = "티켓 사용여부 조회 완료";
                }else{
                    if(resultMsg.equals("NOT EXISTS DATA")){
                        message = "해당 일자의 티켓 데이터가 존재하지 않습니다";
                    }else{
                        message = "티켓 사용여부 조회 실패";
                    }
                }
            }else{
                message = "아산 스파비스 API 호출 실패";
            }
            logWriter.add(message);
        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            message = "티켓 사용여부 조회 실패";
            logWriter.add("error : " + e.getMessage());
        }
        logWriter.log(0);
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 티켓 발권 처리(스파비스에서 호출)
    public String updateStatus(HttpServletRequest httpServletRequest, String strRsvID, String strTicketNo, String strUseStatus, String dateUsed){
        String message = "";
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());

        String successYn = "F";
        String result = "";
        try{
            // 주문번호 우리 DB에 있는지 확인
            int intRsvID = Integer.parseInt(strRsvID);

            // 티켓번호 우리 DB에 있는지 확인
            int ticketCnt = spavisMapper.getStrTicketNoCnt(strTicketNo);
            if(ticketCnt > 0){
                // 상태값, 사용일시 업데이트 -> 성공하면 result = "S";
                int updateResult = spavisMapper.updateTicketStatus(strUseStatus, dateUsed, strTicketNo, intRsvID);

                if(updateResult > 0){
                    successYn = "S";
                    message = "success";
                }else{
                    message = "티켓 발권처리 실패";
                }
            }else{
                message = "해당 티켓번호가 존재하지 않습니다";
            }

        }catch (Exception e){
            e.printStackTrace();
            message = "티켓 발권처리 실패";
            logWriter.add("error : " + e.getMessage());
        }

        result = "<data>\n" +
                "    <rtn_div>" + successYn + "</rtn_div>\n" +
                "    <rtn_msg>" + message + "</rtn_msg>\n" +
                "</data>";

        logWriter.add(result);
        logWriter.log(0);

        return result;
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
