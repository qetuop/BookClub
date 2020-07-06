package com.qetuop.bookclub.service;

import org.springframework.web.multipart.MultipartFile;

public interface IImageService {

    void saveImageFile(Long bookId, MultipartFile file);
}