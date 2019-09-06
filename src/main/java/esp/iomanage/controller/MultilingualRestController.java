package esp.iomanage.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import esp.iomanage.service.MultilingualService;

@RestController
public class MultilingualRestController {

    private Logger logger = LoggerFactory.getLogger(MultilingualRestController.class);
    
    @Autowired
    private MultilingualService ioManageService;
    
    @RequestMapping(value="/iomanage/multilingual")
    public String multilingual(
            @RequestParam(required = false) String revision, 
            @RequestParam(required = false) String execPackage
            ) {
        
        logger.info("==================================================");
        logger.info("◆ ■ ◆ Request Parameter Check ◆ ■ ◆");
        logger.info("--------------------------------------------------");
        logger.info("  - revision    : [" + revision    + "]");
        logger.info("  - execPackage : [" + execPackage + "]");
        logger.info("==================================================");
        
        String responseMessage = "";
        try {
            responseMessage = ioManageService.multilingual(revision, execPackage);
        } catch (Exception e) {
            e.printStackTrace();
            responseMessage = e.getMessage();
        }
        
        return responseMessage;
    }
    
}
