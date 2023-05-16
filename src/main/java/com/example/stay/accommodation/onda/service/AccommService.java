package com.example.stay.accommodation.onda.service;

import com.example.stay.accommodation.onda.mapper.AccomodationMapper;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.UrlResourceDownloader;
import com.example.stay.openMarket.common.dto.CancelInfoDto;
import com.example.stay.openMarket.common.dto.CondoDto;
import com.example.stay.openMarket.common.dto.ContentsPhotoDto;
import com.example.stay.openMarket.common.dto.ToconDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AccommService {

    @Autowired
    private AccomodationMapper accomodationMapper;

    public List<JSONObject> getAccommListApi(String path){
        List<JSONObject> accommList = new ArrayList<>();

        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(path)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", Constants.ondaAuth)
                    .build();

            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();
                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                JSONArray accommArray = (JSONArray) responseJson.get("properties");

                for(int i=0; i<accommArray.size(); i++){
                    JSONObject jsonObject = (JSONObject) accommArray.get(i);
                    accommList.add(jsonObject);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 전체 숙소 목록 가져오기 실패");
        }
        return accommList;
    }

    public JSONObject getAccommDetailApi(String property_id){
        JSONObject jsonObject = new JSONObject();
        try{
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(Constants.ondaPath + "properties/" + property_id)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", Constants.ondaAuth)
                    .build();

            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();
//                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                jsonObject = (JSONObject) responseJson.get("property");

            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 특정 숙소 상세정보 가져오기 실패");
        }
        return jsonObject;
    }

    public List<JSONObject> getRoomTypeListApi(String property_id){
        List<JSONObject> roomTypeList = new ArrayList<>();
        try{
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(Constants.ondaPath + "properties/" + property_id + "/roomtypes")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", Constants.ondaAuth)
                    .build();

            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();
                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                JSONArray roomtypeArray = (JSONArray) responseJson.get("roomtypes");

                for(int i=0; i<roomtypeArray.size(); i++){
                    JSONObject jsonObject = (JSONObject) roomtypeArray.get(i);
                    roomTypeList.add(jsonObject);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 특정 숙소 전체 객실 목록 가져오기 실패");
        }
        return roomTypeList;
    }

    public JSONObject getRoomTypeDetail(String property_id, String roomtype_id){
        JSONObject jsonObject = new JSONObject();
        try{
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(Constants.ondaPath + "properties/" + property_id + "/roomtypes/" + roomtype_id)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", Constants.ondaAuth)
                    .build();

            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();
                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                jsonObject = (JSONObject) responseJson.get("roomtype");

            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 특정 객실 상세정보 가져오기 실패");
        }
        return jsonObject;
    }

    public List<JSONObject> getPackageList(String property_id, String roomtype_id){
        List<JSONObject> packageList = new ArrayList<>();
        try{
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(Constants.ondaPath + "properties/" + property_id + "/roomtypes/" + roomtype_id + "/rateplans")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", Constants.ondaAuth)
                    .build();

            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();
                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                JSONArray packageArray = (JSONArray) responseJson.get("rateplans");

                for(int i=0; i<packageArray.size(); i++){
                    JSONObject jsonObject = (JSONObject) packageArray.get(i);
                    packageList.add(jsonObject);
                }

//                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
//
//                // dto에 담기
//                JSONArray rateplans = (JSONArray) responseJson.get("rateplans");
//                for(int i=0; i<rateplans.size(); i++){
//                    JSONObject jsonObject = (JSONObject) rateplans.get(i);
////                    String id = jsonObject.get("id").toString();
//
//                }


            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 특정 객실의 전체 패키지 목록 가져오기 실패");
        }
        return packageList;
    }

    public JSONObject getPackageDetail(String property_id, String roomtype_id, String rateplan_id){
        JSONObject jsonObject = new JSONObject();
        try{
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(Constants.ondaPath + "properties/" + property_id + "/roomtypes/" + roomtype_id + "/rateplans/" + rateplan_id)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", Constants.ondaAuth)
                    .build();

            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();
                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();

                jsonObject = (JSONObject) jsonParser.parse(responseBody);

                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                jsonObject = (JSONObject) responseJson.get("rateplan");
//
//                // dto에 담기
//                JSONObject lengthOfStay = (JSONObject) responseJson.get("length_of_stay");
//
//                JSONObject salesTerms = (JSONObject) responseJson.get("sales_terms");
//
//                JSONObject meal = (JSONObject) responseJson.get("meal");

            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 특정 패키지의 상세 정보 가져오기 실패");
        }
        return jsonObject;
    }

    public void accommRegist(String path){
        String strConId = "";
        String strToconIdx = "";

        // 전체 숙소 id, 해당 id별 숙소 상세정보 불러오기
        List<JSONObject> accommList = getAccommListApi(path);

        List<String> propertyIdList = new ArrayList<>();
        for(JSONObject list : accommList){
            String propertyId = list.get("id").toString();
            propertyIdList.add(propertyId);
            System.out.println("propertyId : " + propertyId);
        }

        // 숙소 정보 INSERT
        try{
            for(String id : propertyIdList){
                JSONObject accommDetailJson = getAccommDetailApi(id);
                System.out.println("accommDetailJson : " + accommDetailJson);

                // dto에 담기
                CondoDto accommDetail = new CondoDto();

                String strAccommId = accommDetailJson.get("id").toString();
                accommDetail.setStrAccommId(strAccommId);
                String strAccommName = accommDetailJson.get("name").toString();
                accommDetail.setStrAcmName(strAccommName);

                String strAccommStatus = accommDetailJson.get("status").toString();
                accommDetail.setStrConDisplay(getStatusYn(strAccommStatus));

                JSONObject address = (JSONObject) accommDetailJson.get("address");
                accommDetail.setStrNation(address.get("country_code").toString());
                accommDetail.setStrLoc(address.get("region").toString());
                accommDetail.setStrCity(address.get("city").toString());
                /**
                 * db addr관련 컬럼 정리 필요
                 */
                accommDetail.setStrAddress(address.get("address1").toString());
                accommDetail.setStrConAddrNew(address.get("address2").toString());
                accommDetail.setStrZipCode(address.get("postal_code").toString());
                JSONObject location = (JSONObject) address.get("location");
                accommDetail.setDecLat(location.get("latitude").toString());
                accommDetail.setDecLng(location.get("longitude").toString());

                accommDetail.setStrConTel(accommDetailJson.get("phone").toString());
                accommDetail.setStrConFax(accommDetailJson.get("fax").toString());
                accommDetail.setStrTimeIn(accommDetailJson.get("checkin").toString());
                accommDetail.setStrTimeOut(accommDetailJson.get("checkout").toString());

                JSONObject descriptions = (JSONObject) accommDetailJson.get("descriptions");
                accommDetail.setStrSummary(descriptions.get("property").toString());
                accommDetail.setStrUsageNotice(descriptions.get("reservation").toString());

                /**
                 * classifications
                 * 현재 condo테이블의 con_flag -> int
                 * 값이 호텔, 티켓 등 여러개일 가능성도 있음
                 * -> 값을 어떻게 넣아햐는지
                 */
                JSONArray classifications = (JSONArray) accommDetailJson.get("classifications");
//                List<String> classficationList = new ArrayList<>();
//                for(int i=0; i<classifications.size(); i++){
////                    String code = accomodationMapper.selectCode(classifications.get(i).toString());
//                    classficationList.add(accomodationMapper.selectCode(classifications.get(i).toString());
//                }
//                for(classficationList){
//                    accommDetail.setStrFlag(code);
//                }

                // 어차피 다 태그니까 한 번에 몰아넣자 tags 컬럼에.
                JSONObject tags = (JSONObject) accommDetailJson.get("tags");
                JSONArray properties = (JSONArray) tags.get("properties") ;
                JSONArray facilities = (JSONArray) tags.get("facilities");
                JSONArray services = (JSONArray) tags.get("services");
                JSONArray attractions = (JSONArray) tags.get("attractions");

                List<String> tagList = new ArrayList<>();
                if(properties != null){
                    for(Object p : properties){
                        tagList.add(p.toString());
                    }
                }
                if(facilities != null){
                    for(Object f : facilities){
                        tagList.add(f.toString());
                    }
                }
                if(services != null){
                    for(Object s : services){
                        tagList.add(s.toString());
                    }
                }
                if(attractions != null){
                    for(Object a : attractions){
                        tagList.add(a.toString());
                    }
                }

                if(tagList != null){
                    String strTags = "";
                    for(int i=0; i<tagList.size(); i++){
                        if(i == (tagList.size()-1)){
                            strTags += tagList.get(i);
                        }else{
                            strTags += tagList.get(i) + ", ";
                        }
                    }
                    accommDetail.setStrTagName(strTags);
                }

                accommDetail.setStrApiFlag("ONDA");
                //------------------------------------------------------------------------------------------------------
                strConId = accomodationMapper.accommRegist(accommDetail);
                System.out.println("con_id : " + strConId);

                // 이미지------------------------------------------------------------------------------------------------
                // test_CONTENTS_PHOTO, test_CONDO_PHOTO 테이블에 INSERT
                JSONArray images = (JSONArray) accommDetailJson.get("images");

                for(int i=0; i<images.size(); i++){
                    JSONObject image = (JSONObject) images.get(i);

                    String str250Img = image.get("250px").toString();
                    String str500Img = image.get("500px").toString();
                    String str1000Img = image.get("1000px").toString();

                    if(str250Img != null){
                        accommPhotoContentsReg(str250Img, 250, strAccommId, strAccommName, strConId);
                    }

                    if(str500Img != null){
                        accommPhotoContentsReg(str500Img, 500, strAccommId, strAccommName, strConId);
                    }

                    if(str1000Img != null){
                        accommPhotoContentsReg(str1000Img, 1000, strAccommId, strAccommName, strConId);
                    }

                }

                // 환불 취소규정 ----------------------------------------------------------------------------------------
                // tbl_cancel_info_row 테이블에 INSERT
                JSONObject refunds = (JSONObject) accommDetailJson.get("property_refunds");
                CancelInfoDto cancelInfoDto = new CancelInfoDto();
                cancelInfoDto.setStrCname(strAccommName);
                cancelInfoDto.setIntCid(Integer.parseInt(strConId));

                String strCnFlag = "ps";

                for(int i=0; i<2; i++){
                    System.out.println("i : " + i);
                    System.out.println("strCnFlag : " + strCnFlag);
                    cancelInfoDto.setStrCnFlag(strCnFlag);
                    for(int j=0; j<7; j++){
                        cancelInfoDto.setIntCnDcnt(j);
                        cancelInfoDto.setIntCnPer(100 - (Integer.parseInt(refunds.get(j + "d").toString())));
                        accomodationMapper.cancelInfoReg(cancelInfoDto);
                    }
                    strCnFlag = "of";
                }

//                if(cancelInfoRegResult == 0){
//                    System.out.println("INSERT TBL_CANCEL_INFO_ROW FAIL");
//                }



                // 룸타입정보 INSERT
                List<JSONObject> roomTypeList = getRoomTypeListApi(id);

                List<String> roomTypeIdList = new ArrayList<>();
                for(JSONObject list : roomTypeList){
                    String roomTypeId = list.get("id").toString();
                    roomTypeIdList.add(roomTypeId);
                    System.out.println("roomTypeId : " + roomTypeId);
                }

                int intAdminCount = 1;
                for(String roomTypeId : roomTypeIdList){
                    JSONObject roomDetailJson = getRoomTypeDetail(id, roomTypeId);
                    System.out.println("roomTypeDetailJson : " + roomDetailJson);

                    // dto에 담기
                    ToconDto roomDetail = new ToconDto();

                    roomDetail.setRoomTypeId(roomTypeId);
                    roomDetail.setConId(strConId);

                    roomDetail.setTocode(roomDetailJson.get("name").toString());

                    JSONObject capacity = (JSONObject) roomDetailJson.get("capacity");
                    roomDetail.setStandpeopleCnt(Integer.parseInt(capacity.get("standard").toString()));
                    roomDetail.setMaxpeopleCnt(Integer.parseInt(capacity.get("max").toString()));

                    // ONDA는 별도의 내부 순서를 주지 않으므로 불러오는 순서대로 임의로 줌
                    roomDetail.setAdmin_count(intAdminCount);
                    intAdminCount +=1;

                    String strRoomTypeStatus = roomDetailJson.get("status").toString();
                    roomDetail.setStrIngYN(getStatusYn(strRoomTypeStatus));

                    roomDetail.setTocodeText(roomDetailJson.get("description").toString());

                    strToconIdx = accomodationMapper.roomTypeRegist(roomDetail);
                    System.out.println("tocon_idx : " + strToconIdx);

                }
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("숙소 정보 INSERT FAIL");
        }




    }

    public List<JSONObject> getInventories(String rateplan_id, String from, String to){
        List<JSONObject> inventoryList = new ArrayList<>();
        try{

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(Constants.ondaPath + "inventories?rateplan_id=" + rateplan_id + "&from=" + from + "&to=" + to)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", Constants.ondaAuth)
                    .build();

            Response response = client.newCall(request).execute();
            if(response.isSuccessful()) {
                // response 파싱
                String responseBody = response.body().string();
                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                JSONArray inventoryArr = (JSONArray) responseJson.get("inventories");

                for(int i=0; i<inventoryArr.size(); i++){
                    JSONObject jsonObject = (JSONObject) inventoryArr.get(i);
                    inventoryArr.add(jsonObject);
                }

            }

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 특정 패키지의 재고 및 요금 정보 가져오기 실패");
        }
        return  inventoryList;
    }

    // CONTENTS_PHOTO, CONDO_PHOTO에 INSERT
    public void accommPhotoContentsReg(String strImage, int intSize, String strAccommId, String strAccommName, String strConId){
        try{
            ContentsPhotoDto contentsPhotoDto = new ContentsPhotoDto();
            /**
             * 임시로 하드코딩
             */
            int intCreatedSID = 50; // 이미지 생성한사람 50 : 손유정(employ테이블)
            int intModifiedSID = 50; // 이미지 수정한사람

            String[] filePathArr = strImage.split("/");
            String strFileName = "";
            for(int j=0; j< filePathArr.length; j++){
                if(j == (filePathArr.length - 1)){
                    strFileName = filePathArr[j];
                    contentsPhotoDto.setStrFileName(strFileName);
                }
            }

            // 경로에 폴더 생성 -> 있으면 생성 안시킴
            Path directoryPath = Paths.get(Constants.ondaFileDir + strAccommId + "\\" + intSize + "\\");
            Files.createDirectories(directoryPath);

            // 파일 존재여부 체크
            String strFilePath = Constants.ondaFileDir + strAccommId + "\\" + intSize + "\\" + strFileName;
            File file = new File(strFilePath);
            if(!(file.exists())){
                // 이미지 저장
                UrlResourceDownloader downloader = new UrlResourceDownloader(strFilePath, strImage);
                downloader.urlFileDownload();
            }else{
                System.out.println("ALREADY EXISTS PHOTO");
            }

            contentsPhotoDto.setStrFilePath("/onda/" + strAccommId + "/" + intSize + "/");
            contentsPhotoDto.setIntCreatedSID(intCreatedSID);
            contentsPhotoDto.setIntModifiedSID(intModifiedSID);
            contentsPhotoDto.setStrSubject(strAccommName);
            contentsPhotoDto.setStrCid(strConId);

            String insertResult = accomodationMapper.accommPhotoContentsReg(contentsPhotoDto);
//                if(insertResult == 0){
//                    System.out.println("INSERT CONTENTS_PHOTO FAIL");
//                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getStatusYn(String strRoomTypeStatus){
        String status = "";
        if(strRoomTypeStatus.equals("disabled") || strRoomTypeStatus.equals("deleted")){
            status = "N";
        }else if(strRoomTypeStatus.equals("enabled")){
            status = "Y";
        }
        return status;
    }


}
