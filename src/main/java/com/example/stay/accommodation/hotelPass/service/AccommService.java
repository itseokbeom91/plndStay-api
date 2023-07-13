package com.example.stay.accommodation.hotelPass.service;

import com.example.stay.accommodation.hotelPass.mapper.AccommMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.common.util.XmlUtility;
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
                    String latitude = hotelList.item(i).getChildNodes().item(7).getTextContent();
                    String longitude = hotelList.item(i).getChildNodes().item(8).getTextContent();
                    address = commonFunction.getJusoByGeoCd(latitude, longitude);
                    if(address==null){//좌표조회로 주소조회가 안될시 기존 영문주소 입력
                        address = hotelList.item(i).getChildNodes().item(9).getTextContent();
                    }
                    hotelMap.put("nationCode", hotelList.item(i).getChildNodes().item(0).getTextContent());
                    hotelMap.put("cityCode", hotelList.item(i).getChildNodes().item(2).getTextContent());
                    hotelMap.put("cityName", hotelList.item(i).getChildNodes().item(3).getTextContent());
                    hotelMap.put("hotelName", hotelList.item(i).getChildNodes().item(6).getTextContent());
                    hotelMap.put("latitude", hotelList.item(i).getChildNodes().item(7).getTextContent());
                    hotelMap.put("longitude", hotelList.item(i).getChildNodes().item(8).getTextContent());
                    hotelMap.put("address", address);
                    hotelMap.put("tel", hotelList.item(i).getChildNodes().item(11).getTextContent());
                    hotelMap.put("fax", hotelList.item(i).getChildNodes().item(12).getTextContent());
                    hotelMap.put("grade", hotelList.item(i).getChildNodes().item(13).getTextContent());
                    hotelMap.put("roomCnt", hotelList.item(i).getChildNodes().item(14).getTextContent());
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
            System.out.println(hotelListMap);
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
            for (Map<String, Object> map : hotelListMap) {
                //TO-DO 시설정보 INSERT 쿼리 작성 후 로직 작성
                String grade = map.get("grade").toString();
                int intGrade;
                if(grade.lastIndexOf("★") != grade.length()) {
                    intGrade = (int) (grade.length()-0.5);
                } else {
                    intGrade = grade.length();
                }
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

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

            for (int i = 0; i < hotelList.getLength(); i++) {
                System.out.println(hotelList.item(i).getAttributes().item(0).getNodeValue());
                NodeList FacilityList = hotelList.item(i).getChildNodes();


                for (int j = 0; j < FacilityList.getLength(); j++) {
                    facility.put(FacilityList.item(j).getAttributes().item(0).getNodeValue(), FacilityList.item(j).getTextContent());
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


        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CommonFunction().makeReturn("jsonp", "", "", hotelListMap);
    }

    public String getHotelRate(String sendUrl) {

        try {
            //보낼 메시지
            String sendMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
//                    "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">"+
//                    "<soap12:Body>"+
//                    "<gfnGetCityRateList xmlns=\"http://www.hotelpass.com/HPLINK\">"+
//                    "<astrRequestXML>"+
                    "<HotelRequest>" +


                    "<UserInfo>" +
                    "<CompanyCode>2-00226</CompanyCode>" +
                    "<UserID>angmatest</UserID>" +
                    "<UserPWD>angmatest</UserPWD>" +
                    "</UserInfo>" +

                    "<HotelRequestInfo RequestKind=\"C\">" +
                    "<RequestCode>K</RequestCode>" +
                    "<InDate>20230810</InDate>" +
                    "<OutDate>20230811</OutDate>" +
                    "<SGLCnt/>" +
                    "<DBLCnt/>" +
                    "<TWNCnt/>" +
                    "<TRPCnt/>" +
                    "<IncludeHotel LangKind = \"\"/>" +
                    "</HotelRequestInfo>" +
                    "</HotelRequest>";
//                    "</astrRequestXML>";
//                    "</gfnGetCityRateList>"+
//                    "</soap12:Body>"+
//                    "</soap12:Envelope>";



                    URL url = new URL(sendUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            // Header 영역에 쓰기
            conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=\"utf-8\"");
            // BODY 영역에 쓰기
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write("astrRequestXML=" + sendMessage);
            wr.flush();
            System.out.println("Sent message: " + sendMessage);
            System.out.println(conn.getResponseCode());


            // 리턴된 결과 읽기
            String inputLine = null;
            String returnStr = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                returnStr += inputLine;
            }


            //xml 파싱하기
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(returnStr));
            Document dc = db.parse(is);
            NodeList nl = dc.getElementsByTagName("DATA");
            Element e = null;
            for (int i = 0; i < nl.getLength(); i++) {
                e = (Element) nl.item(i);
                System.out.println(e.getAttribute("value"));
            }
            in.close();
            wr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendUrl;
    }
}
