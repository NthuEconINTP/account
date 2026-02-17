package com.moneyflow.account.domain.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.moneyflow.account.auth.security.SecurityUtil;
import com.moneyflow.account.domain.entity.Book;
import com.moneyflow.account.domain.repository.BookRepository;

//因為只有一種實作 就不要抽出介面了

@Service
public class BookService {
	
    BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // ===== 新增單筆 =====
    public Book create(Book book) {

        LocalDateTime now = LocalDateTime.now();

        book.setCreatedAt(now);
        book.setLastUpdatedAt(now);
        book.setIsActive(true);
        book.setUserId(SecurityUtil.getCurrentUserId());
        return bookRepository.save(book);
    }
    
    // ===== 軟刪除（單筆）=====
    public Book softDelete(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.getIsActive()) {
        	System.out.println("This book has been removed.");
            return book; // 已經刪除就回傳
        }

        book.setIsActive(false);
        book.setLastUpdatedAt(LocalDateTime.now());
        book.setUserId(SecurityUtil.getCurrentUserId());
        return bookRepository.save(book);
    }

    // ===== 軟刪除（批次）=====
    public List<Book> softDelete(List<Long> ids) {

        List<Book> books = bookRepository.findAllById(ids);

        LocalDateTime now = LocalDateTime.now();
        // 因為是重複的USER 所以在外面宣告
        Long userId=SecurityUtil.getCurrentUserId();
        for (Book book : books) {
            if (book.getIsActive()) {
                book.setIsActive(false);
                book.setLastUpdatedAt(now);
                book.setUserId(userId);
            }
        }

        return bookRepository.saveAll(books);
    }

}
