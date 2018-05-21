package com.arkheion.app.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Implementation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2809090604246470936L;
	private long id;
	private String name;
	private LocalDateTime create_date;
	private boolean status;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getCreate_date() {
		return create_date;
	}

	public void setCreate_date(LocalDateTime create_date) {
		this.create_date = create_date;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
}
