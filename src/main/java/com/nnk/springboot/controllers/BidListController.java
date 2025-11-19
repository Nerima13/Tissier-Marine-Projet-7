package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.services.BidListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller handling CRUD operations for {@link BidList}.
 * Provides routes to list, add, update, and delete BidList entries.
 */
@Controller
@RequiredArgsConstructor
public class BidListController {

    private final BidListService bidListService;

    /**
     * Displays the list of all BidList entries.
     *
     * @param model the model used to pass data to the view
     * @return the list view
     */
    @RequestMapping("/bidList/list")
    public String home(Model model) {
        List<BidList> bidLists = bidListService.findAll();
        model.addAttribute("bidLists", bidLists);

        // Retrieve the logged-in user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "anonymous";
        model.addAttribute("username", username);

        return "bidList/list";
    }

    /**
     * Displays the form to add a new BidList.
     *
     * @param model the model used to pass data to the view
     * @return the add form view
     */
    @GetMapping("/bidList/add")
    public String addBidForm(Model model) {
        model.addAttribute("bidList", new BidList());
        return "bidList/add";
    }

    /**
     * Validates and saves a new BidList entry.
     *
     * @param bidList the BidList data submitted by the form
     * @param result validation result
     * @return redirect to the list view if successful, otherwise return the form
     */
    @PostMapping("/bidList/validate")
    public String validate(@Valid @ModelAttribute("bidList") BidList bidList,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            return "bidList/add";
        }
        bidListService.create(bidList);
        return "redirect:/bidList/list";
    }

    /**
     * Displays the form to update an existing BidList.
     *
     * @param id the ID of the BidList to update
     * @param model the model used to pass data to the view
     * @return the update form view
     */
    @GetMapping("/bidList/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        BidList existing = bidListService.findById(id);
        model.addAttribute("bidList", existing);
        return "bidList/update";
    }

    /**
     * Validates and updates an existing BidList.
     *
     * @param id the ID of the BidList being updated
     * @param bidList submitted updated data
     * @param result validation result
     * @return redirect to the list view if successful, otherwise return the form
     */
    @PostMapping("/bidList/update/{id}")
    public String updateBid(@PathVariable("id") Integer id,
                            @Valid @ModelAttribute("bidList") BidList bidList,
                            BindingResult result,
                            Model model) {
        if (result.hasErrors()) {
            return "bidList/update";
        }
        bidListService.update(id, bidList);
        return "redirect:/bidList/list";
    }

    /**
     * Deletes a BidList entry by its ID.
     *
     * @param id the ID of the BidList to delete
     * @return redirect to the list view
     */
    @GetMapping("/bidList/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id) {
        bidListService.delete(id);
        return "redirect:/bidList/list";
    }
}
