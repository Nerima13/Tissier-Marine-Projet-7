package com.nnk.springboot.services;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurveServiceTests {

    @Mock
    private CurvePointRepository repository;

    @InjectMocks
    private CurveService curveService;

    private CurvePoint curve;

    @BeforeEach
    void setUp() {
        curve = new CurvePoint();
        curve.setId(1);
        curve.setCurveId(10);
        curve.setAsOfDate(Timestamp.from(Instant.now()));
        curve.setTerm(2.5);
        curve.setValue(30.5);
        curve.setCreationDate(Timestamp.from(Instant.now()));
    }

    @Test
    void findAll_shouldReturnListOfCurvePoints() {
        when(repository.findAll()).thenReturn(List.of(curve));

        List<CurvePoint> result = curveService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(curve, result.get(0));
        verify(repository).findAll();
    }

    @Test
    void findById_whenExistingId_shouldReturnCurvePoint() {
        when(repository.findById(1)).thenReturn(Optional.of(curve));

        CurvePoint result = curveService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(repository).findById(1);
    }

    @Test
    void findById_whenNonExistingId_shouldThrowException() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> curveService.findById(999));

        assertEquals(404, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("CurvePoint not found with id: 999"));
        verify(repository).findById(999);
    }

    @Test
    void create_shouldSetIdToNullAndSave() {
        CurvePoint toCreate = new CurvePoint();
        toCreate.setId(42); // should be reset
        toCreate.setCurveId(99);

        CurvePoint saved = new CurvePoint();
        saved.setId(1);
        saved.setCurveId(99);

        when(repository.save(any(CurvePoint.class))).thenReturn(saved);

        CurvePoint result = curveService.create(toCreate);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(99, result.getCurveId());
        verify(repository).save(argThat(c -> c.getId() == null));
    }

    @Test
    void update_whenExistingId_shouldUpdateAndSave() {
        when(repository.findById(1)).thenReturn(Optional.of(curve));

        CurvePoint updates = new CurvePoint();
        updates.setCurveId(50);
        updates.setAsOfDate(Timestamp.from(Instant.now()));
        updates.setTerm(99.9);
        updates.setValue(88.8);
        updates.setCreationDate(Timestamp.from(Instant.now()));

        when(repository.save(any(CurvePoint.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CurvePoint result = curveService.update(1, updates);

        assertEquals(50, result.getCurveId());
        assertEquals(updates.getAsOfDate(), result.getAsOfDate());
        assertEquals(99.9, result.getTerm());
        assertEquals(88.8, result.getValue());
        assertEquals(updates.getCreationDate(), result.getCreationDate());

        verify(repository).findById(1);
        verify(repository).save(curve);
    }

    @Test
    void update_whenNonExistingId_shouldThrowException() {
        when(repository.findById(123)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> curveService.update(123, new CurvePoint()));

        assertEquals(404, ex.getStatusCode().value());
        verify(repository).findById(123);
        verify(repository, never()).save(any());
    }

    @Test
    void delete_whenExistingId_shouldDeleteCurvePoint() {
        when(repository.findById(1)).thenReturn(Optional.of(curve));

        curveService.delete(1);

        verify(repository).findById(1);
        verify(repository).delete(curve);
    }

    @Test
    void delete_whenNonExistingId_shouldThrowException() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> curveService.delete(999));

        assertEquals(404, ex.getStatusCode().value());
        verify(repository).findById(999);
        verify(repository, never()).delete(any());
    }
}