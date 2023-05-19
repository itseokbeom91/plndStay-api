package com.example.stay.accommodation.hotelStory.service;

import com.example.stay.accommodation.hotelStory.mapper.HotelStoryMapper;
import com.example.stay.accommodation.onda.mapper.AccomodationMapper;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.UrlResourceDownloader;
import com.example.stay.common.util.XmlUtility;
import com.example.stay.openMarket.common.dto.CancelInfoDto;
import com.example.stay.openMarket.common.dto.ContentsPhotoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class APIHotelstoryService {

    @Autowired
    private XmlUtility xmlUtility;

    @Autowired
    private HotelStoryMapper hotelStoryMapper;

    @Autowired
    private AccomodationMapper accomodationMapper;


    /**
     * API 데이터 파싱
     * @param tagElement
     * @param tagList
     * @param roomTypeMap
     * @param ratePlanMap
     * @return String result
     * @throws Exception
     */
    public String hotelStoryParsing(Element tagElement, String tagList, Map<String, Map> roomTypeMap, MultiValueMap<String, Map> ratePlanMap){
        String result = "";

        try {

            // 시설(condo) 변수
            String strPropertyId = "";
            String strPropertyName = "";
            String strAddress = "";
            String strLatitude = "";
            String strLongitude = "";
            String strLocation = "";
            String strHomePageUrl = "";
            String strPhone = "";
            String strNumRooms = "";
            String strCheckInTime = "";
            String strCheckOutTime = "";
            String strCity = "";
            String strPropertyDescription = "";
            String strTrafficInformation = "";
            String strRoomInformation = "";
            int intAID = 0;


            // 시설정보
//            if(tagList.equals("property")){

                result += "PropertyId = " + xmlUtility.getTagValue("PropertyId", tagElement) + "<br>";
                result += "PropertyName = " + xmlUtility.getTagValue("PropertyName", tagElement) + "<br>";
                result += "Address = " + xmlUtility.getTagValue("Address", tagElement) + "<br>";
                result += "Latitude = " + xmlUtility.getTagValue("Latitude", tagElement) + "<br>";
                result += "Longitude = " + xmlUtility.getTagValue("Longitude", tagElement) + "<br>";
                String cityCode = xmlUtility.getTagValue("CityCode", tagElement);
                Document cityDocument = xmlUtility.HotelStoryAPIList("CityList","");
                result += "Location = " + getCityNameByCode(cityDocument, cityCode)[1] + "<br>";
                result += "city = " + getCityNameByCode(cityDocument, cityCode)[0] + "<br>";
                result += "HomePageUrl = " + xmlUtility.getTagValue("HomePageUrl", tagElement) + "<br>";
                result += "Phone = " + xmlUtility.getTagValue("Phone", tagElement) + "<br>";
                result += "NumRooms = " + xmlUtility.getTagValue("NumRooms", tagElement) + "<br>";
                result += "CheckInTime = " + xmlUtility.getTagValue("CheckInTime", tagElement) + "<br>";
                result += "CheckOutTime = " + xmlUtility.getTagValue("CheckOutTime", tagElement) + "<br>";
                result += "PropertyDescription = " + xmlUtility.getTagValue("PropertyDescription", tagElement) + "<br>";
                result += "TrafficInformation = " + xmlUtility.getTagValue("TrafficInformation", tagElement) + "<br>";
                result += "RoomInformation = " + xmlUtility.getTagValue("RoomInformation", tagElement) + "<br>";


                // condo쪽 데이터 저장
                strPropertyId = xmlUtility.getTagValue("PropertyId", tagElement);
                strPropertyName = xmlUtility.getTagValue("PropertyName", tagElement);
                strAddress = xmlUtility.getTagValue("Address", tagElement);
                strLatitude = xmlUtility.getTagValue("Latitude", tagElement);
                strLongitude = xmlUtility.getTagValue("Longitude", tagElement);
                strLocation = getCityNameByCode(cityDocument, cityCode)[1];
                strHomePageUrl = xmlUtility.getTagValue("HomePageUrl", tagElement);
                strPhone = xmlUtility.getTagValue("Phone", tagElement);
                strNumRooms = xmlUtility.getTagValue("NumRooms", tagElement);
                strCheckInTime = xmlUtility.getTagValue("CheckInTime", tagElement);
                strCheckOutTime = xmlUtility.getTagValue("CheckOutTime", tagElement);
                strCity = getCityNameByCode(cityDocument, cityCode)[0];
                strPropertyDescription = xmlUtility.getTagValue("PropertyDescription", tagElement).toString();
                strTrafficInformation = xmlUtility.getTagValue("TrafficInformation", tagElement).toString();
                strRoomInformation = xmlUtility.getTagValue("RoomInformation", tagElement).toString();

                // condo 프로시저 실행
//                intAID = Integer.parseInt(hotelStoryMapper.insertAccomm(strPropertyId, strPropertyName, strAddress, strPhone, strNumRooms, strLocation, strHomePageUrl, strCheckInTime, strCheckOutTime
//                                                            , strLongitude, strLatitude, strCity, strPropertyDescription, strTrafficInformation, strRoomInformation));

                //System.out.println("con_id = " + intAID);
//            }

            /**
             * img 데이터 insert
             */
            String imgData = "";
            NodeList imgList = tagElement.getElementsByTagName("Image");
            for(int i=0; i<imgList.getLength(); i++){
                Node node = imgList.item(i);

                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    result += xmlUtility.getTagValue("ImageUrl",element)+"<br>";

                    String strImage = xmlUtility.getTagValue("ImageUrl",element).toString();
                    //accommPhotoContentsReg(strImage, strPropertyId, xmlUtility.getTagValue("PropertyName", tagElement).toString(), String.valueOf(intAID));
                    imgData += photoTest(strImage, strPropertyId);


                }
            }
            if(imgData.length() > 1){
                imgData = imgData.substring(0, imgData.length()-5);
            }

            /**
             * 취소규정 데이터 insert
             */
            CancelInfoDto cancelInfoDto = new CancelInfoDto();
            cancelInfoDto.setStrCname(strPropertyName);
            cancelInfoDto.setIntCid(intAID);

            String cancelData = "";
            NodeList cancelList = tagElement.getElementsByTagName("CancelPenalty");
            for(int i=0; i<cancelList.getLength(); i++){
                Node node = cancelList.item(i);

                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String strCnFlag = "ps";
                    if(element.getAttribute("Type").equals("1")){ // 비성수기
                        cancelInfoDto.setStrCnFlag("of");
                        strCnFlag = "of";
                    }else{
                        cancelInfoDto.setStrCnFlag("ps");
                    }

                    NodeList infoList = element.getElementsByTagName("Deadline");
                    for(int j=0; j<infoList.getLength(); j++){
                        Node infoNode = infoList.item(j);

                        if(infoNode.getNodeType() == Node.ELEMENT_NODE){
                            Element infoElement = (Element) infoNode;

                            cancelInfoDto.setIntCnDcnt(Integer.parseInt(infoElement.getAttribute("Date")));
                            cancelInfoDto.setIntCnPer(Integer.parseInt(infoElement.getAttribute("value")));
                            //accomodationMapper.cancelInfoReg(cancelInfoDto);
                            cancelData += strCnFlag +"|^|"+ infoElement.getAttribute("Date") +"|^|"+ infoElement.getAttribute("value") + "{{|}}";
                        }
                    }

                }
            }
            if(cancelData.length() > 1){
                cancelData = cancelData.substring(0, cancelData.length()-5);
            }


            /**
             * description(roomType, ratePlan) 데이터 insert
             */

            String roomTypeData = "";
            int intStep = 0; // 노출 순서
            NodeList descList = tagElement.getElementsByTagName("Description");
            for(int i=0; i<descList.getLength(); i++){
                Node node = descList.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;

                    result += "<br><br>|<br>";

                    // 룸타입(tocon) 변수
                    //strRoomInformation = xmlUtility.getTagValue("RoomInformation", tagElement);
                    String strRoomTypeId = xmlUtility.getTagValue("RoomTypeId",element);
                    String strText = "";
                    String strIngYn = "N";
                    String strRoomTypeName = "";
                    String strBedTypeCode = "";
                    String strRatePlanId = "";
                    String strRatePlanName = "";
                    String strMealCode = "";
                    int intSaleRate = 0;
                    int intMinPersons = 0;
                    int intMaxPersons = 0;


                    // roomTypeList 값 넣기
                    if(strRoomTypeId != null){

                        strText = xmlUtility.getTagValue("Text", element);

                        result += "RoomTypeId = " + strRoomTypeId + "<br>";
                        result += "Text = " + strText + "<br>";


                        // 룸타입 있으면(가용하는 룸타입일경우 )
                        if(roomTypeMap.containsKey(strRoomTypeId) == true){
                            intStep ++;
                            result += "intStep = " + intStep +"<br>";
                            result += "strIngYn = Y<br>";
                            result += "RoomTypeName = "+roomTypeMap.get(strRoomTypeId).get("RoomTypeName")+"<br>";
                            result += "BedTypeCode = "+roomTypeMap.get(strRoomTypeId).get("BedTypeCode")+"<br>";
                            result += "MinPersons = "+roomTypeMap.get(strRoomTypeId).get("MinPersons")+"<br>";
                            result += "MaxPersons = "+roomTypeMap.get(strRoomTypeId).get("MaxPersons")+"<br>";

                            strIngYn = "Y";
                            strRoomTypeName = roomTypeMap.get(strRoomTypeId).get("RoomTypeName").toString();
                            intMinPersons = Integer.parseInt(roomTypeMap.get(strRoomTypeId).get("MinPersons").toString());
                            intMaxPersons = Integer.parseInt(roomTypeMap.get(strRoomTypeId).get("MaxPersons").toString());

                            int step = (strIngYn.equals("N"))? 150 : intStep;

                            // tocon 프로시저 실행
//                            int toconIdx = Integer.parseInt(hotelStoryMapper.insertRoomType(strRoomTypeName, intAID, intMinPersons, intMaxPersons, strRoomTypeId, step, strIngYn, strText, strRoomInformation));

                            //System.out.println("toconIdx = "+toconIdx);
                            String ratePlanData = "";
                            if(ratePlanMap.get(strRoomTypeId) != null){

                                for(Map map : ratePlanMap.get(strRoomTypeId)){
                                    result += "RatePlanId = "+map.get("RatePlanId")+"<br>";
                                    result += "RatePlanName = "+map.get("RatePlanName")+"<br>";
                                    result += "MealCode = "+map.get("MealCode")+"<br>";
                                    result += "BedTypeCode = "+map.get("BedTypeCode")+"<br>";
                                    result += "SaleRate = "+map.get("SaleRate")+"<br>";
                                    result += "MinPersons = "+map.get("MinPersons")+"<br>";
                                    result += "MaxPersons = "+map.get("MaxPersons")+"<br>";

                                    strRatePlanId = map.get("RatePlanId").toString();
                                    strRatePlanName = map.get("RatePlanName").toString();
                                    strMealCode = map.get("MealCode").toString();
                                    strBedTypeCode = map.get("BedTypeCode").toString();
                                    intSaleRate = Integer.parseInt(map.get("SaleRate").toString());
                                    intMinPersons = Integer.parseInt(map.get("MinPersons").toString());
                                    intMaxPersons = Integer.parseInt(map.get("MaxPersons").toString());

                                    // 배드타입
                                    String strBedTypeString = "";
                                    switch (strBedTypeCode){
                                        case "1": strBedTypeString = "싱글";
                                            break;
                                        case "2": strBedTypeString = "더블";
                                            break;
                                        case "3": strBedTypeString = "트윈";
                                            break;
                                        case "4": strBedTypeString = "트리플";
                                            break;
                                        case "5": strBedTypeString = "온돌";
                                            break;
                                        case "6": strBedTypeString = "퓨전(온돌더블)";
                                            break;
                                        case "8": strBedTypeString = "스위트";
                                            break;
                                        case "9": strBedTypeString = "4베드";
                                            break;
                                        case "10": strBedTypeString = "패밀리트윈";
                                            break;
                                        case "11": strBedTypeString = "현지배정";
                                            break;
                                        case "13": strBedTypeString = "세미더블";
                                            break;
                                        case "15": strBedTypeString = "더블 더블";
                                            break;
                                        default: strBedTypeString = strBedTypeCode;
                                    }

                                    // rate_plan 프로시저 실행
                                    //String ratePlanIdx = hotelStoryMapper.insertRatePlan(intAID, strPropertyId, toconIdx, strRoomTypeId, strRatePlanId, strRatePlanName, strBedTypeString, strMealCode, intMinPersons, intMaxPersons, intSaleRate);
                                    ratePlanData += strRatePlanId +"|~|"+ strRatePlanName +"|~|"+ strBedTypeString +"|~|"+ strMealCode +"|~|"+ intMinPersons +"|~|"+ intMaxPersons +"{{~}}";
                                    //System.out.println(ratePlanIdx);
                                }

                            }
                            if(ratePlanData.length() > 1){
                                ratePlanData = ratePlanData.substring(0, ratePlanData.length()-5);
                            }



                            roomTypeData += strRoomTypeName +"|^|"+ intMinPersons +"|^|"+ intMaxPersons +"|^|"+ strRoomTypeId +"|^|"+ step +"|^|"+ strIngYn +"|^|"+ strText +"|^|"+ strText +"|^|"+ ratePlanData +"{{|}}"; // strRoomInformation
                            System.out.println(strRoomInformation);
                        }else{
                            result += "strIngYn = N";
                        }

                    }
                }

            }
            if(roomTypeData.length() > 1){
                roomTypeData = roomTypeData.substring(0, roomTypeData.length()-5);
            }

            // 프로시저 돌려
