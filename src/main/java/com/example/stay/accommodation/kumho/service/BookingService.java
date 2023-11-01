package com.example.stay.accommodation.kumho.service;

import com.example.stay.accommodation.kumho.mapper.KumhoMapper;
import com.example.stay.common.mapper.CommonAcmMapper;
import com.example.stay.common.util.*;
import com.example.stay.openMarket.common.dto.BookingDto;
import com.example.stay.openMarket.common.dto.RsvStayDto;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service("kumho.BookingService")
public class BookingService extends CommonFunction{

    @Autowired
    private KumhoMapper kumhoMapper;

    @Autowired
    private CommonAcmMapper commonAcmMapper;

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
            int apiFail = 0;
            String strRsvRmNum = "";
            if(document != null){
                String resultCode = document.getElementsByTagName("resultCode").item(0).getChildNodes().item(0).getNodeValue();
                String resultMsg = document.getElementsByTagName("resultMsg").item(0).getChildNodes().item(0).getNodeValue();

                if(resultCode.equals("S")){
                    strRsvRmNum = document.getElementsByTagName("reserv_number").item(0).getChildNodes().item(0).getNodeValue();
                }else{
                    apiFail ++;
                    logWriter.add(URLDecoder.decode(resultMsg, "utf-8"));
                }
            }else{
                apiFail++;
                logWriter.add("금호 API 호출 실패");
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            int intAID = rsvStayDto.getIntAID();

            String strRmNumDatas = "";
            for (int i = 0; i < nights_count; i++) {
                // 체크인 날짜 구하기
                Calendar cal = Calendar.getInstance();
                cal.setTime(inDate);
                cal.add(Calendar.DATE, i);
                Date dateCheckIn = cal.getTime();

                String strCheckInDate = simpleDateFormat.format(dateCheckIn);

                Map<String, Integer> priceMap = kumhoMapper.getPrice(intAID, intRmIdx, strCheckInDate);
                int intAcmCost = priceMap.get("moneyCost");
                int intAcmSales = priceMap.get("moneySales");

                for(int j=1; j<=room_count; j++){
                    strRmNumDatas += j + "|^|" + strCheckInDate +  "|^|" + strRsvRmNum + "|^|" + strMapCode + "|^|" + intAcmCost + "|^|" + intAcmSales + "|^|C24|^|" + 148 + "{{|}}";
                }

            }
            strRmNumDatas = strRmNumDatas.substring(0, strRmNumDatas.length()-5);

            // 위약금 규정 생성
//            String strPenaltyDatas = makeCancelRules(rsvStayDto);
            String strPenaltyDatas = "";

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("intRsvID", intRsvID);
            dataMap.put("strStatusCode", Constants.rsvStatus_rsv_complete);
            dataMap.put("strRmNumDatas", strRmNumDatas);
            dataMap.put("strPenaltyDatas", strPenaltyDatas);

            String strProcedure = commonFunction.makeStrProcedure("spGW_RSV_STAY_UPDATE_PROCESS", dataMap);

            // DB update & insert
            String result = kumhoMapper.updateRsvStay(intRsvID, Constants.rsvStatus_rsv_complete, strRmNumDatas, strPenaltyDatas);

            String strContentCode = "200";
            // 예약 api 실패 있는 경우
            if(apiFail != 0) {
                strContentCode = "500";
                message = "예약 실패";

                if (result.equals("저장완료")) {
                    message += "\nDB 저장 완료";

                } else {
                    message += "\nDB 저장 실패";
                }
            }else{ // 예약 api 실패 없는 경우
                if (result.equals("저장완료")) {
                    message = "예약완료";
                } else {
                    message = "예약 완료 / DB 저장 실패";
                }
            }
            // api history
            commonAcmMapper.insertRsvStayHistory(intRsvID, "C24", "[" + strContentCode +"]" + Constants.rsv_history_rsv, strProcedure, "", 148);

            logWriter.add(message);
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

    // 예약 - 날짜로
    public String createBookingByDate(String dataType, int intRsvID, String strDate, HttpServletRequest httpServletRequest){
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
            String arrive_date = strDate.replace("-", ""); // 체크인 일자

            // 체크아웃 날짜 구하기
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(rsvStayDto.getDateCheckOut());
            calendar.add(Calendar.DATE, 1);
            Date dateCheckOut = calendar.getTime();

            String leave_date = sdf.format(dateCheckOut); // 체크아웃 일자

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
            int apiFail = 0;
            String strRsvRmNum = "";
            if(document != null){
                String resultCode = document.getElementsByTagName("resultCode").item(0).getChildNodes().item(0).getNodeValue();
                String resultMsg = document.getElementsByTagName("resultMsg").item(0).getChildNodes().item(0).getNodeValue();

                if(resultCode.equals("S")){
                    strRsvRmNum = document.getElementsByTagName("reserv_number").item(0).getChildNodes().item(0).getNodeValue();
                }else{
                    apiFail ++;
                    logWriter.add(URLDecoder.decode(resultMsg, "utf-8"));
                }
            }else{
                apiFail++;
                logWriter.add("금호 API 호출 실패");
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            int intAID = rsvStayDto.getIntAID();

            String strRmNumDatas = "";
            for (int i = 0; i < nights_count; i++) {
                String strCheckInDate = simpleDateFormat.format(strDate);

                Map<String, Integer> priceMap = kumhoMapper.getPrice(intAID, intRmIdx, strCheckInDate);
                int intAcmCost = priceMap.get("moneyCost");
                int intAcmSales = priceMap.get("moneySales");

                for(int j=1; j<=room_count; j++){
                    strRmNumDatas += j + "|^|" + strCheckInDate +  "|^|" + strRsvRmNum + "|^|" + strMapCode + "|^|" + intAcmCost + "|^|" + intAcmSales + "|^|C24|^|" + 148 + "{{|}}";
                }

            }
            strRmNumDatas = strRmNumDatas.substring(0, strRmNumDatas.length()-5);

            // 위약금 규정 생성
            String strPenaltyDatas = makeCancelRules(rsvStayDto);

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("intRsvID", intRsvID);
            dataMap.put("strStatusCode", Constants.rsvStatus_rsv_complete);
            dataMap.put("strRmNumDatas", strRmNumDatas);
            dataMap.put("strPenaltyDatas", strPenaltyDatas);

            String strProcedure = commonFunction.makeStrProcedure("spGW_RSV_STAY_UPDATE_PROCESS", dataMap);

            // DB update & insert
            String result = kumhoMapper.updateRsvStay(intRsvID, Constants.rsvStatus_rsv_complete, strRmNumDatas, strPenaltyDatas);

            String strContentCode = "200";
            // 예약 api 실패 있는 경우
            if(apiFail != 0) {
                strContentCode = "500";
                message = "예약 실패";

                if (result.equals("저장완료")) {
                    message += "\nDB 저장 완료";

                } else {
                    message += "\nDB 저장 실패";
                }
            }else{ // 예약 api 실패 없는 경우
                if (result.equals("저장완료")) {
                    message = "예약완료";
                } else {
                    message = "예약 완료 / DB 저장 실패";
                }
            }
            // api history
            commonAcmMapper.insertRsvStayHistory(intRsvID, "C24", "[" + strContentCode +"]" + Constants.rsv_history_rsv, strProcedure, "", 148);

            logWriter.add(message);
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
    public String updateRoomStock(String dataType, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            List<Map<String, Object>> mappingMapList = kumhoMapper.getMappingInfo();

            int intRmIdx = 0;
            String strMinDate = "";
            String strMaxDate = "";
            String strStockDatas = "";
            int intFailCnt = 0;
            String failRmIdx = "";
            for(Map<String, Object> mappingMap : mappingMapList){
                Map<String, Object> map = mappingMap;

                intRmIdx = Integer.parseInt(map.get("intRmIdx").toString());
                strMinDate = map.get("minDate").toString();
                strMaxDate = map.get("maxDate").toString();
                int intAID = Integer.parseInt(map.get("intAID").toString());
                String strRmtypeID = map.get("strRmCode").toString();
                String strLocalCode = map.get("strLocalCode").toString();

                String kumhoUrl = "";

                Date fromDate = simpleDateFormat.parse(strMinDate);
                Date toDate = simpleDateFormat.parse(strMaxDate);

                // 조회한 날짜의 기간 확인 (90일이 넘는지)
                int diffDay = (int) (((toDate.getTime() - fromDate.getTime()) / 1000) / (24*60*60));

                // 날짜 세팅
                // 90일 이상일 경우
                if(diffDay > 90){
                    int roopCount = diffDay / 90;
                    int remainder = diffDay % 90;
                    if(remainder != 0){
                        roopCount += 1;
                    }

                    Date dateStart = null;
                    Date dateEnd = null;
                    Loop1 :
                    for(int i=0; i<roopCount; i++){
                        Calendar cal = Calendar.getInstance();
                        if(i == 0){
                            cal.setTime(fromDate);
                            cal.add(Calendar.DATE, 90); // fromDate + 90일로 세팅
                            dateEnd = cal.getTime();

                            strMinDate = sdf.format(fromDate);
                            strMaxDate = sdf.format(dateEnd);
                            kumhoUrl = "inter05.asp?groupid=" + Constants.groupId + "&fr_date=" + strMinDate + "&to_date=" + strMaxDate +
                                    "&area=" + strLocalCode + "&site=" + site + "&room_type=" + strRmtypeID;

                            // 새로운 날짜 세팅
                            cal.setTime(fromDate);
                            cal.add(Calendar.DATE, 90); // fromDate + 90일로 새로운 시작날짜 세팅
                            dateStart = cal.getTime();

                            long diff = toDate.getTime() - dateStart.getTime();
                            TimeUnit time = TimeUnit.DAYS;
                            long diffDays = time.convert(diff, TimeUnit.MILLISECONDS);

                            if(diffDays > 90){ // 90일치 한 번 호출하고 난 후에도 maxDate까지 90일 이상 남았을 경우
                                cal.setTime(dateStart);
                                cal.add(Calendar.DATE, 90); // dateEnd = dateStart + 90일로 세팅
                                dateEnd = cal.getTime();
                            }else{
                                cal.setTime(dateStart);
                                cal.add(Calendar.DATE, (int) diffDays); // dateEnd = dateStart + toDate까지 남은일자로 세팅
                                dateEnd = cal.getTime();
                            }
                        }else{
                            strMinDate = sdf.format(dateStart);
                            strMaxDate = sdf.format(dateEnd);
                            kumhoUrl = "inter05.asp?groupid=" + Constants.groupId + "&fr_date=" + strMinDate + "&to_date=" + strMaxDate +
                                    "&area=" + strLocalCode + "&site=" + site + "&room_type=" + strRmtypeID;

                            // 새로운 날짜 세팅
                            cal.setTime(dateStart);
                            cal.add(Calendar.DATE, 90); // dateStart + 90일로 새로운 시작날짜 세팅
                            dateStart = cal.getTime();

                            long diff = toDate.getTime() - dateStart.getTime();
                            TimeUnit time = TimeUnit.DAYS;
                            long diffDays = time.convert(diff, TimeUnit.MILLISECONDS);

                            if(diffDays > 90){
                                cal.setTime(dateStart);
                                cal.add(Calendar.DATE, 90); // dateEnd = 새로운 dateStart + 90일로 세팅
                                dateEnd = cal.getTime();

                            }else{
                                cal.setTime(dateStart);
                                cal.add(Calendar.DATE, (int) diffDays); // dateEnd = 새로운 dateStart + dateEnd까지 남은일자로 세팅
                                dateEnd = cal.getTime();
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

                        }else{
                            message = "재고 조회 실패";
                            logWriter.add(message);
                        }
                    }
                }else{ // 90일 이상이 아닐 경우
                    kumhoUrl = "inter05.asp?groupid=" + Constants.groupId + "&fr_date=" + strMinDate + "&to_date=" + strMaxDate +
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
                    }else{
                        message = "재고 조회 실패";
                    }
                }

                strStockDatas = strStockDatas.substring(0, strStockDatas.length()-5);

//                String result = commonAcmMapper.updateGoods(intAID, intRmIdx, strStockDatas);
                String result = kumhoMapper.updateStock(intAID, intRmIdx, strStockDatas);
                String strResult = result.substring(result.length()-4);
                if(!strResult.equals("저장완료")){
                    intFailCnt ++;
                    failRmIdx += intRmIdx + " ";
                }
            } // for문 끝

            if(intFailCnt == 0){
                message = "재고 등록 및 수정 완료";
            }else{
                message = "재고 등록 및 수정 실패 : " + failRmIdx;
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

    // 재고 등록 및 수정
    public String updateRoomStockByIntAID(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            List<Map<String, Object>> mappingMapList = kumhoMapper.getMappingInfoByIntAID(intAID);

            int intRmIdx = 0;
            String strMinDate = "";
            String strMaxDate = "";
            String strStockDatas = "";
            int intFailCnt = 0;
            String failRmIdx = "";
            for(Map<String, Object> mappingMap : mappingMapList){
                Map<String, Object> map = mappingMap;

                intRmIdx = Integer.parseInt(map.get("intRmIdx").toString());
                strMinDate = map.get("minDate").toString();
                strMaxDate = map.get("maxDate").toString();
                String strRmtypeID = map.get("strRmCode").toString();
                String strLocalCode = map.get("strLocalCode").toString();

                String kumhoUrl = "";

                Date fromDate = simpleDateFormat.parse(strMinDate);
                Date toDate = simpleDateFormat.parse(strMaxDate);

                // 조회한 날짜의 기간 확인 (90일이 넘는지)
                int diffDay = (int) (((toDate.getTime() - fromDate.getTime()) / 1000) / (24*60*60));

                // 날짜 세팅
                // 90일 이상일 경우
                if(diffDay > 90){
                    int roopCount = diffDay / 90;
                    int remainder = diffDay % 90;
                    if(remainder != 0){
                        roopCount += 1;
                    }

                    Date dateStart = null;
                    Date dateEnd = null;
                    Loop1 :
                    for(int i=0; i<roopCount; i++){
                        Calendar cal = Calendar.getInstance();
                        if(i == 0){
                            cal.setTime(fromDate);
                            cal.add(Calendar.DATE, 90); // fromDate + 90일로 세팅
                            dateEnd = cal.getTime();

                            strMinDate = sdf.format(fromDate);
                            strMaxDate = sdf.format(dateEnd);
                            kumhoUrl = "inter05.asp?groupid=" + Constants.groupId + "&fr_date=" + strMinDate + "&to_date=" + strMaxDate +
                                    "&area=" + strLocalCode + "&site=" + site + "&room_type=" + strRmtypeID;

                            // 새로운 날짜 세팅
                            cal.setTime(fromDate);
                            cal.add(Calendar.DATE, 90); // fromDate + 90일로 새로운 시작날짜 세팅
                            dateStart = cal.getTime();

                            long diff = toDate.getTime() - dateStart.getTime();
                            TimeUnit time = TimeUnit.DAYS;
                            long diffDays = time.convert(diff, TimeUnit.MILLISECONDS);

                            if(diffDays > 90){ // 90일치 한 번 호출하고 난 후에도 maxDate까지 90일 이상 남았을 경우
                                cal.setTime(dateStart);
                                cal.add(Calendar.DATE, 90); // dateEnd = dateStart + 90일로 세팅
                                dateEnd = cal.getTime();
                            }else{
                                cal.setTime(dateStart);
                                cal.add(Calendar.DATE, (int) diffDays); // dateEnd = dateStart + toDate까지 남은일자로 세팅
                                dateEnd = cal.getTime();
                            }
                        }else{
                            strMinDate = sdf.format(dateStart);
                            strMaxDate = sdf.format(dateEnd);
                            kumhoUrl = "inter05.asp?groupid=" + Constants.groupId + "&fr_date=" + strMinDate + "&to_date=" + strMaxDate +
                                    "&area=" + strLocalCode + "&site=" + site + "&room_type=" + strRmtypeID;

                            // 새로운 날짜 세팅
                            cal.setTime(dateStart);
                            cal.add(Calendar.DATE, 90); // dateStart + 90일로 새로운 시작날짜 세팅
                            dateStart = cal.getTime();

                            long diff = toDate.getTime() - dateStart.getTime();
                            TimeUnit time = TimeUnit.DAYS;
                            long diffDays = time.convert(diff, TimeUnit.MILLISECONDS);

                            if(diffDays > 90){
                                cal.setTime(dateStart);
                                cal.add(Calendar.DATE, 90); // dateEnd = 새로운 dateStart + 90일로 세팅
                                dateEnd = cal.getTime();

                            }else{
                                cal.setTime(dateStart);
                                cal.add(Calendar.DATE, (int) diffDays); // dateEnd = 새로운 dateStart + dateEnd까지 남은일자로 세팅
                                dateEnd = cal.getTime();
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

                        }else{
                            message = "재고 조회 실패";
                            logWriter.add(message);
                        }
                    }
                }else{ // 90일 이상이 아닐 경우
                    kumhoUrl = "inter05.asp?groupid=" + Constants.groupId + "&fr_date=" + strMinDate + "&to_date=" + strMaxDate +
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
                    }else{
                        message = "재고 조회 실패";
                    }
                }

                strStockDatas = strStockDatas.substring(0, strStockDatas.length()-5);

//                String result = commonAcmMapper.updateGoods(intAID, intRmIdx, strStockDatas);
                String result = kumhoMapper.updateStock(intAID, intRmIdx, strStockDatas);
                String strResult = result.substring(result.length()-4);
                if(!strResult.equals("저장완료")){
                    intFailCnt ++;
                    failRmIdx += intRmIdx + " ";
                }
            } // for문 끝

            if(intFailCnt == 0){
                message = "재고 등록 및 수정 완료";
            }else{
                message = "재고 등록 및 수정 실패 : " + failRmIdx;
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

//    @Async
//    public String updateRoomStock(int intAID, int intRmIdx, String strDateMapping, String strLocalCode, String strRmtypeID){
//        int intFailCount = 0;
//        String strFail = "";
//        try{
//            String kumhoUrl = "inter05.asp?groupid=" + Constants.groupId + "&fr_date=" + strDateMapping + "&to_date=" + strDateMapping +
//                    "&area=" + strLocalCode + "&site=" + site + "&room_type=" + strRmtypeID;
//            // API 호출
//            Document document = callKumhoAPI(kumhoUrl);
//            if(document != null){
//                String strStockDatas = "";
//                NodeList roomList = document.getElementsByTagName("room");
//                for(int j=0; j< roomList.getLength(); j++) {
//                    Node node = roomList.item(j);
//                    if (node.getNodeType() == Node.ELEMENT_NODE) {
//                        Element element = (Element) node;
//
//                        String rdate = xmlUtility.getTagValue("rdate", element).trim();
//                        String msg = URLDecoder.decode(xmlUtility.getTagValue("msg", element), "utf-8");
//                        if(rdate.equals("F")){
//                            System.out.println(URLDecoder.decode(msg, "utf-8"));
//                            intFailCount +=1;
//                            strFail = "1";
//                        }else{
//                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//                            String strDateSales = dateFormat.format(sdf.parse(rdate));
//
//                            int intStock = Integer.parseInt(xmlUtility.getTagValue("remainCount", element));
//                            int intOmkStock = intStock;
//
//                            strStockDatas +=strDateSales + "|^|" + intStock + "|^|0|^|0|^|0|^|0|^|0|^|" + intOmkStock + "|^|0";
//                        }
//                    }
//                }
//
//                String result = commonAcmMapper.updateGoods(intAID, intRmIdx, strStockDatas);
//
//                String strResult = result.substring(result.length()-4);
//                if(!strResult.equals("저장완료")){
//                    intFailCount +=1;
//                    strFail = "1";
//                }
//            }else{
//                intFailCount +=1;
//                strFail = "1";
//                System.out.println("금호 API 호출 실패");
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            intFailCount +=1;
//            strFail = "1";
//        }
//        return strFail;
//    }



    // 예약 취소
    public String cancelBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        try{
            List<String> rsvRmNumList = commonAcmMapper.getStrRsvRmNum(intRsvID);
            RsvStayDto rsvStayDto = kumhoMapper.getReservation(intRsvID);

            String strRmNumDatas = "";
            int apiFail = 0;
            for(String strRsvRmNum : rsvRmNumList) {
                String area = rsvStayDto.getStrLocalCode(); // 사업장(통영, 화순, 설악, 제주)

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String arrive_date = sdf.format(rsvStayDto.getDateCheckIn()); // 도착일자

                String reserv_year = arrive_date.substring(0, 4);

                String kumhoUrl = "inter02.asp?area=" + area + "&site=" + site + "&reserv_year=" + reserv_year
                        + "&reserv_number=" + strRsvRmNum;

                Document document = callKumhoAPI(kumhoUrl);

                if(document != null){
                    String resultCode = document.getElementsByTagName("resultCode").item(0).getChildNodes().item(0).getNodeValue();
                    String resultMsg = document.getElementsByTagName("resultMsg").item(0).getChildNodes().item(0).getNodeValue();
                    if(resultCode.equals("S")){
                        // TODO : intSID 수정
                        strRmNumDatas += strRsvRmNum + "|^|C24|^|" + 148 + "{{|}}";
                    }else{
                        apiFail ++;
                        logWriter.add(URLDecoder.decode(resultMsg, "utf-8"));
                    }
                }else{
                    apiFail ++;
                    logWriter.add("금호 API 호출 실패");
                }
            }
            strRmNumDatas = strRmNumDatas.substring(0, strRmNumDatas.length()-5);

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("intRsvID", intRsvID);
            dataMap.put("strStatusCode", Constants.rsvStatus_rsv_complete);
            dataMap.put("strRmNumDatas", strRmNumDatas);
            dataMap.put("strPenaltyDatas", "");

            String strProcedure = commonFunction.makeStrProcedure("spGW_RSV_STAY_UPDATE_PROCESS", dataMap);

            // 예약 테이블 상태값 업데이트
            String result = kumhoMapper.updateRsvStay(intRsvID, Constants.rsvStatus_cancel_complete, strRmNumDatas, "");

            String strContentCode = "200";
            // 예약취소 api 실패 있는 경우
            if(apiFail != 0) {
                strContentCode = "500";
                message = "예약취소 " + apiFail + "건 실패";

                if (result.equals("저장완료")) {
                    message += "\nDB 저장 완료";

                } else {
                    message += "\nDB 저장 실패";
                }
            }else{ // 예약취소 api 실패 없는 경우
                if (result.equals("저장완료")) {
                    message = "예약취소 완료";
                } else {
                    message = "DB 저장 실패";
                }
            }

            // api history
            commonAcmMapper.insertRsvStayHistory(intRsvID, "C24", "[" + strContentCode +"]" + Constants.rsvStatus_cancel_complete, strProcedure, "", 148);

            logWriter.add(message);
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
    public String getBookingList(String dataType, String startDate, String endDate ,HttpServletRequest httpServletRequest){
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

                String kumhoUrl = "inter07.asp?groupid=" + groupId + "&fr_date=" + startDate + "&to_date=" + endDate;

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
