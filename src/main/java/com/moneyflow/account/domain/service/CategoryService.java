package com.moneyflow.account.domain.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.moneyflow.account.auth.security.SecurityUtil;
import com.moneyflow.account.domain.entity.Category;
import com.moneyflow.account.domain.repository.BookRepository;
import com.moneyflow.account.domain.repository.CategoryRepository;

public class CategoryService {

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
    private BookRepository bookRepository; // 注入 BookRepository 檢查權限
	
	// ===== 新增單筆 =====
	public Category create(Category category) {

	    if (category.getId() == null) {
            throw new RuntimeException("更新失敗：必須提供帳務分類 ID");
        }
	    
	    if (category.getType() == null) {
            throw new RuntimeException("更新失敗：必須提供帳務分類 支出或是收入");
        }
        
        if (category.getName() == null) {
            throw new RuntimeException("更新失敗：必須提供帳務分類 名稱");
        }
  
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
	
	@Transactional
    public Category update(Category updatedCategory) {
		
        Long currentUserId = SecurityUtil.getCurrentUserId();
        Long categoryId = updatedCategory.getId();

        if (categoryId == null) {
            throw new RuntimeException("更新失敗：必須提供帳務分類 ID");
        }
        
        if (updatedCategory.getType() == null) {
            throw new RuntimeException("更新失敗：必須提供帳務分類 支出或是收入");
        }
        
        if (updatedCategory.getName() == null) {
            throw new RuntimeException("更新失敗：必須提供帳務分類 名稱");
        }
  
        // 1. 找出原始分類資料
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("找不到該帳務分類或已刪除"));

        // 2. 核心權限驗證：驗證該分類所屬的帳本是否屬於當前使用者
        checkBookOwnership(existingCategory.getBookId(), currentUserId);

        // 3. 檢查名稱重複 (在同一個帳本內，排除自己目前的 ID)
        if (updatedCategory.getName() != null) {
            boolean nameExists = categoryRepository.existsByBookIdAndNameAndIdNot(
                    existingCategory.getBookId(), 
                    updatedCategory.getName(), 
                    categoryId
            );
            if (nameExists) {
                throw new RuntimeException("帳務分類名稱「" + updatedCategory.getName() + "」已存在於此帳本中");
            }
            existingCategory.setName(updatedCategory.getName());
        }

        // 4. 更新其餘欄位
        if (updatedCategory.getType() != null) {
            existingCategory.setType(updatedCategory.getType());
        }
        if (updatedCategory.getNote() != null) {
            existingCategory.setNote(updatedCategory.getNote());
        }
        if (updatedCategory.getIsActive() != null) {
            existingCategory.setIsActive(updatedCategory.getIsActive());
        }

        existingCategory.setLastUpdatedAt(LocalDateTime.now());
        return categoryRepository.save(existingCategory);
    }

    /**
     * 私有驗證方法：檢查帳本擁有權
     */
    private void checkBookOwnership(Long bookId, Long userId) {
        // 假設 BookRepository 有這個自定義查詢
        boolean isOwner = bookRepository.existsByIdAndUserId(bookId, userId);
        if (!isOwner) {
            throw new RuntimeException("權限不足：您不擁有此帳本，無法修改其分類");
        }
    }
}
