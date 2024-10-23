package sg.edu.nus.iss.otp_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import sg.edu.nus.iss.otp_service.service.OtpService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OtpControllerTest {

    @Mock
    private OtpService otpService;

    @InjectMocks
    private OtpController otpController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateOtp() {
        String email = "test@example.com";
        when(otpService.generateAndStoreOtp(email)).thenReturn("OTP sent to " + email);

        ResponseEntity<String> response = otpController.generateOtp(email);
        assertEquals("OTP sent to " + email, response.getBody());

        verify(otpService, times(1)).generateAndStoreOtp(email);
    }

    @Test
    void validateOtp() {
        String email = "test@example.com";
        String otp = "123456";
        when(otpService.validateOtp(email, otp)).thenReturn("OTP validated successfully.");

        ResponseEntity<String> response = otpController.validateOtp(email, otp);
        assertEquals("OTP validated successfully.", response.getBody());

        verify(otpService, times(1)).validateOtp(email, otp);
    }
}
