package com.arkheion.app.model.service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

import com.arkheion.app.model.FileProperty;
import com.arkheion.app.model.Priority;

public class FileResponseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3458040618009012281L;
	private byte[] data;
	private String fileName;
	private LocalDateTime creationDate;
	private Map<String, FileProperty> additionalProps;
	private String documentClass;
	private Priority priority;
	private String mimeType;
	private String viewUrl;

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public Map<String, FileProperty> getAdditionalProps() {
		return additionalProps;
	}

	public void setAdditionalProps(Map<String, FileProperty> additionalProps) {
		this.additionalProps = additionalProps;
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

	public String getViewUrl() {
		return viewUrl;
	}

	public void setViewUrl(String viewUrl) {
		this.viewUrl = viewUrl;
	}

}
