package com.qetuop.bookclub;


import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Pattern;

import com.qetuop.bookclub.model.Tag;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.stream.Collectors;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.qetuop.bookclub.service.IStorageService;
import com.qetuop.bookclub.service.BookService;
import com.qetuop.bookclub.model.Book;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

public class Scanner {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final List<String> ignoreList = new ArrayList<>(Arrays.asList("@eaDir", "#recycle", "_temp", "_organize", "Warhammer", "_cleanup"));

    @Autowired
    public IStorageService storageService;  // this will be used once I start scanning the dirs over the network and not local
    public BookService bookService;  // TODO: replace with BookService?

    public Scanner(IStorageService storageService, BookService bookService) {
        this.storageService = storageService;
        this.bookService = bookService;
    }

    private Path findLargestFile(List<Path> filePaths) {
        Path largestFile = null; // may be null
        long largestFileSize = 0;
        for (Path filePath : filePaths) {
            try {
                long tmp = FileChannel.open(filePath).size();
                if (tmp > largestFileSize) {
                    largestFileSize = tmp;
                    largestFile = filePath;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return largestFile;
    }

    // TODO: hacked in these args, don't need to pass them all
    // input should be a non-empty directory - contains files not subdirs
    // TODO: pass in book type expected?
    public void parsePath(List<Path> filePaths, String rootDir) {

        // find largest image file, store for cover
        List<Path> images = filePaths.stream()
                .filter(s -> s.toString().toLowerCase().endsWith(".jpg"))
                .collect(Collectors.toList());
        Path coverImage = findLargestFile(images);

        // get one file to parse out most of the book info (contained in dir path)
        Path filePath = filePaths.get(0);
        System.out.println("parsePath::" + filePath);

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
        //Book.Type bookType = Book.Type.audio;

        author = splitList[0];

        //System.out.println("AUTHOR: " + author + "," + splitList[1]);

        // HACK check - ignore these dirs TODO: fix this
        if (ignoreList.contains(author)) {
            return;
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
                    System.out.println(e.toString());
                }


            } else if (titleFull.matches(".*\\d.*")) {
                String[] splitTitle = titleFull.split(" ");
                String seriesNumberStr = splitTitle[splitTitle.length - 1];
                try {
                    seriesNumber = Float.parseFloat(seriesNumberStr);
                } catch (NumberFormatException e) {
                    System.out.println(e.toString());
                }
                //System.out.println("SERIES NUM: " + seriesNumber);
                title = titleFull.substring(0, titleFull.indexOf(seriesNumberStr) - 1);
            }

            //System.out.println("\t\t" + seriesName + ":" + seriesNumber);


            path = rootDir + "/" + author + "/" + seriesName + " - " + seriesNumber + "/" + title;
        } else {
            System.out.println("\t\tCan't parse this path");
            return;
        }

        //cover = fullPathString;
        if (coverImage != null) {
            cover = coverImage.toString();
        }

        try {
            if (cover != null) {
                FileInputStream input = new FileInputStream(new File(cover));
                image = ArrayUtils.toObject(IOUtils.toByteArray(input));
            }

            Book book = new Book(title, author, path, cover, image, seriesName, seriesNumber, false);

            // HACK
            book.setBookType(Book.Type.audio);

            book = bookService.save(book);
            System.out.println("\t\tNew book: " + book.getTitle() + ", " + book.getSeriesName() + ", " + book.getSeriesNumber());
            //bookMap.put(hashCode, book.getId());


        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            System.out.println(e);
            //LOG.error(e.toString());
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void scan() {
        // TODO: this is temporary
        storageService.deleteAll();
        storageService.init();

        // TODO: figure out if i should include trailing slash or not, it affects the split below, just be consistent
        final String rootDir = "/home/brian/Projects/testdir/audio books/";
        //final String rootDir = "/home/brian/Projects/testdir/fake_audio_books/";
        //final String rootDir = "/home/brian/Projects/testdir/test/";
        //final String rootDir = "/media/NAS/audiobooks/";
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

        // walk through all dirs, handle valid books
        try {
            Files.walkFileTree(Paths.get(rootDir), new SimpleFileVisitor<Path>() {

                // TODO: add a config option to control dirs to ignore

                // This will skip visiting a dir listed in the ignore list.
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (ignoreList.contains(dir.getFileName().toString())) {
                        return SKIP_SUBTREE;
                    }
                    return CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    // create list of files in dir to process
                    // TODO: filter based on expected type (mp3, jpg, epub)
                      try {
                        List<Path> files = Files.list(dir)
                                .filter(p -> !p.toFile().isDirectory())
                                .collect(Collectors.toList());

                        if ( !files.isEmpty() ) {
                            parsePath(files, rootDir);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        // TAG TEST
        Book book = null;


        book = bookService.findById(2);
        book.addTag(new Tag("SciFi"));
        book = bookService.save(book);

        book = bookService.findById(3);
        book.addTag(new Tag("Fantasy"));
        book = bookService.save(book);

        book = bookService.findById(4);
        book.addTag(new Tag("SciFi"));
        book.addTag(new Tag("Fantasy"));
        book = bookService.save(book);
        

        System.out.println("\n\n--------------------------------------\n\n");
        System.out.println("TEST tags");
        book = bookService.findById(2);

        Set<Tag> tags = book.getTags();
        System.out.println("Result len: "+tags.size());

        for (Tag tag : tags ) {
            System.out.println("TAG: " + tag.getId() + ":" + tag.getValue());
        }
        System.out.println("\n\n--------------------------------------\n\n");

        System.out.println("TYRING TO FIND TAG");
        List<Book> books = bookService.retrieveByTag("Fantasy");
        System.out.println("Result len: "+books.size());
        for (Book book1 : books) {
            System.out.println("book1: " + book1.getTitle());
        }*/

    } // scan
} // class Scanner
