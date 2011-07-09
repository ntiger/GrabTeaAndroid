package com.eatbang.model;

public class Participator extends BaseModel {

	public static final int PAR_STATUS_INVITED = 0;
	public static final int PAR_STATUS_ACCEPTED = 0;
	public static final int PAR_STATUS_REJECTED = 0;

	public String email;

	public String phone;

	public String name;

	public int status;

	public long lastUpdateTime;

	public String comments;

	public String avatarUri;
}
