package com.example.stay.accommodation.kumho.mapper;

import com.example.stay.openMarket.common.dto.RsvStayDto;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@ResponseBody
public interface SpavisMapper {

    List<String> couponList();

    int updateCouponDates(String datePurchase, String dateExpired, String strCouponNo);

    int updateStrNote(String strNote, String strCouponNo);

    String insertTicket(String strTicketDatas);

//    int getIntRsvIDCnt(int intRsvID);

    int getStrTicketNoCnt(String strTicketNo);

    int updateTicketStatus(String strUseStatus, String dateUsed, String strTicketNo, int intRsvID);

    int cancelAllTicket(int intRsvID);

    int getMaxIdx();

    int insertKkoMsg(String receiver, String sender, String kkoMsg);

    RsvStayDto getRsvStayInfo(int intRsvID);
}
