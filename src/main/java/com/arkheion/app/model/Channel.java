package com.arkheion.app.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Channel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8740549902642447634L;
	private long id;
	private String name;
	private long profile_id;
	private LocalDateTime create_date;
	private long implement_id;
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

	public long getProfile_id() {
		return profile_id;
	}

	public void setProfile_id(long profile_id) {
		this.profile_id = profile_id;
	}

	public LocalDateTime getCreate_date() {
		return create_date;
	}

	public void setCreate_date(LocalDateTime create_date) {
		this.create_date = create_date;
	}

	public long getImplement_id() {
		return implement_id;
	}

	public void setImplement_id(long implement_id) {
		this.implement_id = implement_id;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
}
