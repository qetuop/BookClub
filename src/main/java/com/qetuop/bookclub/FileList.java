package com.qetuop.bookclub;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

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

                    return CONTINUE;
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
        final List<String> ignoreList = new ArrayList<>(Arrays.asList("_test", "@eaDir", "#recycle", "_temp", "_organize", "Warhammer", "_cleanup"));

        List<Path> fileWithName = new ArrayList<Path>();

        Path startPath = Paths.get(rootDir);
        try {
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {

                    //String finalPath = dir.getName(dir.getNameCount()-1).toString();
                    //System.out.println("FinalPath : " + finalPath);
                    if (ignoreList.contains(dir.getFileName().toString())){
                        System.out.println("SKIPPING DIR: " + dir);
                        return SKIP_SUBTREE;
                    }

                    return CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException
                {
                    //String firstLine = Files.newBufferedReader(file, Charset.defaultCharset()).readLine();
                    //System.out.println("FILE: " + file);
                    return CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    List<Path> files = null;
                    try {
                        files = Files.list(dir)
                                .filter(p -> !p.toFile().isDirectory())
                                .collect(Collectors.toList());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if ( !files.isEmpty() ) {
                        System.out.println("ADDING DIR: " + dir);
                        fileWithName.add(dir);
                    }
                    return CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


		/*try {
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
		}*/
		System.out.println("---Done-----");
//.filter(f -> extensions.stream().anyMatch(f::endsWith))
		
		return fileWithName;
       
    } // printFiles

} // FileList

