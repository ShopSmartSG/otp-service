package sg.edu.nus.iss.otp_service.Service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.edu.nus.iss.otp_service.Models.Otp;
import sg.edu.nus.iss.otp_service.Repositories.OtpRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private final SmtpService smtpService;

    @Autowired
    public OtpService(OtpRepository otpRepository, SmtpService smtpService) {
        this.otpRepository = otpRepository;
        this.smtpService = smtpService;
    }

    // Method to generate a 6-digit OTP
    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = random.nextInt(999999);
        return String.format("%06d", otp);  // Generate a 6-digit OTP
    }

    // Generate and store OTP for a given email
    public String generateAndStoreOtp(String email) {
        // Check if an OTP already exists for the user
        Otp existingOtp = otpRepository.findByEmail(email);

        if (existingOtp != null) {
            if (existingOtp.isBlocked()) {
                return "You are blocked from generating OTP. Please try after 15 minutes.";
            }
            existingOtp.setCode(generateOtp());
            existingOtp.setExpirationTime(LocalDateTime.now().plusMinutes(3));  // Reset expiration time
            existingOtp.setAttemptCount(0);  // Reset attempts
            existingOtp.setBlocked(false);  // Unblock the user if they were blocked
            existingOtp.setBlockedUntil(null);  // Reset blockedUntil time
            otpRepository.save(existingOtp);  // Update the existing entry
            smtpService.sendOtp(email, existingOtp.getCode());  // Send OTP via email
        } else {
            // Create a new OTP entry if it doesn't exist
            Otp newOtp = new Otp(email, generateOtp(), LocalDateTime.now().plusMinutes(3));
            otpRepository.save(newOtp);
            smtpService.sendOtp(email, newOtp.getCode());  // Send OTP via email
        }

        return "OTP sent to " + email;
    }

    // Validate OTP for the user
    public String validateOtp(String email, String inputOtp) {
        Otp storedOtp = otpRepository.findByEmail(email);  // Retrieve OTP from database

        if (storedOtp == null) {
            return "No OTP found for the provided email.";
        }

        if (storedOtp.isExpired()) {
            otpRepository.delete(storedOtp);  // Delete expired OTP
            return "OTP has expired.";
        }

        if (storedOtp.isBlocked()) {
            return "You are currently blocked from validating OTP. Please try after 15 minutes";
        }

        if (storedOtp.getCode().equals(inputOtp)) {
            otpRepository.delete(storedOtp);  // OTP validated, delete entry
            return "OTP validated successfully.";
        } else {
            storedOtp.incrementAttempts();  // Increment attempts for invalid OTP
            otpRepository.save(storedOtp);  // Save updated attempt count
            return "Invalid OTP. Attempt " + storedOtp.getAttemptCount() + " of 3.";
        }
    }
}