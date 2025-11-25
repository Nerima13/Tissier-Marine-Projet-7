package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.services.RatingService;
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

@ExtendWith(MockitoExtension.class)
public class RatingControllerTest {

    @Mock
    private RatingService ratingService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private RatingController controller;

    /**
     * Clears the SecurityContext after each test.
     */
    @AfterEach
    public void clear() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Tests that the home method returns the list view and adds ratings and user info to the model.
     */
    @Test
    public void home_returnsListView_andAddsRatingsToModel() {
        Model model = new ConcurrentModel();
        List<Rating> ratings = List.of(new Rating(), new Rating());

        when(ratingService.findAll()).thenReturn(ratings);

        when(authentication.getName()).thenReturn("testUser");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER"))).when(authentication).getAuthorities();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String view = controller.home(model);

        assertEquals("rating/list", view);

        assertTrue(model.containsAttribute("ratings"));
        assertSame(ratings, model.getAttribute("ratings"));

        assertEquals("testUser", model.getAttribute("username"));
        assertEquals(false, model.getAttribute("isAdmin"));

        verify(ratingService).findAll();
    }

    /**
     * Tests that the add form view is returned with a new Rating object.
     */
    @Test
    public void addRatingForm_returnsAddView_andAddsEmptyRating() {
        Model model = new ConcurrentModel();

        String view = controller.addRatingForm(model);

        assertEquals("rating/add", view);
        assertTrue(model.containsAttribute("rating"));
        assertNotNull(model.getAttribute("rating"));
        assertTrue(model.getAttribute("rating") instanceof Rating);
        verifyNoInteractions(ratingService);
    }

    /**
     * Tests that validation errors prevent saving and return the user to the add form.
     */
    @Test
    public void validate_whenHasErrors_returnsAddView_andDoesNotCallService() {
        Model model = new ConcurrentModel();
        Rating form = new Rating();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.validate(form, bindingResult, model);

        assertEquals("rating/add", view);
        verify(bindingResult).hasErrors();
        verifyNoInteractions(ratingService);
    }

    /**
     * Tests that valid data triggers the service creation and redirects to the list.
     */
    @Test
    public void validate_whenNoErrors_callsServiceWithForm_andRedirectsToList() {
        Model model = new ConcurrentModel();
        Rating form = new Rating();
        form.setMoodysRating("Moody A");
        form.setSandPRating("S&P A");
        form.setFitchRating("Fitch A");
        form.setOrderNumber(1);

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.validate(form, bindingResult, model);

        assertEquals("redirect:/rating/list", view);
        verify(bindingResult).hasErrors();

        verify(ratingService).create(argThat(r ->
                r != null
                        && "Moody A".equals(r.getMoodysRating())
                        && "S&P A".equals(r.getSandPRating())
                        && "Fitch A".equals(r.getFitchRating())
                        && Integer.valueOf(1).equals(r.getOrderNumber())));
    }

    /**
     * Tests that the update form is displayed with the existing Rating data.
     */
    @Test
    public void showUpdateForm_returnsUpdateView_andAddsExistingRating() {
        Model model = new ConcurrentModel();
        Rating existing = new Rating();
        existing.setId(1);
        existing.setMoodysRating("Existing Moody");
        when(ratingService.findById(1)).thenReturn(existing);

        String view = controller.showUpdateForm(1, model);

        assertEquals("rating/update", view);
        assertTrue(model.containsAttribute("rating"));
        assertSame(existing, model.getAttribute("rating"));
        verify(ratingService).findById(1);
    }

    /**
     * Tests that validation errors during update return the user to the update form.
     */
    @Test
    public void updateRating_whenHasErrors_returnsUpdateView_andDoesNotCallService() {
        Model model = new ConcurrentModel();
        Rating form = new Rating();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.updateRating(1, form, bindingResult, model);

        assertEquals("rating/update", view);
        verify(bindingResult).hasErrors();
        verifyNoInteractions(ratingService);
    }

    /**
     * Tests that a valid update triggers the service update and redirects to the list.
     */
    @Test
    public void updateRating_whenNoErrors_callsServiceWithForm_andRedirectsToList() {
        Model model = new ConcurrentModel();
        Rating form = new Rating();
        form.setMoodysRating("Updated Moody");
        form.setSandPRating("Updated S&P");
        form.setFitchRating("Updated Fitch");
        form.setOrderNumber(2);

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.updateRating(5, form, bindingResult, model);

        assertEquals("redirect:/rating/list", view);
        verify(bindingResult).hasErrors();

        verify(ratingService).update(eq(5), argThat(r ->
                r != null
                        && "Updated Moody".equals(r.getMoodysRating())
                        && "Updated S&P".equals(r.getSandPRating())
                        && "Updated Fitch".equals(r.getFitchRating())
                        && Integer.valueOf(2).equals(r.getOrderNumber())));
    }

    /**
     * Tests that deleting a rating calls the service and redirects to the list.
     */
    @Test
    public void deleteRating_callsService_andRedirectsToList() {
        String view = controller.deleteRating(3);

        assertEquals("redirect:/rating/list", view);
        verify(ratingService).delete(3);
    }
}