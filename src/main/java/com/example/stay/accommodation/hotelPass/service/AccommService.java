package com.example.stay.accommodation.hotelPass.service;

import com.example.stay.accommodation.hotelPass.mapper.AccommMapper;
import com.example.stay.common.util.*;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service("hotelPass.AccommService")
public class AccommService {
    @Autowired
    private AccommMapper accommMapper;

    CommonFunction commonFunction = new CommonFunction();

    public String getHotelList() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://xml.hotelpass.com/download/HTPWS_HotelInfo/HTPWS_HotelInformation.xml";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            XmlUtility xmlUtility = new XmlUtility();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(response.body().string())));
            NodeList hotelList = doc.getElementsByTagName("Hotel");
            JSONObject cityCode = new JSONObject();
            JSONObject nationCode = new JSONObject();
            JSONObject gradeMap = new JSONObject();
            List<Map<String, Object>> hotelListMap = new ArrayList<>();
            for (int i = 0; i < hotelList.getLength(); i++) {
                String strNationCode = hotelList.item(i).getChildNodes().item(0).getTextContent();
                if (strNationCode.equals("KR")) {
                    Map<String, Object> hotelMap = new HashMap<>();
                    String address = "";
                    String hotelCode = hotelList.item(i).getAttributes().getNamedItem("HotelCode").getTextContent();
                    String latitude = hotelList.item(i).getChildNodes().item(7).getTextContent();
                    String longitude = hotelList.item(i).getChildNodes().item(8).getTextContent();
                    address = commonFunction.getJusoByGeoCd(latitude, longitude);
                    if(address==null){//좌표조회로 주소조회가 안될시 기존 영문주소 입력
                        address = hotelList.item(i).getChildNodes().item(9).getTextContent();
                    }
                    hotelMap.put("hotelCode", hotelCode);
                    hotelMap.put("nationCode", hotelList.item(i).getChildNodes().item(0).getTextContent());
                    hotelMap.put("cityCode", hotelList.item(i).getChildNodes().item(2).getTextContent());
                    hotelMap.put("cityName", hotelList.item(i).getChildNodes().item(3).getTextContent());
                    hotelMap.put("hotelName", hotelList.item(i).getChildNodes().item(6).getTextContent());
                    hotelMap.put("latitude", hotelList.item(i).getChildNodes().item(7).getTextContent());
                    hotelMap.put("longitude", hotelList.item(i).getChildNodes().item(8).getTextContent());
                    hotelMap.put("address", address);
                    if(address.equals(" ")){
                        System.out.println(address + ":::" + hotelList.item(i).getChildNodes().item(6).getTextContent());
                    }
                    hotelMap.put("tel", hotelList.item(i).getChildNodes().item(11).getTextContent());
                    hotelMap.put("fax", hotelList.item(i).getChildNodes().item(12).getTextContent());
                    hotelMap.put("grade", hotelList.item(i).getChildNodes().item(13).getTextContent());
                    hotelMap.put("roomCnt", hotelList.item(i).getChildNodes().item(14).getTextContent());
                    NodeList imgList = hotelList.item(i).getChildNodes().item(15).getChildNodes();
                    String strImgList="";
                    for (int j = 0 ; j<imgList.getLength() ; j++){
                        strImgList += accommPhotoContentsReg((String) imgList.item(j).getTextContent(), hotelMap.get("hotelCode").toString(), "");
                        strImgList += "{{^}}";
                    }
                    strImgList.substring(0, strImgList.length()-5);
                    hotelMap.put("imgDatas", strImgList);
                    hotelMap.put("zipNo", hotelList.item(i).getChildNodes().item(10).getTextContent());
                    hotelListMap.add(hotelMap);
                    if (cityCode.containsKey(hotelList.item(i).getChildNodes().item(2).getTextContent())) {
                        continue;
                    }
                    if (gradeMap.containsKey(hotelList.item(i).getChildNodes().item(13).getTextContent())) {
                        continue;
                    }
                    gradeMap.put(hotelList.item(i).getChildNodes().item(13).getTextContent(), "1");
                    cityCode.put(hotelList.item(i).getChildNodes().item(2).getTextContent(), hotelList.item(i).getChildNodes().item(3).getTextContent());
                }
                if (nationCode.containsKey(hotelList.item(i).getChildNodes().item(0).getTextContent())) {
                    continue;
                }
                nationCode.put(hotelList.item(i).getChildNodes().item(0).getTextContent(), hotelList.item(i).getChildNodes().item(1).getTextContent());
//                System.out.print(hotelList.item(i).getChildNodes().item(0).getTextContent() + " ::: ");
//                System.out.print(hotelList.item(i).getChildNodes().item(1).getTextContent() + " ::: ");

            }
//            System.out.println(hotelListMap);
            Iterator<Map.Entry<String, String>> entry =
                    cityCode.entrySet().iterator();

            Set<Map.Entry<String, String>> entrySet = cityCode.entrySet();

