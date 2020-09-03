package com.qetuop.bookclub.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Config {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String audioRootDir;
    private Long lastScanTime;

    // needed for JPA, NoArgsConstructor created by lombok
    public Config() {}

}
