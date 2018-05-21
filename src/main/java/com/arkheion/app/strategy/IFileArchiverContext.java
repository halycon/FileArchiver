package com.arkheion.app.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.arkheion.app.model.FileModel;
import com.arkheion.app.model.FileProperty;

public interface IFileArchiverContext {

	public void setFileReadStrategy(IFileReadStrategy fileReadStrategy);

	public void setFileWriteStrategy(IFileWriteStrategy fileWriteStrategy);

	public List<FileModel> readFile(String filename, String path, ArrayList<HashMap<String, FileProperty>> addt_props,
			String documentClass);

	public FileModel writeFile(FileModel fileModel, boolean addVersion) throws Exception;
}
