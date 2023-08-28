package com.example.stay.accommodation.kumho.mapper;

import com.example.stay.openMarket.common.dto.BookingDto;
import com.example.stay.openMarket.common.dto.RsvStayDto;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface KumhoMapper {

    RsvStayDto getReservation(int intBookingID);

    String updateRsvStay(int intBookingID, String strStatusCode, String strRsvRmNum, int intRmCnt);

    int updateBookingStatus(int intBookingID, String strBookingProcess);

    String updateGoods(int intAID, int intRmIdx, String strStockDatas);

    Map<String, Object> getRmtypeIDNIntAID(int intRmIdx);

    String getStrLocalCode(int intAID, String strRmtypeID);

    int getIntStep(int intRmIdx);

    int getBreakfastYn(int intRmIdx);

}
