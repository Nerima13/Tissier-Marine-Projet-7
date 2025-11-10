package com.nnk.springboot.services;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BidListService {

    private final BidListRepository repository;

    // Retrieve all BidList entities
    public List<BidList> findAll() {
        return repository.findAll();
    }

    // Retrieve a BidList by its ID
    public BidList findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "BidList not found with id: " + id));
    }

    // Create a new BidList
    public BidList create(BidList bid) {
        bid.setBidListId(null);  // Safety check : ensure we are creating a new entry
        return repository.save(bid);
    }

    // Update an existing BidList
    public BidList update(Integer id, BidList bidList) {
        BidList existing = findById(id); // if not found, throw 404

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

    // Delete a BidList by its ID
    public void delete(Integer id) {
        BidList existing = findById(id);
        repository.delete(existing);
    }
}