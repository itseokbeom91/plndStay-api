package com.example.stay.accommodation.hotelPass.service;

import com.example.stay.accommodation.hotelPass.mapper.BookingMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.XmlUtility;
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
                    return commonFunction.makeReturn("jsonp", "200", "OK", "예약 가능한 객실이 없습니다.");
                }
                
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
                cityRateMap.put("amount", rateList.item(i).getChildNodes().item(1).getChildNodes().item(3).getChildNodes().item(9).getAttributes().item(2).getTextContent());
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


    public String getServiceRate(String requestCode, String sDate, String eDate, String roomCnt, String adultCnt, String childCnt){

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

                    "<HotelRequestInfo RequestKind=\"S\">" +
                    "<RequestCode>" + requestCode + "</RequestCode>" +
                    "<InDate>" + sDate + "</InDate>" +
                    "<OutDate>" + eDate + "</OutDate>" +
                    "<Rooms>" +
                    "<Room Seq='1'>" +
                    "<Adult AdultCnt=\"" + adultCnt + "\">" + "</Adult>" +
                    "<Adult ChildCnt=\"" + childCnt + "\" ChildAge=\"\">" + "</Adult>" +
                    "</Room>" +
                    "</Rooms>" +
                    "<IncludeHotel LangKind = \"kor\"/>" +
                    "</HotelRequestInfo>" +
                    "</HotelRequest>";
//                    "</astrRequestXML>";
//                    "</gfnGetCityRateList>"+
//                    "</soap12:Body>"+
//                    "</soap12:Envelope>";



            URL url = new URL("http://xml.hotelpass.com/HPLINK/V01/HPLINK.asmx/gfnGetServiceCodeRateList");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            // Header 영역에 쓰기
            conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=\"utf-8\"");
            // BODY 영역에 쓰기
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write("astrRequestXML=" + sendMessage);
            wr.getEncoding();
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
            if ("ERROR".equals(dc.getChildNodes().item(0).getNodeName())){
                //조회 ERROR 시 여기
                
                return commonFunction.makeReturn("jsonp", "200", "OK", dc.getChildNodes().item(0).getTextContent());
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
                    cityRateMap.put("cancelAvailDate", rateList.item(0).getChildNodes().item(1).getChildNodes().item(3).getChildNodes().item(7).getTextContent());
                    cityRateMap.put("amount", rateList.item(i).getChildNodes().item(1).getChildNodes().item(3).getChildNodes().item(9).getAttributes().item(2).getTextContent());
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


    public String getCancelPolicy(String requestCode, String sDate, String eDate, String roomCnt, String adultCnt, String childCnt){

        List<Map<String, Object>> hotelRateList = new ArrayList<Map<String, Object>>();
        try {

            //보낼 메시지
            String sendMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
//                    "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">"+
//                    "<soap12:Body>"+
//                    "<gfnGetCityRateList xmlns=\"http://www.hotelpass.com/HPLINK\">"+
//                    "<astrRequestXML>"+
                    "<CancelPolicyRequest>" +


                    "<UserInfo>" +
                    "<CompanyCode>" + Constants.hpComCode + "</CompanyCode>" +
                    "<UserID>" + Constants.hpID + "</UserID>" +
                    "<UserPWD>" + Constants.hpPW + "</UserPWD>" +
                    "</UserInfo>" +

                    "<RequestInfo>" +
                    "<RequestCode>" + requestCode + "</RequestCode>" +
                    "<InDate>" + sDate + "</InDate>" +
                    "<OutDate>" + eDate + "</OutDate>" +
                    "<Rooms>" +
                    "<Room Seq='1'>" +
                    "<Adult AdultCnt=\"" + adultCnt + "\">" + "</Adult>" +
                    "<Adult ChildCnt=\"" + childCnt + "\" ChildAge=\"\">" + "</Adult>" +
                    "</Room>" +
                    "</Rooms>" +
                    "</RequestInfo>" +
                    "</CancelPolicyRequest>";
//                    "</astrRequestXML>";
//                    "</gfnGetCityRateList>"+
//                    "</soap12:Body>"+
//                    "</soap12:Envelope>";



            URL url = new URL("http://xml.hotelpass.com/HPLINK/V01/HPLINK.asmx/gfnGetCancelPolicy");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            // Header 영역에 쓰기
            conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=\"utf-8\"");
            // BODY 영역에 쓰기
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write("astrRequestXML=" + sendMessage);
            wr.getEncoding();
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
            if ("ERROR".equals(dc.getChildNodes().item(0).getNodeName())){
                //조회 ERROR 시 여기
                
                in.close();
                wr.close();
                return commonFunction.makeReturn("jsonp", "200", "OK", dc.getChildNodes().item(0).getTextContent());
            }else {
                NodeList cancelPolicy = dc.getElementsByTagName("CancelPolicy");
                System.out.println(cancelPolicy.getLength());
                System.out.println(cancelPolicy.item(0).getTextContent());
                Map<String, Object> cancelMap = new HashMap<>();
                cancelMap.put("CancelPolicy", cancelPolicy.item(0).getTextContent());
                in.close();
                wr.close();
                return commonFunction.makeReturn("jsonp", "200", "OK", cancelMap);

            }


        } catch (Exception e) {
            e.printStackTrace();
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }

    }

    public String getBookingList(String agentID, String sDate, String eDate){

        List<Map<String, Object>> hotelRateList = new ArrayList<Map<String, Object>>();
        try {

            //보낼 메시지
            String sendMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
//                    "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">"+
//                    "<soap12:Body>"+
//                    "<gfnGetCityRateList xmlns=\"http://www.hotelpass.com/HPLINK\">"+
//                    "<astrRequestXML>"+
                    "<BookingListRequest>" +


                    "<UserInfo>" +
                    "<CompanyCode>" + Constants.hpComCode + "</CompanyCode>" +
                    "<UserID>" + Constants.hpID + "</UserID>" +
                    "<UserPWD>" + Constants.hpPW + "</UserPWD>" +
                    "</UserInfo>" +

                    "<RequestInformation>" +
                    "<AgentUserID>" + agentID + "</AgentUserID>" +
                    "<StartDate>" + sDate + "</StartDate>" +
                    "<EndDate>" + eDate + "</EndDate>" +
                    "</RequestInformation>" +
                    "</BookingListRequest>";
//                    "</astrRequestXML>";
//                    "</gfnGetCityRateList>"+
//                    "</soap12:Body>"+
//                    "</soap12:Envelope>";



            URL url = new URL("http://xml.hotelpass.com/HPLINK/V01/HPLINK.asmx/gfnGetBookingList");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            // Header 영역에 쓰기
            conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=\"utf-8\"");
            // BODY 영역에 쓰기
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write("astrRequestXML=" + sendMessage);
            wr.getEncoding();
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
            if ("ERROR".equals(dc.getChildNodes().item(0).getNodeName())){
                //조회 ERROR 시 여기
                
                in.close();
                wr.close();
                return commonFunction.makeReturn("jsonp", "200", "OK", dc.getChildNodes().item(0).getTextContent());
            }else {
                NodeList cancelPolicy = dc.getElementsByTagName("CancelPolicy");
                System.out.println(cancelPolicy.getLength());
                System.out.println(cancelPolicy.item(0).getTextContent());
                Map<String, Object> cancelMap = new HashMap<>();
                cancelMap.put("CancelPolicy", cancelPolicy.item(0).getTextContent());
                in.close();
                wr.close();
                return commonFunction.makeReturn("jsonp", "200", "OK", cancelMap);

            }


        } catch (Exception e) {
            e.printStackTrace();
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }

    }

    public String createBooking(String requestCode, String sDate, String eDate, String roomCnt, String adultCnt, String childCnt){

        List<Map<String, Object>> hotelRateList = new ArrayList<Map<String, Object>>();
        try {
            //보낼 메시지
            String sendMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<BookingRequest>" +
                    "<UserInfo>" +
                    "<CompanyCode>" + Constants.hpComCode + "</CompanyCode>" +
                    "<UserID>" + Constants.hpID + "</UserID>" +
                    "<UserPWD>" + Constants.hpPW + "</UserPWD>" +
                    "</UserInfo>" +

                    "<RequestInformation Agent_RefNo=\"TEST-BOOKING\" >" +
                    "<BaseInfo ReserveTel=\"\" ReserveMobile=\"\" ReserveEmail=\"\">" +
                    "<ServiceCode>" + requestCode + "</ServiceCode>" +
                    "<InDate>" + sDate + "</InDate>" +
                    "<OutDate>" + eDate + "</OutDate>" +
//                    "<RoomCnt DBLCnt=\"1\"/>" +
//                    "<PassenCnt AdultCnt=\"2\"/>" +
                    "<Rooms>" +
                    "<Room Seq=\"1\">" +
                    "<Adult AdultCnt=\"" + adultCnt + "\"/>" +
                    "<Child ChildCnt=\"" + childCnt + "\" ChildAge=\"\"/>" +
                    "</Room>" +
                    "</Rooms>" +
                    "</BaseInfo>" +
                    "<RoomGroupInformation>" +
                    "<Room Seq=\"1\">" +
                    "<PassengerName Title=\"MR\" Kind=\"ADULT\" FirstName=\"test\" LastName=\"Hong\" /> " +
                    "<PassengerName Title=\"MR\" Kind=\"ADULT\" FirstName=\"test\" LastName=\"Hong\" /> " +
                    "</Room>" +
                    "</RoomGroupInformation>" +
                    "<Remark/>" +
                    "<PaymentInformation PaymentYN=\"N\">" +
                    "<AmountInfo Exchange=\"1.0000\" TotalWon=\"85000\"/>" +
                    "</PaymentInformation>" +
                    "</RequestInformation>" +
                    "</BookingRequest>";



            URL url = new URL("http://xml.hotelpass.com/HPLINK/V01/HPLINK.asmx/gfnHotelBooking");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            // Header 영역에 쓰기
            conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=\"utf-8\"");
            // BODY 영역에 쓰기
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write("astrRequestXML=" + sendMessage);
            wr.getEncoding();
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
            if ("ERROR".equals(dc.getChildNodes().item(0).getNodeName())){
                //조회 ERROR 시 여기
                
                in.close();
                wr.close();
                return commonFunction.makeReturn("jsonp", "200", "OK", dc.getChildNodes().item(0).getTextContent());
            }else {
                NodeList cancelPolicy = dc.getElementsByTagName("CancelPolicy");
                System.out.println(cancelPolicy.getLength());
                System.out.println(cancelPolicy.item(0).getTextContent());
                Map<String, Object> cancelMap = new HashMap<>();
                cancelMap.put("CancelPolicy", cancelPolicy.item(0).getTextContent());
                in.close();
                wr.close();
                return commonFunction.makeReturn("jsonp", "200", "OK", cancelMap);

            }


        } catch (Exception e) {
            e.printStackTrace();
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }

    }
    public String getBookingDetail(String bookingNo){

        List<Map<String, Object>> hotelRateList = new ArrayList<Map<String, Object>>();
        try {

            //보낼 메시지
            String sendMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
//                    "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">"+
//                    "<soap12:Body>"+
//                    "<gfnGetCityRateList xmlns=\"http://www.hotelpass.com/HPLINK\">"+
//                    "<astrRequestXML>"+
                    "<BookingSearchRequest>" +


                    "<UserInfo>" +
                    "<CompanyCode>" + Constants.hpComCode + "</CompanyCode>" +
                    "<UserID>" + Constants.hpID + "</UserID>" +
                    "<UserPWD>" + Constants.hpPW + "</UserPWD>" +
                    "</UserInfo>" +

                    "<BookingInfo>" +
                    "<BKNo>" + bookingNo + "</BKNo>" +
                    "</BookingInfo>" +
                    "</BookingSearchRequest>";
//                    "</astrRequestXML>";
//                    "</gfnGetCityRateList>"+
//                    "</soap12:Body>"+
//                    "</soap12:Envelope>";



            URL url = new URL("http://xml.hotelpass.com/HPLINK/V01/HPLINK.asmx/gfnGetBookingDetailSearch");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            // Header 영역에 쓰기
            conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=\"utf-8\"");
            // BODY 영역에 쓰기
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write("astrRequestXML=" + sendMessage);
            wr.getEncoding();
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
            if ("ERROR".equals(dc.getChildNodes().item(0).getNodeName())){
                //조회 ERROR 시 여기
                
                in.close();
                wr.close();
                return commonFunction.makeReturn("jsonp", "200", "OK", dc.getChildNodes().item(0).getTextContent());
            }else {
                NodeList bookingDetail = dc.getElementsByTagName("Booking");
                System.out.println(bookingDetail.getLength());
                System.out.println(bookingDetail.item(0).getTextContent());
                Map<String, Object> bookingMap = new HashMap<>();
                bookingMap.put("BookingNo", bookingDetail.item(0).getAttributes().item(0).getTextContent());
                bookingMap.put("Operator", bookingDetail.item(0).getChildNodes().item(1).getTextContent());
                bookingMap.put("City", bookingDetail.item(0).getChildNodes().item(3).getTextContent());
                bookingMap.put("Hotel", bookingDetail.item(0).getChildNodes().item(5).getTextContent());
                bookingMap.put("ServiceCode", bookingDetail.item(0).getChildNodes().item(7).getTextContent());
                bookingMap.put("ConfirmNo", bookingDetail.item(0).getChildNodes().item(9).getTextContent());
                bookingMap.put("Condition", bookingDetail.item(0).getChildNodes().item(11).getTextContent());
                bookingMap.put("CancelDeadLine", bookingDetail.item(0).getChildNodes().item(13).getTextContent());
                bookingMap.put("CheckIn", bookingDetail.item(0).getChildNodes().item(15).getTextContent());
                bookingMap.put("CheckOut", bookingDetail.item(0).getChildNodes().item(17).getTextContent());
                bookingMap.put("Nights", bookingDetail.item(0).getChildNodes().item(19).getTextContent());
                bookingMap.put("RoomType", bookingDetail.item(0).getChildNodes().item(21).getTextContent());
                bookingMap.put("MealType", bookingDetail.item(0).getChildNodes().item(23).getTextContent());
                bookingMap.put("BookingStatus", bookingDetail.item(0).getChildNodes().item(25).getTextContent());
                bookingMap.put("PaymentYN", bookingDetail.item(0).getChildNodes().item(27).getTextContent());
                bookingMap.put("Remark", bookingDetail.item(0).getChildNodes().item(29).getTextContent());
                bookingMap.put("TotalAmt", bookingDetail.item(0).getChildNodes().item(31).getTextContent());
                bookingMap.put("Passenger", bookingDetail.item(0).getChildNodes().item(33).getTextContent());
                bookingMap.put("Rooms", bookingDetail.item(0).getChildNodes().item(35).getTextContent());
                bookingMap.put("VoucherURL", bookingDetail.item(0).getChildNodes().item(37).getTextContent());
                in.close();
                wr.close();
                return commonFunction.makeReturn("jsonp", "200", "OK", bookingMap);

            }


        } catch (Exception e) {
            e.printStackTrace();
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }

    }
    public String cancelBooking(String bookingNo){

        List<Map<String, Object>> hotelRateList = new ArrayList<Map<String, Object>>();
        try {

            //보낼 메시지
            String sendMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
//                    "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">"+
//                    "<soap12:Body>"+
//                    "<gfnGetCityRateList xmlns=\"http://www.hotelpass.com/HPLINK\">"+
//                    "<astrRequestXML>"+
                    "<CancelRequest>" +


                    "<UserInfo>" +
                    "<CompanyCode>" + Constants.hpComCode + "</CompanyCode>" +
                    "<UserID>" + Constants.hpID + "</UserID>" +
                    "<UserPWD>" + Constants.hpPW + "</UserPWD>" +
                    "</UserInfo>" +

                    "<BookingInfo>" +
                    "<CancelKind>01</CancelKind>" + //처리종류 01 : 취소처리 (고정값)
                    "<BKNo>" + bookingNo + "</BKNo>" +
                    "</BookingInfo>" +
                    "</CancelRequest>";
//                    "</astrRequestXML>";
//                    "</gfnGetCityRateList>"+
//                    "</soap12:Body>"+
//                    "</soap12:Envelope>";



            URL url = new URL("http://xml.hotelpass.com/HPLINK/V01/HPLINK.asmx/gfnBookingCancel");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            // Header 영역에 쓰기
            conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=\"utf-8\"");
            // BODY 영역에 쓰기
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write("astrRequestXML=" + sendMessage);
            wr.getEncoding();
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
            if ("ERROR".equals(dc.getChildNodes().item(0).getNodeName())){
                //조회 ERROR 시 여기
                in.close();
                wr.close();
                return commonFunction.makeReturn("jsonp", "200", "OK", dc.getChildNodes().item(0).getTextContent());
            }else {
                NodeList bookingDetail = dc.getElementsByTagName("BookingCancel");
                if("04".equals(bookingDetail.item(0).getChildNodes().item(1).getTextContent())){
                    //취소완료
                } else {

                }
                System.out.println(bookingDetail.getLength());
                System.out.println(bookingDetail.item(0).getTextContent());
                Map<String, Object> bookingMap = new HashMap<>();
                bookingMap.put("BookingStatus", bookingDetail.item(0).getChildNodes().item(1).getTextContent());
                in.close();
                wr.close();
                return commonFunction.makeReturn("jsonp", "200", "OK", bookingMap);

            }


        } catch (Exception e) {
            e.printStackTrace();
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }

    }
}
