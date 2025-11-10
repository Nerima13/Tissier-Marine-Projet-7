package com.nnk.springboot.services;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RuleNameService {

    private final RuleNameRepository repository;

    // Retrieve all rule names
    public List<RuleName> findAll() {
        return repository.findAll();
    }

    // Retrieve a rule name by its ID
    public RuleName findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "RuleName not found with id: " + id));
    }

    // Create a new rule name
    public RuleName create(RuleName ruleName) {
        ruleName.setId(null); // ensure creation
        return repository.save(ruleName);
    }

    // Update an existing rule name
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

    // Delete a rule name
    public void delete(Integer id) {
        RuleName existing = findById(id);
        repository.delete(existing);
    }
}