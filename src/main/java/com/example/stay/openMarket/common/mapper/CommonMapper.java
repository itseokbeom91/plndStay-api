package com.example.stay.openMarket.common.mapper;

import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.dto.CancelRulesDto;
import com.example.stay.openMarket.common.dto.RoomTypeDto;
import com.example.stay.openMarket.common.dto.StockDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CommonMapper {

    AccommDto getAcmInfo(int intAID, int intOmkIdx);

    List<RoomTypeDto> getRoomList(int intAID, int intOmkIdx);

    List<String> getPhotoList(int intAID, int intCnt);

    List<StockDto> getStockList(int intAID, int intOmkIdx, String strDate);

    int getMinPrice(int intAID, String strDate);

    List<CancelRulesDto> getCancelRuleList(int intAID);

    RoomTypeDto getRmtpeInfo(int intRmIdx, int intOmkIdx);

    String insertAcmOmk(int intAID, int intOmkIdx, String strUsageYn, String strPdtSubject, String strPdtCode, String strDetailInfo);

    String getStrPdtCode(int intAID, int intOmkIdx);

    double getOmkSales(int intAID, int intOmkIdx);
}
