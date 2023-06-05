package com.example.stay.accommodation.resom.controller;

import com.example.stay.accommodation.resom.mapper.BookingMapper;
import com.example.stay.accommodation.resom.service.BookingService;
import com.example.stay.common.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ResourceBundle;

@Controller("resom.BookingController")
@RequestMapping("/resom/booking/*")
public class BookingController {

    /*
    TO-DO 리솜 인터페이스 목록

패키지 목록조회

패키지 상세정보조회

패키지 예약취소

패키지 예약대사

영업장목록 조회

이용자 정보 변경

패키지 예약조회

패키지 예약취소(고객정보)

패키지 연박 예약

패키지 요금조회
	영업장별
	영업장별, 객실유형별
	영업장별, 객실유형별, 이용일자별

패키지 현황조회
	영업장별
	영업장별, 객실유형별
	영업장별, 객실유형별, 이용일자별
	영업장별 월 시작과종료

     */

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
    public ResponseResult getPackageInfo(@RequestParam String pkgNo) {
        ResponseResult responseResult = bookingService.getPackageInfo(pkgNo);
        return responseResult;
    }

    @GetMapping("/getPackageStatus1")
    @ResponseBody
    public ResponseResult getPackageStatus(@RequestParam String pkgNo, @RequestParam String storeCd, @RequestParam String sDate) {
        ResponseResult responseResult = bookingService.getPackageStatus(pkgNo, storeCd, sDate);
        return responseResult;
    }

    @GetMapping("/getPackageStatus2")
    @ResponseBody
    public ResponseResult getPackageStatus(@RequestParam String pkgNo, @RequestParam String storeCd, @RequestParam String sDate, @RequestParam String rmTypeCd) {
        ResponseResult responseResult = bookingService.getPackageStatus(pkgNo, storeCd, sDate, rmTypeCd);
        return responseResult;
    }

    @GetMapping("/getPackageStatus3")
    @ResponseBody
    public ResponseResult getPackageStatusMonth(@RequestParam String pkgNo, @RequestParam String storeCd, @RequestParam String rmTypeCd, @RequestParam String sDate, @RequestParam String nights) {
        ResponseResult responseResult = bookingService.getPackageStatus(pkgNo, storeCd, rmTypeCd, sDate, nights);
        return responseResult;
    }

    @GetMapping("/getPackageAmount1")
    @ResponseBody
    public ResponseResult getPackageAmount(@RequestParam String pkgNo, @RequestParam String storeCd, @RequestParam String sDate) {
        ResponseResult responseResult = bookingService.getPackageAmount(pkgNo, storeCd, sDate);
        return responseResult;
    }

    @GetMapping("/getPackageAmount2")
    @ResponseBody
    public ResponseResult getPackageAmount(@RequestParam String pkgNo, @RequestParam String storeCd, @RequestParam String sDate, @RequestParam String rmTypeCd) {
        ResponseResult responseResult = bookingService.getPackageAmount(pkgNo, storeCd, sDate, rmTypeCd);
        return responseResult;
    }

    @GetMapping("/getPackageAmount3")
    @ResponseBody
    public ResponseResult getPackageAmount(@RequestParam String pkgNo, @RequestParam String storeCd, @RequestParam String sDate, @RequestParam String rmTypeCd, @RequestParam String nights) {
        ResponseResult responseResult = bookingService.getPackageAmount(pkgNo, storeCd, sDate, rmTypeCd, nights);
        return responseResult;
    }

    @GetMapping("/createBooking")
    @ResponseBody
    public ResponseResult createBooking(@RequestParam String pkgNo, @RequestParam String storeCd, @RequestParam String ciYmd, @RequestParam String rmTypeCd, @RequestParam String comRsvNo, @RequestParam String userName,
                                      @RequestParam String userTel, @RequestParam String payAmt, @RequestParam String adultCnt, @RequestParam String childCnt, @RequestParam String channelCd, @RequestParam String channelNm) {
        ResponseResult responseResult = bookingService.createBooking(pkgNo, storeCd, ciYmd, rmTypeCd, comRsvNo, userName, userTel, payAmt, adultCnt, childCnt, channelCd, channelNm);
        return  responseResult;
    }

    @GetMapping("/cancelBooking")
    @ResponseBody
    public ResponseResult cancelBooking(@RequestParam String roomRsvNo, @RequestParam String pkgSaleSeq, @RequestParam String roomRsvSeq, @RequestParam String comRsvNo) {
        ResponseResult responseResult = bookingService.cancelBooking(roomRsvNo, pkgSaleSeq, roomRsvSeq, comRsvNo);
        return responseResult;
    }

    @GetMapping("/updateGuest")
    @ResponseBody
    public ResponseResult updateGuest(@RequestParam String roomRsvSeq, @RequestParam String pkgSaleSeq, @RequestParam String guestNm, @RequestParam String mpNo) {
        ResponseResult responseResult = bookingService.updateGuest(roomRsvSeq, pkgSaleSeq, guestNm, mpNo);
        return responseResult;
    }

    @GetMapping("/getPackageBookingInfo")
    @ResponseBody
    public ResponseResult getPackageBookingInfo(@RequestParam String ciYmd, @RequestParam String roomRsvNo, @RequestParam String guestNm, @RequestParam String mpNo) {
        ResponseResult responseResult = bookingService.getPackageBookingInfo(ciYmd, roomRsvNo, guestNm, mpNo);
        return responseResult;
    }

    @GetMapping("/reservationList")
    @ResponseBody
    public ResponseResult reservationList(@RequestParam String stndDt) {
        ResponseResult responseResult = bookingService.reservationList(stndDt);
        return responseResult;
    }

    @GetMapping("/createBooking2")
    @ResponseBody
    public ResponseResult createBooking2(@RequestParam String pkgNo, String storeCd, String ciYmd, String rmTypeCd, String comRsvNo, String userName, String userTel, String payAmt, String adultCnt, String childCnt, String channelCd, String channelNm, String nights, String rmCnt) {
        ResponseResult responseResult = bookingService.createBooking(pkgNo, storeCd, ciYmd, rmTypeCd, comRsvNo, userName, userTel, payAmt, adultCnt, childCnt, channelCd, channelNm, nights, rmCnt);
        return responseResult;
    }

    /*
    getPackageList()
    getPackageInfo()
    getStoreLisr()
    getPackageReservationInfo()
    updateGuestName()


     */
}
