package com.example.stay.accommodation.elysian_gangchon.mapper;

import com.example.stay.openMarket.common.dto.CancelRulesDto;
import com.example.stay.openMarket.common.dto.RsvStayDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ElysianMapper {
    String updateGoods(int intAID, int intRmIdx, String strStockDatas);

    int getIntAID(int intRmIdx);

    String getStrPkgCode(int intRmIdx);

    RsvStayDto getReservation(int intRsvID);

    String updateRsvStay(int intRsvID, String strStatusCode, String strRsvRmNum);

    String getStrRsvRmNum(int intRsvID);

    int getTseq();

//    int getPeakCount(String strCheckIn, String strCheckOut);
//
//    List<CancelRulesDto> getCancelRules(int intAID, String strFlag);
//
//    double getOmkSales(int intRsvID);
}
