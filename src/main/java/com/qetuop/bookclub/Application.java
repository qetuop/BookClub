package com.qetuop.bookclub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.qetuop.bookclub.service.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Application {
    //AppStartupRunner asr = new AppStartupRunner();

    // private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(final String[] args) {
        log.info("\n\n*** Starting Application...\n\n");

        SpringApplication.run(Application.class, args);

        log.info("\n\n*** Application Done!\n\n");
    }


    /*
     * @Bean public CommandLineRunner demo(BookRepository repository) { return
     * (args) -> { // save a few customers repository.save(new Book("24",
     * "Jack Bauer")); repository.save(new Book("The Stand", "Chloe O'Brian"));
     * repository.save(new Book("Kim", "Bauer")); repository.save(new Book("David",
     * "Palmer")); repository.save(new Book("Michelle", "Dessler"));
     * 
     * // update one book List<Book> books = repository.findByAuthor("Jack Bauer");
     * System.out.println("books size:" + books.size()); //book.setTitle("24b");
     * //repository.save(book);
     * 
     * // fetch all customers log.info("Books found with findAll():");
     * log.info("-------------------------------"); for (Book book :
     * repository.findAll()) { log.info(book.toString()); } log.info("");
     * 
     * // fetch an individual book by ID Book book = repository.findById(1L);
     * log.info("Book found with findById(1L):");
     * log.info("--------------------------------"); log.info(book.toString());
     * log.info("");
     * 
     * // fetch books by last name
     * log.info("Book found with findByAuthor('Jack Bauer'):");
     * log.info("--------------------------------------------");
     * repository.findByAuthor("Jack Bauer").forEach(author -> {
     * log.info(author.toString()); }); log.info(""); }; }
     */
    
} // Application
