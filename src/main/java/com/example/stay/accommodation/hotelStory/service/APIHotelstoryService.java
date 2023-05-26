package com.example.stay.accommodation.hotelStory.service;

import com.example.stay.accommodation.hotelStory.dto.BookingDto;
import com.example.stay.accommodation.hotelStory.mapper.HotelStoryMapper;
import com.example.stay.accommodation.onda.mapper.AccomodationMapper;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.UrlResourceDownloader;
import com.example.stay.common.util.XmlUtility;
import com.example.stay.openMarket.common.dto.CancelInfoDto;
import com.example.stay.openMarket.common.dto.ContentsPhotoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
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
     * @param roomTypeMap
     * @param ratePlanMap
     * @return String result
     * @throws Exception
     */
    public String hotelStoryParsing(Element tagElement, Map<String, Map> roomTypeMap, MultiValueMap<String, Map> ratePlanMap){
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
//            intAID = Integer.parseInt(hotelStoryMapper.insertAccomm(strPropertyId, strPropertyName, strAddress, strPhone, strNumRooms, strLocation, strHomePageUrl, strCheckInTime, strCheckOutTime
//                                                            , strLongitude, strLatitude, strCity, strPropertyDescription, strTrafficInformation, strRoomInformation));

            //System.out.println("con_id = " + intAID);

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
                    imgData += accommPhotoContentsReg(strImage, strPropertyId);


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
                            //System.out.println(strRoomInformation);
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




            //result = result.replace("\n", "<br>");

        }catch (Exception e){
            e.printStackTrace();
        }


        return result;
    }


    /**
     * 예약하기
     * @param intBookingID
     * @return
     */
    public String booking(int intBookingID){
        String result = "";

        try {
            BookingDto bookingDto = hotelStoryMapper.getbooking(intBookingID);
            //System.out.println(bookingDto);
            String strOrderId = Integer.toString(bookingDto.getIntBookingID());
            String strPropertyId = bookingDto.getStrPropertyId();
            String strRoomTypeId = bookingDto.getStrRoomTypeId();
            String strRatePlanId = bookingDto.getStrRatePlanId();
            String strRatePlanName = bookingDto.getStrRatePlanName();
            String strRoom = Integer.toString(bookingDto.getIntRoomCount());
            String strStartDate = bookingDto.getCheckInDate().split(" ")[0];
            String strEndDate = bookingDto.getCheckOutDate().split(" ")[0];
            String strPrice = Integer.toString(bookingDto.getIntPaymentPrice());

            // 주문자 이름 및 정보
            String strOrderName = bookingDto.getStrOrdName();
            String strOrderLastName = strOrderName.substring(0,1);
            String strOrderFirstName = strOrderName.substring(strOrderName.lastIndexOf(strOrderLastName)+1);
            String strOrderEmail = bookingDto.getStrOrdEmail();
            String strOrderPhone = bookingDto.getStrOrdPhone();

            // 투숙자 이름 및 정보
            String strUserName = bookingDto.getStrRecvName();
            String strUserLastName = strUserName.substring(0,1);
            String strUserFirstName = strUserName.substring(strUserName.lastIndexOf(strUserLastName)+1);
            String strUserEmail = bookingDto.getStrRecvEmail();
            String strUserPhone = bookingDto.getStrRecvPhone();

            // API 호출 정보
            String sysHotelStoryID = Constants.hotelStoryID;
            String sysHotelStoryAuthKey = Constants.hotelStoryAuthKey;
            URL url = new URL("https://b2b.hotelstory.com/API/api.php");

            String strXml =
                    "<RequestBooking>\n" +
                    "   <Auth>\n" +
                    "       <AuthId>"+sysHotelStoryID+"</AuthId>\n" +
                    "       <AuthKey>"+sysHotelStoryAuthKey+"</AuthKey>\n" +
                    "   </Auth>\n" +
                    "   <Channel>condo2424</Channel>\n" +
                    "   <ChannelBookingId>"+strOrderId+"</ChannelBookingId>\n" +
                    "   <PropertyId>"+strPropertyId+"</PropertyId>\n" +
                    "   <RoomTypeId>"+strRoomTypeId+"</RoomTypeId>\n" +
                    "   <RatePlanId>"+strRatePlanId+"</RatePlanId>\n" +
                    "   <RatePlanName>"+strRatePlanName+"</RatePlanName>\n" +
                    "   <NumRooms>"+strRoom+"</NumRooms>\n" +
                    "   <StartDate>"+strStartDate+"</StartDate>\n" +
                    "   <EndDate>"+strEndDate+"</EndDate>\n" +
                    "   <BedTypeCode></BedTypeCode>\n" +
                    "   <MealCode></MealCode>\n" +
                    "   <Price>"+strPrice+"</Price>\n" +
                    "   <AdultCount>2</AdultCount>\n" +
                    "   <ChildCount></ChildCount>\n" +
                    "   <Customer>\n" +
                    "       <CustomerFName>"+strOrderFirstName+"</CustomerFName>\n" +
                    "       <CustomerLName>"+strOrderLastName+"</CustomerLName>\n" +
                    "       <CustomerEmail>"+strOrderEmail+"</CustomerEmail>\n" +
                    "       <CustomerPhone>"+strOrderPhone+"</CustomerPhone>\n" +
                    "   </Customer>\n" +
                    "   <Occupant>\n" +
                    "       <OccupantFName>"+strUserFirstName+"</OccupantFName>\n" +
                    "       <OccupantLName>"+strUserLastName+"</OccupantLName>\n" +
                    "       <OccupantEmail>"+strUserEmail+"</OccupantEmail>\n" +
                    "       <OccupantPhone>"+strUserPhone+"</OccupantPhone>\n" +
                    "   </Occupant>\n" +
                    "   <CancelPolicys>\n" +
                    "       <CancelPolicy>\n" +
                    "       </CancelPolicy>\n" +
                    "   </CancelPolicys>\n" +
                    "</RequestBooking>\n";

            System.out.println(strXml);

            // API 호출
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            writer.write(strXml);
            writer.close();

            // transformer 사용하기 위해 xml을 Document로 파싱
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(conn.getInputStream());
            document.getDocumentElement().normalize();
            conn.disconnect();

            System.out.println(xmlUtility.parsingXml(document));
            NodeList nodeList = document.getElementsByTagName("ResponseBooking");
            for(int i=0; i<nodeList.getLength(); i++) {

                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;
                    //System.out.println(element.);
                    if(xmlUtility.getTagValue("ErrorCode",element).isEmpty()){

                    }else{
                        System.out.println(xmlUtility.getTagValue("ErrorCode", element));
                    }

                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }


    public String bookingCheck(){
        String result = "";

        try {
            // API 호출 정보
            String sysHotelStoryID = Constants.hotelStoryID;
            String sysHotelStoryAuthKey = Constants.hotelStoryAuthKey;
            URL url = new URL("https://b2b.hotelstory.com/API/api.php");

            String strXml =
                    "<RequestBookingList>\n" +
                    "   <Auth>\n" +
                    "       <AuthId>"+sysHotelStoryID+"</AuthId>\n" +
                    "       <AuthKey>"+sysHotelStoryAuthKey+"</AuthKey>\n" +
                    "   </Auth>\n" +
                    "   <ChannelBookingId>2</ChannelBookingId>\n" +
                    "   <BookingId>S2305252145</BookingId>\n" +
                    "   <DateType>C</DateType>\n" +
                    "   <StartDate>2023-06-27</StartDate>\n" +
                    "   <EndDate>2023-06-28</EndDate>\n" +
                    "</RequestBookingList>\n";

            System.out.println(strXml);

            // API 호출
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            writer.write(strXml);
            writer.close();

            // transformer 사용하기 위해 xml을 Document로 파싱
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(conn.getInputStream());
            doc.getDocumentElement().normalize();
            conn.disconnect();

            System.out.println(xmlUtility.parsingXml(doc));

        }catch (Exception e){
            e.printStackTrace();
        }


        return result;
    }

    public String bookingCancel(){
        String result = "";

        try {
            // API 호출 정보
            String sysHotelStoryID = Constants.hotelStoryID;
            String sysHotelStoryAuthKey = Constants.hotelStoryAuthKey;
            URL url = new URL("https://b2b.hotelstory.com/API/api.php");

            String strXml =
                    "<RequestCancellation>\n" +
                    "   <Auth>\n" +
                    "       <AuthId>"+sysHotelStoryID+"</AuthId>\n" +
                    "       <AuthKey>"+sysHotelStoryAuthKey+"</AuthKey>\n" +
                    "   </Auth>\n" +
                    "   <ChannelBookingId>2</ChannelBookingId>\n" +
                    "   <BookingId>S2305252145</BookingId>\n" +
                    "   <CancellationReason>test_cancel</CancellationReason>\n" +
                    "</RequestCancellation>\n";

            System.out.println(strXml);

            // API 호출
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            writer.write(strXml);
            writer.close();

            // transformer 사용하기 위해 xml을 Document로 파싱
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(conn.getInputStream());
            doc.getDocumentElement().normalize();
            conn.disconnect();

            System.out.println(xmlUtility.parsingXml(doc));

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    /**
     * webhook으로 받는 xml to document 파싱
     * @param strXML
     * @return
     * @throws Exception
     */
    public Document getWebhook(String strXML) throws Exception{

        InputSource is = new InputSource(new StringReader(strXML));
        is.setEncoding("UTF-8");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.parse(is);

        return document;

    }

    /**
     * webhook으로 받은 재고 업데이트
     * @param document
     * @return
     */
    public String parsingGoods(Document document){

        String result = "";

        try {
            NodeList nodeList = document.getElementsByTagName("RequestPushAvailability");
            for(int i=0; i<nodeList.getLength(); i++) {

                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;
                    String strRatePlanId = xmlUtility.getTagValue("RatePlanId", element).toString();

                    NodeList dateList = document.getElementsByTagName("Date");
                    for(int j=0; j<dateList.getLength(); j++){
                        Node dateNode = dateList.item(j);
                        if(dateNode.getNodeType() == Node.ELEMENT_NODE){
                            Element dateElement = (Element) dateNode;

                            // 날짜
                            String strDate = dateNode.getTextContent().toString();

                            // 요일 구하기
                            String[] spiDate = strDate.split("-");
                            LocalDate date = LocalDate.of(Integer.parseInt(spiDate[0].toString()),Integer.parseInt(spiDate[1].toString()),Integer.parseInt(spiDate[2].toString()));
                            DayOfWeek week = date.getDayOfWeek();
                            int intWeek = week.getValue();  // 1: 월요일, 7: 일요일
                            //System.out.println(intWeek);

                            // 평일 8%, 금,토 10% 마진 남기기
                            int intBasicPrice = Integer.parseInt(dateElement.getAttribute("Price").toString());
                            int intSalePrice = intBasicPrice;
                            if(intWeek == 5 || intWeek == 6){
                                intSalePrice = (int) Math.round(intBasicPrice*1.1);
                            }else{
                                intSalePrice = (int) Math.round(intBasicPrice*1.08);
                            }
                            // 10원 단위 절사
                            intSalePrice = intSalePrice - (intSalePrice%10);
                            int intStock = Integer.parseInt(dateElement.getAttribute("Allotment").toString());

                            // 프로시저 실행
                            hotelStoryMapper.insertGoods(strRatePlanId, intStock, strDate, intBasicPrice, intSalePrice);
                        }
                    }
                }
            }
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
     */
    public String accommPhotoContentsReg(String strImage, String strAccommId){

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