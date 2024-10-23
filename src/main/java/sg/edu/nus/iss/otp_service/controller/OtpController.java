package sg.edu.nus.iss.otp_service.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sg.edu.nus.iss.otp_service.service.OtpService;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    Logger logger = LoggerFactory.getLogger(OtpController.class);

    @Autowired
    private OtpService otpService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateOtp(@RequestParam String email) {
        logger.info("{\"message\": \"Generating OTP for email: {}\"}", email);
        return ResponseEntity.ok(otpService.generateAndStoreOtp(email));
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateOtp(@RequestParam String email, @RequestParam String otp) {
        logger.info("{\"message\": \"Validating OTP for email: {}\"}", email);
        return ResponseEntity.ok(otpService.validateOtp(email, otp));
    }
}
