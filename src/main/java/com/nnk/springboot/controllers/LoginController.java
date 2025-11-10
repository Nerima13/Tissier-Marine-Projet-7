package com.nnk.springboot.controllers;

import com.nnk.springboot.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("app")
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @GetMapping("/secure/article-details")
    public ModelAndView getAllUserArticles() {
        ModelAndView mav = new ModelAndView("user/list");
        mav.addObject("users", userService.findAll());
        return mav;
    }

    @GetMapping("/error")
    public ModelAndView error() {
        ModelAndView mav = new ModelAndView("403");
        mav.addObject("errorMsg", "You are not authorized for the requested data.");
        return mav;
    }
}
