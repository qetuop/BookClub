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

	//@Autowired
	//ApplicationConfiguration ac;

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

		// ***** MAKE SURE THERE IS A TRAILING '/'  TODO: figure out why and how to fix - the split on file.separator

		String rootDir = "/audiobooks/";
		//rootDir = "/audiobooks/Terry Pratchett/";
		rootDir = "/home/brian/Projects/testdir/audio books/";
		//rootDir = "/home/brian/Projects/testdir/fake_audio_books/";
		//rootDir = "/home/brian/Projects/testdir/test/";
		//rootDir = "/home/brian/Projects/testdir/simple/";
		//rootDir = "/media/NAS/audiobooks/";
		//rootDir = "/media/NAS/audiobooks/Warhammer/";

		//bookService.deleteAll();

		// TODO: add other mock data - ignore dir, last mode, etc. - need to persist this through  restarts...
		//config.setLastScanTime(0l);
		//config.setAudioRootDir(rootDir);
		//configService.save(config);

		//configService.setLastScanTime(0L);
		configService.setAudioRootDir(rootDir);

		//scanner.scan(true);
	}
}