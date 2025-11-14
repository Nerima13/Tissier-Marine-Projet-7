package com.nnk.springboot.services;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
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
class RuleServiceTests {

    @Mock
    private RuleNameRepository repository;

    @InjectMocks
    private RuleNameService ruleNameService;

    private RuleName rule;

    @BeforeEach
    void setUp() {
        rule = new RuleName();
        rule.setId(1);
        rule.setName("Rule 1");
        rule.setDescription("Description 1");
        rule.setJson("JSON 1");
        rule.setTemplate("Template 1");
        rule.setSqlStr("SQL STR 1");
        rule.setSqlPart("SQL PART 1");
    }

    @Test
    void findAll_shouldReturnListOfRuleNames() {
        when(repository.findAll()).thenReturn(List.of(rule));

        List<RuleName> result = ruleNameService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(rule, result.get(0));
        verify(repository).findAll();
    }

    @Test
    void findById_whenExistingId_shouldReturnRuleName() {
        when(repository.findById(1)).thenReturn(Optional.of(rule));

        RuleName result = ruleNameService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(repository).findById(1);
    }

    @Test
    void findById_whenNonExistingId_shouldThrowException() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ruleNameService.findById(999));

        assertEquals(404, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("RuleName not found with id: 999"));
        verify(repository).findById(999);
    }

    @Test
    void create_shouldSetIdToNullAndSave() {
        RuleName toCreate = new RuleName();
        toCreate.setId(42);
        toCreate.setName("New Rule");

        RuleName saved = new RuleName();
        saved.setId(1);
        saved.setName("New Rule");

        when(repository.save(any(RuleName.class))).thenReturn(saved);

        RuleName result = ruleNameService.create(toCreate);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("New Rule", result.getName());
        verify(repository).save(argThat(r -> r.getId() == null));
    }

    @Test
    void update_whenExistingId_shouldUpdateAndSave() {
        when(repository.findById(1)).thenReturn(Optional.of(rule));

        RuleName updates = new RuleName();
        updates.setName("Updated Name");
        updates.setDescription("Updated Description");
        updates.setJson("Updated Json");
        updates.setTemplate("Updated Template");
        updates.setSqlStr("Updated SqlStr");
        updates.setSqlPart("Updated SqlPart");

        when(repository.save(any(RuleName.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RuleName result = ruleNameService.update(1, updates);

        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals("Updated Json", result.getJson());
        assertEquals("Updated Template", result.getTemplate());
        assertEquals("Updated SqlStr", result.getSqlStr());
        assertEquals("Updated SqlPart", result.getSqlPart());

        verify(repository).findById(1);
        verify(repository).save(rule);
    }

    @Test
    void update_whenNonExistingId_shouldThrowException() {
        when(repository.findById(123)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ruleNameService.update(123, new RuleName()));

        assertEquals(404, ex.getStatusCode().value());
        verify(repository).findById(123);
        verify(repository, never()).save(any());
    }

    @Test
    void delete_whenExistingId_shouldDeleteRule() {
        when(repository.findById(1)).thenReturn(Optional.of(rule));

        ruleNameService.delete(1);

        verify(repository).findById(1);
        verify(repository).delete(rule);
    }

    @Test
    void delete_whenNonExistingId_shouldThrowException() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ruleNameService.delete(999));

        assertEquals(404, ex.getStatusCode().value());
        verify(repository).findById(999);
        verify(repository, never()).delete(any());
    }
}