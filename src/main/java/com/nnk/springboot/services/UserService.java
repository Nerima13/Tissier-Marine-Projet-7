package com.nnk.springboot.services;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.regex.Pattern;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Service layer for managing {@link User} entities.
 * Provides CRUD operations and handles password encoding
 * before persisting users.
 */
@Service
public class UserService {

    private final UserRepository repository;

    private final BCryptPasswordEncoder passwordEncoder;

    private static final Pattern RAW_PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+\\-=])[A-Za-z\\d@$!%*?&#^()_+\\-=]{8,}$");

    public UserService(UserRepository repository, BCryptPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Validates the raw password before encoding and saving.
     *
     * @param rawPassword plain text password from the form
     */
    private void validateRawPassword(String rawPassword) {
        if (rawPassword == null || !RAW_PASSWORD_PATTERN.matcher(rawPassword).matches()) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and include an uppercase letter, a digit, and a special symbol.");
        }
    }

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

        // 1) Validate the raw (plain text) password
        validateRawPassword(user.getPassword());

        // 2) Encode the password before saving it
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

        // If a new password is provided, validate it and encode it
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            validateRawPassword(user.getPassword());
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        }

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