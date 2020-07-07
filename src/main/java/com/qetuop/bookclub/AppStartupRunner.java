package com.qetuop.bookclub;

import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.qetuop.bookclub.storage.StorageService;
import com.qetuop.bookclub.repository.BookRepository;
import com.qetuop.bookclub.model.Book;

@Component
public class AppStartupRunner implements ApplicationRunner {
    private static final Logger LOG = LoggerFactory.getLogger(AppStartupRunner.class);
    public static int counter;

    @Autowired
    public  StorageService storageService;
    public  BookRepository repository;

    public  AppStartupRunner(StorageService storageService, BookRepository repository) {
        this.storageService = storageService;
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOG.info("Application started with option names : {}", args.getOptionNames());
        LOG.info("Increment counter");
        
        
        final String[][] data = { 
            { "Dune", "Frank Herbert", "audio books/Frank Herbert/Dune/Dune - 1/", "Dune.jpg" },
            { "Dune Messiah", "Frank Herbert", "audio books/Frank Herbert/Dune/Dune Messiah - 2/", "Dune Messiah-Cover.jpg" } };

        final String rootDir = "/home/brian/Projects/BookClub/testdir/";

        storageService.deleteAll();
        storageService.init();

        Stream.of(data).forEach(array -> {
          
            final String filename = StringUtils.cleanPath(rootDir + array[2] + array[3]);

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
    }
}