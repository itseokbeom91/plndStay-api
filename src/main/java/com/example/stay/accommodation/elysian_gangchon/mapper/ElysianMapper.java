package com.example.stay.accommodation.elysian_gangchon.mapper;

import com.example.stay.openMarket.common.dto.CancelRulesDto;
import com.example.stay.openMarket.common.dto.RsvStayDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ElysianMapper {
    int getIntAID(int intRmIdx);

    RsvStayDto getReservation(int intRsvID);

    String updateRsvStay(int intRsvID, String strStatusCode, String strRsvRmNum, String strPenaltyDatas);

    String getStrRsvRmNum(int intRsvID);

    int getTseq();
}
