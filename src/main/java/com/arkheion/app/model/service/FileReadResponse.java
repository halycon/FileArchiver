package com.arkheion.app.model.service;

import java.util.List;

public class FileReadResponse extends Response {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8919224536040330632L;
	private List<FileResponseModel> files;

	public List<FileResponseModel> getFiles() {
		return files;
	}

	public void setFiles(List<FileResponseModel> files) {
		this.files = files;
	}
}
