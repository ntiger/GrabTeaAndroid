package com.eatbang.model;

import java.util.Date;

public class TimeSlot extends BaseModel {

	public TimeSlot(long timeInLong) {
		this.timeInLong = timeInLong;
	}

	public long timeInLong;

	public Date getDate() {
		return new Date(timeInLong);
	}
}
