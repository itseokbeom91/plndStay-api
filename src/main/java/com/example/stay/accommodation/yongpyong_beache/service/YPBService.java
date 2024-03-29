package com.example.stay.accommodation.yongpyong_beache.service;

import com.example.stay.accommodation.yongpyong_beache.mapper.YPBMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.openMarket.common.dto.RsvStayDto;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class YPBService {



    CommonFunction commonFunction = new CommonFunction();

    @Autowired
    private YPBMapper ypbMapper;

    public String getStock(int intAID, int intRmIdx, int intPkgIdx, String startDate, String endDate){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

            JSONObject mainObject = getCommonHeader("stock");
            JSONObject dataObject = new JSONObject();

            Map<String, String> acmRmMap = ypbMapper.getAcmRmID(intAID, intRmIdx);
            Map<String, String> pkgLcdMap = ypbMapper.getPkgLcdID(intPkgIdx);

            String strProertyId = acmRmMap.get("strPropertyID").toString();
            String strRoomTypeCode = acmRmMap.get("strRmtypeID").toString();
            String strLcdCode = pkgLcdMap.get("strLocalCode").toString();
            String strPackageCode = pkgLcdMap.get("strPkgCode").toString();


            String strCustNo = "";
            if(strProertyId.equals("11")) { // 용평
                strCustNo = Constants.yongpyongCode;
            }else if(strProertyId.equals("22")){ // 비체
                strCustNo = Constants.beacheCode;
            }

            dataObject.put("brch_cd", strProertyId); // 용평 : 11 / 비체 : 22
            dataObject.put("outlet_cd", strLcdCode); // 영업장 코드
            dataObject.put("room_type_cd", strRoomTypeCode); // 룸타입 제외 가능
            dataObject.put("from_date", startDate);
            dataObject.put("to_date", endDate);
            dataObject.put("rsvpl_type_cd", "07"); // condo24
            dataObject.put("pkg_cd", strPackageCode);
            dataObject.put("cust_no", strCustNo); // 용평 : 1199719 / 비체 : 1178413

            mainObject.put("DATA", dataObject);

            System.out.println(mainObject);

//            JsonNode jsonNode = commonService.callJsonApi("YPB", "stock", mainObject);
            JsonNode jsonNode = commonFunction.callJsonApi("YPB", "stock", mainObject, "", "POST");


            // 통신결과 0:실패, 1:성공
            JSONObject codeObject = (JSONObject) new JSONParser().parse(jsonNode.get("HEADER").toString());
            String resultCode = codeObject.get("statusCode").toString();

            JSONArray jsonArray = (JSONArray) new JSONParser().parse(jsonNode.get("DATA").toString());


            // roomType 별로 데이터 담을 map
            // rommType 여러개 받을 경우 대비해서 만들었지만... intRmIdx로 가져오는거로 봐뀌어서 roomType 하나만 사용해서 넣게될듯....
            HashMap<String, String> stockMap = new HashMap<String, String>();

            if(resultCode.equals("1")){

                for(Object object : jsonArray){
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(object.toString());

                    String strDate = jsonObject.get("yyyymmdd").toString();
                    int intStock = Integer.parseInt(jsonObject.get("able_rms").toString());
                    String strRoomTypeId = jsonObject.get("room_type_cd").toString();

                    // key 값에 roomTypeId 없으면 생성
                    if(!stockMap.containsKey(strRoomTypeId)){
                        stockMap.put(strRoomTypeId, "");
                    }

                    String mapValue = stockMap.get(strRoomTypeId);
                    mapValue += strDate + "|^|" + intStock + "|^|0|^|0|^|0|^|0|^|0|^|" + intStock + "|^|0{{|}}";
                    stockMap.put(strRoomTypeId, mapValue);

                }

                for(String key : stockMap.keySet()){
                    String strStockDatas = stockMap.get(key);
                    if(strStockDatas.length() > 1){
                        strStockDatas = strStockDatas.substring(0, strStockDatas.length()-5);
                    }
                    System.out.println(strStockDatas);
                    result = ypbMapper.insertStock(intAID, intRmIdx, strPackageCode, strStockDatas);
                }
                String strResult = result.substring(result.length()-4);
                if(strResult.equals("저장완료")){
                    message = "재고 등록 및 수정 완료";
                }else{
                    message = " 재고 등록 및 수정 실패";
                }

            }else{
                message = "호출 실패";
            }


        }catch (Exception e){
            message = "재고 등록 및 수정 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn("json", statusCode, message);
    }


    public String booking(int intRsvID, String dataType){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

            JSONObject mainObject = getCommonHeader("booking");
            JSONObject dataObject = new JSONObject();

            RsvStayDto rsvStayDto = ypbMapper.getRsvInfo(intRsvID);
            String strPropertyId = rsvStayDto.getStrPropertyID();
            String strLocalCode = rsvStayDto.getStrLocalCode();
            String strRmTypeID = rsvStayDto.getStrRmtypeID();
            String strDate = new SimpleDateFormat("yyyyMMdd").format(rsvStayDto.getDateCheckIn());
            String strPkgCode = rsvStayDto.getStrMapCode();
            String strRsvID = String.valueOf(intRsvID);
            String strPgkName = rsvStayDto.getStrPkgName();
            String strName = rsvStayDto.getStrRcvName();
            String strPhone = rsvStayDto.getStrRcvPhone();

            String strCustNo = "";
            if(strPropertyId.equals("11")) { // 용평
                strCustNo = Constants.yongpyongCode;
            }else if(strPropertyId.equals("22")){ // 비체
                strCustNo = Constants.beacheCode;
            }

            dataObject.put("brch_cd", strPropertyId); // 용평 : 11 / 비체 : 22
            dataObject.put("outlet_cd", strLocalCode); // 영업장 코드
            dataObject.put("room_type_cd", strRmTypeID);
            //dataObject.put("arrv_date", strDate);
            dataObject.put("rsvpl_type_cd", "07"); // condo24
            dataObject.put("pkg_cd", strPkgCode);
            dataObject.put("bkng_id", strRsvID); // 주문번호
            dataObject.put("rem", strPgkName); // 패키지 상품명
            dataObject.put("cust_no", strCustNo); // 용평 : 1199719 / 비체 : 1178413
            dataObject.put("guest_name", strName); // 투숙객명
            dataObject.put("guest_contp", strPhone); // 투숙객 연락처
            dataObject.put("guest_sms_send_yn", "N"); // 취소 메세지 전송 여부

            // 몇 박인지 구하기
            Date checkInDate = rsvStayDto.getDateCheckIn();
            Date checkOutDate = rsvStayDto.getDateCheckOut();
            long longStayCnt = (checkOutDate.getTime() - checkInDate.getTime()) / 86400000;

            // 객실번호 데이터
            String strRmNumDatas = "";

            for(long i=0; i<longStayCnt; i++){

                Instant instant = checkInDate.toInstant();
                LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                localDate = localDate.plusDays(i);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

                // LocalDate를 문자열로 변환
                String formatDate = localDate.format(formatter);
                dataObject.put("arrv_date", formatDate);

                mainObject.put("DATA", dataObject);

                System.out.println(mainObject);

//                JsonNode jsonNode = commonFunction.callJsonApi("YPB", "booking", mainObject, "", "POST");
//
//                // 통신결과 0:실패, 1:성공
//                JSONObject codeObject = (JSONObject) new JSONParser().parse(jsonNode.get("HEADER").toString());
//                String resultCode = codeObject.get("statusCode").toString();
//
//                JSONObject resultObject = (JSONObject) new JSONParser().parse(jsonNode.get("DATA").toString());
//                String strRmCode = resultObject.get("rsrv_no").toString();
//                String strRsvResult = resultObject.get("result").toString();
//
//                if(resultCode.equals("1")){
//                    if(strRsvResult.equals("객실예약완료")){
//                        //strRmNumDatas += intRsvID + "|^|" + ;
//                    }else{
//
//                    }
//
//
//                }else{
//                    statusCode = "500";
//                    message = "호출 실패";
//                }
//
//                result = jsonNode.toString();
//                System.out.println(result);

            }

            // 여기에다가 통합 프로시저 ㄱㄱ
            String procResult = ypbMapper.updateRsv(intRsvID, "4", strRmNumDatas);
            if(procResult.equals("저장완료")){
                //message += "예약 완료[객실번호 : " + strRmCode + "]";
            }else{
                //message += "DB저장 실패[객실번호 : " + strRmCode + "]";
            }



        }catch (Exception e){
            message = "재고 등록 및 수정 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }


    public String getBookingInfo(int intRsvID){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

            JSONObject mainObject = getCommonHeader("bookingInfo");
            JSONObject dataObject = new JSONObject();

            RsvStayDto rsvStayDto = ypbMapper.getRsvInfo(intRsvID);
            String strPropertyId = rsvStayDto.getStrPropertyID();
            String strRmNum = rsvStayDto.getStrRsvRmNum();;
            String strPkgCode = rsvStayDto.getStrMapCode();
            String strRsvID = String.valueOf(intRsvID);
            String strName = rsvStayDto.getStrRcvName();
            String strPhone = rsvStayDto.getStrRcvPhone();

            String strCustNo = "";
            if(strPropertyId.equals("11")) { // 용평
                strCustNo = Constants.yongpyongCode;
            }else if(strPropertyId.equals("22")){ // 비체
                strCustNo = Constants.beacheCode;
            }

            dataObject.put("brch_cd", strPropertyId); // 용평 : 11 / 비체 : 22
            dataObject.put("rsrv_no", strRmNum); // 예약번호
            dataObject.put("outlet_cd", ""); // 영업장 코드
            dataObject.put("room_type_cd", "");
            dataObject.put("arrv_date", "");
            dataObject.put("rsvpl_type_cd", "07"); // condo24
            dataObject.put("pkg_cd", strPkgCode);
            dataObject.put("bkng_id", strRsvID); // 주문번호
            dataObject.put("cust_no", strCustNo); // 용평 : 1199719 / 비체 : 1178413
            dataObject.put("guest_name", strName); // 투숙객명
            dataObject.put("guest_contp", strPhone); // 투숙객 연락처

            mainObject.put("DATA", dataObject);

            JsonNode jsonNode = commonFunction.callJsonApi("YPB", "bookingInfo", mainObject, "", "POST");

            // 통신결과 0:실패, 1:성공
            JSONObject codeObject = (JSONObject) new JSONParser().parse(jsonNode.get("HEADER").toString());
            String resultCode = codeObject.get("statusCode").toString();

            JSONObject resultObject = (JSONObject) new JSONParser().parse(jsonNode.get("DATA").toString());

            if(resultCode.equals("1")){

                message = "조회 완료";
            }else{
                message = "호출 실패";
            }

        }catch (Exception e){
            message = "예약 조회 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn("json", statusCode, message, result);

    }


    public String bookingCancel(int intRsvID, String dataType){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

            JSONObject mainObject = getCommonHeader("bookingCancel");
            JSONObject dataObject = new JSONObject();

            RsvStayDto rsvStayDto = ypbMapper.getRsvInfo(intRsvID);
            String strPropertyId = rsvStayDto.getStrPropertyID();
            String strRmNum = rsvStayDto.getStrRsvRmNum();;
            String strLocalCode = rsvStayDto.getStrLocalCode();
            String strRmTypeID = rsvStayDto.getStrRmtypeID();
            String strDate = new SimpleDateFormat("yyyyMMdd").format(rsvStayDto.getDateCheckIn());
            String strPkgCode = rsvStayDto.getStrMapCode();
            String strRsvID = String.valueOf(intRsvID);
            String strName = rsvStayDto.getStrRcvName();
            String strPhone = rsvStayDto.getStrRcvPhone();

            String strCustNo = "";
            if(strPropertyId.equals("11")) { // 용평
                strCustNo = Constants.yongpyongCode;
            }else if(strPropertyId.equals("22")){ // 비체
                strCustNo = Constants.beacheCode;
            }

            dataObject.put("brch_cd", strPropertyId); // 용평 : 11 / 비체 : 22
            dataObject.put("rsrv_no", strRmNum); // 예약번호
            dataObject.put("outlet_cd", strLocalCode); // 영업장 코드
            dataObject.put("room_type_cd", strRmTypeID);
            dataObject.put("arrv_date", strDate);
            dataObject.put("rsvpl_type_cd", "07"); // condo24
            dataObject.put("pkg_cd", strPkgCode);
            dataObject.put("bkng_id", strRsvID); // 주문번호
            dataObject.put("cust_no", strCustNo); // 용평 : 1199719 / 비체 : 1178413
            dataObject.put("guest_name", strName); // 투숙객명
            dataObject.put("guest_contp", strPhone); // 투숙객 연락처
            dataObject.put("guest_sms_send_yn", "N"); // 취소 메세지 전송 여부

            mainObject.put("DATA", dataObject);

            System.out.println(mainObject);

            JsonNode jsonNode = commonFunction.callJsonApi("YPB", "bookingCancel", mainObject, "", "POST");

            // 통신결과 0:실패, 1:성공
            JSONObject codeObject = (JSONObject) new JSONParser().parse(jsonNode.get("HEADER").toString());
            String resultCode = codeObject.get("statusCode").toString();

            if(resultCode.equals("1")){
                // 프로시저 작업 해야함
                String procResult = ypbMapper.updateRsv(intRsvID, "5", strRmNum);
                if(procResult.equals("저장완료")){
                    message = "취소 완료";
                }else{
                    message = "DB저장 실패";
                }

            }else{
                message = "호출 실패";
            }

            result = jsonNode.toString();
            System.out.println(result);

        }catch (Exception e){
            message = "예약 취소 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn(dataType, statusCode, message);

    }


    public String getBookingList(){

        String result = "";

        try {

            JSONObject mainObject = getCommonHeader("bookingList");
            JSONObject dataObject = new JSONObject();

            dataObject.put("brch_cd", "11"); // 용평 : 11 / 비체 : 22
            dataObject.put("rsvpl_type_cd", "07"); // condo24
            dataObject.put("pkg_cd", "MOP2330");
            dataObject.put("cust_no", "1199719"); // 용평 : 1199719 / 비체 : 1178413

            mainObject.put("DATA", dataObject);

            System.out.println(mainObject);

            JsonNode jsonNode = commonFunction.callJsonApi("YPB", "bookingList", mainObject, "", "POST");

            result = jsonNode.toString();
            System.out.println(result);

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;


    }



    public static JSONObject getCommonHeader(String type){
        System.out.println(System.currentTimeMillis());

        JSONObject mainObject = new JSONObject();

        // type 별 변수 구하기
        String interfaceId = "";
        if(type.equals("stock")) {
            interfaceId = "getRsrvMm";
        }else if(type.equals("booking")){
            interfaceId = "joinSalesPkgRsrvInfo";
        }else if(type.equals("bookingInfo")){
            interfaceId = "getSalesRsrvList";
        }else if(type.equals("bookingCancel")){
            interfaceId = "joinSalesPkgRsrvCncl";
        }else if(type.equals("bookingList")){
            interfaceId = "getArrvRsrvList";
        }

        try {

            JSONObject headerObject = new JSONObject();


            headerObject.put("statusCode", "1");
            headerObject.put("interfaceId", interfaceId);
            headerObject.put("status", "ok");
            headerObject.put("resultCount", 1);
            headerObject.put("systemStatusCode", 1);
            headerObject.put("systemStatus", "ok");
            headerObject.put("isArray", "false");


            mainObject.put("HEADER", headerObject);


        } catch (Exception e){
            e.printStackTrace();
        }

        return mainObject;

    }
}
