package com.qetuop.bookclub.model;

import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;  //add @Data before class to get auto generated getter, setters, etc https://projectlombok.org/features/Data
import lombok.NoArgsConstructor;

@Data
@Entity // This tells Hibernate to make a table out of this class, use @Table(name = "books") to specify the table name
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String title;
    private String author;
    private String path;
    private String cover;
    //private Boolean isSeries;
    private String seriesName;
    private Float seriesNumber;

    @Lob
    private Byte[] image;

    // needed for JPA, NoArgsConstructor created by lombok
    protected Book() {}

    public Book(String title, String author, String path, String cover, Byte[] image, String seriesName, float seriesNumber) {
        this.title = title;
        this.author = author;
        this.path = path;
        this.cover = cover;
        this.image = image.clone();// TODO: what to set to
        this.seriesName = seriesName;
        this.seriesNumber = seriesNumber;
    }

    // getters/setters/hash/toString created by lombok
    /*
    ENSURE "annotationProcessor 'org.projectlombok:lombok'"" is in gradle else stuff doesn't work!
    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }
    */
}