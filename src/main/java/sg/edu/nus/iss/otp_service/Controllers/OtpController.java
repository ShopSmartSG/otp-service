package sg.edu.nus.iss.otp_service.Controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import sg.edu.nus.iss.otp_service.Models.Otp;
import sg.edu.nus.iss.otp_service.Service.OtpService;

@RestController
@RequestMapping("/api/otp")
public class OtpController {
    private final OtpService otpService;

    @Autowired
    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/generate")
    public String generateOtp(@RequestParam String email) {
        return otpService.generateAndStoreOtp(email);
    }

    @PostMapping("/validate")
    public String validateOtp(@RequestParam String email, @RequestParam String inputOtp) {
        return otpService.validateOtp(email, inputOtp); // Return response directly from service
    }
}