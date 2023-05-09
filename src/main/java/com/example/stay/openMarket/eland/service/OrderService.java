package com.example.stay.openMarket.eland.service;

import com.example.stay.openMarket.common.dto.CondoDto;
import com.example.stay.openMarket.eland.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;

    // 주문번호 만들 idx가져오기
    public String getIdxForOrderID(){
        String idx = orderMapper.getIdxForOrderID();
        return idx;
    }

    // strVendGoodsNo, strRoomTypeName, 주문정보의 입실일자로 시설 정보 가져오기
    public CondoDto condoInfoForInsertOrder(String con_id, String strEnterIn, String strRoomTypeName){
        CondoDto condoDto = orderMapper.condoInfoForInsertOrder(con_id, strEnterIn, strRoomTypeName);
        return condoDto;
    }

    // tocode 정보 다시 가져오기
    public String tocodeForRoomTypeNm(String con_id, String strEnterIn, String strRoomTypeName){
        String tocode = orderMapper.tocodeForRoomTypeNm(con_id, strEnterIn, strRoomTypeName);
        return tocode;
    }

}
