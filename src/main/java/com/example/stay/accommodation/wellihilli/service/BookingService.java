package com.example.stay.accommodation.wellihilli.service;

import com.example.stay.accommodation.wellihilli.mapper.WellihilliMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.openMarket.common.dto.RsvStayDto;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("wellihilli.BookingService")
public class BookingService {

    @Autowired
    private WellihilliMapper wellihilliMapper;

    CommonFunction commonFunction = new CommonFunction();

    // 재고 등록 및 수정
    public String getPackageStock(String dataType, HttpServletRequest httpServletRequest, int intRmIdx, String startDate, String endDate){
        String statusCode  = "200";
        String message = "";
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        try{
            String strPkgcode = wellihilliMapper.getStrPkgCode(intRmIdx);

            String strUrl = Constants.whpUrl + ":8070/api/vapi/reservation/calendar?s_vendor_code=" + strPkgcode +
                    "&sresrm=C&s_arrday=" + startDate + "&s_today=" + endDate;
            String method = "GET";

            JsonNode jsonNode = commonFunction.callJsonApi("", "", new JSONObject(), strUrl, method);
            String code = jsonNode.get("status").toString();

            if(code.equals("200")){
                String rmtypeID = wellihilliMapper.getStrRmtypeID(intRmIdx);

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

                        int intExtraA = 0;
                        int intExtraC= 0;
                        int intExtraB = 0;

                        strStockDatas +=strRmtypeID + "|^|" + strDateSales + "|^|" + intStock + "|^|" + intCost + "|^|" + intSales + "|^|"
                                + intExtraA + "|^|" + intExtraC + "|^|" + intExtraB + "|^|" + intOmkStock + "{{|}}";
                    }
                }

                if(strStockDatas.length() > 0){
                    strStockDatas = strStockDatas.substring(0, strStockDatas.length()-5);

                    String result = wellihilliMapper.updateGoods(strStockDatas);
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
                message = "웰리힐리 api 호출 실패";
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
    public boolean checkAvailBooking(String pyung, String sDate, long sleep, int roomCount, String roomType){
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
    public String getTotalPrice(String dataType, String pyung, String sDate, String eDate, String sleep, String roomCount, String roomType, String pkgCode){
        String statusCode = "200";
        String message = "";
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> resultListMap = new ArrayList<>();
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

                resultMap.put("intTotalPrice", intTotalPrice);
                resultMap.put("intSumPrice", intSumPrice);
                resultMap.put("intStandardPrice", intStandardPrice);
                resultMap.put("intSalePrice : ", intSalePrice);
                resultMap.put("intDc", intDc);
                resultMap.put("intExtraPrice", intExtraPrice);

            }
        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            message = "조회 실패";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message, resultMap);
    }


    // 1박 이상일경우 일자별 요금 데이터 조회
    public String getDayPrice(String dataType, String pyung, String sDate, String eDate, String sleep, String roomCount, String roomType, String pkgCode){
        String statusCode = "200";
        String message = "";
        List<Map<String, Object>> resultListMap = new ArrayList<>();
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

                    Map<String, Object> resultMap = new HashMap<>();

                    resultMap.put("intTotalPrice", intTotalPrice);
                    resultMap.put("intSumPrice", intSumPrice);
                    resultMap.put("intStandardPrice", intStandardPrice);
                    resultMap.put("intSalePrice : ", intSalePrice);
                    resultMap.put("intDc", intDc);
                    resultMap.put("intExtraPrice", intExtraPrice);

                    resultListMap.add(resultMap);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            message = "조회 실패";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message, resultListMap);
    }

