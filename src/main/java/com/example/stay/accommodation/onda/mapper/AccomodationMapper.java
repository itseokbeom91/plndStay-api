package com.example.stay.accommodation.onda.mapper;

import com.example.stay.openMarket.common.dto.*;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface AccomodationMapper {

    String insertAccommTotal(String strAccommId, String API_FLAG, String strCondoName, String strConZip,
                             String strConAddr1, String strConAddr2, String strConTel, String strConFax, String strConGekNum, String strConFlag, String strLocation, String strHomepage,
                             String strTimeIn, String strTimeOut, String strConDisplay, String strMapX, String strMapY, String strMobileWarning, String strCity, String strNation,
                             String strConDesc, String strConAround, String strConSookbak, String strTagName, String strImgData, String strPenaltyData, String strRoomNRatePlanDatas);

    String updateAccomm(String strAccommId, String API_FLAG, String strCondoName, String strConZip,
                        String strConAddr1, String strConAddr2, String strConTel, String strConFax, String strConGekNum, String strConFlag, String strLocation, String strHomepage,
                        String strTimeIn, String strTimeOut, String strConDisplay, String strMapX, String strMapY, String strMobileWarning, String strCity, String strNation,
                        String strConDesc, String strConAround, String strConSookbak, String strTagName, String strImgData, String strPenaltyData);

    String updateRoomNRatePlan(String propertyId, String API_FLAG, String strRoomNRatePlanDatas);

    String updateGoods(int strRatePlanId, int intStock, String strCheckInDate, int intBasicPrice, int intSalePrice,
                       int intMinStay, int intMaxStay);

    String accommPhotoContentsReg(ContentsPhotoDto contentsPhotoDto);

    int getRoomAdminCnt(String intCondoId);

    String getCondoIDByAccommId(String propertyId);





}