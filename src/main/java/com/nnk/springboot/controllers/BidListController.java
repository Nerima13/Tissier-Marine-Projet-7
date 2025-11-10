package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.services.BidListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class BidListController {

    private final BidListService bidListService;

    @RequestMapping("/bidList/list")
    public String home(Model model) {
        model.addAttribute("bidLists", bidListService.findAll());
        return "bidList/list";
    }

    @GetMapping("/bidList/add")
    public String addBidForm(Model model) {
        model.addAttribute("bidList", new BidList());
        return "bidList/add";
    }

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

    @GetMapping("/bidList/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        BidList existing = bidListService.findById(id);
        model.addAttribute("bidList", existing);
        return "bidList/update";
    }

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

    @GetMapping("/bidList/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id) {
        bidListService.delete(id);
        return "redirect:/bidList/list";
    }
}
