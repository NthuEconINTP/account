package com.moneyflow.account.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.moneyflow.account.domain.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{
	
}
