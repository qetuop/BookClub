package com.qetuop.bookclub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qetuop.bookclub.model.Book;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface BookRepository extends CrudRepository<Book, Long> {
    List<Book> findByAuthor(String author);
    List<Book> findBySeriesName(String seriesName);

    //@Query("SELECT s FROM Book s JOIN s.tags t WHERE t = LOWER(:tag)")

    //@Query("SELECT s FROM Student s JOIN s.skillTags t WHERE t.name = LOWER(:tagName) AND t.value > :tagValue")
    //List<Student> retrieveByNameFilterByMinimumSkillTag(@Param("tagName") String tagName, @Param("tagValue") int tagValue);

    //"select distinct a from Article a join a.tags t where t.name in (:tags)";

    //@Query("SELECT b FROM Book b LEFT JOIN FETCH b.tags t WHERE t.value = value")

    //@Query("SELECT b FROM Post b JOIN FETCH b.tags bt JOIN fetch bt.value WHERE b.id = :postId")

    //@Query("SELECT b FROM Book b JOIN b.tags t WHERE t.value = (:tag)")

    //@Query("select us.priceAlertsTapas.tapa from User us left join us.priceAlertsTapas  pat left join pat.tapa tapa where pat.priceAlert = ?1")
    //@Query("select pat.tapa from User us join us.priceAlertsTapas pat")

    //@Query("SELECT b FROM Book JOIN Book_Tag ON Book.id = Book_Tag.book_id JOIN Tag ON Book_Tag.tag_id = Tag.id WHERE Tag.value = (:tag)")
    //@Query("SELECT b FROM Book JOIN Book_Tag ON Book.id = Book_Tag.book_id")

    //@Query("SELECT  b FROM Book b WHERE b.title = (:tag)") // works
    //@Query("SELECT b  FROM Book b  JOIN b.tags t WHERE t.value = :tag") // runs but returns no results
    //@Query("SELECT b  FROM Book b  JOIN b.tags t WHERE t.value = 'SciFi'") // runs but returns no results
    @Query("SELECT b FROM Book b JOIN b.tags t WHERE t.value = :tag") // works
    List<Book> retrieveByTag(@Param("tag") String tag);

    // "SELECT DISTINCT e FROM Employee e INNER JOIN e.tasks t where t.supervisor='Denise'");


    //@Query("SELECT u FROM User u JOIN u.addresses a WHERE u.id = a.user")
    //@Query("SELECT b FROM Book b JOIN b.id bt WHERE b.id = bt.book_id")
    //List<Book> test();

    //SELECT * FROM tracks JOIN albums ON tracks.album_id = albums.id
    //JOIN artists ON albums.artist_id = artists.id;




    //      createQuery("SELECT ph FROM Employee e JOIN e.phones ph WHERE ph LIKE '1%'", Phone.class);


    //@Query("SELECT b FROM Book b JOIN b.tags t WHERE t.value = (:tag)")
    //Book retrieveByTag1(@Param("tag") String tag);

    @Query("select b from Book b where b.title like %?1")
    List<Book> findByNameEndsWith(String chars);

    //@Transactional
    //@Query("SELECT p FROM Post p JOIN FETCH p.tags")
    //List<post> findAllPostWithTags();

    // TODO: adding @Transactional fixed the error
    // org.springframework.orm.jpa.JpaSystemException: Unable to access lob stream
    // when calling findByAuthor.  why?!?

    @Query("SELECT b FROM Book b WHERE b.author = :author AND b.title = :title")
    Book findByAuthorAndTitle(@Param("author") String author, @Param("title") String title );

}