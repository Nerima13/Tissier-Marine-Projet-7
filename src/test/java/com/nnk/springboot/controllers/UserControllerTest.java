package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    UserService userService;

    @Mock
    BindingResult bindingResult;

    @InjectMocks
    UserController controller;

    // 1) GET /user/list => returns "user/list" and adds the list to the model
    @Test
    public void home_returnsListView_andAddsUsersToModel() {
        Model model = new ConcurrentModel();
        List<User> users = List.of(new User(), new User());
        when(userService.findAll()).thenReturn(users);

        String view = controller.home(model);

        assertEquals("user/list", view);
        assertTrue(model.containsAttribute("users"));
        assertSame(users, model.getAttribute("users"));
        verify(userService).findAll();
        verifyNoMoreInteractions(userService);
    }

    // 2) GET /user/add => returns "user/add" and adds an empty User
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

    // 3) POST /user/validate with errors => returns "user/add" without calling the service
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

    // 4) POST /user/validate without errors => calls create() with the form and redirects
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
        verifyNoMoreInteractions(userService);
    }

    // 5) GET /user/update/{id} => returns "user/update" with existing user but password cleared
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
        assertTrue(model.containsAttribute("user"));

        User modelUser = (User) model.getAttribute("user");
        assertNotNull(modelUser);
        assertEquals(1, modelUser.getId());
        assertEquals("john.doe", modelUser.getUsername());
        assertEquals("John Doe", modelUser.getFullname());
        assertEquals("USER", modelUser.getRole());
        // Controller clears the password field for the form
        assertEquals("", modelUser.getPassword());

        verify(userService).findById(1);
        verifyNoMoreInteractions(userService);
    }

    // 6) POST /user/update/{id} with errors => returns "user/update" without calling service
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

    // 7) POST /user/update/{id} with blank password => keeps current password and redirects
    @Test
    public void updateUser_whenPasswordBlank_usesCurrentPassword_andRedirectsToList() {
        Model model = new ConcurrentModel();

        // Current user in DB (with encoded password)
        User current = new User();
        current.setId(1);
        current.setUsername("john.doe");
        current.setPassword("encodedPassword");
        current.setFullname("John Doe");
        current.setRole("USER");

        // Form submitted with blank password
        User form = new User();
        form.setUsername("john.doe.updated");
        form.setPassword(""); // blank -> controller should reuse current password
        form.setFullname("John Doe Updated");
        form.setRole("ADMIN");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.findById(1)).thenReturn(current);

        String view = controller.updateUser(1, form, bindingResult, model);

        assertEquals("redirect:/user/list", view);
        verify(bindingResult).hasErrors();
        // findById is called to retrieve current user
        verify(userService).findById(1);

        // Verify that update() is called with a User that has current password
        verify(userService).update(eq(1), argThat(u ->
                u != null
                        && Integer.valueOf(1).equals(u.getId())
                        && "john.doe.updated".equals(u.getUsername())
                        && "John Doe Updated".equals(u.getFullname())
                        && "ADMIN".equals(u.getRole())
                        && "encodedPassword".equals(u.getPassword())));
        verifyNoMoreInteractions(userService);
    }

    // 8) POST /user/update/{id} with new password => passes new password to service and redirects
    @Test
    public void updateUser_whenNewPasswordProvided_passesItToService_andRedirectsToList() {
        Model model = new ConcurrentModel();

        // Current user in DB
        User current = new User();
        current.setId(1);
        current.setUsername("john.doe");
        current.setPassword("encodedPassword");
        current.setFullname("John Doe");
        current.setRole("USER");

        // Form with new raw password
        User form = new User();
        form.setUsername("john.doe.updated");
        form.setPassword("NewPassword1!");
        form.setFullname("John Doe Updated");
        form.setRole("ADMIN");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.findById(1)).thenReturn(current);

        String view = controller.updateUser(1, form, bindingResult, model);

        assertEquals("redirect:/user/list", view);
        verify(bindingResult).hasErrors();
        verify(userService).findById(1);

        // Controller should pass the new password string to the service
        verify(userService).update(eq(1), argThat(u ->
                u != null
                        && Integer.valueOf(1).equals(u.getId())
                        && "john.doe.updated".equals(u.getUsername())
                        && "John Doe Updated".equals(u.getFullname())
                        && "ADMIN".equals(u.getRole())
                        && "NewPassword1!".equals(u.getPassword())));
        verifyNoMoreInteractions(userService);
    }

    // 9) GET /user/delete/{id} => calls delete() and redirects
    @Test
    public void deleteUser_callsService_andRedirectsToList() {
        String view = controller.deleteUser(3);

        assertEquals("redirect:/user/list", view);
        verify(userService).delete(3);
        verifyNoMoreInteractions(userService);
    }
}