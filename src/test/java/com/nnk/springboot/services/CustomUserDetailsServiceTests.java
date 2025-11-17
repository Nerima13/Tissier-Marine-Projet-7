package com.nnk.springboot.services;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import com.nnk.springboot.services.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTests {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    CustomUserDetailsService customUserDetailsService;

    // 1) User exists, role without "ROLE_" prefix => should be normalized to "ROLE_ADMIN"
    @Test
    void loadUserByUsername_whenUserExistsWithRawRole_shouldReturnUserDetailsWithNormalizedRole() {
        User user = new User();
        user.setId(1);
        user.setUsername("john");
        user.setPassword("encodedPassword");
        user.setFullname("John Doe");
        user.setRole("admin"); // without ROLE_ prefix

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("john");

        assertNotNull(userDetails);
        assertEquals("john", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(
                authorities.stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())),
                "Expected authority ROLE_ADMIN");

        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());

        verify(userRepository).findByUsername("john");
        verifyNoMoreInteractions(userRepository);
    }

    // 2) User exists, role already "ROLE_MANAGER" => should stay "ROLE_MANAGER"
    @Test
    void loadUserByUsername_whenUserExistsWithPrefixedRole_shouldKeepRoleAsIs() {
        User user = new User();
        user.setId(2);
        user.setUsername("alice");
        user.setPassword("encodedPwd");
        user.setFullname("Alice Doe");
        user.setRole("ROLE_MANAGER");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("alice");

        assertNotNull(userDetails);
        assertEquals("alice", userDetails.getUsername());
        assertEquals("encodedPwd", userDetails.getPassword());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(
                authorities.stream().anyMatch(a -> "ROLE_MANAGER".equals(a.getAuthority())),
                "Expected authority ROLE_MANAGER");

        verify(userRepository).findByUsername("alice");
        verifyNoMoreInteractions(userRepository);
    }

    // 3) User does not exist => should throw UsernameNotFoundException
    @Test
    void loadUserByUsername_whenUserDoesNotExist_shouldThrowUsernameNotFoundException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("unknown"));

        assertTrue(ex.getMessage().contains("User not found : unknown"));
        verify(userRepository).findByUsername("unknown");
        verifyNoMoreInteractions(userRepository);
    }

    // 4) User exists but role is null => should throw UsernameNotFoundException
    @Test
    void loadUserByUsername_whenRoleIsNull_shouldThrowUsernameNotFoundException() {
        User user = new User();
        user.setId(3);
        user.setUsername("noRoleUser");
        user.setPassword("pwd");
        user.setFullname("No Role");
        user.setRole(null);

        when(userRepository.findByUsername("noRoleUser")).thenReturn(Optional.of(user));

        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("noRoleUser"));

        assertTrue(ex.getMessage().contains("No role defined for the user : noRoleUser"));
        verify(userRepository).findByUsername("noRoleUser");
        verifyNoMoreInteractions(userRepository);
    }

    // 5) User exists but role is blank => should throw UsernameNotFoundException
    @Test
    void loadUserByUsername_whenRoleIsBlank_shouldThrowUsernameNotFoundException() {
        User user = new User();
        user.setId(4);
        user.setUsername("blankRoleUser");
        user.setPassword("pwd");
        user.setFullname("Blank Role");
        user.setRole("   "); // blank string

        when(userRepository.findByUsername("blankRoleUser")).thenReturn(Optional.of(user));

        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("blankRoleUser"));

        assertTrue(ex.getMessage().contains("No role defined for the user : blankRoleUser"));
        verify(userRepository).findByUsername("blankRoleUser");
        verifyNoMoreInteractions(userRepository);
    }
}