package com.example.stay.accommodation.kumho.service;

import com.example.stay.accommodation.kumho.mapper.BookingMapper;
import com.example.stay.common.util.*;
import com.example.stay.openMarket.common.dto.BookingDto;
import org.apache.catalina.util.ToStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.print.Doc;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("kumho.BookingService")
public class BookingService {

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private XmlUtility xmlUtility;

    CommonFunction commonFunction = new CommonFunction();

    private String site = "1"; // 현재 무조건 1

    // 예약
    public String createBooking(String dataType, int intBookingID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        try{
            BookingDto bookingDto = bookingMapper.getBookingByIntBookingID(intBookingID);

            String ipark_resno = Integer.toString(intBookingID); // 예약번호
            String area = bookingDto.getAccommId(); // 사업장(통영, 화순, 설악, 제주)
            String arrive_date = bookingDto.getCheckInDate(); // 도착일자
            String leave_date = bookingDto.getCheckOutDate(); // 퇴실일자

            long nights_count = 0; // 숙박일수
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date inDate = sdf.parse(arrive_date);
            Date outDate = sdf.parse(leave_date);
            nights_count = ((outDate.getTime() - inDate.getTime()) / 1000) / (24*60*60);

            String morning_aqua = bookingDto.getStrRatePlanId(); // 패키지 상품구분
            String event_div = "1"; // 객실예약인지 패키지인지
            if(morning_aqua.equals("roomOnly")){
                event_div = "0";
            }
            
            String use_name = bookingDto.getStrRecvName(); // 사용자명
            String use_phone = bookingDto.getStrRecvPhone().replace("-",""); // 사용자 전화번호
            String use_cell_phone = use_phone; // 사용자 휴대폰번호
            String morning_div = bookingDto.getStrMealCode(); // 조식여부
            String room_type = bookingDto.getStrRoomTypeId(); // 객실타입
            int room_count = bookingDto.getIntRoomCount(); // 객실 수
            int person_count = bookingDto.getIntPersonCount(); // 인원
            String coupon_year = "*"; // 쿠폰발행년도(무조건 *)
            int coupon_number = 0; // 쿠폰번호(무조건 0)
            String ipark_goodsno = Integer.toString(bookingDto.getIntCondoID()); // 상품코드

            arrive_date = arrive_date.replace("-", "");
            leave_date = leave_date.replace("-", "");

            // 예약
            String kumhoUrl = "inter01.asp?ipark_resno=" + ipark_resno + "&area=" + area
                    + "&site=" + site + "&arrive_date=" + arrive_date + "&leave_date=" + leave_date
                    + "&nights_count=" + nights_count + "&groupid=" + Constants.groupId + "&event_div=" + event_div
                    + "&morning_aqua=" + morning_aqua + "&use_name=" + URLEncoder.encode(use_name, "utf-8") + "use_phone=" + use_phone
                    + "&use_cell_phone=" + use_cell_phone + "&morning_div=" + morning_div + "&room_type=" + room_type
                    + "&room_count=" + room_count + "&person_count=" + person_count + "&coupon_year=" + coupon_year
                    + "&coupon_number=" + coupon_number + "&ipark_goodsno=" + ipark_goodsno;

//                    String kumhoUrl = "inter01.asp?ipark_resno=INT2015081804&area=4&site=1&arrive_date=20230915&leave_date=20230917&nights_count=2&groupid=210010&event_div=0&morning_aqua=0&use_name=KMK-%EB%A3%B8%EC%98%A8%EB%A6%AC&use_phone=7469&use_cell_phone=01071050426&morning_div=N&room_type=27A&room_count=1&person_count=5&coupon_year=*&coupon_number=0&ipark_goodsno=0129O01";

            Document document = callKumhoAPI(kumhoUrl);
            // response 처리
            if(document != null){
                String resultCode = document.getElementsByTagName("resultCode").item(0).getChildNodes().item(0).getNodeValue();
                String strSpBookingId = document.getElementsByTagName("reserv_number").item(0).getChildNodes().item(0).getNodeValue();
                String resultMsg = document.getElementsByTagName("resultMsg").item(0).getChildNodes().item(0).getNodeValue();

                // 금호측에 예약이 완료됐으면 우리 DB 상태값 업데이트
                if(resultCode.equals("S")){
                    String result = bookingMapper.updateBooking(intBookingID, "4", strSpBookingId, room_count);
                    if(result.equals("저장완료")){
                        message = "예약완료";
                    }else{
                        message = "예약실패";
                    }
                }else{
                    message = resultMsg;
                }
            }else{
                message = "금호 API 호출 실패";
            }

            logWriter.add("intBookingID : " + intBookingID + " -> " + message);
            logWriter.log(0);
        }catch (Exception e){
            message = "예약 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
            e.printStackTrace();
        }
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 예약 시 잔여 객실 수 조회
//    public int getRemainCount(String fr_date, String to_date, String area, String room_type){
//        int remainCount = 0;
//        try{
//            String kumhoUrl = "inter05.asp?groupid=" + Constants.groupId + "&fr_date=" + fr_date + "&to_date=" + to_date
//                            + "&area=" + area + "&site=" + site + "&room_type=" + room_type;
//
//            Document document = callKumhoAPI(kumhoUrl);
//            if(document != null){
//                NodeList roomList = document.getElementsByTagName("room");
//                for(int i=0; i< roomList.getLength(); i++) {
//                    Node node = roomList.item(i);
//                    if (node.getNodeType() == Node.ELEMENT_NODE) {
//                        Element element = (Element) node;
//
//                        String rdate = xmlUtility.getTagValue("rdate", element).trim();
//                        if(rdate.equals("F")){
//                            remainCount = -1;
//                        }else{
//                            remainCount = Integer.parseInt(xmlUtility.getTagValue("remainCount", element));
//                        }
//                    }
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return remainCount;
//    }

    // 재고 등록 및 수정
    public String updateGoods(String dataType, String fr_date, String to_date, int intRmIdx, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        try{
            String kumhoUrl = "";

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date fromDate = sdf.parse(fr_date);
            Date toDate = sdf.parse(to_date);

            // 조회한 날짜의 기간 확인 (90일이 넘는지)
            long sec = (toDate.getTime() - fromDate.getTime()) / 1000;
            double days =  (sec / (24*60*60));

            Map<String, Object> map = bookingMapper.getRmtypeIDNIntAID(intRmIdx);
            String strRmtypeID = map.get("strRmtypeID").toString();
            int intAID = Integer.parseInt(map.get("intAID").toString());

            String strLocalCode = bookingMapper.getStrLocalCode(intAID, strRmtypeID);

            String strStockDatas = "";

            // 날짜 세팅
            // 90일 이상일 경우
            if(days > 90){
                double roopCount = days / 90;
                roopCount = Math.ceil(((roopCount) * 10)/10.0);

                Date startDate = null;
                Date endDate = null;
                Loop1 :
                for(int i=0; i<roopCount; i++){
                    Calendar cal = Calendar.getInstance();
                    if(i == 0){
                        cal.setTime(fromDate);
                        cal.add(Calendar.DATE, 90); // fromDate + 90일로 세팅
                        endDate = cal.getTime();

                        fr_date = sdf.format(fromDate);
                        to_date = sdf.format(endDate);
                        kumhoUrl = "inter05.asp?groupid=" + Constants.groupId + "&fr_date=" + fr_date + "&to_date=" + to_date +
                                "&area=" + strLocalCode + "&site=" + site + "&room_type=" + strRmtypeID;

                        // 새로운 날짜 세팅
                        cal.setTime(fromDate);
                        cal.add(Calendar.DATE, 90); // fromDate + 90일로 새로운 시작날짜 세팅
                        startDate = cal.getTime();

                        long diff = toDate.getTime() - startDate.getTime();
                        TimeUnit time = TimeUnit.DAYS;
                        long diffDays = time.convert(diff, TimeUnit.MILLISECONDS);

                        if(diffDays > 90){
                            cal.setTime(startDate);
                            cal.add(Calendar.DATE, 90); // endDate = startDate + 90일로 세팅
                            endDate = cal.getTime();
                        }else{
                            cal.setTime(startDate);
                            cal.add(Calendar.DATE, (int) diffDays); // endDate = startDate + toDate까지 남은일자로 세팅
                            endDate = cal.getTime();
                        }
                    }else{
                        fr_date = sdf.format(startDate);
                        to_date = sdf.format(endDate);
                        kumhoUrl = "inter05.asp?groupid=" + Constants.groupId + "&fr_date=" + fr_date + "&to_date=" + to_date +
                                "&area=" + strLocalCode + "&site=" + site + "&room_type=" + strRmtypeID;

                        // 새로운 날짜 세팅
                        cal.setTime(startDate);
                        cal.add(Calendar.DATE, 90); // startDate + 90일로 새로운 시작날짜 세팅
                        startDate = cal.getTime();

                        long diff = toDate.getTime() - startDate.getTime();
                        TimeUnit time = TimeUnit.DAYS;
                        long diffDays = time.convert(diff, TimeUnit.MILLISECONDS);

                        if(diffDays > 90){
                            cal.setTime(startDate);
                            cal.add(Calendar.DATE, 90); // endDate = 새로운 startDate + 90일로 세팅
                            endDate = cal.getTime();

                        }else{
                            cal.setTime(startDate);
                            cal.add(Calendar.DATE, (int) diffDays); // endDate = 새로운 startDate + endDate까지 남은일자로 세팅
                            endDate = cal.getTime();
                        }
                    }

                    // API 호출
                    Document document = callKumhoAPI(kumhoUrl);
                    if(document != null){
                        NodeList roomList = document.getElementsByTagName("room");
                        for(int j=0; j< roomList.getLength(); j++) {
                            Node node = roomList.item(j);
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                Element element = (Element) node;

                                String rdate = xmlUtility.getTagValue("rdate", element).trim();
                                String msg = URLDecoder.decode(xmlUtility.getTagValue("msg", element), "utf-8");
                                if(rdate.equals("F")){
                                    message = msg;
                                    break Loop1;
                                }else{
                                    strRmtypeID = xmlUtility.getTagValue("roomtype", element);

                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    String dateSales = dateFormat.format(sdf.parse(rdate));

                                    int intStock = Integer.parseInt(xmlUtility.getTagValue("remainCount", element));
                                    if(intStock < 0){
                                        intStock = 0;
                                    }
                                    int intOmkStock = intStock;

                                    int intCost = 0, intSales = 0, intExtraA = 0, intExtraB = 0, intExtraC = 0, intOmkSales = 0;

                                    strStockDatas += dateSales + "|^|" + intStock + "|^|" + intCost + "|^|" + intSales + "|^|"
                                            + intExtraA + "|^|" + intExtraC + "|^|" + intExtraB + "|^|" + intOmkStock + "|^|"  + intOmkSales+ "{{|}}";


                                }
                            }
                        }
                        strStockDatas = strStockDatas.substring(0, strStockDatas.length()-5);

                        String result = bookingMapper.updateGoods(intAID, intRmIdx, strStockDatas);
                        String strResult = result.substring(result.length()-4);

                        if(strResult.equals("저장완료")){
                            message = "재고 등록 및 수정 완료";
                        }else{
                            message = "재고 등록 및 수정 실패";
                        }
                    }else{
                        message = "재고 조회 실패";
                        logWriter.add(message);
                    }
                }
            }else{ // 90일 이상이 아닐 경우
                kumhoUrl = "inter05.asp?groupid=" + Constants.groupId + "&fr_date=" + fr_date + "&to_date=" + to_date +
                            "&area=" + strLocalCode + "&site=" + site + "&room_type=" + strRmtypeID;
                // API 호출
                Document document = callKumhoAPI(kumhoUrl);
                if(document != null){
                    NodeList roomList = document.getElementsByTagName("room");
                    for(int j=0; j< roomList.getLength(); j++) {
                        Node node = roomList.item(j);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;

                            String rdate = xmlUtility.getTagValue("rdate", element).trim();
                            String msg = URLDecoder.decode(xmlUtility.getTagValue("msg", element), "utf-8");
                            if(rdate.equals("F")){
                                message = msg;
                                break;
                            }else{
                                strRmtypeID = xmlUtility.getTagValue("roomtype", element);

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                String dateSales = dateFormat.format(sdf.parse(rdate));

                                int intStock = Integer.parseInt(xmlUtility.getTagValue("remainCount", element));
                                int intOmkStock = intStock;

                                int intCost = 0, intSales = 0, intExtraA = 0, intExtraB = 0, intExtraC = 0, intOmkSales = 0;

                                strStockDatas += dateSales + "|^|" + intStock + "|^|" + intCost + "|^|" + intSales + "|^|"
                                        + intExtraA + "|^|" + intExtraC + "|^|" + intExtraB + "|^|" + intOmkStock + "|^|"  + intOmkSales+ "{{|}}";
                            }
                        }
                    }
                    strStockDatas = strStockDatas.substring(0, strStockDatas.length()-5);

                    String result = bookingMapper.updateGoods(intAID, intRmIdx, strStockDatas);
                    String strResult = result.substring(result.length()-4);

                    if(strResult.equals("저장완료")){
                        message = "재고 등록 및 수정 완료";
                    }else{
                        message = "재고 등록 및 수정 실패";
                    }
                }else{
                    message = "재고 조회 실패";
                }
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
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 예약 취소
    public String cancelBooking(String dataType, int intBookingID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        try{
            BookingDto bookingDto = bookingMapper.getBookingByIntBookingID(intBookingID);
            String area = bookingDto.getAccommId(); // 사업장(통영, 화순, 설악, 제주)
            String arrive_date = bookingDto.getCheckInDate(); // 도착일자
            String reserv_year = arrive_date.substring(0, 4);
            String reserv_number = bookingDto.getStrSpBookingId();

            String kumhoUrl = Constants.kumhoUrl + "inter02.asp?area=" + area + "&site=" + site + "&reserv_year=" + reserv_year
                    + "&reserv_number=" + reserv_number;

//            String kumhoUrl = "inter02.asp?area=4&site=1&reserv_year=2023&reserv_number=40154";

            Document document = callKumhoAPI(kumhoUrl);

            if(document != null){
                String resultCode = document.getElementsByTagName("resultCode").item(0).getChildNodes().item(0).getNodeValue();
                String resultMsg = document.getElementsByTagName("resultMsg").item(0).getChildNodes().item(0).getNodeValue();
                if(resultCode.equals("S")){
                    // DB 상태값 변경
                    int result = bookingMapper.updateBookingStatus(intBookingID, "14");
                    if(result > 0){
                        message = "예약 취소 완료";
                    }else{
                        message = "예약 취소 실패";
                    }
                }else{
                    message = resultMsg;
                }
            }else{
                message = "금호 API 호출 실패";
            }

            logWriter.add("intBookingID : " + intBookingID + " -> " + message);
            logWriter.log(0);
        }catch (Exception e){
            message = "예약 취소 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
            e.printStackTrace();
        }
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 예약현황 조회
    public String getReservationStatus(String dataType, int intBookingID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        Map<String, Object> resultMap = new HashMap<>();
        try{
            BookingDto bookingDto = bookingMapper.getBookingByIntBookingID(intBookingID);

            String area = bookingDto.getAccommId(); // 사업장(통영, 화순, 설악, 제주)
            String arrive_date = bookingDto.getCheckInDate(); // 도착일자
            String reserv_year = arrive_date.substring(0, 4);
            String reserv_number = bookingDto.getStrSpBookingId();
//            String area = "4";
//            String arrive_date = "2023-06-10"; // 도착일자
//            String reserv_year = "2023";
//            String reserv_number = "37537";


            String kumhoUrl = "inter06.asp?area=" + area + "&site=" + site + "&reserv_year=" + reserv_year
                    + "&reserv_number=" + reserv_number;

            Document document = callKumhoAPI(kumhoUrl);
            if(document != null){
                String resultCode = document.getElementsByTagName("resultCode").item(0).getChildNodes().item(0).getNodeValue();
                String resultMsg = URLDecoder.decode(document.getElementsByTagName("resultMsg").item(0).getChildNodes().item(0).getNodeValue(), "utf-8");
                if(resultCode.equals("S")){
                    String reservArea = document.getElementsByTagName("area").item(0).getChildNodes().item(0).getNodeValue();
                    String reservNumber = document.getElementsByTagName("reserv_number").item(0).getChildNodes().item(0).getNodeValue();

                    resultMap.put("reservArea", reservArea);
                    resultMap.put("reservNumber", reservNumber);

                    // reusltMsg로 온 데이터들 자르기
                    // <resultMsg>20230610/1/27A/1//N/1/박운주대표/양선경/010-111-1111/010-111-1111/예약현황이 조회되었습니다.</resultMsg>
                    String[] strResult = resultMsg.split("/");
                    String checkInDate = strResult[0]; // 체크인 날짜
                    String stayDays = strResult[1]; // 숙박일 수 
                    String roomType = strResult[2]; // 룸타입
                    String roomCount = strResult[3]; // 객실 수 
                    String personCount = strResult[4]; // 인원
                    String mealYN = strResult[5]; // 조식여부 Y/N
                    String packageDiv = strResult[6]; // 패키지 구분 0 : 객실예약 / 1 : 패키지예약
                    String OrdName = strResult[7]; // 예약자명
                    String recvName = strResult[8]; // 투숙자명
                    String recvTel = strResult[9]; // 투숙자 전화번호
                    String recvPhone = strResult[10]; // 투숙자 휴대폰번호

                    resultMap.put("checkInDate", checkInDate);
                    resultMap.put("stayDays", stayDays);
                    resultMap.put("roomType", roomType);
                    resultMap.put("roomCount", roomCount);
                    resultMap.put("personCount", personCount);
                    resultMap.put("mealYN", mealYN);
                    resultMap.put("packageDiv", packageDiv);
                    resultMap.put("OrdName", OrdName);
                    resultMap.put("recvName", recvName);
                    resultMap.put("recvTel", recvTel);
                    resultMap.put("recvPhone", recvPhone);

                }else{
                    message = resultMsg;
                }
            }else{
                message = "예약 조회 실패";
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

        return commonFunction.makeReturn(dataType, statusCode, message, resultMap);
    }

    // 예약 대사자료 조회
    public String getReservations(String dataType, String fr_date, String to_date ,HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        MultiValueMap<String, Map> resultMap = new LinkedMultiValueMap<>();
        try{
            String kumhoUrl = "inter07.asp?groupid=" + Constants.groupId + "&fr_date=" + fr_date + "&to_date=" + to_date;

            Document document = callKumhoAPI(kumhoUrl);
            if(document != null){
                String resultCode = document.getElementsByTagName("resultCode").item(0).getChildNodes().item(0).getNodeValue();
                String resultMsg = URLDecoder.decode(document.getElementsByTagName("resultMsg").item(0).getChildNodes().item(0).getNodeValue(), "utf-8");
                if(resultCode.equals("S")){
                    NodeList reservList = document.getElementsByTagName("rserve");
                    for(int i=0; i< reservList.getLength(); i++){
                        Map<String, Object> reservMap = new HashMap<>();
                        Node node = reservList.item(i);
                        if(node.getNodeType() == Node.ELEMENT_NODE){
                            Element element = (Element) node;

                            // DB에 정리해서 가져와야할듯...
                            String area = xmlUtility.getTagValue("ps_area", element);
                            if(area.equals("1")){
                                area = "통영";
                            }else if(area.equals("2")){
                                area = "화순";
                            }else if(area.equals("3")){
                                area = "설악";
                            }else if(area.equals("4")){
                                area = "제주";
                            }
                            reservMap.put("area", area);

                            reservMap.put("reservYear", xmlUtility.getTagValue("ps_reserv_year", element));
                            reservMap.put("reservNumber", xmlUtility.getTagValue("ps_reserv_number", element));

                            String reservStatus = xmlUtility.getTagValue("ps_reserv_status", element);
                            if(reservStatus.equals("R")){
                                reservStatus = "예약";
                            }else if(reservStatus.equals("C")){
                                reservStatus = "취소";
                            }else if(reservStatus.equals("I")){
                                reservStatus = "사용";
                            }else if(reservStatus.equals("N")){
                                reservStatus = "노쇼";
                            }
                            reservMap.put("reservStatus", reservStatus);

                            reservMap.put("roomType", xmlUtility.getTagValue("ps_room_type", element));
                            reservMap.put("modifyDate", xmlUtility.getTagValue("ps_modify_date", element));
                            reservMap.put("arriveDate", xmlUtility.getTagValue("ps_arrive_date", element));
                            reservMap.put("leaveDate", xmlUtility.getTagValue("ps_leave_date", element));
                            reservMap.put("reservDate", xmlUtility.getTagValue("ps_reserv_date", element));
                            reservMap.put("strSpBookingId", xmlUtility.getTagValue("ps_ipark_resno", element));
                        }
                        resultMap.add("reservMap", reservMap);
                    }

                }else if(resultCode.equals("F")){
                    message = resultMsg;

                }else if(resultCode.equals("0")){
                    message = resultMsg;
                }
            }else{
                message = "금호 API 호출 실패";
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

        return commonFunction.makeReturn(dataType, statusCode, message, resultMap);
    }

    // 금호 api 호출
    public Document callKumhoAPI(String kumhoUrl){
        Document document = null;
        String method = "";
        String strUrl = "";
        String message = "";
        long startTime = System.currentTimeMillis();
        try{
            URL url = new URL(Constants.kumhoUrl + kumhoUrl);
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
                message = "금호 API 호출 실패";
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
            e.printStackTrace();
        }

        return document;
    }




}
