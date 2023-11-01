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
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

@Service
public class UpdateService {


    @Autowired
    private CommonService commonService;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private SsgMapper ssgMapper;

    CommonFunction commonFunction = new CommonFunction();

    public String updateInfo(String dataType, int intAID, String strType, String strCode, String strStockIdx){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

            AccommDto accommDto = commonService.getAcmInfo(intAID, 7);

            System.out.println("쿼리로 ssg 정보 가져오기 : " + System.currentTimeMillis());

            String strItemId = accommDto.getStrPdtCode();

            // api로 가져온 ssg 시설 정보

            JsonNode jsonNode = commonFunction.callJsonApi("SSG", "", new JSONObject(), "https://eapi.ssgadm.com/item/0.4/viewItem.ssg?itemId=" + strItemId, "POST");
            JSONObject object = (JSONObject) new JSONParser().parse(jsonNode.get("result").toString());

            // site 6005 test
//            JSONArray siteArray = (JSONArray) new JSONParser().parse(jsonNode.get("result").get("sites").get(0).get("site").toString());
//            JSONObject siteObject = new JSONObject();
//
//            siteObject.put("siteNo", "6005");
//            siteObject.put("sellStatCd", "20");
//            siteArray.add(siteObject);
//
//            JSONObject sitesObject = new JSONObject();
//            sitesObject.put("site", siteArray);
//
//            System.out.println(sitesObject);
            // site 6005 test End

            // 전시 카테고리 test
//            JSONObject dispCtgsObject = new JSONObject();
//            List<Object> dispCtgsList = new ArrayList<>();
//
//            JSONObject dispCtg1Object = new JSONObject();
//            dispCtg1Object.put("siteNo", "6001");
//            dispCtg1Object.put("dispCtgId", "6000211750");
//            dispCtgsList.add(dispCtg1Object);
//
//            JSONObject dispCtg2Object = new JSONObject();
//            dispCtg2Object.put("siteNo", "6004");
//            dispCtg2Object.put("dispCtgId", "6000044028");
//            dispCtgsList.add(dispCtg2Object);
//
//            JSONObject dispCtg3Object = new JSONObject();
//            dispCtg3Object.put("siteNo", "6005");
//            dispCtg3Object.put("dispCtgId", "6000054825");
//            dispCtgsList.add(dispCtg3Object);
//
//            JSONObject dispCtg4Object = new JSONObject();
//            dispCtg4Object.put("siteNo", "7013");
//            dispCtg4Object.put("dispCtgId", "6000088233");
//            dispCtgsList.add(dispCtg4Object);
//
//            dispCtgsObject.put("dispCtg", dispCtgsList);
//            insertObject.put("dispCtgs", dispCtgsObject);
            // 전시 카테고리 test End

            JSONObject updateObject = new JSONObject();
            updateObject.put("itemId", object.get("itemId"));
            updateObject.put("sites", object.get("sites")); // sitesObject
            updateObject.put("dispCtgs", object.get("dispCtgs"));



            // 13283 테스트용
//            JSONObject dispObject = new JSONObject();
//            JSONObject ctgObject = new JSONObject();
//            List<Object> dispList = new ArrayList<>();
//            ctgObject.put("siteNo", "7013");
//            ctgObject.put("dispCtgId", "6000088233");
//            dispList.add(ctgObject);
//            JSONObject ctgObject1 = new JSONObject();
//            ctgObject1.put("siteNo", "6005");
//            ctgObject1.put("dispCtgId", "6000054825");
//            dispList.add(ctgObject1);
//            dispObject.put("dispCtg", dispList);
//            List<Object> testList = new ArrayList<>();
//            testList.add(dispObject);
//            updateObject.put("dispCtgs", testList);
//            updateObject.put("srchPsblYn", "Y");
//            updateObject.put("itemSrchwdNm", "test");

            updateObject.put("shppRqrmDcnt", object.get("shppRqrmDcnt"));
            updateObject.put("itemShppCritns", object.get("itemShppCritns"));

            /**
             * 메인 이미지 수정
             */
            if(strType.equals("img")){
                // 메인사진 10장 DB에서 가져오기
                //List<String> photoList = commonService.getPhotoList(intAID, 10);

                String[] photos = accommDto.getStrACMPhotos().split("\\|");
                List<Object> dataPhotoList = new ArrayList<>();
                for(int i=0; i<10; i++){
                    JSONObject imgObject = new JSONObject();
                    imgObject.put("dataSeq", (i+1));
                    imgObject.put("dataFileNm", "https://condo24.com"+photos[i]);
                    imgObject.put("rplcTextNm", "이미지"+(i+1));
                    dataPhotoList.add(imgObject);
                }

                JSONObject itemImgsObject = new JSONObject();
                itemImgsObject.put("imgInfo",dataPhotoList);

                updateObject.put("itemImgs",itemImgsObject);

            /**
             * 상세 이미지 수정
             */
            }else if(strType.equals("desc")) {
                // DB에서 desc 이미지 가져오기(없으면 데이타 종합해서 html 코드)
                String strImgDesc = commonService.getStrPdtDtlInfo(accommDto, intAID, 7).replace("<", "&lt;").replace(">", "&gt;");
                updateObject.put("itemDesc", strImgDesc);
                message = "상세이미지 수정 완료";

            /**
             * 재고 수정
             */
            }else if(strType.equals("stock")) {

                // 우선 채번으로 99999 넘을때 패스 시키기

                // 현재 날짜
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
                Date date = new Date();
                String strNow = dateFormat.format(date);

                int intMaxNum = ssgMapper.getMaxSsgSeq(intAID); // 현재 DB에 등록되어있는 재고의 SSG Seq
                int intCntTempStock = ssgMapper.getCntTempStock(intAID, strNow); // 현재 DB에 재고는 있지만 SSG 등록 안되어있는 재고 개수
                System.out.println(intMaxNum + " / " + intCntTempStock);

                if(intMaxNum + intCntTempStock > 99999){
                    System.out.println("방지");
                    statusCode = "500";
                    message = "99999 넘어서 새로 만들어야함";
                }else{

                    // 현재 ssg api에 등록되어있는 재고 uitemId 가져와서 uitemIdList에 담기
                    JSONArray uitemsArray = (JSONArray) new JSONParser().parse(object.get("uitems").toString());
                    JSONObject uitemsObject = (JSONObject) new JSONParser().parse(uitemsArray.get(0).toString());
                    JSONArray uitemArray = (JSONArray) new JSONParser().parse(uitemsObject.get("uitem").toString());
                    List<String> uitemIdList = new ArrayList<>();
                    for (Object array : uitemArray) {
                        JSONObject arObject = (JSONObject) new JSONParser().parse(array.toString());
                        uitemIdList.add(arObject.get("uitemId").toString());
                        //System.out.println(uitemIdList);
                    }


                    List<StockDto> stockList = commonMapper.getStockList(intAID, 7, strNow);
//                    List<StockDto> stockList = ssgMapper.getTestStockList();

                    List<Object> uitemList = new ArrayList<>();
                    List<Object> priceList = new ArrayList<>();
                    for (StockDto dto : stockList) {
                        JSONObject itemObject = new JSONObject();

                        itemObject.put("uitemNm", object.get("itemNm"));

                        // uitem
                        String strUitemId = String.format("%05d", dto.getIntSsgSeq());

                        // 있는 재고 update 새로운 재고 insert
                        if (uitemIdList.contains(strUitemId)) {
                            itemObject.put("uitemId", strUitemId);
                        } else {
                            intMaxNum += 1;
                            String strTmepUitemId = String.format("%05d", intMaxNum);
                            itemObject.put("tempUitemId", strTmepUitemId);
                        }
//                    itemObject.put("uitemId", uitemId);

                        // 품절여부
                        String strSellStatCd = "20";
                        if (((dto.getStrRmtypeName().contains("2박") == true || dto.getStrRmtypeName().contains(" 연박") == true) & dto.getIntNextStock() == 0) || dto.getIntStock() == 0) {
                            strSellStatCd = "80";
                        }
                        itemObject.put("sellStatCd", strSellStatCd);

                        // 1번옵션명(입실일자)
                        itemObject.put("uitemOptnTypeNm1", "일실입자");

                        String strDate = dto.getDateSales().trim();
                        SimpleDateFormat dateDate = new SimpleDateFormat("yyyyMMdd");
                        Date dateStrDate = dateDate.parse(strDate);
                        SimpleDateFormat goodDate = new SimpleDateFormat("MM월dd일(E)");
                        // 캘린더형으로 할때 유형
//                    SimpleDateFormat goodDate = new SimpleDateFormat("yyyy-MM-dd");
                        String strGoodDate = goodDate.format(dateStrDate);
                        itemObject.put("uitemOptnNm1", strGoodDate);

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

                        itemObject.put("splVenItemId", dto.getIntIdx());
                        itemObject.put("useYn", "Y");

                        uitemList.add(itemObject);

                        // 가격
                        JSONObject priceObject = new JSONObject();

                        int intPrice = dto.getMoneySales();

                        int intSSGPrice = (intPrice * (100 - 8) / 100);
                        if(uitemIdList.contains(strUitemId)){
                            priceObject.put("uitemId", strUitemId);
                        }else{
                            String strTmepUitemId = String.format("%05d", intMaxNum);
                            priceObject.put("tempUitemId", strTmepUitemId);
                        }
                        //priceObject.put("uitemId", uitemId);
                        priceObject.put("splprc", intSSGPrice);
                        priceObject.put("sellprc", intPrice);
                        priceObject.put("mrgrt", 8);

                        priceList.add(priceObject);
                    }

                    JSONObject itemObject = new JSONObject();
                    itemObject.put("uitem", uitemList);
                    updateObject.put("uitems", itemObject);

                    JSONObject itemPriceObject = new JSONObject();
                    itemPriceObject.put("uitemPrc", priceList);
                    updateObject.put("uitemPluralPrcs", itemPriceObject);


                    // attr
                    JSONObject attrObject = new JSONObject();
                    attrObject.put("uitemCacOptnYn", "N");
                    attrObject.put("uitemOptnChoiTypeCd1", "10"); // 10: 텍스트, 30: 캘린더
                    attrObject.put("uitemOptnExpsrTypeCd1", "10");
                    attrObject.put("uitemOptnChoiTypeCd2", "10");
                    attrObject.put("uitemOptnExpsrTypeCd2", "10");

                    updateObject.put("uitemAttr", attrObject);

                    message = "재고 변경 완료";

                }


            /**
             * 개별 재고 수정
             * 추후 utimeId 등 수정 필요
             */
            }else if(strType.equals("stockEach")){ // 개별로 재고 조정 - 코드 어떻게 짤지 아직 미정

                if(strStockIdx != null){
                    if(strStockIdx.length() > 0) {
                        int intStockIdx = Integer.parseInt(strStockIdx);

                        DateFormat dateElandFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                        String strElandDate = dateElandFormat.format(new Date());

                        StockDto dto = commonMapper.getStockInfo(intStockIdx, 7);

                        // uitem
                        String strUitemId = String.format("%05d", dto.getIntSsgSeq());
                        // 품절여부
                        String strSellStatCd = "20";
                        if (((dto.getStrRmtypeName().contains("2박") == true || dto.getStrRmtypeName().contains(" 연박") == true) & dto.getIntNextStock() == 0) || dto.getIntStock() == 0) {
                            strSellStatCd = "80";
                        }
                        // 1번 옵션(n월n일(요일))
                        String strDate = dto.getDateSales().trim();
                        SimpleDateFormat dateDate = new SimpleDateFormat("yyyyMMdd");
                        Date dateStrDate = dateDate.parse(strDate);
                        SimpleDateFormat goodDate = new SimpleDateFormat("MM월dd일(E)");
                        String strGoodDate = goodDate.format(dateStrDate);
                        // 2번 옵션
                        String strTocode = dto.getStrRmtypeName();
                        // 재고
                        int intOMKStock = dto.getIntStock();
                        if (((dto.getStrRmtypeName().contains("2박") == true || dto.getStrRmtypeName().contains(" 연박") == true) & dto.getIntNextStock() == 0) || dto.getIntStock() == 0) {
                            intOMKStock = 0;
                        }

                        List<Object> uitemList = new ArrayList<>();

                        JSONObject oneObject = new JSONObject();
                        oneObject.put("uitemId",strUitemId);
                        oneObject.put("sellStatCd",strSellStatCd);
                        oneObject.put("uitemOptnTypeNm1","입실일자");
                        oneObject.put("uitemOptnNm1",strGoodDate);
                        oneObject.put("uitemOptnTypeNm2","타입");
                        oneObject.put("uitemOptnNm2",strTocode);
                        oneObject.put("baseInvQty",intOMKStock);
                        oneObject.put("splVenItemId",dto.getIntIdx());
                        oneObject.put("useYn","Y");
                        uitemList.add(oneObject);

                        JSONObject itemObject = new JSONObject();
                        itemObject.put("uitem", uitemList);
                        updateObject.put("uitems", itemObject);

                        message = "개별 재고 수정 완료";
                        statusCode = "200";
                    }else{
                        System.out.println("intStockIdx 없음");
                        message = "intStockIdx 없음";
                        statusCode = "500";
                    }
                }else{
                    System.out.println("intStockIdx 없음");
                    message = "intStockIdx 없음";
                    statusCode = "500";
                }

            /**
             * 판매 중지
             */
            }else if(strType.equals("stop")) {

                updateObject.put("sellStatCd", "80");
                ssgMapper.updateStatus("N", intAID);
                message = "판매 중지 완료";

            /**
             * 판매 시작
             */
            }else if(strType.equals("start")) {
                updateObject.put("sellStatCd", "20");
                ssgMapper.updateStatus("Y", intAID);
                message = "판매 시작 완료";

            }else if(strType.equals("brand")) {

                if(strCode != null){
                    updateObject.put("brandId", strCode);
                    message = "브랜드 수정 완료";
                }else{
                    statusCode = "500";
                    message = "브랜드 코드를 넣어주세요";
                    return commonFunction.makeReturn(dataType, statusCode, message);
                }

            }else if(strType.equals("name")) {

                String strSubject = accommDto.getStrSubject();
                updateObject.put("itemNm", strSubject);
                message = "상품명 변경 완료";

            }else if(strType.equals("all")){

                // 사진 10장
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

                    JSONObject itemImgsObject = new JSONObject();
                    itemImgsObject.put("imgInfo",dataPhotoList);
                    updateObject.put("itemImgs",itemImgsObject);
                }


                // desc
                String strImgDesc = commonService.getStrPdtDtlInfo(accommDto, intAID, 7).replace("<", "&lt;").replace(">", "&gt;");
                updateObject.put("itemDesc", strImgDesc);

                // 재고
                // 우선 채번으로 99999 넘을때 패스 시키기
                // 현재 날짜
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
                Date date = new Date();
                String strNow = dateFormat.format(date);

                int intMaxNum = ssgMapper.getMaxSsgSeq(intAID); // 현재 DB에 등록되어있는 재고의 SSG Seq
                int intCntTempStock = ssgMapper.getCntTempStock(intAID, strNow); // 현재 DB에 재고는 있지만 SSG 등록 안되어있는 재고 개수
                System.out.println(intMaxNum + " / " + intCntTempStock);

                if(intMaxNum + intCntTempStock > 99999){
                    System.out.println("방지");
                    statusCode = "500";
                    message = "99999 넘어서 새로 만들어야함";
                }else{

                    // 현재 ssg api에 등록되어있는 재고 uitemId 가져와서 uitemIdList에 담기
                    JSONArray uitemsArray = (JSONArray) new JSONParser().parse(object.get("uitems").toString());
                    JSONObject uitemsObject = (JSONObject) new JSONParser().parse(uitemsArray.get(0).toString());
                    JSONArray uitemArray = (JSONArray) new JSONParser().parse(uitemsObject.get("uitem").toString());
                    List<String> uitemIdList = new ArrayList<>();
                    for (Object array : uitemArray) {
                        JSONObject arObject = (JSONObject) new JSONParser().parse(array.toString());
                        uitemIdList.add(arObject.get("uitemId").toString());
                        //System.out.println(uitemIdList);
                    }


                    List<StockDto> stockList = commonMapper.getStockList(intAID, 7, strNow);
//                    List<StockDto> stockList = ssgMapper.getTestStockList();

                    List<Object> uitemList = new ArrayList<>();
                    List<Object> priceList = new ArrayList<>();
                    for (StockDto dto : stockList) {
                        JSONObject itemObject = new JSONObject();

                        itemObject.put("uitemNm", object.get("itemNm"));

                        // uitem
                        String strUitemId = String.format("%05d", dto.getIntSsgSeq());

                        // 있는 재고 update 새로운 재고 insert
                        if (uitemIdList.contains(strUitemId)) {
                            itemObject.put("uitemId", strUitemId);
                        } else {
                            intMaxNum += 1;
                            String strTmepUitemId = String.format("%05d", intMaxNum);
                            itemObject.put("tempUitemId", strTmepUitemId);
                        }
//                    itemObject.put("uitemId", uitemId);

                        // 품절여부
                        String strSellStatCd = "20";
                        if (((dto.getStrRmtypeName().contains("2박") == true || dto.getStrRmtypeName().contains(" 연박") == true) & dto.getIntNextStock() == 0) || dto.getIntStock() == 0) {
                            strSellStatCd = "80";
                        }
                        itemObject.put("sellStatCd", strSellStatCd);

                        // 1번옵션명(입실일자)
                        itemObject.put("uitemOptnTypeNm1", "일실입자");

                        String strDate = dto.getDateSales().trim();
                        SimpleDateFormat dateDate = new SimpleDateFormat("yyyyMMdd");
                        Date dateStrDate = dateDate.parse(strDate);
                        SimpleDateFormat goodDate = new SimpleDateFormat("MM월dd일(E)");
                        // 캘린더형으로 할때 유형
//                    SimpleDateFormat goodDate = new SimpleDateFormat("yyyy-MM-dd");
                        String strGoodDate = goodDate.format(dateStrDate);
                        itemObject.put("uitemOptnNm1", strGoodDate);

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

                        itemObject.put("splVenItemId", dto.getIntIdx());
                        itemObject.put("useYn", "Y");

                        uitemList.add(itemObject);

                        // 가격
                        JSONObject priceObject = new JSONObject();

                        int intPrice = dto.getMoneySales();

                        int intSSGPrice = (intPrice * (100 - 8) / 100);
                        if(uitemIdList.contains(strUitemId)){
                            priceObject.put("uitemId", strUitemId);
                        }else{
                            String strTmepUitemId = String.format("%05d", intMaxNum);
                            priceObject.put("tempUitemId", strTmepUitemId);
                        }
                        //priceObject.put("uitemId", uitemId);
                        priceObject.put("splprc", intSSGPrice);
                        priceObject.put("sellprc", intPrice);
                        priceObject.put("mrgrt", 8);

                        priceList.add(priceObject);
                    }

                    JSONObject itemObject = new JSONObject();
                    itemObject.put("uitem", uitemList);
                    updateObject.put("uitems", itemObject);

                    JSONObject itemPriceObject = new JSONObject();
                    itemPriceObject.put("uitemPrc", priceList);
                    updateObject.put("uitemPluralPrcs", itemPriceObject);


                    // attr
                    JSONObject attrObject = new JSONObject();
                    attrObject.put("uitemCacOptnYn", "N");
                    attrObject.put("uitemOptnChoiTypeCd1", "10"); // 10: 텍스트, 30: 캘린더
                    attrObject.put("uitemOptnExpsrTypeCd1", "10");
                    attrObject.put("uitemOptnChoiTypeCd2", "10");
                    attrObject.put("uitemOptnExpsrTypeCd2", "10");

                    updateObject.put("uitemAttr", attrObject);

                }

                // 상품명 변경
                String strSubject = accommDto.getStrSubject();
                updateObject.put("itemNm", strSubject);

                message = "상품 전체 수정 완료";


            }else{
                statusCode = "500";
                message = "수정사항 구분을 넣어주세요.";
                return commonFunction.makeReturn(dataType, statusCode, message);
            }

