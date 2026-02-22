package com.moneyflow.account.domain.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moneyflow.account.auth.security.SecurityUtil;
import com.moneyflow.account.domain.entity.Transaction;
import com.moneyflow.account.domain.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction create(Transaction tx) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();
        tx.setCreatedAt(now);
        tx.setLastUpdatedAt(now);
        tx.setIsActive(true);
        tx.setUserId(currentUserId);
        return transactionRepository.save(tx);
    }

    public Transaction softDelete(Long id) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (!tx.getIsActive()) {
            return tx;
        }
        tx.setIsActive(false);
        tx.setLastUpdatedAt(LocalDateTime.now());
        tx.setUserId(SecurityUtil.getCurrentUserId());
        return transactionRepository.save(tx);
    }

    public List<Transaction> softDelete(List<Long> ids) {
        List<Transaction> list = transactionRepository.findAllById(ids);
        Long userId = SecurityUtil.getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();
        for (Transaction tx : list) {
            if (tx.getIsActive()) {
                tx.setIsActive(false);
                tx.setLastUpdatedAt(now);
                tx.setUserId(userId);
            }
        }
        return transactionRepository.saveAll(list);
    }

    public Page<Transaction> findByCondition(Transaction searchTx, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase();
        Example<Transaction> example = Example.of(searchTx, matcher);
        return transactionRepository.findAll(example, pageable);
    }
}
