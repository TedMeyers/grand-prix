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

import grandprix.enums.ContestTypeEnum;
import grandprix.enums.SortByEnum;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.StringTokenizer;

public class GPRacer implements GPConstants {
  private static int ourUID = 0;
  public int myUID;
  private int myCar = 0;                        // Car number.
  private String myID = "";                     // String form of car.
  private String myName = "";                   // Name of car owner.
  private String myClassName = "";              // Class - Spark/T&T/etc.
  private boolean myIsPresent;                  // Is the racer present to race.
  private ContestTypeEnum myContestType;			// Contest entry type (design/novelty)
  private transient boolean myValid;	            // File read error?
  private transient int mySpeedPlaceNumber;     	// Place in race
  private transient int myFabricationPlaceNumber;	// Place in fabrication contest
  private transient int myAwardPlaceNumber;			// Place award given
  private transient String myAwardPlaceType = "";	// Place type in race (speed, design, novelty)
  private transient double myFabricationScore = 0.0;// Design or novelty score

  private transient HashMap<String, GPResult> myResults = 
	  new HashMap<String, GPResult>();    		// String (HeatNum)

  public GPRacer() {
    this(0, "", "", ContestTypeEnum.DESIGN, false);
  }

  public GPRacer(String line) {
    myValid = false;
    if (line == null) {
      System.out.println("GPRacer - Bad input line to racer: " + myUID);
      return;
    }
    StringTokenizer tok = new StringTokenizer(line, ",");

    if (tok.countTokens() < 3) {
      System.out.println("GPRacer - Unable to read Racer: " + line + ": " + myUID);
      myCar = -1;
      myName = "Error";
      myClassName = "";
      return;
    } else if (tok.countTokens() > 6) {
      System.out.println("GPRacer - Possible error reading racer: " + line + ": " + myUID);
      return;
    }

    myID = tok.nextToken().trim();
    myName = tok.nextToken();
    myClassName = tok.nextToken();

    if (tok.hasMoreTokens()) {
      myIsPresent = tok.nextToken().equalsIgnoreCase("true");
    } else {
      myIsPresent = false;
    }

	myContestType = ContestTypeEnum.NONE;
    if (tok.hasMoreTokens()) {
      String s = tok.nextToken();
      if (s.length() > 0) {
    	  String test = "" + s.charAt(0);
    	  if (test.equalsIgnoreCase("N")) {
    		myContestType = ContestTypeEnum.NOVELTY;
    	  } else if (test.equalsIgnoreCase("D")) {
    		myContestType = ContestTypeEnum.DESIGN;
    	  } else if (test.equalsIgnoreCase("X")) {
      		myContestType = ContestTypeEnum.NONE;
    	  }
      }
    }

    if (tok.hasMoreTokens()) {
      String s = tok.nextToken();
      try {
    	  myFabricationScore = Double.parseDouble(s);
      } catch (NumberFormatException ex) {
    	  myFabricationScore = 0.0;
          System.out.println("GPRacer - Error reading FabScore: " + line + ": " + myUID);
          myValid = false;
        }
    }

    try {
      myCar = Integer.parseInt(myID);
      myUID = ourUID++ * 1000 + myCar;
      myValid = true;
    } catch (NumberFormatException ex) {
      myCar = 0;
      System.out.println("GPRacer - Error reading car number: " + line + ": " + myUID);
      myValid = false;
    }
  }

  public GPRacer(int car, String name, String className, ContestTypeEnum contest, boolean isPresent) {
	myUID = ourUID++ * 1000 + car;
	myID = "" + car;
    myCar = car;
    myName = name;
    myClassName = className;
	myContestType = contest;
    myIsPresent = isPresent;
    myValid = true;
  }

// TODO: Do we really need a copy constructor for this object??
//  public GPRacer(GPRacer other) {
//	myUID = ourUID++ * 1000 + other.myCar;
//  myID = other.myID;
//  myCar = other.myCar;
//  myName = other.myName;
//  myClassName = other.myClassName;
//  myIsPresent = other.myIsPresent;
//  myValid = other.myValid;
//	myContestType = other.myContestType;
//	myFabricationPlaceNumber = other.myFabricationPlaceNumber;
//	myFabricationScore = other.myFabricationScore;
//	mySpeedPlaceNumber = other.mySpeedPlaceNumber;
//	myAwardPlaceNumber = other.myAwardPlaceNumber;
//	myAwardPlaceType = other.myAwardPlaceType;
//  }

