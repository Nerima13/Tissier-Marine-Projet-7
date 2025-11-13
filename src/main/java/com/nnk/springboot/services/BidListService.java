package com.nnk.springboot.services;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Service layer for managing {@link BidList} entities.
 * Provides CRUD operations and centralizes business logic
 * related to the BidList domain.
 */
@Service
@RequiredArgsConstructor
public class BidListService {

    private final BidListRepository repository;

    /**
     * Returns all BidList entries.
     *
     * @return list of BidList entities
     */
    public List<BidList> findAll() {
        return repository.findAll();
    }

    /**
     * Retrieves a BidList by its ID.
     *
     * @param id the BidList ID
     * @return the corresponding BidList entity
     * @throws ResponseStatusException if not found (404)
     */
    public BidList findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "BidList not found with id: " + id));
    }

    /**
     * Creates a new BidList entry.
     *
     * @param bid the BidList to create
     * @return the saved BidList entity
     */
    public BidList create(BidList bid) {
        bid.setBidListId(null);  // ensure a new entry
        return repository.save(bid);
    }

    /**
     * Updates an existing BidList entry.
     *
     * @param id the ID of the BidList to update
     * @param bidList data to update
     * @return the updated BidList entity
     * @throws ResponseStatusException if the BidList does not exist
     */
    public BidList update(Integer id, BidList bidList) {
        BidList existing = findById(id);

        existing.setAccount(bidList.getAccount());
        existing.setType(bidList.getType());
        existing.setBidQuantity(bidList.getBidQuantity());
        existing.setAskQuantity(bidList.getAskQuantity());
        existing.setBid(bidList.getBid());
        existing.setAsk(bidList.getAsk());
        existing.setBenchmark(bidList.getBenchmark());
        existing.setBidListDate(bidList.getBidListDate());
        existing.setCommentary(bidList.getCommentary());
        existing.setSecurity(bidList.getSecurity());
        existing.setStatus(bidList.getStatus());
        existing.setTrader(bidList.getTrader());
        existing.setBook(bidList.getBook());
        existing.setCreationName(bidList.getCreationName());
        existing.setCreationDate(bidList.getCreationDate());
        existing.setRevisionName(bidList.getRevisionName());
        existing.setRevisionDate(bidList.getRevisionDate());
        existing.setDealName(bidList.getDealName());
        existing.setDealType(bidList.getDealType());
        existing.setSourceListId(bidList.getSourceListId());
        existing.setSide(bidList.getSide());

        return repository.save(existing);
    }

    /**
     * Deletes a BidList by its ID.
     *
     * @param id the ID of the BidList to delete
     * @throws ResponseStatusException if the BidList does not exist
     */
    public void delete(Integer id) {
        BidList existing = findById(id);
        repository.delete(existing);
    }
}