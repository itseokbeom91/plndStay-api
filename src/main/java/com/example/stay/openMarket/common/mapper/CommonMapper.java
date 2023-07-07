package com.example.stay.openMarket.common.mapper;

import com.example.stay.openMarket.common.dto.AccommDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CommonMapper {

    AccommDto getAcmInfo(int intAID, String strOmkType);

    List<Map<String, String>> getRoomList(int intAID);

    List<Map<String, String>> getPhotoList(int intAID);
}
