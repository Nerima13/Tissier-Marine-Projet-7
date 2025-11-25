package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.services.RuleNameService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RuleNameControllerTest {

    @Mock
    private RuleNameService ruleNameService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private RuleNameController controller;

    /**
     * Clears the SecurityContext after each test.
     */
    @AfterEach
    public void clear() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Tests that the home method returns the list view and adds rule names and user info to the model.
     */
    @Test
    public void home_returnsListView_andAddsRuleNamesToModel() {
        Model model = new ConcurrentModel();
        List<RuleName> ruleNames = List.of(new RuleName(), new RuleName());

        when(ruleNameService.findAll()).thenReturn(ruleNames);

        when(authentication.getName()).thenReturn("testUser");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER"))).when(authentication).getAuthorities();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String view = controller.home(model);

        assertEquals("ruleName/list", view);

        assertTrue(model.containsAttribute("ruleNames"));
        assertSame(ruleNames, model.getAttribute("ruleNames"));

        assertEquals("testUser", model.getAttribute("username"));
        assertEquals(false, model.getAttribute("isAdmin"));

        verify(ruleNameService).findAll();
    }

    /**
     * Tests that the add form view is returned with a new RuleName object.
     */
    @Test
    public void addRuleForm_returnsAddView_andAddsEmptyRuleName() {
        Model model = new ConcurrentModel();

        String view = controller.addRuleForm(model);

        assertEquals("ruleName/add", view);
        assertTrue(model.containsAttribute("ruleName"));
        assertNotNull(model.getAttribute("ruleName"));
        assertTrue(model.getAttribute("ruleName") instanceof RuleName);
        verifyNoInteractions(ruleNameService);
    }

    /**
     * Tests that validation errors prevent saving and return the user to the add form.
     */
    @Test
    public void validate_whenHasErrors_returnsAddView_andDoesNotCallService() {
        Model model = new ConcurrentModel();
        RuleName form = new RuleName();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.validate(form, bindingResult, model);

        assertEquals("ruleName/add", view);
        verify(bindingResult).hasErrors();
        verifyNoInteractions(ruleNameService);
    }

    /**
     * Tests that valid data triggers the service creation and redirects to the list.
     */
    @Test
    public void validate_whenNoErrors_callsServiceWithForm_andRedirectsToList() {
        Model model = new ConcurrentModel();
        RuleName form = new RuleName();
        form.setName("Name");
        form.setDescription("Description");
        form.setJson("{\"key\":\"value\"}");
        form.setTemplate("Template");
        form.setSqlStr("SELECT * FROM table");
        form.setSqlPart("WHERE id = 1");

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.validate(form, bindingResult, model);

        assertEquals("redirect:/ruleName/list", view);
        verify(bindingResult).hasErrors();

        // Verify that the RuleName passed to the service contains these values
        verify(ruleNameService).create(argThat(r ->
                r != null
                        && "Name".equals(r.getName())
                        && "Description".equals(r.getDescription())
                        && "{\"key\":\"value\"}".equals(r.getJson())
                        && "Template".equals(r.getTemplate())
                        && "SELECT * FROM table".equals(r.getSqlStr())
                        && "WHERE id = 1".equals(r.getSqlPart())));
    }

    /**
     * Tests that the update form is displayed with the existing RuleName data.
     */
    @Test
    public void showUpdateForm_returnsUpdateView_andAddsExistingRuleName() {
        Model model = new ConcurrentModel();
        RuleName existing = new RuleName();
        existing.setId(1);
        existing.setName("Existing name");
        when(ruleNameService.findById(1)).thenReturn(existing);

        String view = controller.showUpdateForm(1, model);

        assertEquals("ruleName/update", view);
        assertTrue(model.containsAttribute("ruleName"));
        assertSame(existing, model.getAttribute("ruleName"));
        verify(ruleNameService).findById(1);
    }

    /**
     * Tests that validation errors during update return the user to the update form.
     */
    @Test
    public void updateRuleName_whenHasErrors_returnsUpdateView_andDoesNotCallService() {
        Model model = new ConcurrentModel();
        RuleName form = new RuleName();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.updateRuleName(1, form, bindingResult, model);

        assertEquals("ruleName/update", view);
        verify(bindingResult).hasErrors();
        verifyNoInteractions(ruleNameService);
    }

    /**
     * Tests that a valid update triggers the service update and redirects to the list.
     */
    @Test
    public void updateRuleName_whenNoErrors_callsServiceWithForm_andRedirectsToList() {
        Model model = new ConcurrentModel();
        RuleName form = new RuleName();
        form.setName("Updated name");
        form.setDescription("Updated description");
        form.setJson("{\"updated\":\"json\"}");
        form.setTemplate("Updated template");
        form.setSqlStr("UPDATE table SET col = 1");
        form.setSqlPart("WHERE id = 2");

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.updateRuleName(5, form, bindingResult, model);

        assertEquals("redirect:/ruleName/list", view);
        verify(bindingResult).hasErrors();

        verify(ruleNameService).update(eq(5), argThat(r ->
                r != null
                        && "Updated name".equals(r.getName())
                        && "Updated description".equals(r.getDescription())
                        && "{\"updated\":\"json\"}".equals(r.getJson())
                        && "Updated template".equals(r.getTemplate())
                        && "UPDATE table SET col = 1".equals(r.getSqlStr())
                        && "WHERE id = 2".equals(r.getSqlPart())));
    }

    /**
     * Tests that deleting a rule calls the service and redirects to the list.
     */
    @Test
    public void deleteRuleName_callsService_andRedirectsToList() {
        String view = controller.deleteRuleName(3);

        assertEquals("redirect:/ruleName/list", view);
        verify(ruleNameService).delete(3);
    }
}