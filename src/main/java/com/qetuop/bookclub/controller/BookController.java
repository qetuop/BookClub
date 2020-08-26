package com.qetuop.bookclub.controller;

import java.util.*;

import com.qetuop.bookclub.BackRestore;
import com.qetuop.bookclub.model.Tag;
import com.qetuop.bookclub.repository.BookRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.qetuop.bookclub.model.Book;
import com.qetuop.bookclub.Scanner;

import org.json.JSONArray;

import java.util.stream.Collectors;


import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qetuop.bookclub.service.StorageFileNotFoundException;
import com.qetuop.bookclub.service.StorageService;
import com.qetuop.bookclub.service.BookService;
import com.qetuop.bookclub.service.TagService;

@Controller
public class BookController {

    private final StorageService storageService;
    private final BookService bookService;
    private final TagService tagService;

    // TODO: don't access repository directly but use service?
    //private final BookRepository bookRepository;

    private BackRestore backRestore;

    @Autowired
    public BookController(StorageService storageService, BookService bookService,TagService tagService) {
        this.storageService = storageService;
        this.bookService = bookService;
        //this.bookRepository = bookRepository;
        this.tagService = tagService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {
        System.out.println("HERE:GET/ listUploadedFiles");

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(BookController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        System.out.println("HERE:Post/ ");

        storageService.store(file);
        //storageService.storeDB(file);
        bookService.saveImageFile(1L, file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @GetMapping(path = "/books/{id}/cover")
    public void renderImageFromDB(@PathVariable String id, HttpServletResponse response) throws IOException {
        Book book = bookService.findById(Long.valueOf(id));

        //System.out.println("COVER ID:"+book.getSeriesName());

        if (book.getImage() != null) {
            byte[] byteArray = new byte[book.getImage().length];
            int i = 0;

            for (Byte wrappedByte : book.getImage()) {
                byteArray[i++] = wrappedByte; //auto unboxing
            }

            response.setContentType("image/jpeg");
            InputStream is = new ByteArrayInputStream(byteArray);
            IOUtils.copy(is, response.getOutputStream());
        }
    }


    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        System.out.println("HERE:serveFile " + filename);

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/image/{filename:.+}")
    @ResponseBody
    public byte[] getImage(@PathVariable String filename) throws IOException {
        System.out.println("HERE:getImage " + filename);

        Resource file = storageService.loadAsResource(filename);
        //InputStream in = getClass().getResourceAsStream(file.getInputStream());

        return IOUtils.toByteArray(file.getInputStream());
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/scan")
    public String scan() {
        System.out.println("HERE:POST scan/ ");
        Scanner scanner = new Scanner(storageService, bookService, tagService);
        scanner.scan();
        return "redirect:/";
    }

    @PostMapping("/backup")
    public String backup() {
        System.out.println("HERE:POST backup/ ");
        BackRestore backRestore = new BackRestore(bookService);
        backRestore.backup();
        return "redirect:/";
    }

    @PostMapping("/restore")
    public String restore() {
        System.out.println("HERE:POST restore/ ");
        BackRestore backRestore = new BackRestore(bookService);
        backRestore.restore();
        return "redirect:/";
    }

    @GetMapping("/showBookTable")
    public String showBookTable(Model model) {
        List<Book> books = (List<Book>) bookService.findAll();
//		for (Book book : books) {
//			System.out.println(book.getTitle() + ":" + book.getSeriesName());
//		}
        //books.sort(Comparator.comparing(Book::getTitle));
        books.sort(Comparator.comparing(Book::getAuthor).thenComparing(Book::getSeriesName).thenComparing(Book::getSeriesNumber));


        model.addAttribute("books", books);
        return "showBookTable";
    }

    @GetMapping("/showBooks")
    public String showBooks(Model model) {
        List<Book> books = (List<Book>) bookService.findAll();
        books.sort(Comparator.comparing(Book::getAuthor).thenComparing(Book::getSeriesName).thenComparing(Book::getSeriesNumber));
        model.addAttribute("books", books);
        return "showBooks";
    }

    @GetMapping("/showBooks/author/{author}")
    public String showBooksByAuthor(@PathVariable String author, Model model) {
        List<Book> books = (List<Book>) bookService.findByAuthor(author);
        model.addAttribute("books", books);
        return "showBooks";
    }

    @GetMapping("/showBooks/series/{seriesName}")
    public String showBooksBySeries(@PathVariable String seriesName, Model model) {
        List<Book> books = (List<Book>) bookService.findBySeriesName(seriesName);
        books.sort(Comparator.comparing(Book::getSeriesNumber));
        model.addAttribute("books", books);
        return "showBooks";
    }

    @GetMapping("/showSeries")
    public String showSeries(Model model) {

        List<Book> books = (List<Book>) bookService.findAll();

        HashMap<String, ArrayList<Book>> seriesMap = new HashMap<String, ArrayList<Book>>();

        for (Book book : books) {
            System.out.println(book.getSeriesName());
            String seriesName = book.getSeriesName();

            if (seriesName != null) {

                // add this book to the list, sort on series number
                if (seriesMap.containsKey(seriesName)) {
                    ArrayList<Book> bookList = seriesMap.get(seriesName);
                    bookList.add(book);
                    bookList.sort(Comparator.comparing(Book::getSeriesNumber));
                } else {
                    seriesMap.put(seriesName, new ArrayList<Book>(Arrays.asList(book)));
                }
            } // series book
        } // for each book

        model.addAttribute("seriesMap", seriesMap);
        return "showSeries";
    }

    @GetMapping(path = "/book/{id}")
    public String showBook(@PathVariable String id, Model model) throws IOException {
        Book book = bookService.findById(Long.valueOf(id));
        model.addAttribute("book", book);
        return "showBook";
    }

    @PostMapping("/toggleRead/{id}")
    public String toggleRead(@PathVariable String id) {
        System.out.println("HERE:POST toggleRead/:" + id + ":");
        bookService.setRead(Long.valueOf(id), true);
        return "redirect:/";
    }

    @PostMapping("/updateBooks")
    //@RequestMapping(value = "/updateBooks", headers = "Accept=application/json", consumes = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.POST)
    public String updateBooks(@RequestBody Map<String, Object> body) {
        System.out.println("HERE:POST updateBooks/:");

        ArrayList ids = (ArrayList) body.get("ids");
        System.out.println("ids:" + ids.size());

        Boolean read = (Boolean) body.get("read"); // may be null = do nothing
        System.out.println("read: " + read);


        for (String id : (List<String>) ids) {
            System.out.println("ID: " + id);

            // read/unread
            if (read != null) {
                bookService.setRead(Long.valueOf(id), read.booleanValue());
            }

            // add all tags
            String addTagString = (String) body.get("addTags");
            System.out.println("addTags:" + addTagString);
            List<String> addTagList = Arrays.asList(addTagString.split("\\s*,\\s*"));
            for (String tagString : addTagList) {
                System.out.println("Add tag:*" + tagString + "*");
                Tag tag = tagService.findByValue(tagString);
                if ( tag == null ) {
                    tag = new Tag(tagString);
                }
                bookService.addTag(Long.valueOf(id), tag);
            } // for each tag

            // remove all tags
            String delTagString = (String) body.get("delTags");
            System.out.println("delTags:" + delTagString);
            List<String> delTagList = Arrays.asList(delTagString.split("\\s*,\\s*"));
            for (String tagString : delTagList) {
                System.out.println("Del tag::*" + tagString + "*");
                //TODO: can i just create a Tag object and search the books.tags?
                Tag tag = tagService.findByValue(tagString);
                if ( tag != null ) {
                    bookService.delTag(Long.valueOf(id), tag);
                }
                else {
                    System.out.println("SHOULD NOT BE HERE, how did the tag get created:*" + tagString +"*");
                }

            } // for each tag

        } // for each book


        // TODO: what to put here?
        return "showBookTable";
    }

} // BookController