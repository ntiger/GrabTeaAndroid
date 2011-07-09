package com.eatbang.model;

public class LocationTag extends BaseModel implements Comparable<LocationTag> {

	public static final int TAG_TYPE_PLACE = 10000;

	public String name;

	public int type;

	public LocationTag(String name, int type) {
		this.name = name;
		this.type = type;
	}

	public String toString() {
		return name;
	}

	public int compareTo(LocationTag otherTag) {
		if (type == otherTag.type) {
			return name.compareTo(otherTag.name);
		} else {
			return type - otherTag.type;
		}
	}

}