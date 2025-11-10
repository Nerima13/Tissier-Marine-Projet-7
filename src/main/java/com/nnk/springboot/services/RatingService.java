package com.nnk.springboot.services;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository repository;

    // Retrieve all ratings
    public List<Rating> findAll() {
        return repository.findAll();
    }

    // Retrieve a rating by its ID
    public Rating findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rating not found with id: " + id));
    }

    // Create a new rating
    public Rating create(Rating rating) {
        rating.setId(null); // ensure creation
        return repository.save(rating);
    }

    // Update an existing rating
    public Rating update(Integer id, Rating rating) {
        Rating existing = findById(id);
        existing.setMoodysRating(rating.getMoodysRating());
        existing.setSandPRating(rating.getSandPRating());
        existing.setFitchRating(rating.getFitchRating());
        existing.setOrderNumber(rating.getOrderNumber());
        return repository.save(existing);
    }

    // Delete a rating by its ID
    public void delete(Integer id) {
        Rating existing = findById(id);
        repository.delete(existing);
    }
}