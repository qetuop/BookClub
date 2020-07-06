package com.qetuop.bookclub.service;

import java.util.List;

import com.qetuop.bookclub.model.Book;

public interface IBookService {

    List<Book> findAll();
    Book findById(long id);
}