package com.example.stay.accommodation.hanwha.mapper;

import com.example.stay.openMarket.common.dto.RsvStayDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface HanwhaMapper {

    String packageList(String PackageData);

    String getRmID(int intAID, int intRmIdx);

    Map<String, String> getPkgLcdID(int intPkgIdx);

    String insertStock(int intAID, int intRmIdx, String strStockData);

    List<Map<String, String>> getLcdCode(int intAID);

    RsvStayDto getRsvInfo(int intRsvID);

    String updateRsv(int intRsvID, String strStatusCode, String strRsvRmNum);

    List<Integer> getIntAID();

    List<Map<String, String>> getRmMapCode(int intAID);

    List<String> getLocalCode(int intAID);
}
