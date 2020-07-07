package com.qetuop.bookclub;

import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.net.URLDecoder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.qetuop.bookclub.storage.StorageProperties;
import com.qetuop.bookclub.storage.StorageService;
import com.qetuop.bookclub.repository.BookRepository;
import com.qetuop.bookclub.model.Book;

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

     /*
    @Bean
    CommandLineRunner init(final StorageService storageService, final BookRepository repository) {
        final String[][] data = { { "Dune", "Frank Herbert", "audio books/Frank Herbert/Dune/Dune - 1/", "Dune.jpg" },
                { "Dune Messiah", "Frank Herbert", "audio books/Frank Herbert/Dune/Dune Messiah - 2/",
                        "Dune Messiah-Cover.jpg" } };

        final String rootDir = "/home/brian/Projects/BookClub/testdir/";

        return (args) -> {
            storageService.deleteAll();
            storageService.init();

            Stream.of(data).forEach(array -> {

                final String filename = StringUtils.cleanPath(rootDir + array[2] + array[3]);
                final String filename2 = null;
              
                System.out.println(filename);
                
                final File file = new File(filename);

                FileInputStream input = null;
                try {
                    input = new FileInputStream(file);
                } catch (final FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // MultipartFile multipartFile = new MockMultipartFile("file",file.getName(),
                // "text/plain", IOUtils.toByteArray(input));

                Byte[] image = null;
                try {
                    image = ArrayUtils.toObject(IOUtils.toByteArray(input));
                } catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                    final Book book = new Book(
                        array[0],
                        array[1],
                        array[2],
                        array[3],
                        image
                    );
                    repository.save(book);
    
            });
        };
    }
    */
     /*
    @Bean
    public ApplicationRunner init(BookRepository repository) {
        FileList fl = new FileList();
        //fl.printFiles();

        String[][] data = {
            {"Dune", "Frank Herbert", "audio books/Frank Herbert/Dune - 1", "Dune Messiah-Cover.jpg"},
            {"Dune Messiah", "Frank Herbert", "audio books/Frank Herbert/Dune Messiah - 2", ""},
            {"The Opal Affiar", "Eoin Coffer", "foo", "bar"}
        };
        
        return args -> {
        Stream.of(data).forEach(array -> {
        
            Book book = new Book(
                array[0],
                array[1],
                array[2],
                array[3]
            );
            repository.save(book);
 
        });
        repository.findAll().forEach(System.out::println);
        };
    }
    */
    
  
      
}
