package com.example.stay.openMarket.ssg.controller;

import com.example.stay.openMarket.ssg.service.InsertService;
import com.example.stay.openMarket.ssg.service.SsgService;
import com.example.stay.openMarket.ssg.service.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public void getReserveList(String startDate, String endDate){

        ssgService.getReserveList(startDate, endDate);

    }

    // 츨고대상목록조회
    @GetMapping("/getRlsList")
    public void getReleaseList(String startDate, String endDate){

        ssgService.getReleaseList(startDate, endDate);
    }

    // 배송완료관리
    @GetMapping("/getFinishList")
    public void getFinishList(String startDate, String endDate, String orderNo){

        ssgService.getFinishList(startDate, endDate, orderNo);
    }

    // 주문별 상태 조회
    @GetMapping("getRsvDetail")
    public void getReserveDetail(String orderNo){

        ssgService.getReserveDetail(orderNo);
    }

    // 취소신청 목록조회
    @GetMapping("/getcancelList")
    public void getCancelList(String startDate, String endDate){

        ssgService.getCancelList(startDate,endDate);
    }


    /**
     *************상품 관련**************
     */

    // 상품 정보 수정
    @GetMapping("modify")
    public void modifySSG(int intAID, String strType){

        updateService.updateInfo(intAID, strType);

    }


    // 상품 정보 등록
    @GetMapping("insert")
    public void insertSSG(int intAID){

        insertService.insert(intAID);

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
