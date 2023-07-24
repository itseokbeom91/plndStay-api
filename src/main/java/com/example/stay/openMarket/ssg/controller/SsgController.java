package com.example.stay.openMarket.ssg.controller;

import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.dto.CondoDto;
import com.example.stay.openMarket.common.mapper.CommonApiMapper;
import com.example.stay.openMarket.common.mapper.CommonMapper;
import com.example.stay.openMarket.common.service.CommonApiService;
import com.example.stay.openMarket.common.service.CommonService;
import com.example.stay.openMarket.ssg.service.InsertService;
import com.example.stay.openMarket.ssg.service.SsgService;
import com.example.stay.openMarket.ssg.service.UpdateService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/SSG/*")
public class SsgController {

    @Autowired
    private CommonApiService commonApiService;

    @Autowired
    private CommonApiMapper commonApiMapper;

    @Autowired
    private CommonService commonService;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private InsertService insertService;

    @Autowired
    private UpdateService updateService;

    @Autowired
    private SsgService ssgService;


    // 배송지시 목록조회
    @GetMapping("/getRsvList")
    public void getReserveList(String startDate, String endDate){

        ssgService.getReserveList(startDate, endDate);

    }

    // 츨고대상목록조회
    @GetMapping("/getRlsList")
    public void getReleaseList(String startDate, String endDate){

        ssgService.getReleaseList(startDate, endDate);
    }

    // 배송완료관리
    @GetMapping("/getFinishList")
    public void getFinishList(String startDate, String endDate, String orderNo){

        ssgService.getFinishList(startDate, endDate, orderNo);
    }

    // 주문별 상태 조회
    @GetMapping("getRsvDetail")
    public void getReserveDetail(String orderNo){

        ssgService.getReserveDetail(orderNo);
    }

    // 취소신청 목록조회
    @GetMapping("/getcancelList")
    public void getCancelList(String startDate, String endDate){

        ssgService.getCancelList(startDate,endDate);
    }


    /**
     * 상품 정보 수정
     * @param intAID
     * @param strType
     * @param model
     * @return
     */
    @GetMapping("modify")
    public String modifySSG(int intAID, String strType, Model model){

        String result = "";

        try{

            result = updateService.updateInfo(intAID, strType);
            System.out.println(result);

            model.addAttribute("result", result);

        }catch (Exception e){
            e.printStackTrace();
        }

        return "api/ssgGet";
    }


    /**
     * 상품 정보 등록
     * @param intAID
     * @param model
     * @return
     */
    @GetMapping("insert")
    public String insertSSG(int intAID, Model model){

        String result = "";

        try {

            result = insertService.insert(intAID);

            JSONObject object = (JSONObject) new JSONParser().parse(result);
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(object.get("result").toString());
            System.out.println(result);
            System.out.println(jsonObject.get("resultCode"));
            if(jsonObject.get("resultCode").toString().equals("00") && jsonObject.get("resultMessage").toString().equals("SUCCESS")){
                System.out.println("success");
//                String strItemId = jsonObject.get("itemId").toString();
//                int intResult = commonApiMapper.updateOmkId(intNum, strOmk, strItemId);
//                System.out.println(intResult +" | 1:성공, 0:실패");
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
