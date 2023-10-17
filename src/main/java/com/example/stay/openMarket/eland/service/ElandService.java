package com.example.stay.openMarket.eland.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.dto.CondoDto;
import com.example.stay.openMarket.common.dto.RoomTypeDto;
import com.example.stay.openMarket.common.dto.StockDto;
import com.example.stay.openMarket.common.mapper.CommonMapper;
import com.example.stay.openMarket.common.service.CommonService;
import com.example.stay.openMarket.eland.mapper.ElandMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ElandService {

    @Autowired
    private ElandMapper elandMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private CommonService commonService;

    @Autowired
    private ElandRequestService elandRequestService;

    CommonFunction commonFunction = new CommonFunction();


    // cookie 체크
    public String getCookie(HttpServletRequest request, HttpServletResponse response){

        boolean result = false;

        String ElandCookie = "empty";

        try {

            Cookie[] cookies = request.getCookies(); // 모든 쿠키 가져오기
            if(cookies!=null){
                for (Cookie c : cookies) {
                    String key = c.getName(); // 쿠키 이름 가져오기
                    String value = c.getValue(); // 쿠키 값 가져오기

                    if (key.equals("elandToken")) {
                        result = true;
                        ElandCookie = value;
                    }
                }
            }

            if(result == true){
                // 유효성 검사 & success
                String isToken = checkToken(ElandCookie);

                if(isToken.equals("00")){
                    // success
                }else{
                    //발급
                    String strNewToken = createToken(response);
                    if(strNewToken.equals("fail")){
                        // fail
                        ElandCookie = "empty";
                    }else{
                        ElandCookie = strNewToken;
                    }
                }

            }else{
                // 토큰 생성
                String strNewToken = createToken(response);
                if(strNewToken.equals("fail")){
                    // fail
                    ElandCookie = "empty";
                }else{
                    ElandCookie = strNewToken;
                }
            }

            if(ElandCookie.equals("empty")){
                // error 반환

            }

        }catch (Exception e){
            e.printStackTrace();
        }


        return ElandCookie;

    }

    // 토큰 조회
    public String checkToken(String accessToken){

        String result = "";

        try {

            JsonNode jsonNode = commonFunction.callJsonApi("eland", "Bearer "+ accessToken, new JSONObject(), Constants.elandPath + "/token/checkAccessTokenValidation.action", "POST");

            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonNode.toString());
            System.out.println(jsonObject);
            result = jsonObject.get("error").toString();


        }catch (Exception e){
            e.printStackTrace();
            System.out.println("AccessToken 유효성체크 실패");
        }

        return result;
    }


    // 토큰 생성
    public String createToken(HttpServletResponse response){
        String result = "";

        try {

            JsonNode jsonNode = commonFunction.callJsonApi("eland", Constants.base64EncodedAuth, new JSONObject(), Constants.elandPath + "/auth/requestToken.action", "POST");

            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonNode.toString());
            String error = jsonObject.get("error").toString();
            if (error.equals("99")){
                result = "fail";
            }else{

                result = jsonObject.get("access_token").toString();

                // 쿠키 생성
                Cookie cookie = new Cookie("elandToken", result);
                cookie.setMaxAge(60*60*24);
                cookie.setPath("/");
                response.addCookie(cookie);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    // 출고지시조회
    public String getReserveList(HttpServletRequest request, HttpServletResponse response, String startdate, String endDate){

        String result = "";

        try {
            String accessToken = getCookie(request, response);

            JsonNode jsonNode = commonFunction.callJsonApi("eland", "Bearer " + accessToken, new JSONObject(), Constants.elandPath + "/token/checkAccessTokenValidation.action", "POST");

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }

    // 상품 조회
    public String viewAccomm(HttpServletRequest request, HttpServletResponse response, String dataType, int intAID){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

            String url = "https://int-api.elandmall.co.kr/goods/searchGoodsView.action";  // API 엔드포인트 URL
            Map<String, String> parameters = new HashMap<>();
            parameters.put("goods_no", "2309001833");
            //parameters.put("param2", "value2");
            String accessToken = getCookie(request, response);

//            result = elandRequestService.callApi(url, parameters, "Bearer " + accessToken);
            JsonNode jsonNode = elandRequestService.callApi(url, parameters, "Bearer " + accessToken);
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonNode.toString());
            message = jsonObject.get("error").toString();
            if(message.equals("00")){
                message = "상품조회 성공";
            }else{
                message = "상품조회 실패";
                statusCode = "500";
            }

        }catch (Exception e){
            message = "상품조회 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn(dataType, statusCode, message);

    }

    // 상품 등록
    public String insertAccomm(HttpServletRequest request, HttpServletResponse response, String dataType, int intAID){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

            AccommDto accommDto = commonMapper.getAcmInfo(intAID, 9);

            // 검색어
            String strKeywords = (accommDto.getStrKeywords() != null)? accommDto.getStrKeywords() : accommDto.getStrSubject();

            // 상품군 코드 구하기
            String strType = accommDto.getStrType();
            String strAcmType = "ELAND_L";
            if(strType.equals("C")){
                strAcmType = "ELAND_L";
            }else if(strType.equals("H")){
                strAcmType = "ELAND_H";
            }else if(strType.equals("P")) {
                strAcmType = "ELAND_P";
            }else if(strType.equals("T")){
                strAcmType = "ELAND_T";
            }
            String strRegion = accommDto.getStrRegionKeyword();
            if(strRegion != null){
                strRegion = strRegion.substring(0,2);
            }else{
                strRegion = "";
            }
            strRegion = "%"+strRegion+"%";
            String strElandCate = elandMapper.getCateCode(strAcmType, strRegion);
            strElandCate = (strElandCate  != null)? strElandCate : "";

            // 카테고리 코드 구하기
            String strCategoryCode = elandMapper.getCategoryCode(strRegion);
            strCategoryCode = (strCategoryCode  != null)? strCategoryCode : "";

            // 브랜드 구하기
            String strCateCode = (accommDto.getStrCateCode() != null)? accommDto.getStrCateCode() : "";
            String strBrandId = "";
            if(strCateCode.equals("01")){       // 소노
                strBrandId = "2300038117";
            }else if(strCateCode.equals("02")){ // 한화
                strBrandId = "2300038118";
            }else if(strCateCode.equals("04")){ // 금호
                strBrandId = "2100035451";
            }else if(strCateCode.equals("33")){ // 클럽이에스
                strBrandId = "2300038120";
            }else if(strCateCode.equals("43")){ // 하이원
                strBrandId = "2300038119";
            }else{                              // 기타
                strBrandId = "2000017560";
            }

            // 현재 시간 구하기
            DateFormat dateElandFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String strElandDate = dateElandFormat.format(new Date());
            DateFormat dateDBFormat = new SimpleDateFormat("yyyyMMdd");
            String strDBDate = dateDBFormat.format(new Date());

            // 상세설명
            String strDesc = commonService.getStrPdtDtlInfo(accommDto, intAID, 9).replace("<", "&lt;").replace(">", "&gt;");

            // 최저가
            int intMinPrice = commonMapper.getMinPrice(intAID, strDBDate);

            // 공급가(수수료 계산)
            int intRate = 7;
            int intSupplyPrice = intMinPrice * ((100 - intRate) / 100);

            String url = "";

            url += "goods_nm=" + accommDto.getStrSubject();
            url += "&goods_type_cd=10";
            url += "&goods_type_dtl_cd=10";
            url += "&prgs_stat_cd=10";	  // 판매진행상태 20:판매중지
            url += "&disp_start_dt=" + strElandDate;
            url += "&disp_end_dt=29991231000000";
            url += "&tax_divi_cd=10";	 // 과세
            url += "&std_gsgr_no=" + strElandCate; // 표준상품군조회
            url += "&brand_no=" + strBrandId;
            url += "&origin_cd=08"; //08 대한민국
            url += "&origin_nm=한국"; //위없음
            url += "&maker_cd=000"; //직접입력
            url += "&maker=콘도24";
            url += "&deli_goods_divi_cd=10"; //일반배송
            url += "&multi_item_yn=Y";
            url += "&goods_disp_divi_cd=00"; //상품전시구분
            url += "&welfare_goods_type_cd=10";
            url += "&low_vend_no=LV20021149";
            url += "&deli_form_cd=40";
            url += "&deli_cost_form_cd=10";
            url += "&deli_cost_poli_no=0000383991"; //배송비 책정 번호
            url += "&ret_dlvp_no=0000024871";
            url += "&read_time=3"; // 배송기일
            url += "&ord_poss_min_qty=1";
            url += "&cm_cd=958522";
            url += "&normal_price=" + intMinPrice;
            url += "&supply_price=" + intSupplyPrice;
            url += "&sale_price=" + intMinPrice;
            url += "&stock_mgmt_yn=Y";
            url += "&goods_clss_guide_no=801021";
            url += "&guide_dtl_no=001";
            url += "&guide_dtl_no=002";
            url += "&guide_dtl_no=003";
            url += "&guide_dtl_no=004";
            url += "&guide_dtl_no=005";
            url += "&guide_dtl_no=006";
            url += "&guide_dtl_no=007";
            url += "&guide_dtl_no=008";
            url += "&guide_dtl_no=009";
            url += "&clss_guide_cont=상세내용참조";
            url += "&clss_guide_cont=상세내용참조";
            url += "&clss_guide_cont=상세내용참조";
            url += "&clss_guide_cont=상세내용참조";
            url += "&clss_guide_cont=상세내용참조";
            url += "&clss_guide_cont=상세내용참조";
            url += "&clss_guide_cont=상세내용참조";
            url += "&clss_guide_cont=상세내용참조";
            url += "&clss_guide_cont=상세내용참조";
            url += "&goods_desc10=" + strDesc;
            // 사진 다섯장
            String[] photos = accommDto.getStrACMPhotos().split("\\|");
            for(int i=0; i<5; i++){

                if(i>0){
                    url += "&img_url"+i+"=" + "https://condo24.com"+photos[i];
                }else{
                    url += "&img_url=" + "https://condo24.com"+photos[i];
                }

            }
            url += "&disp_ctg_no=" + strCategoryCode;
            url += "&disp_ctg_no=2104515028"; // KIDIKIDI 전시 설정


            url += "&opt_nm1=날짜캘린더";
            url += "&opt_nm2=객실타입";

            // 재고 가져오기
            List<StockDto> stockList = commonMapper.getStockList(intAID, 9, strDBDate);

            System.out.println(stockList);
            for (StockDto dto : stockList) {

                String strStockSubject = dto.getStrRmtypeName();
                int intStockCnt = dto.getIntStock();
                String strStockdate = dto.getDateSales();
                int intStockSalePrice = dto.getMoneySales(); // 판매가
                int intStockCost = dto.getMoneyCost(); // 공급가

                url += "&item=" + strStockSubject + "/" + strStockdate + "," + intStockCnt + ",Y," + strStockdate + "," + strStockSubject + ",^,^,^," + strElandDate + "," + intStockSalePrice + "," + intStockCost + ",^";

            }



            url += "&ord_poss_max_qty_st_cd=10";
            url += "&search_poss_yn=Y";
            url += "&vend_goods_no=" + intAID;
            url += "&imme_coupon_apply_yn=Y"; //2023-04-05 즉시쿠폰적용여부 Y로 해달라고 요청 들어옴
            url += "&promo_apply_yn=Y"; //2023-04-05 프로모션여부 Y로 해달라고 요청 들어옴
            url += "&ret_poss_yn=Y"; // 반품여부
            url += "&ep_disp_yn=Y";
            url += "&list_disp_yn=Y";
            url += "&imme_ord_cancel_poss_yn=Y";
            url += "&ord_poss_max_qty=10";
            url += "&search_kwd=" + strKeywords;


            System.out.println(url);

            String accessToken = getCookie(request, response);

            JsonNode jsonNode = commonFunction.callJsonApi("eland", "Bearer " + accessToken, new JSONObject(), Constants.elandPath + "/goods/temporarygoods/insertNewGoods.action?" + url, "POST");

            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonNode.toString());
            System.out.println(jsonObject);
            result = jsonObject.get("error").toString();

        }catch (Exception e){
            message = "재고 등록 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }


    // 상품 update
    public String updateAccomm(HttpServletRequest request, HttpServletResponse response, String dataType, int intAID, String strType, String strStockIdx){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {
            String accessToken = getCookie(request, response);

            AccommDto accommDto = commonMapper.getAcmInfo(intAID, 9);
            String goodsNo = accommDto.getStrPdtCode();

            Map<String, String> parameters = new HashMap<>();
            parameters.put("goods_no", goodsNo);


            if(strType.equals("start")){
                parameters.put("prgs_stat_cd", "10");
                message = "상품 판매 개시";
                statusCode = "200";
            }else if(strType.equals("stop")){
                parameters.put("prgs_stat_cd", "20");
                message = "상품 판매 중지";
                statusCode = "200";
            }else if(strType.equals("desc")){

                String strImgDesc = commonService.getNewDetailInfo(accommDto, intAID, 9).replace("&quot;", "\"").replace("\n","");
                parameters.put("goods_desc10", strImgDesc);
                message = "상품 상세이미지 수정 완료";
                statusCode = "200";
            }else if(strType.equals("stock")){

                JsonNode infoJsonNode = elandRequestService.callApi(Constants.elandPath + "/goods/searchGoodsView.action", parameters, "Bearer " + accessToken);
                JSONArray stockArray = (JSONArray) new JSONParser().parse(infoJsonNode.get("itemList").toString());

                List<Integer> intItemNoList = new ArrayList<Integer>();
                for(Object object : stockArray){
                    JSONObject jsonObject = new JSONObject((Map) object);
                    intItemNoList.add(Integer.parseInt(jsonObject.get("ITEM_NO").toString()));
                }
                int intMaxItemNo = Collections.max(intItemNoList);

                DateFormat dateDBFormat = new SimpleDateFormat("yyyyMMdd");
                DateFormat dateElandFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String strElandDate = dateElandFormat.format(new Date());
                String strDBDate = dateDBFormat.format(new Date());

                // 재고 가져오기
                List<StockDto> stockList = commonMapper.getStockList(intAID, 9, strDBDate);

                int intMaxElandSeq = elandMapper.getMaxElandSeq(intAID);

                if(intMaxItemNo  == intMaxElandSeq){ // api 상의 itemNo 최대값과 DB상의 tiemNo 값이 같을때

                    for (StockDto dto : stockList) {

                        String strStockSubject = dto.getStrRmtypeName();
                        int intStockCnt = dto.getIntStock();
                        String strStockdate = dto.getDateSales();
                        int intStockSalePrice = dto.getMoneySales(); // 판매가
                        int intStockCost = dto.getMoneyCost(); // 공급가

                        int intItmeNo = dto.getIntElandSeq();

                        if(intItemNoList.contains(intItmeNo)){
                            parameters.put("item", intItmeNo + "," + strStockSubject + "/" + strStockdate + "," + intStockCnt + ",Y," + strStockdate + "," + strStockSubject + ",^,^,^," + strElandDate + "," + intStockSalePrice + "," + intStockCost + ",^");
                        }else{
                            intMaxElandSeq += 1;
                            parameters.put("item", intMaxElandSeq + "," + strStockSubject + "/" + strStockdate + "," + intStockCnt + ",Y," + strStockdate + "," + strStockSubject + ",^,^,^," + strElandDate + "," + intStockSalePrice + "," + intStockCost + ",^");
                        }

                    }
                    message = "재고 수정 완료";
                    statusCode = "200";

                }else{
                    // 동기화? 잘못됐음
                    System.out.println("아직 채번 기다려야함");

                }

            }else if(strType.equals("stockEach")){

                if(strStockIdx != null){
                    if(strStockIdx.length() > 0) {
                        int intStockIdx = Integer.parseInt(strStockIdx);

                        DateFormat dateElandFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                        String strElandDate = dateElandFormat.format(new Date());

                        StockDto stockDto = commonMapper.getStockInfo(intStockIdx, 9);
                        String strStockSubject = stockDto.getStrRmtypeName();
                        int intStockCnt = stockDto.getIntStock();
                        String strStockdate = stockDto.getDateSales();
                        int intStockSalePrice = stockDto.getMoneySales(); // 판매가
                        int intStockCost = stockDto.getMoneyCost(); // 공급가
                        int intItmeNo = stockDto.getIntElandSeq();
                        parameters.put("item", intItmeNo + "," + strStockSubject + "/" + strStockdate + "," + intStockCnt + ",Y," + strStockdate + "," + strStockSubject + ",^,^,^," + strElandDate + "," + intStockSalePrice + "," + intStockCost + ",^");
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

            }else if(strType.equals("all")){

                // 검색어
                String strKeywords = (accommDto.getStrKeywords() != null)? accommDto.getStrKeywords() : accommDto.getStrSubject();

                // 상품군 코드 구하기
                String strPrductType = accommDto.getStrType();
                String strAcmType = "ELAND_L";
                if(strPrductType.equals("C")){
                    strAcmType = "ELAND_L";
                }else if(strPrductType.equals("H")){
                    strAcmType = "ELAND_H";
                }else if(strPrductType.equals("P")) {
                    strAcmType = "ELAND_P";
                }else if(strPrductType.equals("T")){
                    strAcmType = "ELAND_T";
                }
                String strRegion = accommDto.getStrRegionKeyword();
                if(strRegion != null){
                    strRegion = strRegion.substring(0,2);
                }else{
                    strRegion = "";
                }
                strRegion = "%"+strRegion+"%";
                String strElandCate = elandMapper.getCateCode(strAcmType, strRegion);
                strElandCate = (strElandCate  != null)? strElandCate : "";

                // 카테고리 코드 구하기
                String strCategoryCode = elandMapper.getCategoryCode(strRegion);
                strCategoryCode = (strCategoryCode  != null)? strCategoryCode : "";

                // 브랜드 구하기
                String strCateCode = (accommDto.getStrCateCode() != null)? accommDto.getStrCateCode() : "";
                String strBrandId = "";
                if(strCateCode.equals("01")){       // 소노
                    strBrandId = "2300038117";
                }else if(strCateCode.equals("02")){ // 한화
                    strBrandId = "2300038118";
                }else if(strCateCode.equals("04")){ // 금호
                    strBrandId = "2100035451";
                }else if(strCateCode.equals("33")){ // 클럽이에스
                    strBrandId = "2300038120";
                }else if(strCateCode.equals("43")){ // 하이원
                    strBrandId = "2300038119";
                }else{                              // 기타
                    strBrandId = "2000017560";
                }

                // 현재 시간 구하기
                DateFormat dateElandFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String strElandDate = dateElandFormat.format(new Date());
                DateFormat dateDBFormat = new SimpleDateFormat("yyyyMMdd");
                String strDBDate = dateDBFormat.format(new Date());

                // 상세설명
                String strDesc = commonService.getStrPdtDtlInfo(accommDto, intAID, 9).replace("<", "&lt;").replace(">", "&gt;");

                // 최저가
                int intMinPrice = commonMapper.getMinPrice(intAID, strDBDate);

                // 공급가(수수료 계산)
                int intRate = 7;
                int intSupplyPrice = intMinPrice * ((100 - intRate) / 100);

                String url = "";

                parameters.put("goods_nm", accommDto.getStrSubject());
                parameters.put("disp_start_dt", strElandDate);
                parameters.put("std_gsgr_no", strElandCate); // 표준상품군조회
                parameters.put("brand_no", strBrandId);
                parameters.put("normal_price", String.valueOf(intMinPrice));
                parameters.put("supply_price", String.valueOf(intSupplyPrice));
                parameters.put("sale_price", String.valueOf(intMinPrice));
                parameters.put("goods_desc10", strDesc);
                parameters.put("vend_goods_no", String.valueOf(intAID));
                parameters.put("search_kwd", strKeywords);
                parameters.put("disp_ctg_no", strCategoryCode);


                // 사진 다섯장
                String[] photos = accommDto.getStrACMPhotos().split("\\|");
                for(int i=0; i<5; i++){

                    if(i>0){
                        parameters.put("img_url"+i, "https://condo24.com"+photos[i]);
                    }else{
                        parameters.put("img_url", "https://condo24.com"+photos[i]);
                    }

                }

                JsonNode infoJsonNode = elandRequestService.callApi(Constants.elandPath + "/goods/searchGoodsView.action", parameters, "Bearer " + accessToken);
                JSONArray stockArray = (JSONArray) new JSONParser().parse(infoJsonNode.get("itemList").toString());

                List<Integer> intItemNoList = new ArrayList<Integer>();
                for(Object object : stockArray){
                    JSONObject jsonObject = new JSONObject((Map) object);
                    intItemNoList.add(Integer.parseInt(jsonObject.get("ITEM_NO").toString()));
                }
                int intMaxItemNo = Collections.max(intItemNoList);

                // 재고 가져오기
                List<StockDto> stockList = commonMapper.getStockList(intAID, 9, strDBDate);

                int intMaxElandSeq = elandMapper.getMaxElandSeq(intAID);

                if(intMaxItemNo  == intMaxElandSeq){ // api 상의 itemNo 최대값과 DB상의 tiemNo 값이 같을때

                    for (StockDto dto : stockList) {

                        String strStockSubject = dto.getStrRmtypeName();
                        int intStockCnt = dto.getIntStock();
                        String strStockdate = dto.getDateSales();
                        int intStockSalePrice = dto.getMoneySales(); // 판매가
                        int intStockCost = dto.getMoneyCost(); // 공급가

                        int intItmeNo = dto.getIntElandSeq();

                        if(intItemNoList.contains(intItmeNo)){
                            parameters.put("item", intItmeNo + "," + strStockSubject + "/" + strStockdate + "," + intStockCnt + ",Y," + strStockdate + "," + strStockSubject + ",^,^,^," + strElandDate + "," + intStockSalePrice + "," + intStockCost + ",^");
                        }else{
                            intMaxElandSeq += 1;
                            parameters.put("item", intMaxElandSeq + "," + strStockSubject + "/" + strStockdate + "," + intStockCnt + ",Y," + strStockdate + "," + strStockSubject + ",^,^,^," + strElandDate + "," + intStockSalePrice + "," + intStockCost + ",^");
                        }

                    }

                }else{
                    // 동기화? 잘못됐음
                    System.out.println("아직 채번 기다려야함");
                }

            }


            JsonNode jsonNode = elandRequestService.callApi(Constants.elandPath + "/goods/temporarygoods/updateGoods.action", parameters, "Bearer " + accessToken);


            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonNode.toString());
            System.out.println(jsonObject);
            result = jsonObject.get("error").toString();
            if(!result.equals("00")){
                message = "API 통신 실패";
                statusCode = "500";
            }

        }catch (Exception e){
            message = "상품 수정 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn(dataType, statusCode, message);


    }



    // 주문번호 만들 idx가져오기
    public String getIdxForOrderID(){
        String idx = elandMapper.getIdxForOrderID();
        return idx;
    }

    // strVendGoodsNo, strRoomTypeName, 주문정보의 입실일자로 시설 정보 가져오기
    public CondoDto condoInfoForInsertOrder(String con_id, String strEnterIn, String strRoomTypeName){
        CondoDto condoDto = elandMapper.condoInfoForInsertOrder(con_id, strEnterIn, strRoomTypeName);
        return condoDto;
    }

    // tocode 정보 다시 가져오기
    public String tocodeForRoomTypeNm(String con_id, String strEnterIn, String strRoomTypeName){
        String tocode = elandMapper.tocodeForRoomTypeNm(con_id, strEnterIn, strRoomTypeName);
        return tocode;
    }
}
