package com.eatbang.model;


public class Account extends BaseModel {

	public static final int ACCOUNT_TYPE_RENREN = 'r';
	public static final int ACCOUNT_TYPE_WEIBO = 'w';

	public User user;

	public int accountType;

	public String accountID;

	public String password;

}
