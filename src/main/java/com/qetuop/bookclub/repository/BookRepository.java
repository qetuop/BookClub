package com.qetuop.bookclub.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.qetuop.bookclub.model.Book;

public interface BookRepository extends CrudRepository<Book, Long> {

  List<Book> findByAuthor(String author);

  Book findById(long id);
}