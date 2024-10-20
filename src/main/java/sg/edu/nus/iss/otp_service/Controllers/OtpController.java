package sg.edu.nus.iss.otp_service.Controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

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
        String otp = otpService.generateOtp();
        otpService.sendOtp(email);
        return "OTP sent to " + email;
    }

    @PostMapping("/validate")
    public String validateOtp(@RequestParam String inputOtp) {
        if (otpService.validateOtp(inputOtp)) {
            return "OTP validated successfully.";
        }
        return "Invalid or expired OTP.";
    }
}