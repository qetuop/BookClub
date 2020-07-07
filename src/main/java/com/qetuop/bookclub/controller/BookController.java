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


import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qetuop.bookclub.storage.StorageFileNotFoundException;
import com.qetuop.bookclub.storage.StorageService;
import com.qetuop.bookclub.service.BookService;

@Controller
public class BookController {
    
    private final StorageService storageService;
    private final BookService bookService;    
    
    @Autowired
	public BookController(StorageService storageService, BookService bookService) {
		this.storageService = storageService;
		this.bookService = bookService;
    }    

    @GetMapping("/")
	public String listUploadedFiles(Model model) throws IOException {
		System.out.println("HERE:GET/ ");

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
		bookService.saveImageFile(1L,file);
		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");

		return "redirect:/";
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



	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
		System.out.println("HERE:serveFile "+filename);

		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}

	@GetMapping("/image/{filename:.+}")
	@ResponseBody
	public  byte[] getImage(@PathVariable String filename) throws IOException {
		System.out.println("HERE:getImage "+filename);

		Resource file = storageService.loadAsResource(filename);
		//InputStream in = getClass().getResourceAsStream(file.getInputStream());
		
    	return IOUtils.toByteArray(file.getInputStream());
	}

	

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}





    @GetMapping("/showBooks")
    public String findBooks(Model model) {

        List<Book> books = (List<Book>) bookService.findAll();

        model.addAttribute("books", books);

        //return "showBooks";
        return "showCards";

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