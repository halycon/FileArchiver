package com.arkheion.app.service;

import com.arkheion.app.model.service.FileReadRequest;
import com.arkheion.app.model.service.FileReadResponse;
import com.arkheion.app.model.service.FileWriteRequest;
import com.arkheion.app.model.service.FileWriteResponse;

public interface IFileArchiverService {
	
	public FileWriteResponse writeFile(FileWriteRequest request);
	
	public FileReadResponse readFile(FileReadRequest request);
	
	public void test() throws Exception;
	
}
