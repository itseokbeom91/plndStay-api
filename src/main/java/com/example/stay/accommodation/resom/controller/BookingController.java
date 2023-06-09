package com.example.stay.accommodation.resom.controller;

import com.example.stay.accommodation.resom.mapper.BookingMapper;
import com.example.stay.accommodation.resom.service.BookingService;
import com.example.stay.common.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.ResourceBundle;

@Controller("resom.BookingController")
@RequestMapping("/resom/booking/*")
public class BookingController {

    @Autowired
    BookingService bookingService = new BookingService();
    
    @GetMapping("/call")
    public void main(){
        System.out.println("정상 호출 성공");
    }

    @GetMapping("/getPackageList")
    @ResponseBody
    public ResponseResult getPackageList(){
        System.out.println("패키지 목록 조회");
        ResponseResult responseResult = bookingService.getPackageList();
        return  responseResult;
    }

    @GetMapping("/getStoreList")
    @ResponseBody
    public ResponseResult getStoreList(){
        System.out.println("영업장 목록 조회");
        ResponseResult responseResult = bookingService.getStoreList();
        return  responseResult;
    }

    @GetMapping("/getPackageInfo")
    @ResponseBody
    public ResponseResult getPackageInfo(String pkgNo) {
        ResponseResult responseResult = bookingService.getPackageInfo(pkgNo);
        return responseResult;
    }

    @GetMapping("/getPackageStatus1")
    @ResponseBody
    public ResponseResult getPackageStatus(String pkgNo, String storeCd, String sDate) {
        ResponseResult responseResult = bookingService.getPackageStatus(pkgNo, storeCd, sDate);
        return responseResult;
    }

    @GetMapping("/getPackageStatus2")
    @ResponseBody
    public ResponseResult getPackageStatus(String pkgNo, String storeCd, String sDate, String rmTypeCd) {
        ResponseResult responseResult = bookingService.getPackageStatus(pkgNo, storeCd, sDate, rmTypeCd);
        return responseResult;
    }

    @GetMapping("/getPackageStatus3")
    @ResponseBody
    public ResponseResult getPackageStatusMonth(String pkgNo, String storeCd, String rmTypeCd, String sDate, String nights) {
        ResponseResult responseResult = bookingService.getPackageStatus(pkgNo, storeCd, rmTypeCd, sDate, nights);
        return responseResult;
    }

    @GetMapping("/getPackageAmount1")
    @ResponseBody
    public ResponseResult getPackageAmount(String pkgNo, String storeCd, String sDate) {
        ResponseResult responseResult = bookingService.getPackageAmount(pkgNo, storeCd, sDate);
        return responseResult;
    }

    @GetMapping("/getPackageAmount2")
    @ResponseBody
    public ResponseResult getPackageAmount(String pkgNo, String storeCd, String sDate, String rmTypeCd) {
        ResponseResult responseResult = bookingService.getPackageAmount(pkgNo, storeCd, sDate, rmTypeCd);
        Map<String , Object> resultMap = (Map<String, Object>) responseResult.getResult();
        System.out.println(resultMap.get("resultList"));
        return responseResult;
    }

    @GetMapping("/getPackageAmount3")
    @ResponseBody
    public ResponseResult getPackageAmount(String pkgNo, String storeCd, String sDate, String rmTypeCd, String nights) {
        ResponseResult responseResult = bookingService.getPackageAmount(pkgNo, storeCd, sDate, rmTypeCd, nights);
        return responseResult;
    }
/*
//Depercated
    @GetMapping("/createBooking")
    @ResponseBody
    public ResponseResult createBooking(String pkgNo, String storeCd, String ciYmd, String rmTypeCd, String comRsvNo, String userName,
                                      String userTel, String payAmt, String adultCnt, String childCnt, String channelCd, String channelNm) {
        ResponseResult responseResult = bookingService.createBooking(pkgNo, storeCd, ciYmd, rmTypeCd, comRsvNo, userName, userTel, payAmt, adultCnt, childCnt, channelCd, channelNm);
        return  responseResult;
    }*/

    @GetMapping("/cancelBooking")
    @ResponseBody
    public ResponseResult cancelBooking(String roomRsvNo, String pkgSaleSeq, String roomRsvSeq, String comRsvNo) {
        ResponseResult responseResult = bookingService.cancelBooking(roomRsvNo, pkgSaleSeq, roomRsvSeq, comRsvNo);
        return responseResult;
    }

    @GetMapping("/updateGuest")
    @ResponseBody
    public ResponseResult updateGuest(String roomRsvSeq, String pkgSaleSeq, String guestNm, String mpNo) {
        ResponseResult responseResult = bookingService.updateGuest(roomRsvSeq, pkgSaleSeq, guestNm, mpNo);
        return responseResult;
    }

    @GetMapping("/getPackageBookingInfo")
    @ResponseBody
    public ResponseResult getPackageBookingInfo(String ciYmd, String roomRsvNo, String guestNm, String mpNo) {
        ResponseResult responseResult = bookingService.getPackageBookingInfo(ciYmd, roomRsvNo, guestNm, mpNo);
        return responseResult;
    }

    //예약 조회
    @GetMapping("/reservationList")
    @ResponseBody
    public ResponseResult reservationList(String stndDt) {
        ResponseResult responseResult = bookingService.reservationList(stndDt);
        return responseResult;
    }

    //연박 예약
    @GetMapping("/createBooking")
    @ResponseBody
    public ResponseResult createBooking2(String pkgNo, String storeCd, String ciYmd, String rmTypeCd, String comRsvNo, String userName, String userTel, String payAmt, String adultCnt, String childCnt, String channelCd, String channelNm, String nights, String rmCnt) {
        ResponseResult responseResult = bookingService.createBooking(pkgNo, storeCd, ciYmd, rmTypeCd, comRsvNo, userName, userTel, payAmt, adultCnt, childCnt, channelCd, channelNm, nights, rmCnt);
        return responseResult;
    }

}
