package com.example.stay.accommodation.elysian_gangchon.service;

import com.example.stay.accommodation.elysian_gangchon.mapper.ElysianMapper;
import com.example.stay.common.mapper.CommonAcmMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.openMarket.common.dto.RsvStayDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.metadata.ManagedOperation;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("elysian_gangchon.BookingService")
public class BookingService extends CommonFunction{

    @Autowired
    private ElysianMapper elysianMapper;

    @Autowired
    private CommonAcmMapper commonAcmMapper;

    CommonFunction commonFunction = new CommonFunction();

//    // 예약 가능 수량 조회(재고 등록 및 수정)
//    public String updatePackageStock(String dataType, HttpServletRequest httpServletRequest, String startDate, String endDate, int intRmIdx){
//        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
//                httpServletRequest.getQueryString(), System.currentTimeMillis());
//        String statusCode = "200";
//        String message = "";
//
//        try{
//            List<Map<String, String>> strMapCodeList = elysianMapper.getStrPkgCodeList(intRmIdx, startDate, endDate);
//            for(Map map : strMapCodeList){
//                Map<String, String> MapCodeMap = map;
//                String strMapCode = MapCodeMap.get("strMapCode");
//                String dateMapping = MapCodeMap.get("dateMapping");
//
////                strMapCode = "90004884";
//
//                String elysUrl = "type=SB&pcode=" + strMapCode + "&sdate=" + dateMapping + "&edate=" + dateMapping;
////
//                String strResponse = callElysAPI(elysUrl);
//
//                if(strResponse != null && !strResponse.equals("")){
//                    int intAID = elysianMapper.getIntAID(intRmIdx);
//
//                    String strStockDatas = "";
////                    String[] responseArr = strResponse.split("#");
////                    for(String arr : responseArr){
//                    String[] dataArr = strResponse.replace("#", "").split(";");
//
//                        String dateSales = dataArr[2];
//                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                        dateSales = sdf.format(simpleDateFormat.parse(dateSales));
//
//                        int intStock = Integer.parseInt(dataArr[4]);
//                        int intOmkStock = intStock;
//
////                        int intCost = 0, intSales = 0, intExtraA = 0, intExtraB = 0, intExtraC = 0, intOmkSales = 0;
//
//                        strStockDatas += dateSales + "|^|" + intStock + "|^|0|^|0|^|0|^|0|^|0|^|" + intOmkStock + "|^|0";
//
////                    }
////                    strStockDatas = strStockDatas.substring(0, strStockDatas.length()-5);
//
//                    String result = elysianMapper.updateGoods(intAID, intRmIdx, strStockDatas);
//                    String strResult = result.substring(result.length()-4);
//
//                    if(strResult.equals("저장완료")){
//                        message = "재고 등록 및 수정 완료";
//                    }else{
//                        message = " 재고 등록 및 수정 실패";
//                    }
//
//                }else{
//                    message = "엘리시안 API 호출 실패";
//                }
//            }
//
//            logWriter.add(message);
//            logWriter.log(0);
//        }catch (Exception e){
//            message = "재고 등록 및 수정 실패";
//            statusCode = "500";
//            logWriter.add("error : " + e.getMessage());
//            logWriter.log(0);
//            e.printStackTrace();
//        }
//
//        return commonFunction.makeReturn(dataType, statusCode, message);
//    }

    // 예약 가능 수량 조회(재고 조회)
    @Async
    public int getAvailCount(int intAID, int intRmIdx, String strMapCode, String strDateMapping){
        int intFailCount = 0;

        try{
            strDateMapping = strDateMapping.replace("-", "");
            String elysUrl = "type=SB&pcode=" + strMapCode + "&sdate=" + strDateMapping + "&edate=" + strDateMapping;

            String strResponse = callElysAPI(elysUrl);

            if(strResponse != null && !strResponse.equals("")){
                String strStockDatas = "";
                String[] dataArr = strResponse.replace("#", "").split(";");

                String dateSales = dataArr[2];
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                dateSales = sdf.format(simpleDateFormat.parse(dateSales));

                int intStock = Integer.parseInt(dataArr[4]);
                int intOmkStock = intStock;

                strStockDatas += strDateMapping + "|^|" + intStock + "|^|0|^|0|^|0|^|0|^|0|^|" + intOmkStock + "|^|0";

                String result = commonAcmMapper.updateGoods(intAID, intRmIdx, strStockDatas);

                String strResult = result.substring(result.length()-4);
                if(!strResult.equals("저장완료")){
                    intFailCount +=1;
                }
            }else{
                intFailCount +=1;
                System.out.println("엘리시안 API 호출 실패");
            }
        }catch (Exception e){
            e.printStackTrace();
            intFailCount +=1;
        }
        return intFailCount;
    }

