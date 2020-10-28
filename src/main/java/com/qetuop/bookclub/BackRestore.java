package com.qetuop.bookclub;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.qetuop.bookclub.model.Book;
import com.qetuop.bookclub.model.Tag;
import com.qetuop.bookclub.service.BookService;
import com.qetuop.bookclub.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.invoke.MethodHandles;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;


public class BackRestore {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String filename = "book_club_backup.csv";

    @Autowired
    private final BookService bookService;
    private final TagService tagService;



    public BackRestore(BookService bookService, TagService tagService) {

        this.bookService = bookService;
        this.tagService = tagService;
    }

    public void backup() {
        System.out.println("BackRestore::backup");

        List<Book> books = (List<Book>) bookService.findAll();
        books.sort(Comparator.comparing(Book::getAuthor));

        try {

            FileWriter myWriter = new FileWriter(filename);

            final String bookString = "\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n";

            myWriter.write(String.format(bookString,
                    "author",
                    "title",
                    "series_name",
                    "series_number",
                    "read",
                    "tags",
                    "updated",
                    "type"
            ));
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
                        book.getUpdated(),
                        book.getBookType()
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

       /* // update *only* the....read flag (add more later)
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
        }*/

        try {
            //Reader reader = Files.newBufferedReader(Paths.get(ClassLoader.getSystemResource(filename).toURI()));
            System.out.println("Restoring: " + Paths.get(filename));
            Reader reader = Files.newBufferedReader(Paths.get(filename));

            List<String[]> list = new ArrayList<>();
            CSVReader csvReader = new CSVReader(reader);
            String[] line;

            // skip column headers
            csvReader.readNext();

            while ((line = csvReader.readNext()) != null) {
                //list.add(line);
                System.out.println(line);
                for (String token : line ) {
                    System.out.println(token);
                }
                String author = line[0];
                String title  = line[1];
                Boolean read  = Boolean.valueOf(line[4]);

                //String[] tagArr = line[5].split(",");
                // an empty string will return an array of 1 empty object TODO: how to handle as I encouter this in other places
                ArrayList<String> tagList = new ArrayList<String>(Arrays.asList(line[5].split(",")));
                System.out.println("TAGLIST: " + tagList.size() + "**" +  tagList.get(0) +"**");

                Book book = bookService.findByAuthorAndTitle(author,title);
                System.out.println("BackRestore:restore:book is null?: " + (book == null));
                if ( book != null ) {
                    System.out.println("BackRestore:restore:FOUND BOOK: " + book.getAuthor());
                    book.setRead(read);
                    if ( !tagList.isEmpty() ) {
                        for (String tagStr : tagList) {
                            if ( !tagStr.isEmpty() ) {
                                System.out.println("BackRestore:restore:add tag: " + tagStr);
                                Tag tag = tagService.findByValue(tagStr);
                                if ( tag == null) {
                                    tag = new Tag(tagStr);
                                }
                                //book.addTag(new Tag(tagStr)); // TODO: check if tag exists in DB?  crashes, unique constraint
                                bookService.addTag(book.getId(), tag);
                            }
                        }
                    }

                }

            }
            reader.close();
            csvReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        /*} catch (URISyntaxException e) {
            e.printStackTrace();*/
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }

    }
}
