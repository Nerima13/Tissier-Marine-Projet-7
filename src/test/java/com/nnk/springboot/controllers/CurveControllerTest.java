package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.services.CurveService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurveControllerTest {

    @Mock
    CurveService curveService;

    @Mock
    BindingResult bindingResult;

    @InjectMocks
    CurveController controller;

    // 1) GET /curvePoint/list => returns "curvePoint/list" and adds the list to the model
    @Test
    public void home_returnsListView_andAddsCurvePointsToModel() {
        Model model = new ConcurrentModel();
        List<CurvePoint> curvePoints = List.of(new CurvePoint(), new CurvePoint());
        when(curveService.findAll()).thenReturn(curvePoints);

        String view = controller.home(model);

        assertEquals("curvePoint/list", view);
        assertTrue(model.containsAttribute("curvePoints"));
        assertSame(curvePoints, model.getAttribute("curvePoints"));
        verify(curveService).findAll();
        verifyNoMoreInteractions(curveService);
    }

    // 2) GET /curvePoint/add => returns "curvePoint/add" and adds an empty CurvePoint
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

    // 3) POST /curvePoint/validate with errors => returns "curvePoint/add" without calling the service
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

    // 4) POST /curvePoint/validate without errors => calls create() with the form and redirects
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

        // Verify that the CurvePoint passed to the service contains these values
        verify(curveService).create(argThat(c ->
                c != null
                        && Integer.valueOf(10).equals(c.getCurveId())
                        && Double.valueOf(2.5).equals(c.getTerm())
                        && Double.valueOf(100.0).equals(c.getValue())
                        && Timestamp.valueOf("2024-01-01 00:00:00").equals(c.getAsOfDate())));
        verifyNoMoreInteractions(curveService);
    }

    // 5) GET /curvePoint/update/{id} => returns "curvePoint/update" with the found CurvePoint
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
        verifyNoMoreInteractions(curveService);
    }

    // 6) POST /curvePoint/update/{id} with errors => returns "curvePoint/update" without calling update()
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

    // 7) POST /curvePoint/update/{id} without errors => calls update() with the form and redirects
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
        verifyNoMoreInteractions(curveService);
    }

    // 8) GET /curvePoint/delete/{id} => calls delete() and redirects
    @Test
    public void deleteBid_callsService_andRedirectsToList() {
        String view = controller.deleteBid(3);

        assertEquals("redirect:/curvePoint/list", view);
        verify(curveService).delete(3);
        verifyNoMoreInteractions(curveService);
    }
}