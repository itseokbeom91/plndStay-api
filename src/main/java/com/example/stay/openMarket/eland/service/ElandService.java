package com.example.stay.openMarket.eland.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.dto.CondoDto;
import com.example.stay.openMarket.eland.mapper.ElandMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElandService {

    @Autowired
    private ElandMapper elandMapper;

    CommonFunction commonFunction = new CommonFunction();


    // cookie 체크
    public String checkCookie(HttpServletRequest req){

        String result = "empty";

        Cookie[] cookies = req.getCookies(); // 모든 쿠키 가져오기
        if(cookies!=null){
            for (Cookie c : cookies) {
                String name = c.getName(); // 쿠키 이름 가져오기
                String value = c.getValue(); // 쿠키 값 가져오기
                if (name.equals("elandToken")) {
                    result = value;
                    System.out.println(value);
                }
            }
        }

        return result;

    }

    // 토큰 조회
    public String checkToken(String accessToken){

        String result = "";

        try {

            JsonNode jsonNode = commonFunction.callJsonApi("eland", accessToken, new JSONObject(), Constants.elandPath + "/token/checkAccessTokenValidation.action", "POST");

            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonNode.toString());
            result = jsonObject.get("error").toString();


        }catch (Exception e){
            e.printStackTrace();
            System.out.println("AccessToken 유효성체크 실패");
        }

        return result;
    }

    // accessToken 가져오기
    public String GetToken(HttpServletRequest req, HttpServletResponse res){
        String result = "";

        try {
            String cookie = checkCookie(req);
            if(cookie.equals("empty")){
                String token = requestToken(res);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }

    // 출고지시조회
    public String getReserveList(String startdate, String endDate){

        String result = "";

        try {

            JsonNode jsonNode = commonFunction.callJsonApi("eland", "", new JSONObject(), Constants.elandPath + "/token/checkAccessTokenValidation.action", "POST");

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }

    // 상품 등록
    public String insertAccomm(int intAID){

        String result = "";

        try {

            AccommDto accommDto = elandMapper.getAccommInfo(intAID, 9);

            // 검색어
            String strKeywords = (accommDto.getStrKeywords() != null)? accommDto.getStrKeywords() : accommDto.getStrSubject();

            // 카테고리 구하기
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

            String GC_DTTM = "";

            String url = "";

            url += "&goods_type_cd=10";
            url += "&goods_type_dtl_cd=10";
            url += "&multi_item_yn=Y";
            url += "&ord_poss_max_qty_st_cd=10";
            url += "&search_poss_yn=Y";
            url += "&vend_goods_no=" + intAID;
            url += "&stock_mgmt_yn=Y";
            url += "&imme_coupon_apply_yn=Y"; //2023-04-05 즉시쿠폰적용여부 Y로 해달라고 요청 들어옴
            url += "&promo_apply_yn=Y"; //2023-04-05 프로모션여부 Y로 해달라고 요청 들어옴
            url += "&prgs_stat_cd=10";	  // 판매진행상태 20:판매중지
            url += "&std_gsgr_no=" + strElandCate; // 표준상품군조회 (지역 조회 후 디폴트 국내여행)
            url +=  "&tax_divi_cd=10";	 // 과세
            url +=  "&origin_cd=08"; //08 대한민국
            url +=  "&origin_nm=한국"; //위없음
            url +=  "&maker_cd=000"; //직접입력
            url +=  "&maker=콘도24";
            url +=  "&deli_goods_divi_cd=10"; //일반배송
            url +=  "&goods_disp_divi_cd=00"; //상품전시구분
            url +=  "&welfare_goods_type_cd=10";
            url +=  "&deli_form_cd=40";
            url +=  "&deli_cost_form_cd=10";
            url +=  "&deli_cost_poli_no=0000383991"; //배송비 책정 번호
            url +=  "&ret_dlvp_no=0000024871";
            url +=  "&ret_poss_yn=Y"; // 반품여부
            url +=  "&read_time=3"; // 배송기일
            url +=  "&cm_cd=958522";
            url +=  "&deli_poss_area_grp_no=10000001";
            url +=  "&ep_disp_yn=Y";
            url +=  "&list_disp_yn=Y";
            url +=  "&imme_ord_cancel_poss_yn=Y";
            url +=  "&ord_poss_min_qty=1";
            url +=  "&ord_poss_max_qty=10";
            url +=  "&search_kwd=" + strKeywords;
            url +=  "&disp_ctg_no=2104515028";
// 여기 브랜드 아이디 조회 들어가야함
            url +=  "&disp_start_dt=" + GC_DTTM;
            url +=  "&disp_end_dt=29991231000000";

            // 사진 다섯장
            String[] photos = accommDto.getStrACMPhotos().split("\\|");
            for(int i=0; i<5; i++){

                if(i>0){
                    url += "&img_url"+i+"=" + "https://condo24.com"+photos[i];
                }else{
                    url += "&img_url=" + "https://condo24.com"+photos[i];
                }

            }

            System.out.println(url);

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }



    public String requestToken(HttpServletResponse httpResponse){
        String accessToken = "";
        BufferedReader br = null;
        try {
            // API 호출
            long APIStart = System.currentTimeMillis();

            URL url = new URL(Constants.elandPath + "/auth/requestToken.action");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Authorization", Constants.base64EncodedAuth);
            conn.setRequestProperty("grant_type", "client_credentials");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Accept-Charset", "UTF-8");

            String strResponse = "";

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuffer sb = new StringBuffer();
                while ((strResponse = br.readLine()) != null) {
                    sb.append(strResponse);
                }
                strResponse = sb.toString();

                JSONParser jsonParser = new JSONParser();
                Object objData = jsonParser.parse(strResponse);
                JSONObject resultJson = (JSONObject) objData;

                System.out.println(resultJson);
                accessToken = resultJson.get("access_token").toString();

                // 발급받은 AccessToken DB에 INSERT----------------------------------------------------------------------
//                int result = insertAccessToken(accessToken);
//                if(result > 0){
//                    System.out.println("AccessToken DB에 INSERT 성공");
//                }else{
//                    System.out.println("AccessToken DB에 INSERT 실패");
//                }
                System.out.println("---------------------------------------------------------------------------------\n");
                // -----------------------------------------------------------------------------------------------------

                // 쿠키로 사용시
//                Cookie cookie = new Cookie("AssessToken", accessToken);
//                cookie.setMaxAge(60*60*24);
//                cookie.setPath("/");
//                httpResponse.addCookie(cookie);
            }else{
                strResponse = conn.getResponseMessage();
                System.out.println(strResponse);

                System.out.println("AccessToken이 만료 혹은 미발급 상태입니다.");
            }
            conn.disconnect();
            System.out.println("이랜드 API 호출 실행 시간 : " + (System.currentTimeMillis()-APIStart)/1000.0);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("AccessToken 발급 실패");
        }finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "Bearer " + accessToken;
    }

    // 발급받은 AccessToken DB에 INSERT
    public int insertAccessToken (String token){
        int result = elandMapper.insertAccessToken(token);
        return result;
    }

    //eland api 호출
    public JSONArray elandApi(HttpServletResponse httpResponse, String path, String strPostBody){
        path = Constants.elandPath + path;
//        JSONObject jsonResult = new JSONObject();
        JSONArray jsonArrayData = new JSONArray();
        BufferedReader br = null;
        try{
            // AccessToken 발급
            String accessToken = requestToken(httpResponse);

            long APIStart = System.currentTimeMillis();

            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Authorization", accessToken);
            conn.setRequestProperty("grant_type", "client_credentials");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
//            conn.setDoInput(true);

            if(strPostBody != null && !strPostBody.equals("")){
                conn.setDoOutput(true);

                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                bw.write(strPostBody);
                bw.flush();
                bw.close();

                System.out.println("OutputStream : " + conn.getOutputStream());

            }else{
                System.out.println("요청 파라미터값이 없습니다.");
            }

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                String strResponse = "";
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuffer sb = new StringBuffer();
                while ((strResponse = br.readLine()) != null) {
                    sb.append(strResponse);
                }
                strResponse = sb.toString();

                // 응답값을 json으로 파싱
                JSONParser jsonParser = new JSONParser();
                Object objData = jsonParser.parse(strResponse);
                JSONObject jsonData = (JSONObject) objData;

                jsonArrayData = (JSONArray) jsonData.get("data");

                System.out.println(jsonArrayData);

            }else{
                System.out.println("이랜드 api 통신 실패");
                System.out.println("responseCode : " + conn.getResponseCode() + "\nresponseMessage : " + conn.getResponseMessage());
            }

            conn.disconnect();
            System.out.println("이랜드 API 호출 실행 시간 : " + (System.currentTimeMillis()-APIStart)/1000.0);

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("이랜드 api 호출 실패");
        }finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return jsonArrayData;
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
