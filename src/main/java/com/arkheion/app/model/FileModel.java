package com.arkheion.app.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;

public class FileModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private int channel_id;
	private String ref_code;
	private int profile_id;
	private boolean status;
	private LocalDateTime create_date;
	private LocalDateTime proces_date;
	private Priority topic_id;
	private String document_class;
	private HashMap<String, FileProperty> addt_props;
	private String filename;
	private String localStoreFileName;
	private byte[] data;
	private String mimetype;
	private String path;
	private String channel_ref_code;
	private String errorMessage;

	public int getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(int channel_id) {
		this.channel_id = channel_id;
	}

	public String getRef_code() {
		return ref_code;
	}

	public void setRef_code(String ref_code) {
		this.ref_code = ref_code;
	}

	public int getProfile_id() {
		return profile_id;
	}

	public void setProfile_id(int profile_id) {
		this.profile_id = profile_id;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public LocalDateTime getCreate_date() {
		return create_date;
	}

	public void setCreate_date(LocalDateTime create_date) {
		this.create_date = create_date;
	}

	public LocalDateTime getProces_date() {
		return proces_date;
	}

	public void setProces_date(LocalDateTime proces_date) {
		this.proces_date = proces_date;
	}

	public String getDocument_class() {
		return document_class;
	}

	public void setDocument_class(String document_class) {
		this.document_class = document_class;
	}

	public Priority getTopic_id() {
		return topic_id;
	}

	public void setTopic_id(Priority topic_id) {
		this.topic_id = topic_id;
	}

	

	public String getLocalStoreFileName() {
		return localStoreFileName;
	}

	public void setLocalStoreFileName(String localStoreFileName) {
		this.localStoreFileName = localStoreFileName;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getChannel_ref_code() {
		return channel_ref_code;
	}

	public void setChannel_ref_code(String channel_ref_code) {
		this.channel_ref_code = channel_ref_code;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public HashMap<String, FileProperty> getAddt_props() {
		return addt_props;
	}

	public void setAddt_props(HashMap<String, FileProperty> addt_props) {
		this.addt_props = addt_props;
	}
}
