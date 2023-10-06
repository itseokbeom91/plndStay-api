package com.example.stay.common.controller;


import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.mapper.CommonMapper;
import com.example.stay.openMarket.eland.service.ElandService;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class Scheduler {

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private ElandService elandService;

    CommonFunction commonFunction = new CommonFunction();

    //@Scheduled(cron = "5 * * * * *")
    public void cron(){

        try {
            System.out.println("another test");

            URL url = new URL("http://localhost:8080/SSG/info?intAID=11471");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public String test(HttpServletRequest request, HttpServletResponse response){
        String result = "";

        try {

            String accessToken = elandService.getCookie(request, response);

            int intAID = 11471; // 프로시저로 뽑아내야함 list 반복문사용
            AccommDto accommDto = commonMapper.getAcmInfo(intAID, 9);

            String goodsNo = accommDto.getStrPdtCode();

            JsonNode infoJsonNode = commonFunction.callJsonApi("eland", "Bearer " + accessToken, new JSONObject(), Constants.elandPath + "/goods/searchGoodsView.action?goods_no=" + goodsNo, "POST");
            JSONArray stockArray = (JSONArray) new JSONParser().parse(infoJsonNode.get("itemList").toString());

            List<Integer> intItemNoList = new ArrayList<Integer>();
            for(Object object : stockArray){
                JSONObject jsonObject = new JSONObject((Map) object);
                intItemNoList.add(Integer.parseInt(jsonObject.get("ITEM_NO").toString()));
            }
            System.out.println(stockArray);
            System.out.println(intItemNoList);

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }
}
