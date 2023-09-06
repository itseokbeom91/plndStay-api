package com.example.stay.accommodation.elysian_gangchon.mapper;

import com.example.stay.openMarket.common.dto.RsvStayDto;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface ElysianMapper {
    String updateGoods(int intAID, int intRmIdx, String strStockDatas);

    int getIntAID(int intRmIdx);

    Map<String, String> getPackage(int intRmIdx);

    RsvStayDto getReservation(int intRsvID);

    String updateRsvStay(int intRsvID, String strStatusCode, String strRsvRmNum);

    String getStrRsvRmNum(int intRsvID);
}
