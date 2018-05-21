package com.arkheion.app.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Profile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7838416785462442884L;

	private int id;
	private String hashkey;
	private LocalDateTime create_date;
	private String name;
	private boolean status;

	public String getHashkey() {
		return hashkey;
	}

	public void setHashkey(String hashkey) {
		this.hashkey = hashkey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
