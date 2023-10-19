package com.example.stay.accommodation.hanwha.service;

import com.example.stay.accommodation.hanwha.mapper.HanwhaMapper;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.openMarket.common.dto.RsvStayDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.w3c.dom.Node;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class HanwhaService {



    @Autowired
    private HanwhaMapper hanwhaMapper;

    CommonFunction commonFunction = new CommonFunction();


    /**
     * 예약하기
     * @param
     * @return
     */
    public String booking(int intRsvID, String dataType){ // 예약요청 : 01

        String statusCode = "200";
        String message = "";
        String result = "";

        try {
            JSONObject mainObject = getCommonHeader("01");
            JSONObject dataObject = new JSONObject();
            JSONObject detailObject = new JSONObject();

            RsvStayDto rsvStayDto = hanwhaMapper.getRsvInfo(intRsvID);


            String strPackNo = rsvStayDto.getStrPkgCode();
            String strLocCd = rsvStayDto.getStrLocalCode();
            String strRMCd = rsvStayDto.getStrRmtypeID();
            String strRoomCnt = String.valueOf(rsvStayDto.getIntRmCnt());
            String strReserveName = rsvStayDto.getStrOrdName();
            String strReservePhone = rsvStayDto.getStrOrdPhone();
            String strStayName = rsvStayDto.getStrRcvName();
            String strStayPhone = rsvStayDto.getStrRcvPhone();

            // 몇 박인지 구하기
            String strStaycnt = "";
            Date checkInDate = rsvStayDto.getDateCheckIn();
            Date checkOutDate = rsvStayDto.getDateCheckOut();
            long longStayCnt = (checkOutDate.getTime() - checkInDate.getTime()) / 86400000;
            strStaycnt = String.valueOf(longStayCnt);
            System.out.println(strStaycnt);

            // 체크인 날짜 포멧
            String strDate = new SimpleDateFormat("yyyyMMdd").format(checkInDate);
            System.out.println(strDate);

            // 번호 구하기
            String strStayPhone1 = "010";
            String strStayPhone2 = "";
            String strStayPhone3 = "";
            if(strStayPhone.substring(0,3).equals("010")){
                System.out.println("phone"+strStayPhone);
                strStayPhone2 = strStayPhone.substring(3,7);
                strStayPhone3 = strStayPhone.substring(7,11);
            }else{
                int lenPhone = strStayPhone.length();
                int stand = lenPhone/3;
                strStayPhone1 = strStayPhone.substring(0, stand);
                strStayPhone2 = strStayPhone.substring(stand, stand*2);
                strStayPhone3 = strStayPhone.substring(stand*2, lenPhone);
            }

            String strReservePhone1 = "010";
            String strReservePhone2 = "";
            String strReservePhone3 = "";
            if(strReservePhone.substring(0,3).equals("010")){
                System.out.println("phone"+strReservePhone);
                strReservePhone2 = strReservePhone.substring(3,7);
                strReservePhone3 = strReservePhone.substring(7,11);
            }else{
                int lenPhone = strReservePhone.length();
                int stand = lenPhone/3;
                strReservePhone1 = strReservePhone.substring(0, stand);
                strReservePhone2 = strReservePhone.substring(stand, stand*2);
                strReservePhone3 = strReservePhone.substring(stand*2, lenPhone);
            }



            detailObject.put("CUST_NO", Constants.hanwhaCustNo);
            detailObject.put("MEMB_NO", "");
            detailObject.put("CUST_IDNT_NO", "");
            detailObject.put("CONT_NO", Constants.hanwhaContNo);
            detailObject.put("PAKG_NO", strPackNo);
            detailObject.put("CPON_NO", "");
            detailObject.put("LOC_CD", strLocCd);
            detailObject.put("ROOM_TYPE_CD", strRMCd);
            detailObject.put("RSRV_LOC_DIV_CD", "C");
            detailObject.put("ARRV_DATE", strDate); //20231010
            detailObject.put("RSRV_ROOM_CNT", strRoomCnt); // 객실 수
            detailObject.put("OVNT_CNT", strStaycnt); // 몇박
            detailObject.put("INHS_CUST_NM", strStayName);
            detailObject.put("INHS_CUST_TEL_NO2", strStayPhone1);
            detailObject.put("INHS_CUST_TEL_NO3", strStayPhone2);
            detailObject.put("INHS_CUST_TEL_NO4", strStayPhone3);
            detailObject.put("RSRV_CUST_NM", strReserveName);
            detailObject.put("RSRV_CUST_TEL_NO2", strReservePhone1);
            detailObject.put("RSRV_CUST_TEL_NO3", strReservePhone2);
            detailObject.put("RSRV_CUST_TEL_NO4", strReservePhone3);
            detailObject.put("REFRESH_YN", "N");

            List<Object> dataList = new ArrayList<>();
            dataList.add(detailObject);

            dataObject.put("ds_rsrvInfo", dataList);

            mainObject.put("Data", dataObject);

            System.out.println(mainObject);

            JsonNode jsonNode = commonFunction.callJsonApi("hanwha", "", mainObject, "", "POST");

            result = jsonNode.toString();
            System.out.println(result);

            // 통신결과 0:실패, 1:성공
            JSONObject codeObject = (JSONObject) new JSONParser().parse(jsonNode.get("MessageHeader").get("MSG_DATA_SUB").get(0).toString());
            String resultCode = codeObject.get("MSG_INDC_CD").toString();

            if(resultCode.equals("1")){
                JSONObject responseObject = (JSONObject) new JSONParser().parse(jsonNode.get("Data").get("ds_prcsResult").get(0).toString());
                String strRsvRmNum = responseObject.get("RSRV_NO").toString();

                // 에악 정보 update
                String procResult = hanwhaMapper.updateRsv(intRsvID, "4", strRsvRmNum);

                if (procResult.trim().equals("저장완료")) {
                    message = "예약 완료";
                } else {
                    message = "DB저장 실패[객실번호 : " + strRsvRmNum + "]";
                }

            }else{
                message = "호출 실패";
            }

        }catch (Exception e){
            message = "예약 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }



    /**
     * 예약 취소
     * @return
     */
    public String bookingCancel(int intRsvID, String dataType){ // 예약취소 : 02

        String statusCode = "200";
        String message = "";
        String result = "";

        try {
            JSONObject mainObject = getCommonHeader("02");
            JSONObject dataObject = new JSONObject();
            JSONObject detailObject = new JSONObject();

            RsvStayDto rsvStayDto = hanwhaMapper.getRsvInfo(intRsvID);
            String strRsvNo = String.valueOf(rsvStayDto.getIntRsvID());

            detailObject.put("CUST_NO", Constants.hanwhaCustNo);
            detailObject.put("RSRV_NO", strRsvNo);

            List<Object> dataList = new ArrayList<>();
            dataList.add(detailObject);

            dataObject.put("ds_cnclInfo", dataList);

            mainObject.put("Data", dataObject);

            System.out.println(mainObject);

            JsonNode jsonNode = commonFunction.callJsonApi("hanwha", "", mainObject, "", "POST");

            result = jsonNode.toString();
            System.out.println(result);

            // 통신결과 0:실패, 1:성공
            JSONObject codeObject = (JSONObject) new JSONParser().parse(jsonNode.get("MessageHeader").get("MSG_DATA_SUB").get(0).toString());
            String resultCode = codeObject.get("MSG_INDC_CD").toString();

            if(resultCode.equals("1")){
                JSONObject responseObject = (JSONObject) new JSONParser().parse(jsonNode.get("Data").get("ds_prcsResult").get(0).toString());
                String strRsvRmNum = responseObject.get("RSRV_NO").toString();

                // 에악 정보 update
                String procResult = hanwhaMapper.updateRsv(intRsvID, "5", strRsvRmNum);

                if (procResult.trim().equals("저장완료")) {
                    message = "취소 완료";
                } else {
                    message = "DB저장 실패[객실번호 : " + strRsvRmNum + "]";
                }

            }else{
                message = "호출 실패";
            }

        }catch (Exception e){
            message = "예약 취소 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 예약조회시 예약확정코드 RR : 확정예약, RC : 취소
    // test 예약넘버 : 2308799080

    /**
     * 예약 조회
     * @return
     */
    public String bookingInfo(){ // 예약조회 : 03

        String statusCode = "200";
        String message = "";
        String result = "";

        try {
            JSONObject mainObject = getCommonHeader("03");
            JSONObject dataObject = new JSONObject();
            JSONObject detailObject = new JSONObject();


            detailObject.put("CUST_NO", Constants.hanwhaCustNo);
            detailObject.put("RSRV_NO", ""); // 예약번호
            detailObject.put("RSRV_DATE_STRT", ""); // 예약한 날짜 조회(투숙날 아님)  *예약번호 있으면 안넣어도됨 기간 조회시 사용
            detailObject.put("RSRV_DATE_END", "");

            List<Object> dataList = new ArrayList<>();
            dataList.add(detailObject);

            dataObject.put("ds_search", dataList);

            mainObject.put("Data", dataObject);

            System.out.println(mainObject);

            JsonNode jsonNode = commonFunction.callJsonApi("hanwha", "", mainObject, "", "POST");

            result = jsonNode.toString();
            System.out.println(result);

        }catch (Exception e){
            message = "예약 조회 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn("json", statusCode, message);
    }


    public String bookingModify(int intRsvID){ // 예약 수정 : 04

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

            JSONObject mainObject = getCommonHeader("04");
            JSONObject dataObject = new JSONObject();
            JSONObject detailObject = new JSONObject();

            RsvStayDto rsvStayDto = hanwhaMapper.getRsvInfo(intRsvID);
            String strRsrvNo = rsvStayDto.getStrRsvRmNum();
            String strDate = new SimpleDateFormat("yyyyMMdd").format(rsvStayDto.getDateCheckIn().toString());
            String strRoomCnt = String.valueOf(rsvStayDto.getIntRmCnt());
            String strReserveName = rsvStayDto.getStrOrdName();
            String strReservePhone = rsvStayDto.getStrOrdPhone();
            String strStayName = rsvStayDto.getStrRcvName();
            String strStayPhone = rsvStayDto.getStrRcvPhone();

            // 몇 박인지 구하기
            String strStaycnt = "";
            Date checkInDate = rsvStayDto.getDateCheckIn();
            Date checkOutDate = rsvStayDto.getDateCheckOut();
            long longStayCnt = (checkOutDate.getTime() - checkInDate.getTime()) / 86400000;
            strStaycnt = String.valueOf(longStayCnt);


            String strStayPhone1 = "010";
            String strStayPhone2 = "";
            String strStayPhone3 = "";
            if(strStayPhone.substring(0,3).equals("010")){
                System.out.println("phone"+strStayPhone);
                strStayPhone2 = strStayPhone.substring(3,7);
                strStayPhone3 = strStayPhone.substring(7,11);
            }else{
                int lenPhone = strStayPhone.length();
                int stand = lenPhone/3;
                strStayPhone1 = strStayPhone.substring(0, stand);
                strStayPhone2 = strStayPhone.substring(stand, stand*2);
                strStayPhone3 = strStayPhone.substring(stand*2, lenPhone);
            }

            String strReservePhone1 = "010";
            String strReservePhone2 = "";
            String strReservePhone3 = "";
            if(strReservePhone.substring(0,3).equals("010")){
                System.out.println("phone"+strReservePhone);
                strReservePhone2 = strReservePhone.substring(3,7);
                strReservePhone3 = strReservePhone.substring(7,11);
            }else{
                int lenPhone = strReservePhone.length();
                int stand = lenPhone/3;
                strReservePhone1 = strReservePhone.substring(0, stand);
                strReservePhone2 = strReservePhone.substring(stand, stand*2);
                strReservePhone3 = strReservePhone.substring(stand*2, lenPhone);
            }

            detailObject.put("CUST_NO", Constants.hanwhaCustNo);
            detailObject.put("RSRV_NO", strRsrvNo);
            detailObject.put("CUST_IDNT_NO", "");
            detailObject.put("ARRV_DATE", strDate); //20231010
            detailObject.put("RSRV_ROOM_CNT", strRoomCnt); // 객실 수
            detailObject.put("OVNT_CNT", strStaycnt); // 몇박
            detailObject.put("INHS_CUST_NM", strStayName);
            detailObject.put("INHS_CUST_TEL_NO2", strStayPhone1);
            detailObject.put("INHS_CUST_TEL_NO3", strStayPhone2);
            detailObject.put("INHS_CUST_TEL_NO4", strStayPhone3);
            detailObject.put("RSRV_CUST_NM", strReserveName);
            detailObject.put("RSRV_CUST_TEL_NO2", strReservePhone1);
            detailObject.put("RSRV_CUST_TEL_NO3", strReservePhone2);
            detailObject.put("RSRV_CUST_TEL_NO4", strReservePhone3);

            List<Object> dataList = new ArrayList<>();
            dataList.add(detailObject);

            dataObject.put("ds_rsrvInfo", dataList);

            mainObject.put("Data", dataObject);

            JsonNode jsonNode = commonFunction.callJsonApi("hanwha", "", mainObject, "", "POST");

            // 통신결과 0:실패, 1:성공
            JSONObject codeObject = (JSONObject) new JSONParser().parse(jsonNode.get("MessageHeader").get("MSG_DATA_SUB").get(0).toString());
            String resultCode = codeObject.get("MSG_INDC_CD").toString();

            if(resultCode.equals("1")){
                message = "수정 완료";
            }else{
                message = "호출 실패";
            }

        }catch (Exception e){
            message = "예약 취소 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn("json", statusCode, message);

    }


    /**
     * 케파 조회
     * @param intAID
     * @param intRmIdx
     * @param strIntPkgIdx
     * @param strLocalCode
     * @param strStartDate
     * @param strEndDate
     * @param dataType
     * @return
     */
    public String getCapa(int intAID, int intRmIdx, String strIntPkgIdx, String strLocalCode, String strStartDate, String strEndDate, String dataType){ // 캐파조회 : 05

        String statusCode = "200";
        String message = "";
        String result = "";

        try {
            JSONObject mainObject = getCommonHeader("05");
            JSONObject dataObject = new JSONObject();
            JSONObject detailObject = new JSONObject();

            String strPackageCode = ""; // 패키지코드
            String strRoomTypeId = hanwhaMapper.getRmID(intAID, intRmIdx); // 룸타입코드

            // 패키지idx 있을시
            if(strIntPkgIdx != null){
                if(strIntPkgIdx.length() > 0) {
                    int intPkgIdx = Integer.parseInt(strIntPkgIdx);

                    Map<String, String> pkgLcdMap = hanwhaMapper.getPkgLcdID(intPkgIdx);
                    strPackageCode = pkgLcdMap.get("strPkgCode").toString();
                }
            }


            detailObject.put("CUST_NO", Constants.hanwhaCustNo);
            detailObject.put("CONT_NO", Constants.hanwhaContNo);
            detailObject.put("LOC_CD", strLocalCode);
            detailObject.put("ROOM_TYPE_CD", strRoomTypeId);
            detailObject.put("PAKG_NO", strPackageCode);
            detailObject.put("STRT_DATE", strStartDate);
            detailObject.put("END_DATE", strEndDate);

            List<Object> dataList = new ArrayList<>();
            dataList.add(detailObject);

            dataObject.put("ds_search", dataList);

            mainObject.put("Data", dataObject);

            System.out.println(mainObject);

//            JsonNode jsonNode = commonService.callJsonApi("hanwha", "", mainObject);
            JsonNode jsonNode = commonFunction.callJsonApi("hanwha", "", mainObject, "", "POST");


            // 통신결과 0:실패, 1:성공
            JSONObject codeObject = (JSONObject) new JSONParser().parse(jsonNode.get("MessageHeader").get("MSG_DATA_SUB").get(0).toString());
            String resultCode = codeObject.get("MSG_INDC_CD").toString();

            System.out.println(jsonNode.get("Data").has("ds_roomStatus"));

            if (jsonNode.get("Data").has("ds_roomStatus") == true) {
                JSONArray jsonArray = (JSONArray) new JSONParser().parse(jsonNode.get("Data").get("ds_roomStatus").toString());
                if (resultCode.equals("1")) {
                    String strStockDatas = "";
                    for (Object object : jsonArray) {
                        JSONObject jsonObject = (JSONObject) JSONValue.parse(object.toString());

                        String strStockLcd = jsonObject.get("LOC_CD").toString();
                        String strStockRMId = jsonObject.get("ROOM_TYPE_CD").toString();
                        String strStockDate = jsonObject.get("SESN_DATE").toString();
                        int intStock = Integer.parseInt(jsonObject.get("RSRV_POSBL_CNT").toString());

                        // 일별 객실료 조회
                        int[] intPriceData = getPrice(strStockLcd, strStockRMId, strPackageCode, strStockDate);

                        // 날짜 포맷
                        String date = strStockDate.trim();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                        Date dateDate = dateFormat.parse(date);
                        SimpleDateFormat formDate = new SimpleDateFormat("yyyy-MM-dd");
                        strStockDate = formDate.format(dateDate);

                        strStockDatas += strStockDate + "|^|" + intStock + "|^|" + intPriceData[1] + "|^|" + intPriceData[0] + "|^|0|^|0|^|0|^|" + intStock + "{{|}}";

                    }
                    if (strStockDatas.length() > 1) {
                        strStockDatas = strStockDatas.substring(0, strStockDatas.length() - 5);
                    }

                    result = hanwhaMapper.insertStock(intAID, intRmIdx, strPackageCode, strStockDatas);

                    String strResult = result.substring(result.length() - 4);
                    if (strResult.equals("저장완료")) {
                        message = "재고 등록 및 수정 완료";
                    } else {
                        message = " 재고 등록 및 수정 실패";
                    }
                } else {
                    message = "error";
                }
            } else {
                message = "패키지와 매칭되는 룸타입이 없습니다.";
            }

            result = jsonNode.toString();
            System.out.println(result);


        }catch (Exception e){
            message = "재고 등록 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    /**
     * 패키지 목록 조회
     * @param strAccommId
     * @param strStartDate
     * @return
     */
    public String getPackageList(String strAccommId, String strStartDate, String resultType){ // 패키지 목록 조회 : 06

        String statusCode = "200";
        String message = "";
        String result = "";
        String resultJson = "";

        try {
            JSONObject mainObject = getCommonHeader("06");
            JSONObject dataObject = new JSONObject();
            JSONObject detailObject = new JSONObject();

            if(strStartDate == null){strStartDate = "";}

            // 날짜 없을시 오늘날짜 넣기
            if(strStartDate.length() == 0){

                // 현재날짜 구하기(yyyyMMdd)
                LocalDate localDate = LocalDate.now();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                strStartDate = localDate.format(dateFormatter);

            }

            if(strAccommId == null){strAccommId = "";}

            detailObject.put("CUST_NO", Constants.hanwhaCustNo);
            detailObject.put("CONT_NO", Constants.hanwhaContNo);
            detailObject.put("LOC_CD", strAccommId);
            detailObject.put("ARRV_DATE", strStartDate);

            List<Object> dataList = new ArrayList<>();
            dataList.add(detailObject);

            dataObject.put("ds_search", dataList);

            mainObject.put("Data", dataObject);

            // API 호출
//            JsonNode jsonNode = commonService.callJsonApi("hanwha", "", mainObject);
            JsonNode jsonNode = commonFunction.callJsonApi("hanwha", "", mainObject, "", "POST");

            // 통신결과 0:실패, 1:성공
            JSONObject codeObject = (JSONObject) new JSONParser().parse(jsonNode.get("MessageHeader").get("MSG_DATA_SUB").get(0).toString());
            String resultCode = codeObject.get("MSG_INDC_CD").toString();

            // 결과값 매핑
            String resultData = "";
            JSONArray jsonArray = (JSONArray) new JSONParser().parse(jsonNode.get("Data").get("ds_result").toString());

            resultJson = jsonArray.toString();

            if(resultCode.equals("1")){
                for(Object object : jsonArray){
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(object.toString());
                    System.out.println(jsonObject);
                    resultData += jsonObject.get("LOC_CD") + "|^|";
                    resultData += jsonObject.get("PAKG_NO") + "|^|";
                    resultData += jsonObject.get("PAKG_NM") + "|^|";
                    resultData += jsonObject.get("VALI_PRID_STRT_DATE") + "|^|";
                    resultData += jsonObject.get("VALI_PRID_END_DATE") + "|^|";
                    resultData += jsonObject.get("SALE_STRT_DATE") + "|^|";
                    resultData += jsonObject.get("SALE_END_DATE") + "|^|";
                    resultData += ((int) Float.parseFloat(jsonObject.get("OVNT_CNT").toString())) + "{{|}}";
                }

                if(resultData.length() > 1){
                    resultData = resultData.substring(0, resultData.length()-5);
                }

                result = hanwhaMapper.packageList(resultData);

                if(result.equals("저장완료")){
                    message = "패키지 등록 완료";
                }else{
                    message = " 패키지 등록 실패";
                }

            }else{
                message = "호출 실패";
            }


        }catch (Exception e){
            message = "패키지 목록 조회 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn(resultType, statusCode, message, resultJson);
    }


    /**
     * 일별 객실료 조회
     * @param strAccommId
     * @param strRoomTypeId
     * @param strPackageCode
     * @param strStartDate
     * @return
     */
    public int[] getPrice(String strAccommId, String strRoomTypeId, String strPackageCode, String strStartDate){ // 일별객실료조회 : 07

        String result = "";
        int intSalePrice = 0; // 판매가
        int intOrigPrice = 0; // 공급가

        try {
            JSONObject mainObject = getCommonHeader("07");
            JSONObject dataObject = new JSONObject();
            JSONObject detailObject = new JSONObject();


            detailObject.put("CUST_NO", Constants.hanwhaCustNo);
            detailObject.put("CONT_NO", Constants.hanwhaContNo);
            detailObject.put("LOC_CD", strAccommId);
            detailObject.put("ROOM_TYPE_CD", strRoomTypeId);
            detailObject.put("PAKG_NO", strPackageCode);
            detailObject.put("ARRV_DATE", strStartDate);
            detailObject.put("OVNT_CNT", "1");
            detailObject.put("RSRV_ROOM_CNT", "1");
            detailObject.put("MEMB_NO", "");
            detailObject.put("RSRV_LOC_DIV_CD", "C");


            List<Object> dataList = new ArrayList<>();
            dataList.add(detailObject);

            dataObject.put("ds_search", dataList);

            mainObject.put("Data", dataObject);

            //System.out.println(mainObject);

//            JsonNode jsonNode = commonService.callJsonApi("hanwha", "", mainObject);
            JsonNode jsonNode = commonFunction.callJsonApi("hanwha", "", mainObject, "", "POST");

            // 통신결과 0:실패, 1:성공
            JSONObject codeObject = (JSONObject) new JSONParser().parse(jsonNode.get("MessageHeader").get("MSG_DATA_SUB").get(0).toString());
            String resultCode = codeObject.get("MSG_INDC_CD").toString();

            // 결과값 매핑
            String roomResult = "";
            String pakgResult = "";
            JSONArray roomArray = new JSONArray();
            JSONArray pakgArray = new JSONArray();


            if(jsonNode.get("Data").has("ds_result")){
                roomResult = jsonNode.get("Data").get("ds_result").toString();
                roomArray = (JSONArray) new JSONParser().parse(roomResult);

            }
            if(jsonNode.get("Data").has("ds_pakgAdiSvcList")){
                pakgResult = jsonNode.get("Data").get("ds_pakgAdiSvcList").toString();
                pakgArray = (JSONArray) new JSONParser().parse(pakgResult);

            }

            if(resultCode.equals("1")){
                if(roomArray.size() > 0 ){
                    for(Object object : roomArray){
                        JSONObject jsonObject = (JSONObject) JSONValue.parse(object.toString());
                        System.out.println(jsonObject.get("CALC_ROOM_RATE"));
                        System.out.println(jsonObject.get("ORIG_ROOM_RATE"));

                        intSalePrice += (int) (Float.parseFloat(jsonObject.get("CALC_ROOM_RATE").toString()));
                        intOrigPrice += (int) (Float.parseFloat(jsonObject.get("ORIG_ROOM_RATE").toString()));
                    }
                }
                if(pakgArray.size() > 0 ){
                    for(Object object : pakgArray){
                        JSONObject jsonObject = (JSONObject) JSONValue.parse(object.toString());
                        System.out.println(jsonObject.get("CALC_SVC_RATE"));
                        System.out.println(jsonObject.get("ORIG_SVC_RATE"));

                        intSalePrice += (int) (Float.parseFloat(jsonObject.get("CALC_SVC_RATE").toString()));
                        intOrigPrice += (int) (Float.parseFloat(jsonObject.get("ORIG_SVC_RATE").toString()));
                    }
                }

            }

            System.out.println("판매가 다 더한 값 " + intSalePrice);
            System.out.println("공급가 다 더한 값 " + intOrigPrice);
            result = jsonNode.toString();
            //System.out.println(result);

        }catch (Exception e){
            e.printStackTrace();
        }

        return new int[]{intSalePrice, intOrigPrice};
    }


    /**
     * 패키지 구성 조회
     * @param strPackageCode
     * @return
     */
    public String getPackageDetail(String strPackageCode){ // 패키지 구성 조회 : 08

        String result = "";

        try {
            JSONObject mainObject = getCommonHeader("08");
            JSONObject dataObject = new JSONObject();
            JSONObject detailObject = new JSONObject();


            detailObject.put("PAKG_NO", strPackageCode);

            List<Object> dataList = new ArrayList<>();
            dataList.add(detailObject);

            dataObject.put("ds_search", dataList);

            mainObject.put("Data", dataObject);

            System.out.println(mainObject);

//            JsonNode jsonNode = commonService.callJsonApi("hanwha", "", mainObject);
            JsonNode jsonNode = commonFunction.callJsonApi("hanwha", "", mainObject, "", "POST");

            result = jsonNode.toString();
            System.out.println(result);

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }


    public static JSONObject getCommonHeader(String type){

        JSONObject mainObject = new JSONObject();

        // type 별 변수 구하기
        String RECV_SVC_CD = "";
        String INTF_ID = "";
        if(type.equals("01")) {                                     // 01 : 예약요청
            RECV_SVC_CD = "HBSREMPRR9901";
            INTF_ID = "LCB00HBSREMPRR9901";
        }else if(type.equals("02")) {                               // 02 : 예약 취소
            RECV_SVC_CD = "HBSREMPRR9902";
            INTF_ID = "LCB00HBSREMPRR9902";
        }else if(type.equals("03")){                                // 03 : 예약 조회
            RECV_SVC_CD = "HBSREMPRR9903";
            INTF_ID = "LCB00HBSREMPRR9903";
        }else if(type.equals("04")){                                // 04 : 예약 수정
            RECV_SVC_CD = "HBSREMPRR9904";
            INTF_ID = "LCB00HBSREMPRR9904";
        }else if(type.equals("05")){                                // 05 : 캐파조회
            RECV_SVC_CD = "HBSREMPRR9905";
            INTF_ID = "LCB00HBSREMPRR9905";
        }else if(type.equals("06")){                                // 06 : 패키지목록조회
            RECV_SVC_CD = "HBSREMPRR9906";
            INTF_ID = "LCB00HBSREMPRR9906";
        }else if(type.equals("07")){                                // 07 : 일별객실료조회
            RECV_SVC_CD = "HBSREMPRR9907";
            INTF_ID = "LCB00HBSREMPRR9907";
        }else if(type.equals("08")){                                // 08 : 패키지구성조회
            RECV_SVC_CD = "HBSREMPRR9931";
            INTF_ID = "LCB00HBSREMPRR9931";
        }

        try {

            JSONObject systemObject = new JSONObject();
            JSONObject transactionObject = new JSONObject();
            JSONObject messageObject = new JSONObject();

            // 현재날짜 구하기(yyyyMMdd)
            LocalDate localDate = LocalDate.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String nowDate = localDate.format(dateFormatter);

            // 랜덤 숫자 5자리 구하기
            int intRandom = (int) Math.random()+10000;
            String strSystemNumber = "LCB" + String.format("%05d",intRandom);

            // 현재시간 unixtime 구하기
            String unix = String.valueOf(System.currentTimeMillis());
            String intunixRandom = String.valueOf((int)(Math.random()*10000)%10);
            String strSeqNo = intunixRandom + unix;
            System.out.println(strSeqNo);

            // ip 주소 구하기
            String ipAdress = getClientIP();
            //System.out.println(ipAdress);

            // 현재시간 구하기(yyyyMMddHHmmss)
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String nowtime = format.format(date);
            //System.out.println(nowtime);


            systemObject.put("TMSG_VER_DV_CD", "01");
            systemObject.put("ENVR_INFO_DV_CD", "D");
            systemObject.put("STN_MSG_ENCP_CD", "0");
            systemObject.put("STN_MSG_COMP_CD", "0");
            systemObject.put("LANG_CD", "KO");
            systemObject.put("TMSG_WRTG_DT", nowDate);
            systemObject.put("TMSG_CRE_SYS_NM", strSystemNumber);
            systemObject.put("STD_TMSG_SEQ_NO", strSeqNo);
            systemObject.put("STD_TMSG_PRGR_NO", "00");
            systemObject.put("STN_TMSG_IP", ipAdress);
            systemObject.put("STN_TMSG_MAC", "00-00-00-00-00-00");
            systemObject.put("FRS_RQST_SYS_CD", "LCB");
            systemObject.put("FRS_RQST_DTM", nowtime);
            systemObject.put("TRMS_SYS_CD", "LCB");
            systemObject.put("RQST_RSPS_DV_CD", "S");
            systemObject.put("TRSC_SYNC_DV_CD", "S");
            systemObject.put("TMSG_RQST_DTM", nowtime);

            transactionObject.put("STN_MSG_TR_TP_CD", "O");
            transactionObject.put("SYSTEM_TYPE", "HABIS");
            transactionObject.put("CORP_CD", "1000");
            transactionObject.put("WRKR_NO", "l1711019");
            transactionObject.put("MASK_AUTH", "0");


            systemObject.put("RECV_SVC_CD", RECV_SVC_CD);
            systemObject.put("INTF_ID", INTF_ID);


            mainObject.put("SystemHeader", systemObject);
            mainObject.put("TransactionHeader", transactionObject);
            mainObject.put("MessageHeader", messageObject);


        } catch (Exception e){
            e.printStackTrace();
        }

        return mainObject;

    }

    public static String getClientIP() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        //System.out.println("> X-FORWARDED-FOR : " + ip);

        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
            //System.out.println("> Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            //System.out.println(">  WL-Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            //System.out.println("> HTTP_CLIENT_IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            //System.out.println("> HTTP_X_FORWARDED_FOR : " + ip);
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
            //System.out.println("> getRemoteAddr : "+ip);
        }
        //System.out.println("> Result : IP Address : "+ip);

        return ip;
    }

}
