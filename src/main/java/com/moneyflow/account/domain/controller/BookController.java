package com.moneyflow.account.domain.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moneyflow.account.domain.entity.Book;
import com.moneyflow.account.domain.service.BookService;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // ===== 新增帳本 =====
    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book book) {

        Book createdBook = bookService.create(book);
        return ResponseEntity.ok(createdBook);
    }

    // ===== 軟刪除單筆 =====
    @DeleteMapping("/{id}")
    public ResponseEntity<Book> delete(@PathVariable Long id) {
        Book deletedBook = bookService.softDelete(id);
        return ResponseEntity.ok(deletedBook);
    }

    // ===== 軟刪除批次 =====
    @DeleteMapping
    public ResponseEntity<List<Book>> deleteBatch(@RequestBody List<Long> ids) {
        List<Book> deletedBooks = bookService.softDelete(ids);
        return ResponseEntity.ok(deletedBooks);
    }
}