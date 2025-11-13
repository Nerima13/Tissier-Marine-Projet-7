package com.nnk.springboot.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller handling the application's home pages.
 * Provides access to the public home view and redirects administrators
 * to the main BidList page.
 */
@Controller
public class HomeController {

    /**
     * Displays the public home page.
     *
     * @param model the model used to pass data to the view (unused here)
     * @return the home view
     */
    @RequestMapping("/")
    public String home(Model model) {
        return "home";
    }

    /**
     * Redirects administrators to the BidList page.
     *
     * @param model the model used to pass data to the view (unused here)
     * @return a redirect to the BidList list page
     */
    @RequestMapping("/admin/home")
    public String adminHome(Model model) {
        return "redirect:/bidList/list";
    }
}

