package sg.edu.nus.iss.otp_service.Service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import sg.edu.nus.iss.otp_service.Models.Otp;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class OtpService {
    private final JavaMailSender mailSender;
    private final Map<String, Otp> otpStore = new HashMap<>(); // Store OTPs in memory for demonstration

    @Autowired
    public OtpService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Generate OTP
    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = random.nextInt(999999);
        return String.format("%06d", otp); // 6-digit OTP
    }

    // Send OTP
    public void sendOtp(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP is: " + otp);
        mailSender.send(message);
    }

    // Generate and store OTP
    public String generateAndStoreOtp(String email) {
        Otp storedOtp = otpStore.get(email);
        if (storedOtp != null && storedOtp.isBlocked()) {
            return "You are currently blocked from generating a new OTP. Please try again later.";
        }

        String otpCode = generateOtp();
        Otp otp = new Otp(otpCode, LocalDateTime.now().plusMinutes(3)); // Set expiration time to 3 minutes
        otpStore.put(email, otp); // Store OTP associated with the email
        sendOtp(email, otpCode); // Send OTP after generating it
        return "OTP sent to " + email; // Notify that OTP has been sent
    }

    // Validate OTP
    public String validateOtp(String email, String inputOtp) {
        Otp storedOtp = otpStore.get(email);
        if (storedOtp != null) {
            if (storedOtp.isExpired()) {
                otpStore.remove(email); // Remove expired OTP
                return "OTP has expired.";
            }
            if (storedOtp.isBlocked()) {
                return "You are currently blocked from validating your OTP. Please try again later.";
            }
            if (!storedOtp.canAttempt()) {
                return "Maximum attempts exceeded. Please request a new OTP.";
            }
            if (storedOtp.getCode().equals(inputOtp)) {
                otpStore.remove(email); // Remove OTP after successful validation
                return "OTP validated successfully.";
            }
            storedOtp.incrementAttempts(); // Increment attempts if OTP is invalid
            return "Invalid OTP. Attempt " + storedOtp.attemptCount + " of " + 3;
        }
        return "Invalid or expired OTP.";
    }
}