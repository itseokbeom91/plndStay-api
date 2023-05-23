package com.example.stay.common.util;

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
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class XmlUtility {

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
     * xml 태그값 출력
     * @param tag
     * @param element
     * @return 해당 node의 값
     */
    public String getTagValue(String tag, Element element) {
        if((element.getElementsByTagName(tag)).getLength() == 0){
            return null;
        }else{
            NodeList nlList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node nValue = (Node) nlList.item(0);
            return nValue.getNodeValue();
        }
    }

    /**
     * document로 되어있는 xml 구문을 string 형식으로 출력
     * @param document
     * @return xml 형식
     */
    public String parsingXml(Document document){

        String result = "";

        try {

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StringWriter sw = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(sw));
            result = sw.toString();

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public String sendRequest(String sendXmlData) {
        String ret = "";
        HttpURLConnection connection = null;
        try {
            URL url = new URL("");
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            connection.setRequestProperty("Content-Length", Integer.toString(sendXmlData.length()));
            connection.setDoOutput(true);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(sendXmlData);
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                InputStream inputStream = connection.getInputStream();
                StringBuilder response = new StringBuilder();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    response.append(new String(buffer, 0, bytesRead));
                }
                inputStream.close();
                ret = response.toString();
            } else {
                ret = "DisConnect|" + responseCode + "|" + connection.getResponseMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return ret;
    }

}