    // 예약 가능여부 조회
    public boolean checkAvailBooking(String pcode, String pcode_seq, String sdate, int cnt){
        LogWriter logWriter = new LogWriter(System.currentTimeMillis());
        String message = "";
        boolean avail = false;
        try{
            String elysUrl = "type=SB&pcode=" + pcode + "&pcode_seq=" + pcode_seq + "&sdate=" + sdate + "&edate=" + sdate;

            String strResponse = callElysAPI(elysUrl);

            if(strResponse != null && !strResponse.equals("")){
                String[] responseArr = strResponse.split("#");
                for(String arr : responseArr){
                    String[] dataArr = arr.split(";");
                    String strAvail = dataArr[3];
                    if(strAvail.equals("Y")){
                        // 예약가능여부가 Y값이더라도 예약가능 수량까지 확인해야함
                        int intAvailCnt = Integer.parseInt(dataArr[4]);
                        if(intAvailCnt >= cnt){
                            avail = true;
                        }
                    }
                }
            }else{
                message = "엘리시안 API 호출 실패";
            }
        }catch (Exception e){
            message = "예약 가능여부 조회 실패";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        logWriter.add(message);
        logWriter.log(0);

        return avail;
    }

    // 예약
    public String createBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        try {

            RsvStayDto rsvStayDto = elysianMapper.getReservation(intRsvID);

            int intAID = rsvStayDto.getIntAID();
            int intRmIdx = rsvStayDto.getIntRmIdx();

            String mdn = rsvStayDto.getStrOrdPhone();
            String name = rsvStayDto.getStrOrdName();
            String pcode = rsvStayDto.getStrMapCode();

            String strPkgSubCode = rsvStayDto.getStrPkgSubCode();
            String pkgSubArr[] = strPkgSubCode.split("-");

            int cnt = rsvStayDto.getIntRmCnt();

            int tseq = elysianMapper.getTseq() + 1;

            String AMT_YN = "N";

            // 위약금 규정 생성
            String strPenaltyDatas = makeCancelRules(rsvStayDto);

            // 몇박인지 계산
            Date dateOrgCheckIn = rsvStayDto.getDateCheckIn();
            Date dateOrgCheckOut = rsvStayDto.getDateCheckOut();
            int stayDays = (int) ((dateOrgCheckOut.getTime() - dateOrgCheckIn.getTime()) / 1000) / (24 * 60 * 60);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            int falseCnt = 0;
            List<String> bdateList = new ArrayList<>();
            List<String> pcodeSeqList = new ArrayList<>();
            for (int i = 0; i < stayDays; i++) {
                // 예약 넣을 날짜 구하기
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateOrgCheckIn);
                cal.add(Calendar.DATE, i);
                Date dateCheckIn = cal.getTime();
                
                // 예약 넣을 날짜가 무슨 요일인지 구하기
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateCheckIn);
                int intWeek = calendar.get(Calendar.DAY_OF_WEEK); // 요일 1 : 일 ~ 7 : 토

                String pcode_seq = pkgSubArr[intWeek - 1];
                pcodeSeqList.add(pcode_seq);

                String bdate = sdf.format(dateCheckIn); // 체크인 일자 리스트에 추가
                bdateList.add(bdate);

                // 예약가능 여부 확인 - 예약한 모든 날짜
                boolean avail = checkAvailBooking(pcode, pcode_seq, bdate, cnt);

                if (!avail) {
                    falseCnt++;
                }
            }

