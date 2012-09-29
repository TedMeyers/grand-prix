/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/*
 * Copyright (c) 2002-2012 by Ted Meyers
 */

package grandprix;

/**
 * <p>Title: Grand Prix Timer</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author T Meyers
 * @version 1.0
 */

import grandprix.enums.SortByEnum;

import java.util.List;

public class GPHeat implements GPConstants {
  private int myNum = 0;
  private GPHeatData myGPHeatData = null;
  private GPRacer[] myLanes = null;
  private GPResult[] myResults = null;

  public GPHeat(int num, GPHeatData data, List<GPRacer> racerArray) {
    myNum = num;
    myGPHeatData = data;
    myLanes = new GPRacer[NUM_LANES];
    myResults = new GPResult[NUM_LANES];

    if (myGPHeatData != null) {
      int[] laneNums = myGPHeatData.getLaneNums();
      for (int i = 0; i < myLanes.length; i++) {
        int lane = laneNums[i];
        if (lane >= 0) {
          myLanes[i] = racerArray.get(lane);
        } else {
          myLanes[i] = null;
        }
      }
    }
  }

  public boolean getValid() {
    if (myGPHeatData == null) {
      return false;
    }
    return myGPHeatData.getValid();
  }

  public void clearAllResults() {
    if (myResults != null) {
      for (int i = 0; i < NUM_LANES; i++) {
    	if (myResults[i] != null) myResults[i].set(MAX_TIME, 0);
        myResults[i] = null;
      }
    }
  }

  public GPResult getResult(int lane) {
    if (myResults == null) {
      return null;
    }
    return myResults[lane];
  }

  public void setResult(GPResult result) {
    int lane = result.getLane();
    GPRacer racer = getLane(lane);
    racer.setResult(result);
    myResults[lane] = result;
  }

  public GPRacer getLane(int i) {
    return myLanes[i];
  }

  public int getNum() {
    return myNum;
  }

  public int compareTo(Object o, SortByEnum sortby, boolean isForward) {
  int dir = (isForward)?1:-1;
  int res = dir;

  if ((o == null) || (o.getClass() != getClass())) {
    return res;
  }
  GPHeat other = (GPHeat) o;

  if (sortby==SortByEnum.HeatNumber) {
    res = dir * compare(myNum, other.myNum);

  } else if (sortby==SortByEnum.RacerClass) {
    GPRacer r1 = null;
    GPRacer r2 = null;
    for (int j=0; j<NUM_LANES; j++) {
      if (r1 == null) r1 = getLane(j);
      if (r2 == null) r2 = other.getLane(j);
    }
    if (r1 != null) {
      res = dir * r1.compareTo(r2, sortby, isForward);
    }
  } else {
    System.out.println("Warning, unable to sort heat by: " + sortby);
  }

  return res;
}

protected int compare(int n1, int n2) {
  if (n1 < n2) return -1;
  if (n1 > n2) return 1;
  return 0;
}

  public String toString() {
    String s = "" + myNum + ": ";
    for (int i=0; i<myLanes.length; i++) {
      GPRacer racer = getLane(i);
      if (racer != null) {
        s += " " + racer.getCar();
      }
    }
    return s;
  }

  public String getClassName() {
    for (int i = 0; i < myLanes.length; i++) {
      GPRacer racer = getLane(i);
      if (racer != null) {
        return racer.getClassName();
      }
    }
    return "";
  }
  
//public String toHTMLString() {
//String s = "";
//String club = "";
//s += "<tr>";
//s += "<td> " + (myNum+1) + " </td>";
//
//for (int i=0; i<myLanes.length; i++) {
//  s += "<td>";
//  GPRacer racer = getLane(i);
//  if (racer != null) {
//    s += racer.getCar();
//    club = racer.getClassName();
//  }
//  s += "</td>";
//}
//s += "<td>" + club + "</td>";
//s += "</tr>\n";
//
//return s;
//}
}