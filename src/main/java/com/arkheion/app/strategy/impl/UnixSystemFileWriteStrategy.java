package com.arkheion.app.strategy.impl;

import com.arkheion.app.model.FileModel;
import com.arkheion.app.strategy.IFileWriteStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

@Component("UnixSystemFileWriteStrategy")
public class UnixSystemFileWriteStrategy implements IFileWriteStrategy {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final String rootFolder = "/opt/FileArchiverData/";
	
	@Override
	public FileModel writeFile(FileModel fileModel, boolean addVersion) throws Exception {
		
		File filePath = new File(rootFolder+fileModel.getPath());
		
		if(!filePath.exists())
        {
			filePath.mkdir();
        }	

		if(fileModel.getLocalStoreFileName().length()>100){
			fileModel.setLocalStoreFileName(fileModel.getLocalStoreFileName().substring(0, 100));
		}
		String filePathName = filePath+"/"+fileModel.getLocalStoreFileName();
		
		FileOutputStream fout = new FileOutputStream(filePathName);
		try (ObjectOutputStream oos = new ObjectOutputStream(fout)) {
			oos.writeObject(fileModel);
			oos.close();
		}
		
//		Files.write(Paths.get(filePathName), data);
				
		logger.info("file created : {}",filePathName);		
		return fileModel;
	}


}
