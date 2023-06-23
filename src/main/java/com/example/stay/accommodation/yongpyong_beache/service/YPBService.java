package com.example.stay.accommodation.yongpyong_beache.service;

import com.example.stay.common.service.CommonService;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class YPBService {

    @Autowired
    CommonService commonService;

    public String getStock(){

        String result = "";

        try {

            JSONObject mainObject = getCommonHeader("stock");
            JSONObject dataObject = new JSONObject();

            dataObject.put("brch_cd", "11"); // 용평 : 11 / 비체 : 22
            dataObject.put("outlet_cd", "223001"); // 영업장 코드
            dataObject.put("room_type_cd", "");
            dataObject.put("from_date", "20230623");
            dataObject.put("to_date", "20230714");
            dataObject.put("rsvpl_type_cd", "07"); // condo24
            dataObject.put("pkg_cd", "MOP2330");
            dataObject.put("cust_no", "1199719"); // 용평 : 1199719 / 비체 : 1178413

            mainObject.put("DATA", dataObject);

            System.out.println(mainObject);

            JsonNode jsonNode = commonService.callJsonApi("YPBStock", mainObject);

            result = jsonNode.toString();
            System.out.println(result);

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

            JsonNode jsonNode = commonService.callJsonApi("YPBBooking", mainObject);

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

            JsonNode jsonNode = commonService.callJsonApi("YPBBookingInfo", mainObject);

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

            JsonNode jsonNode = commonService.callJsonApi("YPBBookingCancel", mainObject);

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

            JsonNode jsonNode = commonService.callJsonApi("YPBBookingList", mainObject);

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
