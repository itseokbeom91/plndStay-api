package com.example.stay.openMarket.elevenST.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.common.util.XmlUtility;
import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.dto.RsvStayDto;
import com.example.stay.openMarket.common.dto.StockDto;
import com.example.stay.openMarket.common.mapper.CommonMapper;
import com.example.stay.openMarket.common.service.CommonService;
import com.example.stay.openMarket.elevenST.mapper.ElevenStMapper;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("elevenST.ElevenStService")
public class ElevenStService {

    @Autowired
    private ElevenStMapper elevenStMapper;

    @Autowired
    private CommonMapper commonMapper;

    CommonFunction commonFunction = new CommonFunction();

    @Autowired
    private CommonService commonService;

    XmlUtility xmlUtility = new XmlUtility();


    /**
     * 11번가 상품등록 API
     * @param intAID  : 등록하고자 하는 숙소ID
     * @param bgnDay    : 판매 시작일자 (yyyyMMdd)
     * @param endDay    : 판매 종료일자 (yyyyMMdd)
     * @return
     */
    public String regProduct(String intAID, String bgnDay, String endDay, boolean isUpdate, String prdNm, int category){
        try {
            URL url;
            isUpdate=true;
            if (isUpdate){
                url = new URL(Constants.elevenUrl + "/rest/prodservices/product/6442491590");//+commonMapper.getStrPdtCode(Integer.parseInt(intAID), 1));
            } else {
                url = new URL(Constants.elevenUrl + "/rest/prodservices/product");
            }
//            bgnDay = "2023/11/01";
//            endDay = "2025/04/10";
            int selprc = elevenStMapper.getMinPrice(intAID, bgnDay);
            Map<String, Object>map = elevenStMapper.getAccomm(intAID);
//            String pricet = String.valueOf(commonMapper.getOmkSales(Integer.parseInt(accommID), 1));
            map.put("aplBgnDy", bgnDay);
            map.put("aplEndDy", endDay);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date current = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(current);
            bgnDay = sdf.format(c.getTime());
            c.add(Calendar.DATE, 119); //현재시각 - 1일 11번가는 3분마다 스케쥴링
            endDay = sdf.format(c.getTime());
            map.put("selPrc", selprc); // 대표가이니 가장 낮은가격이 되련지 아님 상품매칭시 대표가격이 등록되는건지 확인 필요
            map.put("prdImage01", "https://cdn.pixabay.com/photo/2023/03/12/21/05/egg-7847875_1280.png"); //
//            map.put("prdImage01", "https://cdn.imweb.me/thumbnail/20221018/2fa9b7c3276c7.png");
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            sb.append("<Product>");
            sb.append("<selMthdCd>01</selMthdCd>"); //판매방식 01:고정가판매, 04:예약판매, 05:중고판매 이 외의 코드는 사용 X
            if (isUpdate && category!=0){
                sb.append("<dispCtgrNo></dispCtgrNo>");
            } else {
                sb.append("<dispCtgrNo>1018070</dispCtgrNo>"); //카테고리 넘버는 소카테고리 넘버 (2878 => 1017895(국내숙박) => 1017904(리조트) => 지역 (ex: 경인 1018106)  //호텔, 리조트, 모텔, 펜션, 게스트하우스등 있음
            }
            sb.append("<prdTypCd>01</prdTypCd>"); // 29:openAPI사용시 여행상품은 29
            sb.append("<selMnbdNckNm>condo24</selMnbdNckNm>"); //닉네임 필수항목은 아님
            sb.append("<sellerPrdCd>" + map.get("intAID") + "</sellerPrdCd>");
            if (isUpdate && !prdNm.equals("")){
                sb.append("<prdNm><![CDATA[[test/주문불가]앱지콘도]]></prdNm>");
            }else {
                sb.append("<prdNm><![CDATA[" + map.get("strSubject") + "[TEST상품/주문불가]]]></prdNm>"); //상품명 추후 배포시 TEST관련 문구 제거
            }
            sb.append("<brand>febHotel</brand>"); //브랜드명
            sb.append("<rmaterialTypCd>04</rmaterialTypCd>"); //원재료 유형코드 04: 표시대상 아님
            sb.append("<orgnTypCd>03</orgnTypCd>"); //원산지코드 03:기타, 01:국내, 02:해외 국내나 해외선택시 원산지지역코드 입력해야함
            sb.append("<suplDtyfrPrdClfCd>01</suplDtyfrPrdClfCd>"); //부가세 / 면세상품 코드 01:과세상품
            sb.append("<prdStatCd>01</prdStatCd>"); //새상품
            sb.append("<minorSelCnYn>Y</minorSelCnYn>"); //미성년자 구매 가능 여부
            sb.append("<prdImage01>" + map.get("prdImage01") + "</prdImage01>");
            sb.append("<htmlDetail><![CDATA[" + /*map.get("strDescription")*/ "TEST" + "]]></htmlDetail>");
            sb.append("<ProductCertGroup>"); //인증정보그룹
            sb.append("<crtfGrpTypCd></crtfGrpTypCd>"); //새상품
            sb.append("<crtfGrpObjClfCd></crtfGrpObjClfCd>"); //새상품
            sb.append("<crtfGrpExptTypCd></crtfGrpExptTypCd>"); //새상품
            sb.append("<ProductCert>"); //새상품
            sb.append("<certTypeCd>131</certTypeCd>"); //새상품
            sb.append("<certKey></certKey>"); //새상품
            sb.append("</ProductCert>"); //새상품
            sb.append("</ProductCertGroup>"); //인증정보그룹
            sb.append("<selPrc>" + map.get("selPrc") + "</selPrc>"); //판매가 (원가)
            //옵션 설정
            sb.append("<optSelectYn>Y</optSelectYn>");
            sb.append("<txtColCnt>1</txtColCnt>");
            sb.append("<optionAllQty>9999</optionAllQty>"); // 각 옵션별 재고 수량 전체 더해서 입력
            sb.append("<optionAllAddPrc>0</optionAllAddPrc>");
            sb.append("<prdExposeClfCd>00</prdExposeClfCd>");
            sb.append("<optMixYn>N</optMixYn>");
            //AS-IS 기준
            sb.append("<ProductOptionExt>");

            // 재고 가져오기
            List<StockDto> stockList = commonMapper.getStockList(Integer.parseInt(intAID), 1, bgnDay.replaceAll("/", "-"));

            System.out.println(stockList);
            for (StockDto dto : stockList) {

                String strStockSubject = dto.getStrRmtypeName();
                int intStockCnt = dto.getIntStock();
                String strStockdate = dto.getDateSales();
                int intStockSalePrice = dto.getMoneySales(); // 판매가
                intStockSalePrice = intStockSalePrice-selprc; //판매가 - 최저가 = 추가금액
                int intStockCost = dto.getMoneyCost(); // 공급가
                int intIdx = dto.getIntIdx();
                String strPkgName = dto.getStrPkgName();
                if(intStockCnt == 0){
                    intStockCnt=1;
                }
                sb.append("<ProductOption>");
                sb.append("<colOptPrice>" + intStockSalePrice + "</colOptPrice>");
                sb.append("<colOptCount>" + intStockCnt + "</colOptCount>");
                sb.append("<colCount/>");
                sb.append("<optWght/>");
//                sb.append("<useYn>Y</useYn>");
                sb.append("<colSellerStockCd>"+intIdx+"</colSellerStockCd>");//셀러가 사용할 재고번호
                sb.append("<optionMappingKey><![CDATA[투숙일자:" + strStockdate + "†" + "객실타입:" + strStockSubject +" / " + strPkgName + " ]]></optionMappingKey>");
                sb.append("</ProductOption>");
            }
            sb.append("</ProductOptionExt>");
            sb.append("<dlvCnAreaCd>01</dlvCnAreaCd>");
            sb.append("<dlvWyCd>05</dlvWyCd>");
//            sb.append("<dlvSendCloseTmpltNo>01</dlvSendCloseTmpltNo>");//필수라고 적혀있는데 뭔지는 모르겠음
            sb.append("<dlvCstInstBasiCd>01</dlvCstInstBasiCd>");
            sb.append("<bndlDlvCnYn>N</bndlDlvCnYn>"); //묶음배송 여부 불가
            sb.append("<dlvCstPayTypCd>03</dlvCstPayTypCd>"); //결제방법 03 선결제
            sb.append("<dlvCnAreaCd>01</dlvCnAreaCd>");
            sb.append("<asDetail>.</asDetail>"); //A/S 안내 필수항목으로 .이라도 입력하라 함
            sb.append("<rtngExchDetail>불가</rtngExchDetail>"); //반품/교환 안내 필수항목으로 .이라도 입력하라 함
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







//            sb.append("<prdImage02>" + map.get("prdImage02") + "</prdImage02>");
//            sb.append("<prdImage03>" + map.get("prdImage03") + "</prdImage03>");
//            sb.append("<useLimitClfCd>Y</useLimitClfCd>"); //판매기간 (N: 즉시 영구판매)selPrdClfCd
//            sb.append("<aplBgnDy>" + map.get("aplBgnDy") + "</aplBgnDy>"); //판매 시작일
//            sb.append("<selPrdClfCd>90:407</selPrdClfCd>"); //판매기간 (N: 즉시 영구판매)
//            sb.append("<setFpSelTermYn>Y</setFpSelTermYn>");
//            sb.append("<selPrdClfFpCd>120:108</selPrdClfFpCd>");
//
//
//
//
//            sb.append("<orgnNmVal>TEST</orgnNmVal>"); //원산지 명
//            sb.append("<prdSelQty>1</prdSelQty>");// 재고수량 필수항목
//            sb.append("<dlvCstInstBasiCd>01</dlvCstInstBasiCd>"); //H.S코드
//            if(bgnDay !=""){
//                sb.append("<aplBgnDy>" + map.get("aplBgnDy") + "</aplBgnDy>"); //판매 시작일
//                sb.append("<aplEndDy>" + map.get("aplEndDy") + "</aplEndDy>"); //판매 종료일
//            }
//            sb.append("<prcCmpExpYn>Y</prcCmpExpYn>"); // 가격비교
//            sb.append("<drcStlYn>Y</drcStlYn>"); // 즉시결제여부
//            sb.append("<directStlYn>Y</directStlYn>"); // 바로결제여부
//            sb.append("<dtldDescTyp>H</dtldDescTyp>"); // 모바일 노출타입
            sb.append("</Product>");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);

            conn.setRequestMethod("POST");
            if(isUpdate){
                conn.setRequestMethod("PUT");
            }
            conn.addRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conn.setRequestProperty("openapikey", Constants.elevenApiKey);
            System.out.println(conn.getRequestMethod());

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(sb.toString());
            wr.flush();
            LogWriter lw = new LogWriter("POST", url.toString(), sb.toString(), System.currentTimeMillis());

            String inputLine = null;
            String returnStr = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
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
                return commonFunction.makeReturn("jsonp", "500", "ERROR", xmlUtility.getTagValue( "message", (Element) nl.item(0)));
            }
            String prdNo = xmlUtility.getTagValue( "productNo", (Element) nl.item(0));

