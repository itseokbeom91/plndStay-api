package com.example.stay.accommodation.kumho.service;

import com.example.stay.accommodation.kumho.mapper.KumhoMapper;
import com.example.stay.common.util.*;
import com.example.stay.openMarket.common.dto.BookingDto;
import com.example.stay.openMarket.common.dto.RsvStayDto;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service("kumho.BookingService")
public class BookingService extends CommonFunction{

    @Autowired
    private KumhoMapper kumhoMapper;

    @Autowired
    private XmlUtility xmlUtility;

    CommonFunction commonFunction = new CommonFunction();

    private static String site = "1"; // 현재 무조건 1

    // 예약
    public String createBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        try{
            RsvStayDto rsvStayDto = kumhoMapper.getReservation(intRsvID);

            int intRmIdx = rsvStayDto.getIntRmIdx();
            String strRmtypeID = rsvStayDto.getStrRmtypeID();
            String ipark_resno = String.format("%015d", intRsvID); // 예약번호
            String area = rsvStayDto.getStrLocalCode(); // 사업장(통영, 화순, 설악, 제주)

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String arrive_date = sdf.format(rsvStayDto.getDateCheckIn()); // 도착일자
            String leave_date = sdf.format(rsvStayDto.getDateCheckOut()); // 퇴실일자

            long nights_count = 0; // 숙박일수
            Date inDate = rsvStayDto.getDateCheckIn();
            Date outDate = rsvStayDto.getDateCheckOut();
            nights_count = ((outDate.getTime() - inDate.getTime()) / 1000) / (24*60*60);

            String event_div = ""; // 객실예약인지 패키지인지
            String morning_aqua = "";  // 패키지 상품일 경우 패키지 코드
            String strMapCode = rsvStayDto.getStrMapCode();
            // 룸온리 상품일 경우
            if(strMapCode.equals("RMONLY")){
                event_div = "0";
                morning_aqua = "";
            }else{ // 패키지 상품일 경우
                event_div = "1";
                morning_aqua = strMapCode;
            }

            String use_name = rsvStayDto.getStrRcvName(); // 사용자명
            String use_phone = rsvStayDto.getStrRcvPhone().replace("-",""); // 사용자 전화번호
            String use_cell_phone = use_phone; // 사용자 휴대폰번호

            String morning_div = ""; // 조식 여부
            int breakfastYn = kumhoMapper.getBreakfastYn(intRmIdx);
            if(breakfastYn == 1){
                morning_div = "Y";
            }else{
                morning_div = "N";
            }

            String room_type = strRmtypeID; // 객실타입
            int room_count = rsvStayDto.getIntRmCnt(); // 객실 수
            int person_count = rsvStayDto.getIntQuantityA() + rsvStayDto.getIntQuantityC() + rsvStayDto.getIntQuantityB(); // 인원
            String coupon_year = "*"; // 쿠폰발행년도(무조건 *)
            int coupon_number = 0; // 쿠폰번호(무조건 0)
            String ipark_goodsno = Integer.toString(rsvStayDto.getIntAID()); // 상품코드

            // 예약
            String kumhoUrl = "inter01.asp?ipark_resno=" + ipark_resno + "&area=" + area
                    + "&site=" + site + "&arrive_date=" + arrive_date + "&leave_date=" + leave_date
                    + "&nights_count=" + nights_count + "&groupid=" + Constants.groupId + "&event_div=" + event_div
                    + "&morning_aqua=" + morning_aqua + "&use_name=" + URLEncoder.encode(use_name, "utf-8") + "&use_phone=" + use_phone
                    + "&use_cell_phone=" + use_cell_phone + "&morning_div=" + morning_div + "&room_type=" + room_type
                    + "&room_count=" + room_count + "&person_count=" + person_count + "&coupon_year=" + coupon_year
                    + "&coupon_number=" + coupon_number + "&ipark_goodsno=" + ipark_goodsno;

//                    String kumhoUrl = "inter01.asp?ipark_resno=INT2015081804&area=4&site=1&arrive_date=20230915&leave_date=20230917&nights_count=2&groupid=210010&event_div=0&morning_aqua=0&use_name=KMK-%EB%A3%B8%EC%98%A8%EB%A6%AC&use_phone=7469&use_cell_phone=01071050426&morning_div=N&room_type=27A&room_count=1&person_count=5&coupon_year=*&coupon_number=0&ipark_goodsno=0129O01";

            Document document = callKumhoAPI(kumhoUrl);
            // response 처리
            if(document != null){
                String resultCode = document.getElementsByTagName("resultCode").item(0).getChildNodes().item(0).getNodeValue();
                String strRsvRmNum = document.getElementsByTagName("reserv_number").item(0).getChildNodes().item(0).getNodeValue();
                String resultMsg = document.getElementsByTagName("resultMsg").item(0).getChildNodes().item(0).getNodeValue();

                // 금호측에 예약이 완료됐으면 우리 DB 상태값 업데이트
                if(resultCode.equals("S")){
                    // 위약금 규정 생성
                    String strPenaltyDatas = makeCancelRules(rsvStayDto);

                    String result = kumhoMapper.updateRsvStay(intRsvID, "4", strRsvRmNum, strPenaltyDatas);
                    if(result.equals("저장완료")){
                        message = "예약완료";
                    }else{
                        message = "예약실패";
                    }
                }else{
                    message = URLDecoder.decode(resultMsg, "utf-8");
                }
            }else{
                message = "금호 API 호출 실패";
            }

            logWriter.add("intRsvID : " + intRsvID + " -> " + message);
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

    // 재고 등록 및 수정
    public String updateRoomStock(String dataType, String strFromDate, String strToDate, int intRmIdx, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        try{
            String kumhoUrl = "";

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date fromDate = sdf.parse(strFromDate);
            Date toDate = sdf.parse(strToDate);

            // 조회한 날짜의 기간 확인 (90일이 넘는지)
            long sec = (toDate.getTime() - fromDate.getTime()) / 1000;
            double days =  (sec / (24*60*60));

            Map<String, Object> map = kumhoMapper.getRmtypeInfo(intRmIdx);
            String strRmtypeID = map.get("strRmtypeID").toString();
            int intAID = Integer.parseInt(map.get("intAID").toString());
            String strLocalCode = map.get("strLocalCode").toString();

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

                        strFromDate = sdf.format(fromDate);
                        strToDate = sdf.format(endDate);
                        kumhoUrl = "inter05.asp?groupid=" + Constants.groupId + "&fr_date=" + strFromDate + "&to_date=" + strToDate +
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
                        strFromDate = sdf.format(startDate);
                        strToDate = sdf.format(endDate);
                        kumhoUrl = "inter05.asp?groupid=" + Constants.groupId + "&fr_date=" + strFromDate + "&to_date=" + strToDate +
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
                                    message = URLDecoder.decode(msg, "utf-8");
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

                                    // 금호는 가격을 안줌
                                    int intCost = 0, intSales = 0, intExtraA = 0, intExtraB = 0, intExtraC = 0;

                                    strStockDatas += dateSales + "|^|" + intStock + "|^|" + intCost + "|^|" + intSales + "|^|"
                                            + intExtraA + "|^|" + intExtraC + "|^|" + intExtraB + "|^|" + intOmkStock + "|^|" + "{{|}}";


                                }
                            }
                        }
                        strStockDatas = strStockDatas.substring(0, strStockDatas.length()-5);

