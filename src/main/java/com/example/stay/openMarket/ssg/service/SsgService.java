package com.example.stay.openMarket.ssg.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.openMarket.ssg.mapper.SsgMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Service
public class SsgService {

    @Autowired
    private SsgMapper ssgMapper;

    CommonFunction commonFunction = new CommonFunction();



    public String getAccommInfo(int intAID){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

            String strItemId = ssgMapper.getItemId(intAID);

            JsonNode jsonNode = commonFunction.callJsonApi("SSG","", new JSONObject(), "https://eapi.ssgadm.com/item/0.4/viewItem.ssg?itemId="+strItemId,"POST");

            System.out.println(jsonNode);

        }catch (Exception e){
            message = " 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn("json", statusCode, message, result);
    }


    /**
     * 배송지시목록조회
     * @param strStarteDate
     * @param strEndDate
     * @return
     */
    public String getReserveList(String strStarteDate, String strEndDate, String dataType){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

            JSONObject mainObject = new JSONObject();
            JSONObject innerObject = new JSONObject();

            innerObject.put("perdType","01");
            innerObject.put("perdStrDts",strStarteDate);
            innerObject.put("perdEndDts",strEndDate);

            mainObject.put("requestShppDirection",innerObject);

            JsonNode jsonNode = commonFunction.callJsonApi("SSG","", mainObject, "https://eapi.ssgadm.com/api/pd/1/listShppDirection.ssg","POST");
//            System.out.println(jsonNode);
//            System.out.println(jsonNode.get("result").get("shppDirections").get(0).size());

            int intCnt = jsonNode.get("result").get("shppDirections").get(0).size();
            if(intCnt > 0){
                JSONArray jsonArray = (JSONArray) new JSONParser().parse(jsonNode.get("result").get("shppDirections").toString());
                System.out.println(jsonArray);
                for(Object object : jsonArray) {
                    JSONObject dataObject = (JSONObject) JSONValue.parse(object.toString());
                    JSONObject jsonObject = (JSONObject) dataObject.get("shppDirection");
                    if(jsonObject.get("shppStatCd").toString().equals("30")){

                        String strRsvCode = "test";
                        String strProductID = jsonObject.get("itemId").toString();
//                        int intAID = ssgMapper.getIntAID(strProductID);
                        int intAID = 101471;
                        int intStockIdx = Integer.parseInt(jsonObject.get("uSplVenItemId").toString());
                        //Map<String, String> map = ssgMapper.getRmIdxNChechIn(intStockIdx);
                        //int intRmIdx = Integer.parseInt(map.get("intRmIdx").toString());
                        int intRmIdx = 15302;
                        int intRmCnt = Integer.parseInt(jsonObject.get("ordQty").toString());
                        String strItemName = jsonObject.get("uitemNm").toString();

                        // 체크인, 체크아웃
                        //String strCheckIn = map.get("dateSales").toString();
                        String strCheckIn = ssgMapper.getCheckIn(intStockIdx);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate localDate = LocalDate.parse(strCheckIn, formatter);
                        localDate = localDate.plusDays(1);
                        String strCheckOut = localDate.format(formatter);

                        String strRmtypeName = strItemName.replace(strCheckIn+"/", "");
                        String strOrdName = jsonObject.get("ordpeNm").toString();
                        String strOrdPhone = jsonObject.get("ordpeHpno").toString();
                        String strRcvName = jsonObject.get("rcptpeNm").toString();
                        String strRcvPhone = jsonObject.get("rcptpeHpno").toString();
                        String strRemark = (jsonObject.get("ordMemoCntt").toString().equals("미입력") || jsonObject.get("ordMemoCntt").toString().equals("미입력"))? "" : jsonObject.get("ordMemoCntt").toString();
                        String strOrderCode = jsonObject.get("ordNo").toString();
                        int intOrderSeq = Integer.parseInt(jsonObject.get("ordItemSeq").toString());
                        String strOrderPackage = jsonObject.get("orordNo").toString();

                        //result = ssgMapper.createBooking(42,strRsvCode,intAID, intRmIdx, intRmCnt,strCheckIn,strCheckOut,strRmtypeName,strOrdName,strOrdPhone,strRcvName,strRcvPhone,strRemark,strOrderCode,intOrderSeq,strProductID,strOrderPackage);
                        System.out.println(result);


                    }
                }
            }else{
                message = " 예약 없음";
                statusCode = "200";
            }



        }catch (Exception e){
            message = " 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
        /* result sample
            {
                "result": {
                    "resultCode": "00",
                    "resultMessage": "SUCCESS",
                    "resultDesc": "성공",
                    "shppDirections": [{
                            "shppDirection": {
                                "ordNo": "20230728EAA107",
                                "ordItemSeq": 1,
                                "orordNo": "20230728EAA107",
                                "orordItemSeq": 1,
                                "shppNo": 10005681523,
                                "shppSeq": 1,
                                "ordStatCd": 110,
                                "shppStatCd": 30,
                                "shppStatNm": "대기",
                                "itemId": 1000028463390,
                                "itemNm": "금호설악리조트(강원도/속초)",
                                "splVenItemId": 10002,
                                "uSplVenItemId": 370756944,
                                "ordCstId": 2155487025,
                                "shppcst": 0,
                                "ordCstOccCd": "부과",
                                "shppcstCodYn": "N",
                                "ordRcpDts": "2023-07-28 09:50:17",
                                "ordpeNm": "이윤경",
                                "rcptpeNm": "이윤경",
                                "rcptpeHpno": "010-2388-7361",
                                "rcptpeTelno": "--",
                                "shppDivDtlCd": 11,
                                "shppDivDtlNm": "일반출고",
                                "shppProgStatDtlCd": 11,
                                "shppRsvtDt": 20230729,
                                "uitemId": 52387,
                                "uitemNm": "08월17일(목)/스위트디럭스＋조식2인(8/6－31투숙객대상)",
                                "siteNo": 6004,
                                "shppVenId": "0017535226",
                                "shppVenNm": "(주)동무해피데이즈",
                                "rsvtItemYn": "N",
                                "dircItemQty": 1,
                                "cnclItemQty": 0,
                                "ordQty": 1,
                                "splprc": 97018,
                                "sellprc": 116000,
                                "rlordAmt": 116000,
                                "dcAmt": 0,
                                "ordpeHpno": "01023887361",
                                "shpplocAddr": "충청남도 천안시 서북구 두정동 1371 팰리스피아 222",
                                "shpplocZipcd": 31106,
                                "shpplocOldZipcd": 331962,
                                "ordMemoCntt": "[고객배송메모]부재 시 문 앞에 놓아주세요",
                                "ordpeRoadAddr": "충청남도 천안시 서북구 두정상가2길 9, 222 (두정동)",
                                "ordShpplocId": 1454539070,
                                "shppTypeDtlCd": 14,
                                "reOrderYn": "N",
                                "itemDiv": 10,
                                "shpplocBascAddr": "충청남도 천안시 서북구 두정상가2길",
                                "shpplocDtlAddr": "9, 222 (두정동)",
                                "ordItemDivNm": "주문",
                                "shppItemDivCd": "01",
                                "shppcstId": "0000516571",
                                "mallTypeCd": 10,
                                "ordTypeCd06Yn": "N",
                                "ordItemCertNoYn": "N"
                            }
                        }
                    ]
                }
            }
         */
    }

