package com.nnk.springboot.services;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
import com.nnk.springboot.services.BidListService;
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
class BidServiceTests {

    @Mock
    private BidListRepository repository;

    @InjectMocks
    private BidListService bidListService;

    private BidList bid;

    @BeforeEach
    void setUp() {
        bid = new BidList();
        bid.setBidListId(1);
        bid.setAccount("Account");
        bid.setType("Type");
        bid.setBidQuantity(10d);
        bid.setAskQuantity(5d);
        bid.setBid(100d);
        bid.setAsk(101d);
        bid.setBenchmark("Benchmark");
        bid.setBidListDate(LocalDateTime.now());
        bid.setCommentary("Comment");
        bid.setSecurity("Security");
        bid.setStatus("Status");
        bid.setTrader("Trader");
        bid.setBook("Book");
        bid.setCreationName("Creator");
        bid.setCreationDate(LocalDateTime.now());
        bid.setRevisionName("Revisor");
        bid.setRevisionDate(LocalDateTime.now());
        bid.setDealName("Deal");
        bid.setDealType("Type");
        bid.setSourceListId("Source");
        bid.setSide("Side");
    }

    @Test
    void findAll_shouldReturnListOfBidLists() {
        when(repository.findAll()).thenReturn(List.of(bid));

        List<BidList> result = bidListService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bid, result.get(0));
        verify(repository).findAll();
    }

    @Test
    void findById_whenExistingId_shouldReturnBidList() {
        when(repository.findById(1)).thenReturn(Optional.of(bid));

        BidList result = bidListService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getBidListId());
        verify(repository).findById(1);
    }

    @Test
    void findById_whenNonExistingId_shouldThrowResponseStatusException() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> bidListService.findById(999));

        assertEquals(404, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("BidList not found with id: 999"));
        verify(repository).findById(999);
    }

    @Test
    void create_shouldSetIdToNullAndSave() {
        BidList toCreate = new BidList();
        toCreate.setBidListId(42);
        toCreate.setAccount("New Account");

        BidList saved = new BidList();
        saved.setBidListId(1);
        saved.setAccount("New Account");

        when(repository.save(any(BidList.class))).thenReturn(saved);

        BidList result = bidListService.create(toCreate);

        assertNotNull(result);
        assertEquals(1, result.getBidListId());
        assertEquals("New Account", result.getAccount());
        verify(repository).save(argThat(b -> b.getBidListId() == null));
    }

    @Test
    void update_whenExistingId_shouldUpdateAndSave() {
        when(repository.findById(1)).thenReturn(Optional.of(bid));

        BidList updates = new BidList();
        updates.setAccount("Updated Account");
        updates.setType("Updated Type");
        updates.setBidQuantity(20d);
        updates.setAskQuantity(10d);
        updates.setBid(200d);
        updates.setAsk(201d);
        updates.setBenchmark("New Benchmark");
        updates.setBidListDate(LocalDateTime.of(2024, 1, 1, 0, 0));
        updates.setCommentary("New Comment");
        updates.setSecurity("New Security");
        updates.setStatus("New Status");
        updates.setTrader("New Trader");
        updates.setBook("New Book");
        updates.setCreationName("New Creator");
        updates.setRevisionName("New Revisor");
        updates.setDealName("New Deal");
        updates.setDealType("New Type");
        updates.setSourceListId("New Source");
        updates.setSide("New Side");

        LocalDateTime ignoredDate = LocalDateTime.now();
        updates.setCreationDate(ignoredDate);
        updates.setRevisionDate(ignoredDate);

        when(repository.save(any(BidList.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BidList result = bidListService.update(1, updates);

        assertEquals("Updated Account", result.getAccount());
        assertEquals("Updated Type", result.getType());
        assertEquals(20d, result.getBidQuantity());
        assertEquals(10d, result.getAskQuantity());
        assertEquals(200d, result.getBid());
        assertEquals(201d, result.getAsk());
        assertEquals("New Benchmark", result.getBenchmark());
        assertEquals(LocalDateTime.of(2024, 1, 1, 0, 0), result.getBidListDate());
        assertEquals("New Comment", result.getCommentary());
        assertEquals("New Security", result.getSecurity());
        assertEquals("New Status", result.getStatus());
        assertEquals("New Trader", result.getTrader());
        assertEquals("New Book", result.getBook());
        assertEquals("New Creator", result.getCreationName());
        assertEquals("New Revisor", result.getRevisionName());
        assertEquals("New Deal", result.getDealName());
        assertEquals("New Type", result.getDealType());
        assertEquals("New Source", result.getSourceListId());
        assertEquals("New Side", result.getSide());

        verify(repository).findById(1);
        verify(repository).save(bid);
    }

    @Test
    void update_whenNonExistingId_shouldThrowResponseStatusException() {
        when(repository.findById(123)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> bidListService.update(123, new BidList()));

        assertEquals(404, ex.getStatusCode().value());
        verify(repository).findById(123);
        verify(repository, never()).save(any());
    }

    @Test
    void delete_whenExistingId_shouldDeleteEntity() {
        when(repository.findById(1)).thenReturn(Optional.of(bid));

        bidListService.delete(1);

        verify(repository).findById(1);
        verify(repository).delete(bid);
    }

    @Test
    void delete_whenNonExistingId_shouldThrowResponseStatusException() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> bidListService.delete(999));

        assertEquals(404, ex.getStatusCode().value());
        verify(repository).findById(999);
        verify(repository, never()).delete(any());
    }
}