  // Returns true iff the passed in values are the same as current values.
  public boolean update(String car, String name, String className, 
		  ContestTypeEnum contest, String fabricationScore, boolean isPresent) {
    int num = 0;
    try {
      num = Integer.parseInt(car);
    } catch (NumberFormatException ex) {
    }
    
    double fabscore = 0.0;
    if (fabricationScore != null) {
  	try {
  	  fabscore = Double.parseDouble(fabricationScore.trim());
  	} catch (NumberFormatException nfex) {
  	}
    }
    
    boolean isSame = (myID.equals(car) && (myCar == num) && 
    		myName.equals(name) && myClassName.equals(className) &&
    		(myContestType == contest) && 
    		// (myFabricationScore == fabscore) &&  // Do not compare fab score
    		(myIsPresent == isPresent));

    myID = car;
    myCar = num;
    myName = name;
    myClassName = className;
    myContestType = contest;
    myFabricationScore = fabscore;
    myIsPresent = isPresent;
    myValid = true;
    
    return isSame;
  }

  public boolean getValid() {
    return myValid;
  }

  // Same as car, but in a String.
  public String getID() {
    return myID;
  }

  public int getCar() {
    return myCar;
  }

  public String getName() {
    return myName;
  }
  
  public void setName(String val) {
	myName = val;
  }

  public String getClassName() {
    return myClassName;
  }
  
  public ContestTypeEnum getContestType() {
	return myContestType;
  }
  
  public void setContestType(ContestTypeEnum contest) {
	  if (myContestType != null) myContestType = contest;
  }

  public boolean getIsPresent() {
    return myIsPresent;
  }

  public void setIsPresent(boolean isPresent) {
    myIsPresent = isPresent;
  }

  public int getSpeedPlaceNumber() {
    return mySpeedPlaceNumber;
  }

  public void setSpeedPlaceNumber(int place) {
	  mySpeedPlaceNumber = place;
  }

  public int getFabricationPlaceNumber() {
    return myFabricationPlaceNumber;
  }
  
  public void setFabricationPlaceNumber(int value) {
	  myFabricationPlaceNumber = value;
  }

  public int getAwardPlaceNumber() {
    return myAwardPlaceNumber;
  }
  
  public void setAwardPlaceNumber(int value) {
	  myAwardPlaceNumber = value;
  }

  public String getAwardPlaceType() {
    return myAwardPlaceType;
  }

  public void setAwardPlaceType(String value) {
    myAwardPlaceType = value;
  }
  
  public double getFabricationScore() {
	return myFabricationScore;
  }
  
  public void setFabricationScore(double value) {
	  myFabricationScore = value;
  }

 public void setResult(GPResult result) {
   String key = "" + result.getHeat();
   myResults.put(key, result);
 }

 public void removeResult(int heat) {
   String key = "" + heat;
   myResults.remove(key);
 }

 public int getHeatCount() {
   int cnt = myResults.size();
   return cnt;
 }

 public double getSpeedScore() {
   double score = 0;
   for (GPResult result : myResults.values()) {
     score += result.getSpeedScore();
   }

   score += (9.999-getMeanTime()) * 6.0;
   score += (9.999-getBestTime()) * 3.0;
   
   return score;
 }

 public double getBestTime() {
   double time = 9.999;
   for (GPResult result : myResults.values()) {
     if (time > result.getTime())
       time = result.getTime();
   }

   return time;
 }

 public double getMeanTime() {
   double time = 0.0;
   int size = myResults.values().size();

   for (GPResult result : myResults.values()) {
     time += result.getTime();
   }

   if (size == 0) return 9.999;
   return (time/size);
 }


 public int compareTo(Object o, SortByEnum sortby, boolean isForward) {
   int dir = (isForward)?1:-1;
   int res = dir;

   if ((o == null) || (o.getClass() != getClass())) {
     return res;
   }
   GPRacer other = (GPRacer) o;

   if (sortby == SortByEnum.RacerNumber) {
     res = dir * compare(myCar, other.myCar);
   } else if (sortby == SortByEnum.RacerName) {
     res = dir * myName.compareTo(other.myName);
   } else if (sortby == SortByEnum.RacerFabScore) {
	     res = -1 * dir * compare(myFabricationScore, other.myFabricationScore);
   } else if (sortby == SortByEnum.RacerClass) {
     res = dir * myClassName.compareTo(other.myClassName);
   } else if (sortby == SortByEnum.RacerPresent) {
     res = dir * compare(myIsPresent, other.myIsPresent);
   } else if (sortby == SortByEnum.RacerContest) {
	     res = dir * compare(myContestType, other.myContestType);
   } else if (sortby == SortByEnum.RacerPlaceNumber) {
     res = dir * compare(getSpeedPlaceNumber(), other.getSpeedPlaceNumber());
   } else if (sortby == SortByEnum.RacerPlaceType) {
	 res = dir * getAwardPlaceType().compareTo(other.getAwardPlaceType());
   } else if (sortby == SortByEnum.RacerSpeedScore) {
     res = -dir * compare(getSpeedScore(), other.getSpeedScore());   // High First
   } else if (sortby == SortByEnum.RacerBestTime) {
     res = dir * compare(getBestTime(), other.getBestTime());
   } else if (sortby == SortByEnum.RacerAveTime) {
     res = dir * compare(getMeanTime(), other.getMeanTime());
   }

   return res;
 }

