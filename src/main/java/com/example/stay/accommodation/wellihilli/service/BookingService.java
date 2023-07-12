package com.example.stay.accommodation.wellihilli.service;

import com.example.stay.accommodation.wellihilli.mapper.BookingMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;

@Service("wellihilli.BookingService")
public class BookingService {

    @Autowired
    private BookingMapper bookingMapper;

    CommonFunction commonFunction = new CommonFunction();

    // 재고 등록 및 수정
    public String updateGoods(String dataType, HttpServletRequest httpServletRequest, int intRmIdx, String startDate, String endDate){
        String statusCode  = "200";
        String message = "";
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        try{
            // 재고는 객실의 잔여수량이기 때문에 어떤 패키지 코드로 호출하든 동일 -> k049로 넣음
            String strUrl = Constants.whpUrl + ":8070/api/vapi/reservation/calendar?s_vendor_code=k049" +
                    "&sresrm=C&s_arrday=" + startDate + "&s_today=" + endDate;
            String method = "GET";

            JsonNode jsonNode = commonFunction.callJsonApi("", "", new JSONObject(), strUrl, method);
            String code = jsonNode.get("status").toString();

            if(code.equals("200")){
                String rmtypeID = bookingMapper.getStrRmtypeID(intRmIdx);

                String strStockDatas = "";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                JSONArray jsonArray = (JSONArray) new JSONParser().parse(jsonNode.get("data").toString());
                for(Object object : jsonArray){
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(object.toString());

                    String strPyung = jsonObject.get("pyung").toString();
                    String roomTypeID = jsonObject.get("roomType").toString();

                    String strRmtypeID = strPyung + roomTypeID;

                    // 웰리힐리는 날짜 보내면 전체 객실타입의 재고를 주기 때문에 가져오고자하는 객실의 재고 데이터만 뽑아서 저장
                    if(strRmtypeID.equals(rmtypeID)){
                        int intStock = Integer.parseInt(jsonObject.get("vcCount").toString());
                        if(intStock < 0){
                            intStock = 0;
                        }
                        int intOmkStock = intStock;

                        int intCost = 0;
                        int intSales = 0;
                        if(jsonObject.get("roompay") != null){
                            intCost = Integer.parseInt(jsonObject.get("roompay").toString());
                            intSales = intCost;
                        }

                        String yearday = jsonObject.get("yearday").toString();
                        Date yearDate = sdf.parse(yearday);
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                        String strDateSales = sdf2.format(yearDate);

                        double doubleOmkSales = 0;

                        int year = Integer.parseInt(strDateSales.substring(0, 4));
                        int month = Integer.parseInt(strDateSales.substring(5, 7));
                        int day = Integer.parseInt(strDateSales.substring(8,10));

                        LocalDate date = LocalDate.of(year, month, day);
                        DayOfWeek dayOfWeek = date.getDayOfWeek();

                        /**
                         * 임시
                         */
                        double weekday = 1.09; // 일~목
                        double friday = 1.09; // 금
                        double saturday = 1.1; // 토

                        if(dayOfWeek.getValue() == 7 || dayOfWeek.getValue() == 1 || dayOfWeek.getValue() == 2 ||
                                dayOfWeek.getValue() == 3 || dayOfWeek.getValue() == 4){
                            doubleOmkSales = intSales * weekday;
                        }else if(dayOfWeek.getValue() == 5){
                            doubleOmkSales = intSales * friday;
                        }else if(dayOfWeek.getValue() == 6){
                            doubleOmkSales = intSales * saturday;
                        }

                        int intExtraA = 0;
                        int intExtraC= 0;
                        int intExtraB = 0;

                        strStockDatas +=strRmtypeID + "|^|" + strDateSales + "|^|" + intStock + "|^|" + intCost + "|^|" + intSales + "|^|"
                                + intExtraA + "|^|" + intExtraC + "|^|" + intExtraB + "|^|" + intOmkStock + "|^|"  + doubleOmkSales+ "{{|}}";
                    }
                }

                if(strStockDatas.length() > 0){
                    strStockDatas = strStockDatas.substring(0, strStockDatas.length()-5);

                    String result = bookingMapper.updateGoods(strStockDatas);
                    String strResult = result.substring(result.length()-4);
                    if(strResult.equals("저장완료")){
                        message = "재고 등록/수정 완료";
                    }else{
                        logWriter.add(result);
                        message = "재고 등록/수정 실패";
                    }
                }else{
                    message = "재고 등록/수정 실패";
                }

            }else{
                message = "재고 등록/수정 실패 - api 응답 코드 : " + code;
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            message = "재고 수정 및 등록 실패";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 예약 가능 여부 조회
    public boolean checkAvailBooking(String pyung, String sDate, String sleep, String roomCount, String roomType){
        LogWriter logWriter = new LogWriter(System.currentTimeMillis());
        boolean avail = false;

        try{
            String strUrl = Constants.whpUrl + ":8070/api/vapi/reservation/room_resv_possible_check?" +
                    "s_memType=4&s_pyung=" + pyung + "&s_arrday=" + sDate + "&s_nightsu=" + sleep +
                    "&s_roomsu=" + roomCount + "&s_roomType=" + roomType;

            String method = "GET";

            JsonNode jsonNode = commonFunction.callJsonApi("", "", new JSONObject(), strUrl, method);
            String code = jsonNode.get("status").toString();

            if(code.equals("200")){
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonNode.get("data").toString());
                String strAvail = jsonObject.get("rtn").toString();
                if(strAvail.equals("Y")){
                    avail = true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return avail;
    }

    // 체크인 날짜에 해당되는 객실 수량 및 계산된 총 요금 조회
    public void getTotalPrice(String pyung, String sDate, String eDate, String sleep, String roomCount, String roomType, String pkgCode){
        LogWriter logWriter = new LogWriter(System.currentTimeMillis());
        try{
            String strUrl = Constants.whpUrl + ":8070/api/vapi/reservation/room_fee_info?s_resrm=C&s_pyung=" + pyung +
                            "&s_arrday=" + sDate + "&s_deptday=" + eDate + "&s_nightsu=" + sleep + "&s_roomsu=" + roomCount +
                            "&s_roomType=" + roomType +"&s_memtype=&s_vendor_type=F&s_vendor_code=" + pkgCode;

            String method = "GET";

            JsonNode jsonNode = commonFunction.callJsonApi("", "", new JSONObject(), strUrl, method);
            String code = jsonNode.get("status").toString();

            if(code.equals("200")){
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonNode.get("data").toString());
                int intTotalPrice = Integer.parseInt(jsonObject.get("totalamt").toString()); // 총금액
                int intSumPrice = Integer.parseInt(jsonObject.get("sumroompay").toString()); // 합계금액
                int intStandardPrice = Integer.parseInt(jsonObject.get("stdamt").toString()); // 표준금액(체크인 당일의)
                int intSalePrice = Integer.parseInt(jsonObject.get("atlrate").toString()); // 실제금액(체크인 당일의)
                int intDc = Integer.parseInt(jsonObject.get("dc").toString()); // 할증/할인
                int intExtraPrice = Integer.parseInt(jsonObject.get("appendPay").toString()); // 추가요금

                System.out.println("intTotalPrice : " + intTotalPrice);
                System.out.println("intSumPrice : " + intSumPrice);
                System.out.println("intStandardPrice : " + intStandardPrice);
                System.out.println("intSalePrice : " + intSalePrice);
                System.out.println("intDc : " + intDc);
                System.out.println("intExtraPrice : " + intExtraPrice);

            }
        }catch (Exception e){
            e.printStackTrace();
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
    }

    // 1박 이상일경우 일자별 요금 데이터 조회
    public void getDayPrice(String pyung, String sDate, String eDate, String sleep, String roomCount, String roomType, String pkgCode){
        LogWriter logWriter = new LogWriter(System.currentTimeMillis());
        try{
            String strUrl = Constants.whpUrl + ":8070/api/vapi/reservation/room_day_fee_list?s_resrm=C&s_pyung=" + pyung +
                    "&s_arrday=" + sDate + "&s_deptday=" + eDate + "&s_nightsu=" + sleep + "&s_roomsu=" + roomCount +
                    "&s_roomType=" + roomType +"&s_memtype=&s_vendor_type=F&s_vendor_code=" + pkgCode;

            String method = "GET";

            JsonNode jsonNode = commonFunction.callJsonApi("", "", new JSONObject(), strUrl, method);
            String code = jsonNode.get("status").toString();

            if(code.equals("200")){
                JSONArray jsonArray = (JSONArray) new JSONParser().parse(jsonNode.get("data").toString());
                for(Object object : jsonArray) {
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(object.toString());

                    int intSumPrice = Integer.parseInt(jsonObject.get("sumroompay").toString()); // 할인/할증전 전체금액(룸수계산)
                    int intStandardPrice = Integer.parseInt(jsonObject.get("roompay").toString()); // 할증/할인전 금액(룸수계산X)
                    int intSalePrice = Integer.parseInt(jsonObject.get("saleAmt").toString()); // 판매가?
                    int intTotalPrice = Integer.parseInt(jsonObject.get("totalAmt").toString()); // 할인/할증후 전체금액(룸수계산)
                    int intExtraPrice = Integer.parseInt(jsonObject.get("appendAmt").toString()); // 추가요금
                    int intDc = Integer.parseInt(jsonObject.get("dc").toString()); // 할증/할인율


                    System.out.println("intTotalPrice : " + intTotalPrice);
                    System.out.println("intSumPrice : " + intSumPrice);
                    System.out.println("intStandardPrice : " + intStandardPrice);
                    System.out.println("intSalePrice : " + intSalePrice);
                    System.out.println("intDc : " + intDc);
                    System.out.println("intExtraPrice : " + intExtraPrice);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
    }

    // 예약
    public String createBooking(String dataType, int intBookingIdx, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";

        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        try{
            // 우리 예약 테이블에서 정보 가져와서 세팅
            String strCheckIn = "20230801";
            String strCheckOut = "20230802";
            String pyung = "13";
            String sleep = "1";
            String roomCount = "1";
            String roomType = "S";
            String pkgCode = "K648";
            // 예약 가능한지 확인
            if(checkAvailBooking(pyung, strCheckIn, sleep, roomCount, roomType)){
                // 예약 api 호출
                String strUrl = Constants.whpUrl + ":8070/api/vapi/reservation/room_access_regist";
                String method = "POST";

                JSONObject requestJson = new JSONObject();
                requestJson.put("s_access_cd", pkgCode + "-101");
                requestJson.put("s_resvno", "");
                requestJson.put("s_fit", "F");
                requestJson.put("s_resrm", "C");
                requestJson.put("s_pyung", pyung);
                requestJson.put("s_travelcd", pkgCode);
                requestJson.put("s_roomsu", roomCount);
                requestJson.put("s_arrday", strCheckIn);
                requestJson.put("s_nightsu", sleep);
                requestJson.put("s_deptday", strCheckOut);
                requestJson.put("s_guest", "손유정");
                requestJson.put("s_resvname", "㈜동무해피데이즈");
                requestJson.put("s_resvtel", "01029405275");
                requestJson.put("s_recordck", "R");
                requestJson.put("s_typeRoom", roomType);

                JsonNode jsonNode = commonFunction.callJsonApi("", "", requestJson, strUrl, method);
                String code = jsonNode.get("status").toString();
                if(code.equals("200")){
                    JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonNode.get("data").toString());
                    String returnCode = jsonObject.get("rtn").toString();

                    System.out.println("returnCode : " + returnCode);
                    // 예약 성공
                    if(returnCode.equals("1")){
                        message = "예약성공";
                    }else if(returnCode.equals("X")){ // 회원 아님
                        message = returnCode;
                    }else { // 객실없음
                        System.out.println("객실없음");
                        message = returnCode;
                    }

                }else{
                    message = "예약 실패";
                }
            }else{
                message = "예약 불가";
            }

        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            message = "예약 실패";
            logWriter.add("error : " + e.getMessage());
        }

        logWriter.log(0);

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 예약 취소
    public String cancelBooking(String dataType, int intBookingIdx, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";

        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        try{
            // 우리 예약 테이블에서 정보 가져와서 세팅
            String pkgCode = "K648";

            // 예약 취소 api 호출
            String strUrl = Constants.whpUrl + ":8070/api/vapi/reservation/room_access_regist";
            String method = "POST";

            JSONObject requestJson = new JSONObject();
            requestJson.put("s_access_cd", pkgCode + "-101");
            requestJson.put("s_resvno", "");
            requestJson.put("s_fit", "F");
            requestJson.put("s_resrm", "C");
            requestJson.put("s_pyung", "13");
            requestJson.put("s_travelcd", "K648");
            requestJson.put("s_roomsu", "1");
            requestJson.put("s_arrday", "20230801");
            requestJson.put("s_nightsu", "1");
            requestJson.put("s_deptday", "20230802");
            requestJson.put("s_guest", "테스트예약");
            requestJson.put("s_resvname", "㈜동무해피데이즈");
            requestJson.put("s_resvtel", "01029405275");
            requestJson.put("s_recordck", "C");
            requestJson.put("s_typeRoom", "S");

            JsonNode jsonNode = commonFunction.callJsonApi("", "", requestJson, strUrl, method);
            String code = jsonNode.get("status").toString();
            if(code.equals("200")){

            }else{
                message = "예약 취소 실패";
            }

        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            message = "예약 취소 실패";
            logWriter.add("error : " + e.getMessage());
        }

        logWriter.log(0);

        return commonFunction.makeReturn(dataType, statusCode, message);
    }
    
    // 예약 수정
    public String modifyBooking(String dataType, int intBookingIdx, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";

        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        try{
            // 우리 예약 테이블에서 정보 가져와서 세팅
            String pkgCode = "K648";

            // 예약 수정 api 호출
            String strUrl = Constants.whpUrl + ":8070/api/vapi/reservation/room_access_regist";
            String method = "POST";

            JSONObject requestJson = new JSONObject();
            requestJson.put("s_access_cd", pkgCode + "-101");
            requestJson.put("s_resvno", "O80894");
            requestJson.put("s_fit", "F");
            requestJson.put("s_resrm", "C");
            requestJson.put("s_pyung", "13");
            requestJson.put("s_travelcd", pkgCode);
            requestJson.put("s_roomsu", "1");
            requestJson.put("s_arrday", "20230801");
            requestJson.put("s_nightsu", "1");
            requestJson.put("s_deptday", "20230802");
            requestJson.put("s_guest", "테스트예약");
            requestJson.put("s_resvname", "㈜동무해피데이즈");
            requestJson.put("s_resvtel", "01029405275");
            requestJson.put("s_recordck", "U");
            requestJson.put("s_typeRoom", "S");

            JsonNode jsonNode = commonFunction.callJsonApi("", "", requestJson, strUrl, method);
            String code = jsonNode.get("status").toString();
            if(code.equals("200")){

            }else{
                message = "예약 수정 실패";
            }

        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            message = "예약 수정 실패";
            logWriter.add("error : " + e.getMessage());
        }

        logWriter.log(0);

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 예약 상세 조회
    public String checkBooking(String dataType, int intBookingIdx, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";

        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        try{
            String strBookingID = "O80894";
            String strUrl = Constants.whpUrl + ":8070/api/vapi/reservation/rsv_detail?s_resvno=" + strBookingID;
            String method = "GET";

            JsonNode jsonNode = commonFunction.callJsonApi("", "", new JSONObject(), strUrl, method);
            String code = jsonNode.get("status").toString();

            if(code.equals("200")) {
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonNode.get("data").toString());
                String resvno = jsonObject.get("resvno").toString();

                message = "예약번호 : " + resvno;
            }else{
                message = "예약 조회 실패";
            }
            logWriter.add(message);
        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            message = "예약 조회 실패";
            logWriter.add("error : " + e.getMessage());
        }
        logWriter.log(0);

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 예약 리스트 조회(체크인날짜 기준)
    public String checkBookingList(String dataType, HttpServletRequest httpServletRequest, String searchFlag, String searchData,
                                   String sDate, String eDate, String rsvFlag){
        String statusCode = "200";
        String message = "";

        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        try{
            if(searchFlag == null){
                searchFlag = "";
            }
            if(searchData == null){
                searchData = "";
            }
            if(rsvFlag == null){
                rsvFlag = "";
            }

            String pkgCode = "k648";

            String strUrl = Constants.whpUrl + ":8070/api/vapi/reservation/rsv_list?s_travelcd=" + pkgCode +
                            "&s_flag=" + searchFlag + "&s_qrystr=" + URLEncoder.encode(searchData, "utf-8")
                            + "&s_sdate=" + sDate + "&s_fdate=" + eDate + "&s_recordck=" + rsvFlag;

            String method = "GET";

            JsonNode jsonNode = commonFunction.callJsonApi("", "", new JSONObject(), strUrl, method);
            String code = jsonNode.get("status").toString();

            if(code.equals("200")) {
                JSONArray jsonArray = (JSONArray) new JSONParser().parse(jsonNode.get("data").toString());
                for(Object object : jsonArray) {
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(object.toString());
//                    String area = jsonObject.get("area").toString().trim();
                    String recordckcd = jsonObject.get("recordckcd").toString().trim(); // 예약 구분 코드 (R:예약, U:변경, C:취소, I:입실)
//                    String resrm = jsonObject.get("resrm").toString().trim(); // 콘도 구분
                    String strBookingID = jsonObject.get("resvno").toString().trim(); // 예약번호
                    String strRsvName =  jsonObject.get("guest").toString().trim();
                    String strRsvPhone = jsonObject.get("resvtel").toString().trim(); // 예약자 전화번호 : '-' 포함도 있고 아닌 것도 있음
                    String strRsvDay = jsonObject.get("resvtel").toString().trim(); // 예약일
                    String strCheckIn = jsonObject.get("arrday").toString().trim(); // 체크인 날짜 ex) 2023/07/05
                    String strCheckOut = jsonObject.get("deptday").toString().trim(); // 체크인 날짜 ex) 2023/07/05
                    int intSleep = Integer.parseInt(jsonObject.get("nightsu").toString().trim()); // 숙박일수

                    String pyung = jsonObject.get("deptday").toString().trim(); // 평형
                    String roomType = jsonObject.get("roomType").toString().trim(); // 객실타입 - 일반객실?
                    String strRmtype = pyung + roomType;

                    int intStandardPrice = Integer.parseInt(jsonObject.get("stdrate").toString().trim()); // 정가
                    int intDiscount = Integer.parseInt(jsonObject.get("discount").toString().trim()); // 할인율
                    int intSalePrice = Integer.parseInt(jsonObject.get("atlrate").toString().trim()); // 판매가
                    int intSumPrice = Integer.parseInt(jsonObject.get("sumamt").toString().trim()); // 합계금액
                    int intTotalPrice = Integer.parseInt(jsonObject.get("totalamt").toString().trim()); // 총액

//                    String bigo = jsonObject.get("resvbigo").toString().trim(); // 비고

                }
            }else{
                message = "예약리스트 조회 실패";
            }
            logWriter.add(message);
        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            message = "예약리스트 조회 실패";
            logWriter.add("error : " + e.getMessage());
        }
        logWriter.log(0);

        return commonFunction.makeReturn(dataType, statusCode, message);
    }




}
