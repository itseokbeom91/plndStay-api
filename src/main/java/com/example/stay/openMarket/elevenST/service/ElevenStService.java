package com.example.stay.openMarket.elevenST.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.common.util.XmlUtility;
import com.example.stay.openMarket.elevenST.mapper.ElevenStMapper;
import org.jetbrains.annotations.NotNull;
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
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("elevenST.ElevenStService")
public class ElevenStService {

    @Autowired
    ElevenStMapper elevenStMapper;

    CommonFunction commonFunction = new CommonFunction();

    XmlUtility xmlUtility = new XmlUtility();


    /**
     * 11번가 상품등록 API
     * @param accommID  : 등록하고자 하는 숙소ID
     * @param bgnDay    : 판매 시작일자 (yyyyMMdd)
     * @param endDay    : 판매 종료일자 (yyyyMMdd)
     * @return
     */
    public String regProduct(String accommID, String bgnDay, String endDay) {
        try {
            URL url = new URL(Constants.elevenUrl + "/rest/prodservices/product");
            Map<String, Object>map = elevenStMapper.getAccomm(accommID);
            map.put("aplBgnDy", bgnDay);
            map.put("aplEndDy", endDay);
            map.put("selPrc", "100000000"); // 대표가이니 가장 낮은가격이 되련지 아님 상품매칭시 대표가격이 등록되는건지 확인 필요
            map.put("prdImage01", "https://cdn.imweb.me/thumbnail/20221018/2fa9b7c3276c7.png");
            StringBuffer sb = new StringBuffer();
            sb.append("<Product>");
            sb.append("<selMnbdNckNm>condo24</selMnbdNckNm>"); //닉네임 필수항목은 아님
            sb.append("<selMthdCd>01</selMthdCd>"); //판매방식 01:고정가판매, 04:예약판매, 05:중고판매 이 외의 코드는 사용 X
            sb.append("<dispCtgrNo>1018070</dispCtgrNo>"); //카테고리 넘버는 소카테고리 넘버 (2878 => 1017895(국내숙박) => 1017902(호텔) => 지역 (ex: 서울 1018070)  //호텔, 리조트, 모텔, 펜션, 게스트하우스등 있음
//            sb.append("<prdNm>" + map.get("prdNm") + "</prdNm>");
            sb.append("<prdNm><![CDATA[" + map.get("strSubject") + "[TEST상품/주문불가]]]></prdNm>"); //상품명 추후 배포시 TEST관련 문구 제거
            sb.append("<sellerPrdCd>" + map.get("intAID") + "</sellerPrdCd>");
            sb.append("<prdImage01>" + map.get("prdImage01") + "</prdImage01>");
//            sb.append("<prdImage02>" + map.get("prdImage02") + "</prdImage02>");
//            sb.append("<prdImage03>" + map.get("prdImage03") + "</prdImage03>");
            sb.append("<htmlDetail><![CDATA[" + map.get("strDescription") + "]]></htmlDetail>");
            sb.append("<selTermUseYn>Y</selTermUseYn>"); //판매기간 (N: 즉시 영구판매)
            sb.append("<brand>febHotel</brand>"); //브랜드명
            sb.append("<ProductNotification>"); //상품정보고시 호텔/펜션예약(891037) 고정값
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

//            Map<String, Object> map3 = new HashMap<>();
            List<Map<String, Object>> listMap = new ArrayList<>(); //TEST용임 실 옵션생성시 어떻게 들어올지 의사협의 필요
//            listMap = elevenStMapper.getAccomm();
            for (int j = 0; j<3; j++) {
                int date = 20230901;
                int price = 1000000;
                for (int i = 0; i < 10; i++) {
                    Map<String, Object> map3 = new HashMap<>();
                    Random random = new Random();
                    map3.put("optPrice", price + random.nextInt(100)*100);
                    map3.put("optCount", "1");
                    map3.put("optType1", date+j);
                    map3.put("optType2", "옵션" + i);
//                map2.put("listMap", map3);
//                Map<String, Object> map2 = map3;
                    listMap.add(map3);
                }
            }


            //옵션 설정
            sb.append("<optSelectYn>Y</optSelectYn>");
            sb.append("<txtColCnt>1</txtColCnt>");
            sb.append("<optionAllQty>9999</optionAllQty>"); // 각 옵션별 재고 수량 전체 더해서 입력
            sb.append("<optionAllAddPrc>0</optionAllAddPrc>");
            sb.append("<prdExposeClfCd>01</prdExposeClfCd>");
            sb.append("<optMixYn>N</optMixYn>");
            //AS-IS 기준
            sb.append("<ProductOptionExt>");
            sb.append("<ProductOption>");
            sb.append("<colOptPrice>0</colOptPrice>");
            sb.append("<colOptCount>1</colOptCount>");
            sb.append("<colCount/>");
            sb.append("<optWght/>");
            sb.append("<useYn>Y</useYn>");
            sb.append("<colSellerStockCd></colSellerStockCd>");
            sb.append("<optionMappingKey><![CDATA[투숙일자:20230901†" + "객실타입:xxx ]]></optionMappingKey>");
            sb.append("</ProductOption>");
            for(int i = 0; i < listMap.size(); i++){//관리자페이지에서 선택된값 리스트로 받아서 넣어야 함
                sb.append("<ProductOption>");
                sb.append("<colOptPrice>" + listMap.get(i).get("optPrice") + "</colOptPrice>");
                sb.append("<colOptCount>" + listMap.get(i).get("optCount") + "</colOptCount>");
                sb.append("<colCount/>");
                sb.append("<optWght/>");
                sb.append("<useYn>Y</useYn>");
                sb.append("<colSellerStockCd></colSellerStockCd>");
                sb.append("<optionMappingKey><![CDATA[투숙일자:" + listMap.get(i).get("optType1") + "†" + "객실타입:" + listMap.get(i).get("optType2") + " ]]></optionMappingKey>");
                sb.append("</ProductOption>");
            }
            sb.append("</ProductOptionExt>");


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
            if (!xmlUtility.getTagValue( "resultCode", (Element) nl.item(0)).equals("200")) {
                return commonFunction.makeReturn("jsonp", "500", xmlUtility.getTagValue( "message", (Element) nl.item(0)));
            }
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

    /**
     * 주문 취소
     * @param ordNo             : 주문번호
     * @param ordPrdSeq         : 주문순번
     * @param ordCnRsnCd        : 사유코드 06: 배송지연, 07: 상품/가격정보 오입력, 08: 상품품절, 09: 옵션품절, 10: 고객변심, 99: 기타
     * @param ordCnDtlsRsn      : 사유
     * @return
     */
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
            if (nl.item(0).getChildNodes().item(2).getTextContent().equals("200")) {
                return commonFunction.makeReturn("jsonp", "200", nl.item(0).getChildNodes().item(1).getTextContent(), nl.item(0).getChildNodes().item(0).getTextContent());
            } else {
                return commonFunction.makeReturn("jsonp", "500", nl.item(0).getChildNodes().item(1).getTextContent(), nl.item(0).getChildNodes().item(0).getTextContent());
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }
    }
    /*
    재고 수량 가져오기
     */
    public String getStockList (String prdNo) {
        try {
            URL url = new URL(Constants.elevenUrl + "/rest/prodmarketservice/prodmarket/stck/" + prdNo);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("GET");
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
            NodeList nl = dc.getChildNodes().item(0).getChildNodes();
            List<Map<String, Object>> resultList = new ArrayList<>();
            String prdNm = xmlUtility.getTagValue("ns2:prdNm", (Element) nl);
            for (int i = 0 ; i<nl.getLength() ; i++) {
                Map<String, Object> prdMap =new HashMap<>();
                nl.item(i);
                if(xmlUtility.getTagValue("mixDtlOptNm", (Element) nl.item(i))==null) continue;
                prdMap.put("prdStockNo", xmlUtility.getTagValue("prdStckNo", (Element) nl.item(i)));
                prdMap.put("stockQty", xmlUtility.getTagValue("stckQty", (Element) nl.item(i)));
                prdMap.put("투숙일자", xmlUtility.getTagValue("mixDtlOptNm", (Element) nl.item(i)).toString().split(",")[0]);
                prdMap.put("객실타입", xmlUtility.getTagValue("mixDtlOptNm", (Element) nl.item(i)).toString().split(",")[1]);

                prdMap.put("mixDetailOptNm", xmlUtility.getTagValue("mixDtlOptNm", (Element) nl.item(i)));
                prdMap.put("mixOptNm", xmlUtility.getTagValue("mixOptNm", (Element) nl.item(i)));
                resultList.add(prdMap);

            }
            //TO-DO 각 옵션별 옵션번호 상품DB에 인입
            return commonFunction.makeReturn("jsonp", "200", "OK", resultList.toString());
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }
    }
    /**
     *  11번가에 등록된 상품의 문의내역을 받아옵니다.
     *
     * @return         	QnA 목록,
     *                  문의 글 번호, 문의내용, 상품번호
     *                  주문번호, 답변여부, 답변내용, 오픈마켓분류
     */
    public String getQnaList() {
        try{
            String startday = "";
            String endday = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date current = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(current);
            endday = sdf.format(c.getTime());
            c.add(Calendar.DATE, -1);
            startday = sdf.format(c.getTime());
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
                String qnaNo = xmlUtility.getTagValue("brdInfoNo", (Element) nl.item(i)); //nl.item(i).getChildNodes().item(5).getTextContent();
                String qnaText = xmlUtility.getTagValue("brdInfoCont", (Element) nl.item(i));
                String prdNo = xmlUtility.getTagValue("brdInfoClfNo", (Element) nl.item(i));
                String prdNm = xmlUtility.getTagValue("prdNm", (Element) nl.item(i));
                String ordNo = xmlUtility.getTagValue("ordNoDe", (Element) nl.item(i));
                String answerYn = xmlUtility.getTagValue("answerYn", (Element) nl.item(i));
                String answer = xmlUtility.getTagValue("answerCont", (Element) nl.item(i));

                map.put("qnaNo", qnaNo);
                map.put("qnaText", qnaText);
                map.put("prdNo", prdNo);
                map.put("ordNo", ordNo);
                map.put("prdNm", prdNm);
                map.put("answerYn", answerYn);
                map.put("answer", answer);
                map.put("salesChannel", "elevenSt");
                listMap.add(map);
            }
            System.out.println(listMap);
            return commonFunction.makeReturn("jsonp", "200", "OK", listMap);


        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
            }

    }
    /**
     * 문의된 내용을 답변합니다.
     *
     * @param  qnaNo  	문의 글 번호 (11번가)
     * @param  prdNo  	상품번호
     * @param  answer  	답변내용
     * @return         	API호출 결과
     */
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

