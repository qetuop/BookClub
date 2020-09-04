package com.qetuop.bookclub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.qetuop.bookclub.model.Tag;
import com.qetuop.bookclub.repository.TagRepository;

@Service
public class TagService {

    @Autowired
    public TagRepository repository;

    public Tag save(Tag tag) {
        return repository.save(tag);
    }

    public List<Tag> findAll() {
        List<Tag> tags = (List<Tag>) repository.findAll();
        return tags;
    }

    public Tag findById(long id) {
        Optional<Tag> tag = repository.findById(id);
        return tag.get();
    }

    public Tag findByValue(String value) {
        System.out.println("TagService:findByValue: " + value);
        Tag tag = (Tag) repository.findByValue(value);
        return tag;
    }
}