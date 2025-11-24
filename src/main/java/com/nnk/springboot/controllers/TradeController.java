package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.services.TradeService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller handling CRUD operations for {@link Trade}.
 * Provides routes to list, add, update, and delete Trade entries.
 */
@Controller
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    /**
     * Displays the list of all Trade entries.
     *
     * @param model the model used to pass data to the view
     * @return the list view
     */
    @RequestMapping("/trade/list")
    public String home(Model model) {
        model.addAttribute("trades", tradeService.findAll());

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

        return "trade/list";
    }

    /**
     * Displays the form to add a new Trade.
     *
     * @param model the model used to pass data to the view
     * @return the add form view
     */
    @GetMapping("/trade/add")
    public String addUser(Model model) {
        model.addAttribute("trade", new Trade());
        return "trade/add";
    }

    /**
     * Validates and saves a new Trade entry.
     *
     * @param trade the Trade submitted from the form
     * @param result validation result
     * @return redirect to the list view if successful, otherwise return the form
     */
    @PostMapping("/trade/validate")
    public String validate(@Valid @ModelAttribute("trade") Trade trade,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            return "trade/add";
        }
        tradeService.create(trade);
        return "redirect:/trade/list";
    }

    /**
     * Displays the form to update an existing Trade.
     *
     * @param id the ID of the Trade to update
     * @param model the model used to pass data to the view
     * @return the update form view
     */
    @GetMapping("/trade/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        Trade existing = tradeService.findById(id);
        model.addAttribute("trade", existing);
        return "trade/update";
    }

    /**
     * Validates and updates an existing Trade entry.
     *
     * @param id the ID of the Trade being updated
     * @param trade the updated data submitted from the form
     * @param result validation result
     * @return redirect to the list view if successful, otherwise return the form
     */
    @PostMapping("/trade/update/{id}")
    public String updateTrade(@PathVariable("id") Integer id,
                              @Valid @ModelAttribute("trade") Trade trade,
                              BindingResult result,
                              Model model) {
        if (result.hasErrors()) {
            return "trade/update";
        }
        tradeService.update(id, trade);
        return "redirect:/trade/list";
    }

    /**
     * Deletes a Trade entry by its ID.
     *
     * @param id the ID of the Trade to delete
     * @return redirect to the list view
     */
    @GetMapping("/trade/delete/{id}")
    public String deleteTrade(@PathVariable("id") Integer id) {
        tradeService.delete(id);
        return "redirect:/trade/list";
    }
}
