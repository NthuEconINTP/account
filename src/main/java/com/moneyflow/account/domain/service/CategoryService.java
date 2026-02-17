package com.moneyflow.account.domain.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.moneyflow.account.domain.entity.Category;
import com.moneyflow.account.domain.repository.CategoryRepository;

public class CategoryService {

	@Autowired
	CategoryRepository categoryRepository;

	// ===== 新增單筆 =====
	public Category create(Category category) {

		LocalDateTime now = LocalDateTime.now();

		category.setCreatedAt(now);
		category.setLastUpdatedAt(now);
		category.setIsActive(true);

		return categoryRepository.save(category);
	}

	// ===== 軟刪除（單筆）=====
	public Category softDelete(Long id) {

		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("category not found"));

		if (!category.getIsActive()) {
			System.out.println("This categoryRepository has been removed.");
			return category; // 已經刪除就回傳
		}

		category.setIsActive(false);
		category.setLastUpdatedAt(LocalDateTime.now());

		return categoryRepository.save(category);
	}

	// ===== 軟刪除（批次）=====
	public List<Category> softDelete(List<Long> ids) {

		List<Category> categorys = categoryRepository.findAllById(ids);

		LocalDateTime now = LocalDateTime.now();

		for (Category category : categorys) {
			if (category.getIsActive()) {
				category.setIsActive(false);
				category.setLastUpdatedAt(now);
			}
		}

		return categoryRepository.saveAll(categorys);
	}
}
