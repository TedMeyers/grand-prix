/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/*
 * Copyright (c) 2002-2012 by Ted Meyers
 */
package grandprix;

import java.util.StringTokenizer;

/**
 * <p>Title: Grand Prix Timer</p>
 * <p>Description: </p>
 * @author T Meyers
 * @version 1.0
 */


public class GPHeatData implements GPConstants {
  private int[] myLaneNums = null;
  private boolean myValid;

  public GPHeatData() {
    myValid = false;
    myLaneNums = new int[NUM_LANES];

    for (int i = 0; i < NUM_LANES; i++) {
      myLaneNums[i] = -1;
    }
  }

  public GPHeatData(String line) {
    this();

    if (line == null) {
      System.out.println("No input for heat!");
      return;
    }

    StringTokenizer tok = new StringTokenizer(line, ",");

    if (tok.countTokens() < 4) {
      System.out.println("Unable to read Heat: " + line);
      return;
    }
    else if (tok.countTokens() > 4) {
      System.out.println("Possible error reading Heat: " + line);
    }

    for (int i = 0; i < NUM_LANES; i++) {
      myLaneNums[i] = Integer.parseInt(tok.nextToken().trim()) - 1;
    }
    myValid = true;
  }

  public boolean getValid() {
    return myValid;
  }

  public int[] getLaneNums() {
    return myLaneNums;
  }
}