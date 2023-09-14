package com.example.stay.openMarket.eland.controller;

import com.example.stay.common.util.Constants;
import com.example.stay.openMarket.common.dto.BookingDto;
import com.example.stay.openMarket.common.dto.CondoDto;
import com.example.stay.openMarket.eland.service.ElandService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Controller
@RequestMapping("/eland/*")
public class ElandController {

    @Autowired
    private ElandService elandService;


    @GetMapping("makeCookie")
    public void makeCookie(HttpServletResponse response){

        Cookie cookie = new Cookie("elandCookie", "test");
        cookie.setMaxAge(60*60*24);
        cookie.setPath("/");
        response.addCookie(cookie);

    }

    @GetMapping("deleteCookie")
    public void deleteCookie(HttpServletResponse response){

        Cookie cookie = new Cookie("elandCookie", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

    }


    /**
     * 인증키 요청
     * 추후 발급받은 ACCESS TOKEN DB에 저장 X -> 쿠키로 사용할 것
     * elandmall_openapi_guide
     */
    @GetMapping("/requestToken")
    public void requestToken(HttpServletResponse httpResponse){
        elandService.requestToken(httpResponse);
    }


    /**
     * 인증키 유효성 확인
     * 이랜드몰_OPEN API 연동표준안_공통 000_(인증키유효성확인)
     */
    @GetMapping("/TokenValidation")
    public void accessTokenValidation(String accessToken){
        BufferedReader br = null;
        try {
            // API 호출 정보
            URL url = new URL(Constants.elandPath + "/token/checkAccessTokenValidation.action");

            // API 호출
            long APIStart = System.currentTimeMillis();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("grant_type", "client_credentials");
            conn.setRequestProperty("Authorization", "Bearer "+accessToken);
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Accept-Charset", "UTF-8");

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                String strResponse = "";
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuffer sb = new StringBuffer();
                while ((strResponse = br.readLine()) != null) {
                    sb.append(strResponse);
                }
                strResponse = sb.toString();
                JSONParser jsonParser = new JSONParser();
                Object objData = jsonParser.parse(strResponse);
                JSONObject resultJson = (JSONObject) objData;

                if(resultJson.get("error").equals("98")){ // Access Token 유효함
                    System.out.println("AccessToken이 유효하지 않습니다.");
                }

                System.out.println(resultJson);
            }else{
                System.out.println("AccessToken 유효성체크 api 통신 실패");
                System.out.println("responseCode : " + conn.getResponseCode() + "\nresponseMessage : " + conn.getResponseMessage());
            }

            conn.disconnect();
            System.out.println("이랜드 API 호출 실행 시간 : " + (System.currentTimeMillis()-APIStart)/1000.0);

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("AccessToken 유효성체크 실패");
        }finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 주문정보 가져오기
     * 이랜드몰_OPEN API 연동표준안_배송 001_(출고지시 조회)
     */
    @GetMapping("getOrder")
    public void getOrder(HttpServletResponse httpResponse){
        try{
            // 주문정보 api 호출할 데이터 세팅
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            Calendar calStart_Date = Calendar.getInstance();
            calStart_Date.setTime(date);
            calStart_Date.add(Calendar.DATE, -1);
            String strStartDate = dateFormat.format(calStart_Date.getTime());
            String strEndDate = dateFormat.format(date.getTime());

            String strPostBody = "start_date=" + strStartDate + "&end_date=" + strEndDate;

            // 주문정보 가져오기
            JSONArray jsonArrayData = elandService.elandApi(httpResponse, "/order/searchDeliIndiList.action", strPostBody);

            String strOrdName = "";
            for (int i=0; i<jsonArrayData.size(); i++) {
                BookingDto bookingDto = new BookingDto();

                JSONObject jsonData = (JSONObject) jsonArrayData.get(i);

                // 가져온 주문정보
                String strCellNo = (String) jsonData.get("cell_no"); // 휴대폰번호 -> ordPhone
                String strDeliMemoCont = (String) jsonData.get("deli_memo_cont"); // 주문 메모 내용
                String strDeliNo = (String) jsonData.get("deli_no"); // 배송 번호
//                String strDeliSeq = (String) jsonData.get("deli_seq"); // 배송 순번
                String strGoodsNo = (String) jsonData.get("goods_no"); // 상품 번호 -> st11_pid
                int intIndiQty = Integer.parseInt(jsonData.get("indi_qty").toString()); // 지시 수량
                String strItemNm = (String) jsonData.get("item_nm"); // 단품명
                String strOrdNo = (String) jsonData.get("ord_no"); // 주문 번호
                String strOrdererNm = (String) jsonData.get("orderer_nm"); // 주문자명
                String strRecvrCellNo = (String) jsonData.get("recvr_cell_no"); // 수취인 휴대폰번호 -> recvPhone
                String strRecvrNm = (String) jsonData.get("recvr_nm"); // 수취인명 -> recvName
                int intSalePrice = Integer.parseInt(jsonData.get("sale_price").toString());; // 판매가격
                String strSupplyPrice = (String) jsonData.get(" supply_price"); // 공급가격
                String strVendGoodsNo = (String) jsonData.get("vend_goods_no"); // 업체 상품 번호 -> vend_goods_no -> con_id

                // 주문자명
                strOrdName = "ELAND( " + strOrdererNm + ")";

                // 숙박 날짜 세팅
                String[] arrItemName = strItemNm.split("/");
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
                String strEnterIn = dateFormat2.format(dateFormat2.parse(arrItemName[0]));

                // OrderId 세팅
                String strDate = arrItemName[0].replace("-", "");
                StringBuffer sb = new StringBuffer(strDate);
                sb.insert(4,"-");
                strDate = sb.toString();

                String idx = elandService.getIdxForOrderID();
                int minNum = 1000;
                int maxNum = 9999;
                int randomNum = (int)((maxNum - minNum + 1) * Math.random() + minNum);

                String strOrderId = strDate + "-" + idx + randomNum;

                // 룸타입명 세팅 - 우리쪽 룸타입명에 맞게 수정(가져온 주문정보가 우리쪽 룸타입명 형식과 일치X)
                String strRoomTypeName = strItemNm.replace(arrItemName[0], ""); // 어떤건 + 같이 들어오고 어떤건X
                // 맨 앞/자르고 공백 -> +
                String strRoomTypeNm = (strRoomTypeName.substring(1)).replace(" ", "+");
                String strTocode = elandService.tocodeForRoomTypeNm(strVendGoodsNo, strEnterIn, strRoomTypeNm);

                strRoomTypeName = strRoomTypeName.replace("/", "");
                strRoomTypeName = strRoomTypeName.replace("，", ",");
                strRoomTypeName = strRoomTypeName.replace("★","");
                strRoomTypeName = strRoomTypeName.replace("(","");
                strRoomTypeName = strRoomTypeName.replace(")","");
                strRoomTypeName = strRoomTypeName.replace(" ","");

                Date dateEnterIn = null;
                Date dateEnterOut = null;
                Calendar calEnterOut = Calendar.getInstance();
                calEnterOut.setTime(dateFormat2.parse(strEnterIn));
                String strStayDays = "";
                /**
                 * dateEnterOut을 DB에 1박이면 같은 날짜로 넣는 이유가 있는지 질문?
                 * 밑에 안내문자쪽도 같이 확인
                 *
                 */
                if(strItemNm.contains("2박") || strItemNm.contains("연박")){
                    calEnterOut.add(Calendar.DATE, +1);
                    dateEnterOut = dateFormat2.parse(dateFormat2.format(calEnterOut.getTime()));
                    strStayDays = "2박";
                }else if(strItemNm.contains("3박")){
                    calEnterOut.add(Calendar.DATE, +2);
                    dateEnterOut = dateFormat2.parse(dateFormat2.format(calEnterOut.getTime()));
                    strStayDays = "3박";
                }else if(strItemNm.contains("4박")){
                    calEnterOut.add(Calendar.DATE, +3);
                    dateEnterOut = dateFormat2.parse(dateFormat2.format(calEnterOut.getTime()));
                    strStayDays = "4박";
                }else if(strItemNm.contains("5박")){
                    calEnterOut.add(Calendar.DATE, +4);
                    dateEnterOut = dateFormat2.parse(dateFormat2.format(calEnterOut.getTime()));
                    strStayDays = "5박";
                }else{
                    dateEnterOut = dateFormat2.parse(strEnterIn);
                    strStayDays = "1박";
                }

                // 주문정보의 입실일자로 시설 정보 가져오기
                CondoDto condoDto = elandService.condoInfoForInsertOrder(strVendGoodsNo, strEnterIn, strRoomTypeName);

                String strAccommName = "";
                String strAccommFlag = "";
                String strChaincode = "";
                String strAccommAddr = "";
                int intMoneyCost = 0;
                if(condoDto != null){
                    strAccommName = condoDto.getStrAcmName();
                    strAccommFlag = condoDto.getStrFlag();
                    strChaincode = condoDto.getStrChainCode();
                    strAccommAddr = condoDto.getStrConAddrNew();
                    intMoneyCost = condoDto.getMoneyCost();

                    if(strAccommName == null || strAccommName.equals("")){
                        strAccommName = "티켓주문";
                    }else if(strAccommFlag == null || strAccommFlag.equals("")){
                        strAccommFlag = "1";
                    }else if(strChaincode == null || strChaincode.equals("")){
                        strChaincode = "";
                    }else if(strAccommAddr == null || strAccommAddr.equals("")){
                        strAccommAddr = "";
                    }
                }else{
                    System.out.println("주문 정보로 시설 정보를 가져오기 실패");
                }

                // 가격 세팅
                intMoneyCost = intMoneyCost * intIndiQty;

                int intMoneySales =  intSalePrice * intIndiQty;
                double dblMoneySettle = (intSalePrice * 0.93) * intIndiQty; //수수료 7% : supply_price 를 쓰려고 했는데 숫자 안맞는 게 간혹 있음??
                String strMemo = "주문번호 : " + strOrdNo + strDeliMemoCont;
                String strUsedPhone = strCellNo.replace("-",""); // 카톡(LMS) 발송용
                String strSendPhone = "15880134";

                // 안내 문자 내용 세팅
                String strKkoCd = ""; // 카카오인증CD
                String strMsgBody = "";
                String strSeller = "ELNAD";
                if(strAccommFlag.equals("5")){ // 티켓이면
                    strKkoCd = "CD54";

                    strMsgBody = "\n" +
                            strOrdererNm + " 고객님!\t\n" +
                            "요청하신 티켓주문건은 정상적으로 접수되었습니다\t\n\n" +
                            "※ 주문상세내용 ※\t\n" +
                            "■ 상품명: " + strAccommName + "\t\n" +
                            "■ 수량: " + strRoomTypeName + " " + intIndiQty + " 매\n\n" +
                            "★ 예약처리안내(필독)★\t\n" +
                            "-주문하신 예약건은 담당자가 처리하여 티켓번호를 알림톡/문자발송\t\n" +
                            "-당일티켓 취소시 위약금 20% 징수 (유효기간내 사용가능)\t\n\n" +
                            "고객센터 ☎ 1588-0134 1번\t\n" +
                            "근무시간: 평일 09:00~18:00\n" +
                            "비상문자서비스: 010-6536-2403 (수신전용)\n";
                }else{
                    strKkoCd = "CD011";

                    dateEnterIn = dateFormat2.parse(strEnterIn);

                    Calendar calendarEnterOut = Calendar.getInstance();
                    calendarEnterOut.setTime(dateEnterOut);
                    calendarEnterOut.add(Calendar.DATE, +1);
                    String strEnterOut = dateFormat2.format(calendarEnterOut.getTime());

                    Date dateEnterOutMsg = dateFormat2.parse(strEnterOut);

//                    strEnterIn : 2023-05-26
//                    strEnterOut : 2023-05-27
//                    dateEnterIn : Fri May 26 00:00:00 KST 2023
//                    dateEnterOut : Sat May 27 00:00:00 KST 2023

                    strMsgBody = "(" + strSeller + ") " + strOrdererNm + " 고객님!\t\n" +
                            "요청하신 숙소주문건은 정상적으로 [접수]되었습니다\t\n" +
                            "주문내용을 재확인 부탁드립니다(투숙일자,사용박수)\n\n" +
                            "※ 주문상세내용 ※\t\n" +
                            "■ 상품명: " + strAccommName + "\t\n" +
                            "■ 입실일자: " + String.format("%tm", dateEnterIn) + "/" + String.format("%td", dateEnterIn) + " ~ " +
                            String.format("%tm", dateEnterOutMsg) + "/" + String.format("%td", dateEnterOutMsg) + "\n" +
                            "■ 객실타입: " + strRoomTypeName + "\n\n" +
                            "★ 예약처리안내(필독)★\t\n" +
                            "주문하신 예약건은 담당자가 처리하여 [확정번호]를 알림톡/문자발송\n\n" +
                            "※ 실시간예약의 상품일 경우 예약번호 자동발송\n" +
                            "- 주말 연박투숙일 경우 사전확보된 객실로 수동처리 후 담당자가 개별처리(시간소요)\t\n" +
                            "(단, 임박한 투숙일자 및 사전 확보된 잔여객실이 소진되어 확정이\n" +
                            "불가능할 경우 100% 환불진행 될 수 있습니다\n\n" +
                            "※ 전화량이 많아 업무가 많이 지연되고 있습니다\t\n" +
                            "입실순서대로 순차적으로 진행중이오니 양해부탁드립니다\t\n\n" +
                            "※ 휴일당일출발(숙소예약) 당직자가 개별 처리합니다 (단,유선상담운영종료)\n" +
                            "※ 취소나 변경은 근무시간내에만 가능합니다\t\n" +
                            "- 비수기: 7일전, 성수기 10~14일전\t\n" +
                            "- 위약금은 개별숙소별로 다를 수 있습니다(사전확인)\t\n\n" +
                            "고객센터 ☎ 1588-0134 숙박팀 1번\t\n" +
                            "근무시간: 평일 09:00~17:30\n" +
                            "공휴일: 휴무\t\n\n" +
                            "[비상문자접수서비스] -> 입실문제발생\n" +
                            "HP 010-6536-2403(전화상담불가)\n" +
                            "(운영시간:근무시간외에 오후8시까지)\n\n" +
                            "★★ 취소변경 접수불가(익일 근무시간에 진행됨)\n" +
                            "- 문자를 남겨주셔도 시설예약과 업무종료로 진행불가\n" +
                            "- 정상근무시간 기준으로 위약금발생";
                }




                bookingDto.setStrOrderId(strOrderId);
                bookingDto.setStrSpace(strTocode);
                bookingDto.setDateEndDay(dateEnterOut);
                bookingDto.setDateStartDay(dateEnterIn);
                bookingDto.setStrOrdPhone(strCellNo);
                bookingDto.setStrPid(strGoodsNo);
                bookingDto.setIntRoom(intIndiQty);
                bookingDto.setStrRecvPhone(strRecvrCellNo);
                bookingDto.setStrRecvName(strRecvrNm);
                bookingDto.setStrCondoId(strVendGoodsNo);
                bookingDto.setStrSaleType("");

            }



        }catch (Exception e){
            e.printStackTrace();
            System.out.println("주문 가져오기 실패");
        }
    }
}
