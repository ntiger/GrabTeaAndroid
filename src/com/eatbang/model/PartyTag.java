package com.eatbang.model;

public class PartyTag extends BaseModel implements Comparable<PartyTag> {

	public String name;

	private PartyTag(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public int compareTo(PartyTag otherTag) {
		return name.compareTo(otherTag.name);
	}

}