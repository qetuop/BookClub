package com.qetuop.bookclub.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.qetuop.bookclub.model.Book;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface BookRepository extends CrudRepository<Book, Long> {
    List<Book> findByAuthor(String author);
    List<Book> findBySeriesName(String seriesName);

    // TODO: adding @Transactional fixed the error
    // org.springframework.orm.jpa.JpaSystemException: Unable to access lob stream
    // when calling findByAuthor.  why?!?


}