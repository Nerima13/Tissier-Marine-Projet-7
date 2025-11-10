package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.services.RuleNameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class RuleNameController {

    private final RuleNameService ruleNameService;

    @RequestMapping("/ruleName/list")
    public String home(Model model) {
        model.addAttribute("ruleNames", ruleNameService.findAll());
        return "ruleName/list";
    }

    @GetMapping("/ruleName/add")
    public String addRuleForm(Model model) {
        model.addAttribute("ruleName", new RuleName());
        return "ruleName/add";
    }

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

    @GetMapping("/ruleName/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        RuleName existing = ruleNameService.findById(id);
        model.addAttribute("ruleName", existing);
        return "ruleName/update";
    }

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

    @GetMapping("/ruleName/delete/{id}")
    public String deleteRuleName(@PathVariable("id") Integer id) {
        ruleNameService.delete(id);
        return "redirect:/ruleName/list";
    }
}