    // 예약
    public String createBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";

        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        try{
            RsvStayDto rsvStayDto = wellihilliMapper.getReservation(intRsvID);
//            String strCheckIn = "20230920";
//            String strCheckOut = "20230921";
//            String pyung = "13";
//            String sleep = "1";
//            String roomCount = "1";
//            String roomType = "S";
//            String pkgCode = "k646";
            String strRsvName = rsvStayDto.getStrRcvName();
            String strRsvPhone = rsvStayDto.getStrRcvPhone();
            Date dateCheckIn = rsvStayDto.getDateCheckIn();
            Date dateCheckOut = rsvStayDto.getDateCheckOut();
            String strRmtypeID = rsvStayDto.getStrRmtypeID();
            String pyung = strRmtypeID.substring(0,2);
            String roomType = strRmtypeID.substring(2);

            long sleep = ((dateCheckOut.getTime() - dateCheckIn.getTime()) / 1000) / (24*60*60);

            int intRmCnt = rsvStayDto.getIntRmCnt();
            String strPkgCode = rsvStayDto.getStrMapCode(); // 웰리힐리는 룸온리 패키지 코드가 있음
            strPkgCode = "k049";

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String strCheckIn = sdf.format(dateCheckIn);
            String strCheckOut = sdf.format(dateCheckOut);

            // 예약 가능한지 확인
            if(checkAvailBooking(pyung, strCheckIn, sleep, intRmCnt, roomType)){
                String strUrl = Constants.whpUrl + ":8070/api/vapi/reservation/room_access_regist";
                String method = "POST";

                JSONObject requestJson = new JSONObject();
                requestJson.put("s_access_cd", strPkgCode + "-101");
                requestJson.put("s_resvno", "");
                requestJson.put("s_fit", "F");
                requestJson.put("s_resrm", "C");
                requestJson.put("s_pyung", pyung);
                requestJson.put("s_travelcd", strPkgCode);
                requestJson.put("s_roomsu", intRmCnt);
                requestJson.put("s_arrday", strCheckIn);
                requestJson.put("s_nightsu", sleep);
                requestJson.put("s_deptday", strCheckOut);
                requestJson.put("s_guest", strRsvName);
                requestJson.put("s_resvname", "㈜동무해피데이즈");
                requestJson.put("s_resvtel", strRsvPhone);
                requestJson.put("s_recordck", "R");
                requestJson.put("s_typeRoom", roomType);

                // 예약 api 호출
                JsonNode jsonNode = commonFunction.callJsonApi("", "", requestJson, strUrl, method);
                String code = jsonNode.get("status").toString();
                if(code.equals("200")){
                    JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonNode.get("data").toString());
                    String returnCode = jsonObject.get("rtn").toString();
                    String strRsvRmNum = jsonObject.get("resvno").toString();

                    // 예약 성공
                    if(returnCode.equals("1")){
                        String result = wellihilliMapper.updateRsvStay(intRsvID, "4", strRsvRmNum);
                        if(result.equals("저장완료")){
                            message = "예약완료";
                        }else{
                            message = "예약실패";
                        }
                    }else {
                        message = "예약 실패";
                    }

                }else{
                    message = "웰리힐리 api 호출 실패";
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
    public String cancelBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";

        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        try{
            RsvStayDto rsvStayDto = wellihilliMapper.getReservation(intRsvID);

            String strRsvRmNum = rsvStayDto.getStrRsvRmNum();
            String strRsvName = rsvStayDto.getStrRcvName();
            String strRsvPhone = rsvStayDto.getStrRcvPhone();
            Date dateCheckIn = rsvStayDto.getDateCheckIn();
            Date dateCheckOut = rsvStayDto.getDateCheckOut();
            String strRmtypeID = rsvStayDto.getStrRmtypeID();
            String pyung = strRmtypeID.substring(0,2);
            String roomType = strRmtypeID.substring(2);

            long sleep = ((dateCheckOut.getTime() - dateCheckIn.getTime()) / 1000) / (24*60*60);

            int intRmCnt = rsvStayDto.getIntRmCnt();
            String strPkgCode = rsvStayDto.getStrMapCode();
            strPkgCode = "k049";

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String strCheckIn = sdf.format(dateCheckIn);
            String strCheckOut = sdf.format(dateCheckOut);

            String strUrl = Constants.whpUrl + ":8070/api/vapi/reservation/room_access_regist";
            String method = "POST";

            JSONObject requestJson = new JSONObject();
            requestJson.put("s_access_cd", strPkgCode + "-101");
            requestJson.put("s_resvno", strRsvRmNum);
            requestJson.put("s_fit", "F");
            requestJson.put("s_resrm", "C");
            requestJson.put("s_pyung", pyung);
            requestJson.put("s_travelcd", strPkgCode);
            requestJson.put("s_roomsu", intRmCnt);
            requestJson.put("s_arrday", strCheckIn);
            requestJson.put("s_nightsu", sleep);
            requestJson.put("s_deptday", strCheckOut);
            requestJson.put("s_guest", strRsvName);
            requestJson.put("s_resvname", "㈜동무해피데이즈");
            requestJson.put("s_resvtel", strRsvPhone);
            requestJson.put("s_recordck", "C");
            requestJson.put("s_typeRoom", roomType);

            // 예약 취소 api 호출
            JsonNode jsonNode = commonFunction.callJsonApi("", "", requestJson, strUrl, method);
            String code = jsonNode.get("status").toString();
            if(code.equals("200")){
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonNode.get("data").toString());
                String returnCode = jsonObject.get("rtn").toString();

                if(returnCode.equals("1")){
                    // 예약 테이블 상태값 업데이트
                    String result = wellihilliMapper.updateRsvStay(intRsvID, "5", "");
                    if(result.equals("저장완료")){
                        message = "예약 취소 완료";
                    }else{
                        message = "예약 취소 실패";
                    }
                }else {
                    message = "예약 실패";
                }
            }else{
                message = "웰리힐리 api 호출 실패";
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
    public String updateBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";

        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        try{
            RsvStayDto rsvStayDto = wellihilliMapper.getReservation(intRsvID);

            String strRsvCode = rsvStayDto.getStrRsvCode();
            String strRsvName = rsvStayDto.getStrRcvName();
            String strRsvPhone = rsvStayDto.getStrRcvPhone();
            Date dateCheckIn = rsvStayDto.getDateCheckIn();
            Date dateCheckOut = rsvStayDto.getDateCheckOut();
            String strRmtypeID = rsvStayDto.getStrRmtypeID();
            String pyung = strRmtypeID.substring(0,2);
            String roomType = strRmtypeID.substring(2);

            long sleep = ((dateCheckOut.getTime() - dateCheckIn.getTime()) / 1000) / (24*60*60);

            int intRmCnt = rsvStayDto.getIntRmCnt();
            String strPkgCode = rsvStayDto.getStrMapCode();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String strCheckIn = sdf.format(dateCheckIn);
            String strCheckOut = sdf.format(dateCheckOut);

            // 예약 수정 api 호출
            String strUrl = Constants.whpUrl + ":8070/api/vapi/reservation/room_access_regist";
            String method = "POST";

            JSONObject requestJson = new JSONObject();
            requestJson.put("s_access_cd", strPkgCode + "-101");
            requestJson.put("s_resvno", strRsvCode);
            requestJson.put("s_fit", "F");
            requestJson.put("s_resrm", "C");
            requestJson.put("s_pyung", pyung);
            requestJson.put("s_travelcd", strPkgCode);
            requestJson.put("s_roomsu", intRmCnt);
            requestJson.put("s_arrday", strCheckIn);
            requestJson.put("s_nightsu", sleep);
            requestJson.put("s_deptday", strCheckOut);
            requestJson.put("s_guest", strRsvName);
            requestJson.put("s_resvname", "㈜동무해피데이즈");
            requestJson.put("s_resvtel", strRsvPhone);
            requestJson.put("s_recordck", "U");
            requestJson.put("s_typeRoom", roomType);

            JsonNode jsonNode = commonFunction.callJsonApi("", "", requestJson, strUrl, method);
            String code = jsonNode.get("status").toString();
            if(code.equals("200")){
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonNode.get("data").toString());
                String returnCode = jsonObject.get("rtn").toString();

                if(returnCode.equals("1")) {
                    message = "예약 수정 완료";
                }else{
                    message = "예약 수정 실패";
                }

            }else{
                message = "웰리힐리 api 호출 실패";
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
    public String getBookingInfo(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";

        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        Map<String, Object> resultMap = new HashMap<>();

        try{
            String strRsvCode = wellihilliMapper.getStrRsvCode(intRsvID);
            String strUrl = Constants.whpUrl + ":8070/api/vapi/reservation/rsv_detail?s_resvno=" + strRsvCode;
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
    public String getBookingList(String dataType, HttpServletRequest httpServletRequest, String searchFlag, String searchData,
                                   String sDate, String eDate, String rsvFlag, String strPkgCode){
        String statusCode = "200";
        String message = "";

        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        List<Map<String, Object>> resultMapList = new ArrayList<>();
        try{
            // 추가 검색 부분 없으면 그냥 시작, 종료일에 해당하는 리스트 조회
            if(searchFlag == null){ // 검색 구분(1 : 예약번호, 2 : 투숙객명)
                searchFlag = "";
            }
            if(searchData == null){ // 검색어
                searchData = "";
            }
            if(rsvFlag == null){ // 예약 상태값 구분(R : 예약, U : 변경, C : 취소, I : 입실)
                rsvFlag = "";
            }

            String strUrl = Constants.whpUrl + ":8070/api/vapi/reservation/rsv_list?s_travelcd=" + strPkgCode +
                            "&s_flag=" + searchFlag + "&s_qrystr=" + URLEncoder.encode(searchData, "utf-8")
                            + "&s_sdate=" + sDate + "&s_fdate=" + eDate + "&s_recordck=" + rsvFlag;

            String method = "GET";

            JsonNode jsonNode = commonFunction.callJsonApi("", "", new JSONObject(), strUrl, method);
            String code = jsonNode.get("status").toString();

            if(code.equals("200")) {
                JSONArray jsonArray = (JSONArray) new JSONParser().parse(jsonNode.get("data").toString());
                for(Object object : jsonArray) {
                    Map<String, Object> resultMap = new HashMap<>();

                    JSONObject jsonObject = (JSONObject) JSONValue.parse(object.toString());
//                    String area = jsonObject.get("area").toString().trim();
                    String recordckcd = jsonObject.get("recordckcd").toString().trim(); // 예약 구분 코드 (R:예약, U:변경, C:취소, I:입실)
//                    String resrm = jsonObject.get("resrm").toString().trim(); // 콘도 구분
                    String strRsvCode = jsonObject.get("resvno").toString().trim(); // 예약번호
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

                    String bigo = jsonObject.get("resvbigo").toString().trim(); // 비고

                    resultMap.put("예약 상태값", recordckcd);
                    resultMap.put("예약번호", strRsvCode);
                    resultMap.put("투숙자명", strRsvName);
                    resultMap.put("투숙자 핸드폰번호", strRsvPhone);
                    resultMap.put("예약일", strRsvDay);
                    resultMap.put("체크인 날짜", strCheckIn);
                    resultMap.put("체크아웃 날짜", strCheckOut);
                    resultMap.put("숙박일수", intSleep);
                    resultMap.put("평형", pyung);
                    resultMap.put("객실타입", roomType);
                    resultMap.put("정가", intStandardPrice);
                    resultMap.put("할인율", intDiscount);
                    resultMap.put("판매가", intSalePrice);
                    resultMap.put("합계금액", intSumPrice);
                    resultMap.put("총액", intTotalPrice);
                    resultMap.put("비고", bigo);

                    resultMapList.add(resultMap);
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

        return commonFunction.makeReturn(dataType, statusCode, message, resultMapList);
    }




}
