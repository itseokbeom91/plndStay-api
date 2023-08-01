package com.example.stay.openMarket.elevenST.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.openMarket.elevenST.mapper.ElevenStMapper;
import okhttp3.OkHttpClient;
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
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("elevenST.ElevenStService")
public class ElevenStService {

    @Autowired
    ElevenStMapper elevenStMapper;

    CommonFunction commonFunction = new CommonFunction();



    public String regProduct(String accommID) {
        try {
            URL url = new URL(Constants.elevenUrl + "/rest/prodservices/product");
            Map<String, Object>map = elevenStMapper.getAccomm(accommID);
            map.put("aplBgnDy", "20230727");
            map.put("aplEndDy", "20230728");
            map.put("selPrc", "100000000");
            map.put("prdImage01", "https://cdn.imweb.me/thumbnail/20221018/2fa9b7c3276c7.png");
            StringBuffer sb = new StringBuffer();
            sb.append("<Product>");
            sb.append("<selMnbdNckNm>condo24</selMnbdNckNm>"); //닉네임 필수항목은 아님
            sb.append("<selMthdCd>01</selMthdCd>"); //판매방식 01:고정가판매, 04:예약판매, 05:중고판매 이 외의 코드는 사용 X
            sb.append("<dispCtgrNo>1018070</dispCtgrNo>"); //카테고리 넘버는 소카테고리 넘버 (2878 => 1017895(국내숙박) => 1017902(호텔) => 지역 (ex: 서울 1018070)  //호텔, 리조트, 모텔, 펜션, 게스트하우스등 있음
//            sb.append("<prdNm>" + map.get("prdNm") + "</prdNm>");
            sb.append("<prdNm>" + map.get("strSubject") + "</prdNm>"); //상품명
            sb.append("<sellerPrdCd>" + map.get("intAID") + "</sellerPrdCd>");
            sb.append("<prdImage01>" + map.get("prdImage01") + "</prdImage01>");
//            sb.append("<prdImage02>" + map.get("prdImage02") + "</prdImage02>");
//            sb.append("<prdImage03>" + map.get("prdImage03") + "</prdImage03>");
            sb.append("<htmlDetail><![CDATA[" + map.get("strDescription") + "]]></htmlDetail>");
            sb.append("<selTermUseYn>Y</selTermUseYn>"); //판매기간 (N: 즉시 영구판매)
            sb.append("<brand>febHotel</brand>"); //브랜드명
            sb.append("<ProductNotification>"); //상품정보고시 호텔/펜션예약(891037)
            sb.append("<type>891037</type>");
            sb.append("<item>");
            sb.append("<code>23754785</code>");//객실타입/등급
            sb.append("<name>상품상세설명 참조</name>");
            sb.append("</item>");
            sb.append("<item>");
            sb.append("<code>23756934</code>");//숙소형태
            sb.append("<name>상품상세설명 참조</name>");
            sb.append("</item>");
            sb.append("<item>");
            sb.append("<code>23756141</code>");//사용가능인원
            sb.append("<name>상품상세설명 참조</name>");
            sb.append("</item>");
            sb.append("<item>");
            sb.append("<code>23756127</code>");//부대시설, 제공서비스
            sb.append("<name>TEST</name>");
            sb.append("</item>");
            sb.append("<item>");
            sb.append("<code>23759722</code>");//국가 또는 지역명
            sb.append("<name>상품상세설명 참조</name>");
            sb.append("</item>");
            sb.append("<item>");
            sb.append("<code>23757169</code>");//예약담당 연락처
            sb.append("<name>상품상세설명 참조</name>");
            sb.append("</item>");
            sb.append("<item>");
            sb.append("<code>23760070</code>");//취소 규정
            sb.append("<name>상품상세설명 참조</name>");
            sb.append("</item>");
            sb.append("</ProductNotification>");
            Map<String, Object> map2 = new HashMap<>();
            Map<String, Object> map3 = new HashMap<>();
            map3.put("optPrice", "100000000");
            map3.put("optCount", "1");
            map3.put("optType1", "20230901");
            map3.put("optType2", "넓은거");
            map2.put("listMap", map3);

            List<Map<String, Object>> listMap = (List<Map<String, Object>>) map2.get("listMap");

            //옵션 설정
            sb.append("<optSelectYn>Y</optSelectYn>");
            sb.append("<txtColCnt>1</txtColCnt>");
            sb.append("<optionAllQty>9999</optionAllQty>"); // 각 옵션별 재고 수량 전체 더해서 입력
            sb.append("<optionAllAddPrc>0</optionAllAddPrc>");
            sb.append("<prdExposeClfCd>01</prdExposeClfCd>");
            sb.append("<optMixYn>N</optMixYn>");
            //AS-IS 기준
            sb.append("<ProductOptionExt>");
            for(int i = 0; i < listMap.size(); i++){//관리자페이지에서 선택된값 리스트로 받아서 넣어야 함
                sb.append("<ProductOption>");
                sb.append("<colOptPrice>" + listMap.get(i).get("optPrice") + "</colOptPrice>");
                sb.append("<colOptCount>" + listMap.get(i).get("optCount") + "</colOptCount>");
                sb.append("<colCount/>");
                sb.append("<optWght/>");
                sb.append("<useYn>Y</useYn>");
                sb.append("<optionMappingKey><![CDATA[투숙일자:" + listMap.get(i).get("optType1") + "†" + "객실타입:" + listMap.get(i).get("optType2") + " ]]></optionMappingKey>");
                sb.append("</ProductOption>");
                sb.append("</ProductOptionExt>");
            }


            sb.append("<selPrdClfCd>0:100</selPrdClfCd>");//판매기간코드
            sb.append("<orgnTypCd>03</orgnTypCd>"); //원산지코드 03:기타, 01:국내, 02:해외 국내나 해외선택시 원산지지역코드 입력해야함
            sb.append("<orgnNmVal>TEST</orgnNmVal>"); //원산지 명
            sb.append("<asDetail>.</asDetail>"); //A/S 안내 필수항목으로 .이라도 입력하라 함
            sb.append("<prdSelQty>1</prdSelQty>");// 재고수량 필수항목
            sb.append("<rtngExchDetail>불가</rtngExchDetail>"); //반품/교환 안내 필수항목으로 .이라도 입력하라 함
            sb.append("<dlvCstInstBasiCd>01</dlvCstInstBasiCd>"); //H.S코드
            sb.append("<aplBgnDy>" + map.get("aplBgnDy") + "</aplBgnDy>"); //판매 시작일
            sb.append("<aplEndDy>" + map.get("aplEndDy") + "</aplEndDy>"); //판매 종료일
            sb.append("<selPrc>" + map.get("selPrc") + "</selPrc>"); //판매가 (원가)
            sb.append("<prcCmpExpYn>Y</prcCmpExpYn>"); // 가격비교
            sb.append("<dtldDescTyp>H</dtldDescTyp>"); // 모바일 노출타입
            sb.append("<prdTypCd>30</prdTypCd>"); // 30:숙박내재화
            sb.append("<drcStlYn>Y</drcStlYn>"); // 즉시결제여부
            sb.append("<htmlDetailIframeYn>N</htmlDetailIframeYn>"); // 숙박은 N : iframe으로 노출여부
            sb.append("<penaltyAppyYn>Y</penaltyAppyYn>"); // 취소수수료 사용여부
            sb.append("<directStlYn>Y</directStlYn>"); // 바로결제 여부
            sb.append("<hotelBaseAddr><![CDATA[" + map.get("strAddr1") + "]]></hotelBaseAddr>");
            sb.append("<hotelDtlsAddr><![CDATA[" + map.get("strAddr2") + "]]></hotelDtlsAddr>");
            sb.append("<addrTypCd>02</addrTypCd>"); //주소 타입 01:지번, 02:도로명
            sb.append("<mailNo>" + map.get("strZipCode") + "</mailNo>");
            sb.append("<hotelPhoneNumber>" + map.get("strPhone") + "</hotelPhoneNumber>");
            sb.append("<roomSeatCount>" + map.get("intRoomCnt") + "</roomSeatCount>");
            sb.append("<checkInTime>" + map.get("strCheckIn") + "</checkInTime>");
            sb.append("<checkOutTime>" + map.get("strCheckOut") + "</checkOutTime>");
            sb.append("<hotelType><![CDATA[" + map.get("strType") + "]]></hotelType>");
            sb.append("<hotelGrade>" + map.get("intGrade") + "</hotelGrade>");
            sb.append("<geoPointX>" + map.get("decLat") + "</geoPointX>");
            sb.append("<geoPointY>" + map.get("decLon") + "</geoPointY>");
            sb.append("<tourNationType>국내</tourNationType>");
            sb.append("</Product>");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Content-Type", "text/xml; charset=euc-kr");
            conn.setRequestProperty("openapikey", Constants.elevenApiKey);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(sb.toString());
            wr.flush();
            LogWriter lw = new LogWriter("POST", url.toString(), sb.toString(), System.currentTimeMillis());

            String inputLine = null;
            String returnStr = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "EUC-KR"));
            while ((inputLine = in.readLine()) != null) {
//                System.out.println(inputLine);
                String decoder = URLDecoder.decode(inputLine, "euc-kr");
                decoder = URLDecoder.decode(decoder, "euc-kr");
                returnStr += decoder;
            }
            System.out.println(returnStr);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(returnStr)));
            NodeList nl = doc.getElementsByTagName("ClientMessage");
            String prdNo = nl.item(1).getTextContent();
            //오픈마켓 테이블에 박아야겠지?

