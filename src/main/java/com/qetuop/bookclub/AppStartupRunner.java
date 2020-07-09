package com.qetuop.bookclub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.qetuop.bookclub.storage.StorageService;
import com.qetuop.bookclub.repository.BookRepository;
import com.qetuop.bookclub.model.Book;

@Component
public class AppStartupRunner implements ApplicationRunner {
	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	public static int counter;

	@Autowired
	public StorageService storageService;
	public BookRepository repository;

	public FileList fileList = new FileList();

	public AppStartupRunner(StorageService storageService, BookRepository repository) {
		this.storageService = storageService;
		this.repository = repository;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		LOG.info("AppStartupRunner::print files");

		// TODO: figure out if i should include trailing slash or not, it affects the split below, just be consistent
		String rootDir = "/home/brian/Projects/testdir/audio books/";       
		//rootDir = "/media/NAS/audiobooks/";

		List<Path> fileWithName = fileList.printFiles(rootDir);

		// keep a map of hash(author/series/title):book id for quicker lookup during operations below - worth it?
		// link to the book object itself then add all at once to repo?  TODO: do that
		HashMap<Integer,Long> bookMap = new HashMap<Integer,Long>();

		List<String> ignoreList = new ArrayList<String>(Arrays.asList("#recycle", "_temp", "_organize", "Warhammer"));


		for (Path filePath : fileWithName) {
			System.out.println("\n------New file: " + filePath);            

			String fullPathString = filePath.toString().strip();

			String tmpString = fullPathString.replace(rootDir, "");
			String pattern = Pattern.quote(System.getProperty("file.separator"));
			String[] splitList = tmpString.toString().split(pattern);

			System.out.println("\t\t*" + StringUtils.join(splitList, "|") +"*");

			// standalone book
			// |Brian Herbert|Paul of Dune|Paul of Dune.jpg

			// series 
			// |Frank Herbert|Dune|Dune - 1|Dune.jpg

			String author = null;
			String title = null;
			String path = null; // rootDir + Author + series + title  or fullPath - last element?
			String cover = null; // save largest image == replace if necessary
			Byte[] image = null;
			String seriesName = null;
			int seriesNumber = 0;

			author = splitList[0];

			System.out.println("AUTHOR: " + author + "," + splitList[1]);
			// HACK check - ignore these dirs TODO: fix this
			if ( ignoreList.contains(author)) {
				continue;
			}

			// single book
			if ( splitList.length == 3 ) {
				title = splitList[1];
				path = rootDir + "/" + author + "/" + title;
			}
			// series
			else if (splitList.length == 4 ) {

				seriesName = splitList[1];
				String titleFull = splitList[2];


				// TODO: can i just split on spaces, if there is a number last wether preceded by a - or not, the re join everyting not a -
				if ( titleFull.matches(".*-.*") ) {

					// TODO: once i've properly named dir/files - may need to handle anyways
					String[] splitTitle = titleFull.toString().split("-");


					//System.out.println("\t\t" + StringUtils.join(splitTitle, "|"));
					title = splitTitle[0].strip();
					seriesNumber = Integer.parseInt(splitTitle[1].strip());

				}
				else if ( titleFull.matches(".*\\d.*") ) {
					String[] splitTitle = titleFull.toString().split(" ");
					String seriesNumberStr = splitTitle[splitTitle.length-1];
					seriesNumber = Integer.parseInt(seriesNumberStr);
					//System.out.println("SERIES NUM: " + seriesNumber);
					title = titleFull.substring(0, titleFull.indexOf(seriesNumberStr)-1);					
				}

				//System.out.println("\t\t" + seriesName + ":" + seriesNumber);


				path = rootDir + "/" + author + "/" + seriesName + " - " + seriesNumber + "/" + title;
			}
			else {
				System.out.println("somthings wrong");
			}

			cover = fullPathString;


			final File file = new File(fullPathString);

			FileInputStream input = null;
			try {
				input = new FileInputStream(file);
				image = ArrayUtils.toObject(IOUtils.toByteArray(input));


				String[] strings = {author, seriesName, title};
				int hashCode = Arrays.hashCode(strings);
				//System.out.println(hashCode);		

				Book book = null;

				// set the cover to the largest image found in the dir.  TODO: is this the best thing to do?
				if ( bookMap.containsKey(hashCode) ) {

					Optional<Book> found = repository.findById(bookMap.get(hashCode));

					if (found.isPresent()) {
						book = found.get();

						if ( book.getImage().length < image.length )  {
							book.setImage(image);						
						}
					}	
				}
				else {
					// Book(String title, String author, String path, String cover, Byte[] image, String seriesName, int seriesNumber)
					book = new Book(title, author, path, cover, image, seriesName, seriesNumber);				
				}

				book = repository.save(book);	
				bookMap.put(hashCode, book.getId());


			} catch (final FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//File file = new File(fullPathString);
			//String simpleFileName = file.getName();
			//System.out.println("\t"+simpleFileName);
		}
	}

	/*
	@Override
	public void run(ApplicationArguments args) throws Exception {
		LOG.info("AppStartupRunner started with option names : {}", args.getOptionNames());

		final String[][] data = { { "Dune", "Frank Herbert", "audio books/Frank Herbert/Dune/Dune - 1/", "Dune.jpg" },
				{ "Dune Messiah", "Frank Herbert", "audio books/Frank Herbert/Dune/Dune Messiah - 2/",
						"Dune Messiah-Cover.jpg" } };

		final String rootDir = "/home/brian/Projects/testdir/";

		storageService.deleteAll();
		storageService.init();

		Stream.of(data).forEach(array -> {

			final String filename = StringUtils.cleanPath(rootDir + array[2] + array[3]);

			System.out.println(filename);

			final File file = new File(filename);

			FileInputStream input = null;
			try {
				input = new FileInputStream(file);
				Byte[] image = ArrayUtils.toObject(IOUtils.toByteArray(input));

				final Book book = new Book(array[0], array[1], array[2], array[3], image);


				repository.save(book);

				// MultipartFile multipartFile = new MockMultipartFile("file",file.getName(),
				// "text/plain", IOUtils.toByteArray(input));


			} catch (final FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	} // run
	 */
}