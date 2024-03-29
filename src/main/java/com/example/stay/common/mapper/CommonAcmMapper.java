package com.example.stay.common.mapper;

import com.example.stay.openMarket.common.dto.CancelRulesDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CommonAcmMapper {
    int getPeakCount(String strCheckIn, String strCheckOut);

    List<CancelRulesDto> getCancelRules(int intAID, String strFlag);

    double getOmkSales(int intAID, int intRmIdx, int intOMKIdx, String strDate);

    List<Map<String, Object>> getStrPkgCodeList(int intRmIdx, String startDate, String endDate);

    String updateGoods(int intAID, int intRmIdx, String strStockDatas);

    String insertRsvStayHistory(int intRsvID, String strFlag, String strContent, String strProcedure, String strIP, int intSID);

    List<String> getStrRsvRmNum(int intRsvID);
}