            //오픈마켓 테이블에 박아야겠지?

            elevenStMapper.insertAccomm(map.get("intAID").toString(),"1","Y", map.get("strSubject").toString(), prdNo, map.get("strDescription").toString());

            getStockList(Integer.parseInt(intAID));

            if(isUpdate){
                return xmlUtility.getTagValue( "message", (Element) nl.item(0)).toString();
            }
            return commonFunction.makeReturn("jsonp", "200", "SUCCESS", "");
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }
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
    public String modProduct(String intAID, int category, String prdNm) {
        try {
            String result = regProduct(intAID, "", "", true, prdNm, category);

            if(result.equals("기존에 요청한 XML과 동일한 XML을 요청하셨습니다. 확인 부탁드립니다.")){
                return commonFunction.makeReturn("jsonp", "202", result);
            }

            return commonFunction.makeReturn("jsonp", "200", result);
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }
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
    public String updateDisplay (int intAID, String state) {
        try {
            String prdNo = commonMapper.getStrPdtCode(intAID, 1);
            if(elevenStMapper.getUsgYn(String.valueOf(intAID)).equals("Y")){
                state = "stopdisplay";
            }else {
                state = "restartdisplay";
            }
            URL url = new URL(Constants.elevenUrl + "/rest/prodstatservice/stat/" + state + "/" + prdNo);
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
                //판매 중지/재개 처리시 accomm_omk strUsageYn update해줘야함
                if(state.equals("stopdisplay")){
                    elevenStMapper.updateUsg(intAID, "N");
                } else {
                    elevenStMapper.updateUsg(intAID, "Y");
                }
                return commonFunction.makeReturn("jsonp", "200", nl.item(0).getChildNodes().item(1).getTextContent(), nl.item(0).getChildNodes().item(0).getTextContent());
            } else {
                return commonFunction.makeReturn("jsonp", "500", nl.item(0).getChildNodes().item(1).getTextContent(), nl.item(0).getChildNodes().item(0).getTextContent());
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }
    }

    /**
     * 11번가에 등록된 상품의 옵션을 조회하는 API
     * @param intAID 11번가 등록된 상품번호
     * @return
     */
    public String getStockList (int intAID) {
        try {
            String prdNo = commonMapper.getStrPdtCode(intAID, 1);
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
                prdMap.put("strDate", xmlUtility.getTagValue("mixDtlOptNm", (Element) nl.item(i)).toString().split(",")[0]);
                prdMap.put("strRmtypeNm", xmlUtility.getTagValue("mixDtlOptNm", (Element) nl.item(i)).toString().split(",")[1]);

                prdMap.put("mixDetailOptNm", xmlUtility.getTagValue("mixDtlOptNm", (Element) nl.item(i)));
                prdMap.put("mixOptNm", xmlUtility.getTagValue("mixOptNm", (Element) nl.item(i)));
                resultList.add(prdMap);

            }

            //TO-DO 각 옵션별 옵션번호 상품DB에 인입
            for(Map<String, Object> resMap : resultList){
                String strRmtypeNm = resMap.get("strRmtypeNm").toString();
                String strDate = resMap.get("strDate").toString();
                String prdStockNo = resMap.get("prdStockNo").toString();
                elevenStMapper.updateSeq(String.valueOf(intAID), strRmtypeNm, strDate, prdStockNo);
            }

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
            c.add(Calendar.DATE, -1); //현재시각 - 1일 11번가는 3분마다 스케쥴링
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
//            System.out.println(returnStr);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(returnStr);
            InputSource is = new InputSource(sr);
            Document dc = db.parse(is);
            NodeList nl = dc.getElementsByTagName("ns2:order");
            List<Map<String, Object>>listMap = new ArrayList<>(); //
            String ordNo = "";
            SimpleDateFormat oldDateFormat = new SimpleDateFormat("MM월dd일(E)");
            SimpleDateFormat nextYearDateFormat = new SimpleDateFormat("YY년MM월dd일(E)");
            SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (int i = 0 ; i<nl.getLength();i++){
                //예약정보 DB에 인입하는과정 필요
                Map<String, Object> map = new HashMap<>();
//                ordNo += nl.item(i).getChildNodes().item(23).getTextContent() + ",";
                ordNo =  xmlUtility.getTagValue("ordNo", (Element) nl.item(i));
                map.put("dlvNo", xmlUtility.getTagValue("dlvNo", (Element) nl.item(i)));
                map.put("ordPrdSeq", xmlUtility.getTagValue("ordPrdSeq", (Element) nl.item(i)));
                map.put("ordQty", xmlUtility.getTagValue("ordQty", (Element) nl.item(i)));
                map.put("prdNo", xmlUtility.getTagValue("prdNo", (Element) nl.item(i)));
                map.put("prdStckNo", xmlUtility.getTagValue("prdStckNo", (Element) nl.item(i)));
                map.put("slctPrdOptNm", xmlUtility.getTagValue("slctPrdOptNm", (Element) nl.item(i)));
                String sellerStockCd = nl.item(0).getChildNodes().item(45).getNodeValue();
                map.put("sellerStockCd",sellerStockCd == null ? "" : sellerStockCd);
                String lsls = map.get("slctPrdOptNm").toString();
//                System.out.println(lsls.length());
                String [] test = map.get("slctPrdOptNm").toString().split(",");
                String dateCheckIn = "";
                String strRmTypeName = "";


                if(test.length>2){
                    dateCheckIn = test[1].substring(test[1].indexOf(':')+1);
                    strRmTypeName = test[2].substring(test[2].indexOf(':')+1, test[2].lastIndexOf('-'));
                }else{
                    dateCheckIn = test[0].substring(test[0].indexOf(':')+1);
                    strRmTypeName = test[1].substring(test[1].indexOf(':')+1, test[1].lastIndexOf('-'));
                }
                Date formatDate = new Date();
                String lkDate = "";
                System.out.println(dateCheckIn + "   :   "+dateCheckIn.length());
//                Date formatDate = oldDateFormat.parse(dateCheckIn);

                if(dateCheckIn.length() > 9){
                    lkDate ="20" + dateCheckIn.toString().replaceAll("월", "-").replaceAll("년", "-").substring(0, 4);
                } else {
                    lkDate ="2023-" + dateCheckIn.toString().replaceAll("월", "-").substring(0, 5);
                }

//                Date newformatDate = newDateFormat.parse(strNewDtFormat);


//                map.put("sellerStockCd", xmlUtility.getTagValue("sellerStockCd", (Element) nl.item(i)));
                map.put("prdStckNo", xmlUtility.getTagValue("prdStckNo", (Element) nl.item(i)));
                map.put("ordNo", ordNo);
//                elevenStMapper.updateRsv(map.get("dlvNo").toString(), "", rsvStayDto);
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
            for (int i = 0 ; i < nl.getLength() ; i++){
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("orderNo", "0");
            }


            return commonFunction.makeReturn("jsonp", "200", "OK", returnStr);
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }

    }

    public String updatePrdAmt(int intAID) {
        try {
            String prdNo = commonMapper.getStrPdtCode(intAID, 1);
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

    public String updatePrdOption(int intAID) {
        try {
            String prdNo = commonMapper.getStrPdtCode(intAID, 1);
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

    /**
     *  11번가에 등록된 상품 옵션 등록 및 수정(STOCK_REAL 기반)
     * @param intAID        시설ID
     * @param bgnDay        시작일자(입실가능한)
     * @return
     */
    public String updateStock(int intAID, String bgnDay) {
        try {
            String prdNo = commonMapper.getStrPdtCode(intAID, 1);
            URL url = new URL(Constants.elevenUrl + "/rest/prodservices/updateProductOption/" + prdNo);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            StringBuffer sb = new StringBuffer();
            int selprc = commonMapper.getMinPrice(intAID, bgnDay, 1);
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            sb.append("<Product>");
            //옵션 설정
            sb.append("<optSelectYn/>");
            sb.append("<txtColCnt/>");
            sb.append("<optionAllQty/>"); // 각 옵션별 재고 수량 전체 더해서 입력
            sb.append("<optionAllAddPrc/>");
            sb.append("<prdExposeClfCd/>");
            sb.append("<optMixYn>N</optMixYn>");
            //AS-IS 기준
            sb.append("<ProductOptionExt>");

            sb.append("<ProductOption>");
            sb.append("<colOptPrice>0</colOptPrice>");
            sb.append("<colOptCount>2</colOptCount>");
            sb.append("<colCount/>");
            sb.append("<optWght/>");
//                sb.append("<useYn>Y</useYn>");
            sb.append("<colSellerStockCd>z</colSellerStockCd>");//셀러가 사용할 재고번호
            sb.append("<optionMappingKey><![CDATA[투숙일자:2†" + "객실타입:3]]></optionMappingKey>");
            sb.append("</ProductOption>");

            // 재고 가져오기
            List<StockDto> stockList = commonMapper.getStockList(intAID, 1, bgnDay.replaceAll("/", "-"));

            System.out.println(stockList);
            for (StockDto dto : stockList) {

                String strStockSubject = dto.getStrRmtypeName();
                int intStockCnt = dto.getIntStock();
                String strStockdate = dto.getDateSales();
                int intStockSalePrice = dto.getMoneySales(); // 판매가
                intStockSalePrice = intStockSalePrice-selprc; //판매가 - 최저가 = 추가금액
                int intStockCost = dto.getMoneyCost(); // 공급가
                int intIdx = dto.getIntIdx();
                String strPkgName = dto.getStrPkgName();
                if(intStockCnt == 0){
                    intStockCnt=1;
                }
                sb.append("<ProductOption>");
                sb.append("<colOptPrice>" + intStockSalePrice + "</colOptPrice>");
                sb.append("<colOptCount>" + intStockCnt + "</colOptCount>");
                sb.append("<colCount/>");
                sb.append("<optWght/>");
//                sb.append("<useYn>Y</useYn>");
                sb.append("<colSellerStockCd>"+intIdx+"</colSellerStockCd>");//셀러가 사용할 재고번호
                sb.append("<optionMappingKey><![CDATA[투숙일자:" + strStockdate + "†" + "객실타입:" + strStockSubject +" / " + strPkgName + " ]]></optionMappingKey>");
                sb.append("</ProductOption>");
            }
            sb.append("</ProductOptionExt>");
            sb.append("</Product>");
            String returnStr = "";
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("openapikey", Constants.elevenApiKey);
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(sb.toString());
            wr.flush();
            String inputLine = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "EUC-KR"));
            while ((inputLine = in.readLine()) != null) {
                String decoder = URLDecoder.decode(inputLine, "euc-kr");
                decoder = URLDecoder.decode(decoder, "euc-kr");
                returnStr += decoder;
//                returnStr += inputLine;
            }
            System.out.println(returnStr);

//            LogWriter lw = new LogWriter("POST", url.toString(), System.currentTimeMillis());
//            lw.log(2);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(returnStr);
            InputSource is = new InputSource(sr);
            Document dc = db.parse(is);

            NodeList nl = dc.getElementsByTagName("message");

            insertStockNo(prdNo);

            return commonFunction.makeReturn("jsonp", "500", returnStr);
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }
    }

    /**
     * 11번가 상품 상세설명 UPDATE
     * @param dataType dataType : 반환받을 데이터타입 default : jsonp
     * @param prdNo 11번가 상품번호
     * @return
     */
    public String updatePrdDesc(String dataType, String intAID) {
        try {
            String prdNo = commonMapper.getStrPdtCode(Integer.parseInt(intAID), 1);

            AccommDto accommDto = commonMapper.getAcmInfo(Integer.parseInt(intAID), 1);
//            if(accommDto.getStrACMPhotos()==null)accommDto.setStrACMPhotos("0|0");
            String strHtmlDesc = commonService.getStrPdtDtlInfo(accommDto, Integer.parseInt(intAID), 1);
            URL url = new URL(Constants.elevenUrl + "/rest/prodservices/updateProductDetailCont/" + prdNo);
//            elevenStMapper.insertAccomm(intAID);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>");
            sb.append("<ProductDetailCont>");
            sb.append("<prdDescContClob><![CDATA[" + strHtmlDesc + "]]></prdDescContClob>");
            sb.append("</ProductDetailCont>");
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("openapikey", Constants.elevenApiKey);
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(sb.toString());
            wr.flush();

            LogWriter lw = new LogWriter("POST", url.toString(), System.currentTimeMillis());

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
            NodeList nl = dc.getElementsByTagName("ProductDetailCont");
            String result = nl.item(0).getChildNodes().item(1).getTextContent();


            return commonFunction.makeReturn(dataType, "200", result );
        } catch (Exception e) {
            return commonFunction.makeReturn(dataType, "500", e.getMessage());
        }
    }

    public String getSettlement(String startDay, String endDay) {
        try{
            URL url = new URL(Constants.elevenUrl + "/rest/settlement/settlementList/" + startDay + "/" + endDay); //00:전체조회, 01:답변완료조회, 02:미답변조회
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("openapikey", Constants.elevenApiKey);


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
            NodeList nl = dc.getElementsByTagName("ns2:seStlDtlLists");
            return commonFunction.makeReturn("jsonp", "200", "OK");


        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }

    }

    public String insertStockNo(String prdNo) {
        try{
            URL url = new URL(Constants.elevenUrl + "/rest/prodmarketservice/prodmarket/stck/"+prdNo);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("openapikey", Constants.elevenApiKey);

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
            NodeList nl = dc.getElementsByTagName("ns2:ProductStock");
            List<Map<String, Object>> listMap = new ArrayList<>();
            String prcd="";
            String intAID = "";
            for (int i = 0 ; i< nl.getLength();i++){
                Map<String, Object> stockMap = new HashMap<>();
                String [] mixOpt = xmlUtility.getTagValue("mixDtlOptNm", (Element) nl.item(i)).toString().split(",");
                if (mixOpt.length > 2){
                    stockMap.put("accommName", xmlUtility.getTagValue("mixDtlOptNm", (Element) nl.item(i)).toString().split(",")[0]);
                    stockMap.put("dateSales", xmlUtility.getTagValue("mixDtlOptNm", (Element) nl.item(i)).toString().split(",")[1]);
                    stockMap.put("strRmName", xmlUtility.getTagValue("mixDtlOptNm", (Element) nl.item(i)).toString().split(",")[2]);
                }else {
                    stockMap.put("dateSales", xmlUtility.getTagValue("mixDtlOptNm", (Element) nl.item(i)).toString().split(",")[0]);
                    stockMap.put("strRmName", xmlUtility.getTagValue("mixDtlOptNm", (Element) nl.item(i)).toString().split(",")[1]);
                }
                stockMap.put("sellerStockCd", xmlUtility.getTagValue("sellerStockCd", (Element) nl.item(i)).toString());
                intAID = xmlUtility.getTagValue("sellerPrdCd", (Element) nl.item(i)).toString(); //sellerPrdCd
                stockMap.put("prdStckNo", xmlUtility.getTagValue("prdStckNo", (Element) nl.item(i)).toString());
                stockMap.put("prdStckStatCd", xmlUtility.getTagValue("prdStckStatCd", (Element) nl.item(i)).toString());
                listMap.add(stockMap);
                prcd += stockMap.get("sellerStockCd") + "|^|" + stockMap.get("prdStckNo") + "|^|" + stockMap.get("dateSales") + "|^|" + stockMap.get("strRmName") + "{{|}}";
            }
            prcd = prcd.substring(0, prcd.length()-5);
            elevenStMapper.updateElevenSeq(Integer.parseInt(intAID), prcd);
            System.out.println(listMap);



            if (!xmlUtility.getTagValue( "resultCode", (Element) nl.item(0)).equals("200")) {
                return commonFunction.makeReturn("jsonp", "500", "ERROR", xmlUtility.getTagValue( "message", (Element) nl.item(0)));
            }
            return commonFunction.makeReturn("jsonp", "200", "OK");


        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }

    }

}
