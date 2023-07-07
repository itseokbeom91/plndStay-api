package com.example.stay.accommodation.kumho.controller;

import com.example.stay.accommodation.kumho.mapper.BookingMapper;
import com.example.stay.accommodation.kumho.mapper.SpavisMapper;
import com.example.stay.accommodation.kumho.service.SpavisService;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.LogWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/kumho/spavis/*")
public class SpavisController {

    @Autowired
    private SpavisService spavisService;

    @Autowired
    private SpavisMapper spavisMapper;

    // 선납권 사용여부 조회 - 1개씩
    @GetMapping("checkCouponStatus")
    @ResponseBody
    public String checkCouponStatus(String dataType, HttpServletRequest httpServletRequest, String couponNo){
        return spavisService.checkCouponStatus(dataType, httpServletRequest, couponNo);
    }

//    // 선납권 사용여부 조회 - 여러개(동기)
//    @GetMapping("checkCouponListStatus")
//    @ResponseBody
//    public String checkCouponListStatus(String dataType, HttpServletRequest httpServletRequest){
//        return spavisService.checkCouponListStatus(dataType, httpServletRequest);
//    }

    // 선납권 사용여부 조회 - 여러개(비동기)
    @GetMapping("checkCouponListStatus2")
    @ResponseBody
    public String checkCouponListStatus2(String dataType, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            List<String> couponList = spavisMapper.couponList();

            int failCount = 0;
            for(int i=0; i< couponList.size(); i++){
                int result = spavisService.checkCouponListStatus2(httpServletRequest, couponList.get(i));
                if(result<0){
                    failCount +=1;
                }
            }

            if(failCount == 0){
                message = "쿠폰 사용여부 조회 완료";
            }else{
                message = failCount + "건 조회 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        CommonFunction commonFunction = new CommonFunction();
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 티켓 발권
    @GetMapping("orderTicket")
    @ResponseBody
    public String orderTicket(String dataType, HttpServletRequest httpServletRequest, int intBookingIdx){
        return spavisService.orderTicket(dataType, httpServletRequest, intBookingIdx);
    }

    // 티켓 취소
    @GetMapping("cancelTicket")
    @ResponseBody
    public String cancelTicket(String dataType, HttpServletRequest httpServletRequest, int intBookingIdx){
        return spavisService.cancelTicket(dataType, httpServletRequest, intBookingIdx);
    }

    // 티켓 사용여부 조회(건별)
    @GetMapping("checkTicketStatus")
    @ResponseBody
    public String checkTicketStatus(String dataType, HttpServletRequest httpServletRequest, int intBookingIdx){
        return spavisService.checkTicketStatus(dataType, httpServletRequest, intBookingIdx);
    }

    // 티켓 사용여부 조회(일별)
    @GetMapping("checkTicketStatusByDate")
    @ResponseBody
    public String checkTicketStatusByDate(String dataType, HttpServletRequest httpServletRequest, String searchDate){
        return spavisService.checkTicketStatusByDate(dataType, httpServletRequest, searchDate);
    }

    @GetMapping("spavisUseResult")
    @ResponseBody
    public String updateStatus(HttpServletRequest httpServletRequest, String strOrderID, String ticketNo, String tkCustomerID){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        if(strOrderID.equals("") || ticketNo.equals("") || tkCustomerID.equals("")){
            String statusCode = "500";
            String message = "필수값이 입력되지 않았습니다";

            logWriter.add(message);
            logWriter.log(0);

            CommonFunction commonFunction = new CommonFunction();
            return commonFunction.makeReturn("json", statusCode, message);
        }else{
            return spavisService.updateStatus(httpServletRequest, strOrderID, ticketNo, tkCustomerID);
        }
    }

}
