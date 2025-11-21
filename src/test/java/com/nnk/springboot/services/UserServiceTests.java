package com.nnk.springboot.services;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository repository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(1);
        existingUser.setUsername("marine");
        existingUser.setPassword("encodedPassword"); // already encoded in database
        existingUser.setFullname("Marine Test");
        existingUser.setRole("USER");
    }

    @Test
    void findAll_shouldReturnListOfUsers() {
        when(repository.findAll()).thenReturn(List.of(existingUser));

        List<User> result = userService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(existingUser, result.get(0));
        verify(repository).findAll();
    }

    @Test
    void findById_whenExisting_shouldReturnUser() {
        when(repository.findById(1)).thenReturn(Optional.of(existingUser));

        User result = userService.findById(1);

        assertEquals(1, result.getId());
        assertEquals("marine", result.getUsername());
        verify(repository).findById(1);
    }

    @Test
    void findById_whenNotExisting_shouldThrow404() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.findById(999));

        assertEquals(404, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("User not found with id: 999"));
        verify(repository).findById(999);
    }

    @Test
    void create_shouldResetIdValidatePasswordEncodeAndSave() {
        // GIVEN
        User toCreate = new User();
        toCreate.setId(45); // will be reset to null
        toCreate.setUsername("newuser");
        toCreate.setPassword("Strong1!");
        toCreate.setFullname("New User");
        toCreate.setRole("ADMIN");

        when(passwordEncoder.encode("Strong1!")).thenReturn("encodedStrong1!");

        User saved = new User();
        saved.setId(10);
        saved.setUsername("newuser");
        saved.setPassword("encodedStrong1!");
        saved.setFullname("New User");
        saved.setRole("ADMIN");

        when(repository.save(any(User.class))).thenReturn(saved);

        User result = userService.create(toCreate);

        assertEquals(10, result.getId());
        assertEquals("newuser", result.getUsername());
        assertEquals("New User", result.getFullname());
        assertEquals("ADMIN", result.getRole());
        assertEquals("encodedStrong1!", result.getPassword());

        // verifies that the object sent to the repository has a null id and an encoded password
        verify(repository).save(argThat(u ->
                u.getId() == null &&
                        u.getUsername().equals("newuser") &&
                        u.getFullname().equals("New User") &&
                        u.getRole().equals("ADMIN") &&
                        !u.getPassword().equals("Strong1!")));

        verify(passwordEncoder).encode("Strong1!");
    }

    @Test
    void create_whenPasswordInvalid_shouldThrowIllegalArgumentException() {
        User toCreate = new User();
        toCreate.setUsername("baduser");
        toCreate.setPassword("weak"); // too short, no uppercase letter, no digit, no special symbol
        toCreate.setFullname("Bad User");
        toCreate.setRole("USER");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.create(toCreate));

        assertTrue(ex.getMessage().contains("Password must be at least 8 characters long"));
        verify(repository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void update_whenExistingWithNewPassword_shouldUpdateFieldsValidateAndEncodePassword() {
        // GIVEN
        when(repository.findById(1)).thenReturn(Optional.of(existingUser));

        User updates = new User();
        updates.setUsername("updated");
        updates.setPassword("NewPass1!");
        updates.setFullname("Updated User");
        updates.setRole("ADMIN");

        when(passwordEncoder.encode("NewPass1!")).thenReturn("encodedNewPass1!");

        when(repository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.update(1, updates);

        assertEquals("updated", result.getUsername());
        assertEquals("Updated User", result.getFullname());
        assertEquals("ADMIN", result.getRole());
        assertEquals("encodedNewPass1!", result.getPassword());
        assertNotEquals("NewPass1!", result.getPassword());

        verify(repository).findById(1);
        verify(passwordEncoder).encode("NewPass1!");
        verify(repository).save(argThat(u ->
                u.getUsername().equals("updated") &&
                        u.getFullname().equals("Updated User") &&
                        u.getRole().equals("ADMIN") &&
                        u.getPassword().equals("encodedNewPass1!")));
    }

    @Test
    void update_whenExistingWithBlankPassword_shouldKeepExistingPassword() {
        // GIVEN
        when(repository.findById(1)).thenReturn(Optional.of(existingUser));

        User updates = new User();
        updates.setUsername("updated");
        updates.setPassword("   "); // blank → do not update password
        updates.setFullname("Updated User");
        updates.setRole("ADMIN");

        when(repository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        User result = userService.update(1, updates);

        // THEN
        assertEquals("updated", result.getUsername());
        assertEquals("Updated User", result.getFullname());
        assertEquals("ADMIN", result.getRole());
        // le mot de passe reste celui d'origine
        assertEquals("encodedPassword", result.getPassword());

        verify(repository).findById(1);
        verify(repository).save(argThat(u ->
                u.getUsername().equals("updated") &&
                        u.getFullname().equals("Updated User") &&
                        u.getRole().equals("ADMIN") &&
                        u.getPassword().equals("encodedPassword")));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void update_whenNotExisting_shouldThrow404() {
        when(repository.findById(123)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.update(123, new User()));

        assertEquals(404, ex.getStatusCode().value());
        verify(repository).findById(123);
        verify(repository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void delete_whenExisting_shouldDeleteUser() {
        when(repository.findById(1)).thenReturn(Optional.of(existingUser));

        userService.delete(1);

        verify(repository).findById(1);
        verify(repository).delete(existingUser);
    }

    @Test
    void delete_whenNotExisting_shouldThrow404() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.delete(999));

        assertEquals(404, ex.getStatusCode().value());
        verify(repository).findById(999);
        verify(repository, never()).delete(any(User.class));
    }
}