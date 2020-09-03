package com.qetuop.bookclub.repository;

import com.qetuop.bookclub.model.Config;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ConfigRepository extends CrudRepository<Config, Long> {
}
