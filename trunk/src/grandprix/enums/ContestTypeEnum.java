/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/*
 * Copyright (c) 2002-2012 by Ted Meyers
 */
package grandprix.enums;

public enum ContestTypeEnum {
	NONE("none"),
	SPEED("speed"),
	DESIGN("design"),
	NOVELTY("novelty");
	
	private String myName;
	
	ContestTypeEnum(String name) {
		myName = name;
	}
	
	@Override
	public String toString() {
		return myName;
	}
}
