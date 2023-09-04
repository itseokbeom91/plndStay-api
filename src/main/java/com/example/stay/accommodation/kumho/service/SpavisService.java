package com.example.stay.accommodation.kumho.service;

import com.example.stay.accommodation.kumho.mapper.SpavisMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.common.util.XmlUtility;
import com.example.stay.openMarket.common.dto.RsvStayDto;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
public class SpavisService {
    @Autowired
    private SpavisMapper spavisMapper;

    @Autowired
    private XmlUtility xmlUtility;

    CommonFunction commonFunction = new CommonFunction();

    // 선납권 사용여부 조회 - 1개씩
    public String checkPrepayStatus(String dataType, HttpServletRequest httpServletRequest, String strCouponNo){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        Map<String, Object> resultMap = new HashMap<>();
        try{
            String spavisUrl = "social_interface/couponif03.asp?COUPON_NO=" + strCouponNo + "&CUST_ID=" + Constants.cpCustomerID;

            Document document = callSpavisAPI(spavisUrl);
            if(document != null){
                String resultCode = document.getElementsByTagName("rtn_div").item(0).getChildNodes().item(0).getNodeValue();
                String returnMessage = document.getElementsByTagName("rtn_msg").item(0).getChildNodes().item(0).getNodeValue();
                if(resultCode.equals("S")){
                    String couponStatus = document.getElementsByTagName("rtn_status_div").item(0).getChildNodes().item(0).getNodeValue();
                    String useDate = document.getElementsByTagName("rtn_result_date").item(0).getChildNodes().item(0).getNodeValue();

                    if(couponStatus.equals("P")){
                        couponStatus = "발행";
                    }else{
                        couponStatus = "사용";
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    useDate = sdf2.format(sdf.parse(useDate));

                    resultMap.put("쿠폰 상태값", couponStatus);
                    resultMap.put("발행/사용일시", useDate);
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
            message = "선납권 사용여부 조회 실패";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message, resultMap);
    }

    // 선납권 사용여부 조회 - 여러개(동기)
//    public String checkCouponListStatus(String dataType, HttpServletRequest httpServletRequest){
//        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
//                httpServletRequest.getQueryString(), System.currentTimeMillis());
//        String statusCode = "200";
//        String message = "";
//        int failCount = 0;
//        try{
//            List<String> couponList = spavisMapper.couponList();
//            for(int i=0; i< couponList.size(); i++){
//                String strCouponNo = couponList.get(i);
//                String spavisUrl = "social_interface/couponif03.asp?COUPON_NO=" + strCouponNo + "&CUST_ID=" + Constants.cpCustomerID;
//
//                Document document = callSpavisAPI(spavisUrl);
//                if(document != null){
//                    String resultCode = document.getElementsByTagName("rtn_div").item(0).getChildNodes().item(0).getNodeValue();
//                    String returnMessage = document.getElementsByTagName("rtn_msg").item(0).getChildNodes().item(0).getNodeValue();
//                    if(resultCode.equals("S")){
//                        String couponStatus = document.getElementsByTagName("rtn_status_div").item(0).getChildNodes().item(0).getNodeValue();
//                        String useDate = document.getElementsByTagName("rtn_result_date").item(0).getChildNodes().item(0).getNodeValue();
//
//                        // 발행
//                        if(couponStatus.equals("P")){
//
//                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//                            Date purchaseDate = sdf.parse(useDate.substring(0,8));
//
//                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
//
//                            Calendar cal = Calendar.getInstance();
//                            cal.setTime(purchaseDate);
//                            cal.add(Calendar.DATE, 364);
//                            String dateExpired = sdf2.format(cal.getTime());
//
//                            String datePurchase = sdf2.format(purchaseDate);
//
//                            int updateResult = spavisMapper.updateCouponDates(datePurchase, dateExpired, strCouponNo);
//                            if(updateResult < 0){
//                                failCount++;
//                            }
//                        }
//
////                    logWriter.add(coupon_no);
////                    logWriter.add(couponStatus);
////                    logWriter.add(useDate);
////                    logWriter.add(returnMessage);
//                    }else{ // response resultCode이 F일 경우
//                        failCount += 1;
//                    }
//
//                }else{ // 응답값이 없을 경우 -> 호출 실패
//                    failCount += 1;
//                }
//            }
//
//            if(failCount == 0){
//                message = "쿠폰 사용여부 업데이트 완료";
//            }else{
//                message = failCount + " 건 실패";
//            }
//
//            logWriter.add(message);
//            logWriter.log(0);
//        }catch (Exception e){
//            e.printStackTrace();
//            statusCode = "500";
//            message = "쿠폰 사용여부 조회 실패";
//            logWriter.add("error : " + e.getMessage());
//            logWriter.log(0);
//        }
//
//        return commonFunction.makeReturn(dataType, statusCode, message);
//    }

//    // 선납권 사용여부 조회 - 여러개(비동기)
//    @Async
//    public int checkPrepayListStatus(HttpServletRequest httpServletRequest, String strCouponNo){
//        int updateResult = 0;
//        try{
//           String spavisUrl = "social_interface/couponif03.asp?COUPON_NO=" + strCouponNo + "&CUST_ID=" + Constants.cpCustomerID;
//
//            Document document = callSpavisAPI(spavisUrl);
//            if(document != null){
//                String resultCode = document.getElementsByTagName("rtn_div").item(0).getChildNodes().item(0).getNodeValue();
//                String returnMessage = document.getElementsByTagName("rtn_msg").item(0).getChildNodes().item(0).getNodeValue();
//
//                String strNote = "";
//                String datePurchase = "";
//                String dateExpired = "";
//                if(resultCode.equals("S")) {
//                    String couponStatus = document.getElementsByTagName("rtn_status_div").item(0).getChildNodes().item(0).getNodeValue();
//                    String useDate = document.getElementsByTagName("rtn_result_date").item(0).getChildNodes().item(0).getNodeValue();
//
//                    // 사용 안한 선납권만
//                    if (couponStatus.equals("P")) {
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//                        Date purchaseDate = sdf.parse(useDate.substring(0, 8));
//
//                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
//
//                        Calendar cal = Calendar.getInstance();
//                        cal.setTime(purchaseDate);
//                        cal.add(Calendar.DATE, 364);
//                        dateExpired = sdf2.format(cal.getTime());
//
//                        datePurchase = sdf2.format(purchaseDate);
//
//                        updateResult = spavisMapper.updateCouponDates(datePurchase, dateExpired, strCouponNo);
//                    }
//
//                }else{ // 유효기간 지난 것
//                    strNote = returnMessage;
//
//                    updateResult = spavisMapper.updateStrNote(strNote, strCouponNo);
//                }
//
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return updateResult;
//    }

    // 티켓 발권
    public String orderTicket(String dataType, HttpServletRequest httpServletRequest, int intRsvID){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            RsvStayDto rsvStayDto = spavisMapper.getRsvStayInfo(intRsvID);

            String strRsvName = rsvStayDto.getStrRcvName();
            String strRsvPhone = rsvStayDto.getStrRcvPhone();
            String strRsvTel = strRsvPhone;

            String strRsvEmail = rsvStayDto.getStrRcvEmail();
            if(strRsvEmail == null){
                strRsvEmail = "condo24@condo24.com";
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String orderDate = sdf.format(rsvStayDto.getDateCreated());
            String strSalesDate = orderDate;
            String strExpiredDate = rsvStayDto.getDatePurchase().replace("-", "");

            String classDiv = ""; // 성인 : A, 소아 : B
            double doubleSales = 0;
            double doubleCost = rsvStayDto.getMoneyCostA();
            // 성인/소아 요금 존재 여부로 구분
            // 성인일 경우
            if(doubleCost != 0){
                classDiv = "A";
                doubleSales = rsvStayDto.getMoneySalesA();
            }else{ // 소아일 경우
                classDiv = "B";
                doubleCost = rsvStayDto.getMoneyCostC();
                doubleSales = rsvStayDto.getMoneySalesC();
            }

            int useCount = rsvStayDto.getIntRmCnt();

//            String[] spiDate = strSalesDate.split("-");
//            LocalDate date = LocalDate.of(Integer.parseInt(spiDate[0].toString()),Integer.parseInt(spiDate[1].toString()),Integer.parseInt(spiDate[2].toString()));
//            DayOfWeek week = date.getDayOfWeek();
//            int intWeek = week.getValue();  // 1: 월요일, 7: 일요일
//            if(intWeek == 7){
//
//            }
            String useSeason = "0";

            String dateSales = sdf.format(rsvStayDto.getDateCreated());

            // api request 데이터 생성
            // 한 주문으로 구매한 티켓이 여러장일 경우 상품코드, 이용시작일, 이용종료일, 대/소인, 이용인원수, 이용금액, 쿠폰번호, 이용시즌 데이터를
            // 개수에 맞춰 ,로 구분해서 보내야함
            String goodsCodes = "";
            String salesDates = "";
            String expiredDates = "";
            String classDivs = "";
            String useCounts = "";
            String costs = "";
            String tickets = "";
            String useSeasons = "";
            int ticketNo = 0;
            String strTicketDatas = "";
            for(int i=0; i<useCount; i++){
                // TR_ + 티켓 테이블의 MAX(idx) + 1 로 생성
                if(i==0){
                    ticketNo = spavisMapper.getMaxIdx()+1;
                }else{
                    ticketNo += 1;
                }
                // TODO : TR_로 변경
                String strTicketNo = "TEST_" + ticketNo;

                if(i == useCount-1){
                    goodsCodes += Constants.goodsCode;
                    salesDates += strSalesDate;
                    expiredDates += strExpiredDate;
                    classDivs += classDiv;
                    useCounts += useCount;
                    costs += doubleCost;
                    tickets += strTicketNo;
                    useSeasons += useSeason;
                }else{
                    goodsCodes += Constants.goodsCode + ",";
                    salesDates += strSalesDate + ",";
                    expiredDates += strExpiredDate + ",";
                    classDivs += classDiv + ",";
                    useCounts += useCount + ",";
                    costs += doubleCost + ",";
                    tickets += strTicketNo + ",";
                    useSeasons += useSeason + ",";
                }
                strTicketDatas += strTicketNo + "|^|" + intRsvID + "|^|" + dateSales + "|^|" + strExpiredDate + "|^|" + doubleCost + "|^|" + doubleSales + "{{|}}";
            }

            strTicketDatas = strTicketDatas.substring(0, strTicketDatas.length()-5);

            // 티켓테이블에 정보 INSERT
            String result = spavisMapper.insertTicket(strTicketDatas);

            if(result.equals("")){
                // 티켓 발권 API 호출
                String spavisUrl = "social_interface/socif01.asp?order_no=" + intRsvID + "&guest_name=" + URLEncoder.encode(strRsvName, "utf-8") +
                        "&guest_cell_phone=" + strRsvPhone + "&guest_tele_no=" + strRsvTel + "&guest_email=" + strRsvEmail +
                        "&cust_id=" + Constants.tkCustomerID + "&approv_date=" + orderDate + "&goods_code=" + goodsCodes +
                        "&use_f_date=" + salesDates + "&use_t_date=" + expiredDates + "&class_div=" + classDivs +
                        "&use_cnt=" + useCounts + "&use_amt=" + costs + "&coupon_no=" + tickets + "&use_season=" + useSeasons;

                Document document = callSpavisAPI(spavisUrl);
                if(document != null){
                    String resultCode = document.getElementsByTagName("rtn_div").item(0).getChildNodes().item(0).getNodeValue();
                    String returnMessage = document.getElementsByTagName("rtn_msg").item(0).getChildNodes().item(0).getNodeValue();
                    if(resultCode.equals("S")){
                        // 예약 테이블 상태값 업데이트
                        spavisMapper.updateStrStatusCode("4", intRsvID);

                        // TODO : 카톡 발송
                        String sender = "15880134";
                        String receiver = strRsvPhone;

                        SimpleDateFormat sdf2 = new SimpleDateFormat("MM월dd일까지");
                        String strExpirationDay = sdf2.format(sdf.parse(strExpiredDate));

                        String kkoMsg = "[콘도24닷컴] 티켓정보 \n" +
                                "● 고객명: " + strRsvName + "\n" +
                                "● 상품명: 아산스파비스 이용권\n" +
                                "● 수량:  " + useCount + "\n" +
                                "● 티켓번호 : " + tickets + "\n" +
                                "● 유효기간 : " + strExpirationDay + "\n" +
                                "※수신된 URL의 QR코드로 매표소에서 수령\n" +
                                "☎고객센터: 1588-0134 ①번\n" +
                                "☎매표소: 041-539-2000\n\n" +
                                "아래 주소를 확인해주세요 \n\n" +
                                "http://www.condo24.com/QRcode.asp?oid=" + intRsvID;

                        spavisMapper.insertKkoMsg(receiver, sender, kkoMsg);

                        message = "티켓 발권 완료";
                    }else{
                        message = "티켓 발권 실패";
                        logWriter.add(returnMessage);
                    }
                }else{
                    message = "아산 스파비스 API 호출 실패";
                }
            }else{
                message = "티켓 데이터 생성 실패";
            }

            logWriter.add(message);
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
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
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
                    // 예약테이블 상태값 업데이트
                    spavisMapper.updateStrStatusCode("5", intRsvID);

                    // 티켓 테이블 상태값 업데이트
                    // 전체 티켓 취소일 경우
                    int cancelResult = 0;
                    if(strTicketNo.equals("ALL")){
                        cancelResult = spavisMapper.cancelAllTicket(intRsvID);
                    }
//                    else{ // 부분 취소일 경우
//                        cancelResult = spavisMapper.updateTicketStatus("C", null, strTicketNo, intRsvID);
//                    }

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
    // 취소티켓 조회불가
    // 미사용 -> 날짜 지났는데 미사용
    public String checkTicketStatus(String dataType, HttpServletRequest httpServletRequest, int intRsvID){
        String statusCode = "200";
        String message = "";
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        List<Map<String, Object>> resultMapList = new ArrayList<>();
        try{
            List<String> ticketList = spavisMapper.getTicketList(intRsvID);

            String strOrderID = String.valueOf(intRsvID);

            String tickets = "";
            for(int i=0; i< ticketList.size(); i++){
                if(i == ticketList.size() -1){
                    tickets += ticketList.get(i);
                }else{
                    tickets += ticketList.get(i) + ",";
                }
            }

            String spavisUrl = "social_interface/socif03.asp?order_no=" + strOrderID + "&coupon_no=" + tickets +
                                "&Cust_id=" + Constants.tkCustomerID;

//            String spavisUrl = "social_interface/socif03.asp?order_no=" + intRsvID + "&coupon_no=" + strTicketNo + "&Cust_id=" + Constants.tkCustomerID;

            Document document = callSpavisAPI(spavisUrl);
            if(document != null){
                String resultCode = document.getElementsByTagName("rtn_div").item(0).getChildNodes().item(0).getNodeValue();
                String returnMessage = document.getElementsByTagName("rtn_msg").item(0).getChildNodes().item(0).getNodeValue();
                if(resultCode.equals("S")){
                    NodeList reservList = document.getElementsByTagName("rtn_coupon");
                    for (int i = 0; i < reservList.getLength(); i++) {
                        Map<String, Object> resultMap = new HashMap<>();
                        Node node = reservList.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;

                            String strTicketNo = xmlUtility.getTagValue("rtn_coupon_no", element);
                            // 예약(R), 미사용(N), 사용(I), 취소(C)
                            String strTicketStatus = xmlUtility.getTagValue("rtn_status_div", element);
                            String strResultDate = xmlUtility.getTagValue("rtn_result_date", element);

                            if(strTicketStatus.equals("R")){
                                strTicketStatus = "예약";
                            }else if(strTicketStatus.equals("N")){
                                strTicketStatus = "미사용";
                            }else if(strTicketStatus.equals("I")){
                                strTicketStatus = "사용";
                            }else if(strTicketStatus.equals("C")){
                                strTicketStatus = "취소";
                            }

                            resultMap.put("티켓 번호", strTicketNo);
                            resultMap.put("티켓 상태값", strTicketStatus);
                            resultMap.put("예약/사용/취소일시", strResultDate);

                            resultMapList.add(resultMap);
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
        return commonFunction.makeReturn(dataType, statusCode, message, resultMapList);
    }

    // 티켓 사용여부 조회(일별)
    // 구매날짜
    public String checkTicketStatusByDate(String dataType, HttpServletRequest httpServletRequest, String searchDate){
        String statusCode = "200";
        String message = "";
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        List<Map<String, Object>> resultMapList = new ArrayList<>();
        try{
            String spavisUrl = "social_interface/socif05.asp?cust_id=" + Constants.tkCustomerID + "&result_date=" + searchDate;

            Document document = callSpavisAPI(spavisUrl);
            if(document != null){
                String resultCode = document.getElementsByTagName("rtn_div").item(0).getChildNodes().item(0).getNodeValue();
                String resultMsg = URLDecoder.decode(document.getElementsByTagName("rtn_msg").item(0).getChildNodes().item(0).getNodeValue(), "utf-8");
                if(resultCode.equals("S")) {
                    NodeList reservList = document.getElementsByTagName("rtn_coupon");
                    for (int i = 0; i < reservList.getLength(); i++) {
                        Map<String, Object> resultMap = new HashMap<>();
                        Node node = reservList.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;

                            String strTicketNo = xmlUtility.getTagValue("rtn_coupon_no", element);
                            // 예약(R), 미사용(N), 사용(I), 취소(C)
                            String strTicketStatus = xmlUtility.getTagValue("rtn_status_div", element);
                            String strResultDate = xmlUtility.getTagValue("rtn_result_date", element); // 사용 안했으면 null값이 옴

                            if(strTicketStatus.equals("R")){
                                strTicketStatus = "예약";
                            }else if(strTicketStatus.equals("N")){
                                strTicketStatus = "미사용";
                            }else if(strTicketStatus.equals("I")){
                                strTicketStatus = "사용";
                            }else if(strTicketStatus.equals("C")){
                                strTicketStatus = "취소";
                            }

                            resultMap.put("티켓 번호", strTicketNo);
                            resultMap.put("티켓 상태값", strTicketStatus);
                            resultMap.put("사용일시", strResultDate);

                            resultMapList.add(resultMap);
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
        return commonFunction.makeReturn(dataType, statusCode, message, resultMapList);
    }

    // 티켓 발권 처리(스파비스에서 호출)
    public String updateStatus(HttpServletRequest httpServletRequest, String strRsvID, String strTicketNo, String strUseStatus, String dateUsed){
        String message = "";
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        String successYn = "F";
        String result = "";
        try{
            // 티켓번호 우리 DB에 있는지 확인
            int ticketCnt = spavisMapper.getStrTicketNoCnt(strRsvID, strTicketNo);
            if(ticketCnt > 0){
                // 상태값, 사용일시 업데이트 -> 성공하면 result = "S";
                int updateResult = spavisMapper.updateTicketStatus(strUseStatus, dateUsed, strTicketNo, strRsvID);

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