                        String result = kumhoMapper.updateStock(intAID, intRmIdx, strStockDatas);
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
                kumhoUrl = "inter05.asp?groupid=" + Constants.groupId + "&fr_date=" + strFromDate + "&to_date=" + strToDate +
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
                                message = URLDecoder.decode(msg, "utf-8");
                                logWriter.add(message);
                                break;
                            }else{
//                                strRmtypeID = xmlUtility.getTagValue("roomtype", element);

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                String dateSales = dateFormat.format(sdf.parse(rdate));

                                int intStock = Integer.parseInt(xmlUtility.getTagValue("remainCount", element));
                                int intOmkStock = intStock;

                                // 금호는 가격을 안줌
                                int intCost = 0, intSales = 0, intExtraA = 0, intExtraB = 0, intExtraC = 0;

                                strStockDatas += dateSales + "|^|" + intStock + "|^|" + intCost + "|^|" + intSales + "|^|"
                                        + intExtraA + "|^|" + intExtraC + "|^|" + intExtraB + "|^|" + intOmkStock + "|^|" + "{{|}}";
                            }
                        }
                    }
                    strStockDatas = strStockDatas.substring(0, strStockDatas.length()-5);

                    String result = kumhoMapper.updateStock(intAID, intRmIdx, strStockDatas);
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
    public String cancelBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        try{
            RsvStayDto rsvStayDto = kumhoMapper.getReservation(intRsvID);

            String area = rsvStayDto.getStrLocalCode(); // 사업장(통영, 화순, 설악, 제주)

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String arrive_date = sdf.format(rsvStayDto.getDateCheckIn()); // 도착일자

            String reserv_year = arrive_date.substring(0, 4);
            String reserv_number = rsvStayDto.getStrRsvRmNum();

            String kumhoUrl = "inter02.asp?area=" + area + "&site=" + site + "&reserv_year=" + reserv_year
                    + "&reserv_number=" + reserv_number;

//            String kumhoUrl = "inter02.asp?area=4&site=1&reserv_year=2023&reserv_number=40154";

            Document document = callKumhoAPI(kumhoUrl);

            if(document != null){
                String resultCode = document.getElementsByTagName("resultCode").item(0).getChildNodes().item(0).getNodeValue();
                String resultMsg = document.getElementsByTagName("resultMsg").item(0).getChildNodes().item(0).getNodeValue();
                if(resultCode.equals("S")){
                    // DB 상태값 변경
                    String strRsvRmNum = rsvStayDto.getStrRsvRmNum();
                    String result = kumhoMapper.updateRsvStay(intRsvID, "5", strRsvRmNum, "");
                    if(result.equals("저장완료")){
                        message = "예약 취소 완료";
                    }else{
                        message = "예약 취소 실패";
                    }
                }else{
                    message = URLDecoder.decode(resultMsg, "utf-8");
                }
            }else{
                message = "금호 API 호출 실패";
            }

            logWriter.add("intRsvID : " + intRsvID + " -> " + message);
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
    public String getBookingInfo(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        Map<String, Object> resultMap = new HashMap<>();
        try{
            // 예약정보 조회
            RsvStayDto rsvStayDto = kumhoMapper.getReservation(intRsvID);
//
            String area = rsvStayDto.getStrLocalCode(); // 사업장(통영, 화순, 설악, 제주)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String arrive_date = sdf.format(rsvStayDto.getDateCheckIn()); // 도착일자
            String reserv_year = arrive_date.substring(0, 4);
            String reserv_number = rsvStayDto.getStrRsvRmNum();

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
                    String dateCheckIn = strResult[0]; // 체크인 날짜
                    String stayDays = strResult[1]; // 숙박일 수
                    String strRmtypeID = strResult[2]; // 룸타입
                    String intRmCnt = strResult[3]; // 객실 수
                    String intQuantity = strResult[4]; // 인원
                    String mealYN = strResult[5]; // 조식여부 Y/N
                    String packageDiv = strResult[6]; // 패키지 구분 0 : 객실예약 / 1 : 패키지예약
                    String strOrdName = strResult[7]; // 예약자명
                    String strRcvName = strResult[8]; // 투숙자명
                    String strRcvTel = strResult[9]; // 투숙자 전화번호
                    String strRcvPhone = strResult[10]; // 투숙자 휴대폰번호

                    resultMap.put("dateCheckIn", dateCheckIn);
                    resultMap.put("stayDays", stayDays);
                    resultMap.put("strRmtypeID", strRmtypeID);
                    resultMap.put("intRmCnt", intRmCnt);
                    resultMap.put("intQuantity", intQuantity);
                    resultMap.put("mealYN", mealYN);
                    resultMap.put("packageDiv", packageDiv);
                    resultMap.put("strOrdName", strOrdName);
                    resultMap.put("strRcvName", strRcvName);
                    resultMap.put("strRcvTel", strRcvTel);
                    resultMap.put("strRcvPhone", strRcvPhone);

                    message = "예약 조회 완료";

                }else{
                    message = URLDecoder.decode(resultMsg, "utf-8");
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
    public String getBookingList(String dataType, String strFromDate, String strToDate ,HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        JSONArray resultJsonArr = new JSONArray();
        try{
            for(String groupId : Constants.groupIdArr){
                JSONObject rsvJson = new JSONObject();
                rsvJson.put("groupId", groupId);

                JSONArray reservations = new JSONArray();

                String kumhoUrl = "inter07.asp?groupid=" + groupId + "&fr_date=" + strFromDate + "&to_date=" + strToDate;

                Document document = callKumhoAPI(kumhoUrl);
                if(document != null){
                    String resultCode = document.getElementsByTagName("resultCode").item(0).getChildNodes().item(0).getNodeValue();
                    String resultMsg = URLDecoder.decode(document.getElementsByTagName("resultMsg").item(0).getChildNodes().item(0).getNodeValue(), "utf-8");

                    if(resultCode.equals("S")) {
                        NodeList rcvList = document.getElementsByTagName("rserve");
                        for (int i = 0; i < rcvList.getLength(); i++) {
                            JSONObject jsonObject = new JSONObject();

                            Node node = rcvList.item(i);
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                Element element = (Element) node;

                                // 금호
                                String area = xmlUtility.getTagValue("ps_area", element);
                                if (area.equals("1")) {
                                    area = "통영";
                                } else if (area.equals("2")) {
                                    area = "화순";
                                } else if (area.equals("3")) {
                                    area = "설악";
                                } else if (area.equals("4")) {
                                    area = "제주";
                                } else if (area.equals("5")) {
                                    area = "아산";
                                }
                                jsonObject.put("strLocalCode", area);

                                jsonObject.put("strRcvYear", xmlUtility.getTagValue("ps_reserv_year", element));
                                jsonObject.put("strRsvRmNum", xmlUtility.getTagValue("ps_reserv_number", element));

                                String strStatusCode = xmlUtility.getTagValue("ps_reserv_status", element);
                                if (strStatusCode.equals("R")) {
                                    strStatusCode = "예약";
                                } else if (strStatusCode.equals("C")) {
                                    strStatusCode = "취소";
                                } else if (strStatusCode.equals("I")) {
                                    strStatusCode = "사용";
                                } else if (strStatusCode.equals("N")) {
                                    strStatusCode = "노쇼";
                                }
                                jsonObject.put("strStatusCode", strStatusCode);

                                jsonObject.put("strRmtypeID", xmlUtility.getTagValue("ps_room_type", element));
                                jsonObject.put("dateModified", xmlUtility.getTagValue("ps_modify_date", element));
                                jsonObject.put("dateCheckIn", xmlUtility.getTagValue("ps_arrive_date", element));
                                jsonObject.put("dateCheckOut", xmlUtility.getTagValue("ps_leave_date", element));
                                jsonObject.put("strRcvDate", xmlUtility.getTagValue("ps_reserv_date", element));
                                jsonObject.put("kumhoIntRsvID", xmlUtility.getTagValue("ps_ipark_resno", element));

                                reservations.add(jsonObject);
                            }
                        }

                    }
                }else{
                    message = "금호 API 호출 실패";
                }

                rsvJson.put("rsv", reservations);
                resultJsonArr.add(rsvJson);
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

        return commonFunction.makeReturn(dataType, statusCode, message, resultJsonArr);
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
                logWriter.add("responseCode : " + conn.getResponseCode());
                message = "금호 API 호출 실패";
            }

            conn.disconnect();

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
