package com.moneyflow.account.domain.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moneyflow.account.auth.security.SecurityUtil;
import com.moneyflow.account.common.ApiResponse;
import com.moneyflow.account.common.ApiResponseUtil;
import com.moneyflow.account.domain.entity.Book;
import com.moneyflow.account.domain.service.BookService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

@RestController
@RequestMapping("/books")
public class BookController {
    @Autowired
    BookService bookService;

    // ===== 新增帳本 =====
    @PostMapping
    public ApiResponse<Book> create(@RequestBody Book book) {

        Book createdBook = bookService.create(book);
        return ApiResponseUtil.success("",createdBook);
    }
    
    /**
     * 更新帳本
     * ID 直接包含在 Book 物件內
     */
    @PutMapping
    //理論上只會讓人改book.name還有book.note
    public ApiResponse<Book> update(@RequestBody Book book) {
        
        Book updatedBook = bookService.update(book);
        return ApiResponseUtil.success("帳本更新成功", updatedBook);
    }

    // ===== 軟刪除單筆 =====
    @DeleteMapping("/{id}")
    public ApiResponse<Book> delete(@PathVariable Long id) {
        Book deletedBook = bookService.softDelete(id);
        return ApiResponseUtil.success("delete single book",deletedBook);
    }

    // ===== 軟刪除批次 =====
    @DeleteMapping
    public ApiResponse<List<Book>> deleteBatch(@RequestBody List<Long> ids) {
        List<Book> deletedBooks = bookService.softDelete(ids);
        return ApiResponseUtil.success("delete books",deletedBooks);
    }

//    @PostMapping("/search")
//    public ApiResponse<List<Book>> search(@RequestBody Book filter,
//                                          @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
//                                          @RequestParam(name = "order", defaultValue = "asc") String order) {
//        Sort sort = "desc".equalsIgnoreCase(order) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
//        List<Book> books = bookService.findAll(filter, sort);
//        return ApiResponseUtil.success("list books", books);
//    }
    
    @PostMapping("/search")
    public ApiResponse<Page<Book>> search(
            @RequestBody(required = false) Book searchBook, // 接收 Body (data)
            @ParameterObject @PageableDefault(              // 接收 URL 參數 (params)
                size = 10, 
                sort = "createdAt", 
                direction = Sort.Direction.DESC
            ) Pageable pageable) {
        
        if (searchBook == null) searchBook = new Book();
        
        // 強制設定為當前使用者
        searchBook.setUserId(SecurityUtil.getCurrentUserId());
        
        Page<Book> books = bookService.findByCondition(searchBook, pageable);
        return ApiResponseUtil.success("查詢成功", books);
    }
    
}
