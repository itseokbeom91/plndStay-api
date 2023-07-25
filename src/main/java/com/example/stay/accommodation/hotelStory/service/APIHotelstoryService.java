package com.example.stay.accommodation.hotelStory.service;

import com.example.stay.accommodation.hotelStory.dto.BookingDto;
import com.example.stay.accommodation.hotelStory.mapper.HotelStoryMapper;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.UrlResourceDownloader;
import com.example.stay.common.util.XmlUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class APIHotelstoryService {

    @Autowired
    private XmlUtility xmlUtility;

    @Autowired
    private HotelStoryMapper hotelStoryMapper;


    public String getAccomm(String strAccommID){

        String result = "";

        try {

            long APIStart = System.currentTimeMillis();
            System.out.println("API 호출 시작");

            // propertyList 불러오기
            Document document = HotelStoryAPIList("propertyList",strAccommID);


            // roomTypeListMap, ratePlanListMap 담을 map 생성
            Map<String, Map> roomTypeListMap = new HashMap<String, Map>();
            // 하나의 roomType에 여러개의 ratePlan이 있을 수 있어서 LinkedMultiValueMap 사용
            MultiValueMap<String, Map> ratePlanListMap = new LinkedMultiValueMap<>();


            // Property 반복 돌려서 strAccommID 없을때의 propertyId 값 가져와서 roomTypeLIst, ratePlanList 담기
            NodeList propertyList = document.getElementsByTagName("Property");
            System.out.println("roomType, ratePlan API 호출시작 | API 수 = "+ propertyList.getLength());
            long ListAPIStart = System.currentTimeMillis();
            int roomTypeCnt = 0;
            int ratePlanCnt = 0;
            for(int j=0; j<propertyList.getLength(); j++) {

                Node node = propertyList.item(j);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    // 노드를 사용할 수 있게 element로 변환
                    Element eElement = (Element) node;

                    // roomTypeList 불러오기
                    Document requestRoomTypeList = HotelStoryAPIList("roomTypeList",xmlUtility.getTagValue("PropertyId", eElement));

                    // RatePlanList 불러오기
                    long rpStart = System.currentTimeMillis();
                    //System.out.println("API 호출 시작");
                    Document requestRatePlanList = HotelStoryAPIList("RatePlanList",xmlUtility.getTagValue("PropertyId", eElement));
                    //System.out.println(xmlUtility.parsingXml(requestRatePlanList));

                    // 10개 단위 콘솔 출력
                    if(j%10 == 0){
                        System.out.println(j);
                    }

                    // roomTypeList 구하기
                    if(requestRoomTypeList != null){
                        NodeList nList = requestRoomTypeList.getElementsByTagName("RoomType");
//                        System.out.println("roomType 개수 = " + nList.getLength());
                        roomTypeCnt += nList.getLength();
                        for (int i = 0; i < nList.getLength(); i++) {

                            Node nNode = nList.item(i);
                            if(nNode.getNodeType() == Node.ELEMENT_NODE){
                                Element element = (Element) nNode;

                                // roomTypeID 기준 value 값 담을 map
                                Map<String, Object> valMap = new HashMap<String, Object>();
                                valMap.put("RoomTypeName", xmlUtility.getTagValue("RoomTypeName", element));
                                valMap.put("BedTypeCode", xmlUtility.getTagValue("BedTypeCode", element));
                                valMap.put("MinPersons", xmlUtility.getTagValue("MinPersons", element));
                                valMap.put("MaxPersons", xmlUtility.getTagValue("MaxPersons", element));

                                roomTypeListMap.put(xmlUtility.getTagValue("RoomTypeId", element), valMap);
                            }
                        }
                    }

                    // ratePlanList 구하기
                    if(requestRatePlanList != null){
                        NodeList nList = requestRatePlanList.getElementsByTagName("RatePlan");
//                        System.out.println("ratePlan 개수 = " + nList.getLength());
                        ratePlanCnt += nList.getLength();
                        for (int i = 0; i < nList.getLength(); i++) {

                            Node nNode = nList.item(i);
                            if(nNode.getNodeType() == Node.ELEMENT_NODE){
                                Element element = (Element) nNode;

                                // roomTypeID 기준 value 값 담을 map
                                Map<String, Object> valMap = new HashMap<>();
                                valMap.put("RatePlanId", xmlUtility.getTagValue("RatePlanId", element));
                                valMap.put("RatePlanName", xmlUtility.getTagValue("RatePlanName", element));
                                valMap.put("BedTypeCode", xmlUtility.getTagValue("BedTypeCode", element));
                                valMap.put("MealCode", xmlUtility.getTagValue("MealCode", element));
                                valMap.put("SaleRate", xmlUtility.getTagValue("SaleRate", element));
                                valMap.put("MinPersons", xmlUtility.getTagValue("MinPersons", element));
                                valMap.put("MaxPersons", xmlUtility.getTagValue("MaxPersons", element));

                                // roomTypeID dp valMap 값 담는 map
                                ratePlanListMap.add(xmlUtility.getTagValue("RoomTypeId", element), valMap); // roomTypeId 기준 ㅇㅇ

                            }
                        }
                    }
                }

                // 코드 실행 시간
                if(j == (propertyList.getLength()-1)){
                    System.out.println("roomType, ratePlan ApI 호출 완료 시간 = " + (System.currentTimeMillis()-ListAPIStart)/1000.0);
                }
            }

            // Property 반복 돌리기

            long listParsingStart = System.currentTimeMillis();
            for(int i=0; i<propertyList.getLength(); i++){

                // 10개 단위 콘솔 출력
                if(i%10 == 0){
                    System.out.println(i);
                }

                Node propertyNode = propertyList.item(i);
                if(propertyNode.getNodeType() == Node.ELEMENT_NODE){

                    // 노드를 사용할 수 있게 element로 변환
                    Element propertyElement = (Element) propertyNode;

                    /**
                     * PropertyList 구하기
                     */
                    result += hotelStoryParsing(propertyElement, roomTypeListMap, ratePlanListMap);

                }

                if(i == (propertyList.getLength()-1)){
                    System.out.println("roomType, ratePlan ApI 파싱 완료 시간 = " + (System.currentTimeMillis()-listParsingStart)/1000.0);
                }

            }
            System.out.println("시설 수 = " + propertyList.getLength());
            System.out.println("roomType 수 = " + roomTypeCnt);
            System.out.println("ratePlan 수 = " + ratePlanCnt);

            //System.out.println(result);

        }catch (Exception e){
            e.printStackTrace();
        }


        return result;


    }

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
            String strStarRating = "";
            String strNumRooms = "";
            String strCheckInTime = "";
            String strCheckOutTime = "";
            String strCity = "";
            String strPropertyDescription = "";
            String strTrafficInformation = "";
            String strRsvGuide = "";
            int intAID = 0;


            // 시설정보

            result += "PropertyId = " + xmlUtility.getTagValue("PropertyId", tagElement) + "<br>";
            result += "PropertyName = " + xmlUtility.getTagValue("PropertyName", tagElement) + "<br>";
            result += "Address = " + xmlUtility.getTagValue("Address", tagElement) + "<br>";
            result += "Latitude = " + xmlUtility.getTagValue("Latitude", tagElement) + "<br>";
            result += "Longitude = " + xmlUtility.getTagValue("Longitude", tagElement) + "<br>";
            String cityCode = xmlUtility.getTagValue("CityCode", tagElement);
            Document cityDocument = HotelStoryAPIList("CityList","");
            result += "Location = " + getCityNameByCode(cityDocument, cityCode)[1] + "<br>";
            result += "city = " + getCityNameByCode(cityDocument, cityCode)[0] + "<br>";
            result += "HomePageUrl = " + xmlUtility.getTagValue("HomePageUrl", tagElement) + "<br>";
            result += "Phone = " + xmlUtility.getTagValue("Phone", tagElement) + "<br>";
            result += "StarRating = " + xmlUtility.getTagValue("StarRating", tagElement) + "<br>";
            result += "NumRooms = " + xmlUtility.getTagValue("NumRooms", tagElement) + "<br>";
            result += "CheckInTime = " + xmlUtility.getTagValue("CheckInTime", tagElement) + "<br>";
            result += "CheckOutTime = " + xmlUtility.getTagValue("CheckOutTime", tagElement) + "<br>";
            result += "PropertyDescription = " + xmlUtility.getTagValue("PropertyDescription", tagElement) + "<br>";
            result += "TrafficInformation = " + xmlUtility.getTagValue("TrafficInformation", tagElement) + "<br>";
            result += "strRsvGuide = " + xmlUtility.getTagValue("CheckInInstructions", tagElement) + "<br>";


            // condo쪽 데이터 저장
            strPropertyId = xmlUtility.getTagValue("PropertyId", tagElement);
            strPropertyName = xmlUtility.getTagValue("PropertyName", tagElement);
            strAddress = xmlUtility.getTagValue("Address", tagElement);
            strLatitude = xmlUtility.getTagValue("Latitude", tagElement);
            strLongitude = xmlUtility.getTagValue("Longitude", tagElement);
            strLocation = getCityNameByCode(cityDocument, cityCode)[1];
            strHomePageUrl = xmlUtility.getTagValue("HomePageUrl", tagElement);
            strPhone = xmlUtility.getTagValue("Phone", tagElement);
            strStarRating = ((xmlUtility.getTagValue("StarRating", tagElement).equals("N")))? "0" : xmlUtility.getTagValue("StarRating", tagElement);
            strNumRooms = xmlUtility.getTagValue("NumRooms", tagElement);
            strCheckInTime = xmlUtility.getTagValue("CheckInTime", tagElement);
            strCheckOutTime = xmlUtility.getTagValue("CheckOutTime", tagElement);
            strCity = getCityNameByCode(cityDocument, cityCode)[0];
            strPropertyDescription = xmlUtility.getTagValue("PropertyDescription", tagElement).toString();
            strTrafficInformation = xmlUtility.getTagValue("TrafficInformation", tagElement).toString();
            strRsvGuide = xmlUtility.getTagValue("CheckInInstructions", tagElement).toString();


            /**
             * 부대시설 insert
             */
            String addData = "";
            NodeList addList = tagElement.getElementsByTagName("Additional");
            for(int i=0; i<addList.getLength(); i++){
                Node node = addList.item(i);

                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    result += xmlUtility.getTagValue("AdditionalName",element)+"<br>";
                    addData += xmlUtility.getTagValue("AdditionalCode",element) + "|^|";
                }
            }
            if(addData.length() > 1){
                addData = addData.substring(0, addData.length()-3);
            }


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
                    String strImgCode = xmlUtility.getTagValue("ImageTypeCode",element);
                    String strImgRPId = xmlUtility.getTagValue("RatePlanId",element);
                    //accommPhotoContentsReg(strImage, strPropertyId, xmlUtility.getTagValue("PropertyName", tagElement).toString(), String.valueOf(intAID));
                    imgData += accommPhotoContentsReg(strImage, strPropertyId, strImgCode, strImgRPId);

                }
            }
            if(imgData.length() > 1){
                imgData = imgData.substring(0, imgData.length()-5);
            }

            /**
             * 취소규정 데이터 insert
             */

            String cancelDatas = "";
            NodeList cancelList = tagElement.getElementsByTagName("CancelPenalty");

            // 성수기 요금 표 안줄 때 비수기꺼로 넣기 위함(반대의 경우도 적용)
            int intOpsCnt = 0; // 성수기 요금표 수
            int intOofCnt = 0; // 비수기 요금표 수
            for(int i=0; i<cancelList.getLength(); i++){
                Node node = cancelList.item(i);

                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String strCnFlag = "OPS";
                    if(element.getAttribute("Type").equals("1")){ // 비성수기
                        strCnFlag = "OOF";
                        intOofCnt += 1;
                    }else{
                        intOpsCnt += 1;
                    }

                    int intDateCnt = 0; // 비어있는 날짜 수수료 넣기 위함
                    NodeList infoList = element.getElementsByTagName("Deadline");
                    for(int j=0; j<infoList.getLength(); j++){
                        Node infoNode = infoList.item(j);

                        if(infoNode.getNodeType() == Node.ELEMENT_NODE){
                            Element infoElement = (Element) infoNode;

                            if(intDateCnt != 0){ // 처음 제외
                                int intSubCnt = Integer.parseInt(infoElement.getAttribute("Date")) - intDateCnt;

                                // 앞의 날짜와 이틀 이상 차이날 때
                                if(intSubCnt > 1){
                                    for(int c=1; c<intSubCnt; c++){
                                        cancelDatas += strCnFlag +"|^|"+ (intDateCnt+1) +"|^|"+ infoElement.getAttribute("value") + "{{|}}";
                                    }
                                }
                            }
                            cancelDatas += strCnFlag +"|^|"+ infoElement.getAttribute("Date") +"|^|"+ infoElement.getAttribute("value") + "{{|}}";

                            intDateCnt = Integer.parseInt(infoElement.getAttribute("Date"));

                        }
                    }

                }
            }

            // 비수기, 성수기 중 하나만 보내줄 때
            if(intOpsCnt + intOofCnt == 1){
                if(intOpsCnt == 0){
                    cancelDatas += cancelDatas.replace("OOF", "OPS");
                }else if(intOofCnt == 0){
                    cancelDatas += cancelDatas.replace("OPS", "OOF");
                }
            }

            if(cancelDatas.length() > 1){
                cancelDatas = cancelDatas.substring(0, cancelDatas.length()-5);
            }

            System.out.println(cancelDatas);


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

                            int step = (strIngYn.equals("N"))? 0 : intStep;

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
                                    String strBedTypeString = String.format("%02d",Integer.parseInt(strBedTypeCode));
                                    /*switch (strBedTypeCode){
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
                                    }*/

                                    ratePlanData += strRatePlanId +"|~|"+ strRatePlanName +"|~|"+ strBedTypeString +"|~|"+ strMealCode +"|~|"+ intMinPersons +"|~|"+ intMaxPersons +"{{~}}";
                                }

                            }
                            if(ratePlanData.length() > 1){
                                ratePlanData = ratePlanData.substring(0, ratePlanData.length()-5);
                            }

                            roomTypeData += strRoomTypeName +"|^|"+ intMinPersons +"|^|"+ intMaxPersons +"|^|"+ strRoomTypeId +"|^|"+ step +"|^|"+ strIngYn +"|^|"+ strText +"|^|"+ ratePlanData +"{{|}}"; // strRoomInformation
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
            String procResult = hotelStoryMapper.insertProperty(strPropertyId, strLocation, strCity, strPropertyName, strLatitude, strLongitude, strStarRating, strNumRooms, strCheckInTime, strCheckOutTime, strPhone, strAddress, strHomePageUrl
                    , strPropertyDescription, strTrafficInformation, strRsvGuide, imgData, cancelDatas, roomTypeData, addData);

            System.out.println(procResult);


            result = result.replace("<br>", "\n");

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
            NodeList successList = document.getElementsByTagName("ResponseBooking");
            NodeList errorList = document.getElementsByTagName("Error");


            if(errorList.getLength() > 0){
                Node node = errorList.item(0);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;
                    System.out.println(xmlUtility.getTagValue("ErrorCode", element));
                    System.out.println(xmlUtility.getTagValue("ErrorDescription", element));

                }

            }else if(successList.getLength() > 0){

                Node node = successList.item(0);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;
                    System.out.println(xmlUtility.getTagValue("BookingId", element));
                    System.out.println(xmlUtility.getTagValue("BookingStatus", element));
                    String strBookingId = xmlUtility.getTagValue("BookingId", element).toString();
                    String strBookingStatus = xmlUtility.getTagValue("BookingStatus", element);
                    String strBookingProcess = "";
                    if(strBookingStatus.equals("CF")) { // 예약완료
                        strBookingProcess = "4";
                    }else if(strBookingStatus.equals("CP")){ // 예약대기(번호대기)
                        strBookingProcess = "2";
                    }else if(strBookingStatus.equals("RJ")){ // 예약불가
                        strBookingProcess = "21";
                    }else{ // 취소대기
                        strBookingProcess = "14";
                    }

                    hotelStoryMapper.updateBooking(intBookingID, strBookingProcess, strBookingId, Integer.parseInt(strRoom));

                }

            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 예약 조회하기 - 조회해서 나온 취소규정(날짜별 수수료 퍼센트) insert
     * @param intBookingID
     * @return
     */
    public String bookingCheck(int intBookingID){
        String result = "";

        try {
            BookingDto bookingDto = hotelStoryMapper.getbooking(intBookingID);

            String strOrderId = Integer.toString(bookingDto.getIntBookingID());
            String strBookingId = bookingDto.getStrSpBookingId();
            String strStartDate = bookingDto.getCheckInDate().split(" ")[0];
            String strEndDate = bookingDto.getCheckOutDate().split(" ")[0];

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
                    "   <ChannelBookingId>"+strOrderId+"</ChannelBookingId>\n" +
                    "   <BookingId>"+strBookingId+"</BookingId>\n" +
                    "   <DateType>C</DateType>\n" +
                    "   <StartDate>"+strStartDate+"</StartDate>\n" +
                    "   <EndDate>"+strEndDate+"</EndDate>\n" +
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
            Document document = dBuilder.parse(conn.getInputStream());
            document.getDocumentElement().normalize();
            conn.disconnect();

            System.out.println(xmlUtility.parsingXml(document));
            int intPirce = 0;
            NodeList infoList = document.getElementsByTagName("Booking");
            Node infoNode = infoList.item(0);
            if (infoNode.getNodeType() == Node.ELEMENT_NODE) {

                Element element = (Element) infoNode;
                //System.out.println(xmlUtility.getTagValue("Price", element));
                intPirce = Integer.parseInt(xmlUtility.getTagValue("Price", element).toString());

            }

            NodeList cancelList = document.getElementsByTagName("CancelPolicy");
            if(cancelList.getLength() > 0){
                String refundData = "";
                for(int i=0; i<cancelList.getLength(); i++){
                    Node node = cancelList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {

                        Element element = (Element) node;
                        double intPercent = Integer.parseInt(xmlUtility.getTagValue("Charge", element).toString());
                        int intRefundPrice = (int) (intPirce*(intPercent/100));
                        int intRefundFee = intPirce - intRefundPrice;

                        System.out.println((int) intPercent +" / "+ intRefundPrice +" / "+ intRefundFee);
                        //System.out.println(xmlUtility.getTagValue("DeadLine", element));
                        //System.out.println(xmlUtility.getTagValue("Charge", element));
                        refundData += xmlUtility.getTagValue("DeadLine", element) + "|^|";
                        refundData += (int) intPercent +"|^|"+ intRefundPrice +"|^|"+ intRefundFee +"{{|}}";

                    }
                }
                if(refundData.length() > 1){
                    refundData = refundData.substring(0, refundData.length()-5);
                }

                hotelStoryMapper.insertRefund(intBookingID, refundData);

            }

        }catch (Exception e){
            e.printStackTrace();
        }


        return result;
    }


    /**
     * 예약 취소하기
     * @param intBookingID
     * @return
     */
    public String bookingCancel(int intBookingID){
        String result = "";

        try {
            BookingDto bookingDto = hotelStoryMapper.getbooking(intBookingID);

            String strOrderId = Integer.toString(bookingDto.getIntBookingID());
            String strBookingId = bookingDto.getStrSpBookingId();
            String strRoom = Integer.toString(bookingDto.getIntRoomCount());

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
                    "   <ChannelBookingId>"+strOrderId+"</ChannelBookingId>\n" +
                    "   <BookingId>"+strBookingId+"</BookingId>\n" +
                    "   <CancellationReason></CancellationReason>\n" +
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
            Document document = dBuilder.parse(conn.getInputStream());
            document.getDocumentElement().normalize();
            conn.disconnect();

            System.out.println(xmlUtility.parsingXml(document));
            NodeList successList = document.getElementsByTagName("ResponseCancellation");
            NodeList errorList = document.getElementsByTagName("Error");

            if(errorList.getLength() > 0){
                Node node = errorList.item(0);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;
                    System.out.println(xmlUtility.getTagValue("ErrorCode", element));
                    System.out.println(xmlUtility.getTagValue("ErrorDescription", element));

                }

            }else if(successList.getLength() > 0){

                Node node = successList.item(0);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;

                    String strBookingStatus = xmlUtility.getTagValue("BookingStatus", element);
                    String strBookingProcess = "";
                    if(strBookingStatus.equals("CX")) { // 취소
                        strBookingProcess = "14";
                        hotelStoryMapper.updateBooking(intBookingID, strBookingProcess, strBookingId, Integer.parseInt(strRoom));
                    }else{ // 취소 실패
                        System.out.println("취소 실패");
                    }

                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    /**
     * webhook으로 받는 xml to document 파싱
     * @param strXml
     * @return
     * @throws Exception
     */
    public String getWebhook(String strXml){

        String result =
                "<ResponsePushAvailability>\n" +
                "   <Error><ErrorCode>1010</ErrorCode><ErrorDescription><![CDATA[not is data]]></ErrorDescription></Error>\n" +
                "</ResponsePushAvailability>";

        try {

            getClientIP(); // 클라이언트 IP 구하기

            // 우선 test xml 삽입
            strXml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><RequestPushAvailability>   <Auth>       <AuthId>hotelstory</AuthId>       <AuthKey>ghxpftmxhfl!@#</AuthKey>   </Auth>   <PropertyId>1000582</PropertyId>   <AvailabilityList>       <Availability>           <RoomTypeId>140148</RoomTypeId>           <RatePlanId>140149</RatePlanId>           <Dates>               <Date Allotment=\"0\" Price=\"54545\">2023-05-22</Date>               <Date Allotment=\"0\" Price=\"54545\">2023-05-23</Date>               <Date Allotment=\"0\" Price=\"54545\">2023-05-24</Date>               <Date Allotment=\"4\" Price=\"54545\">2023-05-25</Date>               <Date Allotment=\"4\" Price=\"81818\">2023-05-26</Date>               <Date Allotment=\"0\" Price=\"109091\">2023-05-27</Date>               <Date Allotment=\"1\" Price=\"90909\">2023-05-28</Date>               <Date Allotment=\"4\" Price=\"63636\">2023-05-29</Date>               <Date Allotment=\"4\" Price=\"63636\">2023-05-30</Date>               <Date Allotment=\"8\" Price=\"63636\">2023-05-31</Date>           </Dates>       </Availability>   </AvailabilityList></RequestPushAvailability>";

            InputSource is = new InputSource(new StringReader(strXml));
            is.setEncoding("UTF-8");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(is);

            String strProcResult = parsingGoods(document);
            if(strProcResult.equals("저장완료")){

            }else{

            }

            result =
                    "<ResponsePushAvailability>\n" +
                    "   <Success/>\n" +
                    "</ResponsePushAvailability>";

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
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
                    String strPropertyId = xmlUtility.getTagValue("PropertyId", element).toString();
                    String strRoomTypeId = xmlUtility.getTagValue("RoomTypeId", element).toString();
                    String strRatePlanId = xmlUtility.getTagValue("RatePlanId", element).toString();

                    Map<String, String> aidRmIdxMap = hotelStoryMapper.getAcmRmIdx(strPropertyId, strRoomTypeId, strRatePlanId);
                    int intAID = Integer.parseInt(String.valueOf(aidRmIdxMap.get("intAID")));
                    int intRmIdx = Integer.parseInt(String.valueOf(aidRmIdxMap.get("intIdx")));

                    NodeList dateList = document.getElementsByTagName("Date");
                    String strStockDatas = "";
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


                            /** 요금 구하기 */
                            // 오픈마켓 일~금 9%, 토 10% 마진
                            int intBasicPrice = Integer.parseInt(dateElement.getAttribute("Price").toString());
                            int intOMKPrice = intBasicPrice;
                            if(intWeek == 6){
                                intOMKPrice = (int) Math.round(intBasicPrice*1.1);
                            }else{
                                intOMKPrice = (int) Math.round(intBasicPrice*1.09);
                            }
                            // 10원 단위 절사
                            intOMKPrice = intOMKPrice - (intOMKPrice%10);

                            // 판매가 마진 구하기
                            // 우선 원가와 동일하게
                            int intSalePrice = intBasicPrice;
                            if(intWeek == 5 || intWeek == 6){
                                intSalePrice = (int) Math.round(intBasicPrice*1);
                            }else{
                                intSalePrice = (int) Math.round(intBasicPrice*1);
                            }
                            // 10원 단위 절사
                            intSalePrice = intSalePrice - (intSalePrice%10);


                            int intStock = Integer.parseInt(dateElement.getAttribute("Allotment").toString());

                            strStockDatas += strDate + "|^|" + intStock + "|^|" + intBasicPrice + "|^|" + intSalePrice + "|^|0|^|0|^|0|^|" + intStock + "|^|" + intOMKPrice + "{{|}}";

                        }
                    }

                    if(strStockDatas.length() > 1){
                        strStockDatas = strStockDatas.substring(0, strStockDatas.length()-5);
                    }

                    result = hotelStoryMapper.insertStock(intAID, intRmIdx, strStockDatas);
                    result = result.substring(result.length()-4);

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 호텔스토리 API 가져오기
     * @param type
     * @param strAccommID
     * @return xml
     * @throws Exception
     */
    public Document HotelStoryAPIList(String type, String strAccommID) throws Exception{

        // roomTypeList 인지 ratePlanList 인지 구분
        String requestType = "";
        if(type == "propertyList") {
            requestType = "RequestPropertyList";
        }else if(type == "roomTypeList"){
            requestType = "RequestRoomTypeList";
        }else if(type == "RatePlanList"){
            requestType = "RequestRatePlanList";
        }else if(type == "CityList"){
            requestType = "RequestProvinceCityList";
        }

        // API 호출 정보
        String sysHotelStoryID = Constants.hotelStoryID;
        String sysHotelStoryAuthKey = Constants.hotelStoryAuthKey;
        URL url = new URL("https://b2b.hotelstory.com/API/api.php");

        // API 호출
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        String sysApiContent = "    <"+requestType+">";
        sysApiContent += "              <Auth>";
        sysApiContent += "                  <AuthId>"+sysHotelStoryID+"</AuthId>";
        sysApiContent += "                  <AuthKey>"+sysHotelStoryAuthKey+"</AuthKey>";
        sysApiContent += "              </Auth>";
        if(strAccommID == null || strAccommID == "" || strAccommID.length() == 0){ /* porpertyId 없을때는 비워두고 호출(RequestPropertyList일때만임.) */ }else{
            sysApiContent += "          <PropertyId>"+strAccommID+"</PropertyId>";
        }
        sysApiContent += "          </"+requestType+">";

        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
        writer.write(sysApiContent);
        writer.close();

        // 코드실행시간 출력
        /*
        long APIEnd = System.currentTimeMillis();
        if(type == "propertyList") {
            System.out.println("API 호출 완료시간 = "+(APIEnd-startTime)/1000.0);
            System.out.println("xml DOM 저장 시작");
        }
        */

        // transformer 사용하기 위해 xml을 Document로 파싱
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(conn.getInputStream());
        doc.getDocumentElement().normalize();
        conn.disconnect();


        // 코드실행시간 출력
        /*
        if(type == "propertyList") {
            long xmlEnd = System.currentTimeMillis();
            System.out.println("xml DOM 저장 완료 = "+(xmlEnd-APIEnd)/1000.0);
        }
        */

        return doc;
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
                        if(propertyName.equals("제주도")){
                            propertyName = "제주특별자치도";
                        }


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
    public String accommPhotoContentsReg(String strImage, String strAccommId, String strImgCode, String strImgRPId){

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

                if(strImgCode.equals("H")){
                    result += "/hotelStory/" + strAccommId + "/" +"|^|"+ strFileName +"|^|"+ intCreatedSID +"|^|"+ intModifiedSID +"|^|"+ strImgCode + "|^|0000{{|}}";
                }else if(strImgCode.equals("R")){
                    result += "/hotelStory/" + strAccommId + "/" +"|^|"+ strFileName +"|^|"+ intCreatedSID +"|^|"+ intModifiedSID +"|^|"+ strImgCode + "|^|" + strImgRPId + "{{|}}";
                }

//                result += "/hotelStory/" + strAccommId + "/" +"|^|"+ strFileName +"|^|"+ intCreatedSID +"|^|"+ intModifiedSID + "|^|";
            }else{
                //System.out.println("ALREADY EXISTS PHOTO");
            }



        }catch (Exception e){
            e.printStackTrace();
        }
        return result;

    }


    /**
     * IP 구하기
     * @return
     */
    public static String getClientIP() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        //System.out.println("> X-FORWARDED-FOR : " + ip);

        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
            //System.out.println("> Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            //System.out.println(">  WL-Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            //System.out.println("> HTTP_CLIENT_IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            //System.out.println("> HTTP_X_FORWARDED_FOR : " + ip);
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
            //System.out.println("> getRemoteAddr : "+ip);
        }
        System.out.println("> Result : IP Address : "+ip);

        return ip;
    }


}