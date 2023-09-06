package com.example.stay.accommodation.wellihilli.mapper;

import com.example.stay.openMarket.common.dto.RsvStayDto;
import org.springframework.stereotype.Repository;

@Repository
public interface WellihilliMapper {
    String updateRmtype(String strRoomDatas);

    String getStrRmtypeID(int intRmIdx);

    String updateGoods(String strStockDatas);

    String getStrPkgCode(int intRmIdx);

    RsvStayDto getReservation(int intRsvID);

    String getStrRsvCode(int intRsvID);

    String updateRsvStay(int intRsvID, String strStatusCode, String strRsvRmNum);

}
