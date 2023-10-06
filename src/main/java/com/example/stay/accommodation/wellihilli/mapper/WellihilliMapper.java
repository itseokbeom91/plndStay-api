package com.example.stay.accommodation.wellihilli.mapper;

import com.example.stay.openMarket.common.dto.RsvStayDto;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface WellihilliMapper {
    String updateRmtype(String strRoomDatas);

    Map<String, Object> getStrRmtypeNAID(int intRmIdx);

    RsvStayDto getReservation(int intRsvID);

    String getStrRsvCode(int intRsvID);

    String updateRsvStay(int intRsvID, String strStatusCode, String strRsvRmNum, String strPenaltyDatas);

}
