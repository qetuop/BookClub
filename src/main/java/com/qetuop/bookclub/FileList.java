package com.qetuop.bookclub;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileList {

    public  List<Path> createFileList2(String rootDir) {
        List<Path> subDirs = new ArrayList<>();

        Path startPath = Paths.get(rootDir);
        try {
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

                    //If there is no subDir --> add it to your list
                    if (Files.list(dir).noneMatch(d ->Files.isDirectory(d)) &&
                        Files.list(dir).allMatch(d ->Files.isRegularFile(d))){ subDirs.add(dir); }

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        //print out all Subdirs
        subDirs.forEach(System.out::println);

        return subDirs;
    }

    public List<Path> createFileList(String rootDir) {

        Path configFilePath = FileSystems.getDefault().getPath(rootDir);
        System.out.println("file dir:"+configFilePath);

        /*
        System.out.println("---Dirs start-----");
        try (Stream<Path> paths = Files.walk(configFilePath)) {
            paths.filter(Files::isDirectory)
                    .forEach(System.out::println);
        }catch (Exception e) {
            ;//TODO: handle exception
        }
        System.out.println("---Dirs end-----");

        System.out.println("---Files start-----");
        try (Stream<Path> paths = Files.walk(configFilePath)) {
            paths.filter(Files::isRegularFile)
                    .forEach(System.out::println);
        }catch (Exception e) {
        ;//TODO: handle exception	
        System.out.println("---Files end-----");
*/
        
        System.out.println("---FileList::createFileList()::Grabbing books-----");
        

        List<Path> fileWithName = new ArrayList<Path>();
		try {
			// add ".map(Path::getFileName)" to just get filename
			fileWithName = Files.walk(configFilePath, FileVisitOption.FOLLOW_LINKS)
                    .filter(Files::isDirectory)
			        .sorted()
			        .collect(Collectors.toList());
//			        .filter(s -> s.toString().endsWith(".jpg"))
            //.filter(p -> p.getFileName().toString().endsWith(".jpg"))

			 for (Path name : fileWithName) {System.out.println(name);}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("---Done-----");
//.filter(f -> extensions.stream().anyMatch(f::endsWith))
		
		return fileWithName;
       
    } // printFiles

} // FileList

