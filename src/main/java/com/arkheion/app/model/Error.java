package com.arkheion.app.model;

import java.io.Serializable;

public class Error implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3625500759699806898L;
	private ErrorType errorType;
	private String errorMessage;

	public Error(ErrorType errorType,String errorMessage){
		this.errorType = errorType;
		this.errorMessage = errorMessage;
	}
	
	public ErrorType getErrorType() {
		return errorType;
	}

	public void setErrorType(ErrorType errorType) {
		this.errorType = errorType;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
