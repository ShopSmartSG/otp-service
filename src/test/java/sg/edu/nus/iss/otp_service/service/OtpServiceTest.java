package sg.edu.nus.iss.otp_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sg.edu.nus.iss.otp_service.model.Otp;
import sg.edu.nus.iss.otp_service.repository.OtpRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OtpServiceTest {

    @Mock
    private OtpRepository otpRepository;

    @Mock
    private SmtpService smtpService;

    @InjectMocks
    private OtpService otpService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateAndStoreOtp_NewOtp() {
        String email = "test@example.com";

        when(otpRepository.findByEmail(email)).thenReturn(null);

        String result = otpService.generateAndStoreOtp(email);
        assertTrue(result.contains("OTP sent to"));

        verify(otpRepository, times(1)).save(any(Otp.class));
        verify(smtpService, times(1)).sendOtp(eq(email), anyString());
    }

    @Test
    void generateAndStoreOtp_ExistingOtp_NotBlocked() {
        String email = "test@example.com";
        Otp existingOtp = new Otp(email, "123456", LocalDateTime.now().plusMinutes(3));

        when(otpRepository.findByEmail(email)).thenReturn(existingOtp);

        String result = otpService.generateAndStoreOtp(email);
        assertTrue(result.contains("OTP sent to"));

        verify(otpRepository, times(1)).save(existingOtp);
        verify(smtpService, times(1)).sendOtp(eq(email), anyString());
    }

    @Test
    void generateAndStoreOtp_ExistingOtp_Blocked() {
        String email = "test@example.com";
        Otp blockedOtp = new Otp(email, "123456", LocalDateTime.now().plusMinutes(3));
        blockedOtp.setBlocked(true);

        when(otpRepository.findByEmail(email)).thenReturn(blockedOtp);

        String result = otpService.generateAndStoreOtp(email);
        assertEquals("You are blocked from generating OTP. Please try after 15 minutes.", result);

        verify(otpRepository, times(0)).save(blockedOtp);
        verify(smtpService, times(0)).sendOtp(anyString(), anyString());
    }

    @Test
    void validateOtp_Success() {
        String email = "test@example.com";
        String inputOtp = "123456";
        Otp storedOtp = new Otp(email, inputOtp, LocalDateTime.now().plusMinutes(3));

        when(otpRepository.findByEmail(email)).thenReturn(storedOtp);

        String result = otpService.validateOtp(email, inputOtp);
        assertEquals("OTP validated successfully.", result);

        verify(otpRepository, times(1)).delete(storedOtp);
    }

    @Test
    void validateOtp_Invalid() {
        String email = "test@example.com";
        String inputOtp = "111111";
        Otp storedOtp = new Otp(email, "123456", LocalDateTime.now().plusMinutes(3));

        when(otpRepository.findByEmail(email)).thenReturn(storedOtp);

        String result = otpService.validateOtp(email, inputOtp);
        assertTrue(result.contains("Invalid OTP"));
        assertEquals(1, storedOtp.getAttemptCount());

        verify(otpRepository, times(1)).save(storedOtp);
    }

    @Test
    void validateOtp_Expired() {
        String email = "test@example.com";
        Otp expiredOtp = new Otp(email, "123456", LocalDateTime.now().minusMinutes(1));  // Expired OTP

        when(otpRepository.findByEmail(email)).thenReturn(expiredOtp);

        String result = otpService.validateOtp(email, "123456");

        assertEquals("OTP has expired.", result);

        // Ensure delete is not called
        verify(otpRepository, times(0)).delete(expiredOtp);
    }
}
