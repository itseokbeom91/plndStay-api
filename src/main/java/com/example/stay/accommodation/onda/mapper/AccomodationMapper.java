package com.example.stay.accommodation.onda.mapper;

import com.example.stay.openMarket.common.dto.CancelInfoDto;
import com.example.stay.openMarket.common.dto.CondoDto;
import com.example.stay.openMarket.common.dto.ContentsPhotoDto;
import org.springframework.stereotype.Repository;

@Repository
public interface AccomodationMapper {
    CondoDto selectCondoByConId(String con_id);

    String selectCode(String con_flag);

    String accommRegist(CondoDto accommDetail);

    String accommPhotoContentsReg(ContentsPhotoDto contentsPhotoDto);

    int cancelInfoReg(CancelInfoDto cancelInfoDto);
}
