package com.nnk.springboot.services;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Find all Users
    public List<User> findAll() {
        return repository.findAll();
    }

    // Find User by ID
    public User findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found with id: " + id));
    }

    // Create a new User (encode password before saving)
    public User create(User user) {
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    // Update an existing User (re-encode password)
    public User update(Integer id, User user) {
        User existing = findById(id);
        existing.setUsername(user.getUsername());
        existing.setFullname(user.getFullname());
        existing.setRole(user.getRole());
        existing.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(existing);
    }

    // Delete a User
    public void delete(Integer id) {
        User existing = findById(id);
        repository.delete(existing);
    }
}