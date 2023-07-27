package com.example.stay.accommodation.onda.controller;

import com.example.stay.accommodation.onda.service.AccommService;
import com.example.stay.common.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.*;

@Controller("onda.AccommController")
@RequestMapping("/onda/accomm/*")
public class AccommController {

    @Autowired
    private AccommService accommService;

//    /**
//     * 전체 숙소 목록 가져오기
//     */
//    @GetMapping("getAccommList")
////    @RequestParam(value = "status", required = false) String status
//    public void getAccommList(){
//        String path = Constants.ondaPath + "properties?status=all";
//
////        if(status != null){
////            path += "?status=" +  status;
////        }
//
//        accommService.getAccommListApi(path);
//    }

//    /**
//     * 특정 숙소 상세정보 가져오기
//     */
//    @GetMapping("getAccommDetail")
//    public void getAccommDetail(String property_id){
//        accommService.getAccommDetailApi(property_id);
//    }
//
//    /**
//     * 특정 숙소 전체 객실 목록 가져오기
//     */
//    @GetMapping("getRoomtypeList")
//    public void getRoomTypeList(String property_id){
//        accommService.getRoomTypeListApi(property_id);
//    }
//
//    /**
//     * 특정 객실 상세정보 가져오기
//     */
//    @GetMapping("getRoomTypeDetail")
//    public void getRoomTypeDetail(String property_id, String roomtype_id){
//        accommService.getRoomTypeDetail(property_id, roomtype_id);
//    }
//
//    /**
//     * 특정 객실의 전체 패키지 목록 가져오기
//     */
//    @GetMapping("getRatePlanList")
//    public void getRatePlanList(String property_id, String roomtype_id){
//        accommService.getRatePlanList(property_id, roomtype_id);
//    }
//
//    /**
//     * 특정 패키지의 상세 정보 가져오기
//     */
//    @GetMapping("getRatePlanDetail")
//    public void getRatePlanDetail(String property_id, String roomtype_id, String rateplan_id){
//        accommService.getRatePlanDetail(property_id, roomtype_id, rateplan_id);
//    }

    CommonFunction commonFunction = new CommonFunction();

    /**
     * ONDA에서 숙소정보 가져와서 INSERT
     */
    @GetMapping("insertAccommTotal")
    @ResponseBody
    public String insertAccommTotal(String dataType, HttpServletRequest httpServletRequest){
        return accommService.insertAccommTotal(dataType, httpServletRequest);
    }

    /**
     * 시설(시설+이미지+취소규정) 수정
     * @param propertyId
     */
    @GetMapping("updateAccomm")
    @ResponseBody
    public String updateAccomm(String dataType, String propertyId){

        JSONObject jsonObject =  accommService.updateAccomm(propertyId);
        String code = jsonObject.get("statusCode").toString();
        String message = jsonObject.get("message").toString();

        return commonFunction.makeReturn(dataType, code, message);
    }

    /**
     * 룸타입, ratePlan 등록 및 수정
     * @param strPropertyID
     * @param strRmtypeID
     * @param strRateplanID
     * @return
     */
    @GetMapping("updateRmtype")
    @ResponseBody
    public String updateRmtype(String dataType, String strPropertyID, String strRmtypeID, @Nullable String strRateplanID, HttpServletRequest httpServletRequest){

        JSONObject jsonObject =  accommService.updateRmtype(strPropertyID, strRmtypeID, strRateplanID, httpServletRequest);
        String code = jsonObject.get("statusCode").toString();
        String message = jsonObject.get("message").toString();

        return commonFunction.makeReturn(dataType, code, message);
    }

    /**
     * 특정 패키지의 재고 및 요금 정보 가져와서 insert or update
     */
    @GetMapping("updateGoods")
    @ResponseBody
    public String updateGoods(String dataType, int intRmIdx, String from, String to){
        return accommService.updateGoods(dataType, intRmIdx, from, to);
    }

    /**
     * 기간내 최저가 숙소 정보 조회
     */
    @GetMapping("getLowestPrice")
    @ResponseBody
    public String getLowestPrice(String dataType, int intAID, String from, String to, HttpServletRequest httpServletRequest){
        return accommService.getLowestPrice(dataType, intAID, from, to, httpServletRequest);
    }

//    /**
//     * WEBHOOK AuthKey 발급
//     */
//    @GetMapping("webhookAuth")
//    public String webhookAuth(){
//        String authKey = "";
//        try{
//            StringEncrypter encrypter = new StringEncrypter("leezeno.com", "condo24.com");
//
//            authKey = encrypter.encrypt("onda_AuthKey_Regist");
//            System.out.println("authKey : " + authKey);
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return authKey;
//    }


    /**
     * WEBHOOK
     */
    @PutMapping("webhook")
    @ResponseBody
    public JSONObject webhook(HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String message = "";
        String statusCode = "500";
        JSONObject returnJson = new JSONObject();

        try{
            String authKey = httpServletRequest.getHeader("Authorization");
            if(authKey.equals(Constants.webhookAuthKey)){
                InputStream inputStream = httpServletRequest.getInputStream();
                BufferedReader br = null;
                StringBuilder stringBuilder = new StringBuilder();
                String line = "";
                if (inputStream != null) {
                    br = new BufferedReader(new InputStreamReader(inputStream));
                    while ((line = br.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    String strBody = stringBuilder.toString();
                    JSONParser jsonParser = new JSONParser();
                    JSONObject bodyJson = (JSONObject) jsonParser.parse(strBody);

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    logWriter.add(gson.toJson(bodyJson));

                    boolean result = accommService.webhookProcess(bodyJson, httpServletRequest);

                    if(result){
                        statusCode = "200";
                        message = "success";
                    }else{
                        statusCode = "500";
                        message = "fail";
                    }

                }else {
                    statusCode = "200";
                    message = "data not found";
                }
            }else{
                statusCode = "401";
                message = "invaild_AuthKey";
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            message = "fail";

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        returnJson.put("statusCode", statusCode);
        returnJson.put("message", message);
        return returnJson;
    }


}
