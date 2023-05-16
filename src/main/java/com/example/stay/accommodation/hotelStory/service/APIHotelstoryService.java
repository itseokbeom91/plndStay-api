package com.example.stay.accommodation.hotelStory.service;

import com.example.stay.accommodation.hotelStory.mapper.HotelStoryMapper;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.XmlUtility;
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
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    public String hotelStoryParsing(Element tagElement, String tagList, Map<String, Map> roomTypeMap, MultiValueMap<String, Map> ratePlanMap){
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

                // condo 프로시저 실행
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
                                , "", "", "", "", "")); // intAID 가져오기위함
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
                                intMinPersons = Integer.parseInt(roomTypeMap.get(strRoomTypeId).get("intMinPersons").toString());
                                intMaxPersons = Integer.parseInt(roomTypeMap.get(strRoomTypeId).get("intMaxPersons").toString());

                                int step = (strIngYn.equals("N"))? 150 : intStep;

                                // tocon 프로시저 실행
                                int toconIdx = Integer.parseInt(hotelStoryMapper.insertRoomType(strRoomTypeName, intAID, intMinPersons, intMaxPersons, strRoomTypeId, step, strIngYn, strText, strRoomInformation));

                                System.out.println("toconIdx = "+toconIdx);

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
                                    String ratePlanIdx = hotelStoryMapper.insertRatePlan(intAID, strPropertyId, toconIdx, strRoomTypeId, strRatePlanId, strRatePlanName, strBedTypeString, strMealCode, intMinPersons, intMaxPersons);

                                    System.out.println(ratePlanIdx);
                                }

                            }else{
                                result += "strIngYn = N";
                            }





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
