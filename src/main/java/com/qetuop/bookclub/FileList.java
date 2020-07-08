package com.qetuop.bookclub;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileList {

    public List<Path> printFiles(String rootDir) {

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
        
        System.out.println("---JPGs start-----");
        

        List<Path> fileWithName = new ArrayList<Path>();
		try {
			// add ".map(Path::getFileName)" to just get filename
			fileWithName = Files.walk(configFilePath)
			        .filter(s -> s.toString().endsWith(".jpg"))
			        .sorted()
			        .collect(Collectors.toList());
			
//			 for (Path name : fileWithName) {
//		            System.out.println(name);
//		        }
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("---JPGs end-----");
//.filter(f -> extensions.stream().anyMatch(f::endsWith))
		
		return fileWithName;
       
    } // printFiles

} // FileList

