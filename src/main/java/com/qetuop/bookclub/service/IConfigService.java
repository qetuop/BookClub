package com.qetuop.bookclub.service;

import com.qetuop.bookclub.model.Config;

import java.util.List;

public interface IConfigService {

    public Config save(Config config);

    List<Config> findAll();

    Config findById(long id);
}