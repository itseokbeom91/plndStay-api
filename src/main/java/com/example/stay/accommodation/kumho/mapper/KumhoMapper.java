package com.example.stay.accommodation.kumho.mapper;

import com.example.stay.openMarket.common.dto.BookingDto;
import com.example.stay.openMarket.common.dto.RsvStayDto;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface KumhoMapper {

    RsvStayDto getReservation(int intBookingID);

    String updateRsvStay(int intBookingID, String strStatusCode, String strRsvRmNum, int intRmCnt);

    int updateStrStatusCode(int intRsvID, String strStatusCode);

    String updateStock(int intAID, int intRmIdx, String strStockDatas);

    Map<String, Object> getRmtypeInfo(int intRmIdx);

    int getIntStep(int intRmIdx);

    int getBreakfastYn(int intRmIdx);

}
