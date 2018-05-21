package com.arkheion.app.strategy.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.arkheion.app.model.FileProperty;
import com.arkheion.app.strategy.IFileDeleteStrategy;

@Component("FilenetDeleteStrategy")
public class FilenetDeleteStrategy implements IFileDeleteStrategy {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${filenet.uri}")
	private String uri;

	@Value("${filenet.username}")
	private String username;

	@Value("${filenet.password}")
	private String password;

	@Value("${filenet.objectstore}")
	private String objectstore;

	@Value("${filenet.rootFolderName}")
	private String rootFolderName;

	@Override
	public boolean deleteFile(String filename, String path, Map<String, FileProperty> addt_props, String documentClass) {
		// TODO Auto-generated method stub
		return false;
	}

}
