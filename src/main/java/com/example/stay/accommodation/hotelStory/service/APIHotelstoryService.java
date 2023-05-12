package com.example.stay.accommodation.hotelStory.service;

import com.example.stay.accommodation.hotelStory.mapper.HotelStoryMapper;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.XmlUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Service
public class APIHotelstoryService {

    @Autowired
    private XmlUtility xmlUtility;

    @Autowired
    private HotelStoryMapper hotelStoryMapper;


    /**
     * API 데이터 파싱
     * @param tagElement
     * @param tagList
     * @param tagName
     * @param frontSb
     * @param backSb
     * @param roomTypeMap
     * @param ratePlanMap
     * @return StringBuilder result
     * @throws Exception
     */
    public StringBuilder parsing(Element tagElement, String tagList, String[] tagName, StringBuilder frontSb, StringBuilder backSb, Map<String, Map> roomTypeMap, Map<String, Map> ratePlanMap) throws Exception{

        NodeList nodeList = tagElement.getElementsByTagName(tagList);

        // 데이터 담을 변수
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodeList.getLength(); i++) {

            Node node = nodeList.item(i);

            if(node.getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element) node;
                sb.append(frontSb);


                if(tagList != "Image"){ // 이미지 태그가 아닐 경우

                    // roomTypeList 값 넣기
                    if(xmlUtility.getTagValue("RoomTypeId", element) != null && roomTypeMap.containsKey(xmlUtility.getTagValue("RoomTypeId", element)) == true){
                        for(int j=0;j<tagName.length;j++){
                            sb.append(tagName[j]+" = "+xmlUtility.getTagValue(tagName[j],element)+"\n");
                        }
                        sb.append("RoomTypeName = "+roomTypeMap.get(xmlUtility.getTagValue("RoomTypeId",element)).get("RoomTypeName")+"\n");
                        sb.append("BedTypeCode = "+roomTypeMap.get(xmlUtility.getTagValue("RoomTypeId",element)).get("BedTypeCode")+"\n");
                        sb.append("MinPersons = "+roomTypeMap.get(xmlUtility.getTagValue("RoomTypeId",element)).get("MinPersons")+"\n");
                        sb.append("MaxPersons = "+roomTypeMap.get(xmlUtility.getTagValue("RoomTypeId",element)).get("MaxPersons")+"\n");
                    }

                    if(xmlUtility.getTagValue("RoomTypeId", element) != null && ratePlanMap.containsKey(xmlUtility.getTagValue("RoomTypeId", element)) == true){
                        sb.append("RatePlanName = "+ratePlanMap.get(xmlUtility.getTagValue("RoomTypeId",element)).get("RatePlanName")+"\n");
                        sb.append("BedTypeCode = "+ratePlanMap.get(xmlUtility.getTagValue("RoomTypeId",element)).get("BedTypeCode")+"\n");
                        sb.append("MealCode = "+ratePlanMap.get(xmlUtility.getTagValue("RoomTypeId",element)).get("MealCode")+"\n");
                        sb.append("SaleRate = "+ratePlanMap.get(xmlUtility.getTagValue("RoomTypeId",element)).get("SaleRate")+"\n");
                        sb.append("MinPersons = "+ratePlanMap.get(xmlUtility.getTagValue("RoomTypeId",element)).get("MinPersons")+"\n");
                        sb.append("MaxPersons = "+ratePlanMap.get(xmlUtility.getTagValue("RoomTypeId",element)).get("MaxPersons")+"\n");
//                        System.out.println(ratePlanMap.get(getTagValue("RoomTypeId",element)).get("RatePlanName"));
                    }
                }else{ // 이미지 태그일 경우

                    for(int j=0;j<tagName.length;j++){
                        sb.append(xmlUtility.getTagValue(tagName[j],element)+"\n");
                    }
                }
                sb.append(backSb);
            }
        }

        return sb;
    }

    /**
     * API 데이터 파싱
     * @param tagElement
     * @param tagList
     * @param tagName
     * @param roomTypeMap
     * @param ratePlanMap
     * @return String result
     * @throws Exception
     */
    public String hotelStoryParsing(Element tagElement, String tagList, String[] tagName, Map<String, Map> roomTypeMap, Map<String, Map> ratePlanMap){
        String result = "";

        try {

            // 시설정보
            if(tagList.equals("property")){

                result += "PropertyId = " + xmlUtility.getTagValue("PropertyId", tagElement) + "<br>";
                result += "PropertyName = " + xmlUtility.getTagValue("PropertyName", tagElement) + "<br>";
                result += "Address = " + xmlUtility.getTagValue("Address", tagElement) + "<br>";
                result += "Latitude = " + xmlUtility.getTagValue("Latitude", tagElement) + "<br>";
                result += "Longitude = " + xmlUtility.getTagValue("Longitude", tagElement) + "<br>";
                result += "HomePageUrl = " + xmlUtility.getTagValue("HomePageUrl", tagElement) + "<br>";
                result += "Phone = " + xmlUtility.getTagValue("Phone", tagElement) + "<br>";
                result += "NumRooms = " + xmlUtility.getTagValue("NumRooms", tagElement) + "<br>";
                result += "CheckInTime = " + xmlUtility.getTagValue("CheckInTime", tagElement) + "<br>";
                result += "CheckOutTime = " + xmlUtility.getTagValue("CheckOutTime", tagElement) + "<br>";
                result += "PropertyDescription = " + xmlUtility.getTagValue("PropertyDescription", tagElement) + "<br>";
                result += "TrafficInformation = " + xmlUtility.getTagValue("TrafficInformation", tagElement) + "<br>";
                result += "RoomInformation = " + xmlUtility.getTagValue("RoomInformation", tagElement) + "<br>";

                // condo쪽 데이터 저장
                String strPropertyId = xmlUtility.getTagValue("PropertyId", tagElement);
                String strPropertyName = xmlUtility.getTagValue("PropertyName", tagElement);
                String strAddress = xmlUtility.getTagValue("Address", tagElement);
                String strLatitude = xmlUtility.getTagValue("Latitude", tagElement);
                String strLongitude = xmlUtility.getTagValue("Longitude", tagElement);
                String strHomePageUrl = xmlUtility.getTagValue("HomePageUrl", tagElement);
                String strPhone = xmlUtility.getTagValue("Phone", tagElement);
                String strNumRooms = xmlUtility.getTagValue("NumRooms", tagElement);
                String strCheckInTime = xmlUtility.getTagValue("CheckInTime", tagElement);
                String strCheckOutTime = xmlUtility.getTagValue("CheckOutTime", tagElement);
                String strPropertyDescription = xmlUtility.getTagValue("PropertyDescription", tagElement);
                String strTrafficInformation = xmlUtility.getTagValue("TrafficInformation", tagElement);
                String strRoomInformation = xmlUtility.getTagValue("RoomInformation", tagElement);

                int intAID = hotelStoryMapper.insertAccomm(strPropertyId, strPropertyName, strAddress, strPhone, strNumRooms, strHomePageUrl, strCheckInTime, strCheckOutTime
                                                            , strLongitude, strLatitude, strPropertyDescription, strTrafficInformation, strRoomInformation);

                System.out.println("con_id = " + intAID);
            }

            NodeList nodeList = tagElement.getElementsByTagName(tagList);

            // 데이터 담을 변수
            int testMin = 0;
            for (int i = 0; i < nodeList.getLength(); i++) {

                Node node = nodeList.item(i);

                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;


                    if(tagList.equals("Image")){

                        for(int j=0;j<tagName.length;j++){
                            result += xmlUtility.getTagValue(tagName[j],element)+"<br>";
                        }

                    }else if(tagList.equals("Description")){
                        result += "<br><br>";

                        String strRoomTypeId = xmlUtility.getTagValue("RoomTypeId",element);

                        // roomTypeList 값 넣기
                        if(strRoomTypeId != null && roomTypeMap.containsKey(strRoomTypeId) == true){
                            for(int j=0;j<tagName.length;j++){
                                result += tagName[j]+" = "+xmlUtility.getTagValue(tagName[j],element)+"<br>";
                            }

                            result += "RoomTypeName = "+roomTypeMap.get(strRoomTypeId).get("RoomTypeName")+"<br>";
                            result += "BedTypeCode = "+roomTypeMap.get(strRoomTypeId).get("BedTypeCode")+"<br>";
//                            result += "MinPersons = "+roomTypeMap.get(strRoomTypeId).get("MinPersons")+"<br>";
//                            result += "MaxPersons = "+roomTypeMap.get(strRoomTypeId).get("MaxPersons")+"<br>";


                            result += "RatePlanId = "+ratePlanMap.get(strRoomTypeId).get("RatePlanId")+"<br>";
                            result += "RatePlanName = "+ratePlanMap.get(strRoomTypeId).get("RatePlanName")+"<br>";
//                            result += "BedTypeCode = "+ratePlanMap.get(strRoomTypeId).get("BedTypeCode")+"<br>";
                            result += "MealCode = "+ratePlanMap.get(strRoomTypeId).get("MealCode")+"<br>";
                            result += "SaleRate = "+ratePlanMap.get(strRoomTypeId).get("SaleRate")+"<br>";
                            result += "MinPersons = "+ratePlanMap.get(strRoomTypeId).get("MinPersons")+"<br>";
                            result += "MaxPersons = "+ratePlanMap.get(strRoomTypeId).get("MaxPersons")+"<br>";


                        }

                    }
                }
            }
            System.out.println(testMin);

            //result = result.replace("\n", "<br>");

        }catch (Exception e){
            e.printStackTrace();
        }


        return result;
    }



}
