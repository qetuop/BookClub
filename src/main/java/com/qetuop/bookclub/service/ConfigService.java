package com.qetuop.bookclub.service;

import com.qetuop.bookclub.model.Book;
import com.qetuop.bookclub.model.Config;
import com.qetuop.bookclub.repository.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConfigService implements IConfigService {

    @Autowired
    public ConfigRepository repository;

    @Override
    public Config save(Config config) {
        return repository.save(config);
    }

    @Override
    public List<Config> findAll() {
        List<Config> rv = (List<Config>) repository.findAll();
        return rv;
    }

    @Override
    public Config findById(long id) {
        Optional<Config> rv = repository.findById(id);
        return rv.get();
    }
}