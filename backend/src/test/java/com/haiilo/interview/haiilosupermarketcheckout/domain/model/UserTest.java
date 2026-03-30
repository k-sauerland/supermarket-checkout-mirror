package com.haiilo.interview.haiilosupermarketcheckout.domain.model;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    @Test
    public void testUserCustomerBridge() {
        User user = new User();
        user.setUsername("test-customer");
        user.setPassword("hashed-password");
        user.setRole("USER");

        Customer customer = new Customer();
        customer.setFirstName("Jane");
        customer.setLastName("Doe");
        customer.setEmail("jane@example.com");

        customer.setUser(user);

        assertEquals("USER", customer.getUser().getRole());
        assertEquals("test-customer", customer.getUser().getUsername());
        assertNotNull(customer.getEmail());
    }

    @Test
    public void testAdminHasNoCustomerProfile() {
        User admin = new User();
        admin.setRole("ADMIN");
        admin.setUsername("admin-user");

        assertEquals("ROLE_ADMIN", admin.getAuthorities().iterator().next().getAuthority());
        assertNotNull(admin.getUsername());
    }
}