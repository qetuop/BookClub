package com.qetuop.bookclub;


import java.util.*;
import java.util.regex.Pattern;

import com.qetuop.bookclub.model.Tag;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.qetuop.bookclub.service.IStorageService;
import com.qetuop.bookclub.service.BookService;
import com.qetuop.bookclub.model.Book;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Scanner {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public IStorageService storageService;  // this will be used once I start scanning the dirs over the network and not local
    public BookService bookService;  // TODO: replace with BookService?

    public FileList fileList = new FileList();

    public Scanner(IStorageService storageService, BookService bookService) {
        this.storageService = storageService;
        this.bookService = bookService;
    }

    public void scan() {
        // TODO: this is temporary
        storageService.deleteAll();
        storageService.init();

        // TODO: figure out if i should include trailing slash or not, it affects the split below, just be consistent
        String rootDir = "/home/brian/Projects/testdir/audio books/";
        //rootDir = "/media/NAS/audiobooks/";

        //ROOT DIR: /home/brian/Projects/testdir/audio books/
        //file dir:/home/brian/Projects/testdir/audio books
/*
        Configurations configs = new Configurations();
        InputStream inputStream = null;
        try
        {
            Properties prop = new Properties();
            inputStream = getClass().getClassLoader().getResourceAsStream("bookclub.properties");
            prop.load(inputStream);

            //Configuration config = configs.properties(new File("bookclub.properties"));
            // access configuration properties
            //rootDir = config.getString("rootDir");
            rootDir = prop.getProperty("rootDir");

        }catch (FileNotFoundException ex) {
            System.err.println("Property file '"  + "' not found in the classpath");
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
*/
        System.out.println("ROOT DIR: " + rootDir);

        List<Path> fileWithName = fileList.printFiles(rootDir);

        // keep a map of hash(author/series/title):book id for quicker lookup during operations below - worth it?
        // link to the book object itself then add all at once to repo?  TODO: do that
        HashMap<Integer, Long> bookMap = new HashMap<Integer, Long>();

        List<String> ignoreList = new ArrayList<String>(Arrays.asList("#recycle", "_temp", "_organize", "Warhammer", "_cleanup"));


        for (Path filePath : fileWithName) {
            System.out.println("------New file: " + filePath);

            String fullPathString = filePath.toString().strip();

            String tmpString = fullPathString.replace(rootDir, "");
            String pattern = Pattern.quote(System.getProperty("file.separator"));
            String[] splitList = tmpString.split(pattern);

            System.out.println("\t\t*" + StringUtils.join(splitList, "|") + "*");

            // standalone book
            // |Brian Herbert|Paul of Dune|Paul of Dune.jpg

            // series
            // |Frank Herbert|Dune|Dune - 1|Dune.jpg

            String author = null;
            String title = null;
            String path = null; // rootDir + Author + series + title  or fullPath - last element?
            String cover = null; // save largest image == replace if necessary
            Byte[] image = null;
            String seriesName = null;
            float seriesNumber = 0;

            author = splitList[0];

            //System.out.println("AUTHOR: " + author + "," + splitList[1]);

            // HACK check - ignore these dirs TODO: fix this
            if (ignoreList.contains(author)) {
                continue;
            }

            // single book
            if (splitList.length == 3) {
                title = splitList[1];
                path = rootDir + "/" + author + "/" + title;
            }
            // series
            else if (splitList.length == 4) {

                seriesName = splitList[1];
                String titleFull = splitList[2];


                // TODO: can i just split on spaces, if there is a number last wether preceded by a - or not, the re join everyting not a -
                if (titleFull.matches(".*-.*")) {

                    // TODO: once i've properly named dir/files - may need to handle anyways
                    String[] splitTitle = titleFull.split("-");

                    //System.out.println("\t\t" + StringUtils.join(splitTitle, "|"));
                    title = splitTitle[0].strip();

                    try {
                        seriesNumber = Float.parseFloat(splitTitle[1].strip());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }


                } else if (titleFull.matches(".*\\d.*")) {
                    String[] splitTitle = titleFull.split(" ");
                    String seriesNumberStr = splitTitle[splitTitle.length - 1];
                    try {
                        seriesNumber = Float.parseFloat(seriesNumberStr);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    //System.out.println("SERIES NUM: " + seriesNumber);
                    title = titleFull.substring(0, titleFull.indexOf(seriesNumberStr) - 1);
                }

                //System.out.println("\t\t" + seriesName + ":" + seriesNumber);


                path = rootDir + "/" + author + "/" + seriesName + " - " + seriesNumber + "/" + title;
            } else {
                System.out.println("somthings wrong");
            }

            cover = fullPathString;


            final File file = new File(fullPathString);

            FileInputStream input = null;
            try {
                input = new FileInputStream(file);
                image = ArrayUtils.toObject(IOUtils.toByteArray(input));


                String[] strings = {author, seriesName, title};
                int hashCode = Arrays.hashCode(strings);
                //System.out.println(hashCode);

                Book book = null;

                // set the cover to the largest image found in the dir.  TODO: is this the best thing to do?
                if (bookMap.containsKey(hashCode)) {

                    book = bookService.findById(bookMap.get(hashCode));

                    if (book != null) {


                        if (book.getImage().length < image.length) {
                            book.setImage(image);
                        }
                    }
                } else {
                    // Book(String title, String author, String path, String cover, Byte[] image, String seriesName, int seriesNumber)
                    book = new Book(title, author, path, cover, image, seriesName, seriesNumber, false);
                }

                book = bookService.save(book);
                bookMap.put(hashCode, book.getId());


            } catch (final FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //File file = new File(fullPathString);
            //String simpleFileName = file.getName();
            //System.out.println("\t"+simpleFileName);

        } // for each path


        // TAG TEST
        Set<Tag> tags = new HashSet<>();

        Book book = null;

        book = bookService.findById(1);
        //tags = new HashSet<>();
        book.addTag(new Tag("SciFi"));
        System.out.println("BOOK CHECK1: " + book.getTags());
        //book.setTags(tags);
        book = bookService.save(book);
        System.out.println("BOOK CHECK2: " + book.getTags());

        book = bookService.findById(2);
        //tags = new HashSet<>();
        book.addTag(new Tag("Fantasy"));
        //book.setTags(tags);
        book = bookService.save(book);

        book = bookService.findById(3);
        //tags = new HashSet<>();
        book.addTag(new Tag("SciFi"));
        book.addTag(new Tag("Fantasy"));
        //book.setTags(tags);
        book = bookService.save(book);

        System.out.println("\n\n--------------------------------------\n\n");

        System.out.println("TEST tags");
        book = bookService.findById(1);

        tags = book.getTags();
        System.out.println("Result len: "+tags.size());

        for (Tag tag : tags ) {
            System.out.println("TAG: " + tag.getId() + ":" + tag.getValue());
        }


        System.out.println("\n\n--------------------------------------\n\n");

        System.out.println("TYRING TO FIND TAG");
        List<Book> books = bookService.retrieveByTag("Fantasy");
        //List<Book> books = bookService.retrieveByTag("Dune");
        //List<Book> books = bookService.test();
        System.out.println("Result len: "+books.size());
        for (Book book1 : books) {
            System.out.println("book1: " + book1.getTitle());
        }
/*
        List<Book> books = bookService.findByNameEndsWith("ne");
        System.out.println("Result len: "+books.size());
        for (Book book1 : books) {
            System.out.println("book1: " + book1.getTitle());
        }
*/
        //EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.baeldung.movie_catalog");



    } // scan
} // class Scanner
