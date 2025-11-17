package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.services.RatingService;
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
public class RatingControllerTest {

    @Mock
    RatingService ratingService;

    @Mock
    BindingResult bindingResult;

    @InjectMocks
    RatingController controller;

    // 1) GET /rating/list => returns "rating/list" and adds the list to the model
    @Test
    public void home_returnsListView_andAddsRatingsToModel() {
        Model model = new ConcurrentModel();
        List<Rating> ratings = List.of(new Rating(), new Rating());
        when(ratingService.findAll()).thenReturn(ratings);

        String view = controller.home(model);

        assertEquals("rating/list", view);
        assertTrue(model.containsAttribute("ratings"));
        assertSame(ratings, model.getAttribute("ratings"));
        verify(ratingService).findAll();
        verifyNoMoreInteractions(ratingService);
    }

    // 2) GET /rating/add => returns "rating/add" and adds an empty Rating
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

    // 3) POST /rating/validate with errors => returns "rating/add" without calling the service
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

    // 4) POST /rating/validate without errors => calls create() with the form and redirects
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

        // Verify that the Rating passed to the service contains these values
        verify(ratingService).create(argThat(r ->
                r != null
                        && "Moody A".equals(r.getMoodysRating())
                        && "S&P A".equals(r.getSandPRating())
                        && "Fitch A".equals(r.getFitchRating())
                        && Integer.valueOf(1).equals(r.getOrderNumber())));
        verifyNoMoreInteractions(ratingService);
    }

    // 5) GET /rating/update/{id} => returns "rating/update" with the found Rating
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
        verifyNoMoreInteractions(ratingService);
    }

    // 6) POST /rating/update/{id} with errors => returns "rating/update" without calling update()
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

    // 7) POST /rating/update/{id} without errors => calls update() with the form and redirects
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
        verifyNoMoreInteractions(ratingService);
    }

    // 8) GET /rating/delete/{id} => calls delete() and redirects
    @Test
    public void deleteRating_callsService_andRedirectsToList() {
        String view = controller.deleteRating(3);

        assertEquals("redirect:/rating/list", view);
        verify(ratingService).delete(3);
        verifyNoMoreInteractions(ratingService);
    }
}