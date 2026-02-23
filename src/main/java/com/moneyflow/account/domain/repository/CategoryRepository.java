package com.moneyflow.account.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.moneyflow.account.domain.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

	// 檢查該帳本下是否存在同名的分類
    boolean existsByBookIdAndName(Long bookId, String name);
	
	boolean existsByBookIdAndNameAndIdNot(Long bookId, String name, Long categoryId);
	
}
