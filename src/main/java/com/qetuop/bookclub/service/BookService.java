package com.qetuop.bookclub.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qetuop.bookclub.model.Book;
import com.qetuop.bookclub.repository.BookRepository;

@Service
public class BookService implements IBookService{

    @Autowired
    private BookRepository repository;

    @Override
    public List<Book> findAll() {

        List<Book> books = (List<Book>) repository.findAll();

        return books;
    } 

    @Override
    public Book findById(long id) {

        Optional<Book> book = repository.findById(id);

        return book.get();
    } 
}