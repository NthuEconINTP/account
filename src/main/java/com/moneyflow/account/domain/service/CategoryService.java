package com.moneyflow.account.domain.service;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.moneyflow.account.auth.security.SecurityUtil;
import com.moneyflow.account.domain.entity.Category;
import com.moneyflow.account.domain.repository.BookRepository;
import com.moneyflow.account.domain.repository.CategoryRepository;
@Service
public class CategoryService {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
    private BookRepository bookRepository; // 注入 BookRepository 檢查權限
	
	// ===== 新增單筆 =====
	public Category create(Category category) {

		// 1. 新增時 ID 必須是空的 (由資料庫生成)
	    if (category.getId() != null) {
	        // 這裡可以選擇不報錯，直接把 ID 設為 null 確保是新增
	        category.setId(null);
	    }
	    
	    // 2. 檢查必要欄位
	    if (category.getType() == null) {
	        throw new RuntimeException("新增失敗：必須提供帳務分類類型 (支出或收入)");
	    }
	    
	    if (category.getName() == null || category.getName().trim().isEmpty()) {
	        throw new RuntimeException("新增失敗：必須提供帳務分類名稱");
	    }

	    if (category.getBookId() == null) {
	        throw new RuntimeException("新增失敗：必須提供所屬帳本 ID");
	    }
	    
	    // --- 新增：同名檢查 ---
	    boolean isDuplicate = categoryRepository.existsByBookIdAndName(
	        category.getBookId(), 
	        category.getName().trim()
	    );

	    if (isDuplicate) {
	        throw new RuntimeException("新增失敗：此帳本已有相同的分類名稱「" + category.getName() + "」");
	    }

	    // 3. 設定時間與預設值
	    LocalDateTime now = LocalDateTime.now();
	    category.setCreatedAt(now);
	    category.setLastUpdatedAt(now);
	    
	    // 如果前端沒傳 isActive，預設給 true
	    if (category.getIsActive() == null) {
	        category.setIsActive(true);
	    }

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
    /**
     * 根據傳入的 Category 物件欄位進行「精確匹配」查詢
     * @param searchCategory 前端傳來的查詢條件物件
     * @param pageable 包含 page, size, sort 的分頁物件
     */
    public Page<Category> findByCondition(Category searchCategory, Pageable pageable) {
        // 1. 安全檢查：確認這個 bookId 真的屬於目前登入的使用者
        Long currentUserId = SecurityUtil.getCurrentUserId();
        Long targetBookId = searchCategory.getBookId();
        if (targetBookId == null) {
            throw new RuntimeException("查詢失敗：必須指定帳本 ID");
        }
        // 這裡需要調用 bookRepository 或 bookService 來檢查權限 //這是我告訴AI 才有的
        boolean isOwner = bookRepository.existsByIdAndUserId(targetBookId, currentUserId);
        
        if (!isOwner) {
            throw new RuntimeException("權限不足：您無權查看此帳本的分類");
        }
        // 2. 權限過濾後，才執行 QBE 查詢
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase();

        Example<Category> example = Example.of(searchCategory, matcher);
        return categoryRepository.findAll(example, pageable);
    }
    
    public Page<Category> findByConditionByJdbc(Category search, Pageable pageable) {
        // 1. 安全檢查 (保留妳原有的邏輯)
        Long currentUserId = SecurityUtil.getCurrentUserId();
        Long targetBookId = search.getBookId();
        if (targetBookId == null) {
            throw new RuntimeException("查詢失敗：必須指定帳本 ID");
        }
        
        boolean isOwner = bookRepository.existsByIdAndUserId(targetBookId, currentUserId);
        if (!isOwner) {
            throw new RuntimeException("權限不足：您無權查看此帳本的分類");
        }

        // 2. 建立基礎條件 SQL (不含 SELECT, ORDER BY 和 LIMIT)
        // 這樣這段 SQL 才能同時給「查資料」和「查總數」共用
        StringBuilder whereSql = new StringBuilder(" FROM category WHERE book_id = ? AND type = ?");
        List<Object> params = new ArrayList<>();
        params.add(targetBookId);
        params.add(search.getType().name());

        // 狀態過濾
        if (search.getIsActive() != null) {
            whereSql.append(" AND is_active = ?");
            params.add(search.getIsActive());
        }

        // 關鍵字搜尋 (Name OR Note)
        if (search.getName() != null && !search.getName().trim().isEmpty()) {
            whereSql.append(" AND (name LIKE ? OR note LIKE ?)");
            String keyword = "%" + search.getName().trim() + "%";
            params.add(keyword);
            params.add(keyword);
        }

        // 3. 查詢總筆數 (totalCount)
        // 注意：必須在加上 LIMIT 之前查總數
        String countSql = "SELECT COUNT(*)" + whereSql.toString();
        // 使用 queryForObject 取得單一數字
        Long totalCount = jdbcTemplate.queryForObject(countSql, Long.class, params.toArray());
        
        // 如果 totalCount 為 null (理論上不會)，設為 0
        long total = (totalCount != null) ? totalCount : 0L;

        // 4. 查詢分頁資料
        StringBuilder dataSql = new StringBuilder("SELECT *" + whereSql.toString());
        dataSql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
        
        // 建立一個新的參數清單，加上分頁參數
        List<Object> dataParams = new ArrayList<>(params);
        dataParams.add(pageable.getPageSize());
        dataParams.add(pageable.getOffset());

        List<Category> list = jdbcTemplate.query(
            dataSql.toString(), 
            new BeanPropertyRowMapper<>(Category.class), 
            dataParams.toArray()
        );

        // 5. 使用 PageImpl 回傳，前端 Vue 就能拿到完整的分頁資訊
        return new PageImpl<>(list, pageable, total);
    }
    
}
