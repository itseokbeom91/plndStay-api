package com.example.stay.openMarket.gmarket.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.common.util.XmlUtility;
import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.dto.ContentsPhotoDto;
import com.example.stay.openMarket.common.dto.RoomTypeDto;
import com.example.stay.openMarket.common.dto.StockDto;
import com.example.stay.openMarket.common.mapper.CommonMapper;
import com.example.stay.openMarket.common.service.CommonService;
import com.example.stay.openMarket.gmarket.hmac.HmacGenerater;
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
import java.io.InputStream;
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

    public String getCategory(){
        String result = "";
        try{
            String authorization = HmacGenerater.generate("sell", "G");
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, new JSONObject(), Constants.gmkUrl + "item/v1/categories/sd-cats/00250002000000000000", "GET");
//            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, new JSONObject(), Constants.gmkUrl + "item/v1/categories/site-cats", "GET");

//            String catCode = jsonNode.get("catCode").toString();
//            String catName = jsonNode.get("catName").toString();

            result = jsonNode.toPrettyString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    // 숙박상품 생성
    public String createAccomm(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            // 숙박상품 생성에 필요한 데이터 가져오기
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
                    goodsName.put("eng", null); // 영문 상품명
                    goodsName.put("chi", null); // 중문 상품명
                    goodsName.put("jpn", null); // 일문 상품명
                    goodsName.put("promotion", ""); // 프로모션용 국문 상품명
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
                    JSONObject catalog = new JSONObject();
                    catalog.put("brandNo", 0); // 브랜드코드 -> api로 조회 가능
                    catalog.put("modelName", null);
                    catalog.put("barCode", null);
                    catalog.put("epinCode", null);
                    itemBasicInfo.put("catalog", catalog);

//                    itemBasicInfo.put("is3PL", false);
                    itemBasicInfo.put("goodsType", 1); // (G마켓용) 상품타입 1 : 일반 배송상품, 2: e쿠폰 상품
                    requestJson.put("itemBasicInfo", itemBasicInfo);

                    // =============================
                    // 가격 및 수량
                    // =============================
                    JSONObject itemAddtionalInfo = new JSONObject();

                    itemAddtionalInfo.put("buyableQuantity", null);
                    JSONObject buyableQuantityPolicy = new JSONObject();
                    buyableQuantityPolicy.put("BuyableUnitCount", null);
                    itemAddtionalInfo.put("buyableQuantityPolicy", buyableQuantityPolicy);

                    // 판매가격
                    JSONObject priceJson = new JSONObject();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    String strDate = dateFormat.format(date);

                    int minPrice = commonMapper.getMinPrice(intAID, strDate);
                    priceJson.put("Gmkt", minPrice);
                    priceJson.put("Iac", null);
                    itemAddtionalInfo.put("price", priceJson);

                    // 재고수량
                    JSONObject stockJson = new JSONObject();
                    stockJson.put("Gmkt", 1); // 옵션 등록시 옵션재고관리(true) 선택할 경우 본 수량 무시되고 옵션 재고합으로 산정
                    stockJson.put("Iac", 1);
                    itemAddtionalInfo.put("stock", stockJson);

                    // 판매기간
                    // 입력 가능 기간 : 15, 30, 60, 90
                    // 수정 시 0 입력하면 기존 기간 유지
                    // TODO : 등록시 고정 판매기간 정해야함 -> 일단 최대일자로?
                    JSONObject sellingPeriod = new JSONObject();
                    sellingPeriod.put("Gmkt", 90);
                    sellingPeriod.put("Iac", null);
                    itemAddtionalInfo.put("sellingPeriod", sellingPeriod);

                    // 판매자 상품코드(관리코드 or 자사몰 상품번호)
                    itemAddtionalInfo.put("managedCode", intAID);

                    itemAddtionalInfo.put("inventoryCode", null);
                    itemAddtionalInfo.put("sellerShop", null);
                    itemAddtionalInfo.put("expiryDate", null);
                    itemAddtionalInfo.put("manufacturedDate", null);
                    itemAddtionalInfo.put("origin", null);
                    itemAddtionalInfo.put("capacity", null);
                    itemAddtionalInfo.put("weight", 0);
                    itemAddtionalInfo.put("skuInfo", null);
                    itemAddtionalInfo.put("goodsStatus", 1);
                    itemAddtionalInfo.put("preSaleShippingDate", null);
                    itemAddtionalInfo.put("isGift", true);

                    // =============================
                    // 옵션
                    // =============================
                    // 추천옵션 -> ? 주문 옵션이 있는데 왜 추천옵션이 필수값인지....
                    // 2.0 에서는 추천옵션을 기본으로 등록/수정 할 수 있기 때문에 recommendedOpts 을 사용 해주시면 됩니다. orderOpts 경우 구옵션 등록/수정 에서만 사용가능 합니다.
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

//                    // 주문옵션 -> 여기에 우리 옵션이 들어가야하는건가..
//                    // 주문옵션의 옵션명은 한글기준 25자까지
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
                        shippingPolicy.put("placeNo", 716363); // 출하지 번호
                        JSONObject each = new JSONObject();
                        each.put("feeType", 1); // 1 : 무료, 2 : 유료, 3 : 조건부무료, 4 : 수량별차등
                        each.put("feePayType", 0); // 상품별 배송비 타입
                        each.put("fee", 0); // 상품별 배송비 지불방법
                        each.put("details", new JSONArray());
                        shippingPolicy.put("each", each);
//                        shippingPolicy.put("is3plDeliveryFeeFree", null);
                        shipping.put("policy", shippingPolicy);

                        JSONObject returnAndExchange = new JSONObject();
                        returnAndExchange.put("addrNo", "1113012");
                        returnAndExchange.put("shippingCompany", "0008");
                        returnAndExchange.put("fee", 0);
                        shipping.put("returnAndExchange", returnAndExchange);

                        // 발송정책번호
                        JSONObject dispatchPolicyNo = new JSONObject();
                        dispatchPolicyNo.put("gmkt", Constants.gmk_dispatch_policy_no); // 발송일미정
                        shipping.put("dispatchPolicyNo", dispatchPolicyNo);

                        shipping.put("generalPost", null);
                        shipping.put("visitAndTake", null);
                        shipping.put("quickService", null);
                        shipping.put("backwoodsDeliveryYn", "Y");

                        itemAddtionalInfo.put("shipping", shipping);

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
                        certInfo.put("iac", null);
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
                        kor.put("contentId", ""); // 상품 상세정보 타입 1일 경우 필수
                        String strPdtDtlInfo = commonService.getStrPdtDtlInfo(accommDto, intAID, 3);
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

                        itemAddtionalInfo.put("eCoupon", null);

                        JSONObject installJson = new JSONObject();
                        installJson.put("IsInstall", false);
                        installJson.put("InstallMakerId", 0);
                        installJson.put("InstallModelCode", null);
                        itemAddtionalInfo.put("install", installJson);

                        requestJson.put("itemAddtionalInfo", itemAddtionalInfo);

                        JSONObject addtionalInfo = new JSONObject();

                        // 판매자할인 사용여부
                        JSONObject sellerDiscount  = new JSONObject();
                        sellerDiscount.put("isUse", false); // true : 할인 적용, false : 할인 미적용
                        sellerDiscount.put("gmkt", null);
                        sellerDiscount.put("iac", null);
                        addtionalInfo.put("sellerDiscount", sellerDiscount);

                        // 지마켓용 사이트부담 지원할인
                        JSONObject siteDiscount = new JSONObject();
                        siteDiscount.put("gmkt", true);
                        siteDiscount.put("iac", true);
                        addtionalInfo.put("siteDiscount", siteDiscount);

                        addtionalInfo.put("gift", null);

                        // 가격비교사이트 상품 노출 여부
                        JSONObject pcs = new JSONObject();
                        pcs.put("isUse", false); // 가격 비교 사이트 상품 노출 여부
                        pcs.put("isUseGmkPcsCoupon", false); // 가격 비교 사이트 쿠폰 적용 여부 - 사용불가
                        pcs.put("isUseIacPcsCoupon", false); // 가격 비교 사이트 쿠폰 적용 여부 - 사용불가
                        addtionalInfo.put("pcs", pcs);

                        // 해외판매 여부
                        JSONObject overseaSales = new JSONObject();
                        overseaSales.put("isAgree", false);
                        addtionalInfo.put("overseaSales", overseaSales);

                        requestJson.put("addtionalInfo", addtionalInfo);

                        // api 호출
                        String authorization = HmacGenerater.generate("sell", "G");
                        JSONObject resultJson = callGmkApi(Constants.gmkUrl + "item/v1/goods", "POST", authorization, requestJson);

                        String code = resultJson.get("resultCode").toString();
                        if(code.equals("0")) {
                            String strPdtCode = resultJson.get("goodsNo").toString();

                            String insertResult = commonMapper.insertAcmOmk(intAID, Constants.intGmkOmkIdx, "Y", accommDto.getStrSubject(), strPdtCode, strPdtDtlInfo);
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

    public JSONObject callGmkApi(String strUrl, String method, String authorization, JSONObject requestJson){
        LogWriter logWriter = new LogWriter(method, strUrl, System.currentTimeMillis());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JSONObject responseJson = new JSONObject();
        try{
            URL url = new URL(strUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Authorization", authorization);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            if(!requestJson.isEmpty()){
                conn.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                writer.write(requestJson.toJSONString());
                writer.close();

                logWriter.addRequest(gson.toJson(requestJson));
            }

            String strJson = "";
            BufferedReader br = null;
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            }else{
                logWriter.add("responseCode : " + conn.getResponseCode());
                if(conn.getErrorStream() == null){
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                }else{
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
                }
            }
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            strJson = sb.toString();

            JSONParser jsonParser = new JSONParser();
            Object obj = jsonParser.parse(strJson);
            responseJson = (JSONObject) obj;

            conn.disconnect();

            logWriter.add(gson.toJson(responseJson));
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return responseJson;
    }

    // 상품명 수정
    // 검색용 상품명은 10일 이내 판매이력이 없을 경우만 수정 가능, 그 이후에는 불가능
    // 수정 불가한 경우 별도에러 X, 기존 상품명 유지
    public String updateAccommName(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            AccommDto accommDto = commonMapper.getAcmInfo(intAID, Constants.intGmkOmkIdx);
            String goodsNo = accommDto.getStrPdtCode();

            if(accommDto != null){
                String kor = accommDto.getStrSubject(); // 검색용 상품명
                String promotion = ""; // 프로모션 상품명(필수X)

                JSONObject requestJson = new JSONObject();
                requestJson.put("kor", kor);
                requestJson.put("promotion", promotion);

                // api 호출
                String authorization = HmacGenerater.generate("", "G");
                JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, requestJson, Constants.gmkUrl + "item/v1/goods/" + goodsNo + "/goods-name", "put");
                String code = jsonNode.get("resultCode").toString();
                String resultMsg = jsonNode.get("message").toString();

                if(code.equals("0")){
                    message = "상품명 수정 완료";
                }else{
                    message = "지마켓 api 호출 실패";
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
            String goodsNo = commonMapper.getStrPdtCode(intAID, Constants.intGmkOmkIdx);

            JSONObject imageModel = new JSONObject();
            List<String> photoList = commonMapper.getPhotoList(intAID, 14);
            for(int i=0; i< photoList.size(); i++){
                if(i==0){
                    imageModel.put("BasicImage", photoList);
                }else{
                    imageModel.put("AdditionalImage" + i, photoList);
                }
            }

            JSONObject requestJson = new JSONObject();
            requestJson.put("imageModel", imageModel);

            // api 호출
            String authorization = HmacGenerater.generate("", "G");
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, requestJson, Constants.gmkUrl + "item/v1/goods/" + goodsNo + "/images" + goodsNo + "/goods-name", "post");
            String code = jsonNode.get("resultCode").toString();
            String resultMsg = jsonNode.get("message").toString();

            if(code.equals("0")){
                message = "상품 이미지 수정 완료";
            }else{
                message = "지마켓 api 호출 실패";
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

            String goodsNo = accommDto.getStrPdtCode();

            // TODO : strShortDesc / strDescription / html?? 뭐로 할지?
            String strDescription = accommDto.getStrDescription();

            JSONObject requestJson = new JSONObject();

            String strHtmlDesc = commonService.getStrPdtDtlInfo(accommDto, intAID, 3);
            requestJson.put("descNew", strHtmlDesc); // 상세설명(html)

            // api 호출
            String authorization = HmacGenerater.generate("", "G");
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, requestJson, Constants.gmkUrl + "item/v1/goods/" + goodsNo + "/descriptions" + goodsNo + "/images" + goodsNo + "/goods-name", "post");
            String code = jsonNode.get("resultCode").toString();
            String resultMsg = jsonNode.get("message").toString();

            if(code.equals("0")){
                message = "상품 이미지 수정 완료";
            }else{
                message = "지마켓 api 호출 실패";
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

    // 가격, 재고, 판매상태 수정 api
    // 상품 등록 api 호출 3분 이후 변경 가능
    public String updatePriceStockStatus(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            String goodsNo = commonMapper.getStrPdtCode(intAID, Constants.intGmkOmkIdx);

            JSONObject requestJson = new JSONObject();

            // 판매상태
            JSONObject isSell = new JSONObject();
//            isSell.put("gmkt", ); // true : 판매가능, false : 판매중지(판매중지 상태로 1개월 유지 시 상품 삭제)
//            isSell.put("iac", );
            requestJson.put("isSell", isSell);

            JSONObject itemBasicInfo = new JSONObject();

            // 판매가격
            JSONObject priceJson = new JSONObject();
            double price = commonMapper.getOmkSales(intAID, Constants.intGmkOmkIdx);
            priceJson.put("gmkt", price);
            itemBasicInfo.put("price", priceJson);

            // 재고수량 - 옵션 등록 시 옵션재고관리(true)로 선택하면, 본판매수량은 입력해도 무시되고 옵션의 합산 재고로 산정됨
            JSONObject stockJson = new JSONObject();
//            stockJson.put("gmkt", );
            itemBasicInfo.put("stock", stockJson);

            // 판매기간
            JSONObject sellingPeriod = new JSONObject();
//            sellingPeriod.put("gmkt", );
            itemBasicInfo.put("sellingPeriod ", sellingPeriod );

            requestJson.put("itemBasicInfo", itemBasicInfo);

            // api 호출
            String authorization = HmacGenerater.generate("", "G");
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, requestJson, Constants.gmkUrl + "item/v1/goods/" + goodsNo + "/sell-status", "put");
//            String code = jsonNode.get("resultCode").toString();
//            String resultMsg = jsonNode.get("message").toString();
//
//            if(code.equals("0")){
//                message = "가격, 재고, 판매상태 수정 완료";
//            }else{
//                message = "지마켓 api 호출 실패";
//                logWriter.add(resultMsg);
//            }

            logWriter.add(message);
            logWriter.log(0);



        }catch (Exception e){
            e.printStackTrace();
            message = "가격, 재고, 판매상태 수정 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 추천옵션 조회
    public void getRecommendOpts(){
        try{
            String siteCatCode = "";
            String authorization = HmacGenerater.generate("categories", "G");

            System.out.println("authorization : " + authorization);

            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, new JSONObject(), Constants.gmkUrl + "item/v1/options/recommended-opts?catCode=" + siteCatCode, "GET");

            String code = jsonNode.get("resultCode").toString();
            String resultMsg = jsonNode.get("message").toString();

            // TODO : != null로 걸러지는지 확인 필요
            if(code != null){
                
            }else{
                
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 카테고리별 상품명 수정 가능 여부 조회
    public String getUpdateYn(){
        String result = "";

        try{
            String siteCatCode = "";
            String authorization = HmacGenerater.generate("", "G");
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, new JSONObject(), Constants.gmkUrl + "item/v1/goods/goods-name-policies?siteId=2&siteCatCode=" + siteCatCode, "get");

            String code = jsonNode.get("resultCode").toString();
            String resultMsg = jsonNode.get("message").toString();

            // TODO : != null로 걸러지는지 확인 필요
            if(code != null){
                result = resultMsg;
            }else{
                result = jsonNode.get("isEditable").toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
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

    // 판매자주소록 등록
    public String registSellerAddress(HttpServletRequest httpServletRequest, String dataType){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String message = "";
        String statusCode = "";

        try{

        }catch (Exception e){
            e.printStackTrace();
            message = "가격, 재고, 판매상태 수정 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

}