            String strContentCode = "200";
            String strProcedure = "";
            // 모든 방이 가능할 때 예약
            if (falseCnt == 0) {
                int apiFail = 0;
                String strRmNumDatas = "";
                for (int i = 0; i < stayDays; i++) {
                    String pcode_seq = pcodeSeqList.get(i);
                    String bdate = bdateList.get(i);

                    String elysUrl = "type=RO&mdn=" + mdn + "&name=" + URLEncoder.encode(name, "EUC-KR") + "&pcode=" + pcode + "&pcode_seq=" + pcode_seq +
                            "&bdate=" + bdate + "&cnt=" + cnt + "&tseq=" + tseq + "&DH_CODE1=" + Constants.elys_DH_CODE1 + "&PASS=" + Constants.ely_PASS + "&DH_CODE2=" + Constants.elys_DH_CODE2 + "&AMT_YN=" + AMT_YN;
                    String strResponse = callElysAPI(elysUrl);

                    String strRsvRmNum = "";

                    if (strResponse != null && !strResponse.equals("")) {
                        if (strResponse.substring(0, 5).equals("ERROR")) {
                            logWriter.add(strResponse);
                            apiFail ++;
                        } else {
                            String[] responseArr = strResponse.split("#");
                            String[] dataArr = null;
                            for (String arr : responseArr) {
                                dataArr = arr.split(";");
                            }

                            strRsvRmNum = dataArr[1];
                        }
                    } else {
                        logWriter.add("엘리시안 API 호출 실패");
                        apiFail ++;
                    }

                    for(int j=1; j<=cnt; j++){
                        // TODO : intSID 수정
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date dateSales = sdf.parse(bdate);
                        String strDate = simpleDateFormat.format(dateSales);

                        Map<String, Integer> priceMap = elysianMapper.getPrice(intAID, intRmIdx, strDate);
                        int intAcmCost = priceMap.get("moneyCost");
                        int intAcmSales = priceMap.get("moneySales");

                        strRmNumDatas += j + "|^|" + strDate +  "|^|" + strRsvRmNum + "|^|" + pcode + "|^|" + intAcmCost + "|^|" + intAcmSales + "|^|C24|^|" + 148 + "{{|}}";
                    }
                }

                strRmNumDatas = strRmNumDatas.substring(0, strRmNumDatas.length()-5);

                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("intRsvID", intRsvID);
                dataMap.put("strStatusCode", Constants.rsvStatus_rsv_complete);
                dataMap.put("strRmNumDatas", strRmNumDatas);
                dataMap.put("strPenaltyDatas", strPenaltyDatas);

                strProcedure = commonFunction.makeStrProcedure("spGW_RSV_STAY_UPDATE_PROCESS", dataMap);

                // DB update & insert
                String result = elysianMapper.updateRsvStay(intRsvID, Constants.rsvStatus_rsv_complete, strRmNumDatas, strPenaltyDatas);

                // 예약 api 실패 있는 경우
                if(apiFail != 0) {
                    strContentCode = "500";
                    message = "예약 " + apiFail + "건 실패";

                    if (result.equals("저장완료")) {
                        message += "\nDB 저장 완료";

                    } else {
                        message += "\nDB 저장 실패";
                    }
                }else{ // 예약 api 실패 없는 경우
                    if (result.equals("저장완료")) {
                        message = "예약완료";
                    } else {
                        message = "DB 저장 실패";
                    }
                }

                // api history
                commonAcmMapper.insertRsvStayHistory(intRsvID, "C24", "[" + strContentCode +"]" + Constants.rsv_history_rsv, strProcedure, "", 148);

            }else{
                message = "예약 불가";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            message = "예약 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
            e.printStackTrace();
        }
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 예약 - 날짜로
    public String createBookingByDate(String dataType, int intRsvID, String startDate, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        try{
            RsvStayDto rsvStayDto = elysianMapper.getReservation(intRsvID);

            int intAID = rsvStayDto.getIntAID();
            int intRmIdx = rsvStayDto.getIntRmIdx();

            String mdn  = rsvStayDto.getStrOrdPhone();
            String name  = rsvStayDto.getStrOrdName();
            String pcode = rsvStayDto.getStrMapCode();

            String strPkgSubCode  = rsvStayDto.getStrPkgSubCode();
            String pkgSubArr [] = strPkgSubCode.split("-");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            Date dateCheckIn = sdf.parse(startDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateCheckIn);
            int intWeek = calendar.get(Calendar.DAY_OF_WEEK); // 요일 1 : 일 ~ 7 : 토

            String pcode_seq = pkgSubArr[intWeek-1];

            String bdate = startDate.replace("-", ""); // 도착일자

            int cnt = rsvStayDto.getIntRmCnt();
            int tseq  = elysianMapper.getTseq() + 1;
            String AMT_YN = "N";

            // 위약금 규정 생성
            String strPenaltyDatas = makeCancelRules(rsvStayDto);

            String strContentCode = "200";
            // 예약가능 여부 확인
            boolean avail = checkAvailBooking(pcode, pcode_seq, bdate, cnt);
            if(avail){
                String elysUrl = "type=RO&mdn=" + mdn + "&name=" + URLEncoder.encode(name, "EUC-KR") + "&pcode=" + pcode + "&pcode_seq=" + pcode_seq +
                        "&bdate="+ bdate + "&cnt="+ cnt + "&tseq="+ tseq + "&DH_CODE1=" + Constants.elys_DH_CODE1 + "&PASS=" + Constants.ely_PASS + "&DH_CODE2=" + Constants.elys_DH_CODE2 + "&AMT_YN=" + AMT_YN;

                String strResponse = callElysAPI(elysUrl);

                String strProcedure = "";
                if(strResponse != null && !strResponse.equals("")){
                    if(strResponse.substring(0,5).equals("ERROR")){
                        message = strResponse;
                        strContentCode = "500";
                    }else{
                        String[] responseArr = strResponse.split("#");
                        String[] dataArr = null;

                        for(String arr : responseArr){
                            dataArr = arr.split(";");
                        }

                        String strRsvRmNum = dataArr[1];
                        String strRmNumDatas = "";
                        for(int i=1; i<=cnt; i++){
                            // TODO : intSID 수정
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date dateSales = sdf.parse(bdate);
                            String strDate = simpleDateFormat.format(dateSales);

                            Map<String, Integer> priceMap = elysianMapper.getPrice(intAID, intRmIdx, strDate);
                            int intAcmCost = priceMap.get("moneyCost");
                            int intAcmSales = priceMap.get("moneySales");

                            strRmNumDatas += i + "|^|" + strDate +  "|^|" + strRsvRmNum + "|^|" + pcode + "|^|" + intAcmCost + "|^|" + intAcmSales + "|^|C24|^|" + 148 + "{{|}}";
                        }
                        strRmNumDatas = strRmNumDatas.substring(0, strRmNumDatas.length()-5);

                        Map<String, Object> dataMap = new HashMap<>();
                        dataMap.put("intRsvID", intRsvID);
                        dataMap.put("strStatusCode", Constants.rsvStatus_rsv_complete);
                        dataMap.put("strRmNumDatas", strRmNumDatas);
                        dataMap.put("strPenaltyDatas", strPenaltyDatas);

                        strProcedure = commonFunction.makeStrProcedure("spGW_RSV_STAY_UPDATE_PROCESS", dataMap);

                        String result = elysianMapper.updateRsvStay(intRsvID, Constants.rsvStatus_rsv_complete, strRmNumDatas, strPenaltyDatas);
                        if(result.equals("저장완료")){
                            message = "예약완료";
                        }else{
                            message = "예약실패";
                        }
                    }
                }else{
                    strContentCode = "500";
                    message = "엘리시안 API 호출 실패";
                }

                // api history
                commonAcmMapper.insertRsvStayHistory(intRsvID, "C24", "[" + strContentCode +"]" + Constants.rsv_history_rsv, strProcedure, "", 148);
            }else{
                message = "예약 불가";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            message = "예약 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
            e.printStackTrace();
        }
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 예약 조회
    public String getBookingInfo(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        List<Map<String, Object>> resultMapList = new ArrayList<>();
        try{
            List<String> rsvRmNumList = elysianMapper.getStrRsvRmNum(intRsvID);

            for(String strRsvRmNum : rsvRmNumList) {
                Map<String, Object> resultMap = new HashMap<>();

                String elysUrl = "type=SO&bno=" + strRsvRmNum;
                String strResponse = callElysAPI(elysUrl);

                if(strResponse != null && !strResponse.equals("")){
                    if(strResponse.substring(0,4).equals("error")){
                        message = strResponse;
                    }else{
                        String[] dataArr = strResponse.split(";");
                        String strOrdPhone = dataArr[2];
                        String strOrdName = dataArr[3];

                        String strRsvStatus = dataArr[4];
                        if(strRsvStatus.equals("A")){
                            strRsvStatus = "예약";
                        }else if(strRsvStatus.equals("C")){
                            strRsvStatus = "취소";
                        }

                        String resultDate = dataArr[5];
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                        Date dateResult = simpleDateFormat.parse(resultDate);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        resultDate = sdf.format(dateResult);

                        resultMap.put("예약번호", strRsvRmNum);
                        resultMap.put("예약자 휴대폰번호", strOrdPhone);
                        resultMap.put("예약자명", strOrdName);
                        resultMap.put("예약 상태", strRsvStatus);
                        resultMap.put("입실일/취소일시", resultDate);

                        resultMapList.add(resultMap);

                        message = "예약 조회 완료";
                    }
                }else{
                    message = "엘리시안 API 호출 실패";
                }
            }

            logWriter.add(message);
            logWriter.log(0);

        }catch (Exception e){
            message = "예약 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
            e.printStackTrace();
        }
        return commonFunction.makeReturn(dataType, statusCode, message, resultMapList);
    }

    // 예약 취소
    public String cancelBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        try{
            List<String> rsvRmNumList = elysianMapper.getStrRsvRmNum(intRsvID);

            String strContentCode = "200";
            int apiFail = 0;
            String strRmNumDatas = "";
            for(String strRsvRmNum : rsvRmNumList){
                String elysUrl = "type=CO&bno=" + strRsvRmNum;
                String strResponse = callElysAPI(elysUrl);

                if(strResponse != null && !strResponse.equals("")){
                    if(strResponse.substring(0,4).equals("error")){
                        apiFail ++;
                        logWriter.add(strResponse);
                    }else{
                        // TODO : intSID 수정
                        strRmNumDatas += "C24|^|" + 148 + "{{|}}";
                    }
                }else{
                    apiFail ++;
                    logWriter.add("엘리시안 API 호출 실패");
                }
            }
            strRmNumDatas = strRmNumDatas.substring(0, strRmNumDatas.length()-5);

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("intRsvID", intRsvID);
            dataMap.put("strStatusCode", Constants.rsvStatus_rsv_complete);
            dataMap.put("strRmNumDatas", strRmNumDatas);
            dataMap.put("strPenaltyDatas", "");

            String strProcedure = commonFunction.makeStrProcedure("spGW_RSV_STAY_UPDATE_PROCESS", dataMap);

            // 예약 테이블 상태값 업데이트
            String result = elysianMapper.updateRsvStay(intRsvID, Constants.rsvStatus_cancel_complete, strRmNumDatas, "");
            // 예약취소 api 실패 있는 경우
            if(apiFail != 0) {
                strContentCode = "500";
                message = "예약취소 " + apiFail + "건 실패";

                if (result.equals("저장완료")) {
                    message += "\nDB 저장 완료";

                } else {
                    message += "\nDB 저장 실패";
                }
            }else{ // 예약취소 api 실패 없는 경우
                if (result.equals("저장완료")) {
                    message = "예약취소 완료";
                } else {
                    message = "DB 저장 실패";
                }
            }

            // api history
            commonAcmMapper.insertRsvStayHistory(intRsvID, "C24", "[" + strContentCode +"]" + Constants.rsvStatus_cancel_complete, strProcedure, "", 148);

            logWriter.add(message);
            logWriter.log(0);

        }catch (Exception e){
            message = "예약 취소 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
            e.printStackTrace();
        }
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    public String callElysAPI(String elysUrl){
        String method = "";
        String strUrl = "";
        String message = "";
        String strResponse = "";
        long startTime = System.currentTimeMillis();

        try{
            URL url = new URL(Constants.elysUrl + elysUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.setRequestProperty("Accept-Charset", "UTF-8");

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                method = conn.getRequestMethod();
                strUrl = conn.getURL().toString();

                strResponse = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "EUC-KR"));
                StringBuffer sb = new StringBuffer();
                while ((strResponse = br.readLine()) != null) {
                    sb.append(strResponse);
                }
                strResponse = sb.toString();
                message = strResponse;

            }else{
                message = "엘리시안 강촌 API 호출 실패";
            }

            conn.disconnect();

            LogWriter logWriter = new LogWriter(conn.getRequestMethod(), conn.getURL().toString(), startTime);
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            LogWriter logWriter = new LogWriter(method, strUrl, startTime);
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
            e.printStackTrace();
        }
        return strResponse;
    }

}
