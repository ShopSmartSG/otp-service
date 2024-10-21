package sg.edu.nus.iss.otp_service.Repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import sg.edu.nus.iss.otp_service.Models.Otp;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
public class OtpRepositoryTest {

    @Autowired
    private OtpRepository otpRepository;

    @Test
    public void testSaveAndFindByEmail() {
        String email = "user@example.com";
        Otp otp = new Otp(email, "123456", LocalDateTime.now().plusMinutes(3));

        otpRepository.save(otp);

        Otp foundOtp = otpRepository.findByEmail(email);
        assertNotNull(foundOtp);
        assertEquals("123456", foundOtp.getCode());
    }

    @Test
    public void testDelete() {
        String email = "user@example.com";
        Otp otp = new Otp(email, "123456", LocalDateTime.now().plusMinutes(3));

        otpRepository.save(otp);
        otpRepository.delete(otp);

        Otp foundOtp = otpRepository.findByEmail(email);
        assertNull(foundOtp);
    }
}