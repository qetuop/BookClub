package com.qetuop.bookclub.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

// import lombok.Data;  add @Data before class to get auto generated getter, setters, etc https://projectlombok.org/features/Data
// import lombok.NoArgsConstructor;


@Entity // This tells Hibernate to make a table out of this class, use @Table(name = "books") to specify the table name
public class Book {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String title;
    private String author;

    // needed for JPA
    protected Book() {}

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    @Override
    public String toString() {
        return String.format("Book[id=%d, title='%s', author='%s']",
        id, title, author);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }
    
}