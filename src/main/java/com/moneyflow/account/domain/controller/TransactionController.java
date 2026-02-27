package com.moneyflow.account.domain.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moneyflow.account.auth.security.SecurityUtil;
import com.moneyflow.account.common.ApiResponse;
import com.moneyflow.account.common.ApiResponseUtil;
import com.moneyflow.account.domain.entity.Transaction;
import com.moneyflow.account.domain.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

//    @PostMapping
//    public ApiResponse<Transaction> create(@RequestBody Transaction tx) {
//        Transaction created = transactionService.create(tx);
//        return ApiResponseUtil.success("創造一筆交易成功", created);
//    }
    
 // ===== 新增交易紀錄 (Transaction) =====
    @Operation(
    	    summary = "新增交易紀錄",
    	    description = "### 業務邏輯說明\n" +
    	                  "1. **權限檢查**：會從 SecurityContext 取得當前 User，並檢查 `bookId` 歸屬。\n" +
    	                  "2. **類型一致性**：`transaction.type` 必須等於 `category.type`。\n" +
    	                  "3. **自動填充**：系統會自動寫入 `createdAt` 與 `userId`。\n" +
    	                  "\n" +
    	                  "> 注意：金額必須大於 0，否則會噴 RuntimeException。"
    	                  + "{\n"
    	                  + "\n"
    	                  + "  \"bookId\": 14,\n"
    	                  + "\n"
    	                  + "  \"categoryId\": 2,\n"
    	                  + "\n"
    	                  + "  \"name\": \"忠孝東路星巴克午茶\",\n"
    	                  + "\n"
    	                  + "  \"type\": \"EXPENSE\",\n"
    	                  + "\n"
    	                  + "  \"amount\": 185.50,\n"
    	                  + "\n"
    	                  + "  \"transactionDate\": \"2026-02-27\",\n"
    	                  + "\n"
    	                  + "  \"note\": \"與客戶開會討論專案\"\n"
    	                  + "\n"
    	                  + "}"
    )
    @PostMapping
    public ApiResponse<Transaction> create(
        @RequestBody Transaction transaction) {

        // 執行 Service 邏輯並回傳儲存後的實體 (包含生成的 ID)
        Transaction savedTransaction = transactionService.create(transaction);
        
        return ApiResponseUtil.success("新增成功", savedTransaction);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Transaction> delete(@PathVariable Long id) {
        Transaction deleted = transactionService.softDelete(id);
        return ApiResponseUtil.success("刪除一筆交易成功", deleted);
    }

    @DeleteMapping
    public ApiResponse<List<Transaction>> deleteBatch(@RequestBody List<Long> ids) {
        List<Transaction> deleted = transactionService.softDelete(ids);
        return ApiResponseUtil.success("刪除多筆交易成功", deleted);
    }

//    @PostMapping("/search")
//    public ApiResponse<Page<Transaction>> search(
//            @RequestBody(required = false) Transaction searchTx,
//            @ParameterObject @PageableDefault(
//                size = 10,
//                sort = "transactionDate",
//                direction = Sort.Direction.DESC
//            ) Pageable pageable) {
//
//        if (searchTx == null) searchTx = new Transaction();
//        searchTx.setUserId(SecurityUtil.getCurrentUserId());
//
//        Page<Transaction> page = transactionService.findByCondition(searchTx, pageable);
//        return ApiResponseUtil.success("查詢交易成功", page);
//    }
}
