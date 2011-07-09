package com.eatbang.model;

import java.util.Date;

public class BaseModel {

	public Long id;

	public Long getId() {
		return id;
	}

	public Date createDate;

	public Date updateDate;

	public Date deletedDate;

	public boolean isDeleted;
}
