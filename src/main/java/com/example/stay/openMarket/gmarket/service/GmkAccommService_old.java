package com.example.stay.openMarket.gmarket.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.common.util.XmlUtility;
import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.mapper.CommonMapper;
import com.example.stay.openMarket.common.service.CommonService;
import com.example.stay.openMarket.gmarket.mapper.GmkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Service
public class GmkAccommService_old {

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private GmkMapper gmkMapper;

    @Autowired
    private CommonService commonService;

    @Autowired
    private XmlUtility xmlUtility;

    private static int intOmkIdx = 5;

    CommonFunction commonFunction = new CommonFunction();

    public String createAccomm(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            AccommDto accommDto = commonMapper.getAcmInfo(intAID, intOmkIdx);

            if(accommDto != null){
                String strAID = String.valueOf(intAID);
                String strCateCode = "100000013"; // 소분류 코드
                String strPdtCode = ""; // 지마켓 상품코드(수정시 필요)
                String strSubject = accommDto.getStrSubject(); // 상품명
                String strDescription = accommDto.getStrDescription(); // 상품 상세정보
//                String strPdtDtlInfo = commonService.getStrPdtDtlInfo(accommDto, intAID, intOmkIdx)
//                        .replace("&#8203;", "").replace("&#39;", "'").replace("&quot;", "'") // &quot; = \" 큰따옴표인디 왜...
//                        .replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
//                        .replace("?", "").replace("\"\"", "'");// 큰따옴표 두개를 왜 하나로..? // new 상품 상세정보
                String strPdtDtlInfo = "상품설명";
                // 상품 이미지
                String strAccommImg1 = "";
                String strAccommImg2 = "";
                String strAccommImg3 = "";
                if(accommDto.getStrACMPhotos() != null){
                    String strAcmPhotos = accommDto.getStrACMPhotos();
                    String[] photoArr = strAcmPhotos.split("\\|");

                    if(photoArr.length >= 3){
                        // TODO : 추후 이미지 저장 경로 정해지면 수정 할 것
                        strAccommImg1 = "https://condo24.com/" + photoArr[0];
                        strAccommImg2 = "https://condo24.com/" + photoArr[1];
                        strAccommImg3 = "https://condo24.com/" + photoArr[2];
                    }else{
                        // TODO : 추후 이미지 저장 경로 정해지면 수정 할 것
                        if(photoArr.length == 1){
                            strAccommImg1 = "https://condo24.com/" + photoArr[0];
                        }else if(photoArr.length == 2){
                            strAccommImg1 = "https://condo24.com/" + photoArr[0];
                            strAccommImg2 = "https://condo24.com/" + photoArr[1];
                        }
                    }

                    String strMakeNo = "제조사번호"; // 제조사번호

                    List<Map<String, String>> brandCodeList = gmkMapper.getBrandCodeList();
                    String strBrandNo = ""; // 브랜드번호
                    for(Map brandCodeMap : brandCodeList){
                        Map<String, String> codeMap = brandCodeMap;
                        String strCode = codeMap.get("strCode");
                        String strName = codeMap.get("strName");
                        if(strSubject.contains(strName)){
                            strBrandNo = strCode;
                        }
                    }

                    String strModelName = ""; // 모델명
                    String strOrgCode = "Etc"; // 원산지 구분
                    String strDlbType = "New"; // 배송비 구분
                    String strDlbFeeType = "Free"; // 상품별 배송비 종류
                    String strIsAdult =  "False"; // 성인용품 여부
                    String strTax = "Free"; // 부가세 면세 여부
                    String strTel = "1588-0134"; // 판매자 연락처
                    String strAddress = "서울특별시 마포구 백범로 116 동무마포타워 3층"; // AS센터 주소/정보
                    String strPriceComparison = "false"; // 가격 비교 노출 제외
                    String strNegotiaion = "false"; // 정하기 노출 제회(?)
                    String strBasket = "true"; // 장바구니 불가
                    String strAffiliateDiscount = "true"; // 제휴할인 제한
                    String strProductKind = "Shipping"; // 상품 종류(Shipping: 배송상품, Ecoupon: 이쿠폰상품)
//                    String strProductStatus = "New"; // 상품 상태(New : 신상품, Used : 중고상품)
//                    String strProductStatus2 = "NotUsed"; // 상품 상태(NotUsed : 미사용, AlmostNew : 거의 새것, Fine : 양호, Old : 약간 낡음 , ForCollect : 사용 불가)
                    String strOverseasShipping = "false"; // 해외배송 가능여부
                    String strFreeDeliveryType = "3"; // 무료배송비 타입
//                    String strGmkDiscount = "true"; // 지마켓 할인 적용 여부

                    String addItemAttr = makeAttr("OutItemNo", strAID) +  makeAttr("CategoryCode", strCateCode) + 
                            makeAttr("GmktItemNo", "") +  makeAttr("ItemName", strSubject) + makeAttr("ItemEngName", "") + 
                            makeAttr("ItemDescription", strDescription) +  makeAttr("GdHtml", strPdtDtlInfo) +  makeAttr("GdAddHtml", "") +
                            makeAttr("GdPrmtHtml", "") +  makeAttr("MakerNo", strMakeNo) +  makeAttr("BrandNo", strBrandNo) + 
                            makeAttr("ModelName", strModelName) +  makeAttr("IsAdult", strIsAdult) +  makeAttr("Tax", strTax) + 
                            makeAttr("MadeDate", "") +  makeAttr("AppearedDate", "") +  makeAttr("ExpirationDate", "") +
                            makeAttr("FreeGift", "") +  makeAttr("ItemKind", strProductKind) +  makeAttr("InventoryNo", strAID) + 
                            makeAttr("ItemWeight", "") +  makeAttr("IsOverseaTransGoods", strOverseasShipping) +  makeAttr("FreeDelFeeType", strFreeDeliveryType);

                    String newItemDescAttr = makeAttr("GdHtml", strPdtDtlInfo) +  makeAttr("GdAddHtml", "") +  makeAttr("GdPrmtHtml", "");

                    String refPriceAttr = makeAttr("Kind", "") +  makeAttr("Price", "");

                    String refusalAttr = makeAttr("IsPriceCompare", strPriceComparison) +  makeAttr("IsNego", strNegotiaion) + 
                            makeAttr("IsJaehuDiscount", strAffiliateDiscount) +  makeAttr("IsPack", strBasket);

                    String itemImageAttr = makeAttr("DefaultImage", strAccommImg1) +  makeAttr("LargeImage", "") + 
                            makeAttr("SmallImage", "") +  makeAttr("AddImage1", strAccommImg2) +  makeAttr("AddImage2", strAccommImg3);

                    String sellerInfoAttr = makeAttr("Telephone", strTel) +  makeAttr("Address", strAddress);

                    String shippingAttr = makeAttr("SetType", strDlbType) +  makeAttr("BundleNo", "") + 
                            makeAttr("GroupCode", "") +  makeAttr("RefundAddrNum", "");

                    String newshippingAttr = makeAttr("FeeCondition", strDlbFeeType) +  makeAttr("FeeBasePrice", "") +  makeAttr("Fee", "");

                    String bundleOrderAttr = makeAttr("BuyUnitCount", "") +  makeAttr("MinBuyCount", "");

                    String orderLimitAttr = makeAttr("OrderLimitCount", "") +  makeAttr("OrderLimitPeriod", "");

                    String attributeCodeAttr = makeAttr("AttributeCode", "");

                    String originAttr = makeAttr("Code", strOrgCode) +  makeAttr("Place", "");

                    String bookAttr = makeAttr("ISBN", "");

//                    System.out.println("addItemAttr : " + addItemAttr);
                    
                    // xml 생성
                    String strXml = "<?xml version='1.0' encoding='utf-8'?>"
                            + "<soap:Envelope xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:xsd='http://www.w3.org/2001/XMLSchema' xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/'>"
                            + "  <soap:Header>"
                            + "    <EncTicket xmlns='http://tpl.gmarket.co.kr/'>"
                            + "      <encTicket>" + Constants.gmk_normal_ticket + "</encTicket>"
                            + "    </EncTicket>"
                            + "  </soap:Header>"
                            + "  <soap:Body>"
                            + "    <AddItem xmlns='http://tpl.gmarket.co.kr/'>"
                            + "      <AddItem " + addItemAttr + ">"
                            + "        <NewItemDescription " + newItemDescAttr + " xmlns='http://tpl.gmarket.co.kr/tpl.xsd' />"
                            + "        <ReferencePrice " + refPriceAttr + " xmlns='http://tpl.gmarket.co.kr/tpl.xsd' />"
                            + "        <Refusal " + refusalAttr + " xmlns='http://tpl.gmarket.co.kr/tpl.xsd' />"
                            + "        <ItemImage " + itemImageAttr + " xmlns='http://tpl.gmarket.co.kr/tpl.xsd' />"
                            + "        <As " + sellerInfoAttr + " xmlns='http://tpl.gmarket.co.kr/tpl.xsd' />"
                            + "        <Shipping " + shippingAttr + " xmlns='http://tpl.gmarket.co.kr/tpl.xsd'>"
                            + "          <NewItemShipping " + newshippingAttr + " />"
                            + "        </Shipping>"
                            + "        <BundleOrder " + bundleOrderAttr + " xmlns='http://tpl.gmarket.co.kr/tpl.xsd' />"
                            + "        <OrderLimit " + orderLimitAttr + " xmlns='http://tpl.gmarket.co.kr/tpl.xsd' />"
                            + "        <AttributeCode " + attributeCodeAttr + " xmlns='http://tpl.gmarket.co.kr/tpl.xsd' />"
                            + "        <Origin " + originAttr + " xmlns='http://tpl.gmarket.co.kr/tpl.xsd' />"
                            + "        <Book " + bookAttr + " xmlns='http://tpl.gmarket.co.kr/tpl.xsd' />"
                            + "      </AddItem>"
                            + "    </AddItem>"
                            + "  </soap:Body>"
                            + "</soap:Envelope>";

//                    System.out.println(strXml);

                    // api 호출
                    URL url = new URL("http://tpl.gmarket.co.kr/v1/ItemService.asmx?WSDL");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(50000);
                    conn.setReadTimeout(50000);
                    conn.setRequestProperty("Host", "tpl.gmarket.co.kr");
                    conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
                    conn.setRequestProperty("Content-Length", Integer.toString(strXml.length()));
                    conn.setRequestProperty("Action", "http://tpl.gmarket.co.kr/AddItem");
                    conn.setRequestProperty("SOAPAction", "http://tpl.gmarket.co.kr/AddItem");

//                    conn.setInstanceFollowRedirects(false);
                    conn.setDoOutput(true);

                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
                    outputStreamWriter.write(strXml);
                    outputStreamWriter.flush();

                    if(conn.getResponseCode() == 200){
                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

                        Document document = dBuilder.parse(conn.getInputStream());
                        document.getDocumentElement().normalize();
                        String result = xmlUtility.parsingXml(document);

                        System.out.println("result : " + result);
                    }else{
                        System.out.println("Fail code : " + conn.getResponseCode());
                        message = "지마켓 api 호출 실패";
                    }

//                    String result = commonFunction.sendMessage("http://tpl.gmarket.co.kr/v1/ItemService.asmx?WSDL", "/AddItem", strXml);

                }else{
                    message = "시설 사진 불러오기 실패";
                }


            }else{
                message = "해당 시설 정보 불러오기 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "상품 생성 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }


        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    public String makeAttr(String strAttrName, String strAttrValue){
        String strAttr = strAttrName + "=\"" + strAttrValue + "\" ";
        return strAttr;
    }

    public String createAccommNotice(int intAID){
        LogWriter logWriter = new LogWriter(System.currentTimeMillis());
        String result = "";

        try{
            String strPdtCode = "";
            int gcode = 27; // 상품코드
            String silCode = "27-1"; // 항목코드
            String silAddYn = "Y"; // 항목코드 > 추가입력번호
            String silAddVal = "상세 설명 참조"; // 항목코드 > 추가입력번호

            int roopCnt = 0;
            if(gcode == 27){
                roopCnt = 7;
            }else{
                roopCnt = 8;
            }
            
            // xml 생성
            String strXml = "<?xml version='1.0' encoding='utf-8'?>"
                    + "<soap:Envelope xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:xsd='http://www.w3.org/2001/XMLSchema' xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/'>"
                    + "  <soap:Header>"
                    + "    <EncTicket xmlns='http://tpl.gmarket.co.kr/'>"
                    + "      <encTicket>" + Constants.gmk_normal_ticket + "</encTicket>"
                    + "    </EncTicket>"
                    + "  </soap:Header>"
                    + "  <soap:Body>"
                    + "    <AddOfficialInfo xmlns='http://tpl.gmarket.co.kr/'>"
                    + "      <AddOfficialInfo GmktItemNo=" + strPdtCode + "GroupCode=" + gcode + ">";

                    for(int i=0; i<roopCnt; i++){
                        strXml += "        <SubInfoList Code=" + gcode + "-" + i+1 + "AddYn=" + silAddYn + "AddValue=" + silAddVal + "xmlns='http://tpl.gmarket.co.kr/tpl.xsd' />";
                    }

            strXml += "      </AddOfficialInfo>"
                    + "    </AddOfficialInfo>"
                    + "  </soap:Body>"
                    + "</soap:Envelope>";


        }catch (Exception e){
            e.printStackTrace();
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return result;
    }



}
