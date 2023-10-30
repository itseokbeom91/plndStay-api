package com.example.stay.openMarket.gmarket.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.common.util.XmlUtility;
import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.dto.StockDto;
import com.example.stay.openMarket.common.mapper.CommonMapper;
import com.example.stay.openMarket.common.service.CommonService;
import com.example.stay.openMarket.gmarket.GmkUtil.GmkApi;
import com.example.stay.openMarket.gmarket.GmkUtil.HmacGenerater;
import com.example.stay.openMarket.gmarket.mapper.GmkMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class GmkAccommService {

    @Autowired
    private GmkMapper gmkMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private CommonService commonService;

    @Autowired
    private XmlUtility xmlUtility;

    CommonFunction commonFunction = new CommonFunction();

    // 숙박상품 생성
    public String createAccomm(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            // =============================
            // 시설 정보
            // =============================
            AccommDto accommDto = commonMapper.getAcmInfo(intAID, Constants.intGmkOmkIdx);
            if(accommDto != null){
                JSONObject requestJson = new JSONObject();

                JSONObject itemBasicInfo = new JSONObject();

                JSONObject goodsName = new JSONObject();
                String strSubject = accommDto.getStrSubject();

                if(strSubject.length() <= 50){
                    goodsName.put("kor", strSubject); // 검색용 국문 상품명
                    itemBasicInfo.put("goodsName", goodsName);

                    // =============================
                    // 카테고리
                    // =============================
                    JSONObject category = new JSONObject();

                    // 지마켓 카테고리 코드
                    List<JSONObject> site = new ArrayList<>();
                    JSONObject siteJson = new JSONObject();
                    siteJson.put("siteType", 2); // 2 : 지마켓

                    Map<String, String> categoryMap = gmkMapper.getCategories(intAID);
                    siteJson.put("catCode", categoryMap.get("strGmkCate3")); // 최하위(Leaf) 카테고리 코드

                    site.add(siteJson);
                    category.put("site", site);

                    // ESM 카테고리 코드
                    JSONObject esm = new JSONObject();
                    esm.put("catCode", categoryMap.get("strESMCate3"));
                    category.put("esm", esm);
                    category.put("shop", null);

                    itemBasicInfo.put("category", category);

                    itemBasicInfo.put("book", null);

                    // 브랜드코드 -> 필수값 아님. 있으면 넣기?
//                    JSONObject catalog = new JSONObject();
//                    catalog.put("brandNo", 0); // 브랜드코드 -> api로 조회 가능
//                    itemBasicInfo.put("catalog", catalog);

                    itemBasicInfo.put("goodsType", 1); // (G마켓용) 상품타입 1 : 일반 배송상품, 2: e쿠폰 상품
                    requestJson.put("itemBasicInfo", itemBasicInfo);

                    // =============================
                    // 가격 및 수량
                    // =============================
                    JSONObject itemAddtionalInfo = new JSONObject();

                    // 판매가격
                    JSONObject priceJson = new JSONObject();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    String strDate = dateFormat.format(date);

                    int minPrice = commonMapper.getMinPrice(intAID, strDate);
                    priceJson.put("Gmkt", minPrice);
                    itemAddtionalInfo.put("price", priceJson);

                    // 재고수량
                    JSONObject stockJson = new JSONObject();
                    stockJson.put("Gmkt", 1); // 옵션 등록시 옵션재고관리(true) 선택할 경우 본 수량 무시되고 옵션 재고합으로 산정
                    itemAddtionalInfo.put("stock", stockJson);

                    // 판매기간
                    // 입력 가능 기간 : 15, 30, 60, 90
                    // 수정 시 0 입력하면 기존 기간 유지
                    // TODO : 등록시 고정 판매기간 정해야함 -> 일단 최대일자로?
                    JSONObject sellingPeriod = new JSONObject();
                    sellingPeriod.put("Gmkt", 90);
                    itemAddtionalInfo.put("sellingPeriod", sellingPeriod);

                    // 판매자 상품코드(관리코드 or 자사몰 상품번호)
                    itemAddtionalInfo.put("managedCode", intAID);

                    itemAddtionalInfo.put("isGift", true);
                    itemAddtionalInfo.put("goodsStatus", 1);

                    // =============================
                    // 옵션
                    // =============================
                    JSONObject recommendedOpts = new JSONObject();
                    recommendedOpts.put("type", 2);
                    recommendedOpts.put("isStockManage", true);

                    JSONObject combination = new JSONObject();
                    // 추천옵션코드 0 : 직접입력
                    combination.put("recommendedOptNo1", 0);
                    combination.put("recommendedOptNo2", 0);

                    // 추천옵션명
                    JSONObject recommendedOptName1 = new JSONObject();
                    recommendedOptName1.put("koreanText", "사용일자");
                    combination.put("recommendedOptName1", recommendedOptName1);

                    JSONObject recommendedOptName2 = new JSONObject();
                    recommendedOptName2.put("koreanText", "타입");
                    combination.put("recommendedOptName2", recommendedOptName2);

                    JSONArray details = new JSONArray();
                    int intBasePriceChk = 0; // 대표가로 지정한 금액과 동일한 금액의 옵션이 포함되어 있는지 확인(없으면 등록불가)
                    // 옵션 정보 등록
                    List<StockDto> stockList = commonMapper.getStockList(intAID, Constants.intGmkOmkIdx, strDate);
                    for(StockDto stock : stockList){
                        JSONObject detailJson = new JSONObject();
                        detailJson.put("recommendedOptValueNo1", 0); // 추천옵션 항목코드
                        detailJson.put("recommendedOptValueNo2", 0); // 추천옵션 항목코드

                        JSONObject recommendedOptValue1 = new JSONObject();
                        recommendedOptValue1.put("koreanText", stock.getDateSales()); // 사용일자
                        detailJson.put("recommendedOptValue1", recommendedOptValue1);

                        JSONObject recommendedOptValue2 = new JSONObject();

                        String strRmtypeName = "";
                        if(!stock.getStrPkgName().isEmpty()){
                            strRmtypeName = stock.getStrRmtypeName() + "/" + stock.getStrPkgName();
                        }else{
                            strRmtypeName = stock.getStrRmtypeName();
                        }
                        recommendedOptValue2.put("koreanText", strRmtypeName); // 객실타입명

                        detailJson.put("recommendedOptValue2", recommendedOptValue2);
                        detailJson.put("manageCode", stock.getIntRmIdx());

                        detailJson.put("isSoldOut", false); // 옵션의 품절여부 제어(옵션 재고 수량으로 제어하지 않음) true : 품절, false : 판매
                        detailJson.put("isDisplay", true); // 옵션의 노출여부 제어

                        // 옵션 재고 수량
                        JSONObject qty = new JSONObject();
                        qty.put("gmkt", stock.getIntStock());
                        detailJson.put("qty", qty);

                        int intSales = stock.getMoneySales();
                        int extraPrice = intSales - minPrice;
                        if(extraPrice == 0){
                            intBasePriceChk +=1;
                        }
                        detailJson.put("addAmnt", extraPrice); // 주문옵션 추가금

                        details.add(detailJson);
                    }

                    if(intBasePriceChk == 0){
                        message = "대표가격과 동일한 금액의 옵션상품이 하나이상 존재하지 않습니다.";
                    }else{
                        combination.put("details", details);
                        recommendedOpts.put("combination", combination);

                        itemAddtionalInfo.put("recommendedOpts", recommendedOpts);

                        JSONObject orderOpts = new JSONObject();
                        orderOpts.put("type", 0);
                        itemAddtionalInfo.put("orderOpts", orderOpts);

                        // =============================
                        // 배송 정보
                        // =============================
                        // 배송방법 타입
                        JSONObject shipping = new JSONObject();
                        shipping.put("type", 1); // 1 : 택배, 2 : 직접배송 (지마켓 단독등록시 1만 가능)

                        // 택배사 코드 - 기타
                        shipping.put("companyNo", Constants.gmk_delivery_compnay_code);

                        // 배송비 타입
                        JSONObject shippingPolicy = new JSONObject();
                        shippingPolicy.put("feeType", 2);
                        shippingPolicy.put("placeNo", Constants.gmk_shipping_place); // 출하지 번호
                        JSONObject each = new JSONObject();
                        each.put("feeType", 1); // 1 : 무료, 2 : 유료, 3 : 조건부무료, 4 : 수량별차등
                        shippingPolicy.put("each", each);
                        shipping.put("policy", shippingPolicy);

                        JSONObject returnAndExchange = new JSONObject();
                        returnAndExchange.put("addrNo", Constants.gmk_seller_AddrNo); // 판매자 주소번호
                        returnAndExchange.put("fee", 0);
                        shipping.put("returnAndExchange", returnAndExchange);

                        // 발송정책번호
                        JSONObject dispatchPolicyNo = new JSONObject();
                        dispatchPolicyNo.put("gmkt", Constants.gmk_dispatch_policy_no); // 발송일미정
                        shipping.put("dispatchPolicyNo", dispatchPolicyNo);

                        shipping.put("backwoodsDeliveryYn", "Y");

                        itemAddtionalInfo.put("shipping", shipping); // 제주/도서산간배송불가 여부

                        // =============================
                        // 상품정보고시 정보
                        // =============================
                        // 상품정보고시 상품군코드
                        JSONObject officialNotice = new JSONObject();
                        officialNotice.put("officialNoticeNo", Constants.gmk_official_notice);

                        // 상품정보고시 항목코드
                        JSONArray officialNoticeDetails = new JSONArray();
                        String strMsg = "상세 설명 참조";
                        JSONObject onJson = new JSONObject();
                        onJson.put("officialNoticeItemelementCode", "27-1"); // 상품정보고시 항목코드 - 국가 또는 지역명
                        onJson.put("value", strMsg); // 상품정보고시 값
                        onJson.put("isExtraMark", false); // 상품정보고시 추가입력여부
                        officialNoticeDetails.add(onJson);

                        onJson = new JSONObject();
                        onJson.put("officialNoticeItemelementCode", "27-2"); // 상품정보고시 항목코드 - 숙소형태
                        onJson.put("value", strMsg); // 상품정보고시 값
                        onJson.put("isExtraMark", false); // 상품정보고시 추가입력여부
                        officialNoticeDetails.add(onJson);

                        onJson = new JSONObject();
                        onJson.put("officialNoticeItemelementCode", "27-3"); // 상품정보고시 항목코드 - 등급, 객실타입
                        onJson.put("value", strMsg); // 상품정보고시 값
                        onJson.put("isExtraMark", false); // 상품정보고시 추가입력여부
                        officialNoticeDetails.add(onJson);

                        onJson = new JSONObject();
                        onJson.put("officialNoticeItemelementCode", "27-4"); // 상품정보고시 항목코드 - 사용가능 인원, 인원 추가 시 비용
                        onJson.put("value", strMsg); // 상품정보고시 값
                        onJson.put("isExtraMark", false); // 상품정보고시 추가입력여부
                        officialNoticeDetails.add(onJson);

                        onJson = new JSONObject();
                        onJson.put("officialNoticeItemelementCode", "27-5"); // 상품정보고시 항목코드 - 부대시설/제공서비스
                        onJson.put("value", strMsg); // 상품정보고시 값
                        onJson.put("isExtraMark", false); // 상품정보고시 추가입력여부
                        officialNoticeDetails.add(onJson);

                        onJson = new JSONObject();
                        onJson.put("officialNoticeItemelementCode", "27-6"); // 상품정보고시 항목코드 - 취소규정
                        onJson.put("value", strMsg); // 상품정보고시 값
                        onJson.put("isExtraMark", false); // 상품정보고시 추가입력여부
                        officialNoticeDetails.add(onJson);

                        onJson = new JSONObject();
                        onJson.put("officialNoticeItemelementCode", "27-7"); // 상품정보고시 항목코드 - 예약담당 연락처
                        onJson.put("value", strMsg); // 상품정보고시 값
                        onJson.put("isExtraMark", false); // 상품정보고시 추가입력여부
                        officialNoticeDetails.add(onJson);

                        onJson = new JSONObject();
                        onJson.put("officialNoticeItemelementCode", "27-8"); // 상품정보고시 항목코드 - 주문후 예상 배송기간
                        onJson.put("value", strMsg); // 상품정보고시 값
                        onJson.put("isExtraMark", false); // 상품정보고시 추가입력여부
                        officialNoticeDetails.add(onJson);

                        onJson = new JSONObject();
                        onJson.put("officialNoticeItemelementCode", "999-5"); // 상품정보고시 항목코드 - 기타 특이사항
                        onJson.put("value", strMsg); // 상품정보고시 값
                        onJson.put("isExtraMark", false); // 상품정보고시 추가입력여부
                        officialNoticeDetails.add(onJson);

                        officialNotice.put("details", officialNoticeDetails);

                        itemAddtionalInfo.put("officialNotice", officialNotice);

                        // =============================
                        // 기타 정보
                        // =============================
                        // 청소년 구매불가
                        itemAddtionalInfo.put("isAdultProduct", false); // true : 성인상품, false : 일반상품

                        // 부가세여부
                        itemAddtionalInfo.put("isVatFree", true); // true : 부가세 면세상품, false : 부과세 과세상품

                        // (옥션용) 의료기기 인증 사용 여부
                        JSONObject certInfo = new JSONObject();
                        JSONObject certInfoJson = new JSONObject();
                        certInfoJson.put("certId", new JSONArray());
                        certInfoJson.put("licenseSeq", null);
                        certInfo.put("gmkt", certInfoJson);

                        // 통합 어린이인증 타입
                        JSONObject safetyCerts = new JSONObject();
                        JSONObject child = new JSONObject();
                        child.put("type", 1); // 0 : 인증대상, 1 : 인증대상아님, 2 : 상품상세별도표기
                        child.put("details", null);
                        safetyCerts.put("child", child);

                        // 통합 전기인증 타입
                        JSONObject electric = new JSONObject();
                        electric.put("type", 1); // 0 : 인증대상, 1 : 인증대상아님, 2 : 상품상세별도표기
                        electric.put("mandatorySafetySign", 0);
                        electric.put("details", null);
                        safetyCerts.put("electric", electric);

                        // 통합 생활용품인증 타입
                        JSONObject life = new JSONObject();
                        life.put("type", 1); // 0 : 인증대상, 1 : 인증대상아님, 2 : 상품상세별도표기
                        life.put("mandatorySafetySign", 0);
                        life.put("details", null);
                        safetyCerts.put("life", life);

                        JSONObject harmful = new JSONObject();
                        harmful.put("type", 1); // 0 : 인증대상, 1 : 인증대상아님, 2 : 상품상세별도표기
                        harmful.put("certId", null);
                        safetyCerts.put("harmful", harmful);


                        certInfo.put("safetyCerts", safetyCerts);
                        itemAddtionalInfo.put("certInfo", certInfo);

                        // =============================
                        // 상품 이미지
                        // =============================
                        // 상품 기본 이미지
                        JSONObject images = new JSONObject();
                        if(accommDto.getStrACMPhotos() != null){
                            String strAcmPhotos = accommDto.getStrACMPhotos();
                            String[] photoArr = strAcmPhotos.split("\\|");

                            for(int i=0; i< photoArr.length; i++){
                                // TODO : 추후 이미지 저장 경로 정해지면 수정 할 것
                                String imgUrl = "https://condo24.com";
                                if(i==0){
                                    images.put("basicImgURL", imgUrl + photoArr[i]);
                                }else if(i==14){ // 추가 이미지 1~14까지 가능
                                    images.put("addtionalImg" + i + "URL", imgUrl + photoArr[i]);
                                    break;
                                }else{
                                    images.put("addtionalImg" + i + "URL", imgUrl + photoArr[i]);
                                }
                            }
                        }
                        itemAddtionalInfo.put("images", images);

                        // =============================
                        // 상품 상세정보
                        // =============================
                        // 상품 상세정보 타입
                        JSONObject descriptions = new JSONObject();
                        JSONObject kor = new JSONObject();
                        kor.put("type", 2); // 1 : contentID(추후제공), 2 : html

                        String strPdtDtlInfo = "";
                        if(accommDto.getStrOMKDetailInfo() != null){
                            strPdtDtlInfo = accommDto.getStrOMKDetailInfo();
                        }else{
                            strPdtDtlInfo = commonService.getStrPdtDtlInfo(accommDto, intAID, 3);
                        }
                        kor.put("html", strPdtDtlInfo);
                        descriptions.put("kor", kor);
                        itemAddtionalInfo.put("descriptions", descriptions);

                        // =============================
                        // 기타 정보
                        // =============================
                        // 추가구성 사용여부
                        JSONObject addonService = new JSONObject();
                        addonService.put("addonServiceUseType", 0); // 0 : 사용하지 않음, 1 : 사용 - 재고관리X, 2 : 사용 - 재고관리O
                        itemAddtionalInfo.put("addonService", addonService);

                        requestJson.put("itemAddtionalInfo", itemAddtionalInfo);

                        JSONObject addtionalInfo = new JSONObject();

                        // 판매자할인 사용여부
                        JSONObject sellerDiscount  = new JSONObject();
                        sellerDiscount.put("isUse", false); // true : 할인 적용, false : 할인 미적용
                        addtionalInfo.put("sellerDiscount", sellerDiscount);

                        // 지마켓용 사이트부담 지원할인
                        JSONObject siteDiscount = new JSONObject();
                        siteDiscount.put("gmkt", true);
                        siteDiscount.put("iac", true);
                        addtionalInfo.put("siteDiscount", siteDiscount);

                        // 가격비교사이트 상품 노출 여부
                        JSONObject pcs = new JSONObject();
                        pcs.put("isUse", false); // 가격 비교 사이트 상품 노출 여부
                        addtionalInfo.put("pcs", pcs);

                        // 해외판매 여부
                        JSONObject overseaSales = new JSONObject();
                        overseaSales.put("isAgree", false);
                        addtionalInfo.put("overseaSales", overseaSales);

                        requestJson.put("addtionalInfo", addtionalInfo);

                        // api 호출
                        String authorization = HmacGenerater.generate("sell");
                        JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/goods", "POST", authorization, requestJson);

                        String resultCode = resultJson.get("resultCode").toString();
                        if(resultCode.equals("0")) {
                            String strPdtCode = resultJson.get("goodsNo").toString();
                            String strOmkSiteCode = resultJson.get("SiteGoodsNo").toString();

                            String insertResult = commonMapper.insertAcmOmk(intAID, Constants.intGmkOmkIdx, "Y", accommDto.getStrSubject(), strPdtCode, strOmkSiteCode, strPdtDtlInfo);
                            String strResult = insertResult.substring(insertResult.length()-4);

                            if(strResult.equals("저장완료")){
                                message = "상품 생성 완료";
                            }else{
                                message = "상품 코드 저장 실패";
                            }

                        }else{
                            String resultMsg = resultJson.get("message").toString();
                            logWriter.add(resultMsg);
                            message = "지마켓 api 호출 실패";
                        }


                    }
                }else{
                    message = "상품명이 50자 이상입니다";
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

    // 상품 수정
    // 등록 후 바로 상품 수정 호출 시 에러 발생하며 2~3분 뒤 수정 가능
    public String updateAccomm(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            // =============================
            // 시설 정보
            // =============================
            AccommDto accommDto = commonMapper.getAcmInfo(intAID, Constants.intGmkOmkIdx);
            if(accommDto != null){
                JSONObject requestJson = new JSONObject();

                JSONObject itemBasicInfo = new JSONObject();

                JSONObject goodsName = new JSONObject();
                String strSubject = accommDto.getStrSubject();

                if(strSubject.length() <= 50){
                    goodsName.put("kor", strSubject); // 검색용 국문 상품명
                    itemBasicInfo.put("goodsName", goodsName);

                    // =============================
                    // 카테고리
                    // =============================
                    JSONObject category = new JSONObject();

                    // 지마켓 카테고리 코드
                    List<JSONObject> site = new ArrayList<>();
                    JSONObject siteJson = new JSONObject();
                    siteJson.put("siteType", 2); // 2 : 지마켓

                    Map<String, String> categoryMap = gmkMapper.getCategories(intAID);
                    siteJson.put("catCode", categoryMap.get("strGmkCate3")); // 최하위(Leaf) 카테고리 코드

                    site.add(siteJson);
                    category.put("site", site);

                    // ESM 카테고리 코드
                    JSONObject esm = new JSONObject();
                    esm.put("catCode", categoryMap.get("strESMCate3"));
                    category.put("esm", esm);
                    category.put("shop", null);

                    itemBasicInfo.put("category", category);

                    itemBasicInfo.put("book", null);

                    // 브랜드코드 -> 필수값 아님. 있으면 넣기?
//                    JSONObject catalog = new JSONObject();
//                    catalog.put("brandNo", 0); // 브랜드코드 -> api로 조회 가능
//                    itemBasicInfo.put("catalog", catalog);

                    itemBasicInfo.put("goodsType", 1); // (G마켓용) 상품타입 1 : 일반 배송상품, 2: e쿠폰 상품
                    requestJson.put("itemBasicInfo", itemBasicInfo);

                    // =============================
                    // 가격 및 수량
                    // =============================
                    JSONObject itemAddtionalInfo = new JSONObject();

                    // 판매가격
                    JSONObject priceJson = new JSONObject();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    String strDate = dateFormat.format(date);

                    int minPrice = commonMapper.getMinPrice(intAID, strDate);
                    priceJson.put("Gmkt", minPrice);
                    itemAddtionalInfo.put("price", priceJson);

                    // 재고수량
                    JSONObject stockJson = new JSONObject();
                    stockJson.put("Gmkt", 1); // 옵션 등록시 옵션재고관리(true) 선택할 경우 본 수량 무시되고 옵션 재고합으로 산정
                    itemAddtionalInfo.put("stock", stockJson);

                    // 판매기간
                    // 입력 가능 기간 : 15, 30, 60, 90
                    // 수정 시 0 입력하면 기존 기간 유지
                    // TODO : 등록시 고정 판매기간 정해야함 -> 일단 최대일자로?
                    JSONObject sellingPeriod = new JSONObject();
                    sellingPeriod.put("Gmkt", 90);
                    itemAddtionalInfo.put("sellingPeriod", sellingPeriod);

                    // 판매자 상품코드(관리코드 or 자사몰 상품번호)
                    itemAddtionalInfo.put("managedCode", intAID);

                    itemAddtionalInfo.put("isGift", true);
                    itemAddtionalInfo.put("goodsStatus", 1);

                    // =============================
                    // 옵션
                    // =============================
                    JSONObject recommendedOpts = new JSONObject();
                    recommendedOpts.put("type", 2);
                    recommendedOpts.put("isStockManage", true);

                    JSONObject combination = new JSONObject();
                    // 추천옵션코드 0 : 직접입력
                    combination.put("recommendedOptNo1", 0);
                    combination.put("recommendedOptNo2", 0);

                    // 추천옵션명
                    JSONObject recommendedOptName1 = new JSONObject();
                    recommendedOptName1.put("koreanText", "사용일자");
                    combination.put("recommendedOptName1", recommendedOptName1);

                    JSONObject recommendedOptName2 = new JSONObject();
                    recommendedOptName2.put("koreanText", "타입");
                    combination.put("recommendedOptName2", recommendedOptName2);

                    JSONArray details = new JSONArray();
                    int intBasePriceChk = 0; // 대표가로 지정한 금액과 동일한 금액의 옵션이 포함되어 있는지 확인(없으면 등록불가)
                    // 옵션 정보 등록
                    List<StockDto> stockList = commonMapper.getStockList(intAID, Constants.intGmkOmkIdx, strDate);
                    for(StockDto stock : stockList){
                        JSONObject detailJson = new JSONObject();
                        detailJson.put("recommendedOptValueNo1", 0); // 추천옵션 항목코드
                        detailJson.put("recommendedOptValueNo2", 0); // 추천옵션 항목코드

                        JSONObject recommendedOptValue1 = new JSONObject();
                        recommendedOptValue1.put("koreanText", stock.getDateSales()); // 사용일자
                        detailJson.put("recommendedOptValue1", recommendedOptValue1);

                        JSONObject recommendedOptValue2 = new JSONObject();
                        recommendedOptValue2.put("koreanText", stock.getStrRmtypeName()); // 객실타입명
                        detailJson.put("recommendedOptValue2", recommendedOptValue2);
                        detailJson.put("manageCode", stock.getIntRmIdx());

                        detailJson.put("isSoldOut", false); // 옵션의 품절여부 제어(옵션 재고 수량으로 제어하지 않음) true : 품절, false : 판매
                        detailJson.put("isDisplay", true); // 옵션의 노출여부 제어

                        // 옵션 재고 수량
                        JSONObject qty = new JSONObject();
                        qty.put("gmkt", stock.getIntStock());
                        detailJson.put("qty", qty);

                        int intSales = stock.getMoneySales();
                        int extraPrice = intSales - minPrice;
                        if(extraPrice == 0){
                            intBasePriceChk +=1;
                        }
                        detailJson.put("addAmnt", extraPrice); // 주문옵션 추가금

                        details.add(detailJson);
                    }

                    if(intBasePriceChk == 0){
                        message = "대표가격과 동일한 금액의 옵션상품이 하나이상 존재하지 않습니다.";
                    }else{
                        combination.put("details", details);
                        recommendedOpts.put("combination", combination);

                        itemAddtionalInfo.put("recommendedOpts", recommendedOpts);

                        JSONObject orderOpts = new JSONObject();
                        orderOpts.put("type", 0);
                        itemAddtionalInfo.put("orderOpts", orderOpts);

                        // =============================
                        // 배송 정보
                        // =============================
                        // 배송방법 타입
                        JSONObject shipping = new JSONObject();
                        shipping.put("type", 1); // 1 : 택배, 2 : 직접배송 (지마켓 단독등록시 1만 가능)

                        // 택배사 코드 - 기타
                        shipping.put("companyNo", Constants.gmk_delivery_compnay_code);

                        // 배송비 타입
                        JSONObject shippingPolicy = new JSONObject();
                        shippingPolicy.put("feeType", 2);
                        shippingPolicy.put("placeNo", Constants.gmk_shipping_place); // 출하지 번호
                        JSONObject each = new JSONObject();
                        each.put("feeType", 1); // 1 : 무료, 2 : 유료, 3 : 조건부무료, 4 : 수량별차등
                        shippingPolicy.put("each", each);
                        shipping.put("policy", shippingPolicy);

                        JSONObject returnAndExchange = new JSONObject();
                        returnAndExchange.put("addrNo", Constants.gmk_seller_AddrNo); // 판매자 주소번호
                        returnAndExchange.put("fee", 0);
                        shipping.put("returnAndExchange", returnAndExchange);

                        // 발송정책번호
                        JSONObject dispatchPolicyNo = new JSONObject();
                        dispatchPolicyNo.put("gmkt", Constants.gmk_dispatch_policy_no); // 발송일미정
                        shipping.put("dispatchPolicyNo", dispatchPolicyNo);

                        shipping.put("backwoodsDeliveryYn", "Y");

                        itemAddtionalInfo.put("shipping", shipping); // 제주/도서산간배송불가 여부

                        // =============================
                        // 상품정보고시 정보
                        // =============================
                        // 상품정보고시 상품군코드
                        JSONObject officialNotice = new JSONObject();
                        officialNotice.put("officialNoticeNo", Constants.gmk_official_notice);

                        // 상품정보고시 항목코드
                        JSONArray officialNoticeDetails = new JSONArray();
                        String strMsg = "상세 설명 참조";
                        JSONObject onJson = new JSONObject();
                        onJson.put("officialNoticeItemelementCode", "27-1"); // 상품정보고시 항목코드 - 국가 또는 지역명
                        onJson.put("value", strMsg); // 상품정보고시 값
                        onJson.put("isExtraMark", false); // 상품정보고시 추가입력여부
                        officialNoticeDetails.add(onJson);

                        onJson = new JSONObject();
                        onJson.put("officialNoticeItemelementCode", "27-2"); // 상품정보고시 항목코드 - 숙소형태
                        onJson.put("value", strMsg); // 상품정보고시 값
                        onJson.put("isExtraMark", false); // 상품정보고시 추가입력여부
                        officialNoticeDetails.add(onJson);

                        onJson = new JSONObject();
                        onJson.put("officialNoticeItemelementCode", "27-3"); // 상품정보고시 항목코드 - 등급, 객실타입
                        onJson.put("value", strMsg); // 상품정보고시 값
                        onJson.put("isExtraMark", false); // 상품정보고시 추가입력여부
                        officialNoticeDetails.add(onJson);

                        onJson = new JSONObject();
                        onJson.put("officialNoticeItemelementCode", "27-4"); // 상품정보고시 항목코드 - 사용가능 인원, 인원 추가 시 비용
                        onJson.put("value", strMsg); // 상품정보고시 값
                        onJson.put("isExtraMark", false); // 상품정보고시 추가입력여부
                        officialNoticeDetails.add(onJson);

                        onJson = new JSONObject();
                        onJson.put("officialNoticeItemelementCode", "27-5"); // 상품정보고시 항목코드 - 부대시설/제공서비스
                        onJson.put("value", strMsg); // 상품정보고시 값
                        onJson.put("isExtraMark", false); // 상품정보고시 추가입력여부
                        officialNoticeDetails.add(onJson);

                        onJson = new JSONObject();
                        onJson.put("officialNoticeItemelementCode", "27-6"); // 상품정보고시 항목코드 - 취소규정
                        onJson.put("value", strMsg); // 상품정보고시 값
                        onJson.put("isExtraMark", false); // 상품정보고시 추가입력여부
                        officialNoticeDetails.add(onJson);

                        onJson = new JSONObject();
                        onJson.put("officialNoticeItemelementCode", "27-7"); // 상품정보고시 항목코드 - 예약담당 연락처
                        onJson.put("value", strMsg); // 상품정보고시 값
                        onJson.put("isExtraMark", false); // 상품정보고시 추가입력여부
                        officialNoticeDetails.add(onJson);

                        onJson = new JSONObject();
                        onJson.put("officialNoticeItemelementCode", "27-8"); // 상품정보고시 항목코드 - 주문후 예상 배송기간
                        onJson.put("value", strMsg); // 상품정보고시 값
                        onJson.put("isExtraMark", false); // 상품정보고시 추가입력여부
                        officialNoticeDetails.add(onJson);

                        onJson = new JSONObject();
                        onJson.put("officialNoticeItemelementCode", "999-5"); // 상품정보고시 항목코드 - 기타 특이사항
                        onJson.put("value", strMsg); // 상품정보고시 값
                        onJson.put("isExtraMark", false); // 상품정보고시 추가입력여부
                        officialNoticeDetails.add(onJson);

                        officialNotice.put("details", officialNoticeDetails);

                        itemAddtionalInfo.put("officialNotice", officialNotice);

                        // =============================
                        // 기타 정보
                        // =============================
                        // 청소년 구매불가
                        itemAddtionalInfo.put("isAdultProduct", false); // true : 성인상품, false : 일반상품

                        // 부가세여부
                        itemAddtionalInfo.put("isVatFree", true); // true : 부가세 면세상품, false : 부과세 과세상품

                        // (옥션용) 의료기기 인증 사용 여부
                        JSONObject certInfo = new JSONObject();
                        JSONObject certInfoJson = new JSONObject();
                        certInfoJson.put("certId", new JSONArray());
                        certInfoJson.put("licenseSeq", null);
                        certInfo.put("gmkt", certInfoJson);

                        // 통합 어린이인증 타입
                        JSONObject safetyCerts = new JSONObject();
                        JSONObject child = new JSONObject();
                        child.put("type", 1); // 0 : 인증대상, 1 : 인증대상아님, 2 : 상품상세별도표기
                        child.put("details", null);
                        safetyCerts.put("child", child);

                        // 통합 전기인증 타입
                        JSONObject electric = new JSONObject();
                        electric.put("type", 1); // 0 : 인증대상, 1 : 인증대상아님, 2 : 상품상세별도표기
                        electric.put("mandatorySafetySign", 0);
                        electric.put("details", null);
                        safetyCerts.put("electric", electric);

                        // 통합 생활용품인증 타입
                        JSONObject life = new JSONObject();
                        life.put("type", 1); // 0 : 인증대상, 1 : 인증대상아님, 2 : 상품상세별도표기
                        life.put("mandatorySafetySign", 0);
                        life.put("details", null);
                        safetyCerts.put("life", life);

                        JSONObject harmful = new JSONObject();
                        harmful.put("type", 1); // 0 : 인증대상, 1 : 인증대상아님, 2 : 상품상세별도표기
                        harmful.put("certId", null);
                        safetyCerts.put("harmful", harmful);


                        certInfo.put("safetyCerts", safetyCerts);
                        itemAddtionalInfo.put("certInfo", certInfo);

                        // =============================
                        // 상품 이미지
                        // =============================
                        // 상품 기본 이미지
                        JSONObject images = new JSONObject();
                        if(accommDto.getStrACMPhotos() != null){
                            String strAcmPhotos = accommDto.getStrACMPhotos();
                            String[] photoArr = strAcmPhotos.split("\\|");

                            for(int i=0; i< photoArr.length; i++){
                                // TODO : 추후 이미지 저장 경로 정해지면 수정 할 것
                                String imgUrl = "https://condo24.com";
                                if(i==0){
                                    images.put("basicImgURL", imgUrl + photoArr[i]);
                                }else if(i==14){ // 추가 이미지 1~14까지 가능
                                    images.put("addtionalImg" + i + "URL", imgUrl + photoArr[i]);
                                    break;
                                }else{
                                    images.put("addtionalImg" + i + "URL", imgUrl + photoArr[i]);
                                }
                            }
                        }
                        itemAddtionalInfo.put("images", images);

                        // =============================
                        // 상품 상세정보
                        // =============================
                        // 상품 상세정보 타입
                        JSONObject descriptions = new JSONObject();
                        JSONObject kor = new JSONObject();
                        kor.put("type", 2); // 1 : contentID(추후제공), 2 : html
                        String strPdtDtlInfo = "";
                        if(accommDto.getStrOMKDetailInfo() != null){
                            strPdtDtlInfo = accommDto.getStrOMKDetailInfo();
                        }else{
                            strPdtDtlInfo = commonService.getStrPdtDtlInfo(accommDto, intAID, 3);
                        }
                        kor.put("html", strPdtDtlInfo);
                        descriptions.put("kor", kor);
                        itemAddtionalInfo.put("descriptions", descriptions);

                        // =============================
                        // 기타 정보
                        // =============================
                        // 추가구성 사용여부
                        JSONObject addonService = new JSONObject();
                        addonService.put("addonServiceUseType", 0); // 0 : 사용하지 않음, 1 : 사용 - 재고관리X, 2 : 사용 - 재고관리O
                        itemAddtionalInfo.put("addonService", addonService);

                        requestJson.put("itemAddtionalInfo", itemAddtionalInfo);

                        JSONObject addtionalInfo = new JSONObject();

                        // 판매자할인 사용여부
                        JSONObject sellerDiscount  = new JSONObject();
                        sellerDiscount.put("isUse", false); // true : 할인 적용, false : 할인 미적용
                        addtionalInfo.put("sellerDiscount", sellerDiscount);

                        // 지마켓용 사이트부담 지원할인
                        JSONObject siteDiscount = new JSONObject();
                        siteDiscount.put("gmkt", true);
                        siteDiscount.put("iac", true);
                        addtionalInfo.put("siteDiscount", siteDiscount);

                        // 가격비교사이트 상품 노출 여부
                        JSONObject pcs = new JSONObject();
                        pcs.put("isUse", false); // 가격 비교 사이트 상품 노출 여부
                        addtionalInfo.put("pcs", pcs);

                        // 해외판매 여부
                        JSONObject overseaSales = new JSONObject();
                        overseaSales.put("isAgree", false);
                        addtionalInfo.put("overseaSales", overseaSales);

                        requestJson.put("addtionalInfo", addtionalInfo);

                        // =============================
                        // 상품 수정 시 추가 정보
                        // =============================
                        // 판매상태
                        JSONObject isSell = new JSONObject();
                        boolean isSellTF = true;
                        if(accommDto.getStrUsageYn().equals("N")){
                            isSellTF = false;
                        }
                        isSell.put("gmkt", isSellTF);
                        requestJson.put("isSell", isSell);

                        // api 호출
                        String authorization = HmacGenerater.generate("sell");
                        JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/goods/" + accommDto.getStrPdtCode(), "PUT", authorization, requestJson);

                        String resultCode = resultJson.get("resultCode").toString();
                        if(resultCode.equals("0")) {
                            message = "상품 수정 완료";
                        }else{
                            String resultMsg = resultJson.get("message").toString();
                            logWriter.add(resultMsg);
                            message = "지마켓 api 호출 실패";
                        }
                    }
                }else{
                    message = "상품명이 50자 이상입니다";
                }

            }else{
                message = "해당 시설 정보 불러오기 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "상품 수정 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 상품 삭제
    public String deleteAccomm(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        try{
            String strPdtCode = commonMapper.getStrPdtCode(intAID, Constants.intGmkOmkIdx);

            // api 호출
            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/goods/" + strPdtCode, "DELETE", authorization, null);

            String resultCode = resultJson.get("resultCode").toString();
            if(resultCode.equals("0")) {
                message = "상품 삭제 완료";
            }else{
                String resultMsg = resultJson.get("message").toString();
                logWriter.add(resultMsg);
                message = "지마켓 api 호출 실패";
            }
        }catch (Exception e){
            e.printStackTrace();
            message = "상품 삭제 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 가격, 재고, 판매상태 수정 api
    // 상품 등록 api 호출 3분 이후 변경 가능
    public String updatePriceStockStatus(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            AccommDto accommDto = commonMapper.getAcmInfo(intAID, Constants.intGmkOmkIdx);
            String strPdtCode = accommDto.getStrPdtCode();

            JSONObject requestJson = new JSONObject();

            // 판매상태
            JSONObject isSell = new JSONObject();
            boolean isSellTF = true;
            if(accommDto.getStrUsageYn().equals("N")){
                isSellTF = false;
            }
            isSell.put("gmkt", isSellTF); // true : 판매가능, false : 판매중지(판매중지 상태로 1개월 유지 시 상품 삭제), 판매중지 상태로 정보 호출 시 반영되지 않음
            requestJson.put("isSell", isSell);

            JSONObject itemBasicInfo = new JSONObject();

            // 판매가격
            JSONObject priceJson = new JSONObject();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String strDate = dateFormat.format(date);

            int minPrice = commonMapper.getMinPrice(intAID, strDate);
            priceJson.put("gmkt", minPrice);
            itemBasicInfo.put("price", priceJson);

            // 재고수량 - 옵션 등록 시 옵션재고관리(true)로 선택하면, 본판매수량은 입력해도 무시되고 옵션의 합산 재고로 산정됨
            JSONObject stockJson = new JSONObject();
            stockJson.put("gmkt", 1);
            itemBasicInfo.put("stock", stockJson);

            // 판매기간
            // TODO : 등록시 고정 판매기간 정해야함 -> 일단 최대일자로?
            JSONObject sellingPeriod = new JSONObject();
            sellingPeriod.put("gmkt", 90);
            itemBasicInfo.put("sellingPeriod", sellingPeriod );

            requestJson.put("itemBasicInfo", itemBasicInfo);

            // api 호출
            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/goods/" + strPdtCode + "/sell-status", "PUT", authorization, requestJson);

            String resultCode = resultJson.get("resultCode").toString();
            if(resultCode.equals("0")) {
                message = "가격/재고/판매상태 수정 완료";
            }else{
                String resultMsg = resultJson.get("message").toString();
                logWriter.add(resultMsg);
                message = "지마켓 api 호출 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "가격/재고/판매상태 수정 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 상품명 수정
    // 검색용 상품명은 10일 이내 판매이력이 없을 경우만 수정 가능, 그 이후에는 불가능
    // 수정 불가한 경우 별도에러 X, 기존 상품명 유지
    public String updateProductName(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            AccommDto accommDto = commonMapper.getAcmInfo(intAID, Constants.intGmkOmkIdx);
            String strPdtCode = accommDto.getStrPdtCode();

            if(accommDto != null){
                String kor = accommDto.getStrSubject(); // 검색용 상품명

                JSONObject requestJson = new JSONObject();
                requestJson.put("kor", kor);

                // api 호출
                String authorization = HmacGenerater.generate("");
                JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/goods/" + strPdtCode + "/goods-name", "PUT", authorization, requestJson);
                String resultCode = resultJson.get("resultCode").toString();

                if(resultCode.equals("0")){
                    message = "상품명 수정 완료";
                }else{
                    message = "지마켓 api 호출 실패";
                    String resultMsg = resultJson.get("message").toString();
                    logWriter.add(resultMsg);
                }
            }else{
                message = "해당 시설 정보 불러오기 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "상품명 수정 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 상품 이미지 수정
    // 추가이미지 14까지 등록/수정 가능
    public String updateAccommImages(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            String strPdtCode = commonMapper.getStrPdtCode(intAID, Constants.intGmkOmkIdx);

            JSONObject imageModel = new JSONObject();
            List<String> photoList = commonMapper.getPhotoList(intAID, 14);
            for(int i=0; i< photoList.size(); i++){
                if(i==0){
                    JSONObject basicImgJson = new JSONObject();
                    basicImgJson.put("URL", photoList.get(i));
                    imageModel.put("BasicImage", basicImgJson);
                }else{
                    JSONObject additionalImgJson = new JSONObject();
                    additionalImgJson.put("URL", photoList.get(i));
                    imageModel.put("AdditionalImage" + i, additionalImgJson);
                }
            }

            JSONObject requestJson = new JSONObject();
            requestJson.put("imageModel", imageModel);

            // api 호출
            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/goods/" + strPdtCode + "/images", "POST", authorization, requestJson);

            String resultCode = resultJson.get("resultCode").toString();
            if(resultCode.equals("0")){
                message = "상품 이미지 수정 완료";
            }else{
                message = "지마켓 api 호출 실패";
                String resultMsg = resultJson.get("message").toString();
                logWriter.add(resultMsg);
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "상품 이미지 수정 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 상품 상세설명 수정
    public String updateAccommDesc(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            AccommDto accommDto = commonMapper.getAcmInfo(intAID, Constants.intGmkOmkIdx);

            String strPdtCode = accommDto.getStrPdtCode();

            JSONObject requestJson = new JSONObject();

            String strPdtDtlInfo = "";
            if(accommDto.getStrOMKDetailInfo() != null){
                strPdtDtlInfo = accommDto.getStrOMKDetailInfo();
            }else{
                strPdtDtlInfo = commonService.getStrPdtDtlInfo(accommDto, intAID, Constants.intGmkOmkIdx);
            }
            requestJson.put("descNew", strPdtDtlInfo); // 상세설명(html)

            // api 호출
            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/goods/" + strPdtCode + "/descriptions", "POST", authorization, requestJson);

            if(resultJson.get("resultCode").toString().equals("0")){
                message = "상세설명 수정 완료";
            }else{
                String resultMsg = resultJson.get("message").toString();
                logWriter.add(resultMsg);
                message = "지마켓 api 호출 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "상세설명 수정 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 옵션 등록/수정
    public String updateAccommOption(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            JSONObject requestJson = new JSONObject();
            requestJson.put("type", 2);
            requestJson.put("isStockManage", true);

            JSONObject combination = new JSONObject();
            // 추천옵션코드 0 : 직접입력
            combination.put("recommendedOptNo1", 0);
            combination.put("recommendedOptNo2", 0);

            // 추천옵션명
            JSONObject recommendedOptName1 = new JSONObject();
            recommendedOptName1.put("koreanText", "사용일자");
            combination.put("recommendedOptName1", recommendedOptName1);

            JSONObject recommendedOptName2 = new JSONObject();
            recommendedOptName2.put("koreanText", "타입");
            combination.put("recommendedOptName2", recommendedOptName2);

            JSONArray details = new JSONArray();
            int intBasePriceChk = 0; // 대표가로 지정한 금액과 동일한 금액의 옵션이 포함되어 있는지 확인(없으면 등록불가)

            // 옵션 정보 등록
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String strDate = dateFormat.format(date);

            List<StockDto> stockList = commonMapper.getStockList(intAID, Constants.intGmkOmkIdx, strDate);
            for(StockDto stock : stockList){
                JSONObject detailJson = new JSONObject();
                detailJson.put("recommendedOptValueNo1", 0); // 추천옵션 항목코드
                detailJson.put("recommendedOptValueNo2", 0); // 추천옵션 항목코드

                JSONObject recommendedOptValue1 = new JSONObject();
                recommendedOptValue1.put("koreanText", stock.getDateSales()); // 사용일자
                detailJson.put("recommendedOptValue1", recommendedOptValue1);

                JSONObject recommendedOptValue2 = new JSONObject();
                recommendedOptValue2.put("koreanText", stock.getStrRmtypeName()); // 객실타입명
                detailJson.put("manageCode", stock.getIntRmIdx());

                detailJson.put("recommendedOptValue2", recommendedOptValue2);

                detailJson.put("isSoldOut", false); // 옵션의 품절여부 제어(옵션 재고 수량으로 제어하지 않음) true : 품절, false : 판매
                detailJson.put("isDisplay", true); // 옵션의 노출여부 제어

                // 옵션 재고 수량
                JSONObject qty = new JSONObject();
                qty.put("gmkt", stock.getIntStock());
                detailJson.put("qty", qty);

                int intSales = stock.getMoneySales();
                int minPrice = commonMapper.getMinPrice(intAID, strDate);
                int extraPrice = intSales - minPrice;
                if(extraPrice == 0){
                    intBasePriceChk +=1;
                }
                detailJson.put("addAmnt", extraPrice); // 주문옵션 추가금

                details.add(detailJson);
            }

            combination.put("details", details);
            requestJson.put("combination", combination);

            if(intBasePriceChk == 0){
                message = "대표가격과 동일한 금액의 옵션상품이 하나이상 존재하지 않습니다.";
            }else{
                // api 호출
                String authorization = HmacGenerater.generate("sell");

                String strPdtCode = commonMapper.getStrPdtCode(intAID, Constants.intGmkOmkIdx);
                JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/goods/" + strPdtCode + "/recommended-options", "PUT", authorization, requestJson);

                if(resultJson.get("resultCode").toString().equals("0")){
                    message = "옵션 등록/수정 완료";
                }else{
                    String resultMsg = resultJson.get("message").toString();
                    logWriter.add(resultMsg);
                    message = "지마켓 api 호출 실패";
                }
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "옵션 등록/수정 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 상품 조회
    public String getAccommInfo(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        JSONObject result = new JSONObject();
        try{
            String strPdtCode = commonMapper.getStrPdtCode(intAID, Constants.intGmkOmkIdx);

            // api 호출
            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/goods/" + strPdtCode, "GET", authorization, null);

            if(resultJson.get("resultCode") == null){
                message = "상품 조회 완료";
                result = resultJson;
            }else{
                String resultMsg = resultJson.get("message").toString();
                logWriter.add(resultMsg);
                message = "지마켓 api 호출 실패";
            }
        }catch (Exception e){
            e.printStackTrace();
            message = "상품 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message, result);
    }

    // 가격/재고/판매상태 조회
    public String getPriceStockStatus(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        JSONObject result = new JSONObject();
        try{
            AccommDto accommDto = commonMapper.getAcmInfo(intAID, Constants.intGmkOmkIdx);
            String strPdtCode = accommDto.getStrPdtCode();

            // api 호출
            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/goods/" + strPdtCode + "/sell-status", "GET", authorization, null);

            if(resultJson.get("resultCode") == null){
                message = "가격/재고/판매상태 조회 완료";
                result = resultJson;
            }else{
                String resultMsg = resultJson.get("message").toString();
                logWriter.add(resultMsg);
                message = "지마켓 api 호출 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "가격/재고/판매상태 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message, result);
    }

    // 옵션 조회
    public String getAccommOption(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        JSONObject result = new JSONObject();
        try{
            String strPdtCode = commonMapper.getStrPdtCode(intAID, Constants.intGmkOmkIdx);

            // api 호출
            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/goods/" + strPdtCode + "/recommended-options", "GET", authorization, null);

            if(resultJson.get("resultCode") == null){
                message = "옵션 조회 완료";
                result = resultJson;
            }else{
                String resultMsg = resultJson.get("message").toString();
                logWriter.add(resultMsg);
                message = "지마켓 api 호출 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "옵션 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message, result);
    }

    // 판매자할인(사이트별 판매자부담 할인) 등록/수정
    public String updateSellerDiscount(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        
        try{
            String strPdtCode = commonMapper.getStrPdtCode(intAID, Constants.intGmkOmkIdx);

            JSONObject requestJson = new JSONObject();

            JSONObject sellerDiscount = new JSONObject();
            sellerDiscount.put("isUse", false); // 판매자할인 사용여부

            JSONObject gmkt = new JSONObject();
            gmkt.put("type", 0); // 0 : 사용안함, 1 : 정액, 2 : 정률

            // TODO : 판매자할인 어떻게 진행할건지?
//            gmkt.put("priceOrRate1", ); // 할인액(율), 최소 100원 이상. 10원단위 입력, 판매가대비 70%까지 허용
//            gmkt.put("priceOrRate2", ); // 권한이 있는 셀러만 사용 가능. 최소 100원 이상, 10원단위 입력, 판매가대비 70%까지 허용
//            gmkt.put("startDate", ); // 할인 시작일자(YYYY-MM-DD)
//            gmkt.put("endDate", ); // 할인 종료일자(YYYY-MM-DD)
            sellerDiscount.put("gmkt", gmkt);

            requestJson.put("sellerDiscount", sellerDiscount);

            // api 호출
            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/goods/" + strPdtCode + "/seller-discounts", "POST", authorization, requestJson);

            if(resultJson.get("resultCode").toString().equals("0")){
                message = "판매자할인 등록/수정 완료";
            }else{
                String resultMsg = resultJson.get("message").toString();
                logWriter.add(resultMsg);
                message = "지마켓 api 호출 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "판매자할인 등록/수정 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 판매자할인(사이트별 판매자부담 할인) 해제
    public String deleteSellerDiscount(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            String strPdtCode = commonMapper.getStrPdtCode(intAID, Constants.intGmkOmkIdx);

            // api 호출
            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/goods/" + strPdtCode + "/seller-discounts", "DELETE", authorization, null);

            if(resultJson.get("resultCode").toString().equals("0")){
                message = "판매자할인 해제 완료";
            }else{
                String resultMsg = resultJson.get("message").toString();
                logWriter.add(resultMsg);
                message = "지마켓 api 호출 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "판매자할인 해제 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 판매자할인(사이트별 판매자부담 할인) 조회
    public String getSellerDiscount(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        JSONObject result = new JSONObject();
        try{
            String strPdtCode = commonMapper.getStrPdtCode(intAID, Constants.intGmkOmkIdx);

            // api 호출
            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/goods/" + strPdtCode + "/seller-discounts", "GET", authorization, null);

            if(resultJson.get("resultCode") == null){
                message = "판매자할인 조회 완료";
                result = resultJson;
            }else{
                String resultMsg = resultJson.get("message").toString();
                logWriter.add(resultMsg);
                message = "지마켓 api 호출 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "판매자할인 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message, result);
    }

    // 상품번호 조회 - 마스터번호 기준으로 site번호 조회
    public String getOmkSiteCode(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        JSONObject result = new JSONObject();
        try{
            String strPdtCode = commonMapper.getStrPdtCode(intAID, Constants.intGmkOmkIdx);

            // api 호출
            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/goods/" + strPdtCode + "/status", "GET", authorization, null);

            if(resultJson.get("resultCode") == null){
                message = "상품 site번호 조회 완료";
                result = resultJson;
            }else{
                String resultMsg = resultJson.get("message").toString();
                logWriter.add(resultMsg);
                message = "지마켓 api 호출 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "상품 site번호 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message, result);
    }

    // 상품번호 조회 - site번호 기준으로 마스터번호 조회
    public String getPdtCode(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        JSONObject result = new JSONObject();
        try{
            String strOmkSiteCode = gmkMapper.getOmkSiteCode(intAID, Constants.intGmkOmkIdx);

            // api 호출
            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/site-goods/" + strOmkSiteCode + "/goods-no", "GET", authorization, null);

            if(resultJson.get("resultCode") == null){
                message = "상품 마스터번호 조회 완료";
                result = resultJson;
            }else{
                String resultMsg = resultJson.get("message").toString();
                logWriter.add(resultMsg);
                message = "지마켓 api 호출 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "상품 마스터번호 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message, result);
    }



















    // 실시간 가격, 재고 체크(지마켓에서 호출)
    public String getPriceNStock(HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
//        String statusCode = "200";
//        String message = "";
        String strXml = "";
        try{
//            InputStream inputStream = httpServletRequest.getInputStream();
//            BufferedReader br = null;
//            StringBuilder stringBuilder = new StringBuilder();
//            String line = "";
//            if (inputStream != null) {
//                br = new BufferedReader(new InputStreamReader(inputStream));
//                while ((line = br.readLine()) != null) {
//                    stringBuilder.append(line);
//                }
//
//                String strBody = stringBuilder.toString();
//            }else{
//
//            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(httpServletRequest.getInputStream());
            document.getDocumentElement().normalize();

            System.out.println("1 : " + xmlUtility.parsingXml(document));

            NodeList orderInfo = document.getElementsByTagName("ORDER_INFO");
            Node node = orderInfo.item(0);
            Element element = (Element) node;
            System.out.println("2 : " + xmlUtility.getTagValue("PRODUCT", element));

//            NodeList nlList = element.getElementsByTagName(tag).item(0).getChildNodes();
//            Node nValue = (Node) nlList.item(0);
//            return nValue.getNodeValue();

            String responseXml =
                "<STOCK_REMAIN_INFO>\n" +
//                "    <PRODUCT NO="123456789" REMAIN_YN="Y" />\n" +
                "    <PRODUCT NO='123456789' REMAIN_YN='Y'/>\n" +
                "    <ORDER_OPTION>\n" +
                "        <OPTION_INFO>\n" +
                "            <NAME><![CDATA[사이즈]]></NAME>\n" +
                "            <VALUE><![CDATA[55]]></VALUE>\n" +
                "            <REMAIN_YN>Y</REMAIN_YN>\n" +
                "            <STOCK_NO>00001</STOCK_NO>\n" +
                "        </OPTION_INFO>\n" +
                "    </ORDER_OPTION>\n" +
                "</STOCK_REMAIN_INFO>\n";

            System.out.println("responseXml : \n" + responseXml);

        }catch (Exception e){
            e.printStackTrace();
//            message = "실시간 가격 재고 조회 실패";
//            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return strXml;
    }


    //==================================================================================================================


    // 지마켓 카테고리조회
    public String getGmkCategory(){
        String result = "";
        try{
            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/categories/site-cats", "GET", authorization, null);

            if(resultJson.get("resultCode") == null){
                result = resultJson.toString();
            }else{
                result = resultJson.get("message").toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    // 지마켓 카테고리조회
    public String getEsmCategory(){
        String result = "";
        try{
            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/categories/sd-cats/0", "GET", authorization, null);

            if(resultJson.get("resultCode") == null){
                result = resultJson.toString();
            }else{
                result = resultJson.get("message").toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    // Stie-ESM 카테고리 매칭조회
    public String getSiteEsmCategory(){
        String result = "";
        try{
            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/categories/sd-cats/00320005000000000000/site-cats", "GET", authorization, null);

            if(resultJson.get("resultCode") == null){
                result = resultJson.toString();
            }else{
                result = resultJson.get("message").toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    // 미니샵 카테고리 조회
    public String getMiniShopCategory(){
        String result = "";
        try{
            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/categories/shop-cats/", "GET", authorization, null);

            if(resultJson.get("resultCode") == null){
                result = resultJson.toString();
            }else{
                result = resultJson.get("message").toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    // 브랜드코드 카테고리 조회 - 브랜드명으로 조회
    public String getBrandCodeCategory(String strBrandName){
        String result = "";
        try{
            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/categories/shop-cats/", "GET", authorization, null);

            if(resultJson.get("resultCode") == null){
                result = resultJson.toString();
            }else{
                result = resultJson.get("message").toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    // 판매자주소록 등록
    public String sellerAddrRegist(){
        String result = "";
        try{
            JSONObject requestJson = new JSONObject();
            requestJson.put("addrName", "동무 주소록등록 test");
            requestJson.put("representativeName", "(주)동무해피데이즈"); // 고객에게 반품수취인으로 노출되는 판매자명
            requestJson.put("zipCode", "04149");
            requestJson.put("addr1", "서울특별시 마포구 백범로 116"); // 우편번호 기준 주소
            requestJson.put("addr2", "동무마포타워 3층"); // 주소 상세
            requestJson.put("homeTel", "1588-0134");
            requestJson.put("cellPhone", "010-6536-2403");
            requestJson.put("isVisitAndTakeAddr", "true"); // 기본 방문수령지여부
            requestJson.put("isReturnAddr", "true"); // 기본 반품배송지 주소여부
            

            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/sellers/address", "POST", authorization, null);

            if(resultJson.get("resultCode") == null){
                result = "판매자주소록 등록 완료";
            }else{
                result = resultJson.get("message").toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    // 판매자주소록 수정
    public String sellerAddrUpdate(){
        String result = "";
        try{
            String strAddrNo = "";

            JSONObject requestJson = new JSONObject();
            requestJson.put("addrName", "동무 주소록등록 test");
            requestJson.put("representativeName", "(주)동무해피데이즈"); // 고객에게 반품수취인으로 노출되는 판매자명
            requestJson.put("zipCode", "04149");
            requestJson.put("addr1", "서울특별시 마포구 백범로 116"); // 우편번호 기준 주소
            requestJson.put("addr2", "동무마포타워 3층"); // 주소 상세
            requestJson.put("homeTel", "1588-0134");
            requestJson.put("cellPhone", "010-6536-2403");
            requestJson.put("isVisitAndTakeAddr", "true"); // 기본 방문수령지여부
            requestJson.put("isReturnAddr", "true"); // 기본 반품배송지 주소여부


            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/sellers/address/" + strAddrNo, "PUT", authorization, requestJson);

            if(resultJson.get("resultCode") == null){
                result = "판매자주소록 수정 완료";
            }else{
                result = resultJson.get("message").toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    // 판매자주소록 조회
    public String getSellerAddr(){
        String result = "";
        try{
            String strAddrNo = "";

            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/sellers/address" + strAddrNo, "GET", authorization, null);

            if(resultJson.get("resultCode") == null){
                result = resultJson.toString();
            }else{
                result = resultJson.get("message").toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    // 판매자주소록 전체조회
    public String getSellerAddrList(){
        String result = "";
        try{
            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/sellers/addresses", "GET", authorization, null);

            if(resultJson.get("resultCode") == null){
                result = resultJson.toString();
            }else{
                result = resultJson.get("message").toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    // 카테고리별 추천옵션 조회
    public String getRecOption(){
        String result = "";
        try{
            // api 호출
            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/options/recommended-opts?catCode=300023931" , "GET", authorization, null);

            if(resultJson.get("resultCode") == null){
                result = resultJson.toString();
            }else{
                result = resultJson.get("message").toString();
            }
        }catch (Exception e){
            e.printStackTrace();

        }
        return result;
    }

    // 추천옵션별 선택항목 조회
    public String getSelectOption(){
        String result = "";
        try{
            // api 호출
            String authorization = HmacGenerater.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "item/v1/options/recommended-opts/976" , "GET", authorization, null);

            if(resultJson.get("resultCode") == null){
                result = resultJson.toString();
            }else{
                result = resultJson.get("message").toString();
            }
        }catch (Exception e){
            e.printStackTrace();

        }
        return result;
    }


}
