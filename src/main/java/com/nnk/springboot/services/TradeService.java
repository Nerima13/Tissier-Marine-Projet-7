package com.nnk.springboot.services;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Service layer for managing {@link Trade} entities.
 * Provides CRUD operations for the Trade domain.
 */
@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository repository;

    /**
     * Retrieves all Trade entries.
     *
     * @return list of Trade entities
     */
    public List<Trade> findAll() {
        return repository.findAll();
    }

    /**
     * Retrieves a Trade by its ID.
     *
     * @param id the Trade ID
     * @return the corresponding Trade entity
     * @throws ResponseStatusException if not found (404)
     */
    public Trade findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "Trade not found with id: " + id));
    }

    /**
     * Creates a new Trade entry.
     *
     * @param trade the Trade to create
     * @return the saved Trade entity
     */
    public Trade create(Trade trade) {
        trade.setTradeId(null); // ensure creation of a new entry
        return repository.save(trade);
    }

    /**
     * Updates an existing Trade entry.
     *
     * @param id the ID of the Trade to update
     * @param trade the updated Trade data
     * @return the updated Trade entity
     * @throws ResponseStatusException if the Trade does not exist
     */
    public Trade update(Integer id, Trade trade) {
        Trade existing = findById(id);

        existing.setAccount(trade.getAccount());
        existing.setType(trade.getType());
        existing.setBuyQuantity(trade.getBuyQuantity());
        existing.setSellQuantity(trade.getSellQuantity());
        existing.setBuyPrice(trade.getBuyPrice());
        existing.setSellPrice(trade.getSellPrice());
        existing.setTradeDate(trade.getTradeDate());
        existing.setSecurity(trade.getSecurity());
        existing.setStatus(trade.getStatus());
        existing.setTrader(trade.getTrader());
        existing.setBenchmark(trade.getBenchmark());
        existing.setBook(trade.getBook());
        existing.setCreationName(trade.getCreationName());
        existing.setCreationDate(trade.getCreationDate());
        existing.setRevisionName(trade.getRevisionName());
        existing.setRevisionDate(trade.getRevisionDate());
        existing.setDealName(trade.getDealName());
        existing.setDealType(trade.getDealType());
        existing.setSourceListId(trade.getSourceListId());
        existing.setSide(trade.getSide());

        return repository.save(existing);
    }

    /**
     * Deletes a Trade by its ID.
     *
     * @param id the ID of the Trade to delete
     * @throws ResponseStatusException if the Trade does not exist
     */
    public void delete(Integer id) {
        Trade existing = findById(id);
        repository.delete(existing);
    }
}