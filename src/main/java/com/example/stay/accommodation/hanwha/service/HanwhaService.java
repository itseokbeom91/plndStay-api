package com.example.stay.accommodation.hanwha.service;

import com.example.stay.common.service.CommonService;
import com.example.stay.common.util.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.w3c.dom.Node;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class HanwhaService {

    @Autowired
    private CommonService commonService;



    /**
     * 예약하기
     * @param
     * @return
     */
    public String booking(String strBookingID){ // 예약요청 : 01

        String result = "";

        try {
            JSONObject mainObject = getCommonHeader("01");
            JSONObject dataObject = new JSONObject();
            JSONObject detailObject = new JSONObject();


            detailObject.put("O", Constants.hanwhaCustNo);
            detailObject.put("MEMB_NO", "");
            detailObject.put("CUST_IDNT_NO", "");
            detailObject.put("CONT_NO", Constants.hanwhaContNo);
            detailObject.put("PAKG_NO", "");
            detailObject.put("CPON_NO", "");
            detailObject.put("LOC_CD", "0101");
            detailObject.put("ROOM_TYPE_CD", "FAM");
            detailObject.put("RSRV_LOC_DIV_CD", "C");
            detailObject.put("ARRV_DATE", "20231010");
            detailObject.put("RSRV_ROOM_CNT", "1");
            detailObject.put("OVNT_CNT", "1");
            detailObject.put("INHS_CUST_NM", "개발테스트");
            detailObject.put("INHS_CUST_TEL_NO2", "010");
            detailObject.put("INHS_CUST_TEL_NO3", "8633");
            detailObject.put("INHS_CUST_TEL_NO4", "1776");
            detailObject.put("RSRV_CUST_NM", "테스트개발");
            detailObject.put("RSRV_CUST_TEL_NO2", "010");
            detailObject.put("RSRV_CUST_TEL_NO3", "8633");
            detailObject.put("RSRV_CUST_TEL_NO4", "1776");
            detailObject.put("REFRESH_YN", "N");

            List<Object> dataList = new ArrayList<>();
            dataList.add(detailObject);

            dataObject.put("ds_rsrvInfo", dataList);

            mainObject.put("Data", dataObject);

            System.out.println(mainObject);

//            JsonNode jsonNode = commonService.callJsonApi("hanwha", mainObject);
//
//            result = jsonNode.toString();
//            System.out.println(result);

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 예약 취소
     * @return
     */
    public String bookingCancel(){ // 예약취소 : 02

        String result = "";

        try {
            JSONObject mainObject = getCommonHeader("02");
            JSONObject dataObject = new JSONObject();
            JSONObject detailObject = new JSONObject();


            detailObject.put("CUST_NO", Constants.hanwhaCustNo);
            detailObject.put("RSRV_NO", "");

            List<Object> dataList = new ArrayList<>();
            dataList.add(detailObject);

            dataObject.put("ds_cnclInfo", dataList);

            mainObject.put("Data", dataObject);

            System.out.println(mainObject);

            JsonNode jsonNode = commonService.callJsonApi("hanwha", mainObject);

            result = jsonNode.toString();
            System.out.println(result);

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    // 예약조회시 예약확정코드 RR : 확정예약, RC : 취소
    // test 예약넘버 : 2308799080

    /**
     * 예약 죄회
     * @return
     */
    public String bookingInfo(){ // 예약조회 : 03

        String result = "";

        try {
            JSONObject mainObject = getCommonHeader("03");
            JSONObject dataObject = new JSONObject();
            JSONObject detailObject = new JSONObject();


            detailObject.put("CUST_NO", Constants.hanwhaCustNo);
            detailObject.put("RSRV_NO", ""); // 예약번호
            detailObject.put("RSRV_DATE_STRT", ""); // 예약한 날짜 조회(투숙날 아님)
            detailObject.put("RSRV_DATE_END", "");

            List<Object> dataList = new ArrayList<>();
            dataList.add(detailObject);

            dataObject.put("ds_search", dataList);

            mainObject.put("Data", dataObject);

            System.out.println(mainObject);

            JsonNode jsonNode = commonService.callJsonApi("hanwha", mainObject);

            result = jsonNode.toString();
            System.out.println(result);

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 캐파조회
     * @param strAccommId
     * @param strRoomTypeId
     * @return
     */
    public String getCapa(String strAccommId, String strRoomTypeId, String strStartDate, String strEndDate){ // 캐파조회 : 05

        String result = "";

        try {
            JSONObject mainObject = getCommonHeader("05");
            JSONObject dataObject = new JSONObject();
            JSONObject detailObject = new JSONObject();


            detailObject.put("CUST_NO", Constants.hanwhaCustNo);
            detailObject.put("CONT_NO", Constants.hanwhaContNo);
            detailObject.put("LOC_CD", strAccommId);
            detailObject.put("ROOM_TYPE_CD", strRoomTypeId);
            detailObject.put("STRT_DATE", strStartDate);
            detailObject.put("END_DATE", strEndDate);

            List<Object> dataList = new ArrayList<>();
            dataList.add(detailObject);

            dataObject.put("ds_search", dataList);

            mainObject.put("Data", dataObject);

            System.out.println(mainObject);

            JsonNode jsonNode = commonService.callJsonApi("hanwha", mainObject);

            result = jsonNode.toString();
            System.out.println(result);

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 패키지 목록 조회
     * @param strAccommId
     * @param strStartDate
     * @return
     */
    public String getPackageList(String strAccommId, String strStartDate){ // 패키지 목록 조회 : 06

        String result = "";

        try {
            JSONObject mainObject = getCommonHeader("06");
            JSONObject dataObject = new JSONObject();
            JSONObject detailObject = new JSONObject();


            detailObject.put("CUST_NO", Constants.hanwhaCustNo);
            detailObject.put("CONT_NO", Constants.hanwhaContNo);
            detailObject.put("LOC_CD", strAccommId);
            detailObject.put("ARRV_DATE", strStartDate);

            List<Object> dataList = new ArrayList<>();
            dataList.add(detailObject);

            dataObject.put("ds_search", dataList);

            mainObject.put("Data", dataObject);

            // API 호출
            JsonNode jsonNode = commonService.callJsonApi("hanwha", mainObject);

            // 통신결과 0:실패, 1:성공
            JSONObject codeObject = (JSONObject) new JSONParser().parse(jsonNode.get("MessageHeader").get("MSG_DATA_SUB").get(0).toString());
            String resultCode = codeObject.get("MSG_INDC_CD").toString();

            // 결과값 매핑
            JSONArray jsonArray = (JSONArray) new JSONParser().parse(jsonNode.get("Data").get("ds_result").toString());
            if(resultCode.equals("1")){
                for(Object object : jsonArray){
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(object.toString());
                    System.out.println(jsonObject);
                }
            }

            result = jsonNode.toString();

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 패키지 구성 조회
     * @param strPackageCode
     * @return
     */
    public String getPackageDetail(String strPackageCode){ // 패키지 구성 조회 : 08

        String result = "";

        try {
            JSONObject mainObject = getCommonHeader("06");
            JSONObject dataObject = new JSONObject();
            JSONObject detailObject = new JSONObject();


            detailObject.put("PAKG_NO", strPackageCode);

            List<Object> dataList = new ArrayList<>();
            dataList.add(detailObject);

            dataObject.put("ds_search", dataList);

            mainObject.put("Data", dataObject);

            System.out.println(mainObject);

            JsonNode jsonNode = commonService.callJsonApi("hanwha", mainObject);

            result = jsonNode.toString();
            System.out.println(result);

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }


    public static JSONObject getCommonHeader(String type){

        JSONObject mainObject = new JSONObject();

        // type 별 변수 구하기
        String RECV_SVC_CD = "";
        String INTF_ID = "";
        if(type.equals("01")) {                                     // 01 : 예약요청
            RECV_SVC_CD = "HBSREMPRR9901";
            INTF_ID = "LCB00HBSREMPRR9901";
        }else if(type.equals("02")) {                               // 02 : 예약 취소
            RECV_SVC_CD = "HBSREMPRR9902";
            INTF_ID = "LCB00HBSREMPRR9902";
        }else if(type.equals("03")){                                // 03 : 예약 조히
            RECV_SVC_CD = "HBSREMPRR9903";
            INTF_ID = "LCB00HBSREMPRR9903";
        }else if(type.equals("05")){                                // 05 : 캐파조회
            RECV_SVC_CD = "HBSREMPRR9905";
            INTF_ID = "LCB00HBSREMPRR9905";
        }else if(type.equals("06")){                                // 06 : 패키지목록조회
            RECV_SVC_CD = "HBSREMPRR9906";
            INTF_ID = "LCB00HBSREMPRR9906";
        }else if(type.equals("08")){                                // 08 : 패키지구성조회
            RECV_SVC_CD = "HBSREMPRR9931";
            INTF_ID = "LCB00HBSREMPRR9931";
        }

        try {

            JSONObject systemObject = new JSONObject();
            JSONObject transactionObject = new JSONObject();
            JSONObject messageObject = new JSONObject();

            // 현재날짜 구하기(yyyyMMdd)
            LocalDate localDate = LocalDate.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String nowDate = localDate.format(dateFormatter);

            // 랜덤 숫자 5자리 구하기
            int intRandom = (int) Math.random()+10000;
            String strSystemNumber = "LCB" + String.format("%05d",intRandom);

            // 현재시간 unixtime 구하기
            String unix = String.valueOf(System.currentTimeMillis());
            String intunixRandom = String.valueOf((int)(Math.random()*10000)%10);
            String strSeqNo = intunixRandom + unix;
            System.out.println(strSeqNo);

            // ip 주소 구하기
            String ipAdress = getClientIP();
            System.out.println(ipAdress);

            // 현재시간 구하기(yyyyMMddHHmmss)
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String nowtime = format.format(date);
            System.out.println(nowtime);


            systemObject.put("TMSG_VER_DV_CD", "01");
            systemObject.put("ENVR_INFO_DV_CD", "D");
            systemObject.put("STN_MSG_ENCP_CD", "0");
            systemObject.put("STN_MSG_COMP_CD", "0");
            systemObject.put("LANG_CD", "KO");
            systemObject.put("TMSG_WRTG_DT", nowDate);
            systemObject.put("TMSG_CRE_SYS_NM", strSystemNumber);
            systemObject.put("STD_TMSG_SEQ_NO", strSeqNo);
            systemObject.put("STD_TMSG_PRGR_NO", "00");
            systemObject.put("STN_TMSG_IP", ipAdress);
            systemObject.put("STN_TMSG_MAC", "00-00-00-00-00-00");
            systemObject.put("FRS_RQST_SYS_CD", "LCB");
            systemObject.put("FRS_RQST_DTM", nowtime);
            systemObject.put("TRMS_SYS_CD", "LCB");
            systemObject.put("RQST_RSPS_DV_CD", "S");
            systemObject.put("TRSC_SYNC_DV_CD", "S");
            systemObject.put("TMSG_RQST_DTM", nowtime);

            transactionObject.put("STN_MSG_TR_TP_CD", "O");
            transactionObject.put("SYSTEM_TYPE", "HABIS");
            transactionObject.put("CORP_CD", "1000");
            transactionObject.put("WRKR_NO", "l1711019");
            transactionObject.put("MASK_AUTH", "0");


            systemObject.put("RECV_SVC_CD", RECV_SVC_CD);
            systemObject.put("INTF_ID", INTF_ID);


            mainObject.put("SystemHeader", systemObject);
            mainObject.put("TransactionHeader", transactionObject);
            mainObject.put("MessageHeader", messageObject);


        } catch (Exception e){
            e.printStackTrace();
        }

        return mainObject;

    }

    public static String getClientIP() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        System.out.println("> X-FORWARDED-FOR : " + ip);

        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
            System.out.println("> Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            System.out.println(">  WL-Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            System.out.println("> HTTP_CLIENT_IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            System.out.println("> HTTP_X_FORWARDED_FOR : " + ip);
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
            System.out.println("> getRemoteAddr : "+ip);
        }
        System.out.println("> Result : IP Address : "+ip);

        return ip;
    }

}
