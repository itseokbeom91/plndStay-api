package com.example.stay.accommodation.kumho.mapper;

import com.example.stay.openMarket.common.dto.BookingDto;
import com.example.stay.openMarket.common.dto.RsvStayDto;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface KumhoMapper {

    RsvStayDto getReservation(int intRsvID);

    String updateRsvStay(int intRsvID, String strStatusCode, String strRsvRmNum);

    String updateStock(int intAID, int intRmIdx, String strStockDatas);

    Map<String, Object> getRmtypeInfo(int intRmIdx);

    int getBreakfastYn(int intRmIdx);

}
