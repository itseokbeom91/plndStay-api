package com.example.stay.accommodation.kumho.service;

import com.example.stay.accommodation.kumho.mapper.BookingMapper;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.common.util.ResponseResult;
import com.example.stay.common.util.XmlUtility;
import com.example.stay.openMarket.common.dto.BookingDto;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("kumho.BookingService")
public class BookingService {

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private XmlUtility xmlUtility;

    // 예약
    public ResponseResult createBooking(int intBookingID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        try{
            BookingDto bookingDto = bookingMapper.getBookingByIntBookingID(intBookingID);

            String ipark_resno = Integer.toString(intBookingID); // 예약번호
            String area = bookingDto.getAccommId(); // 사업장(통영, 화순, 설악, 제주)
            String site = "1"; // 사이트(현재 무조건 1)
            String arrive_date = bookingDto.getCheckInDate(); // 도착일자
            String leave_date = bookingDto.getCheckOutDate(); // 퇴실일자

            long nights_count = 0; // 숙박일수
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date inDate = sdf.parse(arrive_date);
            Date outDate = sdf.parse(leave_date);
            nights_count = ((outDate.getTime() - inDate.getTime()) / 1000) / (24*60*60);

            String groupid = Constants.groupId;

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

            int intRoomCount = getRemainCount(arrive_date, leave_date, area, room_type);
            if(intRoomCount == -1){
                message = "재고확인 실패";
            }else{
                if(intRoomCount >= room_count){
                    String kumhoUrl = Constants.kumhoUrl + "inter01.asp?ipark_resno=" + ipark_resno + "&area=" + area
                            + "&site=" + site + "&arrive_date=" + arrive_date + "&leave_date=" + leave_date
                            + "&nights_count=" + nights_count + "&groupid=" + groupid + "&event_div=" + event_div
                            + "&morning_aqua=" + morning_aqua + "&use_name=" + use_name + "use_phone=" + use_phone
                            + "&use_cell_phone=" + use_cell_phone + "&morning_div=" + morning_div + "&room_type=" + room_type
                            + "&room_count=" + room_count + "&person_count=" + person_count + "&coupon_year=" + coupon_year
                            + "&coupon_number=" + coupon_number + "&ipark_goodsno=" + ipark_goodsno;

                    Document document = callKumhoAPI(kumhoUrl);
                    // response 처리
                    if(document != null){
                        String resultCode = document.getElementsByTagName("resultCode").item(0).getChildNodes().item(0).getNodeValue();
                        String strSpBookingId = document.getElementsByTagName("reserv_number").item(0).getChildNodes().item(0).getNodeValue();
                        String resultmsg = document.getElementsByTagName("resultmsg").item(0).getChildNodes().item(0).getNodeValue();

                        System.out.println("resultCode : " + resultCode);
                        System.out.println("strSpBookingId : " + strSpBookingId);
                        System.out.println("resultmsg : " + resultmsg);

                        // 금호측에 예약이 완료됐으면 우리 DB 상태값 업데이트
                        if(resultCode.equals("S")){
                            String result = bookingMapper.updateBooking(intBookingID, "4", strSpBookingId, room_count);
                            if(result.equals("저장완료")){
                                message = "예약완료";
                            }else{
                                message = "예약실패";
                            }
                        }else{
                            message = resultmsg;
                        }
                    }else{
                        message = "금호 API 호출 실패";
                    }
                }else{
                    message = "예약불가 - 재고없음";
                }
            }
            logWriter.add("intBookingID : " + intBookingID + " -> " + message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "예약 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return new ResponseResult<>(statusCode, message);
    }

    // 예약 시 잔여 객실 수 조회
    public int getRemainCount(String fr_date, String to_date, String area, String room_type){
        int remainCount = 0;
        try{
            String kumhoUrl = "inter05.asp?groupid=" + Constants.groupId + "&fr_date=" + fr_date + "&to_date=" + to_date
                            + "&area=" + area + "&site=1" + "&room_type=" + room_type;
//            String kumhoUrl = "inter05.asp?groupid=210001&fr_date=20151201&to_date=20151201&area=1&site=1&room_type=16A";

            Document document = callKumhoAPI(kumhoUrl);
            if(document != null){
                NodeList roomList = document.getElementsByTagName("room");
                for(int i=0; i< roomList.getLength(); i++) {
                    Node node = roomList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;

                        String rdate = xmlUtility.getTagValue("rdate", element).trim();
                        if(rdate.equals("F")){
                            remainCount = -1;
                        }else{
                            remainCount = Integer.parseInt(xmlUtility.getTagValue("remainCount", element));
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return remainCount;
    }

    // 잔여 객실 수 조회
    public ResponseResult getRemainCountList(String fr_date, String to_date, String area, String room_type, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        MultiValueMap<String, Map> resultMap = new LinkedMultiValueMap<>();
        try{
            String kumhoUrl = "inter05.asp?groupid=" + Constants.groupId + "&fr_date=" + fr_date + "&to_date=" + to_date
                            + "&area=" + area + "&site=1" + "&room_type=" + room_type;

            Document document = callKumhoAPI(kumhoUrl);
            if(document != null){
                NodeList roomList = document.getElementsByTagName("room");
                for(int i=0; i< roomList.getLength(); i++) {
                    Map<String, Object> roomMap = new HashMap<>();
                    Node node = roomList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;

                        String rdate = xmlUtility.getTagValue("rdate", element).trim();
                        String msg = URLDecoder.decode(xmlUtility.getTagValue("msg", element), "utf-8");
                        if(rdate.equals("F")){
                            message = msg;
                        }else{
                            roomMap.put("reservDate", rdate);
                            roomMap.put("roomtype", xmlUtility.getTagValue("roomtype", element));
                            roomMap.put("remainCount", xmlUtility.getTagValue("remainCount", element));
                        }
                    }
                    resultMap.add("roomMap", roomMap);
                }
            }else{
                message = "잔여 객실 수 조회 실패";
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "잔여 객실 수 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return new ResponseResult<>(statusCode, message, resultMap);
    }

    // 예약 취소
    public ResponseResult cancelBooking(int intBookingID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        try{
            BookingDto bookingDto = bookingMapper.getBookingByIntBookingID(intBookingID);
            String area = bookingDto.getAccommId(); // 사업장(통영, 화순, 설악, 제주)
            String site = "1"; // 사이트(현재 무조건 1)
            String arrive_date = bookingDto.getCheckInDate(); // 도착일자
            String reserv_year = arrive_date.substring(0, 4);
            String reserv_number = bookingDto.getStrSpBookingId();

            String kumhoUrl = Constants.kumhoUrl + "inter02.asp?area=" + area + "&site=" + site + "&reserv_year=" + reserv_year
                    + "&reserv_number=" + reserv_number;

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
            e.printStackTrace();
            message = "예약 취소 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return new ResponseResult<>(statusCode, message);
    }

    // 예약현황 조회
    public ResponseResult getReservationStatus(int intBookingID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        Map<String, Object> resultMap = new HashMap<>();
        try{
            BookingDto bookingDto = bookingMapper.getBookingByIntBookingID(intBookingID);

//            String area = bookingDto.getAccommId(); // 사업장(통영, 화순, 설악, 제주)
            String site = "1"; // 사이트(현재 무조건 1)
//            String arrive_date = bookingDto.getCheckInDate(); // 도착일자
//            String reserv_year = arrive_date.substring(0, 4);
//            String reserv_number = bookingDto.getStrSpBookingId();
            String area = "4";
            String arrive_date = "2023-06-10"; // 도착일자
            String reserv_year = "2023";
            String reserv_number = "37537";


            String kumhoUrl = "inter06.asp?area=" + area + "&site=" + site + "&reserv_year=" + reserv_year
                    + "&reserv_number=" + reserv_number;

            Document document = callKumhoAPI(kumhoUrl);
            if(document != null){
                String resultCode = document.getElementsByTagName("resultCode").item(0).getChildNodes().item(0).getNodeValue();
                String resultMsg = URLDecoder.decode(document.getElementsByTagName("resultMsg").item(0).getChildNodes().item(0).getNodeValue(), "utf-8");
                if(resultCode.equals("S")){
                    String reservArea = document.getElementsByTagName("area").item(0).getChildNodes().item(0).getNodeValue();
//                    String reservSite = document.getElementsByTagName("site").item(0).getChildNodes().item(0).getNodeValue();
//                    String reservYear = document.getElementsByTagName("reserv_year").item(0).getChildNodes().item(0).getNodeValue();
                    String reservNumber = document.getElementsByTagName("reserv_number").item(0).getChildNodes().item(0).getNodeValue();

                    resultMap.put("reservArea", reservArea);
//                    resultMap.put("reservSite", reservSite);
//                    resultMap.put("reservYear", reservYear);
                    resultMap.put("reservNumber", reservNumber);

                    // reusltMsg로 온 데이터들 자르기 <resultMsg>20230610/1/27A/1//N/1/박운주대표/양선경/010-111-1111/010-111-1111/예약현황이 조회되었습니다.</resultMsg>
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
            e.printStackTrace();
            message = "예약 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return new ResponseResult<>(statusCode, message, resultMap);
    }

    // 예약 대사자료 조회
    public ResponseResult getReservations(String fr_date, String to_date ,HttpServletRequest httpServletRequest){
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
            e.printStackTrace();
            message = "예약 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return new ResponseResult<>(statusCode, message, resultMap);
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

            e.printStackTrace();
            LogWriter logWriter = new LogWriter(method, strUrl, startTime);
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return document;
    }




}
