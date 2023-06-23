package com.example.stay.accommodation.onda.mapper;

import com.example.stay.openMarket.common.dto.*;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("onda.AccommMapper")
public interface AccommMapper {

    String insertAccommTotal(String strPropertyID, String strDeleteYn, String strViewYn, String strType,
                             int intDistrict1, int intDistrict2, String strSubject, String strLat, String strLon,
                             String strCheckIn, String strCheckOut, String strPhone, String strFax, String strEmail, String strZipCode,
                             String strAddr1, String strAddr2, String strDescription, String strRsvGuide, String strAcmNotice,String strImgDatas,
                             String strPenaltyDatas, String strKeyWordDatas, String strAttractionDatas, String strFacilityDatas, String strRmtypeDatas);

    String updateRmtype(String strPropertyID, String strType, String strRmtypeDatas);

    String updateGoods(String strRateplanID, String strRmtypeID, String strDateSales, int intStock, int intCost, int intSales,
                       int intExtraA, int intExtraC, int intExtraB, int intOmkStock, double intOmkSales);

    int getRoomAdminCnt(String intCondoId);

    int getAIDByStrPropertyID(String strPropertyID);

    String updateStatus(String target, String strDeleteYn, String strViewYn, String propertyId, String roomTypeId, String ratePlanId);

    Map<String, Integer> getDistrictCode(String strRegion, String strCity);

    String getStrCodeByStrName(String strCateCode, String strName);

}