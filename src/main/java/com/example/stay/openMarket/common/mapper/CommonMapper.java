package com.example.stay.openMarket.common.mapper;

import com.example.stay.openMarket.common.dto.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CommonMapper {

    AccommDto getAcmInfo(int intAID, int intOmkIdx);

    List<RoomTypeDto> getRoomList(int intAID, int intOmkIdx);

    List<String> getPhotoList(int intAID, int intCnt);

    List<StockDto> getStockList(int intAID, int intOmkIdx, String strDate);

    StockDto getStockInfo(int intStockIdx, int intOmkIdx);

    int getMinPrice(int intAID, String strDate); // 수정하면 지우기
    int getMinPrice(int intAID, String strDate, int intOmkIdx);

    List<CancelRulesDto> getCancelRuleList(int intAID);

    RoomTypeDto getRmtpeInfo(int intRmIdx, int intOmkIdx);

    String insertAcmOmk(int intAID, int intOmkIdx, String strUsageYn, String strPdtSubject, String strPdtCode, String strDetailInfo, String strOmkSiteCode);

    String getStrPdtCode(int intAID, int intOmkIdx);

    double getOmkSales(int intAID, int intOmkIdx);

    RsvStayDto getBookingInfo(int intRsvID);

    List<Map<String, Object>> getBookingInfo(String intRsvID);
    Map<String, Object> getTypeCode (String intRsvID);
}
