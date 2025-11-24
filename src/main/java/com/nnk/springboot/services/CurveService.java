package com.nnk.springboot.services;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Service layer for managing {@link CurvePoint} entities.
 * Provides basic CRUD operations for the CurvePoint domain.
 */
@Service
public class CurveService {

    private final CurvePointRepository repository;

    public CurveService(CurvePointRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all CurvePoint entries.
     *
     * @return list of CurvePoint entities
     */
    public List<CurvePoint> findAll() {
        return repository.findAll();
    }

    /**
     * Retrieves a CurvePoint by its ID.
     *
     * @param id the CurvePoint ID
     * @return the corresponding CurvePoint entity
     * @throws ResponseStatusException if not found (404)
     */
    public CurvePoint findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "CurvePoint not found with id: " + id));
    }

    /**
     * Creates a new CurvePoint entry.
     *
     * @param curvePoint the CurvePoint to create
     * @return the saved CurvePoint entity
     */
    public CurvePoint create(CurvePoint curvePoint) {
        curvePoint.setId(null); // ensure a new entry
        return repository.save(curvePoint);
    }

    /**
     * Updates an existing CurvePoint entry.
     *
     * @param id the ID of the CurvePoint to update
     * @param curvePoint data to apply to the update
     * @return the updated CurvePoint entity
     * @throws ResponseStatusException if the CurvePoint does not exist
     */
    public CurvePoint update(Integer id, CurvePoint curvePoint) {
        CurvePoint existing = findById(id);

        existing.setCurveId(curvePoint.getCurveId());
        existing.setAsOfDate(curvePoint.getAsOfDate());
        existing.setTerm(curvePoint.getTerm());
        existing.setValue(curvePoint.getValue());
        existing.setCreationDate(curvePoint.getCreationDate());

        return repository.save(existing);
    }

    /**
     * Deletes a CurvePoint by its ID.
     *
     * @param id the ID of the CurvePoint to delete
     * @throws ResponseStatusException if the CurvePoint does not exist
     */
    public void delete(Integer id) {
        CurvePoint existing = findById(id);
        repository.delete(existing);
    }
}