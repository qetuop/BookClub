package com.qetuop.bookclub;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.qetuop.bookclub.model.Book;
import com.qetuop.bookclub.model.Tag;
import com.qetuop.bookclub.service.BookService;
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
        books.sort(Comparator.comparing(Book::getAuthor));

        try {

            FileWriter myWriter = new FileWriter(filename);

            final String bookString = "\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"\n";

            myWriter.write(String.format(bookString,
                    "author",
                    "title",
                    "series_name",
                    "series_number",
                    "read",
                    "tags",
                    "updated"));
//.map{ Bike b -> b.bikeModel }.toCollection(arrayListOf())
//            book.getTags().stream().map(Book::getValue).collect(Collectors.toList());
//            entities.stream().map(urEntity -> urEntity.getField1()).collect(Collectors.toList());

            for (Book book : books ) {
                // TODO: format the series num correctly  5.0 -> 5
                // null -> blank
                myWriter.write(String.format(bookString,
                        book.getAuthor(),
                        book.getTitle(),
                        book.getSeriesName() == null ? "" : book.getSeriesName(),
                        book.getSeriesNumber() == 0 ? "" : book.getSeriesNumber(),
                        book.getRead(),
                        book.getTags() == null ? "" : book.getTags().stream().map(Tag::getValue).collect(Collectors.joining(",")),
                        book.getUpdated()
                ));
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
