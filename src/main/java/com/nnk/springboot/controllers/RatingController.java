package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.services.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


/**
 * Controller handling CRUD operations for {@link Rating}.
 * Provides routes to list, add, update, and delete Rating entries.
 */
@Controller
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    /**
     * Displays the list of all Rating entries.
     *
     * @param model the model used to pass data to the view
     * @return the list view
     */
    @RequestMapping("/rating/list")
    public String home(Model model) {
        model.addAttribute("ratings", ratingService.findAll());

        // Retrieve the logged-in user's authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Username for display
        String username = (authentication != null) ? authentication.getName() : "anonymous";
        model.addAttribute("username", username);

        // Check if the logged-in user has ADMIN role
        boolean isAdmin = authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        model.addAttribute("isAdmin", isAdmin);

        return "rating/list";
    }

    /**
     * Displays the form to add a new Rating.
     *
     * @param model the model used to pass data to the view
     * @return the add form view
     */
    @GetMapping("/rating/add")
    public String addRatingForm(Model model) {
        model.addAttribute("rating", new Rating());
        return "rating/add";
    }

    /**
     * Validates and saves a new Rating entry.
     *
     * @param rating the Rating submitted from the form
     * @param result validation result
     * @return redirect to the list view if successful, otherwise return the form
     */
    @PostMapping("/rating/validate")
    public String validate(@Valid @ModelAttribute("rating") Rating rating,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            return "rating/add";
        }
        ratingService.create(rating);
        return "redirect:/rating/list";
    }

    /**
     * Displays the form to update an existing Rating.
     *
     * @param id the ID of the Rating to update
     * @param model the model used to pass data to the view
     * @return the update form view
     */
    @GetMapping("/rating/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        Rating existing = ratingService.findById(id);
        model.addAttribute("rating", existing);
        return "rating/update";
    }

    /**
     * Validates and updates an existing Rating entry.
     *
     * @param id the ID of the Rating being updated
     * @param rating the updated data submitted from the form
     * @param result validation result
     * @return redirect to the list view if successful, otherwise return the form
     */
    @PostMapping("/rating/update/{id}")
    public String updateRating(@PathVariable("id") Integer id,
                               @Valid @ModelAttribute("rating") Rating rating,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "rating/update";
        }
        ratingService.update(id, rating);
        return "redirect:/rating/list";
    }

    /**
     * Deletes a Rating entry by its ID.
     *
     * @param id the ID of the Rating to delete
     * @return redirect to the list view
     */
    @GetMapping("/rating/delete/{id}")
    public String deleteRating(@PathVariable("id") Integer id) {
        ratingService.delete(id);
        return "redirect:/rating/list";
    }
}
