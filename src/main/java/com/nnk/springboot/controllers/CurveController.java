package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.services.CurveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
public class CurveController {

    private final CurveService curveService;

    @RequestMapping("/curvePoint/list")
    public String home(Model model) {
        model.addAttribute("curvePoints", curveService.findAll());
        return "curvePoint/list";
    }

    @GetMapping("/curvePoint/add")
    public String addBidForm(Model model) {
        model.addAttribute("curvePoint", new CurvePoint());
        return "curvePoint/add";
    }

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

    @GetMapping("/curvePoint/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        CurvePoint existing = curveService.findById(id);
        model.addAttribute("curvePoint", existing);
        return "curvePoint/update";
    }

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

    @GetMapping("/curvePoint/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id) {
        curveService.delete(id);
        return "redirect:/curvePoint/list";
    }
}
