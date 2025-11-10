package com.nnk.springboot.services;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository repository;

    // Find all Trades
    public List<Trade> findAll() {
        return repository.findAll();
    }

    // Find Trade by ID
    public Trade findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Trade not found with id: " + id));
    }

    // Create a new Trade
    public Trade create(Trade trade) {
        trade.setTradeId(null); // ensure new entity
        return repository.save(trade);
    }

    // Update an existing Trade
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

    // Delete a Trade
    public void delete(Integer id) {
        Trade existing = findById(id);
        repository.delete(existing);
    }
}