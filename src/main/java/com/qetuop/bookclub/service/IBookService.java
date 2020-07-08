package com.qetuop.bookclub.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import com.qetuop.bookclub.model.Book;

public interface IBookService {

    List<Book> findAll();
    Book findById(long id);

    void saveImageFile(long id, MultipartFile file);
}