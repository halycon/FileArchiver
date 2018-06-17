package com.arkheion.app.strategy.impl;

import com.arkheion.app.model.FileModel;
import com.arkheion.app.model.FileProperty;
import com.arkheion.app.strategy.IFileReadStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component("UnixSystemFileReadStrategy")
public class UnixSystemFileReadStrategy implements IFileReadStrategy {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
	public List<FileModel> readFile(String filename, String path, ArrayList<HashMap<String, FileProperty>> addt_props,
			String documentClass) {
		ObjectInputStream ois = null;
		try {
            String rootFolder = "/opt/FileArchiverData/";
            ois = new ObjectInputStream(new FileInputStream(rootFolder + path + filename));
			FileModel fileModel = (FileModel) ois.readObject();
			ois.close();
			List<FileModel> files = new ArrayList<>();
			files.add(fileModel);
			return files;

		} catch (Exception ex) {
			logger.error("error UnixSystemFileReadStrategy readFile :: ", ex);
		}finally {
			try {
				if (ois != null) {
					ois.close();
				}
			} catch (IOException e) {
                logger.error("error UnixSystemFileReadStrategy readFile ::", e);
			}
		}
		return null;
	}

}
