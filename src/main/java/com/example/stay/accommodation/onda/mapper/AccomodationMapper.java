package com.example.stay.accommodation.onda.mapper;

import com.example.stay.openMarket.common.dto.*;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface AccomodationMapper {

    String accommPhotoContentsReg(ContentsPhotoDto contentsPhotoDto);

    String roomTypeRegist(ToconDto toconDto);

    int getRoomAdminCnt(String intCondoId);

    String ratePlanRegist(RatePlanDto ratePlanDto);

    StockDto getIdxsByRatePlanId(String strRatePlanId);

    String goodsRegist(StockDto stockDto);

    String accommRegistTotal(Map<String, Object> totalData);

    String accommUpdate(Map<String, Object> totalData);

}