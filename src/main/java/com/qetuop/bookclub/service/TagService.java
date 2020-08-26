package com.qetuop.bookclub.service;

import com.qetuop.bookclub.model.Book;
import com.qetuop.bookclub.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;

import com.qetuop.bookclub.model.Tag;
import com.qetuop.bookclub.repository.TagRepository;

@Service
public class TagService implements ITagService {

    @Autowired
    public TagRepository repository;

    @Override
    public Tag save(Tag tag) {
        return repository.save(tag);
    }

    @Override
    public List<Tag> findAll() {
        List<Tag> tags = (List<Tag>) repository.findAll();
        return tags;
    }

    @Override
    public Tag findById(long id) {
        Optional<Tag> tag = repository.findById(id);
        return tag.get();
    }

    @Override
    public Tag findByValue(String value) {
        System.out.println("TagService:findByValue: " + value);
        Tag tag = (Tag) repository.findByValue(value);
        return tag;
    }
}