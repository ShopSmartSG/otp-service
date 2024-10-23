package sg.edu.nus.iss.otp_service.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sg.edu.nus.iss.otp_service.service.OtpService;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/generate")
    public String generateOtp(@RequestParam String email) {
        return otpService.generateAndStoreOtp(email);
    }

    @PostMapping("/validate")
    public String validateOtp(@RequestParam String email, @RequestParam String otp) {
        return otpService.validateOtp(email, otp);
    }
}
