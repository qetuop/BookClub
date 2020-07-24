package com.qetuop.bookclub.service;

import java.util.List;
import java.util.Optional;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


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

    @Override
    public List<Book> findByAuthor(String author) {
        System.out.println("BookService:findByAuthor: " + author);
        List<Book> books = (List<Book>) repository.findByAuthor(author);
        return books;
    }

    @Override
    public List<Book> findBySeriesName(String seriesName) {
        System.out.println("BookService:findBySeries: " + seriesName);
        List<Book> books = (List<Book>) repository.findBySeriesName(seriesName);
        return books;
    }

    @Override
    @Transactional
    public void saveImageFile(long id, MultipartFile file) {
        System.out.println("HERE:saveImageFile");
        try {
            Book book = repository.findById(id).get();
            Byte[] byteObjects = new Byte[file.getBytes().length];

            int i = 0;
            for (byte b : file.getBytes()){
                byteObjects[i++] = b;
            }

            book.setImage(byteObjects);

            repository.save(book);
        } catch (IOException e) {
            //todo handle better
            //log.error("Error occurred", e);
            System.out.println("Error occurred");
            e.printStackTrace();
        }
    }
}