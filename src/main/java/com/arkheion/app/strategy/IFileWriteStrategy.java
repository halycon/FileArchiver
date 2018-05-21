package com.arkheion.app.strategy;

import com.arkheion.app.model.FileModel;

public interface IFileWriteStrategy {

	public FileModel writeFile(FileModel fileModel,boolean addVersion) throws Exception ;

}
