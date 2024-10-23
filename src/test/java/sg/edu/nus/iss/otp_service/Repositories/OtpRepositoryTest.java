package sg.edu.nus.iss.otp_service.Repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import sg.edu.nus.iss.otp_service.Models.Otp;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
public class OtpRepositoryTest {

    @Autowired
    private OtpRepository otpRepository;

    private Otp testOtp;

    @BeforeEach
    void setUp() {
        // Clear the repository before each test
        otpRepository.deleteAll();

        // Create a test OTP object
        testOtp = new Otp("user@example.com", "123456", LocalDateTime.now().plusMinutes(3));
        otpRepository.save(testOtp);
    }

    @Test
    void testSaveAndFindByEmail() {
        // Test that OTP can be found by email
        Otp foundOtp = otpRepository.findByEmail("user@example.com");

        assertNotNull(foundOtp);
        assertEquals("user@example.com", foundOtp.getEmail());
        assertEquals("123456", foundOtp.getCode());
    }

    @Test
    void testFindByEmailNotFound() {
        // Test that OTP is not found for an unknown email
        Otp foundOtp = otpRepository.findByEmail("unknown@example.com");

        assertNull(foundOtp);  // Should return null if no OTP is found
    }

    @Test
    void testUpdateOtp() {
        // Test that the OTP code can be updated
        Otp foundOtp = otpRepository.findByEmail("user@example.com");
        foundOtp.setCode("654321");
        otpRepository.save(foundOtp);

        Otp updatedOtp = otpRepository.findByEmail("user@example.com");
        assertEquals("654321", updatedOtp.getCode());
    }

    @Test
    void testDeleteOtp() {
        // Test that an OTP can be deleted
        Otp foundOtp = otpRepository.findByEmail("user@example.com");
        assertNotNull(foundOtp);

        otpRepository.delete(foundOtp);

        Otp deletedOtp = otpRepository.findByEmail("user@example.com");
        assertNull(deletedOtp);  // OTP should no longer exist
    }

    @Test
    void testUniqueEmailConstraint() {
        // First save should work
        otpRepository.save(testOtp);

        // Attempt to save a duplicate email, expecting a DuplicateKeyException
        Otp duplicateOtp = new Otp("user@example.com", "654321", LocalDateTime.now().plusMinutes(3));

        assertThrows(DuplicateKeyException.class, () -> {
            otpRepository.save(duplicateOtp);
        });
    }

    @Test
    void testFindById() {
        // Test finding OTP by ID
        Optional<Otp> foundOtp = otpRepository.findById(testOtp.getId());

        assertTrue(foundOtp.isPresent());
        assertEquals("user@example.com", foundOtp.get().getEmail());
    }

    @Test
    void testDeleteAll() {
        // Test that all records can be deleted
        otpRepository.deleteAll();
        assertEquals(0, otpRepository.count());
    }
}
