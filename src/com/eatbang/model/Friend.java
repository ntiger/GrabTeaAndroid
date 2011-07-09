package com.eatbang.model;

public class Friend extends BaseModel {

	public static final int FRIEND_STATUS_REQUESTED = 0;
	public static final int FRIEND_STATUS_ACCEPTED = 1;
	public static final int FRIEND_STATUS_REJECTED = 2;
	public static final int FRIEND_STATUS_REJECTED_EVER = 3;

	public String friendNickName;

	public int status;

	public String type;

	public String comments;

	public User friendOf;

	public User user;

	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof Friend) {
			Friend o = (Friend) other;
			if (o.user.id.equals(this.user.id)
					&& o.friendOf.id.equals(this.friendOf.id)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new String(friendOf.id + "" + user.id).hashCode();
	}

}
