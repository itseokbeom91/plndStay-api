package com.example.stay.accommodation.kumho.mapper;

import com.example.stay.openMarket.common.dto.BookingDto;
import com.example.stay.openMarket.common.dto.RsvStayDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface KumhoMapper {

    RsvStayDto getReservation(int intRsvID);

    String updateRsvStay(int intRsvID, String strStatusCode, String strRsvRmNum, String strPenaltyDatas);

    List<Map<String, Object>> getMappingInfo();

    List<Map<String, Object>> getMappingInfoByIntAID(int intAID);

    int getBreakfastYn(int intRmIdx);

    Map<String, Integer> getPrice(int intAID, int intRmIdx, String dateSales);

    String updateStock(int intAID, int intRmIdx, String strStockDatas);

}
