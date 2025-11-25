package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.services.CurveService;
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

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurveControllerTest {

    @Mock
    private CurveService curveService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private CurveController controller;

    /**
     * Clears the SecurityContext after each test.
     */
    @AfterEach
    public void clear() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Tests home() for GET /curvePoint/list with a regular user.
     * Verifies that the correct view is returned, the curvePoints list is added to the model,
     * and the username and isAdmin flag are correctly set.
     */
    @Test
    public void home_returnsListView_andAddsCurvePointsToModel() {
        Model model = new ConcurrentModel();
        List<CurvePoint> curvePoints = List.of(new CurvePoint(), new CurvePoint());

        when(curveService.findAll()).thenReturn(curvePoints);

        when(authentication.getName()).thenReturn("testUser");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .when(authentication).getAuthorities();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String view = controller.home(model);

        assertEquals("curvePoint/list", view);

        assertTrue(model.containsAttribute("curvePoints"));
        assertSame(curvePoints, model.getAttribute("curvePoints"));

        assertEquals("testUser", model.getAttribute("username"));
        assertEquals(false, model.getAttribute("isAdmin"));

        verify(curveService).findAll();
    }

    /**
     * Tests addBidForm() for GET /curvePoint/add.
     * Verifies that the correct view is returned and an empty CurvePoint is added to the model.
     */
    @Test
    public void addBidForm_returnsAddView_andAddsEmptyCurvePoint() {
        Model model = new ConcurrentModel();

        String view = controller.addBidForm(model);

        assertEquals("curvePoint/add", view);
        assertTrue(model.containsAttribute("curvePoint"));
        assertNotNull(model.getAttribute("curvePoint"));
        assertTrue(model.getAttribute("curvePoint") instanceof CurvePoint);
        verifyNoInteractions(curveService);
    }

    /**
     * Tests validate() for POST /curvePoint/validate when there are validation errors.
     * Verifies that the add view is returned and the service is not called.
     */
    @Test
    public void validate_whenHasErrors_returnsAddView_andDoesNotCallService() {
        Model model = new ConcurrentModel();
        CurvePoint form = new CurvePoint();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.validate(form, bindingResult, model);

        assertEquals("curvePoint/add", view);
        verify(bindingResult).hasErrors();
        verifyNoInteractions(curveService);
    }

    /**
     * Tests validate() for POST /curvePoint/validate with valid data.
     * Verifies that the service create() method is called with the correct values
     * and that the method redirects to the list view.
     */
    @Test
    public void validate_whenNoErrors_callsServiceWithForm_andRedirectsToList() {
        Model model = new ConcurrentModel();
        CurvePoint form = new CurvePoint();
        form.setCurveId(10);
        form.setTerm(2.5);
        form.setValue(100.0);
        form.setAsOfDate(Timestamp.valueOf("2024-01-01 00:00:00"));

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.validate(form, bindingResult, model);

        assertEquals("redirect:/curvePoint/list", view);
        verify(bindingResult).hasErrors();

        verify(curveService).create(argThat(c ->
                c != null
                        && Integer.valueOf(10).equals(c.getCurveId())
                        && Double.valueOf(2.5).equals(c.getTerm())
                        && Double.valueOf(100.0).equals(c.getValue())
                        && Timestamp.valueOf("2024-01-01 00:00:00").equals(c.getAsOfDate())));
    }

    /**
     * Tests showUpdateForm() for GET /curvePoint/update/{id}.
     * Verifies that the existing CurvePoint is added to the model
     * and the update view is returned.
     */
    @Test
    public void showUpdateForm_returnsUpdateView_andAddsExistingCurvePoint() {
        Model model = new ConcurrentModel();
        CurvePoint existing = new CurvePoint();
        existing.setId(1);
        existing.setCurveId(20);

        when(curveService.findById(1)).thenReturn(existing);

        String view = controller.showUpdateForm(1, model);

        assertEquals("curvePoint/update", view);
        assertTrue(model.containsAttribute("curvePoint"));
        assertSame(existing, model.getAttribute("curvePoint"));
        verify(curveService).findById(1);
    }

    /**
     * Tests updateBid() for POST /curvePoint/update/{id} when there are validation errors.
     * Verifies that the update view is returned and the service is not called.
     */
    @Test
    public void updateBid_whenHasErrors_returnsUpdateView_andDoesNotCallService() {
        Model model = new ConcurrentModel();
        CurvePoint form = new CurvePoint();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.updateBid(1, form, bindingResult, model);

        assertEquals("curvePoint/update", view);
        verify(bindingResult).hasErrors();
        verifyNoInteractions(curveService);
    }

    /**
     * Tests updateBid() for POST /curvePoint/update/{id} with valid data.
     * Verifies that the service update() method is called with the correct values
     * and that the method redirects to the list view.
     */
    @Test
    public void updateBid_whenNoErrors_callsServiceWithForm_andRedirectsToList() {
        Model model = new ConcurrentModel();
        CurvePoint form = new CurvePoint();
        form.setCurveId(30);
        form.setTerm(5.0);
        form.setValue(200.0);

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.updateBid(5, form, bindingResult, model);

        assertEquals("redirect:/curvePoint/list", view);
        verify(bindingResult).hasErrors();

        verify(curveService).update(eq(5), argThat(c ->
                c != null
                        && Integer.valueOf(30).equals(c.getCurveId())
                        && Double.valueOf(5.0).equals(c.getTerm())
                        && Double.valueOf(200.0).equals(c.getValue())));
    }

    /**
     * Tests deleteBid() for GET /curvePoint/delete/{id}.
     * Verifies that the service delete() method is called with the correct ID
     * and that the method redirects to the list view.
     */
    @Test
    public void deleteBid_callsService_andRedirectsToList() {
        String view = controller.deleteBid(3);

        assertEquals("redirect:/curvePoint/list", view);
        verify(curveService).delete(3);
    }
}