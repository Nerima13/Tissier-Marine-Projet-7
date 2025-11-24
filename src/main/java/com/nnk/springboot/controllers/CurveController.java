package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.services.CurveService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


/**
 * Controller handling CRUD operations for {@link CurvePoint}.
 * Provides routes to list, add, update, and delete CurvePoint entries.
 */
@Controller
public class CurveController {

    private final CurveService curveService;

    public CurveController(CurveService curveService) {
        this.curveService = curveService;
    }

    /**
     * Displays the list of all CurvePoint entries.
     *
     * @param model the model used to pass data to the view
     * @return the list view
     */
    @RequestMapping("/curvePoint/list")
    public String home(Model model) {
        model.addAttribute("curvePoints", curveService.findAll());

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

        return "curvePoint/list";
    }

    /**
     * Displays the form to add a new CurvePoint.
     *
     * @param model the model used to pass data to the view
     * @return the add form view
     */
    @GetMapping("/curvePoint/add")
    public String addBidForm(Model model) {
        model.addAttribute("curvePoint", new CurvePoint());
        return "curvePoint/add";
    }

    /**
     * Validates and saves a new CurvePoint entry.
     *
     * @param curvePoint the CurvePoint submitted from the form
     * @param result validation result
     * @return redirect to the list view if successful, otherwise return the form
     */
    @PostMapping("/curvePoint/validate")
    public String validate(@Valid @ModelAttribute("curvePoint") CurvePoint curvePoint,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            return "curvePoint/add";
        }
        curveService.create(curvePoint);
        return "redirect:/curvePoint/list";
    }

    /**
     * Displays the form to update an existing CurvePoint.
     *
     * @param id the ID of the CurvePoint to update
     * @param model the model used to pass data to the view
     * @return the update form view
     */
    @GetMapping("/curvePoint/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        CurvePoint existing = curveService.findById(id);
        model.addAttribute("curvePoint", existing);
        return "curvePoint/update";
    }

    /**
     * Validates and updates an existing CurvePoint entry.
     *
     * @param id the ID of the CurvePoint being updated
     * @param curvePoint the updated data submitted from the form
     * @param result validation result
     * @return redirect to the list view if successful, otherwise return the form
     */
    @PostMapping("/curvePoint/update/{id}")
    public String updateBid(@PathVariable("id") Integer id,
                            @Valid @ModelAttribute("curvePoint") CurvePoint curvePoint,
                            BindingResult result,
                            Model model) {
        if (result.hasErrors()) {
            return "curvePoint/update";
        }
        curveService.update(id, curvePoint);
        return "redirect:/curvePoint/list";
    }

    /**
     * Deletes a CurvePoint entry by its ID.
     *
     * @param id the ID of the CurvePoint to delete
     * @return redirect to the list view
     */
    @GetMapping("/curvePoint/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id) {
        curveService.delete(id);
        return "redirect:/curvePoint/list";
    }
}
