package com.example.stay.openMarket.ssg.controller;

import com.example.stay.openMarket.ssg.service.InsertService;
import com.example.stay.openMarket.ssg.service.SsgService;
import com.example.stay.openMarket.ssg.service.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/SSG/*")
public class SsgController {

    @Autowired
    private InsertService insertService;

    @Autowired
    private UpdateService updateService;

    @Autowired
    private SsgService ssgService;


    /**
     ********예약 관련*********
     */
    // 배송지시 목록조회
    @GetMapping("/getRsvList")
    @ResponseBody
    public void getReserveList(){

        DateFormat dateDBFormat = new SimpleDateFormat("yyyyMMdd");
        String strDate = dateDBFormat.format(new Date());
        String dataType = "jsonp";

        String result = ssgService.getReserveList(strDate, strDate, dataType);
        System.out.println(result);

    }

    @GetMapping("/getRsvListTest")
    @ResponseBody
    public void getReserveListTest(String startDate, String endDate, String dataType){

        ssgService.getReserveList(startDate, endDate, dataType);

    }

    // 츨고대상목록조회
    @GetMapping("/getRlsList")
    @ResponseBody
    public void getReleaseList(String startDate, String endDate){

        ssgService.getReleaseList(startDate, endDate);
    }

    // 주문 완료처리
    @GetMapping("/approveBooking")
    @ResponseBody
    public String apporveBooking(String dataType, int intRsvID){

        return ssgService.apporveBooking(dataType, intRsvID);
    }

    // 취소처리
    @GetMapping("/cancelBooking")
    @ResponseBody
    public String cancelBooking(String dataType, int intRsvID){

        return ssgService.cancelBooking(dataType, intRsvID);
    }

    // 배송완료관리
    @GetMapping("/getFinishList")
    @ResponseBody
    public void getFinishList(String startDate, String endDate, String orderNo){

        ssgService.getFinishList(startDate, endDate, orderNo);
    }

    // 주문별 상태 조회
    @GetMapping("getRsvDetail")
    @ResponseBody
    public void getReserveDetail(String orderNo){

        ssgService.getReserveDetail(orderNo);
    }

    // 취소신청 목록조회
    @GetMapping("/getCancelList")
    @ResponseBody
    public void getCancelList(){
        DateFormat dateDBFormat = new SimpleDateFormat("yyyyMMdd");
        String strDate = dateDBFormat.format(new Date());
        String dataType = "jsonp";
        String result = ssgService.getCancelList(strDate,strDate,dataType);
        System.out.println(result);
    }


    /**
     *************상품 관련**************
     */

    // 상품 정보 조회
    @GetMapping("info")
    @ResponseBody
    public String infoSSG(int intAID){

        return ssgService.getAccommInfo(intAID);

    }

    // 상품 정보 수정
    @GetMapping("updateInfo")
    @ResponseBody
    public String modifySSG(String dataType, int intAID, String strType, @Nullable String strCode, @Nullable String strStockIdx){

        return updateService.updateInfo(dataType, intAID, strType, strCode, strStockIdx);

    }


    // 상품 정보 등록
    @GetMapping("insert")
    @ResponseBody
    public String insertSSG(int intAID){

        return insertService.insert(intAID);

    }


    /**
     *******기타******
     */

    // 브랜드ID 검색
    @GetMapping("/getBrandId")
    public void getBrandId(int intAID){

        ssgService.getBrandId(intAID);

    }

    // qna list 조회
    @GetMapping("qnaList")
    public void getQnaList(String startDate, String endDate){

        ssgService.getQnaList(startDate, endDate);

    }

    // qna 답변
    @GetMapping("answer")
    public void answerQna(String postId, String answer){

        ssgService.answerQna(postId, answer);

    }

    // 정산 조회
    @GetMapping("/salesList")
    public void getSalesList(String strDate){

        ssgService.getSaleList(strDate);

    }
}
