package com.moneyflow.account.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moneyflow.account.domain.entity.Book;


public  interface BookRepository extends JpaRepository<Book, Long>{
	// 檢查該用戶是否已經有同名的帳本
    boolean existsByUserIdAndName(Long userId, String name);
    
    // 新增：檢查該用戶下，除了某個 ID 以外，是否已有同名帳本
    boolean existsByUserIdAndNameAndIdNot(Long userId, String name, Long id);
}