//            elevenStMapper.insertAccomm(map.get("intAID").toString(), map.get("prdNm").toString(), prdNo, map.get("detailInfo").toString());
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }
        return "";
    }
    public String getProductList(){
        try {
            URL url = new URL(Constants.elevenUrl + "/rest/prodmarketservice/prodmarket");
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"euc-kr\" standalone=\"yes\"?>");
            sb.append("<SearchProduct>");
            sb.append("<category1/>");
            sb.append("<category2/>");
            sb.append("<category3/>");
            sb.append("<category4/>");
            sb.append("<prdNo/>");
            sb.append("<prdNm/>");
            sb.append("<selStatCd/>");
            sb.append("<selMthdCd/>");
            sb.append("<schDateType/>");
            sb.append("<schBgnDt/>");
            sb.append("<schEndDt/>");
            sb.append("<limit/>");
            sb.append("<start/>");
            sb.append("<end/>");
            sb.append("</SearchProduct>");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Content-Type", "text/xml; charset=euc-kr");
            conn.setRequestProperty("openapikey", Constants.elevenApiKey);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(sb.toString());
            wr.flush();
            LogWriter lw = new LogWriter("POST", url.toString(), sb.toString(), System.currentTimeMillis());

            // 리턴된 결과 읽기
            String inputLine = null;
            String returnStr = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "EUC-KR"));
            while ((inputLine = in.readLine()) != null) {
//                System.out.println(inputLine);
                String decoder = URLDecoder.decode(inputLine, "euc-kr");
                decoder = URLDecoder.decode(decoder, "euc-kr");
                returnStr += decoder;
            }
            System.out.println(returnStr);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(returnStr);
            InputSource is = new InputSource(sr);

            Document dc = db.parse(is);
            NodeList nl = dc.getElementsByTagName("ns2:product");
            for (int i = 0 ; i< nl.getLength() ; i++){
                String prdNo = nl.item(i).getChildNodes().item(1).getTextContent();
                String prdNm = nl.item(i).getChildNodes().item(2).getTextContent();

            }
            return returnStr;
