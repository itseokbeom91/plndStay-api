package com.example.stay.openMarket.gmarket.controller;

import com.example.stay.openMarket.gmarket.service.GmkBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/gmk/booking/*")
public class GmkBookingController {

    @Autowired
    private GmkBookingService gmkBookingService;

    // 결제 완료된 주문 데이터 조회 - 클레임(취소, 반품, 교환, 미수령신고) 주문은 조회 X
    // 31일 이내 기간만 조회 가능
    @GetMapping("/getBookingList")
    @ResponseBody
    public String getBookingList(String dataType, String startDate, String endDate, HttpServletRequest httpServletRequest){
        return gmkBookingService.getBookingList(dataType, startDate, endDate, httpServletRequest);
    }
    // 스케줄러용
    @GetMapping("/getRsvList")
    @ResponseBody
    public String getRsvList(HttpServletRequest httpServletRequest){
        DateFormat dateDBFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateDBFormat.format(new Date());
        String dataType = "jsonp";
        return gmkBookingService.getBookingList(dataType, strDate, strDate, httpServletRequest);
    }

    // 취소주문 목록 조회
    // 일주일 단위 조회 가능
    @GetMapping("/getCancelList")
    @ResponseBody
    public String getCancelList(String dataType, String startDate, String endDate, HttpServletRequest httpServletRequest){
        return gmkBookingService.getCancelList(dataType, startDate, endDate, httpServletRequest);
    }

    // 주문확인 -> 배송준비중으로 상태 변경
    @GetMapping("/confirmBooking")
    @ResponseBody
    public String confirmBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return gmkBookingService.confirmBooking(dataType, intRsvID, httpServletRequest);
    }

    // 발송처리
    @GetMapping("/sendProcess")
    @ResponseBody
    public String sendProcess(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return gmkBookingService.sendProcess(dataType, intRsvID, httpServletRequest);
    }

    // 배송완료처리
    @GetMapping("/deliveryComplete")
    @ResponseBody
    public String deliveryComplete(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return gmkBookingService.deliveryComplete(dataType, intRsvID, httpServletRequest);
    }

    // 주문상태 조회
    @GetMapping("/getOrderStatus")
    @ResponseBody
    public String getOrderStatus(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return gmkBookingService.getOrderStatus(dataType, intRsvID, httpServletRequest);
    }

    // 미수령신고 철회요청
    // 철회시 바로 해제되는 것은 아니며 고객에게 안내 후 이의 없을 경우 배송완료일 + 7일차 자동으로 미수령신고 해제
    // 미수령신고 철회되어야 정산됨
    @GetMapping("/withdrawalNotReceived")
    @ResponseBody
    public String withdrawalNotReceived(String dataType, int intRsvID, String strMessage, HttpServletRequest httpServletRequest){
        return gmkBookingService.withdrawalNotReceived(dataType, intRsvID, strMessage, httpServletRequest);
    }

    // 취소승인
    @GetMapping("/cancelBooking")
    @ResponseBody
    public String cancelBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return gmkBookingService.cancelBooking(dataType, intRsvID, httpServletRequest);
    }

    // 판매취소
    @GetMapping("/soldOutProcess")
    @ResponseBody
    public String soldOutProcess(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return gmkBookingService.soldOutProcess(dataType, intRsvID, httpServletRequest);
    }

    // 반품신청 목록 조회
    // 일주일 단위 조회 가능
    @GetMapping("/getReturnList")
    @ResponseBody
    public String getReturnList(String dataType, String startDate, String endDate, HttpServletRequest httpServletRequest){
        return gmkBookingService.getReturnList(dataType, startDate, endDate, httpServletRequest);
    }

    // 반품승인
    // 반품 승인 시 자동으로 반품 보류 해제 및 반품수거송장의 배송 완료가 처리되며 구매자 환불 진행
    // 바로 환불 처리 X, 고객 결제 수단 및 타 장바구니 클레임 진행 여부에 따라 실제 고객 환불 진행
    @GetMapping("/returnProcess")
    @ResponseBody
    public String returnProcess(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return gmkBookingService.returnProcess(dataType, intRsvID, httpServletRequest);
    }

    // 반품보류
    @GetMapping("/holdReturn")
    @ResponseBody
    public String holdReturn(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return gmkBookingService.holdReturn(dataType, intRsvID, httpServletRequest);
    }

    // 일주일 단위 조회 가능
    @GetMapping("/getCalculationList")
    @ResponseBody
    public String getCalculationList(String dataType, String startDate, String endDate, HttpServletRequest httpServletRequest){
        return gmkBookingService.getCalculationList(dataType, startDate, endDate, httpServletRequest);
    }








//    // 입금확인중 주문조회
//    @GetMapping("/getBookingListBeforeDeposit")
//    @ResponseBody
//    public String getBookingListBeforeDeposit(String dataType, String startDate, String endDate, HttpServletRequest httpServletRequest){
//        return gmkBookingService.getBookingListBeforeDeposit(dataType, startDate, endDate, httpServletRequest);
//    }

    // 배송진행정보 조회
    @GetMapping("/getDeliveryProcess")
    @ResponseBody
    public String getDeliveryPrcess(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return gmkBookingService.getDeliveryProcess(dataType, intRsvID, httpServletRequest);
    }

    // 미수령 신고 목록 조회
    @GetMapping("/getNotReceivedList")
    @ResponseBody
    public String getNotReceivedList(String dataType, String startDate, String endDate, HttpServletRequest httpServletRequest){
        return gmkBookingService.getNotReceivedList(dataType, startDate, endDate, httpServletRequest);
    }
}
