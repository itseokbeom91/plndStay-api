package com.example.stay.accommodation.hotelPass.service;

import com.example.stay.accommodation.hotelPass.mapper.BookingMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import org.apache.jasper.tagplugins.jstl.core.Url;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("hotelPass.BookingService")
public class BookingService {
    @Autowired
    BookingMapper bookingMapper;

    CommonFunction commonFunction = new CommonFunction();

    public String getCityRate(String requestCode, String sDate, String eDate, String roomCnt, String adultCnt, String childCnt){

        List<Map<String, Object>> hotelRateList = new ArrayList<Map<String, Object>>();
        try {
            //보낼 메시지
            String sendMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
//                    "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">"+
//                    "<soap12:Body>"+
//                    "<gfnGetCityRateList xmlns=\"http://www.hotelpass.com/HPLINK\">"+
//                    "<astrRequestXML>"+
                    "<HotelRequest>" +


                    "<UserInfo>" +
                    "<CompanyCode>" + Constants.hpComCode + "</CompanyCode>" +
                    "<UserID>" + Constants.hpID + "</UserID>" +
                    "<UserPWD>" + Constants.hpPW + "</UserPWD>" +
                    "</UserInfo>" +

                    "<HotelRequestInfo RequestKind=\"C\">" +
                    "<RequestCode>"+requestCode+"</RequestCode>" +
                    "<InDate>" + sDate + "</InDate>" +
                    "<OutDate>" + eDate + "</OutDate>" +
                    "<Rooms>" +
                    "<Room Seq='1'>" +
                    "<Adult AdultCnt=\"" + adultCnt + "\">" + "</Adult>" +
                    "<Adult ChildCnt=\"" + childCnt + "\" ChildAge=\"\">" + "</Adult>" +
                    "</Room>" +
                    "</Rooms>" +
//                    "<SGLCnt/>" +
//                    "<DBLCnt>" + "1" + "</DBLCnt>" +
//                    "<TWNCnt/>" +
//                    "<TRPCnt/>" +
                    "<IncludeHotel LangKind = \"kor\"/>" +
                    "</HotelRequestInfo>" +
                    "</HotelRequest>";
//                    "</astrRequestXML>";
//                    "</gfnGetCityRateList>"+
//                    "</soap12:Body>"+
//                    "</soap12:Envelope>";



            URL url = new URL("http://xml.hotelpass.com/HPLINK/V01/HPLINK.asmx/gfnGetCityRateList");

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
//                System.out.println(inputLine);
                returnStr += inputLine;
            }


            //xml 파싱하기
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(returnStr);
            InputSource is = new InputSource(sr);
//            is.setCharacterStream(new StringReader(returnStr));
            Document dc = db.parse(is);
            System.out.println(dc.getFirstChild().getTextContent());
            sr = new StringReader(dc.getFirstChild().getTextContent());
            is = new InputSource(sr);
            dc = db.parse(is);
            if (dc.getChildNodes().item(0).hasAttributes()){
                //조회 ERROR 시 여기
                if("No Data Found".equals(dc.getElementsByTagName("ErrorDesc").item(0).getChildNodes().item(0).getTextContent())){
                    //조회된 DATA 없음
                    System.out.println("예약 가능한 객실이 없습니다.");
                }
                System.out.println("성공 히히");
            }else {
                NodeList rateList = dc.getElementsByTagName("RateInformation");
                System.out.println(rateList.getLength());
            for (int i = 0; i < rateList.getLength(); i++) {
                Map<String, Object> cityRateMap = new HashMap<String, Object>();
                rateList.item(0).getChildNodes().item(1).getChildNodes().item(1).getTextContent();
                cityRateMap.put("hotelCd", dc.getElementsByTagName("HotelRate").item(i).getChildNodes().item(0).getTextContent());
                cityRateMap.put("hotelNm", dc.getElementsByTagName("HotelRate").item(i).getChildNodes().item(1).getTextContent());
                cityRateMap.put("rmTypeCd", rateList.item(i).getChildNodes().item(1).getAttributes().item(0).getTextContent());
                cityRateMap.put("rmName", rateList.item(i).getChildNodes().item(1).getChildNodes().item(1).getTextContent());
                cityRateMap.put("amount", rateList.item(i).getChildNodes().item(1).getChildNodes().item(3).getChildNodes().item(9).getAttributes().item(2));
                cityRateMap.put("available", rateList.item(i).getChildNodes().item(1).getChildNodes().item(3).getChildNodes().item(11).getTextContent());
//                cityRateMap.put("promotion", rateList.item(i).getChildNodes().item(1).getChildNodes().item(3).getChildNodes().item(13).getTextContent());
                hotelRateList.add(cityRateMap);
            }

            }


            in.close();
            wr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commonFunction.makeReturn("jsonp", "200", "OK", hotelRateList);

    }

