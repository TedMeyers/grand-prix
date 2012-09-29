/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/*
 * Copyright (c) 2002-2012 by Ted Meyers
 */
package grandprix;

import grandprix.enums.TimerTypeEnum;




/**
 * <p>Title: Grand Prix Timer</p>
 * <p>Description: </p>
 * @author T Meyers
 * @version 1.0
 */

public class GPResult implements Comparable<GPResult>, GPConstants {

  private double myTime = MIN_TIME;
  private int myLane = 0;
  private int mySpeedScore = 0;
  private int myPlaceNum = NONE_PLACE;
  private int myHeat = 0;
  private int myCar = 0;
  private String myClass = "";
  private boolean myIsFirst = false;
  private boolean myIsSecond = false;
  private boolean myIsThird = false;
  private boolean myIsFourth = false;
  private boolean myIsValid = false;


  public GPResult(String[] token) {
    parseCSV(token);
  }

  public GPResult(int heat, int lane, double time, int place, int car, String clas) {
    myHeat = heat;
    myLane = lane;
    myTime = time;
    mySpeedScore = RESULTS_SCORE_MULT * (NUM_LANES-place);
    myPlaceNum = place;
    myCar = car;
    myClass = clas;
    myIsValid = (myTime != MAX_TIME);
  }

  public GPResult(int heat, int lane, int car, String clas) {
   myIsValid = false;
   myHeat = heat;
   myLane = lane;
   myCar = car;
   myClass = clas;
   
   myPlaceNum = NONE_PLACE;
   myTime = MAX_TIME;
  }
 
  
  public void parseResults(TimerTypeEnum tt, String[] results) {
	  switch(tt) {
	  	case FastTrack:
		  parseResultsFT(results[0]);
		  break;
	  	case RaceMaster:
		  parseResultsRM(results);
		  break;
	  	default:
	  	  System.out.println("Should not be here: GPResult");
	  	  break;
	  }
  }

  /**
   * Parse RaceMaster Timer results
   * @param results
   */
  public void parseResultsRM(String[] results) {
	  myIsValid = false;
	  myTime = MAX_TIME;
	  myPlaceNum = LAST_PLACE;
	  mySpeedScore = 0;
	  
	  for (String line: results) {
		  if (line != null) {
			  try {
				  String[] arr = line.split(":");
				  if (arr.length == 3) {
					  int place = Integer.parseInt(arr[0].trim());
					  int lane = Integer.parseInt(arr[1].trim())-1;
					  double time = Double.parseDouble(arr[2].trim());
					  if (myTime <= EPS_TIME) {
						  myIsValid = false;
						  myTime = MAX_TIME;
						  myPlaceNum = LAST_PLACE;
						  mySpeedScore = 0;
					  }
					  if (lane == myLane) {
						  myIsValid = true;
						  myPlaceNum = place;
						  myTime = time;
						  mySpeedScore = 0;
					  }
				  } else {
					  System.out.println("GPResult - Skip: [" + line + "]");
				  }
			  } catch (Exception ex) {
				  myIsValid = false;
				  myTime = MAX_TIME;
				  myPlaceNum = LAST_PLACE;
				  mySpeedScore = 0;
				  System.out.println("Parse problem: " + line);
			  }
		  }
	  }
	  
  }
  
