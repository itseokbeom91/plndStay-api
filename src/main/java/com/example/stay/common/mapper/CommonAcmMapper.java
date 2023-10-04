package com.example.stay.common.mapper;

import com.example.stay.openMarket.common.dto.CancelRulesDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CommonAcmMapper {
    int getPeakCount(String strCheckIn, String strCheckOut);

    List<CancelRulesDto> getCancelRules(int intAID, String strFlag);

    double getOmkSales(int intRsvID);

    List<Map<String, String>> getStrPkgCodeList(int intRmIdx, String startDate, String endDate);
}
