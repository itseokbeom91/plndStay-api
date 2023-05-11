package com.example.stay.accommodation.hotelStory.controller;

import com.example.stay.accommodation.hotelStory.mapper.HotelStoryMapper;
import com.example.stay.accommodation.hotelStory.service.APIHotelstoryService;
import com.example.stay.common.util.XmlUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/hotelStory/*")
public class APIHotelStoryController {

    @Autowired
    private APIHotelstoryService apiHotelstoryService;

    @Autowired
    private HotelStoryMapper hotelStoryMapper;

//    private XmlUtility xmlUtility = new XmlUtility();
    @Autowired
    private XmlUtility xmlUtility;

    /**
     *
     * @param strAccommID
     * @return API의 img, description 값
     * @throws Exception
     */
    @GetMapping("/callapi")
    public ResponseEntity<String> callApi(String strAccommID) throws Exception {

        long APIStart = System.currentTimeMillis();
        System.out.println("API 호출 시작");
        // propertyList 불러오기
        Document document = xmlUtility.HotelStoryAPIList("propertyList",strAccommID);
        //System.out.println(xmlUtility.parsingXml(document));


        // roomTypeListMap, ratePlanListMap 담을 map 생성
        Map<String, Map> roomTypeListMap = new HashMap<String, Map>();

        Map<String, Map> ratePlanListMap = new HashMap<String, Map>();

        // Property 반복 돌려서 strAccommID 없을때의 propertyId 값 가져와서 roomTypeLIst, ratePlanList 담기
        NodeList sAIList = document.getElementsByTagName("Property");
        System.out.println("roomType, ratePlan API 호출시작 | API 수 = "+ sAIList.getLength());
        long ListAPIStart = System.currentTimeMillis();
        for(int j=0; j<sAIList.getLength(); j++) {

            Node node = sAIList.item(j);
            if (node.getNodeType() == Node.ELEMENT_NODE) {

                // 노드를 사용할 수 있게 element로 변환
                Element eElement = (Element) node;

                // roomTypeList 불러오기
                Document requestRoomTypeList = xmlUtility.HotelStoryAPIList("roomTypeList",xmlUtility.getTagValue("PropertyId", eElement));

                // RatePlanList 불러오기
                long rpStart = System.currentTimeMillis();
                //System.out.println("API 호출 시작");
                Document requestRatePlanList = xmlUtility.HotelStoryAPIList("RatePlanList",xmlUtility.getTagValue("PropertyId", eElement));
                //System.out.println(xmlUtility.parsingXml(requestRatePlanList));

                // 10개 단위 콘솔 출력
                if(j%10 == 0){
                    System.out.println(j);
                }

                // roomTypeList 구하기
                if(requestRoomTypeList != null){
                    NodeList nList = requestRoomTypeList.getElementsByTagName("RoomType");
                    List list = new ArrayList<Object>();
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
                            list.add(roomTypeListMap);
                        }
                    }
                }

                // ratePlanList 구하기
                if(requestRatePlanList != null){
                    NodeList nList = requestRatePlanList.getElementsByTagName("RatePlan");

                    for (int i = 0; i < nList.getLength(); i++) {

                        Node nNode = nList.item(i);
                        if(nNode.getNodeType() == Node.ELEMENT_NODE){
                            Element element = (Element) nNode;

                            // roomTypeID 기준 value 값 담을 map
                            Map<String, Object> valMap = new HashMap<String, Object>();
                            valMap.put("RatePlanId", xmlUtility.getTagValue("RatePlanId", element));
                            valMap.put("RatePlanName", xmlUtility.getTagValue("RatePlanName", element));
                            valMap.put("BedTypeCode", xmlUtility.getTagValue("BedTypeCode", element));
                            valMap.put("MealCode", xmlUtility.getTagValue("MealCode", element));
                            valMap.put("SaleRate", xmlUtility.getTagValue("SaleRate", element));
                            valMap.put("MinPersons", xmlUtility.getTagValue("MinPersons", element));
                            valMap.put("MaxPersons", xmlUtility.getTagValue("MaxPersons", element));

                            // roomTypeID dp valMap 값 담는 map
                            ratePlanListMap.put(xmlUtility.getTagValue("RoomTypeId", element), valMap); // roomTypeId 기준 ㅇㅇ

                        }
                    }
                }
            }

            // 코드 실행 시간
            if(j == (sAIList.getLength()-1)){
                System.out.println("roomType, ratePlan ApI 호출 완료 시간 = " + (System.currentTimeMillis()-ListAPIStart)/1000.0);
            }
        }


        // 데이터 담을 변수
        StringBuilder sb = new StringBuilder();
        String testSb = "";

        // Property 반복 돌리기
        NodeList propertyList = document.getElementsByTagName("Property");

        long listParsingStart = System.currentTimeMillis();
        for(int i=0; i<propertyList.getLength(); i++){

            Node propertyNode = propertyList.item(i);
            if(propertyNode.getNodeType() == Node.ELEMENT_NODE){

                // 노드를 사용할 수 있게 element로 변환
                Element propertyElement = (Element) propertyNode;



                /**
                 * Image 구하기
                 */
//                sb.append("</br>");
//                sb.append(apiHotelstoryService.parsing(propertyElement,"Image",new String[]{"ImageUrl"}, new StringBuilder("<img width=\"150px;\" src=\""), new StringBuilder("\" >"), roomTypeListMap, ratePlanListMap));

                //testSb += apiHotelstoryService.hotelStoryParsing(propertyElement,"Image",new String[]{"ImageUrl"}, roomTypeListMap, ratePlanListMap);

                // condo 정보
                testSb += apiHotelstoryService.hotelStoryParsing(propertyElement,"property",new String[]{}, roomTypeListMap, ratePlanListMap);

                /**
                 * Description 구하기
                 */
//                sb.append("</br><textarea style=\"width=900px; height:700px;\">");
//                sb.append(apiHotelstoryService.parsing(propertyElement,"Description",new String[]{"RoomTypeId","RatePlanId","Text"}, new StringBuilder(""), new StringBuilder("\n"), roomTypeListMap, ratePlanListMap));
//                sb.append("</textarea><br><br><br><br>");

                testSb += apiHotelstoryService.hotelStoryParsing(propertyElement,"Description",new String[]{"RoomTypeId", "Text"}, roomTypeListMap, ratePlanListMap);

            }

            if(i == (propertyList.getLength()-1)){
                System.out.println("roomType, ratePlan ApI 파싱 완료 시간 = " + (System.currentTimeMillis()-listParsingStart)/1000.0);
            }

        }

        //System.out.println(testSb);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/html; charset=UTF-8");
        return new ResponseEntity<String>(testSb, headers, HttpStatus.OK);
    }


}