 protected int compare(ContestTypeEnum n1, ContestTypeEnum n2) {
   if (n1.ordinal() < n2.ordinal()) return -1;
   if (n1.ordinal() > n2.ordinal()) return 1;
   return 0;
 }

 protected int compare(boolean n1, boolean n2) {
   if (n1 && !n2) return -1;
   if (!n1 && n2) return 1;
   return 0;
 }

 protected int compare(int n1, int n2) {
   if (n1 < n2) return -1;
   if (n1 > n2) return 1;
   return 0;
 }

 protected int compare(double n1, double n2) {
   if (n1 < n2) return -1;
   if (n1 > n2) return 1;
   return 0;
 }

public String toCSVSave() {
   String present = (myIsPresent) ? "true" : "false";
   String contest = myContestType.toString();

   return "" + myCar + CSV + myName + CSV + myClassName + CSV + present + CSV + contest + CSV + myFabricationScore;
 }

 public String toCSV() {
   DecimalFormat df = new DecimalFormat("0.000");
   String mean = df.format(getMeanTime());
   String fscore = df.format(getFabricationScore());

   return "" + myCar + CSV + myName + CSV + myClassName +
       CSV + getSpeedScore() + CSV + getBestTime() + CSV + mean + CSV + fscore;
 }


 public String toSaveString() {
   String contest = myContestType.toString();
   String s = "";
   s += "" + myID + CSV + myName + CSV + myClassName + CSV + myIsPresent + CSV + contest + CSV + myFabricationScore;
   return s;
 }

 public String toString() {
   DecimalFormat df = new DecimalFormat("0.000");
   String mean = df.format(getMeanTime());
   String fscore = df.format(getFabricationScore());
   
   return myClassName + ": " + myCar + " " + myName +
       " = " + getSpeedScore() +
       " (best = " + getBestTime() + " average = " + mean +  "fscore = " + fscore + ")";
 }

// public String toHTMLResultsString() {
//   DecimalFormat df1 = new DecimalFormat("0.000");
//   DecimalFormat df2 = new DecimalFormat("0.0");
//   String ave = df1.format(getMeanTime());
//   String best = df1.format(getBestTime());
//   String score = df2.format(getSpeedScore());
//   String fscore = df1.format(getFabricationScore());
//
//   int n = getSpeedPlaceNumber();
//   String place = ((n > 0) && (n <= 4)) ? ("" + n) : "";
//   //String place = (n > 0) ? ("" + n) : "";
//
//   String s = "";
//   s += "<TR>";
//   s += "<TD><font face=arial size=3>" + myCar + "</font></TD>";
//   s += "<TD><font face=arial size=3>" + myName + "</font></TD>";
//   s += "<TD><font face=arial size=3>" + place + "</font></TD>";
//   s += "<TD><font face=arial size=3>" + myAwardPlaceType + "</font></TD>";
//   s += "<TD><font face=arial size=3>" + score + "</font></TD>";
//   s += "<TD><font face=arial size=3>" + ave + "</font></TD>";
//   s += "<TD><font face=arial size=3>" + best  + "</font></TD>";
//   s += "<TD><font face=arial size=3>" + fscore + "</font></TD>";
//   s += "<TD><font face=arial size=3>" + myClassName + "</font></TD>";
//   s += "</TR>\n";
//
//   return s;
// }
//
// public String toHTMLEntryString() {
//   String s = "";
//   String present = "";
//   String contest = myContestType.toString();
//
//   if (myIsPresent) {
//     present = "*";
//   }
//   if (!myValid) {
//     present += " (error)";
//   }
//
//   s += "<TR>";
//   s += "<TD><font face=arial size=3>" + myCar + "</font></TD>";
//   s += "<TD><font face=arial size=3>" + myName + "</font></TD>";
//   s += "<TD><font face=arial size=3>" + myClassName + "</font></TD>";
//   s += "<TD align=center><font face=arial size=3>" + contest + "</font></TD>";
//   s += "<TD align=center><font face=arial size=3>" + present + "</font></TD>";
//   s += "</TR>\n";
//
//   return s;
// }
}