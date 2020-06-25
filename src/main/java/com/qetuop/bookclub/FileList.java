package com.qetuop.bookclub;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileList {

    public void printFiles() throws IOException {

        String dirName = "/home/brian/Projects/BookClub/testdir";

        try (Stream<Path> paths = Files.walk(Paths.get(dirName))) {
            paths.filter(Files::isDirectory)
                    .forEach(System.out::println);
        }

        // Files::isRegularFile for files vs dirs
        
    }
}