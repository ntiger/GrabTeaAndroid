package com.eatbang.model;

public class Message extends BaseModel {

	public String title;

	public String content;

	public User sender;

	public User receiver;

	public boolean isRead;

	public boolean isDeletedBySender;

	public boolean isDeletedByReceiver;

	public Message replyTo;
}
