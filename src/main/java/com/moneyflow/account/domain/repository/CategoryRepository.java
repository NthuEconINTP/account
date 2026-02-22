package com.moneyflow.account.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.moneyflow.account.domain.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

	boolean existsByBookIdAndNameAndIdNot(Long bookId, String name, Long categoryId);

}
