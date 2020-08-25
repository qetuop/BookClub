package com.qetuop.bookclub.service;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.multipart.MultipartFile;

import com.qetuop.bookclub.model.Book;

public interface IBookService {

    public Book save(Book book);

    List<Book> findAll();
    Book findById(long id);

    List<Book> findByAuthor(String author);
    List<Book> findBySeriesName(String seriesName);

    Book findByAuthorAndTitle(String author, String title);

    List<Book> retrieveByTag(String tag);
    List<Book> findByNameEndsWith(String chars);
    //List<Book> test();

    void saveImageFile(long id, MultipartFile file);
    void setRead(Long id, boolean read);
    void addTag(Long id, String tag);
    void delTag(Long id, String tag);
}