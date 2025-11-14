package com.nnk.springboot.services;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("marine");
        user.setPassword("Password1!");
        user.setFullname("Marine Test");
        user.setRole("USER");
    }

    @Test
    void findAll_shouldReturnListOfUsers() {
        when(repository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user, result.get(0));
        verify(repository).findAll();
    }

    @Test
    void findById_whenExisting_shouldReturnUser() {
        when(repository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.findById(1);

        assertEquals(1, result.getId());
        assertEquals("marine", result.getUsername());
        verify(repository).findById(1);
    }

    @Test
    void findById_whenNotExisting_shouldThrow() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.findById(999));

        assertEquals(404, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("User not found with id: 999"));
        verify(repository).findById(999);
    }

    @Test
    void create_shouldResetIdEncodePasswordAndSave() {
        User toCreate = new User();
        toCreate.setId(45);
        toCreate.setUsername("newuser");
        toCreate.setPassword("Strong1!");
        toCreate.setFullname("New User");
        toCreate.setRole("ADMIN");

        User saved = new User();
        saved.setId(1);
        saved.setUsername("newuser");
        saved.setPassword("encodedPass");
        saved.setFullname("New User");
        saved.setRole("ADMIN");

        when(repository.save(argThat(u ->
                u.getUsername().equals("newuser"))))
                .thenReturn(saved);

        User result = userService.create(toCreate);

        assertEquals(1, result.getId());
        assertEquals("newuser", result.getUsername());
        assertEquals("New User", result.getFullname());
        assertEquals("ADMIN", result.getRole());
        assertNotEquals("Strong1!", result.getPassword());

        verify(repository).save(argThat(u ->
                u.getId() == null &&
                        u.getUsername().equals("newuser") &&
                        u.getFullname().equals("New User") &&
                        u.getRole().equals("ADMIN") &&
                        !u.getPassword().equals("Strong1!")));
    }

    @Test
    void update_whenExisting_shouldUpdateFieldsAndEncodePassword() {
        when(repository.findById(1)).thenReturn(Optional.of(user));

        User updates = new User();
        updates.setUsername("updated");
        updates.setPassword("NewPass1!");
        updates.setFullname("Updated User");
        updates.setRole("ADMIN");

        when(repository.save(argThat(u ->
                u.getUsername().equals("updated")
        ))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.update(1, updates);

        assertEquals("updated", result.getUsername());
        assertEquals("Updated User", result.getFullname());
        assertEquals("ADMIN", result.getRole());
        assertNotEquals("NewPass1!", result.getPassword());

        verify(repository).save(argThat(u ->
                u.getUsername().equals("updated") &&
                        u.getFullname().equals("Updated User") &&
                        u.getRole().equals("ADMIN") &&
                        !u.getPassword().equals("NewPass1!")));
    }

    @Test
    void update_whenNotExisting_shouldThrow() {
        when(repository.findById(123)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> userService.update(123, new User()));

        verify(repository).findById(123);
        verify(repository, never()).save(any());
    }

    @Test
    void delete_whenExisting_shouldDeleteUser() {
        when(repository.findById(1)).thenReturn(Optional.of(user));

        userService.delete(1);

        verify(repository).findById(1);
        verify(repository).delete(user);
    }

    @Test
    void delete_whenNotExisting_shouldThrow() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> userService.delete(999));

        verify(repository).findById(999);
        verify(repository, never()).delete((User) any());
    }
}