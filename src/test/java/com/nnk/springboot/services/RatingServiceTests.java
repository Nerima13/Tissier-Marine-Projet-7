package com.nnk.springboot.services;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
import com.nnk.springboot.services.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTests {

    @Mock
    private RatingRepository repository;

    @InjectMocks
    private RatingService ratingService;

    private Rating rating;

    @BeforeEach
    void setUp() {
        rating = new Rating();
        rating.setId(1);
        rating.setMoodysRating("Aaa");
        rating.setSandPRating("BBB");
        rating.setFitchRating("CCC");
        rating.setOrderNumber(10);
    }

    @Test
    void findAll_shouldReturnListOfRatings() {
        when(repository.findAll()).thenReturn(List.of(rating));

        List<Rating> result = ratingService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(rating, result.get(0));
        verify(repository).findAll();
    }

    @Test
    void findById_whenExistingId_shouldReturnRating() {
        when(repository.findById(1)).thenReturn(Optional.of(rating));

        Rating result = ratingService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(repository).findById(1);
    }

    @Test
    void findById_whenNonExistingId_shouldThrowException() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ratingService.findById(999));

        assertEquals(404, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Rating not found with id: 999"));
        verify(repository).findById(999);
    }

    @Test
    void create_shouldSetIdToNullAndSave() {
        Rating toCreate = new Rating();
        toCreate.setId(42);
        toCreate.setMoodysRating("New Moody");

        Rating saved = new Rating();
        saved.setId(1);
        saved.setMoodysRating("New Moody");

        when(repository.save(any(Rating.class))).thenReturn(saved);

        Rating result = ratingService.create(toCreate);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("New Moody", result.getMoodysRating());
        verify(repository).save(argThat(r -> r.getId() == null));
    }

    @Test
    void update_whenExistingId_shouldUpdateAndSave() {
        when(repository.findById(1)).thenReturn(Optional.of(rating));

        Rating updates = new Rating();
        updates.setMoodysRating("Updated Moody");
        updates.setSandPRating("Updated S&P");
        updates.setFitchRating("Updated Fitch");
        updates.setOrderNumber(99);

        when(repository.save(any(Rating.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rating result = ratingService.update(1, updates);

        assertEquals("Updated Moody", result.getMoodysRating());
        assertEquals("Updated S&P", result.getSandPRating());
        assertEquals("Updated Fitch", result.getFitchRating());
        assertEquals(99, result.getOrderNumber());

        verify(repository).findById(1);
        verify(repository).save(rating);
    }

    @Test
    void update_whenNonExistingId_shouldThrowException() {
        when(repository.findById(123)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ratingService.update(123, new Rating()));

        assertEquals(404, ex.getStatusCode().value());
        verify(repository).findById(123);
        verify(repository, never()).save(any());
    }

    @Test
    void delete_whenExistingId_shouldDeleteRating() {
        when(repository.findById(1)).thenReturn(Optional.of(rating));

        ratingService.delete(1);

        verify(repository).findById(1);
        verify(repository).delete(rating);
    }

    @Test
    void delete_whenNonExistingId_shouldThrowException() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ratingService.delete(999));

        assertEquals(404, ex.getStatusCode().value());
        verify(repository).findById(999);
        verify(repository, never()).delete(any());
    }
}