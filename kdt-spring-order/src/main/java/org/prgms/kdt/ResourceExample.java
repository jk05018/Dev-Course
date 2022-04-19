package org.prgms.kdt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class ResourceExample {
	private static Logger logger = LoggerFactory.getLogger(ResourceExample.class);

	public static void main(String[] args) throws IOException {
		var applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);

		ResourceLoader lc = new FileSystemResourceLoader();
		final Resource resource1 = lc.getResource("file:voucher_info.txt");
		final File voucher_info = resource1.getFile();

		final List<String> strings1 = Files.readAllLines(voucher_info.toPath());
		logger.info(strings1.stream().collect(Collectors.joining("\n")));


		try (BufferedWriter writer = new BufferedWriter(new FileWriter(voucher_info))) {
			writer.append("Hello");
			writer.append("nice");
			writer.append("meet");
		} catch (IOException e) {
			e.printStackTrace();
		};


		// Resource resource = applicationContext.getResource("classpath:application.yaml");
		//
		// Resource resource2 = applicationContext.getResource("file:sample.txt");
		// Resource resource3 = applicationContext.getResource("https://stackoverflow.com/");
		// System.out.println(MessageFormat.format("Resource ->{0}", resource3.getClass().getCanonicalName()));
		// File file = resource2.getFile();
		// List<String> strings = Files.readAllLines(file.toPath()); // file.toiPath() 파일의 경로 지 개행을 기준으로 짤린다
		// logger.info(strings.stream().collect(Collectors.joining("\n")));

		// ReadableByteChannel readableByteChannel = Channels.newChannel(resource3.getURI().toURL().openStream());
		// BufferedReader bufferedReader = new BufferedReader(
		// 	Channels.newReader(readableByteChannel, StandardCharsets.UTF_8));
		// Stream<String> lines = bufferedReader.lines();
		// System.out.println(lines.collect(Collectors.joining("\n")));
	}
}
