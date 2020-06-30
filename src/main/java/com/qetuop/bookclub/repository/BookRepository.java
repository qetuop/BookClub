package com.qetuop.bookclub.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.qetuop.bookclub.model.Book;

//public interface BookRepository extends CrudRepository<Book, Long> {

// direct Spring MVC to create RESTful endpoints at '/books'. the defalut value is 'books' and this not needed
@RepositoryRestResource(collectionResourceRel = "books", path = "books") 
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {

    // orig CrudRepository way
  //List<Book> findByAuthor(String author);

  List<Book> findByAuthor(@Param("author") String author);

  Book findById(long id);
}