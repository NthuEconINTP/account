package com.moneyflow.account.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.moneyflow.account.domain.entity.ActivatableEntity;
import com.moneyflow.account.domain.entity.Transaction;
import com.moneyflow.account.domain.repository.TransactionRepository;

@Service
public class TransactionService implements BaseService< ActivatableEntity, Long> {
	@Autowired
    private final TransactionRepository transactionRepository;

 
    @Override
    public JpaRepository<Transaction, Long> getRepository() {
        return transactionRepository;
    }
}


}
