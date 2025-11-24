package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @RequestMapping("/user/list")
    public String home(Model model) {
        model.addAttribute("users", userService.findAll());

        // Retrieve the logged-in user's authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Username for display
        String username = (authentication != null) ? authentication.getName() : "anonymous";
        model.addAttribute("username", username);

        // Check if the logged-in user has ADMIN role
        boolean isAdmin = authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        model.addAttribute("isAdmin", isAdmin);

        return "user/list";
    }

    @GetMapping("/user/add")
    public String addUser(Model model) {
        model.addAttribute("user", new User());
        return "user/add";
    }

    @PostMapping("/user/validate")
    public String validate(@Valid @ModelAttribute("user") User user,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            return "user/add";
        }

        userService.create(user);
        return "redirect:/user/list";
    }

    @GetMapping("/user/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        User existing = userService.findById(id);

        existing.setPassword("");
        model.addAttribute("user", existing);
        return "user/update";
    }

    @PostMapping("/user/update/{id}")
    public String updateUser(@PathVariable("id") Integer id,
                             @Valid @ModelAttribute("user") User user,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            return "user/update";
        }

        User current = userService.findById(id);

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword(current.getPassword());
        }

        user.setId(id);
        userService.update(id, user);
        return "redirect:/user/list";
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id) {
        userService.delete(id);
        return "redirect:/user/list";
    }
}