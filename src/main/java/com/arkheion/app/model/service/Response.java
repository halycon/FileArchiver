package com.arkheion.app.model.service;

import java.io.Serializable;

import com.arkheion.app.model.Error;

public class Response implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5359607121206565872L;
	private boolean success;
	private Error error;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public Error getError() {
		return error;
	}
	public void setError(Error error) {
		this.error = error;
	}
	
}
