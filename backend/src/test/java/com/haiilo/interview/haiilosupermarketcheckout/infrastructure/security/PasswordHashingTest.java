package com.haiilo.interview.haiilosupermarketcheckout.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;


public class PasswordHashingTest {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void shouldEncodePasswordAndVerify() {
        String rawPassword = "totallysecurepassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("Raw: "+ rawPassword);
        System.out.println("Encoded: " + encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void shouldProduceDifferentHashesForSamePassword() {
        String rawPassword = "totallyuniquepassword123";

        String hash1 = passwordEncoder.encode(rawPassword);
        String hash2 = passwordEncoder.encode(rawPassword);

        assertNotEquals(hash1, hash2);

        assertTrue(passwordEncoder.matches(rawPassword, hash1));
        assertTrue(passwordEncoder.matches(rawPassword, hash2));
    }
}
