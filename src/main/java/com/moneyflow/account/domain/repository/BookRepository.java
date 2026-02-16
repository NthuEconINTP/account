package com.moneyflow.account.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moneyflow.account.domain.entity.Book;



public  interface BookRepository extends JpaRepository<Book, Long>{

}
