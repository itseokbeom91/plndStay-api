package com.example.stay.openMarket.common.service;

import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.mapper.CommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommonService {

    @Autowired
    private CommonMapper commonMapper;

    // 시설 정보 가져오기 test 중
    public String getAcmInfo(int intAID, int intOmkIdx){

        // 시설 정보
        AccommDto accommDto = commonMapper.getAcmInfo(intAID, intOmkIdx);

        // 룸타입 리스트
        List<Map<String,String>> strRoomList = commonMapper.getRoomList(intAID, intOmkIdx);

        // 사진 리스트
        List<Map<String,String>> strPhotoList = commonMapper.getPhotoList(intAID);

        // 재고 리스트
        List<Map<String,String>> strStockList = commonMapper.getStockList(intAID, intOmkIdx, "20230701");

        for(Map<String, String> map : strStockList){
            System.out.println(map.get("strSubject"));
        }

        //System.out.println(strStockList);
        return "";
    }


}
