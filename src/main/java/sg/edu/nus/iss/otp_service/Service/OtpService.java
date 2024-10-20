package sg.edu.nus.iss.otp_service.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {
    private final JavaMailSender mailSender;
    private final SecureRandom random = new SecureRandom();
    private final int OTP_LENGTH = 6; // You can change the length of the OTP
    private String otp;
    private long otpTimestamp;

    public OtpService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public String generateOtp() {
        otp = String.format("%0" + OTP_LENGTH + "d", random.nextInt((int) Math.pow(10, OTP_LENGTH)));
        otpTimestamp = System.currentTimeMillis();
        return otp;
    }

    public void sendOtp(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP is: " + otp);
        mailSender.send(message);
    }

    public boolean validateOtp(String inputOtp) {
        if (inputOtp.equals(otp) && !isOtpExpired()) {
            return true;
        }
        return false;
    }

    private boolean isOtpExpired() {
        return TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - otpTimestamp) > 5; // OTP expires in 5 minutes
    }
}