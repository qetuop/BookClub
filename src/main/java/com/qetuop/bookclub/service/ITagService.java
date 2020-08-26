package com.qetuop.bookclub.service;

import java.util.List;

import com.qetuop.bookclub.model.Tag;

public interface ITagService {

    public Tag save(Tag tag);

    List<Tag> findAll();

    Tag findById(long id);

    // TODO: should only be one tag in table eventually
    //List<Tag> findByValue(String value);
    Tag findByValue(String value);
}