package com.example.stay.accommodation.wellihilli.mapper;

import org.springframework.stereotype.Repository;

@Repository
public interface WellihilliMapper {
    String updateRmtype(String strRoomDatas);

    String getStrRmtypeID(int intRmIdx);

    String updateGoods(String strStockDatas);
}
