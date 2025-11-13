package com.nnk.springboot.services;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Service layer for managing {@link User} entities.
 * Provides CRUD operations and handles password encoding
 * before persisting users.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Retrieves all User entries.
     *
     * @return list of User entities
     */
    public List<User> findAll() {
        return repository.findAll();
    }

    /**
     * Retrieves a User by its ID.
     *
     * @param id the User ID
     * @return the corresponding User entity
     * @throws ResponseStatusException if not found (404)
     */
    public User findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "User not found with id: " + id));
    }

    /**
     * Creates a new User and encodes the password before saving.
     *
     * @param user the User to create
     * @return the saved User entity
     */
    public User create(User user) {
        user.setId(null); // ensure creation
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    /**
     * Updates an existing User and re-encodes the password.
     *
     * @param id the ID of the User to update
     * @param user the updated User data
     * @return the updated User entity
     * @throws ResponseStatusException if the User does not exist
     */
    public User update(Integer id, User user) {
        User existing = findById(id);

        existing.setUsername(user.getUsername());
        existing.setFullname(user.getFullname());
        existing.setRole(user.getRole());
        existing.setPassword(passwordEncoder.encode(user.getPassword()));

        return repository.save(existing);
    }

    /**
     * Deletes a User by its ID.
     *
     * @param id the ID of the User to delete
     * @throws ResponseStatusException if the User does not exist
     */
    public void delete(Integer id) {
        User existing = findById(id);
        repository.delete(existing);
    }
}