package sg.edu.nus.iss.otp_service.Models;


import java.time.LocalDateTime;

public class Otp {
    private String code;
    private LocalDateTime expirationTime;
    public int attemptCount;
    private boolean blocked; // Track if the user is blocked
    private LocalDateTime blockedUntil; // Time until which the user is blocked

    private static final int MAX_ATTEMPTS = 3; // Maximum allowed attempts

    public Otp(String code, LocalDateTime expirationTime) {
        this.code = code;
        this.expirationTime = expirationTime;
        this.attemptCount = 0;
        this.blocked = false;
        this.blockedUntil = null;
    }

    public String getCode() {
        return code;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationTime);
    }

    public boolean canAttempt() {
        return attemptCount < MAX_ATTEMPTS;
    }

    public void incrementAttempts() {
        attemptCount++;
        if (attemptCount >= MAX_ATTEMPTS) {
            blockUser(); // Block the user after maximum attempts
        }
    }

    public boolean isBlocked() {
        if (blocked) {
            // Check if the blocking period has expired
            if (blockedUntil != null && LocalDateTime.now().isAfter(blockedUntil)) {
                blocked = false; // Unblock the user
                attemptCount = 0; // Reset attempts after unblocking
            }
        }
        return blocked;
    }

    private void blockUser() {
        blocked = true;
        blockedUntil = LocalDateTime.now().plusMinutes(15); // Block for 15 minutes
    }
}
