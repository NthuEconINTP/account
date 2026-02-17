package com.moneyflow.account.domain.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.moneyflow.account.domain.entity.Book;
import com.moneyflow.account.domain.repository.BookRepository;

//因為只有一種實作 就不要抽出介面了

@Service
public class BookService {
	
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // ===== 新增單筆 =====
    public Book create(Book book) {

        LocalDateTime now = LocalDateTime.now();

        book.setCreatedAt(now);
        book.setLastUpdatedAt(now);
        book.setIsActive(true);

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

        return bookRepository.save(book);
    }

    // ===== 軟刪除（批次）=====
    public List<Book> softDelete(List<Long> ids) {

        List<Book> books = bookRepository.findAllById(ids);

        LocalDateTime now = LocalDateTime.now();

        for (Book book : books) {
            if (book.getIsActive()) {
                book.setIsActive(false);
                book.setLastUpdatedAt(now);
            }
        }

        return bookRepository.saveAll(books);
    }

}
