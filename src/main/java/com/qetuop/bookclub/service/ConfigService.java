package com.qetuop.bookclub.service;

import com.qetuop.bookclub.model.Book;
import com.qetuop.bookclub.model.Config;
import com.qetuop.bookclub.repository.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConfigService {

    @Autowired
    public ConfigRepository repository;

    public Config save(Config config) {
        return repository.save(config);
    }

    public List<Config> findAll() {
        List<Config> rv = (List<Config>) repository.findAll();
        return rv;
    }

    public Config findById(long id) {
        Optional<Config> rv = repository.findById(id);
        return rv.get();
    }
}