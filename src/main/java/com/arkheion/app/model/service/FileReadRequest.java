package com.arkheion.app.model.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.arkheion.app.model.FileProperty;

public class FileReadRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1069339962487406739L;
	private String referenceCode;
	private String hashkey;

	private ArrayList<HashMap<String, FileProperty>> addt_props;
	private String documentClass;

	public FileReadRequest() {

	}

	public FileReadRequest(String hashkey, String referenceCode) {
		this.hashkey = hashkey;
		this.referenceCode = referenceCode;
	}

	public String getReferenceCode() {
		return referenceCode;
	}

	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public String getHashkey() {
		return hashkey;
	}

	public void setHashkey(String hashkey) {
		this.hashkey = hashkey;
	}

	public String getDocumentClass() {
		return documentClass;
	}

	public void setDocumentClass(String documentClass) {
		this.documentClass = documentClass;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public ArrayList<HashMap<String, FileProperty>> getAddt_props() {
		return addt_props;
	}

	public void setAddt_props(ArrayList<HashMap<String, FileProperty>> addt_props) {
		this.addt_props = addt_props;
	}

}