    /**
     * 주문 목록 조회
     * @return
     */
    public String getOrderList() {
        //YYYYMMDDHHmm 형식으로 전달되어야함
        try {
            String startdate = "";
            String enddate = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            Date current = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(current);
            enddate = sdf.format(c.getTime());
            c.add(Calendar.MINUTE, -15); //현재시각 - 15분
            startdate = sdf.format(c.getTime());
            URL url = new URL(Constants.elevenUrl + "/rest/ordservices/dlvcompleted/" + startdate + "/" + enddate);
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
            NodeList nl = dc.getElementsByTagName("ns2:order");
            List<Map<String, Object>>listMap = new ArrayList<>(); //
            String ordNo = "";

            for (int i = 0 ; i<nl.getLength();i++){
                //예약정보 DB에 인입하는과정 필요
                Map<String, Object> map = new HashMap<>();
//                ordNo += nl.item(i).getChildNodes().item(23).getTextContent() + ",";
                ordNo =  xmlUtility.getTagValue("ordNo", (Element) nl.item(i));
                map.put("ordNo", ordNo);
                listMap.add(map);
            }


            return commonFunction.makeReturn("jsonp", "200", "OK", listMap);
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }
    }

    public String getOrderInfo(String ordNo) {
        //YYYYMMDDHHmm 형식으로 전달되어야함
        try {
            URL url = new URL(Constants.elevenUrl + "/rest/claimservice/orderlistalladdr/" + ordNo);
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
            NodeList nl = dc.getElementsByTagName("ns2:order");
            List<Map<String, Object>>listMap = new ArrayList<>(); //


            return commonFunction.makeReturn("jsonp", "200", "OK", "OK");
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }

    }

