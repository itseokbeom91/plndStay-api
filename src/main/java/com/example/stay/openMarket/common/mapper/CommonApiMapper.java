package com.example.stay.openMarket.common.mapper;


import com.example.stay.openMarket.common.dto.CancelInfoDto;
import com.example.stay.openMarket.common.dto.CondoDto;
import com.example.stay.openMarket.common.dto.StockDto;
import com.example.stay.openMarket.common.dto.ToconDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonApiMapper {

    // con_id로 정보 가져오기
    CondoDto getInfo(int num, String omk);

    // con_id로 roomType 정보 가져오기
    List<ToconDto> getroomType(int num, String omk);

    // con_id로 상품 이미지 가져오기
    // (추후에 다른 이미지 필요시 사용할 수 있게 해당 상품의 모든 이미지 추출)
    List<String> getPhotoList(int num);

    // con_id로 해당날짜 재고 가져오기
    List<StockDto> getStockList(int num, String omk, String date);

    // con_id로 최저가격 가져오기(상품등록시 사용)
    int getMinPrice(int num, String date);

    // 오픈마켓에 새로 insert 할 때 OMK_PRODUCT table에 오픈마켓 아이디 update
    int updateOmkId(int num, String omk, String id);

    // cid로 취소규정 가져오기
    List<CancelInfoDto> getCancelInfo(int num);


}
