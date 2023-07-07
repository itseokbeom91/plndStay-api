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

    // 시설 정보 가져오기
    public AccommDto getAcmInfo(int intAID, String strOmkType){
        AccommDto accommDto = commonMapper.getAcmInfo(intAID, strOmkType);

        List<Map<String,String>> strRoomList = commonMapper.getRoomList(intAID);

        List<Map<String,String>> strPhotoList = commonMapper.getPhotoList(intAID);

        System.out.println(strPhotoList);
        return accommDto;
    }


}
