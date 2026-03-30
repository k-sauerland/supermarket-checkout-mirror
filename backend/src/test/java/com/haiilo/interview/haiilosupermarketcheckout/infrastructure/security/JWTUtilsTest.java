package com.haiilo.interview.haiilosupermarketcheckout.infrastructure.security;

import com.haiilo.interview.haiilosupermarketcheckout.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class JWTUtilsTest {

    private JWTUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JWTUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecretKey", "mySecretKeyThatIsVeryLongAndSecure12345678901234567890");
        ReflectionTestUtils.setField(jwtUtils, "EXPIRATION_TIME",60000);
    }

    @Test
    void shouldGenerateTokenAndValidateToken() {
        User testUser = new User();
        testUser.setUsername("tester");
        testUser.setPassword("encoded_password");
        testUser.setId(1L);

        String token = jwtUtils.generateToken(testUser);

        assertNotNull(token);
        assertEquals("tester", jwtUtils.getUsernameFromToken(token));
    }

    @Test
    void shouldFailValidationWithInvalidToken() {
        String fakeToken = "this.is.not.a.real.token";
        assertFalse(jwtUtils.validateJwtToken(fakeToken));
    }
}
