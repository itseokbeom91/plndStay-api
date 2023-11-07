package com.example.stay.common.util;

import com.example.stay.common.mapper.CommonAcmMapper;
import com.example.stay.openMarket.common.dto.CancelRulesDto;
import com.example.stay.openMarket.common.dto.RsvStayDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.w3c.dom.Document;
import org.xhtmlrenderer.simple.Graphics2DRenderer;
import org.xml.sax.InputSource;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CommonFunction<T> {

    @Autowired
    private CommonAcmMapper commonAcmMapper;

    public String makeReturn(String returnType, String statusCode, String message){

        String strResult = "";
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", statusCode);
            jsonObject.put("message", message);

            if(returnType.equals("json")){
                strResult = jsonObject.toJSONString();
            }else if(returnType.equals("jsonp")){
                strResult = Constants.jsonpCallback + "(" + jsonObject.toJSONString() + ")";
            }else if(returnType.equals("화면출력???")){
                strResult = "?????????";
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return strResult;
    }

    public String makeReturn(String returnType, String statusCode, String message, T result){

        String strResult = "";

        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", statusCode);
            jsonObject.put("message", message);
            jsonObject.put("result", result);

            if(returnType.equals("json")){
                strResult = jsonObject.toJSONString();
            }else if(returnType.equals("jsonp")){
                strResult = Constants.jsonpCallback + "(" + jsonObject.toJSONString() + ")";
            }else if(returnType.equals("view")){
                strResult = jsonObject.toJSONString();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return strResult;
    }

    public JsonNode callJsonApi(String strAccomm, String strType, JSONObject requestJson, String strUrl, String method){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.createObjectNode();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        // API 호출 정보
        if(strUrl.equals("")){
            if(strAccomm.equals("hanwha")){
                strUrl = Constants.hanwhaUrl;
            }else if(strAccomm.equals("YPB")){
                if(strType.equals("stock")){
                    strUrl = Constants.ypUrl+"getRsrvMm";
                }else if(strType.equals("booking")){
                    strUrl = Constants.ypUrl+"joinSalesPkgRsrvInfo";
                }else if(strType.equals("bookingInfo")){
                    strUrl = Constants.ypUrl+"getSalesRsrvList";
                }else if(strType.equals("bookingCancel")){
                    strUrl = Constants.ypUrl+"joinSalesPkgRsrvCncl";
                }else if(strType.equals("bookingList")){
                    strUrl = Constants.ypUrl+"getArrvRsrvList";
                }
            }
        }

        BufferedReader br = null;
        LogWriter logWriter = new LogWriter(method, strUrl, System.currentTimeMillis());
        try{
            URL url = new URL(strUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            if(strAccomm.equals("YPB")){
                conn.setRequestProperty("X-Yobiss-AuthToken", Constants.ypTokenKey);
            }else if(strAccomm.equals("SSG")){
                conn.setRequestProperty("Authorization", Constants.SsgAuthorization);
            }else if(strAccomm.equals("gmk")){
                conn.setRequestProperty("Authorization", strType);
            }else if(strAccomm.equals("eland")){
                conn.setRequestProperty("Authorization", strType);
            }

            if(!requestJson.isEmpty()){
                conn.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                writer.write(requestJson.toJSONString());
                writer.close();

                logWriter.addRequest(gson.toJson(requestJson));
            }

            String strResult = "";
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                strResult = sb.toString();
            }else{
                logWriter.add("responseCode : " + conn.getResponseCode());
                logWriter.add("responseMessage : " + conn.getResponseMessage() + "\n");
                strResult = conn.getResponseMessage();
            }

            conn.disconnect();

            // JSON 파싱
            jsonNode = objectMapper.readTree(strResult);

            logWriter.add(gson.toJson(jsonNode));
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return jsonNode;
    }
    /*
    SOAP Client API
     */
    public String sendMessage(String url, String method, String message){
//        LogWriter logWriter = new LogWriter(method, url, System.currentTimeMillis());
        try {
//            logWriter.addRequest(message);
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            factory.setNamespaceAware(true);
//            DocumentBuilder parser = factory.newDocumentBuilder();
//
//            StringReader reader = new StringReader(message);
//            InputSource is = new InputSource(reader);
//            Document document = parser.parse(is);
//            DOMSource source = new DOMSource(document);
            Service service = new Service();
            Call call = (Call) service.createCall();

            call.setTargetEndpointAddress(new java.net.URL("http://tpl.gmarket.co.kr/v1/ItemService.asmx?WSDL"));

//            call.setOperationName(method);
            call.setOperationName(new QName("http://tpl.gmarket.co.kr/", "AddItem"));

            call.setUseSOAPAction(true);
            call.setSOAPActionURI("http://tpl.gmarket.co.kr/AddItem");

            String response = (String) call.invoke(new Object[]{message});


//            logWriter.add(response);
//            logWriter.log(0);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /*
    한글 지역명을 코드로
    지역이 같아도 지역명이 다른경우가 있어 매칭 선처리
    ex) 강원, 강원특별자치도, 강원도 => 42
     */
    public String addressToDistrictCode(String address) {
        if ("서울".equals(address) || "서울시".equals(address) || "서울특별시".equals(address)) {
            return "11";
        } else if ( "부산".equals(address) ||  "부산시".equals(address) || "부산광역시".equals(address)) {
            return "26";
        } else if ( "대구".equals(address) ||  "대구시".equals(address) || "대구광역시".equals(address)) {
            return "27";
        } else if ( "인천".equals(address) ||  "인천시".equals(address) || "인천광역시".equals(address)) {
            return "28";
        } else if ( "광주".equals(address) ||  "광주시".equals(address) || "광주광역시".equals(address)) {
            return "29";
        } else if ( "대전".equals(address) ||  "대전시".equals(address) || "대전광역시".equals(address)) {
            return "30";
        } else if ( "울산".equals(address) ||  "울산시".equals(address) || "울산광역시".equals(address)) {
            return "31";
        } else if ( "경기".equals(address) ||  "경기도".equals(address)) {
            return "41";
        } else if ( "강원".equals(address) ||  "강원도".equals(address) || "강원특별자치도".equals(address)) {
            return "42";
        } else if ( "충북".equals(address) ||  "충청북도".equals(address)) {
            return "43";
        } else if ( "충남".equals(address) ||  "충청남도".equals(address)) {
            return "44";
        } else if ( "전북".equals(address) ||  "전라북도".equals(address)) {
            return "45";
        } else if ( "전남".equals(address) ||  "전라남도".equals(address)) {
            return "46";
        } else if ( "경북".equals(address) ||  "경상북도".equals(address)) {
            return "47";
        } else if ( "경남".equals(address) ||  "경상남도".equals(address)) {
            return "48";
        } else if ( "제주".equals(address) ||  "제주도".equals(address) || "제주특별자치도".equals(address)) {
            return "50";
        } else {
            return "";
        }

    }

    /*
    지번 주소 => 우편번호
    주소로 신우편번호(5자리) 가져오기
    !상세주소로만 조회할것! 상세주소가 아니면 관련된 모든 우편번호중 첫번째 항목을 SET하니 오출력 가능성 있음
     */
    public String getZipcodeByParcelAddress(String parcelAddress){
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        String requestUrl = "http://api.vworld.kr/req/search?service=search&request=search&version=2.0&crs=EPSG:900913&size=10&page=1&type=address&category=parcel&format=json&errorformat=json";
        requestUrl += "&query=" + parcelAddress;
        requestUrl += "&key=" + Constants.vWorldApiSecretKey;
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(requestUrl)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                //response 파싱
                String responseBody = response.body().string();
                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                responseJson = ( JSONObject ) responseJson.get("response");
                if(responseJson.containsKey("result")){
                    responseJson = ( JSONObject ) responseJson.get("result");
                    List<Map<String, Object>> itemList = ( List<Map<String, Object> > ) responseJson.get("items");
                    JSONObject address = ( JSONObject ) itemList.get(0).get("address");
                    System.out.println(address.get("zipcode").toString());
                    return address.get("zipcode").toString();
                }else {
                    return "";
                }


            } else {
                return makeReturn("json", String.valueOf(response.code()), response.message());
            }
        } catch (Exception e){
            e.printStackTrace();
            return makeReturn("json", e.toString(), e.getMessage());
        }

    }

    /*
    좌표 -> 한글주소
     */
    public String getJusoByGeoCd (String latitude, String longtitude) {
        String apiKey = Constants.vWorldApiSecretKey;
        String x = longtitude;
        String y = latitude;
        String url = "http://apis.vworld.kr/coord2jibun.do";
        OkHttpClient client = new OkHttpClient();
        url += "?apiKey=" + apiKey + "&x=" + x + "&y=" + y + "&output=json" + "&resultType=json";
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()) {
                //response 파싱
                String responseBody = response.body().string();
                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                String korAddr = (String) responseJson.get("ADDR");
//                System.out.println(korAddr);
                return  korAddr;

            } else {
                return makeReturn("json", String.valueOf(response.code()), response.message());
            }
        } catch (Exception e) {
            return makeReturn("json", "500", e.getMessage());
        }
    }

    // ACCOMM_CANCEL_RULES 데이터 만들기
    public String makeCancelRules(RsvStayDto rsvStayDto){
        String strPenaltyDatas = "";
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String strCheckIn = simpleDateFormat.format(rsvStayDto.getDateCheckIn());
            String strCheckOut = simpleDateFormat.format(rsvStayDto.getDateCheckOut());

            // 숙박일 중 성수기 포함 여부 확인
            int peakCount = commonAcmMapper.getPeakCount(strCheckIn, strCheckOut);

            // 하루라도 성수기 포함시 성수기 취소규정 적용
            String strFlag = "";
            if(peakCount > 0){
                strFlag = "OPS";
            }else{
                strFlag = "OOF";
            }

            // 해당 취소규정 조회
            int intAID = rsvStayDto.getIntAID();
            int intRmIdx = rsvStayDto.getIntRmIdx();
            List<CancelRulesDto> cancelRuleList = commonAcmMapper.getCancelRules(intAID, strFlag);
            if(cancelRuleList.size() == 0){
                cancelRuleList = commonAcmMapper.getCancelRules(0, strFlag);
            }

            String strRateFlag = "P";

            // 판매금액 조회
            double sales = 0;
            if(rsvStayDto.getStrRsvSite().equals("OMK")){
                // 오픈마켓별 판매금액 조회
                int intOmkIdx = rsvStayDto.getIntOMKIdx();
                 sales = commonAcmMapper.getOmkSales(intAID, intRmIdx, intOmkIdx, strCheckIn);
            }else{
                sales = rsvStayDto.getMoneySales();
            }

            for(int i=0; i<cancelRuleList.size(); i++){
                CancelRulesDto cancelRulesDto = cancelRuleList.get(i);
                double doubleRate = cancelRulesDto.getIntPercent();
                int intDay = cancelRulesDto.getIntDay();

                // 체크인 날짜 - intDay가 며칠인지 구하기
                Calendar cal = Calendar.getInstance();
                cal.setTime(rsvStayDto.getDateCheckIn());
                cal.add(Calendar.DATE, -intDay);
                Date endDate = cal.getTime();

                // 체크인 날짜 - intDay가 무슨 요일인지 구하기
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(endDate);
                int businessWeek = cal2.get(Calendar.DAY_OF_WEEK); // 요일 1 : 일 ~ 7 : 토

                // 평일은 5시까지, 토요일은 12시까지, 일요일은 불가 -> 다음날 위약금 적용
                String strTime = "";
                if(businessWeek > 1 && businessWeek < 7){ // 평일
                    strTime = "16:59:59";
                }else if(businessWeek == 7){ // 토요일
                    strTime = "11:59:59";
                }else{
                    strTime = "23:59:59";
                    if(i == 0){ // 일요일
                        doubleRate = 100;
                    }else{
                        CancelRulesDto cancelRule = cancelRuleList.get(i-1);
                        doubleRate = cancelRule.getIntPercent();
                    }
                }

                double rate = (doubleRate / 100);
                double penalty = sales * rate; // 위약금액
                double refund = sales - penalty; // 환불금액

                strPenaltyDatas += strRateFlag + "|^|" + doubleRate + "|^|" + intDay + "|^|" + strTime + "|^|" + refund + "|^|" + penalty + "{{|}}";
            }

            strPenaltyDatas = strPenaltyDatas.substring(0, strPenaltyDatas.length()-5);

        }catch (Exception e){
            e.printStackTrace();
            return "";
        }

        return strPenaltyDatas;
    }

    // RSV_STAY_HISTORY의 strProcedure 만들기
    public String makeStrProcedure(String procedure, Map<String, Object> dataMap){
        String strProcedure = "";
        if(procedure.equals("spGW_RSV_STAY_UPDATE_PROCESS")){
            int intRsvID = (Integer) dataMap.get("intRsvID");
            String strStatusCode = dataMap.get("strStatusCode").toString();
            String strRmNumDatas = dataMap.get("strRmNumDatas").toString();
            String strPenaltyDatas = dataMap.get("strPenaltyDatas").toString();
            strProcedure = "EXEC spGW_RSV_STAY_UPDATE_PROCESS @intRSvID = " + intRsvID + ", @strStatusCode = " + strStatusCode
                    + ", @strRmNumDatas = " + strRmNumDatas + ", @strPenaltyDatas = " + strPenaltyDatas;
        }

        return strProcedure;
    }

    // ip가져오기
    public String getClientIP() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        //System.out.println("> X-FORWARDED-FOR : " + ip);

        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
            //System.out.println("> Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            //System.out.println(">  WL-Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            //System.out.println("> HTTP_CLIENT_IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            //System.out.println("> HTTP_X_FORWARDED_FOR : " + ip);
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
            //System.out.println("> getRemoteAddr : "+ip);
        }
        System.out.println("> Result : IP Address : "+ip);

        return ip;
    }

}
