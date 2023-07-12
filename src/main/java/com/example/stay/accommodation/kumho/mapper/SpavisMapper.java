package com.example.stay.accommodation.kumho.mapper;

import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@ResponseBody
public interface SpavisMapper {

    List<String> couponList();

    int updateCouponDates(String datePurchase, String dateExpired, String strCouponNo);

    int updateStrNote(String strNote, String strCouponNo);

    int insertTicket(String strTicketNo,int intRsvID, String strSalesDate,String strExpiredDate,int intCost,int intSales);

    int getIntRsvIDCnt(int intRsvID);

    int getStrTicketNoCnt(String strTicketNo);

    int updateTicketStatus(String strUseStatus, String dateUsed, String strTicketNo, int intRsvID);

    int cancelAllTicket(int intRsvID);
}
