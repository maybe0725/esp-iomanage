package esp.iomanage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import esp.iomanage.service.ExcelService;

@RestController
public class ExcelRestController {
    
    @Autowired
    private ExcelService excelService;
    
    @RequestMapping(value="/iomanage/excel/uiScriptParser", method = RequestMethod.POST)
    public void uiScriptParser(@RequestBody String body) {
        try {
            excelService.uiScriptParser(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
