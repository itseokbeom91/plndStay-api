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
