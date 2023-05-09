package com.example.stay.openMarket.coupang.mapper;

import com.example.stay.openMarket.common.dto.ToconDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LodgingsMapper {

    // con_id로 coupang_id 가져오기
    String getCoupangIdByconId(int num);

    // con_id로 rateId가져오기
    List<ToconDto> getCreUpdRoomByConId(int con_id);
    
    // pyong_idx로 룸타입정보 가져오기
    List<ToconDto> getRoomTypeByPyongIdx(int pyong_idx);

    // con_id로 rateId 가져오기
    String getRateIdByConId(int con_id);
}
