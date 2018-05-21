package com.arkheion.app.model;

import java.io.Serializable;

public class CustomMessage implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8464570362990608632L;
	private int id;
	private String ref_code;
	private String localStoreFileName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRef_code() {
		return ref_code;
	}

	public void setRef_code(String ref_code) {
		this.ref_code = ref_code;
	}

	public String getLocalStoreFileName() {
		return localStoreFileName;
	}

	public void setLocalStoreFileName(String localStoreFileName) {
		this.localStoreFileName = localStoreFileName;
	}
	
}
