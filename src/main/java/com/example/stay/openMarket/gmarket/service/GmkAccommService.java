package com.example.stay.openMarket.gmarket.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.dto.ContentsPhotoDto;
import com.example.stay.openMarket.common.dto.StockDto;
import com.example.stay.openMarket.common.mapper.CommonMapper;
import com.example.stay.openMarket.common.service.CommonService;
import com.example.stay.openMarket.gmarket.hmac.HmacGenerater;
import com.example.stay.openMarket.gmarket.mapper.GmkMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class GmkAccommService {

    @Autowired
    private GmkMapper gmkMapper;

    @Autowired
    private CommonMapper commonMapper;

    private CommonService commonService;

    private static int intOmkIdx = 5;

    CommonFunction commonFunction = new CommonFunction();

    // ESM+ 에서는 공지된 내용과 같이 1.0 상품 등록 제한 (23년 9월) → 1.0 상품 수정 제한 (23년 12월) 으로 계획되어 있으니 참고 부탁 드립니다.

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
            AccommDto accommDto = commonMapper.getAcmInfo(intAID, intOmkIdx);
            if(accommDto != null){
                JSONObject accommJson = new JSONObject();

                JSONObject itemBasicInfo = new JSONObject();

                JSONObject goodsName = new JSONObject();
                String strSubject = accommDto.getStrSubject();

                if(strSubject.length() <= 50){
                    goodsName.put("kor", strSubject);
                    itemBasicInfo.put("goodsName", goodsName);

                    JSONObject category = new JSONObject();

                    // 지마켓 카테고리 코드
                    List<JSONObject> site = new ArrayList<>();
                    JSONObject siteJson = new JSONObject();
                    siteJson.put("siteTpye", 2); // 2 : 지마켓
                    // TODO: 나중에 전체 카테고리코드 조회해보고 뭔지 봐야할듯
//                siteJson.put("catCode",); // 최하위(Leaf) 카테고리 코드
                    // 여행/항공권 > 국내호텔/레지던스 > 강원

                    category.put("site", site);

                    // ESM 카테고리 코드 -> 뭐임?
                    JSONObject esm = new JSONObject();
//                esm.put("catCode", );
                    category.put("esm", esm);

                    itemBasicInfo.put("category", category);

                    // 브랜드코드
//                JSONObject catalog = new JSONObject();
////                catalog.put("brandNo", ) // 브랜드코드 -> api로 조회 가능
//                itemBasicInfo.put("catalog", catalog);

                    JSONObject itemAdditionalInfo = new JSONObject();

                    // 판매가격
                    // TODO : 지마켓, 옥션 둘 다 필수값으로 되어있는데 지마켓에 상품 등록할 때는 지마켓 데이터만 입력하면 되는건지 확인 필요
                    JSONObject priceJson = new JSONObject();
                    double price = commonMapper.getOmkSales(intAID, intOmkIdx);
                    priceJson.put("price", price);
                    itemAdditionalInfo.put("price", priceJson);

                    // 재고수량 -> 100일치..?
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    String strDate = dateFormat.format(date);

                    List<StockDto> stockDto = commonMapper.getStockList(intAID, intOmkIdx, strDate);
                    JSONObject stock = new JSONObject();
//                stock.put("Gmkt", ); // 옵션 등록시 옵션재고관리(true) 선택할 경우 본 수량 무시되고 옵션 재고합으로 산정
                    itemAdditionalInfo.put("stock", stock);

                    // 판매기간
                    // 입력 가능 기간 : 15, 30, 60, 90
                    // 수정 시 0 입력하면 기존 기간 유지
                    JSONObject sellingPeriod = new JSONObject();
//                sellingPeriod.put("Gmkt", );
                    itemAdditionalInfo.put("sellingPeriod", sellingPeriod);

                    // 판매자 상품코드(관리코드 or 자사몰 상품번호)
//                itemAdditionalInfo.put("managedCode", intAID);

                    // 추천옵션 -> ? 주문 옵션이 있는데 왜 추천옵션이 필수값인지....
                    // 2.0 에서는 추천옵션을 기본으로 등록/수정 할 수 있기 때문에 recommendedOpts 을 사용 해주시면 됩니다. orderOpts 경우 구옵션 등록/수정 에서만 사용가능 합니다.
                    JSONObject recommendedOpts = new JSONObject();
                    recommendedOpts.put("type", 0); // 추천옵션 타임 0 : 옵션 미사용
                    itemAdditionalInfo.put("recommendedOpts", recommendedOpts);

                    // 주문옵션 -> 여기에 우리 옵션이 들어가야하는건가..
                    // 주문옵션의 옵션명은 한글기준 25자까지
                    JSONObject orderOpts = new JSONObject();
                    orderOpts.put("type", 2);
                    orderOpts.put("isStockManage", true);

                    // ex) 사용일자 : 08월07일(월), 타입 : 디럭스패밀리트윈
                    JSONObject combination = new JSONObject();
                    JSONObject name1 = new JSONObject();
                    name1.put("kor", "사용일자");

                    JSONObject name2 = new JSONObject();
                    name2.put("kor", "타입");

                    combination.put("name1", name1);
                    combination.put("name2", name2);

                    JSONArray orderOptsDetails = new JSONArray();
                    // 사용일자
                    JSONObject value1 = new JSONObject();
//                value1.put("kor", "");

                    // 재고
                    JSONObject aty = new JSONObject();
                    aty.put("gmkt", 0);
                    orderOptsDetails.add(aty);

                    itemAdditionalInfo.put("orderOpts", orderOpts);

                    // 판매자 브랜드명
                    JSONObject sellerShop = new JSONObject();
                    sellerShop.put("catName", ""); // 콘도24? 플랜드스테이?
                    itemAdditionalInfo.put("sellerShop", sellerShop);

                    // 배송방법 타입
                    JSONObject shipping = new JSONObject();
//                shipping.put("type", );

                    // 배송비 타입
                    shipping.put("feeType", 2);
                    JSONObject each = new JSONObject();
                    each.put("feeType", 1); // 1 : 무료, 2 : 유료, 3 : 조건부무료, 4 : 수량별차등

                    // 발송정책번호 -> ?
                    JSONObject dispatchPolicyNo = new JSONObject();
//                dispatchPolicyNo.put("gmkt", );
                    shipping.put("dispatchPolicyNo", dispatchPolicyNo);

                    itemAdditionalInfo.put("shipping", shipping);

                    // 상품정보고시 상품군코드 -> api로 조회 가능
                    JSONObject officialNotice = new JSONObject();
//                officialNotice.put("officialNoticeNo", );

                    // 상품정보고시 항목코드 -> api로 조회 가능
                    JSONArray details = new JSONArray();
                    JSONObject detailJson = new JSONObject();
//                detailJson.put("officialNoticeItemelementCode", ); // 상품정보고시 항목코드
//                detailJson.put("value", ); // 상품정보고시 값
                    details.add(detailJson);

                    // 상품정보고시 추가입력여부
//                officialNotice.put("isExtraMark", );

                    itemAdditionalInfo.put("officialNotice", officialNotice);

                    // 청소년 구매불가
                    itemAdditionalInfo.put("isAdultProduct", false); // true : 성인상품, false : 일반상품

                    // 부가세여부
                    itemAdditionalInfo.put("isVatFree", true); // true : 부가세 면세상품, false : 부과세 과세상품

                    // 통합 어린이인증 타입
                    JSONObject certInfo = new JSONObject();
                    JSONObject safetyCerts = new JSONObject();
                    JSONObject child = new JSONObject();
                    child.put("type", 1); // 0 : 인증대상, 1 : 인증대상아님, 2 : 상품상세별도표기
                    safetyCerts.put("child", child);

                    // 통합 전기인증 타입
                    JSONObject electric = new JSONObject();
                    electric.put("type", 1); // 0 : 인증대상, 1 : 인증대상아님, 2 : 상품상세별도표기
                    safetyCerts.put("electric", electric);

                    // 통합 생활용품인증 타입
                    JSONObject life = new JSONObject();
                    life.put("life", 1); // 0 : 인증대상, 1 : 인증대상아님, 2 : 상품상세별도표기
                    safetyCerts.put("life", life);

                    certInfo.put("safetyCerts", safetyCerts);
                    itemAdditionalInfo.put("certInfo", certInfo);

                    // 상품 기본 이미지
                    JSONObject images = new JSONObject();
                    if(accommDto.getStrACMPhotos() != null){
                        String strAcmPhotos = accommDto.getStrACMPhotos();
                        String[] photoArr = strAcmPhotos.split("\\|");

                        for(int i=0; i< photoArr.length; i++){
                            // TODO : 추후 이미지 저장 경로 정해지면 추가 할 것
                            images.put("addtionalImg" + i + "URL", "https://condo24.com/" + photoArr[i]);
                            if(i==0){
                                images.put("basicImgURL", true);
                            }
                        }
                    }
                    itemAdditionalInfo.put("images", images);

                    // 상품 상세정보 타입
                    JSONObject descriptions = new JSONObject();
                    JSONObject kor = new JSONObject();
//                kor.put("type", ); // 1 : contentID(추후제공), 2 : html
//                kor.put("contentId", ""); // 상품 상세정보 타입 1일 경우 필수
                    String strImgDesc = commonService.getStrPdtDtlInfo(accommDto, intAID, 3);
                    kor.put("html", strImgDesc);
                    descriptions.put("kor", kor);
                    itemAdditionalInfo.put("descriptions", descriptions);

                    // 추가구성 사용여부
                    JSONObject addonService = new JSONObject();
//                addonService.put("addonServiceUseType", 0); // 0 : 사용하지 않음, 1 : 사용 - 재고관리X, 2 : 사용 - 재고관리O

                    // TODO : 추가구성 사용여부 0으로 해도 추가구성 항목들 필수값인지 확인
                    itemAdditionalInfo.put("addonService", addonService);

                    // 판매자할인 사용여부
                    JSONObject sellerDiscount  = new JSONObject();
                    sellerDiscount.put("isUse", false); // true : 할인 적용, false : 할인 미적용

                    JSONObject addtionalInfo = new JSONObject();
                    addtionalInfo.put("sellerDiscount", sellerDiscount);

                    // 지마켓용 사이트부담 지원할인
                    JSONObject siteDiscount = new JSONObject();

                    siteDiscount.put("gmkt", true);
                    addtionalInfo.put("siteDiscount", siteDiscount);

                    // 가격비교사이트 상품 노출 여부
                    JSONObject pcs = new JSONObject();
                    pcs.put("isUse", false);
                    addtionalInfo.put("pcs", pcs);

                    // 해외판매 여부
                    JSONObject overseaSales = new JSONObject();
//                overseaSales.put("isAgree", );
                    addtionalInfo.put("overseaSales", overseaSales);

                    // api 호출
                    String authorization = HmacGenerater.generate("goods");
                    JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, new JSONObject(), Constants.gmkUrl + "item/v1/goods", "post");
                    String code = jsonNode.get("resultCode").toString();
                    String resultMsg = jsonNode.get("message").toString();
                    if(code.equals("0")) {
                        message = "상품 생성 완료";
                    }else{
                        message = "지마켓 api 호출 실패";
                        logWriter.add(resultMsg);
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

    // 상품명 수정
    // 검색용 상품명은 10일 이내 판매이력이 없을 경우만 수정 가능, 그 이후에는 불가능
    // 수정 불가한 경우 별도에러 X, 기존 상품명 유지
    public String updateAccommName(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            AccommDto accommDto = commonMapper.getAcmInfo(intAID, intOmkIdx);
            String goodsNo = accommDto.getStrPdtCode();

            if(accommDto != null){
                String kor = accommDto.getStrSubject(); // 검색용 상품명
                String promotion = ""; // 프로모션 상품명(필수X)

                JSONObject requestJson = new JSONObject();
                requestJson.put("kor", kor);
                requestJson.put("promotion", promotion);

                // api 호출
                String authorization = HmacGenerater.generate("");
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
            String goodsNo = commonMapper.getStrPdtCode(intAID, intOmkIdx);

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
            String authorization = HmacGenerater.generate("");
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
            AccommDto accommDto = commonMapper.getAcmInfo(intAID, intOmkIdx);

            String goodsNo = accommDto.getStrPdtCode();

            // TODO : strShortDesc / strDescription / html?? 뭐로 할지?
            String strDescription = accommDto.getStrDescription();

            JSONObject requestJson = new JSONObject();

            String strHtmlDesc = commonService.getStrPdtDtlInfo(accommDto, intAID, 3);
            requestJson.put("descNew", strHtmlDesc); // 상세설명(html)

            // api 호출
            String authorization = HmacGenerater.generate("");
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
            String goodsNo = commonMapper.getStrPdtCode(intAID, intOmkIdx);

            JSONObject requestJson = new JSONObject();

            // 판매상태
            JSONObject isSell = new JSONObject();
//            isSell.put("gmkt", ); // true : 판매가능, false : 판매중지(판매중지 상태로 1개월 유지 시 상품 삭제)
//            isSell.put("iac", );
            requestJson.put("isSell", isSell);

            JSONObject itemBasicInfo = new JSONObject();

            // 판매가격
            JSONObject priceJson = new JSONObject();
            double price = commonMapper.getOmkSales(intAID, intOmkIdx);
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
            String authorization = HmacGenerater.generate("");
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
            String authorization = HmacGenerater.generate("");
            JsonNode jsonNode = commonFunction.callJsonApi("gmk", authorization, new JSONObject(), Constants.gmkUrl + "item/v1/options/recommended-opts?catCode=" + siteCatCode, "put");

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


}
