package com.qetuop.bookclub.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.io.IOException;

import com.qetuop.bookclub.model.Tag;
import com.qetuop.bookclub.repository.TagRepository;
import com.qetuop.bookclub.service.TagService;
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
public class BookService {

    @Autowired
    public BookRepository repository;
    @Autowired
    public TagService tagService;

    public Book save(Book book) {
        book.setUpdated(Instant.now().toEpochMilli());
        //book.setUpdated(LocalDateTime.now());
        return repository.save(book);
    }

    public Iterable<Book> saveAll(Iterable<Book> books) {
        //book.setUpdated(Instant.now().toEpochMilli());
        //book.setUpdated(LocalDateTime.now());
        //DataAccessException
        return repository.saveAll(books);
    }

    public List<Book> findAll() {
        List<Book> books = (List<Book>) repository.findAll();
        return books;
    } 

    public Book findById(long id) {
        Optional<Book> book = repository.findById(id);
        return book.get();
    }

    public List<Book> findByAuthor(String author) {
        List<Book> books = (List<Book>) repository.findByAuthor(author);
        return books;
    }

    public List<Book> findBySeriesName(String seriesName) {
        List<Book> books = (List<Book>) repository.findBySeriesName(seriesName);
        return books;
    }

    public Book findByAuthorAndTitle(String author, String title) {
        Book book = repository.findByAuthorAndTitle(author,title);
        return book;
    }
/*
    public Book findByAuthorTitle(String author, String title) {
        Book book = repository.findByAuthorTitle(author,title);
        return book;
    }
*/
    public List<Book> retrieveByTag(String tag) {
        List<Book> books = (List<Book>) repository.retrieveByTag(tag);
        return books;
    }

    public List<Book> findByNameEndsWith(String chars) {
        List<Book> books = (List<Book>) repository.findByNameEndsWith(chars);
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

    public void addTagList(Long id, List<String> addTagList) {
        for (String tagString : addTagList) {
            if (!tagString.isEmpty()) {
                this.addTag(id, tagString);
            }
        }
    }

    public void delTagList(Long id, List<String> delTagList) {
        for (String tagString : delTagList) {
            if (!tagString.isEmpty()) {
                this.delTag(id, tagString);
            }
        }
    }

    public void addTag(Long id, String strTag) {
        Book book = repository.findById(id).get();

        // use existing tag if exists
        Tag tag = tagService.findByValue(strTag);
        if ( tag == null) {
            tag = new Tag(strTag);
        }

        book.addTag(tag);
        repository.save(book);
    }

    public void delTag(Long id, String strTag) {
        Book book = repository.findById(id).get();

        // Dont try and remove non-existent tags?
        Tag tag = tagService.findByValue(strTag);
        if ( tag != null) {
            book.removeTag(tag);
            repository.save(book);
        }
    }

    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    /*  TODO: do i need these?
    public void addTag(Long id, Tag tag) {
        Book book = repository.findById(id).get();

        // first see if tag exists for book, TODO: can i have a Tag table with unique tags?
        // do i need a TagRepository and TagService?

        //Tag tag = new Tag(_tag);

        book.addTag(tag);
        repository.save(book);
    }

    public void delTag(Long id, Tag tag) {
        Book book = repository.findById(id).get();

        // first see if tag exists for book, TODO: can i have a Tag table with unique tags?
        // do i need a TagRepository and TagService?

        //Tag tag = new Tag(_tag);

        book.removeTag(tag);
        repository.save(book);
    }
    */
}