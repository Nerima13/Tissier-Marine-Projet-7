package com.nnk.springboot.services;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Service layer for managing {@link RuleName} entities.
 * Provides CRUD operations for the RuleName domain.
 */
@Service
@RequiredArgsConstructor
public class RuleNameService {

    private final RuleNameRepository repository;

    /**
     * Retrieves all RuleName entries.
     *
     * @return list of RuleName entities
     */
    public List<RuleName> findAll() {
        return repository.findAll();
    }

    /**
     * Retrieves a RuleName by its ID.
     *
     * @param id the RuleName ID
     * @return the corresponding RuleName entity
     * @throws ResponseStatusException if not found (404)
     */
    public RuleName findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "RuleName not found with id: " + id));
    }

    /**
     * Creates a new RuleName entry.
     *
     * @param ruleName the RuleName to create
     * @return the saved RuleName entity
     */
    public RuleName create(RuleName ruleName) {
        ruleName.setId(null); // ensure creation of a new entry
        return repository.save(ruleName);
    }

    /**
     * Updates an existing RuleName entry.
     *
     * @param id the ID of the RuleName to update
     * @param ruleName the updated RuleName data
     * @return the updated RuleName entity
     * @throws ResponseStatusException if the RuleName does not exist
     */
    public RuleName update(Integer id, RuleName ruleName) {
        RuleName existing = findById(id);

        existing.setName(ruleName.getName());
        existing.setDescription(ruleName.getDescription());
        existing.setJson(ruleName.getJson());
        existing.setTemplate(ruleName.getTemplate());
        existing.setSqlStr(ruleName.getSqlStr());
        existing.setSqlPart(ruleName.getSqlPart());

        return repository.save(existing);
    }

    /**
     * Deletes a RuleName by its ID.
     *
     * @param id the ID of the RuleName to delete
     * @throws ResponseStatusException if the RuleName does not exist
     */
    public void delete(Integer id) {
        RuleName existing = findById(id);
        repository.delete(existing);
    }
}
