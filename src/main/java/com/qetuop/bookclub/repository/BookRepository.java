package com.qetuop.bookclub.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.qetuop.bookclub.model.Book;

public interface BookRepository extends CrudRepository<Book, Long> {

}