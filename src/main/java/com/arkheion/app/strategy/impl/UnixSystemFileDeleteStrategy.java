package com.arkheion.app.strategy.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.arkheion.app.model.FileProperty;
import com.arkheion.app.strategy.IFileDeleteStrategy;

@Component("UnixSystemFileDeleteStrategy")
public class UnixSystemFileDeleteStrategy implements IFileDeleteStrategy {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final String rootFolder = "/opt/FileArchiverData/";

	@Override
	public boolean deleteFile(String filename, String path, Map<String, FileProperty> addt_props, String documentClass) {

		try {
			File file = new File(rootFolder + path + filename);
			return Files.deleteIfExists(file.toPath());
		} catch (IOException err) {
			logger.error("UnixSystemFileDeleteStrategy deleteFile :: {}", err);
			return false;
		}
	}

}
