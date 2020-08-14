package com.qetuop.bookclub.model;

import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;  //add @Data before class to get auto generated getter, setters, etc https://projectlombok.org/features/Data
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Data
@Entity // This tells Hibernate to make a table out of this class, use @Table(name = "book") to specify the table name
//@Table(name = "book")
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
    private Boolean read;
    private LocalDateTime updated;  // time this book, TODO: split into creationTime and modificationTime?  use ZonedDateTime?

    @Lob
    private Byte[] image;


    // TODO: I'm not sure if using the EAGER fetch is right, default is lazy, it fixes the error
    // org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: com.qetuop.bookclub.model.Book.tags, could not initialize proxy - no Session
    // i think related to the addTag/removeTag's use of the tag object?
    // Add the two Excludes else you'll get a circular reference/stack overflow...i think
    // I think the @joinTable is only needed if you want to change the default behavoir, in this case the below should
    // be auto create by just using the ManyToMany....i think
    @ManyToMany(cascade = { CascadeType.ALL }, fetch=FetchType.EAGER)
    @JoinTable(
            name = "Book_Tag",
            joinColumns = { @JoinColumn(name = "book_id") },
            inverseJoinColumns = { @JoinColumn(name = "tag_id") }
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Set<Tag> tags = new HashSet<>();

    // needed for JPA, NoArgsConstructor created by lombok
    protected Book() {}

    public Book(String title, String author, String path, String cover, Byte[] image, String seriesName, float seriesNumber, boolean read) {
        this.title = title;
        this.author = author;
        this.path = path;
        this.cover = cover;
        this.image = image.clone();// TODO: what to set to
        this.seriesName = seriesName;
        this.seriesNumber = seriesNumber;
        this.read = read;
    }

    public void addTag(Tag tag) {
        this.getTags().add(tag);
        tag.getBooks().add(this);
    }

    public void removeTag(Tag tag) {
        this.getTags().remove(tag);
        tag.getBooks().remove(this);
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