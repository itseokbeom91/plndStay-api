package com.example.stay.openMarket.common.mapper;

import com.example.stay.openMarket.common.dto.AccommDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CommonMapper {

    AccommDto getAcmInfo(int intAID, int intOmkIdx);

    List<Map<String, String>> getRoomList(int intAID, int intOmkIdx);

    List<Map<String, String>> getPhotoList(int intAID);

    List<Map<String, String>> getStockList(int intAID, int intOmkIdx, String strDate);


}
