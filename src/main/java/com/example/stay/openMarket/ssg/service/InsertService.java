package com.example.stay.openMarket.ssg.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.dto.CondoDto;
import com.example.stay.openMarket.common.dto.StockDto;
import com.example.stay.openMarket.common.mapper.CommonMapper;
import com.example.stay.common.util.Constants;
import com.example.stay.openMarket.common.service.CommonService;
import com.example.stay.openMarket.ssg.mapper.SsgMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class InsertService {

    @Autowired
    private SsgService ssgService;

    @Autowired
    private SsgMapper ssgMapper;

    @Autowired
    private CommonService commonService;

    @Autowired
    private CommonMapper commonMapper;

    CommonFunction commonFunction = new CommonFunction();

    public String insert(int intAID, String dataType){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

            AccommDto accommDto = commonService.getAcmInfo(intAID, 7); // 7이 내부 SSG 오픈마켓 idx 값
            String strBrandId = ssgService.getBrandId(intAID); // 시설별 브랜드 id 값

            // 검색어 필수 값이므로 만약 검색어 데이터가 없을 시 시설명으로 설정
            String strKeywords = (accommDto.getStrKeywords() != null)? accommDto.getStrKeywords() : accommDto.getStrSubject();

            JSONObject mainObject = new JSONObject();

            // 최저가
            DateFormat dateDBFormat = new SimpleDateFormat("yyyyMMdd");
            String strDBDate = dateDBFormat.format(new Date());
            int intMinPrice = commonMapper.getMinPrice(intAID, strDBDate, 7);

            // =============================
            // 고정값
            // =============================
            JSONObject insertObject = new JSONObject();
            insertObject.put("itemNm", accommDto.getStrSubject()); // 상품명
            insertObject.put("brandId", strBrandId); // 브랜드 ID
            insertObject.put("stdCtgId", "4000004432"); // 표준 카테고리 ID
            insertObject.put("dispStrtDts", "20220711000000"); // 전시 시작 일시
            insertObject.put("dispEndDts", "20990201235900"); // 전시 종료 일시
            insertObject.put("srchPsblYn","Y"); // 검색가능여부
            insertObject.put("itemSrchwdNm", strKeywords); // 검색어
            insertObject.put("itemChrctDivCd","10"); // 상품 특성코드 10:일반
            insertObject.put("itemChrctDtlCd","10"); // 상품 특성 상세코드 10:일반
            insertObject.put("exusItemDivCd","10"); // 전용상품 구분코드 10:일반, 20:특장점
            insertObject.put("exusItemDtlCd","10"); // 전용상품 구분 상세코드 10:일반, 20:특장점
            insertObject.put("dispAplRngTypeCd","10"); // 전시 적용 범위 유형 코드 10:전체(모바일+PC), 30:모바일
            insertObject.put("manufcoNm","콘도24");
            insertObject.put("prodManufCntryId","2000000078"); // 생산 제조 국가 ID
            insertObject.put("minOnetOrdPsblQty", 1); // 최소 1회 주문 가능 수량
            insertObject.put("maxOnetOrdPsblQty", 20); // 최대 1회 주문 가능 수량
            insertObject.put("max1dyOrdPsblQty", 20); // 최대 1일 주문 가능 수량
            insertObject.put("adultItemTypeCd", "90"); // 성인상품 타입코드 90:일반
            insertObject.put("hriskItemYn", "N"); // 고 위험 상품 여부
            insertObject.put("nitmAplYn", "N"); // 신상품 적용 여부
            insertObject.put("invMngYn", "Y"); // 재고 관리 여부
            insertObject.put("sellUnitQty", 9999); // 판매 단위 수량
            insertObject.put("baseInvQty", 9999); // 재고 수량
            insertObject.put("buyFrmCd", "60"); // 매입형태 코드 60:위수탁
            insertObject.put("txnDivCd", "10"); // 과세 구분 코드 10:과세
            insertObject.put("invQtyMarkgYn", "N"); // 재고 수량 표기 여부
            insertObject.put("splVenItemId", String.valueOf(accommDto.getIntAID())); // 공급업체상품ID
            insertObject.put("itemSellTypeCd", "20"); // 상품 판매유형 코드 10:일반, 20:옵션
            insertObject.put("itemSellTypeDtlCd", "10"); // 상품 판매유형 상세코드 10:일반
            insertObject.put("shppMthdCd", "10"); // 배송 방법 코드 10: 자사배송
            insertObject.put("shppItemDivCd", "01"); // 배송상품구분코드 01:일반
            insertObject.put("shppMainCd", "41"); // 배송 주체 코드 31: 자사창고, 32: 업체창고, 41: 협력업체
            insertObject.put("mareaShppYn", "N"); // 수도권 배송여부
            insertObject.put("shppRqrmDcnt", 3); // 배송 소요일수
            insertObject.put("whoutShppcstId", "0000516571"); // 출고배송비 ID
            insertObject.put("retShppcstId", "0000516572"); // 반품배송비 ID
            insertObject.put("whoutAddrId", "0028508564"); // 출고 주소 ID
            insertObject.put("snbkAddrId", "0028508564"); // 반품 주소 ID
            insertObject.put("retExchPsblYn", "N"); // 반품 교환가능 여부
            insertObject.put("itemMngPropClsId", "0000000030"); // 상품 관리 항목 분류 ID



            // =============================
            // site(등록 사이트)
            // =============================
            JSONObject siteObject = new JSONObject();
            List<Object> siteList = new ArrayList<>();

            JSONObject site1Object = new JSONObject();
            site1Object.put("siteNo", "6001");
            site1Object.put("sellStatCd", "20");
            siteList.add(site1Object);

            JSONObject site2Object = new JSONObject();
            site2Object.put("siteNo", "6004");
            site2Object.put("sellStatCd", "20");
            siteList.add(site2Object);

            JSONObject site3Object = new JSONObject();
            site3Object.put("siteNo", "7013");
            site3Object.put("sellStatCd", "20");
            siteList.add(site3Object);

            siteObject.put("site", siteList);
            insertObject.put("sites", siteObject);


            // =============================
            // 전시 카테고리
            // =============================
            JSONObject dispCtgsObject = new JSONObject();
            List<Object> dispCtgsList = new ArrayList<>();

            JSONObject dispCtg1Object = new JSONObject();
            dispCtg1Object.put("siteNo", "6001");
            dispCtg1Object.put("dispCtgId", "6000211750");
            dispCtgsList.add(dispCtg1Object);

            JSONObject dispCtg2Object = new JSONObject();
            dispCtg2Object.put("siteNo", "6004");
            dispCtg2Object.put("dispCtgId", "6000044028");
            dispCtgsList.add(dispCtg2Object);

            JSONObject dispCtg3Object = new JSONObject();
            dispCtg3Object.put("siteNo", "6005");
            dispCtg3Object.put("dispCtgId", "6000054825");
            dispCtgsList.add(dispCtg3Object);

            JSONObject dispCtg4Object = new JSONObject();
            dispCtg4Object.put("siteNo", "7013");
            dispCtg4Object.put("dispCtgId", "6000088233");
            dispCtgsList.add(dispCtg4Object);

            dispCtgsObject.put("dispCtg", dispCtgsList);
            insertObject.put("dispCtgs", dispCtgsObject);


            // =============================
            // 상품관리항목
            // =============================
            JSONObject itemMngObject = new JSONObject();
            List<Object> itemList = new ArrayList<>();

            JSONObject itemMng1Object = new JSONObject();
            itemMng1Object.put("itemMngPropId", "0000000123");
            itemMng1Object.put("itemMngCntt", "상세 설명 참조");
            itemList.add(itemMng1Object);

            JSONObject itemMng2Object = new JSONObject();
            itemMng2Object.put("itemMngPropId", "0000000124");
            itemMng2Object.put("itemMngCntt", "상세 설명 참조");
            itemList.add(itemMng2Object);

            JSONObject itemMng3Object = new JSONObject();
            itemMng3Object.put("itemMngPropId", "0000000125");
            itemMng3Object.put("itemMngCntt", "상세 설명 참조");
            itemList.add(itemMng3Object);

            JSONObject itemMng4Object = new JSONObject();
            itemMng4Object.put("itemMngPropId", "0000000126");
            itemMng4Object.put("itemMngCntt", "상세 설명 참조");
            itemList.add(itemMng4Object);

            JSONObject itemMng5Object = new JSONObject();
            itemMng5Object.put("itemMngPropId", "0000000127");
            itemMng5Object.put("itemMngCntt", "상세 설명 참조");
            itemList.add(itemMng5Object);

            JSONObject itemMng6Object = new JSONObject();
            itemMng6Object.put("itemMngPropId", "0000000128");
            itemMng6Object.put("itemMngCntt", "상세 설명 참조");
            itemList.add(itemMng6Object);

            JSONObject itemMng7Object = new JSONObject();
            itemMng7Object.put("itemMngPropId", "0000000129");
            itemMng7Object.put("itemMngCntt", "02-1588-0134");
            itemList.add(itemMng7Object);

            itemMngObject.put("itemMngAttr", itemList);

            insertObject.put("itemMngAttrs", itemMngObject);


            // =============================
            // 상품 메인 이미지 10장
            // =============================
            JSONObject itemImgsObject = new JSONObject();

            // 메인사진 10장 DB에서 가져오기

            System.out.println(accommDto.getStrACMPhotos());
            if(accommDto.getStrACMPhotos() != null){
                String[] photos = accommDto.getStrACMPhotos().split("\\|");
                List<Object> dataPhotoList = new ArrayList<>();
                for(int i=0; i<10; i++){
                    JSONObject imgObject = new JSONObject();
                    imgObject.put("dataSeq", (i+1));
                    imgObject.put("dataFileNm", "https://condo24.com"+photos[i]);
                    imgObject.put("rplcTextNm", "이미지"+(i+1));
                    dataPhotoList.add(imgObject);
                }
                itemImgsObject.put("imgInfo",dataPhotoList);

            }
            insertObject.put("itemImgs",itemImgsObject);



            // =============================
            // 상품 상세페이지
            // =============================
            // DB에서 desc 이미지 가져오기(없으면 데이타 종합해서 html 코드)
            String strImgDesc = commonService.getStrPdtDtlInfo(accommDto, intAID, 7);

            // api 보낼 html코드 비교표현식 변환
            String strApiImgDesc = strImgDesc.replace("<", "&lt;").replace(">", "&gt;");
            insertObject.put("itemDesc", strApiImgDesc);


            // =============================
            // 상품 재고
            // =============================

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
            Date date = new Date();
            String strNow = dateFormat.format(date);

            List<StockDto> stockList = commonMapper.getStockList(intAID, 7, strNow);

            List<Object> uitemList = new ArrayList<>();
            List<Object> priceList = new ArrayList<>();
            int index = 0;
            for (StockDto dto : stockList) {
                JSONObject itemObject = new JSONObject();

                // uitem
                index ++;
                itemObject.put("tempUitemId", String.valueOf(index));

                // 품절여부
                String strSellStatCd = "20";
                if (((dto.getStrRmtypeName().contains("2박") == true || dto.getStrRmtypeName().contains(" 연박") == true) & dto.getIntNextStock() == 0) || dto.getIntStock() == 0) {
                    strSellStatCd = "80";
                }
                itemObject.put("sellStatCd", strSellStatCd);

                // 1번옵션명(입실일자)
                itemObject.put("uitemOptnTypeNm1", "입실일자");

//                String strDate = dto.getDateSales().substring(0, dto.getDateSales().lastIndexOf("."));
                // 날짜 포맷
                String strDate = dto.getDateSales().trim();
//                SimpleDateFormat dateDate = new SimpleDateFormat("yyyyMMdd");
//                Date dateStrDate = dateDate.parse(strDate);
//                SimpleDateFormat goodDate = new SimpleDateFormat("MM월dd일(E)"); // uitemOptnChoiTypeCd1 : 10일때
////                SimpleDateFormat goodDate = new SimpleDateFormat("yyyy-MM-dd"); // uitemOptnChoiTypeCd1 : 30일때
//                String strGoodDate = goodDate.format(dateStrDate);

                LocalDate localDate = LocalDate.parse(strDate);
                String formattedDate = localDate.format(DateTimeFormatter.ofPattern("MM월dd일(E)", Locale.KOREAN));


                itemObject.put("uitemOptnNm1", formattedDate);

                // 2번옵션명(타입)
                itemObject.put("uitemOptnTypeNm2", "타입");
                String strTocode = dto.getStrRmtypeName();
                itemObject.put("uitemOptnNm2", strTocode);

                // 재고
                int intOMKStock = dto.getIntStock();
                if (((dto.getStrRmtypeName().contains("2박") == true || dto.getStrRmtypeName().contains(" 연박") == true) & dto.getIntNextStock() == 0) || dto.getIntStock() == 0) {
                    intOMKStock = 0;
                }

                itemObject.put("baseInvQty", intOMKStock);

                itemObject.put("splVenItemId", dto.getIntSsgSeq());
                itemObject.put("useYn", "Y");

                uitemList.add(itemObject);

                // 가격
                JSONObject priceObject = new JSONObject();

                int intPrice = dto.getMoneySales();

                int intSSGPrice = (intPrice * (100 - 8) / 100);
                priceObject.put("tempUitemId", String.valueOf(index));
                priceObject.put("splprc", intSSGPrice);
                priceObject.put("sellprc", intPrice);
                priceObject.put("mrgrt", 8);

                priceList.add(priceObject);
            }

            JSONObject itemObject = new JSONObject();
            itemObject.put("uitem", uitemList);
            insertObject.put("uitems", itemObject);

            JSONObject itemPriceObject = new JSONObject();
            itemPriceObject.put("uitemPrc", priceList);
            insertObject.put("uitemPluralPrcs", itemPriceObject);

            // attr
            JSONObject attrObject = new JSONObject();
            attrObject.put("uitemCacOptnYn", "N");
            attrObject.put("uitemOptnChoiTypeCd1", "10");
            attrObject.put("uitemOptnExpsrTypeCd1", "10");
            attrObject.put("uitemOptnChoiTypeCd2", "10");
            attrObject.put("uitemOptnExpsrTypeCd2", "10");

            insertObject.put("uitemAttr", attrObject);



            // =============================
            // 상품노출가
            // =============================
            JSONObject salesPrcObject = new JSONObject();
            JSONObject salesPrcInfoObject = new JSONObject();


            int intSellprc = ssgMapper.getMinPrice(intAID, strNow);
            System.out.println(intSellprc);
            int intMrgrt = 8;
            int intSplprc = (intSellprc * (100-intMrgrt) / 100);

            salesPrcInfoObject.put("sellprc", intSellprc);
            salesPrcInfoObject.put("splprc", intSplprc);
            salesPrcInfoObject.put("mrgrt", intMrgrt);

            salesPrcObject.put("uitemPrc", salesPrcInfoObject);

            insertObject.put("salesPrcInfos", salesPrcObject);


            mainObject.put("insertItem", insertObject);
            System.out.println(mainObject.toJSONString());

            // api 호출
            JsonNode resultNode = commonFunction.callJsonApi("SSG", "", mainObject, "https://eapi.ssgadm.com/item/0.4/insertItem.ssg", "POST");

            JSONObject jsonObject = (JSONObject) new JSONParser().parse(resultNode.get("result").toString());

            if(jsonObject.get("resultCode").toString().equals("00") && jsonObject.get("resultMessage").toString().equals("SUCCESS")){

                // 프로시저 실행(ACCOMM_OMK insert)
                String strItemId = jsonObject.get("itemId").toString();
                String strAcmOmk = commonMapper.insertAcmOmk(intAID, 7, "Y", accommDto.getStrSubject(), strItemId, "", strImgDesc);
                String strAcmOmkResult = strAcmOmk.substring(strAcmOmk.length()-4);

                if(strAcmOmkResult.equals("저장완료")){
                    message = " 상품 생성 성공";
                    statusCode = "200";
                    result = strItemId;
                    System.out.println("success");
                }else{
                    message = " 상품 생성 / DB 인입 실패";
                    statusCode = "200";
                    System.out.println("procedure fail");
                }

            }else{
                message = " 상품 생성 실패";
                statusCode = "500";
                System.out.println("fail");
            }

            System.out.println(result);

        }catch (Exception e){
            message = " 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn(dataType, statusCode, message, result);
    }
}
