package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.services.TradeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TradeControllerTest {

    @Mock
    TradeService tradeService;

    @Mock
    BindingResult bindingResult;

    @InjectMocks
    TradeController controller;

    // 1) GET /trade/list => returns "trade/list" and adds the list to the model
    @Test
    public void home_returnsListView_andAddsTradesToModel() {
        Model model = new ConcurrentModel();
        List<Trade> trades = List.of(new Trade(), new Trade());
        when(tradeService.findAll()).thenReturn(trades);

        String view = controller.home(model);

        assertEquals("trade/list", view);
        assertTrue(model.containsAttribute("trades"));
        assertSame(trades, model.getAttribute("trades"));
        verify(tradeService).findAll();
        verifyNoMoreInteractions(tradeService);
    }

    // 2) GET /trade/add => returns "trade/add" and adds an empty Trade
    @Test
    public void addUser_returnsAddView_andAddsEmptyTrade() {
        Model model = new ConcurrentModel();

        String view = controller.addUser(model);

        assertEquals("trade/add", view);
        assertTrue(model.containsAttribute("trade"));
        assertNotNull(model.getAttribute("trade"));
        assertTrue(model.getAttribute("trade") instanceof Trade);
        verifyNoInteractions(tradeService);
    }

    // 3) POST /trade/validate with errors => returns "trade/add" without calling the service
    @Test
    public void validate_whenHasErrors_returnsAddView_andDoesNotCallService() {
        Model model = new ConcurrentModel();
        Trade form = new Trade();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.validate(form, bindingResult, model);

        assertEquals("trade/add", view);
        verify(bindingResult).hasErrors();
        verifyNoInteractions(tradeService);
    }

    // 4) POST /trade/validate without errors => calls create() with the form and redirects
    @Test
    public void validate_whenNoErrors_callsServiceWithForm_andRedirectsToList() {
        Model model = new ConcurrentModel();
        Trade form = new Trade();
        form.setAccount("AccountTest");
        form.setType("TypeTest");
        form.setBuyQuantity(10.0);
        form.setSellQuantity(5.0);
        form.setBuyPrice(100.0);
        form.setSellPrice(101.0);
        form.setTradeDate(LocalDateTime.of(2024, 1, 1, 0, 0));
        form.setSecurity("Security");
        form.setStatus("Status");
        form.setTrader("Trader");
        form.setBenchmark("Benchmark");
        form.setBook("Book");
        form.setCreationName("Creator");
        form.setRevisionName("Revisor");
        form.setDealName("Deal");
        form.setDealType("DealType");
        form.setSourceListId("Source");
        form.setSide("Side");

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.validate(form, bindingResult, model);

        assertEquals("redirect:/trade/list", view);
        verify(bindingResult).hasErrors();

        // Verify that the Trade passed to the service contains these values
        verify(tradeService).create(argThat(t ->
                t != null
                        && "AccountTest".equals(t.getAccount())
                        && "TypeTest".equals(t.getType())
                        && Double.valueOf(10.0).equals(t.getBuyQuantity())
                        && Double.valueOf(5.0).equals(t.getSellQuantity())
                        && Double.valueOf(100.0).equals(t.getBuyPrice())
                        && Double.valueOf(101.0).equals(t.getSellPrice())
                        && LocalDateTime.of(2024, 1, 1, 0, 0).equals(t.getTradeDate())
                        && "Security".equals(t.getSecurity())
                        && "Status".equals(t.getStatus())
                        && "Trader".equals(t.getTrader())
                        && "Benchmark".equals(t.getBenchmark())
                        && "Book".equals(t.getBook())
                        && "Creator".equals(t.getCreationName())
                        && "Revisor".equals(t.getRevisionName())
                        && "Deal".equals(t.getDealName())
                        && "DealType".equals(t.getDealType())
                        && "Source".equals(t.getSourceListId())
                        && "Side".equals(t.getSide())));
        verifyNoMoreInteractions(tradeService);
    }

    // 5) GET /trade/update/{id} => returns "trade/update" with the found Trade
    @Test
    public void showUpdateForm_returnsUpdateView_andAddsExistingTrade() {
        Model model = new ConcurrentModel();
        Trade existing = new Trade();
        existing.setTradeId(1);
        existing.setAccount("ExistingAccount");
        when(tradeService.findById(1)).thenReturn(existing);

        String view = controller.showUpdateForm(1, model);

        assertEquals("trade/update", view);
        assertTrue(model.containsAttribute("trade"));
        assertSame(existing, model.getAttribute("trade"));
        verify(tradeService).findById(1);
        verifyNoMoreInteractions(tradeService);
    }

    // 6) POST /trade/update/{id} with errors => returns "trade/update" without calling update()
    @Test
    public void updateTrade_whenHasErrors_returnsUpdateView_andDoesNotCallService() {
        Model model = new ConcurrentModel();
        Trade form = new Trade();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.updateTrade(1, form, bindingResult, model);

        assertEquals("trade/update", view);
        verify(bindingResult).hasErrors();
        verifyNoInteractions(tradeService);
    }

    // 7) POST /trade/update/{id} without errors => calls update() with the form and redirects
    @Test
    public void updateTrade_whenNoErrors_callsServiceWithForm_andRedirectsToList() {
        Model model = new ConcurrentModel();
        Trade form = new Trade();
        form.setAccount("UpdatedAccount");
        form.setType("UpdatedType");
        form.setBuyQuantity(20.0);
        form.setSellQuantity(10.0);
        form.setBuyPrice(200.0);
        form.setSellPrice(201.0);
        form.setTradeDate(LocalDateTime.of(2025, 1, 1, 0, 0));
        form.setSecurity("UpdatedSecurity");
        form.setStatus("UpdatedStatus");
        form.setTrader("UpdatedTrader");
        form.setBenchmark("UpdatedBenchmark");
        form.setBook("UpdatedBook");
        form.setCreationName("UpdatedCreator");
        form.setRevisionName("UpdatedRevisor");
        form.setDealName("UpdatedDeal");
        form.setDealType("UpdatedType");
        form.setSourceListId("UpdatedSource");
        form.setSide("UpdatedSide");

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.updateTrade(5, form, bindingResult, model);

        assertEquals("redirect:/trade/list", view);
        verify(bindingResult).hasErrors();

        verify(tradeService).update(eq(5), argThat(t ->
                t != null
                        && "UpdatedAccount".equals(t.getAccount())
                        && "UpdatedType".equals(t.getType())
                        && Double.valueOf(20.0).equals(t.getBuyQuantity())
                        && Double.valueOf(10.0).equals(t.getSellQuantity())
                        && Double.valueOf(200.0).equals(t.getBuyPrice())
                        && Double.valueOf(201.0).equals(t.getSellPrice())
                        && LocalDateTime.of(2025, 1, 1, 0, 0).equals(t.getTradeDate())
                        && "UpdatedSecurity".equals(t.getSecurity())
                        && "UpdatedStatus".equals(t.getStatus())
                        && "UpdatedTrader".equals(t.getTrader())
                        && "UpdatedBenchmark".equals(t.getBenchmark())
                        && "UpdatedBook".equals(t.getBook())
                        && "UpdatedCreator".equals(t.getCreationName())
                        && "UpdatedRevisor".equals(t.getRevisionName())
                        && "UpdatedDeal".equals(t.getDealName())
                        && "UpdatedType".equals(t.getDealType())
                        && "UpdatedSource".equals(t.getSourceListId())
                        && "UpdatedSide".equals(t.getSide())));
        verifyNoMoreInteractions(tradeService);
    }

    // 8) GET /trade/delete/{id} => calls delete() and redirects
    @Test
    public void deleteTrade_callsService_andRedirectsToList() {
        String view = controller.deleteTrade(3);

        assertEquals("redirect:/trade/list", view);
        verify(tradeService).delete(3);
        verifyNoMoreInteractions(tradeService);
    }
}