package com.qetuop.bookclub;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileList {

    public void printFiles() {

        String dirName = "/home/brian/Projects/BookClub/testdir";

        try (Stream<Path> paths = Files.walk(Paths.get(dirName))) {
            paths.filter(Files::isDirectory)
                    .forEach(System.out::println);
        }catch (Exception e) {
            ;//TODO: handle exception
        }

        System.out.println("--------");

        try (Stream<Path> paths = Files.walk(Paths.get(dirName))) {
            paths.filter(Files::isRegularFile)
                    .forEach(System.out::println);
        }catch (Exception e) {
        ;//TODO: handle exception
    }

        // Files::isRegularFile for files vs dirs
        
    }
}