            //도시코드 인입
            for (Map.Entry<String, String> element : entrySet) {
//                System.out.println("KEY: " +  element.getKey() +
//                        " / VALUE: " + element.getValue());
//                String insertResult = accommMapper.insertCityList(element.getKey(), element.getValue()); //도시코드 인입
//                return commonFunction.makeReturn("jsonp", "200", "OK", insertResult);
            }
            //시설정보 인입
            //grade 관련
            String hotelData="";
            for (Map<String, Object> map : hotelListMap) {
                String grade = map.get("grade").toString();
                int intGrade;
                if(grade.lastIndexOf("★") != grade.length()) {
                    intGrade = (int) (grade.length()-0.5);
                } else {
                    intGrade = grade.length();
                }
                String address = map.get("address").toString();
                String cityName = map.get("cityName").toString();
                String hotelCode = map.get("hotelCode").toString();
                String hotelName = map.get("hotelName").toString();
                String zipNo = map.get("zipNo").toString();
                String tel = map.get("tel").toString();
                String fax = map.get("fax").toString();
                String roomCnt = map.get("roomCnt").toString();
                String latitude = map.get("latitude").toString();
                String longitude = map.get("longitude").toString();
                String strDistrictCode1 = "";
                String strDistrictCode2 = "";
                String imgdatas = map.get("imgDatas").toString();
                if(address == " "){

                } else {
                    String[] addressDetail = address.split(" ");
                    strDistrictCode1 = commonFunction.addressToDistrictCode(addressDetail[0]);
                    strDistrictCode2 = accommMapper.getDistrictCode(addressDetail[1], strDistrictCode1);
                }
                hotelData += hotelCode + "|^|" + hotelName + "|^|" + strDistrictCode1 + "|^|" + strDistrictCode2 + "|^|"
                        + latitude + "|^|" + longitude + "|^|" + address + "|^|" + tel + "|^|" + fax + "|^|" + zipNo + "|^|" + String.valueOf(intGrade) + "|^|" + roomCnt + "|^|" + imgdatas + "{{|}}";


            }
            hotelData = hotelData.substring(0, hotelData.length()-5);
//            String result = accommMapper.insertHotel(hotelData);
            return commonFunction.makeReturn("jsonp", "200", "OK", "result");

        } catch (Exception e) {
            e.printStackTrace();
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }

    }


    public String getFacilityList() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://xml.hotelpass.com/download/HTPWS_HotelInfo/HTPWS_HotelFacilityInfo.xml";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        List<Map<String, Object>> hotelListMap = null;
        try {
            Response response = client.newCall(request).execute();
            XmlUtility xmlUtility = new XmlUtility();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(response.body().string())));
            NodeList hotelList = doc.getElementsByTagName("Hotel");
            hotelListMap = new ArrayList<>();
            JSONObject hotel = new JSONObject();
            JSONObject facility = new JSONObject();
            JSONObject facilitymap = new JSONObject();

            for (int i = 0; i < hotelList.getLength(); i++) {
                System.out.println(hotelList.item(i).getAttributes().item(0).getNodeValue());
                NodeList FacilityList = hotelList.item(i).getChildNodes();


                for (int j = 0; j < FacilityList.getLength(); j++) {
                    facility.put(FacilityList.item(j).getAttributes().item(0).getNodeValue(), FacilityList.item(j).getTextContent());
                    facilitymap.put(FacilityList.item(j).getAttributes().item(0).getNodeValue(), FacilityList.item(j).getTextContent());
                    System.out.print(FacilityList.item(j).getAttributes().item(0).getNodeValue());
                    System.out.println(FacilityList.item(j).getTextContent());
                }
                hotel.put(hotelList.item(i).getAttributes().item(0).getNodeValue(), facility);
                hotelListMap.add(hotel);
                hotel = new JSONObject();
                facility = new JSONObject();


//                Facility.put(hotelList.item(i).getAttributes().item(0).getNodeValue(), hotelList.item(i).getTextContent());
            }
            System.out.println(hotelListMap);
            System.out.println(facilitymap);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CommonFunction().makeReturn("jsonp", "", "", hotelListMap);
    }

    // CONTENTS_PHOTO, CONDO_PHOTO에 INSERT
    public String accommPhotoContentsReg(String strImage, String strPropertyID, String strRmtypeID){
        String strAccommPhotoContent = "";
        try{
            /**
             * 임시로 하드코딩
             */
            int intCreatedSID = 158; // 이미지 생성한사람 158 : 이운범(STAFFS테이블)
            int intModifiedSID = 158; // 이미지 수정한사람

            String[] filePathArr = strImage.split("/");
            String strFileName = "";
            for(int j=0; j< filePathArr.length; j++){
                if(j == (filePathArr.length - 1)){
                    strFileName = filePathArr[j];
                }
            }


            Path directoryPath = null;
            String filePath = "";
            String strFilePath = "";
            // 시설 이미지일 경우
            // 경로에 폴더 생성 -> 있으면 생성 안시킴
            if(strRmtypeID.equals("")){
                directoryPath = Paths.get("D:\\dev\\4.photo\\condo_images\\hotelPass\\" + strPropertyID + "\\");
                filePath = "D:\\dev\\4.photo\\condo_images\\hotelPass\\" + strPropertyID + "\\" + strFileName;
                strFilePath = "/hotelPass/" + strPropertyID + "/";
            }else{ // 객실 이미지일 경우
                directoryPath = Paths.get("D:\\dev\\4.photo\\condo_images\\hotelPass\\" + strPropertyID + "\\" + strRmtypeID + "\\");
                filePath = "D:\\dev\\4.photo\\condo_images\\hotelPass\\" + strPropertyID + "\\" + strRmtypeID + "\\" + strFileName;
                strFilePath = "/hotelPass/" + strPropertyID + "/" + strRmtypeID + "/";
            }

            Files.createDirectories(directoryPath);

            // 파일 존재여부 체크
            File file = new File(filePath);
            if(!(file.exists())){
                // 이미지 저장
                UrlResourceDownloader downloader = new UrlResourceDownloader(filePath, strImage);
                downloader.urlFileDownload();
            }else{
                System.out.println("ALREADY EXISTS PHOTO");
            }

            strAccommPhotoContent = strFilePath + "|~|" + strFileName + "|~|" + intCreatedSID + "|~|"
                    + intModifiedSID;

        }catch (Exception e){
            e.printStackTrace();
        }
        return strAccommPhotoContent;
    }

}
