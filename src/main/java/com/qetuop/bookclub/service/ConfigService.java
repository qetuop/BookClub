package com.qetuop.bookclub.service;

import com.qetuop.bookclub.model.Book;
import com.qetuop.bookclub.model.Config;
import com.qetuop.bookclub.repository.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/*
    There should only ever be one row in the Config Table.  Don't let application directly access Config objects, ex:
    creating/saving them.  Use wrapper functions to ensure only one ever exists.

 */
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

    // There should only ever be one row in the Config Table
    private Config getConfig() {
        Config config = null;
        List<Config> configs = (List<Config>) repository.findAll();
        if ( configs.isEmpty() ) {
            config = new Config();
            config.setLastScanTime(0l);
            config.setAudioRootDir("");
            this.save(config);
        }
        else {
            config = configs.get(0);
        }
        return config;
    }

    public void setLastScanTime(Long lastScanTime) {
        Config config = getConfig();
        config.setLastScanTime(lastScanTime);
        this.save(config);
    }

    public Long getLastScanTime() {
        Config config = getConfig();
        return config.getLastScanTime();
    }

    public void setAudioRootDir(String rootDir) {
        Config config = getConfig();
        config.setAudioRootDir(rootDir);
        this.save(config);
    }

    public String getAudioRootDir() {
        Config config = getConfig();
        return config.getAudioRootDir();
    }
}