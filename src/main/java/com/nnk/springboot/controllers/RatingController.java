package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.services.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @RequestMapping("/rating/list")
    public String home(Model model) {
        model.addAttribute("ratings", ratingService.findAll());
        return "rating/list";
    }

    @GetMapping("/rating/add")
    public String addRatingForm(Model model) {
        model.addAttribute("rating", new Rating());
        return "rating/add";
    }

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

    @GetMapping("/rating/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        Rating existing = ratingService.findById(id);
        model.addAttribute("rating", existing);
        return "rating/update";
    }

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

    @GetMapping("/rating/delete/{id}")
    public String deleteRating(@PathVariable("id") Integer id) {
        ratingService.delete(id);
        return "redirect:/rating/list";
    }
}
