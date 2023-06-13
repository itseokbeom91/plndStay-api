package com.example.stay.accommodation.onda.mapper;

import com.example.stay.openMarket.common.dto.*;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface AccomodationMapper {

    String insertAccommTotal(String strPropertyID, String strDeleteYn, String strViewYn, String strType,
                             String intDistrict1, String intDistrict2, String intDistrict3, String strSubject, String strLat, String strLon,
                             String strCheckIn, String strCheckOut, String strPhone, String strFax, String strEmail, String strZipCode,
                             String strAddr1, String strAddr2, String strDescription, String strRsvGuide, String strAcmNotice,String strImgDatas,
                             String strPenaltyDatas, String strKeyWordDatas, String strFacilityDatas, String strRmtypeDatas);

    String updateRmtype(String strPropertyID, String strType, String strRmtypeDatas);

    String updateGoods(int strRatePlanId, int intStock, String strCheckInDate, int intBasicPrice, int intSalePrice,
                       int intMinStay, int intMaxStay);

    int getRoomAdminCnt(String intCondoId);

    int getAIDByStrPropertyID(String strPropertyID);

    String updateStatus(String target, String strDeleteYn, String strViewYn, String propertyId, String roomTypeId, String ratePlanId);

    String getDistrictCodeByStrName(String strDistrict);
    String getDistrictCodeByStrName2(String strDistrict);

    String getStrCodeByStrName(String strCateCode, String strName);

}