package com.arkheion.app.model.service;

import java.io.Serializable;
import java.util.HashMap;

import com.arkheion.app.model.FileProperty;
import com.arkheion.app.model.Priority;

public class FileWriteRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9175036557768541L;
	private String filename;
	private byte[] data;
	private HashMap<String, FileProperty> additionalProps;
	private String documentClass;
	private Priority priority;
	private String mimeType;
	private String hashkey;
	
	
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public String getDocumentClass() {
		return documentClass;
	}
	public void setDocumentClass(String documentClass) {
		this.documentClass = documentClass;
	}
	public Priority getPriority() {
		return priority;
	}
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public String getHashkey() {
		return hashkey;
	}
	public void setHashkey(String hashkey) {
		this.hashkey = hashkey;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public HashMap<String,FileProperty> getAdditionalProps() {
		return additionalProps;
	}
	public void setAdditionalProps(HashMap<String,FileProperty> additionalProps) {
		this.additionalProps = additionalProps;
	}
}
