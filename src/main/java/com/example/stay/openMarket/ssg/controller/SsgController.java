package com.example.stay.openMarket.ssg.controller;

import com.example.stay.openMarket.common.dto.CondoDto;
import com.example.stay.openMarket.common.mapper.CommonApiMapper;
import com.example.stay.openMarket.common.service.CommonApiService;
import com.example.stay.openMarket.ssg.service.InsertService;
import com.example.stay.openMarket.ssg.service.UpdateService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/API/SSG/*")
public class SsgController {

    @Autowired
    private CommonApiService commonApiService;

    @Autowired
    private CommonApiMapper commonApiMapper;

    @Autowired
    private InsertService insertService;

    @Autowired
    private UpdateService updateService;

    @GetMapping("modify")
    public String modifySSG(int intNum, String strType, String strOmk, Model model){

        try{
            // return할 html코드담을 변수
            String result = "";

            // 데이터 가져오기
            CondoDto condoDto = commonApiService.getInfo(intNum, strOmk);
            System.out.println("쿼리로 ssg 정보 가져오기 : " + System.currentTimeMillis());

            // itemId 구하기
            String itemId = condoDto.getStrItemID();

            result = updateService.updateInfo(intNum, strType, itemId, condoDto);
            System.out.println(result);

            model.addAttribute("result", result);

        }catch (Exception e){
            e.printStackTrace();
        }

        return "api/ssgGet";
    }

    @GetMapping("insert")
    public String insertSSG(int intNum, String strOmk, Model model){

        try {

            String result = "";

            // 데이터 가져오기
            CondoDto condoDto = commonApiService.getInfo(intNum, strOmk);

            result = insertService.insert(intNum, condoDto);

            JSONObject object = (JSONObject) new JSONParser().parse(result);
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(object.get("result").toString());
            System.out.println(result);
            System.out.println(jsonObject.get("resultCode"));
            if(jsonObject.get("resultCode").toString().equals("00") && jsonObject.get("resultMessage").toString().equals("SUCCESS")){
                System.out.println("success 진행");
                String strItemId = jsonObject.get("itemId").toString();
                int intResult = commonApiMapper.updateOmkId(intNum, strOmk, strItemId);
                System.out.println(intResult +" | 1:성공, 0:실패");
            }else{
                System.out.println("fail");
            }

            model.addAttribute("result", result);

        }catch (Exception e){
            e.printStackTrace();
        }

        return "api/ssgGet";
    }
}
