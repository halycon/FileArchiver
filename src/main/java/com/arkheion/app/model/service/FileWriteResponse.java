package com.arkheion.app.model.service;

import java.time.LocalDateTime;

public class FileWriteResponse extends Response {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1965203053750967787L;
	private String referenceCode;
	private LocalDateTime creationDate;
	private String viewUrl;

	public String getReferenceCode() {
		return referenceCode;
	}

	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public String getViewUrl() {
		return viewUrl;
	}

	public void setViewUrl(String viewUrl) {
		this.viewUrl = viewUrl;
	}
}
