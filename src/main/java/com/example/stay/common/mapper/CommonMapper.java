package com.example.stay.common.mapper;

import com.example.stay.openMarket.common.dto.CancelRulesDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonMapper {
    int getPeakCount(String strCheckIn, String strCheckOut);

    List<CancelRulesDto> getCancelRules(int intAID, String strFlag);

    double getOmkSales(int intRsvID);
}
