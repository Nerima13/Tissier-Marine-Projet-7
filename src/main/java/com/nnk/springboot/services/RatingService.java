package com.nnk.springboot.services;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Service layer for managing {@link Rating} entities.
 * Provides CRUD operations and centralizes business logic
 * related to the Rating domain.
 */
@Service
public class RatingService {

    private final RatingRepository repository;

    public RatingService(RatingRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all Rating entries.
     *
     * @return list of Rating entities
     */
    public List<Rating> findAll() {
        return repository.findAll();
    }

    /**
     * Retrieves a Rating by its ID.
     *
     * @param id the Rating ID
     * @return the corresponding Rating entity
     * @throws ResponseStatusException if not found (404)
     */
    public Rating findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "Rating not found with id: " + id));
    }

    /**
     * Creates a new Rating entry.
     *
     * @param rating the Rating to create
     * @return the saved Rating entity
     */
    public Rating create(Rating rating) {
        rating.setId(null); // ensure creation of a new entry
        return repository.save(rating);
    }

    /**
     * Updates an existing Rating entry.
     *
     * @param id the ID of the Rating to update
     * @param rating the updated Rating data
     * @return the updated Rating entity
     * @throws ResponseStatusException if the Rating does not exist
     */
    public Rating update(Integer id, Rating rating) {
        Rating existing = findById(id);

        existing.setMoodysRating(rating.getMoodysRating());
        existing.setSandPRating(rating.getSandPRating());
        existing.setFitchRating(rating.getFitchRating());
        existing.setOrderNumber(rating.getOrderNumber());

        return repository.save(existing);
    }

    /**
     * Deletes a Rating by its ID.
     *
     * @param id the ID of the Rating to delete
     * @throws ResponseStatusException if the Rating does not exist
     */
    public void delete(Integer id) {
        Rating existing = findById(id);
        repository.delete(existing);
    }
}