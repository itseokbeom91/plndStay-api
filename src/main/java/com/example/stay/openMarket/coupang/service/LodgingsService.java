package com.example.stay.openMarket.coupang.service;

import com.example.stay.openMarket.common.dto.CondoDto;
import com.example.stay.openMarket.common.dto.ToconDto;
import com.example.stay.common.util.Constants;
import com.example.stay.openMarket.coupang.mapper.LodgingsMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class LodgingsService {

    @Autowired
    private LodgingsMapper lodgingsMapper;

    // con_id로 coupang_id 가져오기
    public String getCoupangIdByconId(int num){
        String coupangId = lodgingsMapper.getCoupangIdByconId(num);
        return coupangId;
    }

    // con_id로 수정할 룸타입(동기화 버튼 페이지에 노출되는 룸타입 리스트만) 불러오기
    public List<ToconDto> getCreUpdRoomByConId(int con_id){
        List<ToconDto> toconDto = lodgingsMapper.getCreUpdRoomByConId(con_id);
        return toconDto;
    }

    // pyong_idx로 룸타입정보 가져오기
    public List<ToconDto> getRoomTypeByPyongIdx(int pyong_idx){
        List<ToconDto> toconDto = lodgingsMapper.getRoomTypeByPyongIdx(pyong_idx);
        return toconDto;
    }

    // 객실 상품 생성/수정 Json데이터 생성
    public JSONObject getRoomTypeJson(int pyong_idx){
        JSONObject returnJson = new JSONObject();
        try {
            // 생성 or 수정할 상품의 pyong_idx로 룸타입별 정보 가져오기
            List<ToconDto> roomType = getRoomTypeByPyongIdx(pyong_idx);
            for(ToconDto r : roomType){
                JSONObject room = new JSONObject();

                room.put("sellerRoomId", r.getPyongIdx());
                room.put("name", r.getTocode());
                room.put("additionalInfo", r.getTocodeText());

                // images
                JSONArray roomImagesArr = new JSONArray();
                String[] pyongImgs = {"22782_1.jpg", "22782_2.jpg", "22782_3.jpg"};
//                    String[] pyongImgs = t.getPyongImgs().split("\\|");
                for(int j=0; j<pyongImgs.length; j++){
                    JSONObject roomImages = new JSONObject();
                    roomImages.put("sellerUrl", Constants.condoSellerUrl + pyongImgs[j]);
//                        roomImages.put("sellerUrl", Constants.toconSellerUrl + r.getConId() + "/rooms/" + r.getPyongIdx() + "/" + pyongImgs[j]);
                    if(j == 0){
                        roomImages.put("representative", true);
                    }else{
                        roomImages.put("representative", false);
                    }
                    roomImages.put("seq", j);

                    roomImagesArr.add(roomImages);

                }

                room.put("images", roomImagesArr);

                // occupancy
                JSONObject occupancy = new JSONObject();
                occupancy.put("standardOccupancy", r.getStandpeopleCnt());
                occupancy.put("maximumOccupancy", r.getMaxpeopleCnt());

                room.put("occupancy", occupancy);

                returnJson = room;
            }

        }catch (Exception e){
            e.printStackTrace();
            returnJson = null;
        }

        return returnJson;
    }

    // con_id로 rateId 가져오기
    public String getRateIdByConId(int con_id){
        String rateId = lodgingsMapper.getRateIdByConId(con_id);
        return rateId;
    }



}
