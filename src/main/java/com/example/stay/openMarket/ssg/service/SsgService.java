package com.example.stay.openMarket.ssg.service;

import com.example.stay.common.util.CommonFunction;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class SsgService {

    CommonFunction commonFunction = new CommonFunction();

    /**
     * 배송지시목록조회
     * @param strStarteDate
     * @param strEndDate
     * @return
     */
    public String getReserveList(String strStarteDate, String strEndDate){

        String result = "";

        try {

            JSONObject mainObject = new JSONObject();
            JSONObject innerObject = new JSONObject();

            innerObject.put("perdType","01");
            innerObject.put("perdStrDts",strStarteDate);
            innerObject.put("perdEndDts",strEndDate);

            mainObject.put("requestShppDirection",innerObject);

            JsonNode jsonNode = commonFunction.callJsonApi("SSG","", mainObject, "https://eapi.ssgadm.com/api/pd/1/listShppDirection.ssg","POST");
            System.out.println(jsonNode);

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
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

}