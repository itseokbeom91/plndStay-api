package com.example.stay.accommodation.gpension.controller;

import com.example.stay.accommodation.gpension.service.AccommService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller("gpension.AccommController")
@RequestMapping("/gp/accomm/*")
public class AccommController {

    @Autowired
    AccommService accommService = new AccommService();

    @GetMapping("/getPensionList")
    @ResponseBody
    public String getPensionList(String dataType){
        return accommService.getPensionList(dataType);
    }

    @GetMapping("/getPensionInfo")
    @ResponseBody
    public String getPensionInfo(String dataType, String pensionId){
        return accommService.getPensionInfo(dataType, pensionId);
    }

    @GetMapping("/getPensionStatus")
    @ResponseBody
    public String getPensionStatus(String dataType, String pensionId, String sDate, String eDate){
        return accommService.getPensionStatus(dataType, pensionId, sDate, eDate);
    }

    @GetMapping("/getPensionDailyInfo")
    @ResponseBody
    public String getPensionDailyInfo(String dataType, String pensionId, String sDate, String eDate){
        return accommService.getPensionDailyInfo(dataType, pensionId, sDate, eDate);
    }

    @GetMapping("/getPensionMainList")
    @ResponseBody
    public String getPensionMainList(String dataType){
        return accommService.getPensionMainList(dataType);
    }

    @GetMapping("/getRoomInfo")
    @ResponseBody
    public String getRoomInfo(String dataType, String pensionId, String roomId){
        return accommService.getRoomInfo(dataType, pensionId, roomId);
    }

    @GetMapping("/getRoomPriceInfo")
    @ResponseBody
    public String getRoomPriceInfo(String dataType, String pensionId){
        return accommService.getRoomPriceInfo(dataType, pensionId);
    }

    @GetMapping("/getPensionModList")
    @ResponseBody
    public String getPensionModList(String dataType, String lastDate){
        return accommService.getPensionModList(dataType, lastDate);
    }

    @GetMapping("/insertGP")
    @ResponseBody
    public String insertAccomm(String dataType){
        return accommService.insertGP(dataType);
    }
    @GetMapping("/testup")
    @ResponseBody
    public String testup() throws ParseException {
        return accommService.updatePenaltyData();
    }

    @GetMapping("/testMV")
    @ResponseBody
    public ModelAndView testMV() throws ParseException {
        ModelAndView mv = new ModelAndView();
        String test = getPensionList("jsonp");
        test = test.substring(5, test.length() - 1);
        JSONParser jsonParser = new JSONParser();
        JSONObject responseJson = (JSONObject) jsonParser.parse(test);
        List<Map<String, Object>> responseList = (List<Map<String, Object>>) responseJson.get("result");
        mv.setViewName("/testMV");
        mv.addObject("test", responseList);

        return mv;
    }

}
