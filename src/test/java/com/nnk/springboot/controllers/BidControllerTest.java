package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.services.BidListService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BidControllerTest {

    @Mock
    BidListService bidListService;

    @Mock
    BindingResult bindingResult;

    @InjectMocks
    BidListController controller;

    // 1) GET /bidList/list => returns "bidList/list" and adds the list to the model
    @Test
    public void home_returnsListView_andAddsBidListsToModel() {
        Model model = new ConcurrentModel();
        List<BidList> bidLists = List.of(new BidList(), new BidList());
        when(bidListService.findAll()).thenReturn(bidLists);

        String view = controller.home(model);

        assertEquals("bidList/list", view);
        assertTrue(model.containsAttribute("bidLists"));
        assertSame(bidLists, model.getAttribute("bidLists"));
        verify(bidListService).findAll();
        verifyNoMoreInteractions(bidListService);
    }

    // 2) GET /bidList/add => returns "bidList/add" and adds an empty BidList
    @Test
    public void addBidForm_returnsAddView_andAddsEmptyBidList() {
        Model model = new ConcurrentModel();

        String view = controller.addBidForm(model);

        assertEquals("bidList/add", view);
        assertTrue(model.containsAttribute("bidList"));
        assertNotNull(model.getAttribute("bidList"));
        assertTrue(model.getAttribute("bidList") instanceof BidList);
        verifyNoInteractions(bidListService);
    }

    // 3) POST /bidList/validate with errors => returns "bidList/add" without calling the service
    @Test
    public void validate_whenHasErrors_returnsAddView_andDoesNotCallService() {
        Model model = new ConcurrentModel();
        BidList form = new BidList();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.validate(form, bindingResult, model);

        assertEquals("bidList/add", view);
        verify(bindingResult).hasErrors();
        verifyNoInteractions(bidListService);
    }

    // 4) POST /bidList/validate without errors => calls create() with the correct fields and redirects
    @Test
    public void validate_whenNoErrors_callsServiceWithForm_andRedirectsToList() {
        Model model = new ConcurrentModel();
        BidList form = new BidList();
        form.setAccount("AccountTest");
        form.setType("TypeTest");
        form.setBidQuantity(10.0);

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.validate(form, bindingResult, model);

        assertEquals("redirect:/bidList/list", view);
        verify(bindingResult).hasErrors();

        // Verify that the BidList passed to the service contains these values
        verify(bidListService).create(argThat(b ->
                b != null
                        && "AccountTest".equals(b.getAccount())
                        && "TypeTest".equals(b.getType())
                        && Double.valueOf(10.0).equals(b.getBidQuantity())));
        verifyNoMoreInteractions(bidListService);
    }

    // 5) GET /bidList/update/{id} => returns "bidList/update" with the found BidList
    @Test
    public void showUpdateForm_returnsUpdateView_andAddsExistingBidList() {
        Model model = new ConcurrentModel();
        BidList existing = new BidList();
        existing.setId(1);
        existing.setAccount("ExistingAccount");
        when(bidListService.findById(1)).thenReturn(existing);

        String view = controller.showUpdateForm(1, model);

        assertEquals("bidList/update", view);
        assertTrue(model.containsAttribute("bidList"));
        assertSame(existing, model.getAttribute("bidList"));
        verify(bidListService).findById(1);
        verifyNoMoreInteractions(bidListService);
    }

    // 6) POST /bidList/update/{id} with errors => returns "bidList/update" without calling update()
    @Test
    public void updateBid_whenHasErrors_returnsUpdateView_andDoesNotCallService() {
        Model model = new ConcurrentModel();
        BidList form = new BidList();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.updateBid(1, form, bindingResult, model);

        assertEquals("bidList/update", view);
        verify(bindingResult).hasErrors();
        verifyNoInteractions(bidListService);
    }

    // 7) POST /bidList/update/{id} without errors => calls update() with the correct fields and redirects
    @Test
    public void updateBid_whenNoErrors_callsServiceWithForm_andRedirectsToList() {
        Model model = new ConcurrentModel();
        BidList form = new BidList();
        form.setAccount("UpdatedAccount");
        form.setType("UpdatedType");
        form.setBidQuantity(20.0);

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.updateBid(5, form, bindingResult, model);

        assertEquals("redirect:/bidList/list", view);
        verify(bindingResult).hasErrors();

        verify(bidListService).update(eq(5), argThat(b ->
                b != null
                        && "UpdatedAccount".equals(b.getAccount())
                        && "UpdatedType".equals(b.getType())
                        && Double.valueOf(20.0).equals(b.getBidQuantity())));
        verifyNoMoreInteractions(bidListService);
    }

    // 8) GET /bidList/delete/{id} => calls delete() and redirects
    @Test
    public void deleteBid_callsService_andRedirectsToList() {
        String view = controller.deleteBid(3);

        assertEquals("redirect:/bidList/list", view);
        verify(bidListService).delete(3);
        verifyNoMoreInteractions(bidListService);
    }
}