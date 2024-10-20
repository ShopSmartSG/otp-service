import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;import sg.edu.nus.iss.otp_service.OtpServiceApplication;

@RestController
public  class HomeController {

    Logger logger = org.slf4j.LoggerFactory.getLogger(OtpServiceApplication.HomeController.class);

    @RequestMapping("/")
    public String home() {
        logger.info("{\"message\": \"Welcome to Shopsmart OTP Service"+"\"}");
        return "Welcome to Shopsmart Shopsmart OTP Service";
    }
}