  /**
   * Parse FastTrack Timer results
   * @param result
   */
  public void parseResultsFT(String result) {
    try {
		int start = 0;
	    if (result.charAt(0) == '@') {
	      start = 1;
	    }
	    
	    myPlaceNum = LAST_PLACE;
	    int primaryPlace = NONE_PLACE;
	    int beg = myLane * RESULTS_R_WIDTH + start + 2;
	    myIsFirst = (result.charAt(beg + RESULTS_N_WIDTH) == FIRST_CHAR);
	    myIsSecond = (result.charAt(beg + RESULTS_N_WIDTH) == SECOND_CHAR);
	    myIsThird = (result.charAt(beg + RESULTS_N_WIDTH) == THIRD_CHAR);
	    myIsFourth = (result.charAt(beg + RESULTS_N_WIDTH) == FOURTH_CHAR);
	    if (myIsFirst) primaryPlace = FIRST_PLACE;
	    if (myIsSecond) primaryPlace = SECOND_PLACE;
	    if (myIsThird) primaryPlace = THIRD_PLACE;
	    if (myIsFourth) primaryPlace = FOURTH_PLACE;
	
	    String timeStr = result.substring(beg, beg + RESULTS_N_WIDTH);
	    myTime = Double.parseDouble(timeStr);
	    myIsValid = true;
	    if (myTime == MIN_TIME) {
	      myTime = MAX_TIME;
	      primaryPlace = LAST_PLACE;
	      //myIsValid = false;
	    } else if (myTime <= EPS_TIME) {
	      myTime = MAX_TIME;
	      primaryPlace = LAST_PLACE;
		  //myIsValid = false;
	    } else if (myTime == MAX_TIME) {
		  primaryPlace = LAST_PLACE;
	    }
	
	    if (primaryPlace != NONE_PLACE) {
	      myPlaceNum = primaryPlace;
	    } else if (!myIsFirst) {
	      myPlaceNum = FIRST_PLACE;
	      for (int i = 0; i < NUM_LANES; i++) {
	        if (i != myLane) {
	          beg = i * RESULTS_R_WIDTH + start + 2;
	          timeStr = result.substring(beg, beg + RESULTS_N_WIDTH);
	          double time = Double.parseDouble(timeStr);
	          if (time <= EPS_TIME) {
	              time = MAX_TIME;
	  	      }
	          if (myTime > time) {
	            myPlaceNum++;
	          }
	        }
	      }
	
	      // First place non-tie.
	      if (myPlaceNum <= FIRST_PLACE) {
	        myPlaceNum = SECOND_PLACE;
	      }
	    }
    } catch (Exception ex) {
    	myIsValid = false;
	    myTime = MAX_TIME;
	    myPlaceNum = LAST_PLACE;
	    System.out.println("----- Unable to read results! -----");
   }
    mySpeedScore = RESULTS_SCORE_MULT * (1 + NUM_LANES - myPlaceNum);
 }

 public boolean isValid() {
	 return myIsValid;
 }
 
 public int getLane() {
   return myLane;
 }

  public int getHeat() {
    return myHeat;
  }

  public double getTime() {
    return myTime;
  }

  public int getPlaceNumber() {
    return myPlaceNum;
  }

  public int getSpeedScore() {
    return mySpeedScore;
  }

  public void set(double time, int score) {
    myTime = time;
    mySpeedScore = RESULTS_SCORE_MULT * score;
    myIsValid = (myTime != MAX_TIME);
  }

  public int hashCode() {
    return myHeat;
  }

  public boolean equals(Object o) {
    if (o.getClass() != this.getClass()) return false;
    int heat = ((GPResult) o).getHeat();

    return (myHeat == heat);
  }

  public int compareTo(GPResult o) throws ClassCastException {
    if (o.getClass() != this.getClass()) throw new ClassCastException();
    int heat = o.getHeat();

    if (myHeat < heat) return -1;
    if (myHeat > heat) return 1;
    return 0;
  }

  public void parseCSV(String[] token) {
    //String s = token[1].trim();
    if ((token != null) && (token.length == 9)) {
      myHeat = Integer.parseInt(token[2].trim());
      myLane = Integer.parseInt(token[3].trim());
      myTime = Double.parseDouble(token[4].trim());
      myPlaceNum = Integer.parseInt(token[5].trim());
      mySpeedScore = Integer.parseInt(token[6].trim());
      myClass = token[7].trim();
      myCar = Integer.parseInt(token[8].trim());
      myIsValid = true;
    } else {
      myIsValid = false;
    }
  }

  public String toCSV() {
    return "" + myHeat + CSV + myLane + CSV +
                myTime + CSV + myPlaceNum + CSV + 
                getSpeedScore() + CSV + myClass + CSV + myCar;
  }

  public String toString() {
    return "heat: " + myHeat + " lane: " + myLane +
           " time: " + myTime + " place: " + myPlaceNum + " "  + 
           " score: " + getSpeedScore() + "class: " + myClass +
           "  car: " + myCar + " valid: " + myIsValid;
  }


}