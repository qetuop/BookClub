package com.qetuop.bookclub;

import java.lang.invoke.MethodHandles;

import com.qetuop.bookclub.model.Config;
import com.qetuop.bookclub.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.qetuop.bookclub.service.BookService;

@Component
public class AppStartupRunner implements ApplicationRunner {
	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	public static int counter;

	@Autowired
	private StorageService storageService;
	@Autowired
	private BookService bookService;
	@Autowired
	private TagService tagService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private Scanner scanner;

	/*public AppStartupRunner(StorageService storageService, BookService bookService, TagService tagService, ConfigService configService) {
		this.storageService = storageService;
		this.bookService = bookService;
		this.tagService = tagService;
		this.configService = configService;
	}*/

	@Override
	public void run(ApplicationArguments args) throws Exception {
		LOG.info("AppStartupRunner::run");

		Config config = new Config();

		// TODO: figure out if i should include trailing slash or not, it affects the split below, just be consistent
		//final String rootDir = "/home/brian/Projects/testdir/audio books/";
		//final String rootDir = "/home/brian/Projects/testdir/fake_audio_books/";
		//final String rootDir = "/home/brian/Projects/testdir/test/";
		//final String rootDir = "/media/NAS/audiobooks/";

		config.setAudioRootDir("/home/brian/Projects/testdir/audio books/");
		config.setAudioRootDir("/home/brian/Projects/testdir/fake_audio_books/");
		configService.save(config);

		// TODO: add a function to return the one AND only entry - or just create get/set functions in service class
		config = configService.findById(1);
		String rootDir = config.getAudioRootDir();
		System.out.println("ROOT DIR: " + rootDir);

		//Scanner scanner = new Scanner(storageService, bookService, tagService);
		//Scanner scanner = new Scanner();
		scanner.scan();
/*
		// TODO: figure out if i should include trailing slash or not, it affects the split below, just be consistent
		String rootDir = "/home/brian/Projects/testdir/audio books/";       
		rootDir = "/media/NAS/audiobooks/";

		List<Path> fileWithName = fileList.printFiles(rootDir);

		// keep a map of hash(author/series/title):book id for quicker lookup during operations below - worth it?
		// link to the book object itself then add all at once to repo?  TODO: do that
		HashMap<Integer,Long> bookMap = new HashMap<Integer,Long>();

		List<String> ignoreList = new ArrayList<String>(Arrays.asList("#recycle", "_temp", "_organize", "Warhammer"));


		for (Path filePath : fileWithName) {
			System.out.println("------New file: " + filePath);

			String fullPathString = filePath.toString().strip();

			String tmpString = fullPathString.replace(rootDir, "");
			String pattern = Pattern.quote(System.getProperty("file.separator"));
			String[] splitList = tmpString.split(pattern);

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
			float seriesNumber = 0;

			author = splitList[0];

			//System.out.println("AUTHOR: " + author + "," + splitList[1]);
			
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
					String[] splitTitle = titleFull.split("-");


					//System.out.println("\t\t" + StringUtils.join(splitTitle, "|"));
					title = splitTitle[0].strip();
					seriesNumber = Float.parseFloat(splitTitle[1].strip());

				}
				else if ( titleFull.matches(".*\\d.*") ) {
					String[] splitTitle = titleFull.split(" ");
					String seriesNumberStr = splitTitle[splitTitle.length-1];
					seriesNumber = Float.parseFloat(seriesNumberStr);
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

 */
	} // run

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