package com.qetuop.bookclub;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Scanner;

import com.qetuop.bookclub.model.Book;
import com.qetuop.bookclub.repository.BookRepository;
import com.qetuop.bookclub.service.BookService;
import com.qetuop.bookclub.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class BackRestore {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String filename = "book_club_backup.csv";

    @Autowired
    private final BookService bookService;



    public BackRestore(BookService bookService) {
        this.bookService = bookService;
    }

    public void backup() {
        System.out.println("BackRestore::backup");

        List<Book> books = (List<Book>) bookService.findAll();

        try {

            FileWriter myWriter = new FileWriter(filename);

            final String bookString = "\"%s\", \"%s\", \"%s\", \"%s\", \"%s\"\n";

            myWriter.write(String.format(bookString,
                    "author",
                    "title",
                    "series_name",
                    "series_number",
                    "read" ));

            for (Book book : books ) {
                // TODO: format the series num correctly  5.0 -> 5
                // null -> blank
                myWriter.write(String.format(bookString,
                        book.getAuthor(),
                        book.getTitle(),
                        book.getSeriesName(),
                        book.getSeriesNumber(),
                        book.getRead() ));
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void restore() {
        System.out.println("BackRestore::restore");
        // read each line

        // search for existing book based on....title/author

        // update *only* the....read flag (add more later)
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