//            return commonFunction.makeReturn("jsonp", "200", "OK", returnStr);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /*
    전체 수정을 위한 수정함수
    각 항목별 따로 변경API는 따로 구현
    PUT
     */
    public String modProduct(String prdNo) {
        try {
            URL url = new URL(Constants.elevenUrl + "rest/prodservices/product/" + prdNo);
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"euc-kr\" standalone=\"yes\"?>");
            sb.append("<Product>");
            sb.append("<selMnbdNckNm>condo24</selMnbdnckNm>");
            sb.append("<selMthdCd>01</selMthdCd>");
            sb.append("<dispCtgrNo>1018070</dispCtgrNo>"); //카테고리 넘버는 소카테고리 넘버 (2878 => 1017895(국내숙박) => 1017902(호텔) => 지역 (ex: 서울 1018070)  //호텔, 리조트, 모텔, 펜션, 게스트하우스등 있음
            sb.append("<prdTypCd>30</prdTypCd>");
            sb.append("<prdNm>01</prdNm>");
            sb.append("<brand>01</brand>");
            sb.append("<htmlDetail>01</htmlDetail>");
            sb.append("<selPrc>01</selPrc>");
            sb.append("<asDetail>01</asDetail>");
            sb.append("<rtngExchDetail>01</rtngExchDetail>");
            sb.append("<ProductNotification>"); //상품정보고시 호텔/펜션예약(891037)
            sb.append("<type>891037</type>");
            sb.append("<item>");
            sb.append("<code>23754785</code>");//객실타입/등급
            sb.append("<name>상품상세설명 참조</name>");
            sb.append("</item>");
            sb.append("<item>");
            sb.append("<code>23756934</code>");//숙소형태
            sb.append("<name>상품상세설명 참조</name>");
            sb.append("</item>");
            sb.append("<item>");
            sb.append("<code>23756141</code>");//사용가능인원
            sb.append("<name>상품상세설명 참조</name>");
            sb.append("</item>");
            sb.append("<item>");
            sb.append("<code>23756127</code>");//부대시설, 제공서비스
            sb.append("<name>TEST</name>");
            sb.append("</item>");
            sb.append("<item>");
            sb.append("<code>23759722</code>");//국가 또는 지역명
            sb.append("<name>상품상세설명 참조</name>");
            sb.append("</item>");
            sb.append("<item>");
            sb.append("<code>23757169</code>");//예약담당 연락처
            sb.append("<name>상품상세설명 참조</name>");
            sb.append("</item>");
            sb.append("<item>");
            sb.append("<code>23760070</code>");//취소 규정
            sb.append("<name>상품상세설명 참조</name>");
            sb.append("</item>");
            sb.append("</ProductNotification>");
            sb.append("<htmlDetail>01</htmlDetail>");


        } catch (Exception e) {

        }
        return "";
    }

    public String setRejectOrder(String ordNo, String ordPrdSeq, String ordCnRsnCd, String ordCnDtlsRsn) {
        try {
            URL url = new URL(Constants.elevenUrl + "/rest/claimservice/refrejectorder/" + ordNo + "/" + ordPrdSeq + "/" + ordCnRsnCd + "/" + ordCnDtlsRsn);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("GET");
            conn.addRequestProperty("Content-Type", "text/xml; charset=euc-kr");
            conn.setRequestProperty("openapikey", Constants.elevenApiKey);

            LogWriter lw = new LogWriter("GET", url.toString(), System.currentTimeMillis());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "EUC-KR"));
            String inputLine = null;
            String returnStr = "";
            while ((inputLine = in.readLine()) != null) {
//                System.out.println(inputLine);
                String decoder = URLDecoder.decode(inputLine, "euc-kr");
                decoder = URLDecoder.decode(decoder, "euc-kr");
                returnStr += decoder;
            }
            System.out.println(returnStr);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(returnStr);
            InputSource is = new InputSource(sr);
            Document dc = db.parse(is);
            NodeList nl = dc.getElementsByTagName("ResultOrder");
            if(nl.item(0).getChildNodes().item(3).getTextContent().equals("0")){
                return commonFunction.makeReturn("jsonp", "200", nl.item(0).getChildNodes().item(3).getTextContent(), nl.item(0).getChildNodes().item(4).getTextContent());
            } else {
                return commonFunction.makeReturn("jsonp", "500", nl.item(0).getChildNodes().item(3).getTextContent(), nl.item(0).getChildNodes().item(4).getTextContent());
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }
    }