//            System.out.println(imgData);
//            System.out.println(cancelData);
//            System.out.println(roomTypeData);

            result += hotelStoryMapper.insertAccommtotal(strPropertyId, strPropertyName, strAddress, strPhone, strNumRooms, strLocation, strHomePageUrl, strCheckInTime, strCheckOutTime
                    , strLongitude, strLatitude, strCity, strPropertyDescription, strTrafficInformation, strRoomInformation, imgData, cancelData, roomTypeData);


/*
            NodeList nodeList = tagElement.getElementsByTagName(tagList);

            // 데이터 담을 변수
            int intStep = 0; // 노출 순서
            for (int i = 0; i < nodeList.getLength(); i++) {

                Node node = nodeList.item(i);

                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;

                    strPropertyId = xmlUtility.getTagValue("PropertyId", tagElement);
                    intAID = Integer.parseInt(hotelStoryMapper.insertAccomm(strPropertyId, "", "", "", "", "", "", "", ""
                            , "", "", "", "", "", "")); // intAID 가져오기위함

                    if(tagList.equals("Image")){

                        result += xmlUtility.getTagValue("ImageUrl",element)+"<br>";

                        String strImage = xmlUtility.getTagValue("ImageUrl",element).toString();
                        accommPhotoContentsReg(strImage, strPropertyId, xmlUtility.getTagValue("PropertyName", tagElement).toString(), String.valueOf(intAID));

                    }else if(tagList.equals("Description")){
                        result += "<br><br>|<br>";

                        // 룸타입(tocon) 변수
                        strRoomInformation = xmlUtility.getTagValue("RoomInformation", tagElement);
                        String strRoomTypeId = xmlUtility.getTagValue("RoomTypeId",element);
                        String strText = "";
                        String strIngYn = "N";
                        String strRoomTypeName = "";
                        String strBedTypeCode = "";
                        String strRatePlanId = "";
                        String strRatePlanName = "";
                        String strMealCode = "";
                        int intSaleRate = 0;
                        int intMinPersons = 0;
                        int intMaxPersons = 0;


                        // roomTypeList 값 넣기
                        if(strRoomTypeId != null){

                            strText = xmlUtility.getTagValue("Text", element);

                            result += "RoomTypeId = " + strRoomTypeId + "<br>";
                            result += "Text = " + strText + "<br>";


                            // 룸타입 있으면(가용하는 룸타입일경우 )
                            if(roomTypeMap.containsKey(strRoomTypeId) == true){
                                intStep ++;
                                result += "intStep = " + intStep +"<br>";
                                result += "strIngYn = Y<br>";
                                result += "RoomTypeName = "+roomTypeMap.get(strRoomTypeId).get("RoomTypeName")+"<br>";
                                result += "BedTypeCode = "+roomTypeMap.get(strRoomTypeId).get("BedTypeCode")+"<br>";
                                result += "MinPersons = "+roomTypeMap.get(strRoomTypeId).get("MinPersons")+"<br>";
                                result += "MaxPersons = "+roomTypeMap.get(strRoomTypeId).get("MaxPersons")+"<br>";

                                strIngYn = "Y";
                                strRoomTypeName = roomTypeMap.get(strRoomTypeId).get("RoomTypeName").toString();
                                intMinPersons = Integer.parseInt(roomTypeMap.get(strRoomTypeId).get("MinPersons").toString());
                                intMaxPersons = Integer.parseInt(roomTypeMap.get(strRoomTypeId).get("MaxPersons").toString());

                                int step = (strIngYn.equals("N"))? 150 : intStep;

                                // tocon 프로시저 실행
                                int toconIdx = Integer.parseInt(hotelStoryMapper.insertRoomType(strRoomTypeName, intAID, intMinPersons, intMaxPersons, strRoomTypeId, step, strIngYn, strText, strRoomInformation));

                                //System.out.println("toconIdx = "+toconIdx);

                                if(ratePlanMap.get(strRoomTypeId) != null){
                                    for(Map map : ratePlanMap.get(strRoomTypeId)){
                                        result += "RatePlanId = "+map.get("RatePlanId")+"<br>";
                                        result += "RatePlanName = "+map.get("RatePlanName")+"<br>";
                                        result += "MealCode = "+map.get("MealCode")+"<br>";
                                        result += "BedTypeCode = "+map.get("BedTypeCode")+"<br>";
                                        result += "SaleRate = "+map.get("SaleRate")+"<br>";
                                        result += "MinPersons = "+map.get("MinPersons")+"<br>";
                                        result += "MaxPersons = "+map.get("MaxPersons")+"<br>";

                                        strRatePlanId = map.get("RatePlanId").toString();
                                        strRatePlanName = map.get("RatePlanName").toString();
                                        strMealCode = map.get("MealCode").toString();
                                        strBedTypeCode = map.get("BedTypeCode").toString();
                                        intSaleRate = Integer.parseInt(map.get("SaleRate").toString());
                                        intMinPersons = Integer.parseInt(map.get("MinPersons").toString());
                                        intMaxPersons = Integer.parseInt(map.get("MaxPersons").toString());

                                        // 배드타입
                                        String strBedTypeString = "";
                                        switch (strBedTypeCode){
                                            case "1": strBedTypeString = "싱글";
                                                break;
                                            case "2": strBedTypeString = "더블";
                                                break;
                                            case "3": strBedTypeString = "트윈";
                                                break;
                                            case "4": strBedTypeString = "트리플";
                                                break;
                                            case "5": strBedTypeString = "온돌";
                                                break;
                                            case "6": strBedTypeString = "퓨전(온돌더블)";
                                                break;
                                            case "8": strBedTypeString = "스위트";
                                                break;
                                            case "9": strBedTypeString = "4베드";
                                                break;
                                            case "10": strBedTypeString = "패밀리트윈";
                                                break;
                                            case "11": strBedTypeString = "현지배정";
                                                break;
                                            case "13": strBedTypeString = "세미더블";
                                                break;
                                            case "15": strBedTypeString = "더블 더블";
                                                break;
                                            default: strBedTypeString = strBedTypeCode;
                                        }

                                        // rate_plan 프로시저 실행
                                        String ratePlanIdx = hotelStoryMapper.insertRatePlan(intAID, strPropertyId, toconIdx, strRoomTypeId, strRatePlanId, strRatePlanName, strBedTypeString, strMealCode, intMinPersons, intMaxPersons, intSaleRate);

                                        //System.out.println(ratePlanIdx);
                                    }
                                }


                            }else{
                                result += "strIngYn = N";
                            }


                        }

                    }
                }
            }
 */


            //result = result.replace("\n", "<br>");

        }catch (Exception e){
            e.printStackTrace();
        }


        return result;
    }


    /**
     * cityCode로 해당 구역 가져오기
     * @param document
     * @param cityCode
     * @return
     */
    public static String[] getCityNameByCode(Document document, String cityCode) {
        try {
            NodeList cityList = document.getElementsByTagName("City");

            for (int i = 0; i < cityList.getLength(); i++) {
                Node cityNode = cityList.item(i);
                if (cityNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element cityElement = (Element) cityNode;

                    String code = cityElement.getElementsByTagName("CityCode").item(0).getTextContent();

                    if (code.equals(cityCode)) {

                        String cityName = cityElement.getElementsByTagName("CityName").item(0).getTextContent();

                        Node provinceNode = cityElement.getParentNode().getParentNode();
                        String propertyName = ((Element) provinceNode).getElementsByTagName("PropertyName").item(0).getTextContent();


                        return new String[]{cityName, propertyName};
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 사진 저장하기
     * @param strImage
     * @param strAccommId
     * @param strAccommName
     * @param strConId
     */
    public void accommPhotoContentsReg(String strImage, String strAccommId, String strAccommName, String strConId){
        try{
            ContentsPhotoDto contentsPhotoDto = new ContentsPhotoDto();
            /**
             * 임시로 하드코딩
             */
            int intCreatedSID = 147; // 이미지 생성한사람 147 : 이석범(employ테이블)
            int intModifiedSID = 147; // 이미지 수정한사람

            String[] filePathArr = strImage.split("/");
            String strFileName = "";
            for(int j=0; j< filePathArr.length; j++){
                if(j == (filePathArr.length - 1)){
                    strFileName = filePathArr[j];
                    contentsPhotoDto.setStrFileName(strFileName);
                }
            }

            // 경로에 폴더 생성 -> 있으면 생성 안시킴
            Path directoryPath = Paths.get(Constants.hotelStoryFileDir + strAccommId + "\\");
            Files.createDirectories(directoryPath);

            // 파일 존재여부 체크
            String strFilePath = Constants.hotelStoryFileDir + strAccommId + "\\" +  strFileName;
            File file = new File(strFilePath);
            if(!(file.exists())){
                // 이미지 저장
                UrlResourceDownloader downloader = new UrlResourceDownloader(strFilePath, strImage);
                downloader.urlFileDownload();

                contentsPhotoDto.setStrFilePath("/hotelStory/" + strAccommId + "/");
                contentsPhotoDto.setIntCreatedSID(intCreatedSID);
                contentsPhotoDto.setIntModifiedSID(intModifiedSID);
                contentsPhotoDto.setStrSubject(strAccommName);
                contentsPhotoDto.setStrCid(strConId);

                String insertResult = accomodationMapper.accommPhotoContentsReg(contentsPhotoDto);
//                if(Integer.parseInt(insertResult) == 0){
//                    System.out.println("INSERT CONTENTS_PHOTO FAIL");
//                }
                System.out.println(insertResult);
            }else{
                //System.out.println("ALREADY EXISTS PHOTO");
            }



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String photoTest(String strImage, String strAccommId){

        String result = "";
        try{
            /**
             * 임시로 하드코딩
             */
            int intCreatedSID = 147; // 이미지 생성한사람 147 : 이석범(employ테이블)
            int intModifiedSID = 147; // 이미지 수정한사람

            String[] filePathArr = strImage.split("/");
            String strFileName = "";
            for(int j=0; j< filePathArr.length; j++){
                if(j == (filePathArr.length - 1)){
                    strFileName = filePathArr[j];
                }
            }

            // 경로에 폴더 생성 -> 있으면 생성 안시킴
            Path directoryPath = Paths.get(Constants.hotelStoryFileDir + strAccommId + "\\");
            Files.createDirectories(directoryPath);

            // 파일 존재여부 체크
            String strFilePath = Constants.hotelStoryFileDir + strAccommId + "\\" +  strFileName;
            File file = new File(strFilePath);
            if(!(file.exists())){
                // 이미지 저장
                UrlResourceDownloader downloader = new UrlResourceDownloader(strFilePath, strImage);
                downloader.urlFileDownload();

                result += "/hotelStory/" + strAccommId + "/" +"|^|"+ strFileName +"|^|"+ intCreatedSID +"|^|"+ intModifiedSID + "{{|}}";
            }else{
                //System.out.println("ALREADY EXISTS PHOTO");
            }



        }catch (Exception e){
            e.printStackTrace();
        }
        return result;

    }


}