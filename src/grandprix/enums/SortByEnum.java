/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/*
 * Copyright (c) 2002-2012 by Ted Meyers
 */
package grandprix.enums;

public enum SortByEnum {
	None("None"),
	HeatNumber("Heat-Number"),
	RacerClass("Club"),
	RacerNumber("Car-Number"),
	RacerName("Name"),
	RacerFabScore("Score"),
	RacerPresent("Checked-in"),
	RacerContest("Contest"),
	RacerPlaceNumber("Place-Number"),
	RacerPlaceType("Place-Type"),
	RacerSpeedScore("Speed-Score"),
	RacerBestTime("Best-Time"),
	RacerAveTime("Average-Time");
	
	private String myName;
	
	SortByEnum(String name) {
		myName = name;
	}
	
	public static SortByEnum getByName(String name) {
		for (SortByEnum item : SortByEnum.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		return SortByEnum.None;
	}
	
	@Override
	public String toString() {
		return myName;
	}
}
