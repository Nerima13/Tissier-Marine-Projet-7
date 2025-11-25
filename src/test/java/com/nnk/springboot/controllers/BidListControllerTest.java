package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.services.BidListService;
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

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BidListControllerTest {

    @Mock
    private BidListService bidListService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private BidListController controller;

    /**
     * Clears the SecurityContext after each test.
     */
    @AfterEach
    public void clear() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Tests the home() method for a regular user.
     * Verifies that the list view is returned, the BidList items are added
     * to the model, the username is set, and isAdmin is false.
     */
    @Test
    public void home_returnsListView_andAddsBidListsAndUserToModel() {
        Model model = new ConcurrentModel();
        List<BidList> bidLists = List.of(new BidList(), new BidList());

        when(bidListService.findAll()).thenReturn(bidLists);

        when(authentication.getName()).thenReturn("testUser");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .when(authentication).getAuthorities();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        String view = controller.home(model);

        assertEquals("bidList/list", view);
        assertTrue(model.containsAttribute("bidLists"));
        assertSame(bidLists, model.getAttribute("bidLists"));
        assertEquals("testUser", model.getAttribute("username"));
        assertEquals(false, model.getAttribute("isAdmin"));

        verify(bidListService).findAll();
    }

    /**
     * Tests the home() method for an admin user.
     * Verifies that isAdmin is true in the model.
     */
    @Test
    public void home_whenAdmin_setsIsAdminTrue() {
        Model model = new ConcurrentModel();
        when(bidListService.findAll()).thenReturn(Collections.emptyList());

        when(authentication.getName()).thenReturn("adminUser");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(authentication).getAuthorities();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        controller.home(model);

        assertEquals(true, model.getAttribute("isAdmin"));
    }

    /**
     * Tests the addBidForm() method.
     * Verifies that the correct view is returned and an empty BidList
     * object is added to the model.
     */
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

    /**
     * Tests validate() when validation errors occur.
     * Verifies that the form view is returned and the service is not called.
     */
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

    /**
     * Tests validate() for successful validation.
     * Verifies that the service is called with the correct values and
     * the method redirects to the list view.
     */
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

        verify(bidListService).create(argThat(b ->
                b != null &&
                        "AccountTest".equals(b.getAccount()) &&
                        "TypeTest".equals(b.getType()) &&
                        Double.valueOf(10.0).equals(b.getBidQuantity())));
    }

    /**
     * Tests showUpdateForm().
     * Verifies that the correct view is returned and the existing BidList
     * object is added to the model.
     */
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
    }

    /**
     * Tests updateBid() when validation errors occur.
     * Verifies that the form view is returned and the service is not called.
     */
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

    /**
     * Tests updateBid() for successful validation.
     * Verifies that the service is called with the correct data and the
     * method redirects to the list view.
     */
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
                b != null &&
                        "UpdatedAccount".equals(b.getAccount()) &&
                        "UpdatedType".equals(b.getType()) &&
                        Double.valueOf(20.0).equals(b.getBidQuantity())));
    }

    /**
     * Tests deleteBid().
     * Verifies that the service delete method is called and the controller
     * redirects to the list view.
     */
    @Test
    public void deleteBid_callsService_andRedirectsToList() {
        String view = controller.deleteBid(3);

        assertEquals("redirect:/bidList/list", view);
        verify(bidListService).delete(3);
    }
}