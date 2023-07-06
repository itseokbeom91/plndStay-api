package com.example.stay.accommodation.hotelPass.service;

import com.example.stay.accommodation.hotelPass.mapper.AccommMapper;
import com.example.stay.common.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service("hotelPass.AccommService")
public class AccommService {
    @Autowired
    private AccommMapper accommMapper;

    public String getPensionList(String sendUrl) {

        try {
            //보낼 메시지
            String sendMessage =
                    "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">"+
                    "<soap12:Body>"+
                    "<gfnGetCityRateListResponse xmlns=\"http://www.hotelpass.com/HPLINK\">"+
                            "<gfnGetCityRateListResult>"+
                    "<HotelRequest xmlns:xsi=\" http:www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://xml.hotelpass.com/HTPWS_V01/SCHEMA/Request/HotelRateSearch_Request.xsd\">" +
                    "<UserInfo>" +
                    "< CompanyCode > 2 - 00226 </CompanyCode >" +
                    "<UserID > angmatest </UserID >" +
                    "<UserPWD > angmatest </UserPWD >" +
                    "</UserInfo >" +
                            "<HotelRequestInfo RequestKind = \"C\" >" +
                    "<RequestCode > KRCHA </RequestCode >" +
                    "<InDate > 99999999 </InDate >" +
                    "<OutDate > 99999999 </OutDate >" +
                    "<SGLCnt / >" +
                    "<DBLCnt / >" +
                    "<TWNCnt / >" +
                    "<TRPCnt / >" +
                    "<IncludeHotel LangKind = \"\" / >"+
                    "</HotelRequestInfo >" +
                    "</HotelRequest >" +
                    "</gfnGetCityRateListResult>"+
                    "</gfnGetCityRateListResponse>"+
                    "</soap12:Body>"+
                    "</soap12:Envelope>";

            CommonService commonService = new CommonService();

            commonService.callSoapApi(sendUrl, "gfnGetCityRateList", sendMessage);

//                    URL url = new URL(sendUrl);

//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setDoOutput(true);
//            conn.setRequestMethod("POST");
//            // Header 영역에 쓰기
//            conn.addRequestProperty("Content-Type", "application/soap+xml");
//            // BODY 영역에 쓰기
//            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//            wr.write(sendMessage);
//            wr.flush();
//
//            // 리턴된 결과 읽기
//            String inputLine = null;
//            String returnStr = "";
//            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            while ((inputLine = in.readLine()) != null) {
//                System.out.println(inputLine);
//                returnStr += inputLine;
//            }
//
//            //xml 파싱하기
//            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//            DocumentBuilder db = dbf.newDocumentBuilder();
//            InputSource is = new InputSource();
//            is.setCharacterStream(new StringReader(returnStr));
//            Document dc = db.parse(is);
//            NodeList nl = dc.getElementsByTagName("DATA");
//            Element e = null;
//            for (int i = 0; i < nl.getLength(); i++) {
//                e = (Element) nl.item(i);
//                System.out.println(e.getAttribute("value"));
//            }
//
//            in.close();
//            wr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return sendUrl;
    }
}
