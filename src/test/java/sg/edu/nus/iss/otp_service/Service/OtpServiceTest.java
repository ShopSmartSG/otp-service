package sg.edu.nus.iss.otp_service.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sg.edu.nus.iss.otp_service.Models.Otp;
import sg.edu.nus.iss.otp_service.Repositories.OtpRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OtpServiceTest {

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
    public void testGenerateAndStoreOtp_NewUser() {
        String email = "user@example.com";
        when(otpRepository.findByEmail(email)).thenReturn(null);

        String result = otpService.generateAndStoreOtp(email);

        assertTrue(result.contains("OTP sent to"));
        verify(otpRepository, times(1)).save(any(Otp.class));
        verify(smtpService, times(1)).sendOtp(eq(email), anyString());
    }

    @Test
    public void testGenerateAndStoreOtp_ExistingUser() {
        String email = "user@example.com";
        Otp existingOtp = new Otp(email, "123456", LocalDateTime.now().plusMinutes(3));
        when(otpRepository.findByEmail(email)).thenReturn(existingOtp);

        String result = otpService.generateAndStoreOtp(email);

        assertTrue(result.contains("OTP sent to"));
        assertEquals(0, existingOtp.getAttemptCount());
        verify(otpRepository, times(1)).save(existingOtp);
        verify(smtpService, times(1)).sendOtp(eq(email), anyString());
    }

    @Test
    public void testValidateOtp_Success() {
        String email = "user@example.com";
        String validOtp = "123456";
        Otp storedOtp = new Otp(email, validOtp, LocalDateTime.now().plusMinutes(3));
        when(otpRepository.findByEmail(email)).thenReturn(storedOtp);

        String result = otpService.validateOtp(email, validOtp);

        assertEquals("OTP validated successfully.", result);
        verify(otpRepository, times(1)).delete(storedOtp);
    }

    @Test
    public void testValidateOtp_Failure() {
        String email = "user@example.com";
        String invalidOtp = "654321";
        Otp storedOtp = new Otp(email, "123456", LocalDateTime.now().plusMinutes(3));
        when(otpRepository.findByEmail(email)).thenReturn(storedOtp);

        String result = otpService.validateOtp(email, invalidOtp);

        assertTrue(result.contains("Invalid OTP"));
        verify(otpRepository, never()).delete(storedOtp);
    }

    @Test
    public void testValidateOtp_Expired() {
        String email = "user@example.com";
        Otp expiredOtp = new Otp(email, "123456", LocalDateTime.now().minusMinutes(1));  // Expired OTP
        when(otpRepository.findByEmail(email)).thenReturn(expiredOtp);

        String result = otpService.validateOtp(email, "123456");

        assertEquals("OTP has expired.", result);
        verify(otpRepository, times(1)).delete(expiredOtp);
    }
}