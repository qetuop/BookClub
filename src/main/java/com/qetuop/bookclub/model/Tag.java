package com.qetuop.bookclub.model;

import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;  //add @Data before class to get auto generated getter, setters, etc https://projectlombok.org/features/Data
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Tag {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String value;

    @ManyToMany(mappedBy = "tags")
    private Set<Book> books = new HashSet<>();

    // needed for JPA, NoArgsConstructor created by lombok
    protected Tag() {}

    public Tag(String value) {
        this.value = value;
    }
}
