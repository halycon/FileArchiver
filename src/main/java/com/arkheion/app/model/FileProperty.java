package com.arkheion.app.model;

import java.io.Serializable;

public class FileProperty implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3221091698984132875L;
	private String value;
	private FilePropertyType type;

	public FileProperty(){
		
	}
	
	public FileProperty(String value,FilePropertyType filePropertyType){
		this.value = value;
		this.type = filePropertyType;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public FilePropertyType getType() {
		return type;
	}

	public void setType(FilePropertyType type) {
		this.type = type;
	}
}
