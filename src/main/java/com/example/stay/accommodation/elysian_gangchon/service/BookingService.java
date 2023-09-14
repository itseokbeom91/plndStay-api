package com.example.stay.accommodation.elysian_gangchon.service;

import com.example.stay.accommodation.elysian_gangchon.mapper.ElysianMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.openMarket.common.dto.RsvStayDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("elysian_gangchon.BookingService")
public class BookingService{

    @Autowired
    private ElysianMapper elysianMapper;

    CommonFunction commonFunction = new CommonFunction();

    // 재고 등록 및 수정
    public String updatePackagetock(String dataType, HttpServletRequest httpServletRequest, String startDate, String endDate, int intRmIdx){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            String strPkgCode = elysianMapper.getStrPkgCode(intRmIdx);

//            strPkgCode = "90004884";
            String elysUrl = "type=SB&pcode=" + strPkgCode + "&sdate=" + startDate + "&edate=" + endDate;

            String strResponse = callElysAPI(elysUrl);

            if(strResponse != null && !strResponse.equals("")){
                int intAID = elysianMapper.getIntAID(intRmIdx);

                String strStockDatas = "";
                String[] responseArr = strResponse.split("#");
                for(String arr : responseArr){
                    String[] dataArr = arr.split(";");

                    String dateSales = dataArr[2];
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    dateSales = sdf.format(simpleDateFormat.parse(dateSales));

                    int intStock = Integer.parseInt(dataArr[4]);
                    int intOmkStock = intStock;

                    int intCost = 0, intSales = 0, intExtraA = 0, intExtraB = 0, intExtraC = 0, intOmkSales = 0;

                    strStockDatas += dateSales + "|^|" + intStock + "|^|" + intCost + "|^|" + intSales + "|^|"
                            + intExtraA + "|^|" + intExtraC + "|^|" + intExtraB + "|^|" + intOmkStock + "|^|"  + intOmkSales+ "{{|}}";

                }
                strStockDatas = strStockDatas.substring(0, strStockDatas.length()-5);

                String result = elysianMapper.updateGoods(intAID, intRmIdx, strStockDatas);
                String strResult = result.substring(result.length()-4);

                if(strResult.equals("저장완료")){
                    message = "재고 등록 및 수정 완료";
                }else{
                    message = " 재고 등록 및 수정 실패";
                }

            }else{
                message = "엘리시안 API 호출 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            message = "재고 등록 및 수정 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
            e.printStackTrace();
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
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

        try{
//            String mdn  = "01011111111";
//            String name  = "개발테스트";
//            String pcode  = "90004884";
//            String pcode_seq  = "1";
//            String bdate  = "20230817";
//            int cnt = 1;
//            String tseq  = "980";
//            String DH_CODE1 = "1030";
//            String DH_CODE2 = "9999";
//            String PASS = "1234";
//            String AMT_YN = "N";

            RsvStayDto rsvStayDto = elysianMapper.getReservation(intRsvID);

            String mdn  = rsvStayDto.getStrOrdPhone();
            String name  = rsvStayDto.getStrOrdName();
            String pcode = rsvStayDto.getStrMapCode();

            String strPkgSubCode  = rsvStayDto.getStrPkgSubCode();
//            String pkgSubArr [] = strPkgSubCode.split("-");
            
            Date dateCheckIn = rsvStayDto.getDateCheckIn();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateCheckIn);
            int intWeek = calendar.get(Calendar.DAY_OF_WEEK); // 요일 1 : 일 ~ 7 : 토

//            String pcode_seq = pkgSubArr[intWeek];

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String bdate = sdf.format(rsvStayDto.getDateCheckIn()); // 도착일자

            int cnt = rsvStayDto.getIntRmCnt();
            int tseq  = elysianMapper.getTseq() + 1;
            String DH_CODE1 = "1006";
            String DH_CODE2 = "1030";
            String PASS = "1234";
            String AMT_YN = "N";

            // 예약가능 여부 확인
//            boolean avail = checkAvailBooking(pcode, pcode_seq, bdate, cnt);
//            if(avail){
//                String elysUrl = "type=RO&mdn=" + mdn + "&name=" + URLEncoder.encode(name, "EUC-KR") + "&pcode=" + pcode + "&pcode_seq=" + pcode_seq +
//                        "&bdate="+ bdate + "&cnt="+ cnt + "&tseq="+ tseq + "&DH_CODE1=" + DH_CODE1 + "&PASS=" + PASS + "&DH_CODE2=" + DH_CODE2 + "&AMT_YN=" + AMT_YN;
//                String strResponse = callElysAPI(elysUrl);

                String strResponse = "OK;751FK0PD;980;정상처리";

                if(strResponse != null && !strResponse.equals("")){
                    if(strResponse.substring(0,5).equals("ERROR")){
                        message = strResponse;
                    }else{
                        String[] responseArr = strResponse.split("#");
                        for(String arr : responseArr){
                            String[] dataArr = arr.split(";");

                            // 예약 테이블 상태값 업데이트
                            String strRsvRmNum = dataArr[1];

//                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                            String strCheckIn = simpleDateFormat.format(rsvStayDto.getDateCheckIn());
//                            String strCheckOut = simpleDateFormat.format(rsvStayDto.getDateCheckOut());

                            // 위약금 데이터 생성--------------------------------------------------------------------------
                            
                            // 숙박일 중 성수기 포함 여부 확인
//                            int peakCount = elysianMapper.getPeakCount(strCheckIn, strCheckOut);
//                            // 하루라도 성수기 포함시 성수기 취소규정 적용
//                            String strFlag = "";
//                            if(peakCount > 0){
//                                strFlag = "OPS";
//                            }else{
//                                strFlag = "OOF";
//                            }
//
//                            // 해당 취소규정 조회
//                            int intAID = rsvStayDto.getIntAID();
//                            intAID = 11149;
//                            List<CancelRulesDto> cancelRuleList = elysianMapper.getCancelRules(intAID, strFlag);
//                            String strPenaltyDatas = "";
//                            String strRateFlag = "P";
//
//                            // 오픈마켓별 판매금액 조회
//                            double sales = elysianMapper.getOmkSales(intRsvID);
//
//                            for(int i=0; i<cancelRuleList.size(); i++){
//                                CancelRulesDto cancelRulesDto = cancelRuleList.get(i);
//                                double doubleRate = cancelRulesDto.getIntPercent();
//                                int intDay = cancelRulesDto.getIntDay();
//
//                                // 체크인 날짜 - intDay가 며칠인지 구하기
//                                Calendar cal = Calendar.getInstance();
//                                cal.setTime(dateCheckIn);
//                                cal.add(Calendar.DATE, -intDay);
//                                Date endDate = cal.getTime();
//
//                                // 체크인 날짜 - intDay가 무슨 요일인지 구하기
//                                Calendar cal2 = Calendar.getInstance();
//                                cal2.setTime(endDate);
//                                int businessWeek = cal2.get(Calendar.DAY_OF_WEEK); // 요일 1 : 일 ~ 7 : 토
//
//                                // 평일은 5시까지, 토요일은 12시까지, 일요일은 불가 -> 다음날 위약금 적용
//                                String strTime = "";
//                                if(businessWeek > 1 && businessWeek < 7){ // 평일
//                                    strTime = "16:59:59";
//                                }else if(businessWeek == 7){ // 토요일
//                                    strTime = "11:59:59";
//                                }else{
//                                    strTime = "23:59:59";
//                                    if(i == 0){ // 일요일
//                                        doubleRate = 100;
//                                    }else{
//                                        CancelRulesDto cancelRule = cancelRuleList.get(i-1);
//                                        doubleRate = cancelRule.getIntPercent();
//                                    }
//                                }
//
//                                double rate = (doubleRate / 100);
//                                double penalty = sales * rate; // 위약금액
//                                double refund = sales - penalty; // 환불금액
//
//                                strPenaltyDatas += intRsvID + "|^|" + strRateFlag + "|^|" + doubleRate + "|^|" + intDay + "|^|" + strTime + "|^|" + refund + "|^|" + penalty + "{{|}}";
//                            }
//
//                            strPenaltyDatas = strPenaltyDatas.substring(0, strPenaltyDatas.length()-5);



                            String strPenaltyDatas = commonFunction.makeCancelRules(rsvStayDto);
//                            String strPenaltyDatas = commonFunction.makeCancelRules(rsvStayDto);
                            if(!strPenaltyDatas.equals("")){
                                message = "성공";
                                System.out.println("Result : " + strPenaltyDatas);
                            }else{
                                message = "위약금 규정 생성 실패";
                            }


                            // -----------------------------------------------------------------------------------------

//                            String result = elysianMapper.updateRsvStay(intRsvID, "4", strRsvRmNum);
//                            if(result.equals("저장완료")){
//                                message = "예약완료";
//                            }else{
//                                message = "예약실패";
//                            }
                        }
                    }
                }else{
                    message = "엘리시안 API 호출 실패";
                }
//            }else{
//                message = "예약 불가";
//            }

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
        Map<String, Object> resultMap = new HashMap<>();
        try{
            String strRsvRmNum = elysianMapper.getStrRsvRmNum(intRsvID);

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
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date dateResult = simpleDateFormat.parse(resultDate);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    resultDate = sdf.format(dateResult);

                    resultMap.put("예약번호", strRsvRmNum);
                    resultMap.put("예약자 휴대폰번호", strOrdPhone);
                    resultMap.put("예약자명", strOrdName);
                    resultMap.put("예약 상태", strRsvStatus);
                    resultMap.put("입실일/취소일시", resultDate);

                    message = "예약 조회 완료";
                }
            }else{
                message = "엘리시안 API 호출 실패";
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
        return commonFunction.makeReturn(dataType, statusCode, message, resultMap);
    }

    // 예약 취소
    public String cancelBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        try{
            String strRsvRmNum = elysianMapper.getStrRsvRmNum(intRsvID);

            String elysUrl = "type=CO&bno=" + strRsvRmNum;
            String strResponse = callElysAPI(elysUrl);

            if(strResponse != null && !strResponse.equals("")){
                if(strResponse.substring(0,4).equals("error")){
                    message = strResponse;
                }else{
                    // 예약 테이블 상태값 업데이트
                    String result = elysianMapper.updateRsvStay(intRsvID, "5", strRsvRmNum);
                    if(result.equals("저장완료")){
                        message = "예약 취소 완료";
                    }else{
                        message = "예약 취소 실패";
                    }
                }
            }else{
                message = "엘리시안 API 호출 실패";
            }

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

            System.out.println("code : " + conn.getResponseCode());

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
