package com.moneyflow.account.domain.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional // 建議新增資料的方法加上事務註解
    public Book create(Book book) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        // 1. 檢查名稱重複 (同一個用戶下book name 不可重複)
        if (bookRepository.existsByUserIdAndName(currentUserId, book.getName())) {
            throw new RuntimeException("帳本名稱「" + book.getName() + "」已存在，請換個名字吧！");
        }
        // 2. 設定預設值
        LocalDateTime now = LocalDateTime.now();
        book.setCreatedAt(now);
        book.setLastUpdatedAt(now);
        book.setIsActive(true);
        book.setUserId(currentUserId);

        return bookRepository.save(book);
    }
    
    @Transactional
    public Book update(Book updatedBook) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        Long bookId = updatedBook.getId(); // 直接從傳入的物件拿 ID

        if (bookId == null) {
            throw new RuntimeException("更新失敗：必須提供帳本 ID");
        }

        // 1. 找出原始資料並驗證權限
        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("找不到該帳本或已刪除"));

        if (!existingBook.getUserId().equals(currentUserId)) {
            throw new RuntimeException("你沒有權限修改此帳本");
        }

        // 2. 檢查名稱重複 (排除自己目前的 ID)
        if (updatedBook.getName() != null) {
            boolean nameExists = bookRepository.existsByUserIdAndNameAndIdNot(
                    currentUserId, 
                    updatedBook.getName(), 
                    bookId
            );
            if (nameExists) {
                throw new RuntimeException("帳本名稱「" + updatedBook.getName() + "」已存在");
            }
            existingBook.setName(updatedBook.getName());
        }

        // 3. 更新其他欄位
        if (updatedBook.getNote() != null) {
            existingBook.setNote(updatedBook.getNote());
        }
        
//        if (updatedBook.getIsActive() != null) {
//            existingBook.setIsActive(updatedBook.getIsActive());
//        }

        // 4. 設定更新時間
        existingBook.setLastUpdatedAt(LocalDateTime.now());

        return bookRepository.save(existingBook);
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
        Long userId = SecurityUtil.getCurrentUserId();
        for (Book book : books) {
            if (book.getIsActive()) {
                book.setIsActive(false);
                book.setLastUpdatedAt(now);
                book.setUserId(userId);
            }
        }

        return bookRepository.saveAll(books);
    }

//    public List<Book> findAll(Book probe, Sort sort) {
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withIgnoreNullValues()
//                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
//                .withIgnoreCase();
//        Example<Book> example = Example.of(probe, matcher);
//        return bookRepository.findAll(example, sort);
//    }
    
    /**
     * 根據傳入的 Book 物件欄位進行「精確匹配」查詢
     * @param searchBook 前端傳來的查詢條件物件
     * @param pageable 包含 page, size, sort 的分頁物件
     */
    public Page<Book> findByCondition(Book searchBook, Pageable pageable) {
        // 1. 配置比對器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                // 將所有字串欄位設為「包含」模式（相當於 LIKE %value%）
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                // 如果你希望不分大小寫，可以加上這行
                .withIgnoreCase();

        // 2. 建立查詢範例
        Example<Book> example = Example.of(searchBook, matcher);

        // 3. 執行查詢
        return bookRepository.findAll(example, pageable);
    }

}
