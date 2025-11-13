package com.nnk.springboot.services;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Custom implementation of {@link UserDetailsService} used by Spring Security
 * to load user information from the database during authentication.
 *
 * <p>This service retrieves a {@link User} by username, validates its role,
 * and converts it into a Spring Security {@link UserDetails} object.</p>
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user by its username and converts it to a {@link UserDetails} instance.
     *
     * @param username the username provided during login
     * @return the authenticated user's details
     * @throws UsernameNotFoundException if the user does not exist or has no valid role
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieve the user from the database (username must be unique)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found : " + username));

        // Normalize the role -> Spring expects roles to start with "ROLE_"
        String rawRole = user.getRole();
        if (rawRole == null || rawRole.isBlank()) {
            throw new UsernameNotFoundException("No role defined for the user : " + username);
        }
        String role = rawRole.startsWith("ROLE_") ? rawRole : "ROLE_" + rawRole.toUpperCase();

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        // Build the UserDetails object expected by Spring Security
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())      // already encrypted with BCrypt in the database
                .authorities(authorities)          // ex : ROLE_ADMIN / ROLE_USER
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}

