package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import com.nnk.springboot.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @RequestMapping("/user/list")
    public String home(Model model) {
        model.addAttribute("users", userService.findAll());
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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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

        // If the password is empty, the current (hashed) one is kept
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword(current.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
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