    public String updatePrdAmt(String prdNo) {
        try {
            URL url = new URL(Constants.elevenUrl + "/rest/prodservice/product/priceCoupon/" + prdNo);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("openapikey", Constants.elevenApiKey);
            conn.setRequestProperty("Content-Type", "text/xml; charset=euc-kr");
            conn.getResponseCode();
            LogWriter lw = new LogWriter("POST", url.toString(), System.currentTimeMillis());
            return commonFunction.makeReturn("jsonp", "200", "OK", "OK");
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }
    }

    public String updatePrdOption(String prdNo) {
        try {
            URL url = new URL(Constants.elevenUrl + "/rest/prodservice/updateProductOption/" + prdNo);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"euc-kr\" standalone=\"yes\"?>");
            sb.append("<Product>");
            //옵션 설정
            sb.append("<optSelectYn>Y</optSelectYn>");
            sb.append("<txtColCnt>1</txtColCnt>");
            sb.append("<optionAllQty>9999</optionAllQty>"); // 각 옵션별 재고 수량 전체 더해서 입력
            sb.append("<optionAllAddPrc>0</optionAllAddPrc>");
            sb.append("<prdExposeClfCd>01</prdExposeClfCd>");
            sb.append("<optMixYn>N</optMixYn>");
            //AS-IS 기준
            sb.append("<ProductOptionExt>");
            sb.append("<ProductOption>");

            sb.append("<colOptPrice>0</colOptPrice>");
            sb.append("<colOptCount>1</colOptCount>");
            sb.append("<colCount/>");
            sb.append("<optWght/>");
            sb.append("<useYn>Y</useYn>");
            sb.append("<colSellerStockCd></colSellerStockCd>");
            sb.append("<optionMappingKey><![CDATA[투숙일자:20230901†" + "객실타입:xxx ]]></optionMappingKey>");
            
            sb.append("</ProductOption>");
            sb.append("</ProductOptionExt>");
            sb.append("</Product>");
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("openapikey", Constants.elevenApiKey);
            conn.setRequestProperty("Content-Type", "text/xml; charset=euc-kr");
            conn.getResponseCode();
            LogWriter lw = new LogWriter("POST", url.toString(), System.currentTimeMillis());
            return commonFunction.makeReturn("jsonp", "200", "OK", "OK");
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }
    }

    public String updateStock(String prdNo, String Stock) {
        try {
            URL url = new URL(Constants.elevenUrl + "/rest/prodservice/stockqty/" + prdNo);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"euc-kr\" standalone=\"yes\"?>");
            sb.append("<ProductStock>");
            sb.append("<prdNo>" + prdNo + "</prdNo>");
            sb.append("<prdStockNo></prdStockNo>");
            sb.append("<stckQty>" + Stock + "</stckQty>");
            sb.append("</ProductStock>");
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("openapikey", Constants.elevenApiKey);
            conn.setRequestProperty("Content-Type", "text/xml; charset=euc-kr");
            conn.getResponseCode();
            LogWriter lw = new LogWriter("POST", url.toString(), System.currentTimeMillis());
            return commonFunction.makeReturn("jsonp", "200", "OK", "OK");
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }
    }

}
