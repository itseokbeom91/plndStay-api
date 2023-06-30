package com.example.stay.accommodation.yongpyong_beache.service;

import com.example.stay.accommodation.yongpyong_beache.mapper.YPBMapper;
import com.example.stay.common.service.CommonService;
import com.example.stay.common.util.CommonFunction;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class YPBService {

    @Autowired
    private CommonService commonService;

    CommonFunction commonFunction = new CommonFunction();

    @Autowired
    private YPBMapper ypbMapper;

    public String getStock(int intAID, int intRmIdx, int intPkgIdx, String startDate, String endDate){

        String result = "";

        try {

            JSONObject mainObject = getCommonHeader("stock");
            JSONObject dataObject = new JSONObject();

            Map<String, String> acmRmMap = ypbMapper.getAcmRmID(intAID, intRmIdx);
            Map<String, String> pkgLcdMap = ypbMapper.getPkgLcdID(intPkgIdx);
            System.out.println(acmRmMap);
            System.out.println(pkgLcdMap);
            String strProertyId = acmRmMap.get("strPropertyID").toString();
            String strRoomTypeCode = acmRmMap.get("strRmtypeID").toString();
            String strLcdCode = pkgLcdMap.get("strLocalCode").toString();
            String strPackageCode = pkgLcdMap.get("strPkgCode").toString();


            String strCustNo = "1199719"; // default 용평
            String strCateCode = "37"; // default 용평
            if(strProertyId.equals("22")){ // 비체
                strCustNo = "1178413";
                strCateCode = "38";
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
                    String strRoomTypeId = key;
                    String strStockDatas = stockMap.get(key);
                    if(strStockDatas.length() > 1){
                        strStockDatas = strStockDatas.substring(0, strStockDatas.length()-5);
                    }
                    System.out.println(strStockDatas);
                    ypbMapper.insertStock(intAID, intRmIdx, strPackageCode, strStockDatas);
                }

            }

            result = jsonNode.toString();
            //System.out.println(result);



        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }


    public String booking(){

        String result = "";

        try {

            JSONObject mainObject = getCommonHeader("booking");
            JSONObject dataObject = new JSONObject();

            dataObject.put("brch_cd", "11"); // 용평 : 11 / 비체 : 22
            dataObject.put("outlet_cd", "223001"); // 영업장 코드
            dataObject.put("room_type_cd", "41D");
            dataObject.put("arrv_date", "20230714");
            dataObject.put("rsvpl_type_cd", "07"); // condo24
            dataObject.put("pkg_cd", "MOP2330");
            dataObject.put("bkng_id", "2023-0714-10891958677"); // 주문번호
            dataObject.put("rem", "MOP2330패키지"); // 패키지 상품명
            dataObject.put("cust_no", "1199719"); // 용평 : 1199719 / 비체 : 1178413
            dataObject.put("guest_name", "개발테스트"); // 투숙객명
            dataObject.put("guest_contp", "01012345678"); // 투숙객 연락처
            dataObject.put("guest_sms_send_yn", "N"); // 취소 메세지 전송 여부

            mainObject.put("DATA", dataObject);

            System.out.println(mainObject);

//            JsonNode jsonNode = commonService.callJsonApi("YPB", "booking", mainObject);
            JsonNode jsonNode = commonFunction.callJsonApi("YPB", "booking", mainObject, "", "POST");

            result = jsonNode.toString();
            System.out.println(result);

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }


    public String getBookingInfo(){

        String result = "";

        try {

            JSONObject mainObject = getCommonHeader("bookingInfo");
            JSONObject dataObject = new JSONObject();

            dataObject.put("brch_cd", "11"); // 용평 : 11 / 비체 : 22
            dataObject.put("rsrv_no", "233368778"); // 예약번호
            dataObject.put("outlet_cd", "223001"); // 영업장 코드
            dataObject.put("room_type_cd", "");
            dataObject.put("arrv_date", "");
            dataObject.put("rsvpl_type_cd", "07"); // condo24
            dataObject.put("pkg_cd", "MOP2330");
            dataObject.put("bkng_id", "2023-0714-10891958677"); // 주문번호
            dataObject.put("cust_no", "1199719"); // 용평 : 1199719 / 비체 : 1178413
            dataObject.put("guest_name", "개발테스트"); // 투숙객명
            dataObject.put("guest_contp", "01012345678"); // 투숙객 연락처

            mainObject.put("DATA", dataObject);

            System.out.println(mainObject);

//            JsonNode jsonNode = commonService.callJsonApi("YPB", "bookingInfo", mainObject);
            JsonNode jsonNode = commonFunction.callJsonApi("YPB", "bookingInfo", mainObject, "", "POST");

            result = jsonNode.toString();
            System.out.println(result);

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }


    public String bookingCancel(){

        String result = "";

        try {

            JSONObject mainObject = getCommonHeader("bookingCancel");
            JSONObject dataObject = new JSONObject();

            dataObject.put("brch_cd", "11"); // 용평 : 11 / 비체 : 22
            dataObject.put("rsrv_no", "233368778"); // 예약번호
            dataObject.put("outlet_cd", "223001"); // 영업장 코드
            dataObject.put("room_type_cd", "41D");
            dataObject.put("arrv_date", "20230714");
            dataObject.put("rsvpl_type_cd", "07"); // condo24
            dataObject.put("pkg_cd", "MOP2330");
            dataObject.put("bkng_id", "2023-0714-10891958677"); // 주문번호
            dataObject.put("cust_no", "1199719"); // 용평 : 1199719 / 비체 : 1178413
            dataObject.put("guest_name", "개발테스트"); // 투숙객명
            dataObject.put("guest_contp", "01012345678"); // 투숙객 연락처
            dataObject.put("guest_sms_send_yn", "N"); // 취소 메세지 전송 여부

            mainObject.put("DATA", dataObject);

            System.out.println(mainObject);

//            JsonNode jsonNode = commonService.callJsonApi("YPB", "bookingCancel", mainObject);
            JsonNode jsonNode = commonFunction.callJsonApi("YPB", "bookingCancel", mainObject, "", "POST");

            result = jsonNode.toString();
            System.out.println(result);

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

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

//            JsonNode jsonNode = commonService.callJsonApi("YPB", "bookingList", mainObject);
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
