package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserController}.
 */
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserController controller;

    /**
     * Clears the SecurityContext after each test.
     */
    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Tests that the home method returns the list view and adds users and user info to the model.
     */
    @Test
    public void home_returnsListView_andAddsUsersToModel() {
        Model model = new ConcurrentModel();
        List<User> users = List.of(new User(), new User());

        when(userService.findAll()).thenReturn(users);

        when(authentication.getName()).thenReturn("testUser");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER"))).when(authentication).getAuthorities();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String view = controller.home(model);

        assertEquals("user/list", view);

        assertTrue(model.containsAttribute("users"));
        assertSame(users, model.getAttribute("users"));

        assertEquals("testUser", model.getAttribute("username"));
        assertEquals(false, model.getAttribute("isAdmin"));

        verify(userService).findAll();
    }

    /**
     * Tests that the add form view is returned with a new empty User object.
     */
    @Test
    public void addUser_returnsAddView_andAddsEmptyUser() {
        Model model = new ConcurrentModel();

        String view = controller.addUser(model);

        assertEquals("user/add", view);
        assertTrue(model.containsAttribute("user"));
        assertNotNull(model.getAttribute("user"));
        assertTrue(model.getAttribute("user") instanceof User);
        verifyNoInteractions(userService);
    }

    /**
     * Tests that validation errors prevent saving and return the user to the add form.
     */
    @Test
    public void validate_whenHasErrors_returnsAddView_andDoesNotCallService() {
        Model model = new ConcurrentModel();
        User form = new User();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.validate(form, bindingResult, model);

        assertEquals("user/add", view);
        verify(bindingResult).hasErrors();
        verifyNoInteractions(userService);
    }

    /**
     * Tests that valid data triggers the service creation and redirects to the list.
     */
    @Test
    public void validate_whenNoErrors_callsServiceWithForm_andRedirectsToList() {
        Model model = new ConcurrentModel();
        User form = new User();
        form.setUsername("john.doe");
        form.setPassword("Password1!");
        form.setFullname("John Doe");
        form.setRole("ADMIN");

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.validate(form, bindingResult, model);

        assertEquals("redirect:/user/list", view);
        verify(bindingResult).hasErrors();

        // Verify that the User passed to the service contains these values
        verify(userService).create(argThat(u ->
                u != null
                        && "john.doe".equals(u.getUsername())
                        && "Password1!".equals(u.getPassword())
                        && "John Doe".equals(u.getFullname())
                        && "ADMIN".equals(u.getRole())));
    }

    /**
     * Tests that the update form is displayed with existing data, and the password field is cleared.
     */
    @Test
    public void showUpdateForm_returnsUpdateView_andAddsExistingUserWithEmptyPassword() {
        Model model = new ConcurrentModel();
        User existing = new User();
        existing.setId(1);
        existing.setUsername("john.doe");
        existing.setPassword("encodedPassword");
        existing.setFullname("John Doe");
        existing.setRole("USER");

        when(userService.findById(1)).thenReturn(existing);

        String view = controller.showUpdateForm(1, model);

        assertEquals("user/update", view);

        User modelUser = (User) model.getAttribute("user");
        assertEquals("", modelUser.getPassword());

        verify(userService).findById(1);
    }

    /**
     * Tests that validation errors during update return the user to the update form.
     */
    @Test
    public void updateUser_whenHasErrors_returnsUpdateView_andDoesNotCallService() {
        Model model = new ConcurrentModel();
        User form = new User();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.updateUser(1, form, bindingResult, model);

        assertEquals("user/update", view);
        verify(bindingResult).hasErrors();
        verifyNoInteractions(userService);
    }

    /**
     * Tests that if the password field is left blank, the existing password is preserved during the update.
     */
    @Test
    public void updateUser_whenPasswordBlank_usesCurrentPassword_andRedirectsToList() {
        Model model = new ConcurrentModel();
        String originalPassword = "encodedPassword";

        // Current user in DB
        User current = new User();
        current.setId(1);
        current.setPassword(originalPassword);

        // Form submitted with blank password
        User form = new User();
        form.setUsername("john.doe.updated");
        form.setPassword("");
        form.setFullname("John Doe Updated");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.findById(1)).thenReturn(current);

        String view = controller.updateUser(1, form, bindingResult, model);

        assertEquals("redirect:/user/list", view);
        verify(userService).findById(1);

        // Verify that update() is called with the original password
        verify(userService).update(eq(1), argThat(u ->
                u != null
                        && "john.doe.updated".equals(u.getUsername())
                        && originalPassword.equals(u.getPassword())));
    }

    /**
     * Tests that if a new password is provided, it is passed to the service for update.
     */
    @Test
    public void updateUser_whenNewPasswordProvided_passesItToService_andRedirectsToList() {
        Model model = new ConcurrentModel();

        // Current user in DB (needed for findById call in controller)
        User current = new User();
        current.setId(1);
        current.setPassword("encodedPassword");

        // Form with new raw password
        User form = new User();
        form.setUsername("john.doe.updated");
        String newPassword = "NewPassword1!";
        form.setPassword(newPassword);
        form.setFullname("John Doe Updated");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.findById(1)).thenReturn(current);

        String view = controller.updateUser(1, form, bindingResult, model);

        assertEquals("redirect:/user/list", view);
        verify(userService).findById(1);

        // Verify that the new password string is passed to the service
        verify(userService).update(eq(1), argThat(u ->
                u != null
                        && "john.doe.updated".equals(u.getUsername())
                        && newPassword.equals(u.getPassword())));
    }

    /**
     * Tests that deleting a user calls the service and redirects to the list.
     */
    @Test
    public void deleteUser_callsService_andRedirectsToList() {
        String view = controller.deleteUser(3);

        assertEquals("redirect:/user/list", view);
        verify(userService).delete(3);
    }
}