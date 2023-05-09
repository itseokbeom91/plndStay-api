package com.example.stay.openMarket.common.controller;

import com.example.stay.openMarket.common.dto.CondoDto;
import com.example.stay.openMarket.common.service.CommonApiService;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/API/*")
public class CommonApiController {

    @Autowired
    private CommonApiService commonApiService;

    @GetMapping("/callapi")
    public ResponseEntity<JsonNode> callApi(int intNum, String strOmk) throws Exception{

        CondoDto condoDto = commonApiService.getInfo(intNum, strOmk);

        // itemId 구하기
        String strItemId = condoDto.getStrItemID();

        JsonNode jsonNode = commonApiService.callJsonApi(strItemId, strOmk, "getInfo", new JSONObject());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        return new ResponseEntity<JsonNode>(jsonNode, headers, HttpStatus.OK);
    }

    /**
     *
     * @param intNum -> con_id
     * @param strOmk -> 오픈마켓 ex)SSG, COUPANG
     * @param model
     * @return
     */
    @GetMapping("/getInfo")
    public String getInfo(int intNum, String strOmk, Model model){

        // return할 html코드담을 변수
        String result = "";
        
        // 데이터 가져오기
        CondoDto condoDto = commonApiService.getInfo(intNum, strOmk);
        System.out.println("쿼리로 숙박상품 정보 가져오기 : " + System.currentTimeMillis());


        try{
            // 상세페이지 데이터 가져오기
            result = commonApiService.getStrPdtDtlInfo(condoDto, intNum, strOmk);

            model.addAttribute("result", result);

        }catch (Exception e){
            e.printStackTrace();
        }

        return "api/ssgGet";
    }

}
