package com.nnk.springboot.controllers;

import com.nnk.springboot.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller responsible for handling login and restricted content views.
 * Provides access to the login page, a secured sample page, and an error page.
 */
@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Displays the login page.
     *
     * @return the login view
     */
    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    /**
     * Displays a secured page containing a list of users.
     *
     * @return the view with user data
     */
    @GetMapping("/secure/article-details")
    public ModelAndView getAllUserArticles() {
        ModelAndView mav = new ModelAndView("user/list");
        mav.addObject("users", userService.findAll());
        return mav;
    }

    /**
     * Displays a custom 403 error page when access is denied.
     *
     * @return the error view
     */
    @GetMapping("/error")
    public ModelAndView error() {
        ModelAndView mav = new ModelAndView("403");
        mav.addObject("errorMsg", "You are not authorized for the requested data.");
        return mav;
    }
}
