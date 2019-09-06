package esp.iomanage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MultilingualController {

    @RequestMapping(value = "/")
    public String index() {
        return "multilingual-view";
    }
    
    @RequestMapping(value = "/iomanage/multilingual/view")
    public String view() {
        return "multilingual-view";
    }
    
}