    public String getMultiRate(String requestCode, String sDate, String eDate, String roomCnt, String adultCnt, String childCnt){

        List<Map<String, Object>> hotelRateList = new ArrayList<Map<String, Object>>();
        try {
            //보낼 메시지
            String sendMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
//                    "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">"+
//                    "<soap12:Body>"+
//                    "<gfnGetCityRateList xmlns=\"http://www.hotelpass.com/HPLINK\">"+
//                    "<astrRequestXML>"+
                    "<HotelRequest>" +


                    "<UserInfo>" +
                    "<CompanyCode>" + Constants.hpComCode + "</CompanyCode>" +
                    "<UserID>" + Constants.hpID + "</UserID>" +
                    "<UserPWD>" + Constants.hpPW + "</UserPWD>" +
                    "</UserInfo>" +

                    "<HotelRequestInfo RequestKind=\"M\">" +
                    "<RequestCode>"+requestCode+"</RequestCode>" +
                    "<InDate>" + sDate + "</InDate>" +
                    "<OutDate>" + eDate + "</OutDate>" +
                    "<Rooms>" +
                    "<Room Seq='1'>" +
                    "<Adult AdultCnt=\"" + adultCnt + "\">" + "</Adult>" +
                    "<Adult ChildCnt=\"" + childCnt + "\" ChildAge=\"\">" + "</Adult>" +
                    "</Room>" +
                    "</Rooms>" +
//                    "<SGLCnt/>" +
//                    "<DBLCnt>" + "1" + "</DBLCnt>" +
//                    "<TWNCnt/>" +
//                    "<TRPCnt/>" +
                    "<IncludeHotel LangKind = \"kor\"/>" +
                    "</HotelRequestInfo>" +
                    "</HotelRequest>";
//                    "</astrRequestXML>";
//                    "</gfnGetCityRateList>"+
//                    "</soap12:Body>"+
//                    "</soap12:Envelope>";



            URL url = new URL("http://xml.hotelpass.com/HPLINK/V01/HPLINK.asmx/gfnGetMultiHotelRateList");

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
//                System.out.println(inputLine);
                returnStr += inputLine;
            }


            //xml 파싱하기
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(returnStr);
            InputSource is = new InputSource(sr);
//            is.setCharacterStream(new StringReader(returnStr));
            Document dc = db.parse(is);
            System.out.println(dc.getFirstChild().getTextContent());
            sr = new StringReader(dc.getFirstChild().getTextContent());
            is = new InputSource(sr);
            dc = db.parse(is);
            if (dc.getChildNodes().item(0).hasAttributes()){
                //조회 ERROR 시 여기
                if("No Data Found".equals(dc.getElementsByTagName("ErrorDesc").item(0).getChildNodes().item(0).getTextContent())){
                    //조회된 DATA 없음
                    System.out.println("예약 가능한 객실이 없습니다.");
                }
                System.out.println("성공 히히");
            }else {
                NodeList rateList = dc.getElementsByTagName("RoomType");
                System.out.println(rateList.getLength());
            for (int i = 0; i < rateList.getLength(); i++) {
                Map<String, Object> hotelRateMap = new HashMap<String, Object>();
                hotelRateMap.put("rmTypeCd", rateList.item(i).getAttributes().item(0).getTextContent());
                hotelRateMap.put("rmName", rateList.item(i).getChildNodes().item(1).getTextContent());
                hotelRateMap.put("serviceCode", rateList.item(i).getChildNodes().item(3).getChildNodes().item(3).getTextContent());
                hotelRateMap.put("condition", rateList.item(i).getChildNodes().item(3).getChildNodes().item(5).getTextContent());
                hotelRateMap.put("cxlDate", rateList.item(i).getChildNodes().item(3).getChildNodes().item(7).getTextContent());
                hotelRateMap.put("amount", rateList.item(i).getChildNodes().item(3).getChildNodes().item(9).getTextContent());
                hotelRateMap.put("available", rateList.item(i).getChildNodes().item(3).getChildNodes().item(11).getTextContent());
                hotelRateMap.put("promotion", rateList.item(i).getChildNodes().item(3).getChildNodes().item(13).getTextContent());
                hotelRateList.add(hotelRateMap);
            }

            }

            in.close();
            wr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commonFunction.makeReturn("jsonp", "200", "OK", hotelRateList);

    }
}
