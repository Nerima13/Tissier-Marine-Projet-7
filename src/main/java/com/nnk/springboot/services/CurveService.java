package com.nnk.springboot.services;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CurveService {

    private final CurvePointRepository repository;

    // Retrieve all curve points
    public List<CurvePoint> findAll() {
        return repository.findAll();
    }

    // Retrieve a curve point by its ID
    public CurvePoint findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "CurvePoint not found with id: " + id));
    }

    // Create a new curve point
    public CurvePoint create(CurvePoint curvePoint) {
        curvePoint.setId(null);  // Safety check : ensure we are creating a new entry
        return repository.save(curvePoint);
    }

    // Update an existing curve point
    public CurvePoint update(Integer id, CurvePoint curvePoint) {
        CurvePoint existing = findById(id);
        existing.setCurveId(curvePoint.getCurveId());
        existing.setAsOfDate(curvePoint.getAsOfDate());
        existing.setTerm(curvePoint.getTerm());
        existing.setValue(curvePoint.getValue());
        existing.setCreationDate(curvePoint.getCreationDate());
        return repository.save(existing);
    }

    // Delete a curve point
    public void delete(Integer id) {
        CurvePoint existing = findById(id);
        repository.delete(existing);
    }
}