package com.qetuop.bookclub.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.io.IOException;

import com.qetuop.bookclub.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;

import com.qetuop.bookclub.model.Book;
import com.qetuop.bookclub.repository.BookRepository;

@Service
public class BookService implements IBookService{

    @Autowired
    public BookRepository repository;

    @Override
    public Book save(Book book) {
        book.setUpdated(LocalDateTime.now());
        return repository.save(book);
    }

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
    public Book findByAuthorAndTitle(String author, String title) {
        System.out.println("BookService:findByAuthorAndTitle: " + author +":"+ title);
        Book book = repository.findByAuthorAndTitle(author,title);
        return book;
    }

    @Override
    public List<Book> retrieveByTag(String tag) {
        System.out.println("retrieveByTag: " + tag);
        List<Book> books = (List<Book>) repository.retrieveByTag(tag);
        System.out.println("retrieveByTag FOUND: " + books.size());
        return books;
    }

    public List<Book> findByNameEndsWith(String chars) {
        System.out.println("findByNameEndsWith: " + chars);
        List<Book> books = (List<Book>) repository.findByNameEndsWith(chars);
        System.out.println("findByNameEndsWith FOUND: " + books.size());
        return books;
    }

    /*
    @Override
    public List<Book> test() {
        System.out.println("test: ");
        List<Book> books = (List<Book>) repository.test();
        System.out.println("test FOUND: " + books.size());
        return books;
    }
*/

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

    public void setRead(Long id, boolean read) {
        Book book = repository.findById(id).get();
        book.setRead(read);
        repository.save(book);
    }

    @Override
    public void addTag(Long id, String _tag) {
        Book book = repository.findById(id).get();

        // first see if tag exists for book, TODO: can i have a Tag table with unique tags?
        // do i need a TagRepository and TagService?

        Tag tag = new Tag(_tag);

        book.addTag(tag);
        repository.save(book);
    }

    @Override
    public void delTag(Long id, String _tag) {
        Book book = repository.findById(id).get();

        // first see if tag exists for book, TODO: can i have a Tag table with unique tags?
        // do i need a TagRepository and TagService?

        Tag tag = new Tag(_tag);

        book.removeTag(tag);
        repository.save(book);
    }
}