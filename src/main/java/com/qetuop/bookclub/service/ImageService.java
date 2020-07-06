package com.qetuop.bookclub.service;

import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import com.qetuop.bookclub.model.Book;
import com.qetuop.bookclub.repository.BookRepository;

@Slf4j
@Service
public class ImageService implements IImageService {


    private final BookRepository bookRepository;

    public ImageService( BookRepository bookRepository) {

        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public void saveImageFile(Long bookId, MultipartFile file) {
        System.out.println("HERE:saveImageFile");
        try {
            Book book = bookRepository.findById(bookId).get();

            Byte[] byteObjects = new Byte[file.getBytes().length];

            int i = 0;

            for (byte b : file.getBytes()){
                byteObjects[i++] = b;
            }

            book.setImage(byteObjects);

            bookRepository.save(book);
        } catch (IOException e) {
            //todo handle better
            log.error("Error occurred", e);

            e.printStackTrace();
        }
    }
}
