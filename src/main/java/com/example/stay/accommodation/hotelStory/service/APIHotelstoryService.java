package com.example.stay.accommodation.hotelStory.service;

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

    public String hotelStoryParsing(Element tagElement, String tagList, String[] tagName, Map<String, Map> roomTypeMap, Map<String, Map> ratePlanMap){
        String result = "";

        try {
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
                result += "CheckInInstructions = " + xmlUtility.getTagValue("CheckInInstructions", tagElement) + "<br>";

            }

            NodeList nodeList = tagElement.getElementsByTagName(tagList);

            // 데이터 담을 변수
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

                        // roomTypeList 값 넣기
                        if(xmlUtility.getTagValue("RoomTypeId", element) != null && roomTypeMap.containsKey(xmlUtility.getTagValue("RoomTypeId", element)) == true){
                            for(int j=0;j<tagName.length;j++){
                                result += tagName[j]+" = "+xmlUtility.getTagValue(tagName[j],element)+"<br>";
                            }
                            result += "RoomTypeName = "+roomTypeMap.get(xmlUtility.getTagValue("RoomTypeId",element)).get("RoomTypeName")+"<br>";
                            result += "BedTypeCode = "+roomTypeMap.get(xmlUtility.getTagValue("RoomTypeId",element)).get("BedTypeCode")+"<br>";
                            result += "MinPersons = "+roomTypeMap.get(xmlUtility.getTagValue("RoomTypeId",element)).get("MinPersons")+"<br>";
                            result += "MaxPersons = "+roomTypeMap.get(xmlUtility.getTagValue("RoomTypeId",element)).get("MaxPersons")+"<br>";
                        }

                        if(xmlUtility.getTagValue("RoomTypeId", element) != null && ratePlanMap.containsKey(xmlUtility.getTagValue("RoomTypeId", element)) == true){
                            result += "RatePlanName = "+ratePlanMap.get(xmlUtility.getTagValue("RoomTypeId",element)).get("RatePlanName")+"<br>";
                            result += "BedTypeCode = "+ratePlanMap.get(xmlUtility.getTagValue("RoomTypeId",element)).get("BedTypeCode")+"<br>";
                            result += "MealCode = "+ratePlanMap.get(xmlUtility.getTagValue("RoomTypeId",element)).get("MealCode")+"<br>";
                            result += "SaleRate = "+ratePlanMap.get(xmlUtility.getTagValue("RoomTypeId",element)).get("SaleRate")+"<br>";
                            result += "MinPersons = "+ratePlanMap.get(xmlUtility.getTagValue("RoomTypeId",element)).get("MinPersons")+"<br>";
                            result += "MaxPersons = "+ratePlanMap.get(xmlUtility.getTagValue("RoomTypeId",element)).get("MaxPersons")+"<br>";
//                        System.out.println(ratePlanMap.get(getTagValue("RoomTypeId",element)).get("RatePlanName"));
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
