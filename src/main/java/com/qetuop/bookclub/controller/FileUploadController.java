package com.qetuop.bookclub.controller;

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
import com.qetuop.bookclub.service.ImageService;

@Controller
public class FileUploadController {

	private final StorageService storageService;
	private final ImageService imageService;

	@Autowired
	public FileUploadController(StorageService storageService,ImageService imageService) {
		this.storageService = storageService;
		this.imageService = imageService;
	}

	@GetMapping("/")
	public String listUploadedFiles(Model model) throws IOException {
		System.out.println("HERE:GET/ ");

		model.addAttribute("files", storageService.loadAll().map(
				path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
						"serveFile", path.getFileName().toString()).build().toUri().toString())
				.collect(Collectors.toList()));

		return "uploadForm";
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

	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {

		System.out.println("HERE:Post/ ");

		storageService.store(file);
		//storageService.storeDB(file);
		imageService.saveImageFile(1L,file);
		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");

		return "redirect:/";
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

}
