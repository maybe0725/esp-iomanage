package esp.iomanage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BootstrapController {
    
    @RequestMapping(value = "/bootstrap/index")
    public String jqueryIndex() {
        return "bootstrap-index";
    }
}