    /**
     * 출고 대상 목록 조회
     * @param strStarteDate
     * @param strEndDate
     * @return
     */
    public String getReleaseList(String strStarteDate, String strEndDate){

        String result = "";

        try {

            JSONObject mainObject = new JSONObject();
            JSONObject innerObject = new JSONObject();

            innerObject.put("perdType","01");
            innerObject.put("perdStrDts",strStarteDate);
            innerObject.put("perdEndDts",strEndDate);

            mainObject.put("requestWarehouseOut",innerObject);

            JsonNode jsonNode = commonFunction.callJsonApi("SSG","", mainObject, "https://eapi.ssgadm.com/api/pd/1/listWarehouseOut.ssg","POST");
            System.out.println(jsonNode);

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 배송완료관리
     * @param strStarteDate
     * @param strEndDate
     * @return
     */
    public String getFinishList(String strStarteDate, String strEndDate, String strOrderNo){

        String result = "";

        try {

            JSONObject mainObject = new JSONObject();
            JSONObject innerObject = new JSONObject();

            innerObject.put("perdType","01");
            innerObject.put("perdStrDts",strStarteDate);
            innerObject.put("perdEndDts",strEndDate);
            innerObject.put("commType","01");
            innerObject.put("commValue",strOrderNo);

            mainObject.put("requestDeliveryEnd",innerObject);

            JsonNode jsonNode = commonFunction.callJsonApi("SSG","", mainObject, "https://eapi.ssgadm.com/api/pd/1/listDeliveryEnd.ssg","POST");
            System.out.println(jsonNode);

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 주문별 상태 조회
     * @param strOrderNo
     * @return
     */
    public String getReserveDetail(String strOrderNo){

        String result = "";

        try {

            JsonNode jsonNode = commonFunction.callJsonApi("SSG", "", new JSONObject(), "https://eapi.ssgadm.com/api/claim/v2/order/"+strOrderNo, "GET");
            System.out.println(jsonNode);

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 취소 신청 목록 조회
     * @param startDate
     * @param endDate
     * @return
     */
    public String getCancelList(String startDate, String endDate){

        String result = "";

        try {

            JsonNode jsonNode = commonFunction.callJsonApi("SSG", "", new JSONObject(), "https://eapi.ssgadm.com/api/claim/v2/cancel/requests?perdStrDts="+startDate+"&perdEndDts="+endDate, "GET");
            System.out.println(jsonNode);

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 시설 브랜드 ID 조회
     * @param intAID
     * @return
     */
    public String getBrandId(int intAID){

        String result = "";

        try {


            String strBrandId = ssgMapper.getBrnadId(intAID); // 시설별 브랜드 id 값

            JsonNode jsonNode = commonFunction.callJsonApi("SSG", "", new JSONObject(), "https://eapi.ssgadm.com/venInfo/0.1/listBrand.ssg?brandId="+strBrandId, "GET");

            String brandId = jsonNode.get("result").get("brands").get(0).get("brand").get("brandId").toString();
            System.out.println(strBrandId + "//" + brandId);
            if(brandId.equals(strBrandId)){
                result = brandId;
            }else{
                result = "error";
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }


    /**
     * Q&A 리스트 조회
     * @return
     */
    public String getQnaList(String startDate, String endDate){
        // ex) 8월 1일 리스트 조회시 endDate = 20230803
        String result = "";

        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject qnaObject = new JSONObject();

            startDate = startDate + "0000";
            endDate = endDate + "0000";

            qnaObject.put("qnaStartDt", startDate);
            qnaObject.put("qnaEndDt", endDate);

            jsonObject.put("postngReq", qnaObject);

            JsonNode jsonNode = commonFunction.callJsonApi("SSG", "", jsonObject, "https://eapi.ssgadm.com/api/postng/qnaList.ssg", "POST");
            System.out.println(jsonNode);

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }


    public String answerQna(String strPostngId, String strAnswer){
        String result = "";

        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject qnaObject = new  JSONObject();

            qnaObject.put("postngId", strPostngId);
            qnaObject.put("postngCntt", strAnswer);

            jsonObject.put("postngReq", qnaObject);

            JsonNode jsonNode = commonFunction.callJsonApi("SSG", "", jsonObject, "https://eapi.ssgadm.com/api/postng/ansQna.ssg", "POST");

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }



    /**
     * 위수탁 마감리스트
     * @param strDate
     * @return
     */
    public String getSaleList(String strDate){
        String result = "";

        try {

            /*
            // 오늘 날짜
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String strDate = simpleDateFormat.format(date);
             */
            JsonNode jsonNode = commonFunction.callJsonApi("SSG", "", new JSONObject(), "https://eapi.ssgadm.com/api/settle/v1/ven/sales/list.ssg?critnDt=" + strDate, "GET");
            System.out.println(jsonNode);
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

}
