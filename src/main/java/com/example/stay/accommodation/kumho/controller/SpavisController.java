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

    // 쿠폰 사용여부 조회 - 1개씩
    @GetMapping("checkCouponStatus")
    @ResponseBody
    public String checkCouponStatus(String dataType, HttpServletRequest httpServletRequest, String couponNo){
        return spavisService.checkCouponStatus(dataType, httpServletRequest, couponNo);
    }

    // 쿠폰 사용여부 조회 - 여러개(동기)
    @GetMapping("checkCouponListStatus")
    @ResponseBody
    public String checkCouponListStatus(String dataType, HttpServletRequest httpServletRequest){
        return spavisService.checkCouponListStatus(dataType, httpServletRequest);
    }

    // 쿠폰 사용여부 조회 - 여러개(비동기)
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

        }catch (Exception e){
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        logWriter.add(message);
        logWriter.log(0);

        CommonFunction commonFunction = new CommonFunction();
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 티켓 주문
    @GetMapping("orderTicket")
    @ResponseBody
    public String orderTicket(String dataType, HttpServletRequest httpServletRequest, int intBookingIdx){
        return spavisService.orderTicket(dataType, httpServletRequest, intBookingIdx);
    }


}
