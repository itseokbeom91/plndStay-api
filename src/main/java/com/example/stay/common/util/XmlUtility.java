package com.example.stay.common.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class XmlUtility {

    /**
     * xml 태그값 가져오는 메서드
     * @param tag
     * @param eElement
     * @return 해당 node의 값
     */
    public static String getTagValue(String tag, Element eElement) {
        if((eElement.getElementsByTagName(tag)).getLength() == 0){
            return null;
        }else{
            NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
            Node nValue = (Node) nlList.item(0);
            return nValue.getNodeValue();
        }
    }

    /**
     *
     * @param document
     * @return xml 형식
     */
    public static String parsingXml(Document document){

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

}