/*
판매중지처리 (PUT)
 */
    public String stopDisplay (String prdNo) {
        try {
            URL url = new URL(Constants.elevenUrl + "/rest/prodstatservice/stat/stopdisplay/" + prdNo);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("openapikey", Constants.elevenApiKey);

            conn.getResponseCode();
            LogWriter lw = new LogWriter("PUT", url.toString(), System.currentTimeMillis());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "EUC-KR"));
            String inputLine = null;
            String returnStr = "";
            while ((inputLine = in.readLine()) != null) {
//                System.out.println(inputLine);
                String decoder = URLDecoder.decode(inputLine, "euc-kr");
                decoder = URLDecoder.decode(decoder, "euc-kr");
                returnStr += decoder;
            }
            System.out.println(returnStr);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(returnStr);
            InputSource is = new InputSource(sr);
            Document dc = db.parse(is);
            NodeList nl = dc.getElementsByTagName("ClientMessage");
            if (nl.item(0).getChildNodes().item(1).getTextContent().equals("200")) {
                return commonFunction.makeReturn("jsonp", "200", nl.item(0).getChildNodes().item(1).getTextContent(), nl.item(0).getChildNodes().item(0).getTextContent());
            } else {
                return commonFunction.makeReturn("jsonp", "500", nl.item(0).getChildNodes().item(1).getTextContent(), nl.item(0).getChildNodes().item(0).getTextContent());
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }
    }

    public String getQnaList(String startday, String endday) {
        try{
            URL url = new URL(Constants.elevenUrl + "/rest/prodqnaservices/prodqnalist/" + startday + "/" + endday + "/00" ); //00:전체조회, 01:답변완료조회, 02:미답변조회
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("openapikey", Constants.elevenApiKey);

            LogWriter lw = new LogWriter("GET", url.toString(), System.currentTimeMillis());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "EUC-KR"));
            String inputLine = null;
            String returnStr = "";
            while ((inputLine = in.readLine()) != null) {
                returnStr += inputLine;
            }
            System.out.println(returnStr);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(returnStr);
            InputSource is = new InputSource(sr);
            Document dc = db.parse(is);
            NodeList nl = dc.getElementsByTagName("ns2:productQna");
            List<Map<String, Object>>listMap = new ArrayList<>();
            /*
            문의 글 번호
            문의 내용
            상품번호
            주문번호
            답변여부
            답변내용
            오픈마켓분류
             */
            for (int i = 0 ; i<nl.getLength();i++){
                //DB에 인입하는과정 필요
                Map<String, Object> map = new HashMap<>();
                String qnaNo = nl.item(i).getChildNodes().item(5).getTextContent();
                String qnaText = nl.item(i).getChildNodes().item(4).getTextContent();
                String prdNo = nl.item(i).getChildNodes().item(3).getTextContent();
                String prdNm = nl.item(i).getChildNodes().item(14).getTextContent();
                String ordNo = nl.item(i).getChildNodes().item(12).getTextContent();
                String answerYn = nl.item(i).getChildNodes().item(2).getTextContent();
                String answer = nl.item(i).getChildNodes().item(0).getTextContent();

                map.put("qnaNo", qnaNo);
                map.put("qnaText", qnaText);
                map.put("prdNo", prdNo);
                map.put("ordNo", ordNo);
                map.put("prdNm", prdNm);
                map.put("answerYn", answerYn);
                map.put("answer", answer);
                listMap.add(map);
            }
            System.out.println(listMap);
            return commonFunction.makeReturn("jsonp", "200", "OK", listMap);


        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
            }

    }

    public String answerQna(String qnaNo, String prdNo, String answer){
        try {
            URL url = new URL(Constants.elevenUrl + "rest/prodqnaservices/prodqnaanswer/" +qnaNo+"/"+prdNo);
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"euc-kr\" standalone=\"yes\"?>");
            sb.append("<ProductQna>");
            sb.append("<answerCont>" + answer +"</answerCont>");
            sb.append("</ProductQna>");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("openapikey", Constants.elevenApiKey);
            conn.setRequestProperty("Content-Type", "text/xml; charset=euc-kr");

            OutputStreamWriter ws = new OutputStreamWriter(conn.getOutputStream(), "EUC-KR");
            ws.write(sb.toString());
            ws.flush();
            LogWriter lw = new LogWriter("PUT", url.toString(), sb.toString(), System.currentTimeMillis());

            conn.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "EUC-KR"));
            String inputLine = null;
            String returnStr = "";
            while ((inputLine = in.readLine()) != null) {
                returnStr += inputLine;
            }
            System.out.println(returnStr);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(returnStr);
            InputSource is = new InputSource(sr);
            Document dc = db.parse(is);
            NodeList nl = dc.getElementsByTagName("ClientMessage");
            if(nl.item(3).getTextContent().equals("200")) {
                //결과코드 200 성공시
                return commonFunction.makeReturn("jsonp", "200", "OK", returnStr);
            } else {
                //결과코드 에러시
                return commonFunction.makeReturn("jsonp", "500", nl.item(3).getTextContent(), nl.item(2).getTextContent());
            }


        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500",e.getMessage());
        }
    }

}
