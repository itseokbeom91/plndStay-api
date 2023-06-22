package com.example.stay.accommodation.resom.controller;

import com.example.stay.accommodation.resom.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
    public String getPackageList(){
        return  bookingService.getPackageList();
    }

    @GetMapping("/getStoreList")
    @ResponseBody
    public String getStoreList(){
        return  bookingService.getStoreList();
    }

    @GetMapping("/getPackageInfo")
    @ResponseBody
    public String getPackageInfo(String pkgNo) {
        return bookingService.getPackageInfo(pkgNo);
    }

    @GetMapping("/getPackageStatus1")
    @ResponseBody
    public String getPackageStatus(String pkgNo, String storeCd, String sDate) {
        return bookingService.getPackageStatus(pkgNo, storeCd, sDate);
    }

    @GetMapping("/getPackageStatus2")
    @ResponseBody
    public String getPackageStatus(String pkgNo, String storeCd, String sDate, String rmTypeCd) {
        return bookingService.getPackageStatus(pkgNo, storeCd, sDate, rmTypeCd);
    }

    @GetMapping("/getPackageStatus3")
    @ResponseBody
    public String getPackageStatusMonth(String pkgNo, String storeCd, String rmTypeCd, String sDate, String nights) {
        return bookingService.getPackageStatus(pkgNo, storeCd, rmTypeCd, sDate, nights);
    }

    @GetMapping("/getPackageAmount1")
    @ResponseBody
    public String getPackageAmount(String pkgNo, String storeCd, String sDate) {
        return bookingService.getPackageAmount(pkgNo, storeCd, sDate);
    }

    @GetMapping("/getPackageAmount2")
    @ResponseBody
    public String getPackageAmount(String pkgNo, String storeCd, String sDate, String rmTypeCd) {
        return bookingService.getPackageAmount(pkgNo, storeCd, sDate, rmTypeCd);
    }

    @GetMapping("/getPackageAmount3")
    @ResponseBody
    public String getPackageAmount(String pkgNo, String storeCd, String sDate, String rmTypeCd, String nights) {
        return bookingService.getPackageAmount(pkgNo, storeCd, sDate, rmTypeCd, nights);
    }

    @GetMapping("/cancelBooking")
    @ResponseBody
    public String cancelBooking(String roomRsvNo, String pkgSaleSeq, String roomRsvSeq, String comRsvNo) {
        return bookingService.cancelBooking(roomRsvNo, pkgSaleSeq, roomRsvSeq, comRsvNo);
    }

    @GetMapping("/updateGuest")
    @ResponseBody
    public String updateGuest(String roomRsvSeq, String pkgSaleSeq, String guestNm, String mpNo) {
        return bookingService.updateGuest(roomRsvSeq, pkgSaleSeq, guestNm, mpNo);
    }

    @GetMapping("/getPackageBookingInfo")
    @ResponseBody
    public String getPackageBookingInfo(String ciYmd, String roomRsvNo, String guestNm, String mpNo) {
        return bookingService.getPackageBookingInfo(ciYmd, roomRsvNo, guestNm, mpNo);
    }

    //예약 조회
    @GetMapping("/reservationList")
    @ResponseBody
    public String reservationList(String stndDt) {
        return bookingService.reservationList(stndDt);
    }

    //연박 예약
    @GetMapping("/createBooking")
    @ResponseBody
    public String createBooking2(String pkgNo, String storeCd, String ciYmd, String rmTypeCd, String comRsvNo, String userName, String userTel, String payAmt, String adultCnt, String childCnt, String channelCd, String channelNm, String nights, String rmCnt) {
        return bookingService.createBooking(pkgNo, storeCd, ciYmd, rmTypeCd, comRsvNo, userName, userTel, payAmt, adultCnt, childCnt, channelCd, channelNm, nights, rmCnt);
    }

    @GetMapping("/insertRESOM")
    @ResponseBody
    public String insertSONO(HttpServletRequest httpServletRequest) {
        return bookingService.insertRESOM(httpServletRequest);
    }

    @GetMapping("/stockResult")
    @ResponseBody
    public String getStockAndInsert(HttpServletRequest httpServletRequest) {
        return bookingService.getStockAndInsert(httpServletRequest);
    }

}
