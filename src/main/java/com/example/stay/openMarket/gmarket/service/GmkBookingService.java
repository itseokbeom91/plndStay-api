package com.example.stay.openMarket.gmarket.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.openMarket.gmarket.GmkUtil.GmkApi;
import com.example.stay.openMarket.gmarket.GmkUtil.HmacGenerator;
import com.example.stay.openMarket.gmarket.mapper.GmkMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class GmkBookingService {

    @Autowired
    private GmkMapper gmkMapper;

    CommonFunction commonFunction = new CommonFunction();

    // 결제 완료된 주문 데이터 조회 - 클레임(취소, 반품, 교환, 미수령신고) 주문은 조회 X
    public String getBookingList(String dataType, String startDate, String endDate, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            JSONObject requestJson = new JSONObject();
            requestJson.put("siteType", 2); // 1: 옥션, 2 : 지마켓
            requestJson.put("orderStatus", 4); // 1 : 결제완료(주문 확인 전)
            requestJson.put("requestDateType", 5); // 조회 기준 구분 1 : 주문일
            requestJson.put("requestDateFrom", startDate);
            requestJson.put("requestDateTo", endDate);

            // api 호출
            String authorization = HmacGenerator.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "shipping/v1/Order/RequestOrders", "POST", authorization, requestJson);
            String code = resultJson.get("ResultCode").toString();
            if(code.equals("0")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yy년MM월dd일");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

                JSONObject dataJson = (JSONObject) resultJson.get("Data");
                JSONArray orderArr = (JSONArray) dataJson.get("RequestOrders");
                for(Object order : orderArr){
                    JSONObject orderJson = (JSONObject) order;

                    //System.out.println(orderJson);

//                    String payNo = orderJson.get("PayNo").toString(); // 장바구니 번호(결제번호)
                    String strOrderCode = orderJson.get("OrderNo").toString(); // 주문번호
                    String orderDate = orderJson.get("OrderDate").toString(); // 주문일자
                    String payDate = orderJson.get("PayDate").toString(); // 결제일자
//                    String goodsNo = orderJson.get("GoodsNo").toString(); // ESM 상품번호 - 추후 적용 예정, 현재는 null만
                    String strProductID = orderJson.get("SiteGoodsNo").toString(); // 사이트 상품번호
                    String intAID = orderJson.get("OutGoodsNo").toString(); // 상품 등록 시 입력한 관리코드 -> intAID / old_cid
//                    String goodsName = orderJson.get("GoodsName").toString(); // 상품명 - 시설명

//                    String salePrice = orderJson.get("SalePrice").toString(); // 해당 주문시점 판매단가
//                    int contrAmount = Integer.parseInt(orderJson.get("ContrAmount").toString()); // 주문수량
//                    String orderAmount = orderJson.get("OrderAmount").toString(); // 판매단가 * 수량
//                    String acntMoney = orderJson.get("AcntMoney").toString(); // 결제금액
//                    String directDiscountPrice = orderJson.get("DirectDiscountPrice").toString(); // 지마켓 할인 지원 금액
//                    String costPrice = orderJson.get("CostPrice").toString(); // 공급원가
//                    String settlementPrice = orderJson.get("SettlementPrice").toString(); // 정산예정금액
//                    String outsidePrice = orderJson.get("OutsidePrice").toString(); // 판매자 공제금액 - 제휴수수료 등 정산금액에서 추가 공제될 금액

//                    String serviceFee = "";
//                    if(orderJson.get("ServiceFee") != null){
//                        serviceFee = orderJson.get("ServiceFee").toString(); // 서비스 이용료 금액
//                    }
//
//                    String basicServiceFee = "";
//                    if(orderJson.get("BasicServiceFee") != null){
//                        basicServiceFee = orderJson.get("BasicServiceFee").toString(); // 기본수수료
//                    }
//
//                    String sellerCashBackMoney = "";
//                    if(orderJson.get("SellerCashBackMoney") != null){
//                        sellerCashBackMoney = orderJson.get("SellerCashBackMoney").toString(); // 판매자 지급 스마일캐시 적용금액
//                    }
//
//                    String singlePayDcAmmnt = "";
//                    if(orderJson.get("singlePayDcAmmnt") != null){
//                        singlePayDcAmmnt = orderJson.get("singlePayDcAmmnt").toString(); // 일시불 할인
//                    }

//                    String OptSelPrice = orderJson.get("OptSelPrice").toString(); // 옵션 추가금액(옵션단가 * 수량)

                    String strOrdName = orderJson.get("BuyerName").toString(); // 구매자명
//                    String buyerID = orderJson.get("BuyerID").toString(); // 구매자ID

                    String strOrdPhone = "";
                    if(orderJson.get("BuyerMobileTel") != null){
                        strOrdPhone = orderJson.get("BuyerMobileTel").toString(); // 구매자 휴대폰번호
                    }

                    String strRcvName = orderJson.get("ReceiverName").toString(); // 수령인명

                    String strRcvPhone = "";
                    if(orderJson.get("HpNo") != null){
                        strRcvPhone = orderJson.get("HpNo").toString(); // 수령인 휴대폰번호
                    }


                    String strRemark = "";
                    if(orderJson.get("DelMemo") != null){
                        strRemark = orderJson.get("DelMemo").toString(); // 배송시 요구사항
                    }

//                    String sellerFundingDiscountPrice = orderJson.get("SellerFundingDiscountPrice").toString(); // 판매자 펀딩할인

                    // strRsvCode -> 주문번호 형식 어케?
                    String strRsvCode = "GMKtest";
                    // intOrderSeq -> 순번 어케 따지는? => default 값 사용
                    // strOrderStatus -> 뭐로 해야됨? 신규주문 조회로 할거니까 무조건 신규조회로 나올텐데 값을 뭐라고 넣음?
                    String strOrderStatus = "0";
                    String strIP = "211.242.129.51";
                    String strRsvDatas = Constants.intGmkConnID + "|^|" + strRsvCode + "|^|" + intAID + "|^|"
                            + strOrdName + "|^|" + strOrdPhone  + "|^|" + strRcvName + "|^|" + strRcvPhone + "|^|" + strIP + "|^|"
                            + strRemark + "|^|" + strOrderCode + "|^|" +  strOrderStatus + "|^|" + strProductID + "|^|";

                    JSONArray optionArr = (JSONArray) orderJson.get("ItemOptionSelectList");
                    for(Object opt : optionArr){
                        JSONObject optJson = (JSONObject) opt;
                        String itemOptionValue = optJson.get("ItemOptionValue").toString(); // 주문옵션명 ex) 10월07일(일):숙박세일/로얄(디럭스단층 취사/파크뷰/침대) / 24년10월07일(일)
                        String[] optionValueArr = itemOptionValue.split(":");
                        String strCheckIn = optionValueArr[0];
                        String strRmtypeName = optionValueArr[1];
                        strCheckIn = strCheckIn.substring(0, strCheckIn.length()-3);

                        Date dateCheckIn = new Date();
                        if(strCheckIn.length() == 9){ // 년도까지 붙어있는 경우(연말에 내년 예약인 경우) ex) 24년10월07일
                            dateCheckIn = sdf.parse(strCheckIn);

                        }else{ // 당해 예약인 경우 ex) 10월07일

                            // 올해 year구해서 적용
                            String year = simpleDateFormat.format(dateCheckIn).substring(2,4);

                            strCheckIn = year + "년" + strCheckIn;
                            dateCheckIn = sdf.parse(strCheckIn);
                        }

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(dateCheckIn);

                        // 체크아웃일자 = 체크인일자+1
                        calendar.add(Calendar.DATE, 1);
                        String strCheckOut = simpleDateFormat.format(calendar.getTime());

                        strCheckIn = simpleDateFormat.format(dateCheckIn);

                        //int intRmIdx = Integer.parseInt(optJson.get("ItemOptionCode").toString()); // 주문옵션코드
                        int intRmIdx = 15302; // 일단 test data

                        int intRmCnt = Integer.parseInt(optJson.get("ItemOptionOrderCnt").toString()); // 주문옵션개수

                        strRsvDatas += strRmtypeName + "|^|" + strCheckIn + "|^|" + strCheckOut + "|^|" + intRmIdx + "|^|" + intRmCnt + "{{|}}";
                    }
                    strRsvDatas = strRsvDatas.substring(0, strRsvDatas.length()-5);

                    // procedure insert
                    System.out.println(strRsvDatas);
                    String insertResult = gmkMapper.insertBooking(strRsvDatas);
                    if(insertResult.equals("sucess")){
                        System.out.println("완");
                    }else{
                        System.out.println("실패");
                    }

                }
            }else{
                message = resultJson.get("Message").toString();
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "결제 완료된 주문 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 취소주문 목록 조회
    // 일주일 단위 조회 가능
    public String getCancelList(String dataType, String strDateFrom, String strDateTo, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            JSONObject requestJson = new JSONObject();
            requestJson.put("SiteType", 3);
            requestJson.put("CancelStatus", 0); // 0 : 전체
            requestJson.put("Type", 2); // 조회기준 구분 - 2: 취소신청일
            requestJson.put("StartDate", strDateFrom);
            requestJson.put("EndDate", strDateTo);

            // api 호출
            String authorization = HmacGenerator.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "claim/v1/sa/Cancels", "POST", authorization, requestJson);

            String code = resultJson.get("ResultCode").toString();
            if(code.equals("0")) {
                message = "취소주문 목록 조회 완료";

                JSONArray cancelArr = (JSONArray) resultJson.get("Data");

                for(Object cancel : cancelArr){
                    JSONObject cancelJson = (JSONObject) cancel;

                    String cancelStatus = cancelJson.get("CancelStatus").toString(); // 취소상태
//                    String addShippingFee = cancelJson.get("AddShippingFee").toString(); // 취소배송비
                    String payNo = cancelJson.get("PayNo").toString(); // 장바구니번호(결제번호)
                    String orderNo = cancelJson.get("OrderNo").toString(); // 주문번호
//                    String goodsNo = cancelJson.get("GoodsNo").toString(); // ESM 상품번호 - 현재는 null, 추후 적용 예정
                    String siteGoodsNo = cancelJson.get("SiteGoodsNo").toString(); // 사이트 상품번호
                    String reason = cancelJson.get("Reason").toString(); // 취소사유
                    String reasonCode = cancelJson.get("ReasonCode").toString(); // 취소요청사유코드
                    String reasonDetail = cancelJson.get("ReasonDetail").toString(); // 상세취소사유
                    String orderDate = cancelJson.get("OrderDate").toString(); // 주문일자
                    String payDate = cancelJson.get("PayDate").toString(); // 결제일자

                    String requestDate = ""; // 취소신청일자
                    if(cancelJson.get("RequestDate") != null){
                        requestDate = cancelJson.get("RequestDate").toString();
                    }

                    String withdrawDate = ""; // 취소철회일자
                    if(cancelJson.get("WithdrawDate") != null){
                        withdrawDate = cancelJson.get("WithdrawDate").toString();
                    }

                    String approveDate = ""; // 최소승인일자
                    if(cancelJson.get("ApproveDate") != null){
                        approveDate = cancelJson.get("ApproveDate").toString();
                    }

                    String completeDate = ""; // 취소완료일자
                    if(cancelJson.get("CompleteDate") != null){
                        completeDate = cancelJson.get("CompleteDate").toString();
                    }

                    // 취소요청 상태
                    if(cancelStatus.equals("1")){

                    }else if(cancelStatus.equals("4")){ // 취소 철회

                    }


                }
            }else{
                message = resultJson.get("Message").toString();
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "취소주문 목록 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 주문확인
    public String confirmBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            String strOrderNo = "";

            // api 호출
            String authorization = HmacGenerator.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "shipping/v1/Order/OrderCheck/" + strOrderNo, "POST", authorization, null);

            String code = resultJson.get("ResultCode").toString();
            if(code.equals("0")) {
                message = "주문확인 완료 -> 배송준비중으로 상태 변경됨";

            }else{
                message = resultJson.get("Message").toString();
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "주문확인 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 발송처리
    // TODO : 발송처리랑 배송완료처리를 같이 해도되는건지 확인 필요
    public String sendProcess(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

            JSONObject requestJson = new JSONObject();
            requestJson.put("orderNo", "");
            requestJson.put("ShippingDate", sdf.format(now)); // 현재 일시?
            requestJson.put("DeliveryCompanyCode", Constants.gmk_delivery_compnay_code);
            requestJson.put("InvoiceNo", sdf2.format(now)); // 날짜?

            // api 호출
            String authorization = HmacGenerator.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "shipping/v1/Delivery/ShippingInfo", "POST", authorization, requestJson);

            String code = resultJson.get("ResultCode").toString();
            if(code.equals("0")) {
                message = "발송처리 완료";

            }else{
                message = resultJson.get("Message").toString();
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "발송처리 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 배송완료 처리
    public String deliveryComplete(String dataType, int intRsvID, HttpServletRequest httpServletRequest) {
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try {
            String strOrderNo = "";

            // api 호출
            String authorization = HmacGenerator.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "shipping/v1/Delivery/AddShippingCompleteInfo/" + strOrderNo, "POST", authorization, null);

            String code = resultJson.get("ResultCode").toString();
            if(code.equals("0")) {
                message = "배송완료 처리 완료";

            }else{
                message = resultJson.get("Message").toString();
            }
            logWriter.add(message);
            logWriter.log(0);
        } catch (Exception e) {
            e.printStackTrace();
            message = "배송완료 처리 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 주문상태 조회
    public String getOrderStatus(String dataType, int intRsvID, HttpServletRequest httpServletRequest) {
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        JSONArray resultArr = new JSONArray();
        try {
            String strOrderNo = "";

            JSONObject requestJson = new JSONObject();
            requestJson.put("orderNo", strOrderNo);
            requestJson.put("Page", "1");

            // api 호출
            String authorization = HmacGenerator.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "shipping/v1/Delivery/GetDeliveryStatus", "POST", authorization, requestJson);

            String code = resultJson.get("ResultCode").toString();
            if(code.equals("0")) {
                JSONArray dataArr = (JSONArray) resultJson.get("Data");
                for(Object data : dataArr){
                    JSONObject dataJson = (JSONObject) data;

                    resultArr.add(dataJson);
                }

                message = "주문상태 조회 완료";
            }else{
                message = resultJson.get("Message").toString();
            }
            logWriter.add(message);
            logWriter.log(0);
        } catch (Exception e) {
            e.printStackTrace();
            message = "주문상태 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message, resultArr);
    }

    // 미수령신고 철회요청
    // 철회시 바로 해제되는 것은 아니며 고객에게 안내 후 이의 없을 경우 배송완료일 + 7일차 자동으로 미수령신고 해제
    // 미수령신고 철회되어야 정산됨
    public String withdrawalNotReceived(String dataType, int intRsvID, String strMessage, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        try{
            JSONObject requestJson = new JSONObject();
            requestJson.put("orderNo", "");
            requestJson.put("ClaimCancelType", 2); // 조회기준구분 - 1 : 송장번호 재입력(원송장번호 잘못되어 미수령신고가 들어온경우 송장번호 재입력으로 정상송장을 재연동) 2 : 철회요청 메시지 입력
            if(strMessage.length() > 125){
                message = "125자까지만 노출 가능합니다";
            }else{
                requestJson.put("CancelComment", strMessage); // 125자까지만 노출

                // api 호출
                String authorization = HmacGenerator.generate("sell");
                JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "shipping/v1/Delivery/ClaimRelease", "POST", authorization, requestJson);

                String code = resultJson.get("ResultCode").toString();
                if(code.equals("0")) {
                    message = "미수령신고 철회요청 완료";
                }else{
                    message = resultJson.get("Message").toString();
                }
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "미수령신고 철회요청 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 취소 요청 건에 대해 취소 승인
    // 이미 배송이 되거나 제작중이어서 취소승인할 수 없을 경우 발송처리 API를 호출하면 취소거부됨
    public String cancelBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try {
            String strOrderNo = "";

            JSONObject requestJson = new JSONObject();
            requestJson.put("SiteType", 2);

            // api 호출
            String authorization = HmacGenerator.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "claim/v1/sa/Cancel/" + strOrderNo, "PUT", authorization, requestJson);

            String code = resultJson.get("ResultCode").toString();
            if(code.equals("0")) {
                message = "취소승인 완료";
            }else{
                message = resultJson.get("Message").toString();
            }
            logWriter.add(message);
            logWriter.log(0);
        } catch (Exception e) {
            e.printStackTrace();
            message = "취소 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 판매취소
    // 재고가 부족하거나 발송을 할 수 없는 주문을 해야 할 경우 판매취소 API를 호출 -> 해당 주문의 고객 환불되고 해당 상품(또는 옵션)은 품절처리
    // 발송처리 전 주문상태 경우만 가능
    public String soldOutProcess(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try {
            String strOrderNo = "";

            JSONObject requestJson = new JSONObject();
            requestJson.put("SiteType", 2);

            // api 호출
            String authorization = HmacGenerator.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "claim/v1/sa/Cancel/" + strOrderNo + "/SoldOut", "POST", authorization, requestJson);

            String code = resultJson.get("ResultCode").toString();
            if(code.equals("0")) {
                message = "판매취소 완료";
            }else{
                message = resultJson.get("Message").toString();
            }
            logWriter.add(message);
            logWriter.log(0);
        } catch (Exception e) {
            e.printStackTrace();
            message = "판매취소 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 반품신청 목록 조회
    // 일주일 단위 조회 가능
    public String getReturnList(String dataType, String startDate, String endDate, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        JSONArray resultArr = new JSONArray();
        try{
            JSONObject requestJson = new JSONObject();
            requestJson.put("SiteType", 3);
            requestJson.put("ReturnStatus", 1); // 1 : 반품요청
            requestJson.put("Type", 2); // 조회기준 구분 2 : 반품 신청일
            requestJson.put("StartDate", startDate);
            requestJson.put("EndDate", endDate);

            // api 호출
            String authorization = HmacGenerator.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "claim/v1/sa/Returns", "POST", authorization, requestJson);

            String code = resultJson.get("ResultCode").toString();
            if(code.equals("0")) {
                JSONArray dataArr = (JSONArray) resultJson.get("Data");
                for(Object data : dataArr){
                    JSONObject dataJson = (JSONObject) data;

                    resultArr.add(dataJson);
                }
                message = "반품신청 목록 조회 완료";
            }else{
                message = resultJson.get("Message").toString();
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "반품신청 목록 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message, resultArr);
    }

    // 반품승인
    // 반품 승인 시 자동으로 반품 보류 해제 및 반품수거송장의 배송 완료가 처리되며 구매자 환불 진행
    // 바로 환불 처리 X, 고객 결제 수단 및 타 장바구니 클레임 진행 여부에 따라 실제 고객 환불 진행
    public String returnProcess(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try {
            String strOrderNo = "";

            JSONObject requestJson = new JSONObject();
            requestJson.put("SiteType", 2);

            // api 호출
            String authorization = HmacGenerator.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "claim/v1/sa/return/" + strOrderNo, "PUT", authorization, requestJson);

            String code = resultJson.get("ResultCode").toString();
            if(code.equals("0")) {
                message = "반품승인 완료";
            } else {
                message = resultJson.get("Message").toString();
            }
            logWriter.add(message);
            logWriter.log(0);
        } catch (Exception e) {
            e.printStackTrace();
            message = "반품승인 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 반품보류
    public String holdReturn(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try {
            String strOrderNo = "";

            JSONObject requestJson = new JSONObject();
            requestJson.put("OrderNo", strOrderNo);
            requestJson.put("HoldReason", 0); // 보류 사유 - 0 : 기타유보사유
            requestJson.put("SiteType", 2);

            // api 호출
            String authorization = HmacGenerator.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "claim/v1/sa/return/" + strOrderNo + "/hold", "POST", authorization, requestJson);

            String code = resultJson.get("ResultCode").toString();
            if(code.equals("0")) {
                message = "반품보류 완료";
            } else {
                message = resultJson.get("Message").toString();
            }
            logWriter.add(message);
            logWriter.log(0);
        } catch (Exception e) {
            e.printStackTrace();
            message = "반품보류 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 판매대금 정산목록 조회
    public String getCalculationList(String dataType, String startDate, String endDate, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        JSONArray resultArr = new JSONArray();
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            startDate = sdf.format(sdf.parse(startDate));
            endDate = sdf.format(sdf.parse(endDate));

            JSONObject requestJson = new JSONObject();
            requestJson.put("siteType", "G");
            // D1 : 입금확인일(정상), D2 : 배송일(정상), D3 : 배송완료일(정상), D4 : 구매결정일(정상), D5 : 정산예정일, D6 : 송금일(당일데이터는 영업일 기준 D+1일 조회가능), D7 : 환불일(환불), D8 : 입금확인일+환불일, D9 : 배송완료일 + 배송완료일 있는 환불일
            requestJson.put("SrchType", "D2");
            requestJson.put("SrchStartDate", startDate);
            requestJson.put("SrchEndDate", endDate);
//            requestJson.put("PageNo", 0);
//            requestJson.put("PageRowCnt", 0);

            // api 호출
            String authorization = HmacGenerator.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "account/v1/settle/getsettleorder", "POST", authorization, requestJson);

            String code = resultJson.get("ResultCode").toString();
            if(code.equals("0")) {
                JSONArray dataArr = (JSONArray) resultJson.get("Data");
                for(Object data : dataArr){
                    JSONObject dataJson = (JSONObject) data;

                    resultArr.add(dataJson);
                }
                message = "판매대금 정산목록 조회 완료";
            }else{
                message = resultJson.get("Message").toString();
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "판매대금 정산목록 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message, resultArr);
    }





    // =================================================================================================================

    // 입금확인중 주문조회
    // 일주일 단위 조회 가능
    public String getBookingListBeforeDeposit(String dataType, String startDate, String endDate, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            JSONObject requestJson = new JSONObject();
            requestJson.put("SiteType", 2);
            requestJson.put("requestDateFrom", startDate);
            requestJson.put("requestDateTo", endDate);

            // api 호출
            String authorization = HmacGenerator.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "shipping/v1/Order/PreRequestOrders", "POST", authorization, requestJson);

            String code = resultJson.get("ResultCode").toString();
            if(code.equals("0")) {
                message = "입금확인중 주문조회 목록 조회 완료";

            }else{
                message = "입금확인중 주문조회 목록 조회 실패";
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "입금확인중 주문조회 목록 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 배송진행정보 조회
    public String getDeliveryProcess(String dataType, int intRsvID, HttpServletRequest httpServletRequest) {
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        JSONArray resultArr = new JSONArray();
        try {
            String strOrderNo = "4043583826";

            JSONObject requestJson = new JSONObject();
            requestJson.put("orderNo", strOrderNo);

            // api 호출
            String authorization = HmacGenerator.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "shipping/v1/Delivery/Progress", "POST", authorization, requestJson);

            String code = resultJson.get("ResultCode").toString();
            if(code.equals("0")) {
                JSONArray dataArr = (JSONArray) resultJson.get("Data");
                for(Object data : dataArr){
                    JSONObject dataJson = (JSONObject) data;

                    resultArr.add(dataJson);
                }

                message = "배송진행정보 조회 완료";
            }else{
                message = resultJson.get("Message").toString();
            }
            logWriter.add(message);
            logWriter.log(0);
        } catch (Exception e) {
            e.printStackTrace();
            message = "배송진행정보 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message, resultArr);
    }

    // 미수령 신고 목록 조회
    // 미수령신고된 주문은 정산되지 않기 때문에 구매자와 연락하여 배송사고가 발생했는지 등 확인해야 함
    public String getNotReceivedList(String dataType, String startDate, String endDate, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        JSONArray resultArr = new JSONArray();
        try{
            JSONObject requestJson = new JSONObject();
            requestJson.put("searchType", 1); // 조회기준구분 0 : 주문번호 조회, 1 : 미수령신고일 조회
            requestJson.put("StartDate", startDate);
            requestJson.put("EndDate", endDate);

            // api 호출
            String authorization = HmacGenerator.generate("sell");
            JSONObject resultJson = GmkApi.callGmkApi(Constants.gmkUrl + "shipping/v1/Delivery/ClaimList", "POST", authorization, requestJson);

            String code = resultJson.get("ResultCode").toString();
            if(code.equals("0")) {
                if(resultJson.get("Data") != null){
                    JSONArray dataArr = (JSONArray) resultJson.get("Data");
                    for(Object data : dataArr){
                        JSONObject dataJson = (JSONObject) data;

                        resultArr.add(dataJson);
                    }
                    message = "미수령 신고 목록 조회 완료";
                }else{
                    message = "조회 데이터 없음";
                }
            }else{
                message = resultJson.get("Message").toString();
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "미수령 신고 목록 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message, resultArr);
    }



}
