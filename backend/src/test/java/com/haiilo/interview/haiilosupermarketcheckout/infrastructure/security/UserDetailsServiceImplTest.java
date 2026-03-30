package com.haiilo.interview.haiilosupermarketcheckout.infrastructure.security;

import com.haiilo.interview.haiilosupermarketcheckout.domain.model.User;
import com.haiilo.interview.haiilosupermarketcheckout.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        User user = new User();
        user.setUsername("dev_dudette");

        when(userRepository.findByUsername("dev_dudette"))
                .thenReturn(Optional.of(user));

        UserDetails result = userDetailsServiceImpl.loadUserByUsername("dev_dudette");

        assertNotNull(result);
        assertEquals("dev_dudette", result.getUsername());
        verify(userRepository, times(1)).findByUsername("dev_dudette");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findByUsername("n/e"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsServiceImpl.loadUserByUsername("n/e");
        });
    }
}
