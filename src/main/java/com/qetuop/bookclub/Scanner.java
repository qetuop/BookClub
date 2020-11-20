package com.qetuop.bookclub;


import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

//import com.google.common.io.Resources;

import com.qetuop.bookclub.model.Config;
import com.qetuop.bookclub.service.ConfigService;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.invoke.MethodHandles;
import java.util.stream.Collectors;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.qetuop.bookclub.service.StorageService;
import com.qetuop.bookclub.service.BookService;
import com.qetuop.bookclub.model.Book;

import com.qetuop.bookclub.service.TagService;
import com.qetuop.bookclub.model.Tag;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resources;

import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

@Component
public class Scanner {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final List<String> ignoreList = new ArrayList<>(Arrays.asList("@eaDir", "#recycle", "_temp", "_organize", "_cleanup"));

    @Autowired
    private StorageService storageService;  // this will be used once I start scanning the dirs over the network and not local
    @Autowired
    private BookService bookService;
    @Autowired
    private TagService tagService;
    @Autowired
    private ConfigService configService;

/*    public Scanner(StorageService storageService, BookService bookService, TagService tagService) {
        this.storageService = storageService;
        this.bookService = bookService;
        this.tagService = tagService;
        //this.configService = configService;
    }*/

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
        System.out.println("tmpString:*"+tmpString+"*");
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] splitList = tmpString.split(pattern);

        System.out.println("splitList.length: " + splitList.length);
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

        // HACK check - ignore these dirs TODO: fix this
        if (ignoreList.contains(author)) {
            return;
        }

        // single book
        if (splitList.length == 3) {
            System.out.println("SINGLE BOOK");
            title = splitList[1];
            path = rootDir + "/" + author + "/" + title;
        }
        // series
        else if (splitList.length == 4) {
            System.out.println("SERIES");
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
                    title = titleFull.substring(0, titleFull.indexOf(seriesNumberStr) - 1);
                } catch (NumberFormatException e) {
                    System.out.println(e.toString());
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
                path = rootDir + "/" + author + "/" + seriesName + " - " + seriesNumber + "/" + title;
            } else {
                System.out.println("\t\tCan't parse this path");
                return;
            }
        } // series

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

                Book tmpBook = bookService.findByAuthorAndTitle(author, title);
                if (tmpBook != null) {
                    System.out.println(String.format("BOOK %s %s is already in DB", title, author));
                } else {
                    book = bookService.save(book);
                    System.out.println("\t\tNew book: " + book.getAuthor() + ", " + book.getTitle() + ", " + book.getSeriesName() + ", " + book.getSeriesNumber());
                }
            } catch (final FileNotFoundException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
                System.out.println(e);
                //LOG.error(e.toString());
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

    } // parsePath

    public void scan(boolean forceScan) {
        System.out.println("Scanner:Scan() " + forceScan);
        // TODO: this is temporary
        //storageService.deleteAll();
        //storageService.init();

        Config config = configService.findById(1);
        String rootDir = config.getAudioRootDir();
        Long lastScan = config.getLastScanTime();

        System.out.println("ROOT DIR: " + rootDir);
        System.out.println(lastScan + " : " + LocalDateTime.ofInstant(Instant.ofEpochMilli(lastScan), ZoneId.systemDefault()));

        // walk through all dirs/files, handle valid books
        try {
            Files.walkFileTree(Paths.get(rootDir), EnumSet.of(FOLLOW_LINKS), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {

                // TODO: add a config option to control dirs to ignore

                // This will skip visiting a dir listed in the ignore list.
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    System.out.println(dir.toFile().lastModified() + " : " + dir.toFile().toString() );

                    if (ignoreList.contains(dir.getFileName().toString())) {
                        System.out.println(String.format("DIR %s is in ignore list", dir.getFileName()));
                        return SKIP_SUBTREE;
                    }

                    if ( dir.getFileName().toString().startsWith("_") ) {
                        System.out.println(String.format("Dir %s has an ignore pattern", dir.getFileName()));
                        return SKIP_SUBTREE;
                    }
                    return CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    // create list of files in dir to process
                    // TODO: filter based on expected type (mp3, jpg, epub)

                    //Date start = new Date();
                    //attrs.creationTime().toInstant().isAfter(start.toInstant())

                    //The mtime (modification time) on the directory itself changes when a file or a subdirectory is added, removed or renamed.

                    //Modifying the contents of a file within the directory does not change the directory itself, nor does updating the modified
                    //times of a file or a subdirectory. If you change the permissions on the directory, the ctime changes but the mtime does not.

                    // skip *files* whose mod date is < the last scan date
                    // the dir mod date does NOT update if sub file has updated  TODO: check this for windows (all linux FS>)

                    if ( !forceScan ) {
                        if (dir.toFile().lastModified() < lastScan) {
                            System.out.println("TOO OLD: " + dir.toFile().lastModified() + " : " + Instant.ofEpochMilli(dir.toFile().lastModified()));
                            return CONTINUE; // don't skip subtree
                        }
                    }


                    // scan this dir, add files that:
                    // are not dirs
                    try {
                        //Files.list(dir).forEach(c -> System.out.println(c.toFile().lastModified()) );

                        List<Path> files = Files.list(dir)
                                .filter(p -> !p.toFile().isDirectory())
                                .collect(Collectors.toList());

                        // do mod date check at dir level so all files get added
                        //.filter(p -> p.toFile().lastModified() > lastMod.toEpochMilli())
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

        config.setLastScanTime(Instant.now().toEpochMilli());
        configService.save(config);

        System.out.println("Scanner:Scan():complete!");


        /*// TAG TEST
        Book book = null;
        Tag fantTag = tagService.findByValue("Fantasy");
        Tag sciTag = tagService.findByValue("SciFi");
        if ( fantTag == null ) {
            fantTag = new Tag("Fantasy");
        }
        if ( sciTag == null ) {
            sciTag = new Tag("SciFi");
        }

        book = bookService.findById(2);
        book.addTag(sciTag);
        book = bookService.save(book);

        book = bookService.findById(3);
        book.addTag(fantTag);
        book = bookService.save(book);

        book = bookService.findById(4);
        book.addTag(fantTag);
        book.addTag(sciTag);
        book = bookService.save(book);
        

        System.out.println("\n\n--------------------------------------\n\n");
        System.out.println("TEST tags");
        book = bookService.findById(2);

        Set<Tag> tags = book.getTags();
        System.out.println("Result len: "+tags.size());
        for (Tag tag0 : tags) {
            System.out.println("TAG: " + tag0.getId() + ":" + tag0.getValue());
        }

        System.out.println("\n\n--------------------------------------\n\n");

        System.out.println("TYRING TO FIND TAG");
        List<Book> books = bookService.retrieveByTag("Fantasy");
        System.out.println("Result len: "+books.size());
        for (Book book1 : books) {
            System.out.println("book1: " + book1.getTitle());
        }
        System.out.println("\n\n--------------------------------------\n\n");
        System.out.println("PRINT ALL TAGS");
        List<Tag> allTags = tagService.findAll();
        for ( Tag tag2 : allTags ) {
            System.out.println("Tag(" + tag2.getId() + "): " + tag2.getValue());
        }*/

    } // scan
} // class Scanner
