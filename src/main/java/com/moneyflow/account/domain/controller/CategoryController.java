package com.moneyflow.account.domain.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;
import com.moneyflow.account.common.ApiResponse;
import com.moneyflow.account.common.ApiResponseUtil;
import com.moneyflow.account.domain.entity.Category;
import com.moneyflow.account.domain.service.CategoryService;

@RestController
@RequestMapping("/category")
public class CategoryController {
	
	@Autowired
	CategoryService categoryService;
	
	 // ===== 新增帳本帳務分類 =====
    @PostMapping
    public ApiResponse<Category> create(@RequestBody Category category) {

    	Category createdBook =  categoryService.create(category);
        return ApiResponseUtil.success("新增帳本帳務分類成功",createdBook);
    }
    
    /**
     * 更新新增帳本帳務分類
     * ID 直接包含在 Book 物件內
     */
    @PutMapping
    //理論上只會讓人改category.name還有category.note
    public ApiResponse<Category> update(@RequestBody Category category) {
        
    	Category updatedCategory = categoryService.update(category);
        return ApiResponseUtil.success("帳本帳務分類成功更新成功", updatedCategory);
    }
    
    // ===== 軟刪除單筆 =====
    @DeleteMapping("/{id}")
    public ApiResponse<Category> delete(@PathVariable Long id) {
    	Category deletedBook = categoryService.softDelete(id);
        return ApiResponseUtil.success("delete single book",deletedBook);
    }

    // ===== 軟刪除批次 =====
    @DeleteMapping
    public ApiResponse<List<Category>> deleteBatch(@RequestBody List<Long> ids) {
        List<Category> deletedCategorys = categoryService.softDelete(ids);
        return ApiResponseUtil.success("delete books",deletedCategorys);
    }
    
 // ===== 分頁查詢帳務分類 =====
    @PostMapping("/search")
    public ApiResponse<Page<Category>> search(
            @RequestBody(required = false) Category searchCategory, // 接收查詢條件
            @ParameterObject @PageableDefault(
                size = 10, 
                sort = "createdAt", 
                direction = Sort.Direction.DESC
            ) Pageable pageable) {
        
        if (searchCategory == null) {
            searchCategory = new Category();
        }

        // 安全檢查：分類查詢通常必須指定 bookId，
        // 確保使用者只能查詢到屬於該帳本的分類
        if (searchCategory.getBookId() == null) {
            return ApiResponseUtil.error("查詢失敗：必須指定帳本 ID");
        }
        
        // 呼叫 Service 進行多條件查詢
        Page<Category> categoryPage = categoryService.findByCondition(searchCategory, pageable);
        //Page<Category> categoryPage = categoryService.findByConditionByJdbc(searchCategory, pageable);
        return ApiResponseUtil.success("查詢分類成功", categoryPage);
    }
    
    @PostMapping("/searchByJdbc")
    public ApiResponse<Page<Category>> searchByJdbc(
            @RequestBody(required = false) Category searchCategory, // 接收查詢條件
            @ParameterObject @PageableDefault(
                size = 10, 
                sort = "createdAt", 
                direction = Sort.Direction.DESC
            ) Pageable pageable) {
        
        if (searchCategory == null) {
            searchCategory = new Category();
        }

        // 安全檢查：分類查詢通常必須指定 bookId，
        // 確保使用者只能查詢到屬於該帳本的分類
        if (searchCategory.getBookId() == null) {
            return ApiResponseUtil.error("查詢失敗：必須指定帳本 ID");
        }
        
        // 呼叫 Service 進行多條件查詢
        Page<Category> categoryPage = categoryService.findByConditionByJdbc(searchCategory, pageable);
        return ApiResponseUtil.success("查詢分類成功", categoryPage);
    }
    
}
