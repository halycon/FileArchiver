package com.arkheion.app.strategy.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.arkheion.app.model.FileModel;
import com.arkheion.app.model.FileProperty;
import com.arkheion.app.strategy.IFileArchiverContext;
import com.arkheion.app.strategy.IFileReadStrategy;
import com.arkheion.app.strategy.IFileWriteStrategy;

@Component("FileArchiverContext")
public class FileArchiverContext implements IFileArchiverContext {

	private IFileReadStrategy fileReadStrategy;
	private IFileWriteStrategy fileWriteStrategy;

	@Override
	public void setFileReadStrategy(IFileReadStrategy fileReadStrategy) {
		this.fileReadStrategy = fileReadStrategy;
	}

	@Override
	public void setFileWriteStrategy(IFileWriteStrategy fileWriteStrategy) {
		this.fileWriteStrategy = fileWriteStrategy;
	}

	@Override
	public List<FileModel> readFile(String filename, String path, ArrayList<HashMap<String, FileProperty>> addt_props,
			String documentClass) {
		return fileReadStrategy.readFile(filename, path, addt_props, documentClass);
	}

	@Override
	public FileModel writeFile(FileModel fileModel, boolean addVersion) throws Exception {

		return fileWriteStrategy.writeFile(fileModel, addVersion);
	}

}
