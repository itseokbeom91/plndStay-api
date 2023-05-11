package com.example.stay.accommodation.onda.service;

import com.example.stay.accommodation.onda.mapper.AccomodationMapper;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.UrlResourceDownloader;
import com.example.stay.openMarket.common.dto.CondoDto;
import com.example.stay.openMarket.common.dto.ContentsPhotoDto;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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





                // 바뀐 데이터가 있다면 해당 데이터, updated_at update작업
//                for(JSONObject list : accommList){
//                    int updateCnt = 0;
//
//                    String strOndaId = list.get("id").toString();
//                    String strOndaName = list.get("name").toString();
//                    String strOndaStatus = list.get("status").toString();
//
//                    if(strOndaStatus.equals("disabled") || strOndaStatus.equals("deleted")){
//                        strOndaStatus = "N";
//                    }else if(strOndaStatus.equals("enabled")){
//                        strOndaStatus = "Y";
//                    }

                    // id로 현재 DB숙소 데이터 가져오기
//                    CondoDto condoDto = accomodationMapper.selectCondoByConId(strOndaId);
//
//                    // insert
//                    CondoDto ondaCondoDto = new CondoDto();
//                    if(condoDto == null){
//                        ondaCondoDto.setStrAcmName(strOndaName);
//                        ondaCondoDto.setStrConDisplay(strOndaStatus);
//                    }else{ // 현재 DB데이터와 비교
////                        CondoDto updatedCondo = new CondoDto();
//                        if(condoDto.getStrConDisplay().equals("Y")){
//                            if(strOndaStatus.equals("N")){
//                                ondaCondoDto.setStrConDisplay("N");
//                                updateCnt += 1;
//                            }
//                        }
//
//                        if(!condoDto.getStrAcmName().equals(strOndaName)){
//                            ondaCondoDto.setStrAcmName(strOndaName);
//                            updateCnt += 1;
//                        }
//
//                        // update된 항목이 있으면 DB 업데이트
//                        if(updateCnt > 0){
////                        DB업데이트.
//                        }
//                    }
//                }
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
                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                jsonObject = (JSONObject) jsonParser.parse(responseBody);

            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 특정 숙소 상세정보 가져오기 실패");
        }
        return jsonObject;
    }

    public void getRoomTypeListApi(String property_id){
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

                List<JSONObject> roomTypeList = new ArrayList<>();
                for(int i=0; i<roomtypeArray.size(); i++){
                    JSONObject jsonObject = (JSONObject) roomtypeArray.get(i);
                    roomTypeList.add(jsonObject);
                    System.out.println(roomtypeArray);
                }

                // dto에 담기
                for(JSONObject list : roomTypeList){
//                    list.get("id").toString();
                    System.out.println(list.get("id").toString());
                }



            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 특정 숙소 전체 객실 목록 가져오기 실패");
        }
    }

    public void getRoomTypeDetail(String property_id, String roomtype_id){
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

                JSONObject jsonRoomType = (JSONObject) responseJson.get("roomtype");

                // dto에 담기
                JSONObject capacity = (JSONObject) jsonRoomType.get("capacity");

                JSONObject tags = (JSONObject)jsonRoomType.get("tags");
                JSONArray roomtypes = (JSONArray) tags.get("roomtypes");
                JSONArray views = (JSONArray) tags.get("views");
                JSONArray amenities = (JSONArray) tags.get("amenities");

                JSONObject details = (JSONObject) jsonRoomType.get("details");
//                String room = details.get("room").toString(); // String으로 받든 int로 받든...

                JSONObject bedtype = (JSONObject) jsonRoomType.get("bedtype");
//                String room = details.get("single_beds").toString();

                JSONArray images = (JSONArray) jsonRoomType.get("images");
                for(int i=0; i<images.size(); i++){
                    JSONObject image = (JSONObject) images.get(i);
//                    String original = image.get("original").toString();
                }



            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 특정 객실 상세정보 가져오기 실패");
        }
    }

    public void getPackageList(String property_id, String roomtype_id){
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

                // dto에 담기
                JSONArray rateplans = (JSONArray) responseJson.get("rateplans");
                for(int i=0; i<rateplans.size(); i++){
                    JSONObject jsonObject = (JSONObject) rateplans.get(i);
//                    String id = jsonObject.get("id").toString();

                }


            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 특정 객실의 전체 패키지 목록 가져오기 실패");
        }
    }

    public void getPackageDetail(String property_id, String roomtype_id, String rateplan_id){
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
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                JSONObject rateplan = (JSONObject) responseJson.get("rateplan");

                // dto에 담기
                JSONObject lengthOfStay = (JSONObject) responseJson.get("length_of_stay");

                JSONObject salesTerms = (JSONObject) responseJson.get("sales_terms");

                JSONObject meal = (JSONObject) responseJson.get("meal");

            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 정 패키지의 상세 정보 가져오기 실패");
        }
    }

    public void getAccommNInsert(String path){
        try{
            // 전체 숙소 id, 해당 id별 숙소 상세정보 불러오기
            List<JSONObject> accommList = getAccommListApi(path);

            List<String> ondaIdList = new ArrayList<>();
            for(JSONObject list : accommList){
                String strOndaId = list.get("id").toString();
                ondaIdList.add(strOndaId);
            }

            for(String id : ondaIdList){

                JSONObject accommDetailJson = getAccommDetailApi(id);
                System.out.println("accommDetailJson : " + accommDetailJson);

                JSONObject jsonProperty = (JSONObject) accommDetailJson.get("property");

                // dto에 담기
                CondoDto accommDetail = new CondoDto();

                String strAccommId = jsonProperty.get("id").toString();
                accommDetail.setStrAccommId(strAccommId);
                String strAccommName = jsonProperty.get("name").toString();
                accommDetail.setStrAcmName(strAccommName);

                String strOndaStatus = jsonProperty.get("status").toString();

                if(strOndaStatus.equals("disabled") || strOndaStatus.equals("deleted")){
                    strOndaStatus = "N";
                }else if(strOndaStatus.equals("enabled")){
                    strOndaStatus = "Y";
                }
                accommDetail.setStrConDisplay(strOndaStatus);

                JSONObject address = (JSONObject) jsonProperty.get("address");
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



                accommDetail.setStrConTel(jsonProperty.get("phone").toString());
                accommDetail.setStrConFax(jsonProperty.get("fax").toString());
                accommDetail.setStrTimeIn(jsonProperty.get("checkin").toString());
                accommDetail.setStrTimeOut(jsonProperty.get("checkout").toString());

                JSONObject descriptions = (JSONObject) jsonProperty.get("descriptions");
                accommDetail.setStrSummary(descriptions.get("property").toString());
                accommDetail.setStrUsageNotice(descriptions.get("reservation").toString());

                /**
                 * classifications, tags, facilities, services, attractions
                 *  값이 여러개면 어떻게 할건지 정리 필요
                 */
//                JSONArray classifications = (JSONArray) jsonProperty.get("classifications");
//                for(int i=0; i<classifications.size(); i++){
                // CODE_SYSTEM 테이블에서 strName과 일치하는 strCode 가져오기

//                    String code = accomodationMapper.selectCode(classifications.get(i).toString());
//                    accommDetail.setStrFlag(code);
//                }

//                JSONObject tags = (JSONObject) jsonProperty.get("tags");
//                JSONArray properties = (JSONArray) tags.get("properties");
//                JSONArray facilities = (JSONArray) tags.get("facilities");
//                JSONArray services = (JSONArray) tags.get("services");
//                JSONArray attractions = (JSONArray) tags.get("attractions");



                //------------------------------------------------------------------------------------------------------
//                int accommInsertResult = accomodationMapper.getAccommNInsert(accommDetail);
//                System.out.println("accommInsertResult : " + accommInsertResult);
//                if(accommInsertResult == 200){
//                    System.out.println("숙소 정보 INSERT 성공");
//                }else{
//                    System.out.println("숙소 정보 INSERT 성공");
//                }




                // 이미지------------------------------------------------------------------------------------------------

                // test_CONTENTS_PHOTO, test_CONDO_PHOTO 테이블에 INSERT
                JSONArray images = (JSONArray) jsonProperty.get("images");

                for(int i=0; i<images.size(); i++){
                    JSONObject image = (JSONObject) images.get(i);

                    String str250Img = image.get("250px").toString();
                    String str500Img = image.get("500px").toString();
                    String str1000Img = image.get("1000px").toString();

                    ContentsPhotoDto contentsPhotoDto = new ContentsPhotoDto();
                    String strFilePath = "";
                    /**
                     * 임시로 하드코딩
                     */
                    int intCreatedSID = 50; // 이미지 생성한사람 50 : 손유정(employ테이블)
                    int intModifiedSID = 50; // 이미지 수정한사람

                    if(str250Img != null){
                        String[] filePathArr = str250Img.split("/");
                        String strFileName = "";
                        for(int j=0; j< filePathArr.length; j++){
                            if(j == (filePathArr.length - 1)){
                                strFileName = filePathArr[j];
                                System.out.println("strFileName : " + strFileName);

                                contentsPhotoDto.setStrFileName(strFileName);

//                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                Date date = new Date();
                            }
                        }

                        // 경로에 폴더 생성 -> 있으면 생성 안시킴
                        Path directoryPath = Paths.get(Constants.ondaFileDir + strAccommId + "\\250\\");
                        Files.createDirectories(directoryPath);

                        // 파일 존재여부 체크
                        strFilePath = Constants.ondaFileDir + strAccommId + "\\250\\" + strFileName;
                        File file = new File(strFilePath);
                        if(!file.exists()){
                            // 이미지 저장
                            UrlResourceDownloader downloader = new UrlResourceDownloader(strFilePath, str250Img);
                            downloader.urlFileDownload();
                        }

                        contentsPhotoDto.setStrFilePath("/onda/" + strAccommId + "/250/");
                        contentsPhotoDto.setIntCreatedSID(intCreatedSID);
                        contentsPhotoDto.setIntModifiedSID(intModifiedSID);
                        contentsPhotoDto.setStrSubject(strAccommName);

                        int insertResult = accomodationMapper.insertAccommPhotoContents(contentsPhotoDto);
                        if(insertResult == 0){
                            System.out.println("INSERT CONTENTS_PHOTO FAIL");
                        }
                    }

                    if(str500Img != null){
                        String[] filePathArr = str500Img.split("/");
                        String strFileName = "";
                        for(int j=0; j< filePathArr.length; j++){
                            if(j == (filePathArr.length - 1)){
                                strFileName = filePathArr[j];
                                System.out.println("strFileName : " + strFileName);

                                contentsPhotoDto.setStrFileName(strFileName);

//                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                Date date = new Date();
                            }
                        }

                        // 경로에 폴더 생성 -> 있으면 생성 안시킴
                        Path directoryPath = Paths.get(Constants.ondaFileDir + strAccommId + "\\500\\");
                        Files.createDirectories(directoryPath);

                        // 파일 존재여부 체크
                        strFilePath = Constants.ondaFileDir + strAccommId + "\\500\\" + strFileName;
                        File file = new File(strFilePath);
                        if(!file.exists()){
                            // 이미지 저장
                            UrlResourceDownloader downloader = new UrlResourceDownloader(strFilePath, str250Img);
                            downloader.urlFileDownload();
                        }

                        contentsPhotoDto.setStrFilePath("/onda/" + strAccommId + "/500/");
                        contentsPhotoDto.setIntCreatedSID(intCreatedSID);
                        contentsPhotoDto.setIntModifiedSID(intModifiedSID);
                        contentsPhotoDto.setStrSubject(strAccommName);

                        int insertResult = accomodationMapper.insertAccommPhotoContents(contentsPhotoDto);
                        if(insertResult == 0){
                            System.out.println("INSERT CONTENTS_PHOTO FAIL");
                        }
                    }

                    if(str1000Img != null){
                        String[] filePathArr = str1000Img.split("/");
                        String strFileName = "";
                        for(int j=0; j< filePathArr.length; j++){
                            if(j == (filePathArr.length - 1)){
                                strFileName = filePathArr[j];
                                System.out.println("strFileName : " + strFileName);

                                contentsPhotoDto.setStrFileName(strFileName);

                            }
                        }

                        // 경로에 폴더 생성 -> 있으면 생성 안시킴
                        Path directoryPath = Paths.get(Constants.ondaFileDir + strAccommId + "\\1000\\");
                        Files.createDirectories(directoryPath);

                        // 파일 존재여부 체크
                        strFilePath = Constants.ondaFileDir + strAccommId + "\\500\\" + strFileName;
                        File file = new File(strFilePath);
                        if(!file.exists()){
                            // 이미지 저장
                            UrlResourceDownloader downloader = new UrlResourceDownloader(strFilePath, str250Img);
                            downloader.urlFileDownload();
                        }

                        contentsPhotoDto.setStrFilePath("/onda/" + strAccommId + "/1000/");
                        contentsPhotoDto.setIntCreatedSID(intCreatedSID);
                        contentsPhotoDto.setIntModifiedSID(intModifiedSID);
                        contentsPhotoDto.setStrSubject(strAccommName);

                        int insertResult = accomodationMapper.insertAccommPhotoContents(contentsPhotoDto);
                        if(insertResult == 0){
                            System.out.println("INSERT CONTENTS_PHOTO FAIL");
                        }
                    }






                }



                // 환불 취소규정 ----------------------------------------------------------------------------------------
                JSONObject refunds = (JSONObject) jsonProperty.get("property_refunds");
                // tbl_cancel_info_row 테이블에 INSERT


























            }



        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
