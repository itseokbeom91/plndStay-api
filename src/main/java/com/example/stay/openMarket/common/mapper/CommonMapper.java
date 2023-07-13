package com.example.stay.openMarket.common.mapper;

import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.dto.RoomTypeDto;
import com.example.stay.openMarket.common.dto.StockDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CommonMapper {

    AccommDto getAcmInfo(int intAID, int intOmkIdx);

    List<RoomTypeDto> getRoomList(int intAID, int intOmkIdx);

    List<String> getPhotoList(int intAID);

    List<StockDto> getStockList(int intAID, int intOmkIdx, String strDate);

    int getMinPrice(int intAID, String strDate);


}