            JSONObject resultObject = new JSONObject();
            resultObject.put("updateItem",updateObject);
            System.out.println(resultObject);
            //result = resultObject.toJSONString();

            // api 호출
            //JsonNode resultNode = commonFunction.callJsonApi("SSG", "", resultObject, "https://eapi.ssgadm.com/item/0.4/updateItem.ssg", "POST");
            result = callAPI("https://eapi.ssgadm.com/item/0.4/updateItem.ssg", resultObject.toJSONString());
            System.out.println(result);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(result);
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(node.get("result").toString());
            System.out.println(jsonObject.get("resultCode").toString());
            System.out.println(jsonObject.get("resultMessage").toString());
            if(jsonObject.get("resultCode").toString().equals("00")){

            }else{
                statusCode = "500";
                message = jsonObject.get("resultMessage").toString();
            }


        }catch (Exception e){
            message = "상품 수정 실패";
            statusCode = "500";
            e.printStackTrace();
        }



        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    public String callAPI(String strUrl, String jsonRequest){
        String responseBody = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // API 엔드포인트 URL 설정
//            String apiUrl = "https://example.com/api/endpoint";

            // JSON 요청 데이터 생성
//            String jsonRequest = "{\"key1\": \"value1\", \"key2\": \"value2\"}";

            // HTTP POST 요청 생성
            HttpPost httpPost = new HttpPost(strUrl);
            httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
            httpPost.setEntity(new StringEntity(jsonRequest));
            httpPost.setHeader("Authorization", Constants.SsgAuthorization);
            httpPost.setHeader("Accept-Charset", "UTF-8");

            // HTTP 요청 보내기
            HttpResponse response = httpClient.execute(httpPost);

            // HTTP 응답 처리
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseBody = EntityUtils.toString(entity);
                System.out.println("HTTP 응답: " + responseBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseBody;

    }
}
