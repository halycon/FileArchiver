package com.arkheion.app.strategy;

import java.util.Map;

import com.arkheion.app.model.FileProperty;

public interface IFileDeleteStrategy {

	public boolean deleteFile(String filename, String path, Map<String, FileProperty> addt_props, String documentClass);
}
