package com.example.stay.accommodation.onda.controller;

import com.example.stay.accommodation.onda.service.AccommService;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.StringEncrypter;
import com.example.stay.common.util.XmlUtility;
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

@Controller
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

    /**
     * ONDA에서 숙소정보 가져와서 INSERT
     */
    @GetMapping("insertAccommTotal")
    public void insertAccommTotal(){
        accommService.insertAccommTotal();
    }

    /**
     * 시설(시설+이미지+취소규정) 수정
     * @param propertyId
     */
    @GetMapping("updateAccomm")
    public void updateAccomm(String propertyId){
        accommService.updateAccomm(propertyId);
    }

    /**
     * 룸타입, ratePlan 등록 및 수정
     * @param propertyId
     * @param roomTypeId
     */
    @GetMapping("updateRoomNRatePlan")
    public void updateRoomNRatePlan(String propertyId, String roomTypeId){
        String ratePlanId = "";
        accommService.updateRoomNRatePlan(propertyId, roomTypeId, ratePlanId);
    }

    /**
     * 특정 패키지의 재고 및 요금 정보 가져와서 insert or update
     */
    @GetMapping("updateGoods")
    public void updateGoods(int rateplan_id, String from, String to){
        accommService.updateGoods(rateplan_id, from, to);
    }

    /**
     * WEBHOOK AuthKey 발급
     */
    @GetMapping("webhookAuth")
    public String webhookAuth(){
        String authKey = "";
        try{
            StringEncrypter encrypter = new StringEncrypter("leezeno.com", "condo24.com");

//            String encrypted = encrypter.encrypt("148|syjung618|손유정|yoojung.son@plnd.co.kr|05|07||210.206.23.116|1||");
            authKey = encrypter.encrypt("onda_AuthKey_Regist");
//            String decodeText = URLDecoder.decode(encrypted, "UTF-8");
            System.out.println("authKey : " + authKey); // AuthKey : b/8waV7zIhA5w8O1BnpHhmikDvaCTnDFpJwmJVdkCbo=
//            String decrypted = encrypter.decrypt(encrypted);
//            System.out.println("decrypted : " + decrypted);

//            JSONObject authKeyJson = new JSONObject();
//            authKeyJson.put("Auth_key", encrypted);

        }catch (Exception e){
            e.printStackTrace();
        }

        return authKey;

    }


    /**
     * WEBHOOK
     */
    @PutMapping("webhook")
    @ResponseBody
    public ResponseEntity<Object> webhook(HttpServletRequest request){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        Map<String,Object> responseMap = new HashMap<String,Object>();

        try{
            String authKey = request.getHeader("Authorization");
            if(authKey.equals(Constants.webhookAuthKey)){
                httpStatus = HttpStatus.OK;
                responseMap.put("httpStatus", httpStatus.value());
                responseMap.put("message", "success");

                InputStream inputStream = request.getInputStream();
                BufferedReader br = null;
                StringBuilder stringBuilder = new StringBuilder();
                String line = "";
                if (inputStream != null) {
                    br = new BufferedReader(new InputStreamReader(inputStream));
                    //더 읽을 라인이 없을때까지 계속
                    while ((line = br.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    String strBody = stringBuilder.toString();
                    System.out.println(strBody);
                    JSONParser jsonParser = new JSONParser();
                    JSONObject bodyJson = (JSONObject) jsonParser.parse(strBody);

                    accommService.webhookProcess(bodyJson);

                }else {
                    System.out.println("Data 없음");
                }


            }else{
                responseMap.put("httpStatus", httpStatus.value());
                responseMap.put("message", "invaild_AuthKey");

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(responseMap, headers, httpStatus);
    }





//    @GetMapping("getAccommTotal")
//    public ModelAndView getAccommTotal(){
//        List totalList = new ArrayList<Object>();
//
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("ondaAccomm");
//
//        List<JSONObject> accommList = accommService.getAccommListApi(Constants.ondaPath + "properties");
//
//        List<String> ondaIdList = new ArrayList<>();
////        for(JSONObject list : accommList){
////            String propertyId = list.get("id").toString();
////            ondaIdList.add(propertyId);
////        }
//
//        ondaIdList.add("130517");
//        String total = "";
//        for(String propertyId : ondaIdList) {
//            JSONObject accommDetailJson = accommService.getAccommDetailApi(propertyId);
//            JSONObject jsonProperty = (JSONObject) accommDetailJson.get("property");
//            System.out.println(jsonProperty);
//
//            total += "[ACCOMMODATION INFO] <br><br>";
//
//            total += "id : " + jsonProperty.get("id") + "<br>";
//            total += "name : " + jsonProperty.get("name").toString() + "<br>";
//
//            JSONObject address = (JSONObject) jsonProperty.get("address");
//            total += "country_code : " + address.get("country_code") + "<br>";
//            total += "region : " + address.get("region") + "<br>";
//            total += "city : " + address.get("city") + "<br>";
//            total += "address1 : " + address.get("address1") + "<br>";
//            total += "address2 : " + address.get("address2") + "<br>";
//            total += "postal_code : " + address.get("postal_code") + "<br>";
//
//            JSONObject location = (JSONObject) address.get("location");
//            total += "latitude : " + location.get("latitude") + "<br>";
//            total += "longitude : " + location.get("longitude") + "<br>";
//
//            total += "classifications : ";
//            JSONArray classifications = (JSONArray) jsonProperty.get("classifications");
//            for(Object o : classifications){
//                total += o + " ";
//            }
//            total += "<br>";
//
//            total += "properties : ";
//            JSONObject tagsObj = (JSONObject) jsonProperty.get("tags");
//            JSONArray properties = (JSONArray) tagsObj.get("properties");
//            JSONArray facilities = (JSONArray) tagsObj.get("facilities");
//            JSONArray services = (JSONArray) tagsObj.get("services");
//            JSONArray attractions = (JSONArray) tagsObj.get("attractions");
//
//            for(Object o : properties){
//                total += o + " ";
//            }
//
//            total += "<br>facilities : ";
//            for(Object o : facilities){
//                total += o + " ";
//            }
//
//            total += "<br>services : ";
//            for(Object o : services){
//                total += o + " ";
//            }
//
//            total += "<br>attractions : ";
//            for(Object o : attractions){
//                total += o + " ";
//            }
//            total += "<br>";
//
//            total += "tel : " + jsonProperty.get("tel") + "<br>";
//            total += "fax : " + jsonProperty.get("fax") + "<br>";
//            total += "TimeIn : " + jsonProperty.get("TimeIn") + "<br>";
//            total += "TimeOut : " + jsonProperty.get("TimeOut") + "<br>";
//
//            JSONObject descriptions = (JSONObject) jsonProperty.get("descriptions");
//            total += "summary : " + descriptions.get("property") + "<br>";
//            total += "usageNotice : " + descriptions.get("reservation") + "<br>";
//            total += "notice : " + descriptions.get("notice") + "<br>";
//            total += "refund : " + descriptions.get("refund") + "<br><br>";
//
//            total += "images <br>";
//            JSONArray images = (JSONArray) jsonProperty.get("images");
//            for(int i=0; i<images.size(); i++){
//                JSONObject image = (JSONObject) images.get(i);
//                total += "이미지 " + (i+1) + "<br>";
//                total += "original : " + image.get("original") + "<br>";
//                total += "250px : " + image.get("250px") + "<br>";
//                total += "500px : " + image.get("500px") + "<br>";
//                total += "1000px : " + image.get("1000px") + "<br>";
//                total += "description : " + image.get("description") + "<br>";
//                total += "order : " + image.get("order") + "<br>";
//            }
//
//            total += "<br>refunds <br>";
//            JSONObject refunds = (JSONObject) jsonProperty.get("property_refunds");
//            total += "0d : " + refunds.get("0d") + "<br>";
//            total += "1d : " + refunds.get("1d") + "<br>";
//            total += "2d : " + refunds.get("2d") + "<br>";
//            total += "3d : " + refunds.get("3d") + "<br>";
//            total += "4d : " + refunds.get("4d") + "<br>";
//            total += "5d : " + refunds.get("5d") + "<br>";
//            total += "6d : " + refunds.get("6d") + "<br><br>";
//
//            total += "updated : " + jsonProperty.get("updated_at");
//            total += "<hr><hr>";
//
//
//
////-----------------------------------------------------------------------------------
//
//
//
////***************************************************************************************************
//            total += "<br><br><br>[ROOMTYPE INFO]<br><br>";
//            List<JSONObject> roomTypeList = accommService.getRoomTypeListApi(propertyId);
//            List<String> roomtypeArr = new ArrayList<>();
//            for(JSONObject list : roomTypeList){
//                String roomtypeId = list.get("id").toString();
//                roomtypeArr.add(roomtypeId);
//            }
//
//            String roomTypeId = "";
//            JSONArray packageArray = null;
//            String ratePlanId = "";
//            for(Object o : roomtypeArr){
//                roomTypeId = o.toString();
//                JSONObject roomTypeDetailObj = accommService.getRoomTypeDetail(propertyId, roomTypeId);
//                JSONObject roomTypeDetail = (JSONObject) roomTypeDetailObj.get("roomtype");
//
//                total += "roomtypeId : " + roomTypeDetail.get("id")+"<br>";
//                total += "propertyId : " + roomTypeDetail.get("property_id")+"<br>";
//                total += "name : " + roomTypeDetail.get("name")+"<br>";
//                total += "status : " + roomTypeDetail.get("status")+"<br>";
//                total += "description : " + roomTypeDetail.get("description")+"<br>";
//                total += "size : " + roomTypeDetail.get("size")+"<br>";
//
//                total += "capacity<br>";
//                JSONObject capacity = (JSONObject) roomTypeDetail.get("capacity");
//                total += "standard : " + capacity.get("standard") + "<br>";
//                total += "max : " + capacity.get("max") + "<br>";
//
//                total += "tags<br>";
//                JSONObject roomTagsObj = (JSONObject) roomTypeDetail.get("tags");
//                JSONArray roomtypes = (JSONArray) roomTagsObj.get("roomtypes");
//                JSONArray views = (JSONArray) roomTagsObj.get("views");
//                JSONArray amenities = (JSONArray) roomTagsObj.get("amenities");
//
//                total += "roomtypes : ";
//                for(Object tagRoomtypes : roomtypes){
//                    total += tagRoomtypes + " ";
//                }
//
//                total += "<br>views : ";
//                for(Object tagViews : views){
//                    total += tagViews + " ";
//                }
//
//                total += "<br>amenities : ";
//                for(Object tagAmenities : amenities){
//                    total += tagAmenities + " ";
//                }
//
//                total += "<br>details<br>";
//                JSONObject roomdDetailObj = (JSONObject) roomTypeDetail.get("details");
//                total += "room : " + roomdDetailObj.get("room") + "<br>";
//                total += "ondolroom : " + roomdDetailObj.get("ondolroom") + "<br>";
//                total += "bedroom : " + roomdDetailObj.get("bedroom") + "<br>";
//                total += "livingroom : " + roomdDetailObj.get("livingroom") + "<br>";
//                total += "kitchen : " + roomdDetailObj.get("kitchen") + "<br>";
//                total += "bathroom : " + roomdDetailObj.get("bathroom") + "<br>";
//
//                total += "<br>bedtype<br>";
//                JSONObject bedTypeObj = (JSONObject) roomTypeDetail.get("bedtype");
//                total += "single_beds : " + bedTypeObj.get("single_beds") + "<br>";
//                total += "super_single_beds : " + bedTypeObj.get("super_single_beds") + "<br>";
//                total += "double_beds : " + bedTypeObj.get("double_beds") + "<br>";
//                total += "queen_beds : " + bedTypeObj.get("queen_beds") + "<br>";
//                total += "king_beds : " + bedTypeObj.get("king_beds") + "<br>";
//                total += "sofa_beds : " + bedTypeObj.get("sofa_beds") + "<br>";
//                total += "air_beds : " + bedTypeObj.get("air_beds") + "<br>";
//
//                total += "images <br>";
//                JSONArray roomImages = (JSONArray) jsonProperty.get("images");
//                for(int i=0; i<images.size(); i++){
//                    JSONObject image = (JSONObject) roomImages.get(i);
//                    total += "이미지 " + (i+1) + "<br>";
//                    total += "original : " + image.get("original") + "<br>";
//                    total += "250px : " + image.get("250px") + "<br>";
//                    total += "500px : " + image.get("500px") + "<br>";
//                    total += "1000px : " + image.get("1000px") + "<br>";
//                    total += "description : " + image.get("description") + "<br>";
//                    total += "order : " + image.get("order") + "<br><br>";
//                }
//
//                total += "updated : " + jsonProperty.get("updated_at");
//
////***************************************************************************************************
//
//                total += "<br><br><br>[PACKAGE INFO]<br><br>";
//
//                List<JSONObject> packageList = accommService.getPackageList(propertyId, roomTypeId);
//                for(JSONObject list : packageList){
//                    ratePlanId = list.get("id").toString();
//
//                    JSONObject packageDetailJson = accommService.getPackageDetail(propertyId, roomTypeId, ratePlanId);
//
//                    JSONObject packageDetail = (JSONObject) packageDetailJson.get("rateplan");
//                    total += "packageId : " + packageDetail.get("id") + "<br>";
//                    total += "propertyId : " + packageDetail.get("property_id") + "<br>";
//                    total += "roomtypeId : " + packageDetail.get("roomtype_id") + "<br>";
//                    total += "name : " + packageDetail.get("name") + "<br>";
//                    total += "status : " + packageDetail.get("status") + "<br>";
//                    total += "type : " + packageDetail.get("type") + "<br>";
//
//                    total += "length of stay<br>";
//                    JSONObject stayLength = (JSONObject) packageDetail.get("length_of_stay");
//                    total += "min : " + stayLength.get("min") + "<br>";
//                    total += "max : " + stayLength.get("max") + "<br>";
//
//                    total += "sales terms<br>";
//                    JSONObject salesTerms = (JSONObject) packageDetail.get("sales_terms");
//                    total += "from : " + salesTerms.get("from") + "<br>";
//                    total += "to : " + salesTerms.get("to") + "<br>";
//
//                    total += "description : " + packageDetail.get("description") + "<br>";
//                    total += "refundable : " + packageDetail.get("refundable") + "<br>";
//
//                    total += "meal<br>";
//                    JSONObject meal = (JSONObject) packageDetail.get("meal");
//                    total += "breakfast : " + meal.get("breakfast") + "<br>";
//                    total += "lunch : " + meal.get("lunch") + "<br>";
//                    total += "dinner : " + meal.get("dinner") + "<br>";
//                    total += "meal_count : " + meal.get("meal_count") + "<br>";
//
//                    total += "updated : " + packageDetail.get("updated_at") + "<br>";
//
//
//
//                    total += "<br><br>";
//
////                    total += "<br><br><br>[INVENTORYS]<br><br>";
////                    List<JSONObject> inventoryList = accommService.getInventories(ratePlanId, "2023-05-22", "2023-05-22");
////                    for(JSONObject inventories : inventoryList){
////
////                        total += "rateplan_id : " + inventories.get("rateplan_id") + "<br>";
////
////
////                    }
//
//
//                }
//
//                total += "<br><hr>";
//
//            }
//
//
//
//
//
//        }
//
//
//        totalList.add(total);
//
//        modelAndView.addObject("response", totalList);
//
//
//        return modelAndView;
//    }

}
