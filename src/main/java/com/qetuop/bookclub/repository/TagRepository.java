package com.qetuop.bookclub.repository;

import java.util.List;

import com.qetuop.bookclub.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qetuop.bookclub.model.Book;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TagRepository extends CrudRepository<Tag, Long> {
    Tag findByValue(String value);
}
