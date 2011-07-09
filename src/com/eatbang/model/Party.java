package com.eatbang.model;

import java.util.Set;
import java.util.TreeSet;

public class Party extends BaseModel {

	public static final int PARTY_PERIOD_TIME_60MIN = 1;
	public static final int PARTY_PERIOD_TIME_30MIN = 2;
	public static final int PARTY_PERIOD_TIME_15MIN = 4;

	static final long PARTY_SHOW_CODE_SECRET = 2011209;
	static final long PARTY_INVITE_CODE_SECRET = 2011203;
	static final long PARTY_EDIT_CODE_SECRET = 20111122;

	public static final int CODE_TYPE_SHOW = 0;
	public static final int CODE_TYPE_EDIT = 1;
	public static final int CODE_TYPE_INVITE = 2;

	public static final int COST_LEVEL_ANY = 0;
	public static final int COST_LEVEL_NONE = 1;
	public static final int COST_LEVEL_50 = 2;
	public static final int COST_LEVEL_100 = 3;
	public static final int COST_LEVEL_200 = 4;
	public static final int COST_LEVEL_500 = 5;
	public static final int COST_LEVEL_1000 = 6;
	public static final int COST_LEVEL_5000 = 7;

	public String name;

	public int cityCode;

	public String showCode;

	public String editCode;

	public String inviteCode;

	public String creatorEmail;

	public String creatorName;

	public long lastTime;

	public long firstTime;

	public long finalTime;

	public int status;

	public String bring;

	public String notBring;

	public int periodType;

	public boolean isPublic;

	public long lastCheckTime;

	public String location;

	public int guestMaxNumber;

	public int costLevel;

	public String description;

	public User creator;

	public Set<TimeSlot> timeSlots;

	public Set<Participator> participators;

	public Set<LocationTag> locationTags = new TreeSet<LocationTag>();

	public Set<PartyTag> partyTags = new TreeSet<PartyTag>();

}
