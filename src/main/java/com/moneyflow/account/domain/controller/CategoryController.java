package com.moneyflow.account.domain.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    
    
    
}
