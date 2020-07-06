package com.qetuop.bookclub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.qetuop.bookclub.model.Book;
import com.qetuop.bookclub.service.IBookService;

@Controller
public class BookController {
    
    @Autowired
    private IBookService bookService;

    @GetMapping("/showBooks")
    public String findBooks(Model model) {

        List<Book> books = (List<Book>) bookService.findAll();

        model.addAttribute("books", books);

        //return "showBooks";
        return "showCards";

    }

    @GetMapping(path="/books/{id}/cover")
    public void renderImageFromDB(@PathVariable String id, HttpServletResponse response) throws IOException {
        Book book = bookService.findById(Long.valueOf(id));

        if (book.getImage() != null) {
            byte[] byteArray = new byte[book.getImage().length];
            int i = 0;

            for (Byte wrappedByte : book.getImage()){
                byteArray[i++] = wrappedByte; //auto unboxing
            }

            response.setContentType("image/jpeg");
            InputStream is = new ByteArrayInputStream(byteArray);
            IOUtils.copy(is, response.getOutputStream());
        }
    }


    /*
    @GetMapping(path="/books/{bookId}/cover")
    //https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#core.web for auto resolution of path var to domain Object
    public @ResponseBody byte[] getPoster(@PathVariable("bookId") Book book, HttpServletResponse response){
        File file = book.getPosterFile();
        //stream the bytes of the file
        // see https://www.baeldung.com/spring-controller-return-image-file
        // see https://www.baeldung.com/spring-mvc-image-media-data
    }
    */
}