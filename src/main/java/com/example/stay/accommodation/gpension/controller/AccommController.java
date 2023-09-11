package com.example.stay.accommodation.gpension.controller;

import com.example.stay.accommodation.gpension.service.AccommService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Controller("gpension.AccommController")
@RequestMapping("/gp/accomm/*")
public class AccommController {




    @Autowired
    AccommService accommService = new AccommService();

    @GetMapping(value = "/getPensionList", params = "dataType=view")
    public String getPensionListFrame(Model model, @RequestParam(required = false, defaultValue="jsonp") String dataType) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject responseJson = null;
        responseJson = (JSONObject) jsonParser.parse(accommService.getPensionList(dataType));
        List<Map<String, Object>> responseList = (List<Map<String, Object>>) responseJson.get("result");
        model.addAttribute("test", responseList);
        return "/testMV";
    }

    @GetMapping(value = "/getPensionList")
    @ResponseBody
    public String getPensionList(Model model, @RequestParam(required = false, defaultValue="jsonp") String dataType){
        return accommService.getPensionList(dataType);
    }

    @GetMapping("/getPensionInfo")
    @ResponseBody
    public String getPensionInfo(@RequestParam(required = false, defaultValue="jsonp") String dataType, String pensionId){
        return accommService.getPensionInfo(dataType, pensionId);
    }

    @GetMapping("/getPensionStatus")
    @ResponseBody
    public String getPensionStatus(@RequestParam(required = false, defaultValue="jsonp") String dataType, String pensionId, String sDate, String eDate){
        return accommService.getPensionStatus(dataType, pensionId, sDate, eDate);
    }

    @GetMapping("/getPensionDailyInfo")
    @ResponseBody
    public String getPensionDailyInfo(@RequestParam(required = false, defaultValue="jsonp") String dataType, String pensionId, String sDate, String eDate){
        return accommService.getPensionDailyInfo(dataType, pensionId, sDate, eDate);
    }

    @GetMapping("/getPensionMainList")
    @ResponseBody
    public String getPensionMainList(@RequestParam(required = false, defaultValue="jsonp") String dataType){
        return accommService.getPensionMainList(dataType);
    }

    @GetMapping("/getRoomInfo")
    @ResponseBody
    public String getRoomInfo(@RequestParam(required = false, defaultValue="jsonp") String dataType, String pensionId, String roomId){
        return accommService.getRoomInfo(dataType, pensionId, roomId);
    }

    @GetMapping("/getRoomPrice")
    @ResponseBody
    public String getRoomPriceInfo(@RequestParam(required = false, defaultValue="jsonp") String dataType, String pensionId){
        return accommService.getRoomPriceInfo(dataType, pensionId);
    }

    @GetMapping("/getPensionModList")
    @ResponseBody
    public String getPensionModList(@RequestParam(required = false, defaultValue="jsonp") String dataType, String endDate){
        return accommService.getPensionModList(dataType, endDate);
    }

    @GetMapping("/insertGP")
    @ResponseBody
    public String insertAccomm(@RequestParam(required = false, defaultValue="jsonp") String dataType){
        return accommService.insertGP(dataType);
    }
    @GetMapping("/testup")
    @ResponseBody
    public String testup() throws ParseException {
        return accommService.updatePenaltyData();
    }

    @GetMapping("/testMV")
    public ModelAndView testMV() throws ParseException {
        ModelAndView mv = new ModelAndView();
        String test = (String) accommService.getPensionList("jsonp");;
        test = test.substring(5, test.length() - 1);
        JSONParser jsonParser = new JSONParser();
        JSONObject responseJson = (JSONObject) jsonParser.parse(test);
        List<Map<String, Object>> responseList = (List<Map<String, Object>>) responseJson.get("result");
        mv.setViewName("/testMV");
        mv.addObject("test", responseList);

        return mv;
    }

}
