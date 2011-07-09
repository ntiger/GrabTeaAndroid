package com.eatbang.model;

import java.util.List;
import java.util.Set;

public class User extends BaseModel {

	public String email;

	public String password;

	public String phone;

	public String uid;

	public String role;

	public String name;

	public boolean isVerified;

	public int cityCode = 010;

	public String iconURI;

	public boolean isActivated;

	public Profile profile;

	public Set<Account> accounts;

	public Set<Friend> friends;

	public Set<Friend> friendedBy;

	public List<Message> sentMessages;

	public List<Message> indbox;

	public List<Party> createdParties;

	public List<Participator> participants;
}
