package com.nnk.springboot.services;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceTests {

    @Mock
    private TradeRepository repository;

    @InjectMocks
    private TradeService tradeService;

    private Trade trade;

    @BeforeEach
    void setUp() {
        trade = new Trade();
        trade.setId(1);
        trade.setAccount("Account");
        trade.setType("Type");
        trade.setBuyQuantity(10d);
        trade.setSellQuantity(5d);
        trade.setBuyPrice(100d);
        trade.setSellPrice(101d);
        trade.setTradeDate(LocalDateTime.now());
        trade.setSecurity("Security");
        trade.setStatus("Status");
        trade.setTrader("Trader");
        trade.setBenchmark("Benchmark");
        trade.setBook("Book");
        trade.setCreationName("Creator");
        trade.setCreationDate(LocalDateTime.now());
        trade.setRevisionName("Revisor");
        trade.setRevisionDate(LocalDateTime.now());
        trade.setDealName("Deal");
        trade.setDealType("Type");
        trade.setSourceListId("Source");
        trade.setSide("Side");
    }

    @Test
    void findAll_shouldReturnListOfTrades() {
        when(repository.findAll()).thenReturn(List.of(trade));

        List<Trade> result = tradeService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(trade, result.get(0));
        verify(repository).findAll();
    }

    @Test
    void findById_whenExisting_shouldReturnTrade() {
        when(repository.findById(1)).thenReturn(Optional.of(trade));

        Trade result = tradeService.findById(1);

        assertEquals(1, result.getId());
        verify(repository).findById(1);
    }

    @Test
    void findById_whenNotExisting_shouldThrow() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> tradeService.findById(999));

        assertEquals(404, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Trade not found with id: 999"));
        verify(repository).findById(999);
    }

    @Test
    void create_shouldResetIdAndTimestampsAndSave() {
        Trade toCreate = new Trade();
        toCreate.setId(42);
        toCreate.setAccount("New Account");
        toCreate.setCreationDate(LocalDateTime.now());
        toCreate.setRevisionDate(LocalDateTime.now());

        Trade saved = new Trade();
        saved.setId(1);
        saved.setAccount("New Account");

        when(repository.save(any(Trade.class))).thenReturn(saved);

        Trade result = tradeService.create(toCreate);

        assertEquals(1, result.getId());
        assertEquals("New Account", result.getAccount());

        verify(repository).save(argThat(t ->
                t.getId() == null &&
                        t.getCreationDate() == null &&
                        t.getRevisionDate() == null));
    }

    @Test
    void update_whenExisting_shouldUpdateOnlyMutableFields() {
        when(repository.findById(1)).thenReturn(Optional.of(trade));

        LocalDateTime originalCreationDate = trade.getCreationDate();
        LocalDateTime originalRevisionDate = trade.getRevisionDate();

        Trade updates = new Trade();
        updates.setAccount("Updated Account");
        updates.setType("Updated Type");
        updates.setBuyQuantity(20d);
        updates.setSellQuantity(10d);
        updates.setBuyPrice(200d);
        updates.setSellPrice(201d);
        LocalDateTime newTradeDate = LocalDateTime.of(2025, 5, 1, 12, 0);
        updates.setTradeDate(newTradeDate);
        updates.setSecurity("New Security");
        updates.setStatus("New Status");
        updates.setTrader("New Trader");
        updates.setBenchmark("New Benchmark");
        updates.setBook("New Book");
        updates.setCreationName("New Creator");
        updates.setCreationDate(LocalDateTime.of(2030, 1, 1, 0, 0));
        updates.setRevisionName("New Revisor");
        updates.setRevisionDate(LocalDateTime.of(2030, 1, 2, 0, 0));
        updates.setDealName("New Deal");
        updates.setDealType("New Type");
        updates.setSourceListId("New Source");
        updates.setSide("New Side");

        when(repository.save(any(Trade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trade result = tradeService.update(1, updates);

        assertEquals("Updated Account", result.getAccount());
        assertEquals("Updated Type", result.getType());
        assertEquals(20d, result.getBuyQuantity());
        assertEquals(10d, result.getSellQuantity());
        assertEquals(200d, result.getBuyPrice());
        assertEquals(201d, result.getSellPrice());
        assertEquals(newTradeDate, result.getTradeDate());
        assertEquals("New Security", result.getSecurity());
        assertEquals("New Status", result.getStatus());
        assertEquals("New Trader", result.getTrader());
        assertEquals("New Benchmark", result.getBenchmark());
        assertEquals("New Book", result.getBook());
        assertEquals("New Creator", result.getCreationName());
        assertEquals(originalCreationDate, result.getCreationDate());
        assertEquals("New Revisor", result.getRevisionName());
        assertEquals(originalRevisionDate, result.getRevisionDate());
        assertEquals("New Deal", result.getDealName());
        assertEquals("New Type", result.getDealType());
        assertEquals("New Source", result.getSourceListId());
        assertEquals("New Side", result.getSide());

        verify(repository).findById(1);
        verify(repository).save(trade);
    }

    @Test
    void update_whenNotExisting_shouldThrow() {
        when(repository.findById(123)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> tradeService.update(123, new Trade()));

        verify(repository).findById(123);
        verify(repository, never()).save(any());
    }

    @Test
    void delete_whenExisting_shouldDelete() {
        when(repository.findById(1)).thenReturn(Optional.of(trade));

        tradeService.delete(1);

        verify(repository).findById(1);
        verify(repository).delete(trade);
    }

    @Test
    void delete_whenNotExisting_shouldThrow() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> tradeService.delete(999));

        verify(repository).findById(999);
        verify(repository, never()).delete(any());
    }
}