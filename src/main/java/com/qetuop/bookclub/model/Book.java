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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Data
@Entity // This tells Hibernate to make a table out of this class, use @Table(name = "book") to specify the table name
//@Table(name = "book")
public class Book {

    public enum Type {
        audio,
        ebook,
        paper
    }

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
    private Instant updated;  // time this book, TODO: split into creationTime and modificationTime?  use ZonedDateTime?
    private Type bookType;

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
            inverseJoinColumns = { @JoinColumn(name = "tag_id") },
            uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "tag_id"})
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
        if (image == null) {
            image = null;
        }
        else {
            this.image = image.clone();
        }
        this.seriesName = seriesName;
        this.seriesNumber = seriesNumber;
        this.read = read;

        // TODO: HACK - should i allow null or not?!?!
        // this will fail with null values books.sort(Comparator.comparing(Book::getAuthor).thenComparing(Book::getSeriesName)
        if ( this.title == null ) this.title = "";
        if ( this.seriesName == null ) this.seriesName = "";
        if ( this.seriesNumber == null ) this.seriesNumber = 0f;
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