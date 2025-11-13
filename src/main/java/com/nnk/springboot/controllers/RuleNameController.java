package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.services.RuleNameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Controller handling CRUD operations for {@link RuleName}.
 * Provides routes to list, add, update, and delete RuleName entries.
 */
@Controller
@RequiredArgsConstructor
public class RuleNameController {

    private final RuleNameService ruleNameService;

    /**
     * Displays the list of all RuleName entries.
     *
     * @param model the model used to pass data to the view
     * @return the list view
     */
    @RequestMapping("/ruleName/list")
    public String home(Model model) {
        model.addAttribute("ruleNames", ruleNameService.findAll());
        return "ruleName/list";
    }

    /**
     * Displays the form to add a new RuleName.
     *
     * @param model the model used to pass data to the view
     * @return the add form view
     */
    @GetMapping("/ruleName/add")
    public String addRuleForm(Model model) {
        model.addAttribute("ruleName", new RuleName());
        return "ruleName/add";
    }

    /**
     * Validates and saves a new RuleName entry.
     *
     * @param ruleName the RuleName submitted from the form
     * @param result validation result
     * @return redirect to the list view if successful, otherwise return the form
     */
    @PostMapping("/ruleName/validate")
    public String validate(@Valid @ModelAttribute("ruleName") RuleName ruleName,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            return "ruleName/add";
        }
        ruleNameService.create(ruleName);
        return "redirect:/ruleName/list";
    }

    /**
     * Displays the form to update an existing RuleName.
     *
     * @param id the ID of the RuleName to update
     * @param model the model used to pass data to the view
     * @return the update form view
     */
    @GetMapping("/ruleName/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        RuleName existing = ruleNameService.findById(id);
        model.addAttribute("ruleName", existing);
        return "ruleName/update";
    }

    /**
     * Validates and updates an existing RuleName entry.
     *
     * @param id the ID of the RuleName being updated
     * @param ruleName the updated data submitted from the form
     * @param result validation result
     * @return redirect to the list view if successful, otherwise return the form
     */
    @PostMapping("/ruleName/update/{id}")
    public String updateRuleName(@PathVariable("id") Integer id,
                                 @Valid @ModelAttribute("ruleName") RuleName ruleName,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            return "ruleName/update";
        }
        ruleNameService.update(id, ruleName);
        return "redirect:/ruleName/list";
    }

    /**
     * Deletes a RuleName entry by its ID.
     *
     * @param id the ID of the RuleName to delete
     * @return redirect to the list view
     */
    @GetMapping("/ruleName/delete/{id}")
    public String deleteRuleName(@PathVariable("id") Integer id) {
        ruleNameService.delete(id);
        return "redirect:/ruleName/list";
    }
}
