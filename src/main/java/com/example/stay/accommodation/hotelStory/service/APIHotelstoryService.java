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
     * @param roomTypeMap
     * @param ratePlanMap
     * @return String result
     * @throws Exception
     */
    public String hotelStoryParsing(Element tagElement, String tagList, Map<String, Map> roomTypeMap, Map<String, Map> ratePlanMap){
        String result = "";

        try {

            // 시설(condo) 변수
            String strPropertyId = "";
            String strPropertyName = "";
            String strAddress = "";
            String strLatitude = "";
            String strLongitude = "";
            String strHomePageUrl = "";
            String strPhone = "";
            String strNumRooms = "";
            String strCheckInTime = "";
            String strCheckOutTime = "";
            String strPropertyDescription = "";
            String strTrafficInformation = "";
            String strRoomInformation = "";
            int intAID = 0;


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
                strPropertyId = xmlUtility.getTagValue("PropertyId", tagElement);
                strPropertyName = xmlUtility.getTagValue("PropertyName", tagElement);
                strAddress = xmlUtility.getTagValue("Address", tagElement);
                strLatitude = xmlUtility.getTagValue("Latitude", tagElement);
                strLongitude = xmlUtility.getTagValue("Longitude", tagElement);
                strHomePageUrl = xmlUtility.getTagValue("HomePageUrl", tagElement);
                strPhone = xmlUtility.getTagValue("Phone", tagElement);
                strNumRooms = xmlUtility.getTagValue("NumRooms", tagElement);
                strCheckInTime = xmlUtility.getTagValue("CheckInTime", tagElement);
                strCheckOutTime = xmlUtility.getTagValue("CheckOutTime", tagElement);
                strPropertyDescription = xmlUtility.getTagValue("PropertyDescription", tagElement);
                strTrafficInformation = xmlUtility.getTagValue("TrafficInformation", tagElement);
                strRoomInformation = xmlUtility.getTagValue("RoomInformation", tagElement);

                intAID = Integer.parseInt(hotelStoryMapper.insertAccomm(strPropertyId, strPropertyName, strAddress, strPhone, strNumRooms, strHomePageUrl, strCheckInTime, strCheckOutTime
                                                            , strLongitude, strLatitude, strPropertyDescription, strTrafficInformation, strRoomInformation));

                System.out.println("con_id = " + intAID);
            }

            NodeList nodeList = tagElement.getElementsByTagName(tagList);

            // 데이터 담을 변수
            int intStep = 0; // 노출 순서
            for (int i = 0; i < nodeList.getLength(); i++) {

                Node node = nodeList.item(i);

                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;


                    if(tagList.equals("Image")){

                        result += xmlUtility.getTagValue("ImageUrl",element)+"<br>";

                    }else if(tagList.equals("Description")){
                        result += "<br><br>|<br>";

                        // 룸타입(tocon) 변수
                        strPropertyId = xmlUtility.getTagValue("PropertyId", tagElement);
                        intAID = Integer.parseInt(hotelStoryMapper.insertAccomm(strPropertyId, "", "", "", "", "", "", ""
                                , "", "", "", "", ""));
                        strRoomInformation = xmlUtility.getTagValue("RoomInformation", tagElement);
                        String strRoomTypeId = "";
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

                            result += "RoomTypeId = " + strRoomTypeId + "<br>";
                            result += "Text = " + xmlUtility.getTagValue("Text", element) + "<br>";

                            strRoomTypeId = xmlUtility.getTagValue("RoomTypeId",element);
                            strText = xmlUtility.getTagValue("Text", element);

                            // 룸타입 있으면(가용하는 룸타입일경우 )
                            if(roomTypeMap.containsKey(strRoomTypeId) == true){
                                intStep ++;
                                result += "intStep = " + intStep +"<br>";
                                result += "strIngYn = Y<br>";
                                result += "RoomTypeName = "+roomTypeMap.get(strRoomTypeId).get("RoomTypeName")+"<br>";
                                result += "BedTypeCode = "+roomTypeMap.get(strRoomTypeId).get("BedTypeCode")+"<br>";

                                result += "RatePlanId = "+ratePlanMap.get(strRoomTypeId).get("RatePlanId")+"<br>";
                                result += "RatePlanName = "+ratePlanMap.get(strRoomTypeId).get("RatePlanName")+"<br>";
                                result += "MealCode = "+ratePlanMap.get(strRoomTypeId).get("MealCode")+"<br>";
                                result += "SaleRate = "+ratePlanMap.get(strRoomTypeId).get("SaleRate")+"<br>";
                                result += "MinPersons = "+ratePlanMap.get(strRoomTypeId).get("MinPersons")+"<br>";
                                result += "MaxPersons = "+ratePlanMap.get(strRoomTypeId).get("MaxPersons")+"<br>";

                                strIngYn = "Y";
                                strRoomTypeName = roomTypeMap.get(strRoomTypeId).get("RoomTypeName").toString();
                                strBedTypeCode = roomTypeMap.get(strRoomTypeId).get("BedTypeCode").toString();

                                strRatePlanId = ratePlanMap.get(strRoomTypeId).get("RatePlanId").toString();
                                strRatePlanName = ratePlanMap.get(strRoomTypeId).get("RatePlanName").toString();
                                strMealCode = ratePlanMap.get(strRoomTypeId).get("MealCode").toString();
                                intSaleRate = Integer.parseInt(ratePlanMap.get(strRoomTypeId).get("SaleRate").toString());
                                intMinPersons = Integer.parseInt(ratePlanMap.get(strRoomTypeId).get("MinPersons").toString());
                                intMaxPersons = Integer.parseInt(ratePlanMap.get(strRoomTypeId).get("MaxPersons").toString());

                            }else{
                                result += "strIngYn = N";
                            }


                            int step = (strIngYn.equals("N"))? 150 : intStep;

                            String roomTypeRegist = hotelStoryMapper.insertRoomType(strRoomTypeName, intAID, intSaleRate, intMinPersons, intMaxPersons, strRatePlanId, strRoomTypeId, step, strIngYn, strText, strRoomInformation);

                            System.out.println("roomTypeId = "+roomTypeRegist);

                        }

                    }
                }
            }


            //result = result.replace("\n", "<br>");

        }catch (Exception e){
            e.printStackTrace();
        }


        return result;
    }



}
