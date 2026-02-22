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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping
    public ApiResponse<Transaction> create(@RequestBody Transaction tx) {
        Transaction created = transactionService.create(tx);
        return ApiResponseUtil.success("創造一筆交易成功", created);
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

    @PostMapping("/search")
    public ApiResponse<Page<Transaction>> search(
            @RequestBody(required = false) Transaction searchTx,
            @ParameterObject @PageableDefault(
                size = 10,
                sort = "transactionDate",
                direction = Sort.Direction.DESC
            ) Pageable pageable) {

        if (searchTx == null) searchTx = new Transaction();
        searchTx.setUserId(SecurityUtil.getCurrentUserId());

        Page<Transaction> page = transactionService.findByCondition(searchTx, pageable);
        return ApiResponseUtil.success("查詢交易成功", page);
    }
}
