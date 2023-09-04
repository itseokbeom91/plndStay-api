package com.example.stay.accommodation.kumho.mapper;

import com.example.stay.openMarket.common.dto.RsvStayDto;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@ResponseBody
public interface SpavisMapper {

//    List<String> getPrepayList();
//
//    int updateCouponDates(String datePurchase, String dateExpired, String strCouponNo);
//
//    int updateStrNote(String strNote, String strCouponNo);

    String insertTicket(String strTicketDatas);

//    int getIntRsvIDCnt(int intRsvID);

    int getStrTicketNoCnt(String strRsvID, String strTicketNo);

    int updateTicketStatus(String strUseStatus, String dateUsed, String strTicketNo, String strRsvID);

    int cancelAllTicket(int intRsvID);

    int getMaxIdx();

    int insertKkoMsg(String receiver, String sender, String kkoMsg);

    RsvStayDto getRsvStayInfo(int intRsvID);

    int updateStrStatusCode(String strStatusCode, int intRsvID);

    List<String> getTicketList(int intRsvID);
}
