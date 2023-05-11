package com.example.stay.accommodation.onda.mapper;

import com.example.stay.openMarket.common.dto.CondoDto;
import com.example.stay.openMarket.common.dto.ContentsPhotoDto;
import org.springframework.stereotype.Repository;

@Repository
public interface AccomodationMapper {
    CondoDto selectCondoByConId(String con_id);

    String selectCode(String con_flag);

    int getAccommNInsert(CondoDto accommDetail);

    int insertAccommPhotoContents(ContentsPhotoDto contentsPhotoDto);
}
