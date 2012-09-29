/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/*
 * Copyright (c) 2002-2012 by Ted Meyers
 */
package grandprix;

import grandprix.enums.SortByEnum;
import grandprix.enums.TimerTypeEnum;



/**
 * <p>Title: Grand Prix Timer</p>
 * <p>Description: </p>
 * @author T Meyers
 * @version 1.0
 */

public interface GPConstants {
	  
  // See GrandPrixRace -- myTestModeEnabled
  public static final boolean TEST_MODE_ENABLED = false;
  
  public static final boolean DEBUG_LOG_TO_STDOUT = false;

  public static final String BEEP_FILE =
      "file://C:\\Documents and Settings\\Ted\\jbproject\\GrandPrix\\src\\score.au";
  
  public static final TimerTypeEnum TIMER_TYPE = TimerTypeEnum.FastTrack;
  public static final int FONT_SIZE = 24;
  public static final int MAX_HEAT_SETS = 61;
  
  public static final double MIN_TIME = 0.0;
  public static final double EPS_TIME = 0.001;
  public static final double MAX_TIME = 9.999;
  
  public static final int NONE_PLACE = 0;
  public static final int FIRST_PLACE = 1;
  public static final int SECOND_PLACE = 2;
  public static final int THIRD_PLACE = 3;
  public static final int FOURTH_PLACE = 4;
  public static final int LAST_PLACE = 4;
  
  public static final char FIRST_CHAR = '!';
  public static final char SECOND_CHAR = '"';
  public static final char THIRD_CHAR = '#';
  public static final char FOURTH_CHAR = '$';
  public static final char FIFTH_CHAR = '%';
  public static final char SIXTH_CHAR = '&';
  
  public static final String[] LANE_DISP_NAMES = {"Red", "Blue", "Green", "Yellow"};
  public static final String[] LANE_NAMES = {"RED", "BLUE", "GREEN", "YELLOW"};
  public static final int NUM_LANES = 4;
  
  public static final char GPFILE_COMMENT_CHAR = '#';
  public static final String CSV = ",";
  public static final String CSV_EXT = "csv";
  public static final String CSV_DESC = "CSV";
  
  public static SortByEnum[] RACER_PANEL_SORTS = {
    SortByEnum.RacerNumber, SortByEnum.RacerName, SortByEnum.RacerFabScore, 
    SortByEnum.RacerClass, SortByEnum.RacerContest, SortByEnum.RacerPresent};

 
  public static final String[] TABLE_REPORTS_TITLES = {"Racer Report", "Race Schedule", "Results Report"};

  public static final String[] RACER_COL_NAMES = {
      "Number", "Name", "Score", "Club", "Contest", "Checked In"};
  public static final int RR_NUMBER_IDX = 0;
  public static final int RR_NAME_IDX = 1;
  public static final int RR_FAB_SCORE_IDX = 2;
  public static final int RR_CLUB_IDX = 3;
  public static final int RR_CONTEST_IDX = 4;
  public static final int RR_PRESENT_IDX = 5;
  public static final int RR_COL_SIZE = 6;

  public static final String[] SCHEDULE_COL_NAMES = {
      "Number", LANE_DISP_NAMES[0], LANE_DISP_NAMES[1], 
      LANE_DISP_NAMES[2], LANE_DISP_NAMES[3], "Club"};
  public static final int SR_NUMBER_IDX = 0;
  public static final int SR_LANE_0_IDX = 1;
  public static final int SR_LANE_1_IDX = 2;
  public static final int SR_LANE_2_IDX = 3;
  public static final int SR_LANE_3_IDX = 4;
  public static final int SR_CLUB_IDX = 5;
  public static final int SR_COL_SIZE = 6;

  public static final Object[] TEST_CLASSES = {"A", "B", "C", "D"};

  public static final String[] RESULTS_COL_NAMES = {
      "Number", "Name", "Place", "Contest", "Score", "Average", "Best", "Club", "DS"};
  public static final int TR_NUMBER_IDX = 0;
  public static final int TR_NAME_IDX = 1;
  public static final int TR_PLACE_NUM_IDX = 2;
  public static final int TR_PLACE_TYPE_IDX = 3;
  public static final int TR_SPEED_SCORE_IDX = 4;
  public static final int TR_AVE_IDX = 5;
  public static final int TR_BEST_IDX = 6;
  public static final int TR_CLUB_IDX = 7;
  public static final int TR_DS_IDX = 8;
  public static final int TR_COL_SIZE = 9;

  public static final int RESULTS_R_WIDTH = 9;
  public static final int RESULTS_N_WIDTH = 5;
  public static final int RESULTS_SCORE_MULT = 100;
}