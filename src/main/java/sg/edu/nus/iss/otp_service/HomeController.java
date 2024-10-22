package sg.edu.nus.iss.otp_service;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public  class HomeController {

    Logger logger = org.slf4j.LoggerFactory.getLogger(HomeController.class);

    @RequestMapping("/")
    public String home() {
        logger.info("{\"message\": \"Welcome to Shopsmart OTP Service"+"\"}");
        return "Welcome to Shopsmart Shopsmart OTP Service";
    }
}
