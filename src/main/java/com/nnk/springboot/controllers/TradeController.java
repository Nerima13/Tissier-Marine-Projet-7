package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.services.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @RequestMapping("/trade/list")
    public String home(Model model) {
        model.addAttribute("trades", tradeService.findAll());
        return "trade/list";
    }

    @GetMapping("/trade/add")
    public String addUser(Model model) {
        model.addAttribute("trade", new Trade());
        return "trade/add";
    }

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

    @GetMapping("/trade/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        Trade existing = tradeService.findById(id);
        model.addAttribute("trade", existing);
        return "trade/update";
    }

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

    @GetMapping("/trade/delete/{id}")
    public String deleteTrade(@PathVariable("id") Integer id) {
        tradeService.delete(id);
        return "redirect:/trade/list";
    }
}
