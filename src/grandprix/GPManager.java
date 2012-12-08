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
import grandprix.enums.TimerTypeEnum;
import grandprix.gui.GrandPrixFrame;
import grandprix.gui.GrandPrixRace;
import grandprix.gui.GrandPrixRacerPanel;
import grandprix.gui.GrandPrixSetup;
import grandprix.interfaces.CommInterface;
import grandprix.interfaces.GPActionInterface;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GPManager implements GPActionInterface, GPConstants {

  private GrandPrixFrame myFrame = null;
  private CommInterface myComm;

  private PrintWriter myOut = null;

  private HashMap<String, GPRacer> myCars = new HashMap<String, GPRacer>(); // String is ID
  private HashMap<String, List<GPRacer>> myClasses = 
	  new HashMap<String, List<GPRacer>>(); // String (class) to List of Racers.
  private HashSet<String> myClassSet = new HashSet<String>();
  private List<GPHeat> myHeats = new ArrayList<GPHeat>();
  private List<GPHeatData>[] myFileHeats = null; // Heats read from file.

  private File myRacersFile;
  private File myOutFile;

  private boolean myResultsFlag;
  private boolean myAutoAdvance;
  private AudioClip myBeep = null;

  private int myCurrentHeat = 0;
  private GPHeat myDefaultHeat;

  public GPManager(GrandPrixFrame frame) {
    myFrame = frame;
    myComm = null;

    myCars.clear();
    myClasses.clear();
    myClassSet.clear();
    myHeats.clear();

    myRacersFile = null;

    myCurrentHeat = 0;
    myResultsFlag = false;
    
	List<GPRacer> racers = new ArrayList<GPRacer>();
	racers.add(new GPRacer(1, LANE_NAMES[0], "", ContestTypeEnum.NONE, true));
	racers.add(new GPRacer(2, LANE_NAMES[1], "", ContestTypeEnum.NONE, true));
	racers.add(new GPRacer(3, LANE_NAMES[2], "", ContestTypeEnum.NONE, true));
	racers.add(new GPRacer(4, LANE_NAMES[3], "", ContestTypeEnum.NONE, true));
	myDefaultHeat = new GPHeat(0, new GPHeatData("1,2,3,4"), racers);
  }

  public void initialize() {
    myCars.clear();
    myClasses.clear();
    myClassSet.clear();
    myHeats.clear();

    myRacersFile = null;

    myCurrentHeat = 0;
    myResultsFlag = false;
    println("INITIALIZE");
  }
  
  public void setComm(CommInterface comm) {
	  myComm = comm;
  }

  public void beep() {
    if (myBeep == null) {
      try {
        URL url = new URL(BEEP_FILE);
        myBeep = Applet.newAudioClip(url);
      }
      catch (Exception ex) {
        System.out.println("ERROR" + ex);
      }
    }
    if (myBeep != null)
      myBeep.play();
  }

  public void advance() {
    println("GATE RESET");

    beep();
    if (myAutoAdvance) {
      next();
      clearResults();
    }
  }

  public void setAutoAdvance(boolean b) {
    myAutoAdvance = b;
    println("LOG" + CSV + " AUTO" + CSV + " " + myAutoAdvance);
  }

  public boolean getAutoAdvance() {
    return myAutoAdvance;
  }

  public void printAllResults() {
    if (!myResultsFlag) {
      myResultsFlag = true;
      printResults();
      printRacers();
    }
  }

  public void exit() {
    printAllResults();
    closeFile();
    println("LOG" + CSV + " Shutting down.");
  }

  public void closeFile() {
    if (myOut != null) {
      println("LOG" + CSV + "File Closed.");
      myOut.flush();
      myOut.close();
    }
  }

  public void setOutFile(File file) {
    myOutFile = file;
    GrandPrixRace race = getRace();

    closeFile();
    String fname = file.getAbsolutePath();
    try {
      boolean append = true;
      myOut = new PrintWriter(new BufferedWriter(new FileWriter(file, append)));

      if (myOut == null) {
        if (race != null) {
          race.showDialog("Unable to open output file: " + fname);
        }
      }
      else {
        println("LOG" + CSV + "File Opened: " + fname);
      }
    }
    catch (IOException ex) {
      if (race != null) {
        race.showDialog("Unable to open output file: " + fname);
      }
    }
  }

  public void println(String line) {
    Date time = new Date();
    if (myOut != null) {
      myOut.println(time + CSV + line);
      myOut.flush();
      if (DEBUG_LOG_TO_STDOUT) System.out.println("Printing: " + line);
    }
    else {
      System.out.println("Unable to print: " + line);
    }
  }

  public void println(Object obj) {
    println(obj.toString());
  }

  public File getHeatFile(File inFile) {
    String name = "Heats_all.csv";
    return new File(inFile.getParentFile(), name);
  }

  // ----------------------------------------------------------------------
  public boolean openRacers(File file) {
    GrandPrixSetup setup = myFrame.getSetup();
    if (setup != null) {
      setup.setRacerFileName(file);
      return true;
    }
    return false;
  }

  public boolean setRacers(String fname) {
    return setRacers(new File(fname));
  }

  public boolean setRacers(File inFile) {
    printAllResults();
    println("SET RACERS FILE" + CSV + inFile.getAbsolutePath());
    myRacersFile = inFile;

    GPInputFile file = new GPInputFile();
    List<GPRacer> cars = file.readCars(inFile);

    if (!file.getReadCarsOK()) {
      return setRacersBase(new ArrayList<GPRacer>());
    }

    return setRacersBase(cars);
  }

  public boolean setRacers(List<GPRacer> cars) {
    printAllResults();
    println("SET RACERS" + CSV + cars.size());

    return setRacersBase(cars);
  }

  public boolean setRacersBase(List<GPRacer> cars) {
    boolean status = true;

    if ((cars == null) || (cars.size() <= 0)) {
      println("LOG" + CSV + " No cars found, possible error");
      GrandPrixRace race = getRace();
      if (race != null) {
        race.showDialog("No cars found!  See log.");
      }
      cars = new ArrayList<GPRacer>();
      status = false;
    }
    myCars.clear();
    myClasses.clear();
    myClassSet.clear();
    myCurrentHeat = 0;

    for (GPRacer r : cars) {
      String cname = r.getClassName();
      List<GPRacer> racers = (List<GPRacer>) myClasses.get(cname);
      if (racers == null) {
        racers = new ArrayList<GPRacer>();
        myClasses.put(cname, racers);
        myClassSet.add(cname);
      }

      String key = r.getID();
      myCars.put(key, r);
	  if (r.getIsPresent()) {
         racers.add(r);
	  }
    }
    setRacerPanel();

    return status;
  }
  
  public void setRacerPanel() {
    GrandPrixRacerPanel racerPanel = getRacerPanel();

    if (racerPanel != null) {
      racerPanel.updateCars();
      racerPanel.clear();
      racerPanel.setClasses(myClassSet.toArray(new String[0]));

      for (GPRacer racer : getCars(false)) {
        racerPanel.addRacer(racer);
      }
      racerPanel.layoutPanel();
      racerPanel.setChangesNeedSaving(false);
    }
  }

  public GrandPrixRacerPanel getRacerPanel() {
    if (myFrame != null) {
      return myFrame.getRacerPanel();
    }
    return null;
  }

  public GrandPrixRace getRace() {
    if (myFrame != null) {
      return myFrame.getRace();
    }
    return null;
  }

  public void saveRacers() {
    saveRacers(myRacersFile);
  }

  public void saveRacers(File file) {
    if ((myFrame != null) && (file != null)) {
      try {
        boolean append = false;
        PrintWriter pw = null;
        pw =  new PrintWriter(new BufferedWriter(new FileWriter(file, append)));
        List<GPRacer> cars = getCars(false);
        if (cars != null) {
          for (GPRacer gp : cars) {
            if (gp != null) {
              pw.println(gp.toSaveString());
            }
          }
        }
        pw.flush();
        pw.close();
      } catch (IOException ex) {
        println("LOG" + CSV + " Error saving cars" + CSV + file.getAbsolutePath());
        GrandPrixRace race = getRace();
        if (race != null) {
          race.showDialog("Unable to save cars file!  See log.");
        }
      }

      GrandPrixSetup setup = myFrame.getSetup();
      if (setup != null) {
        setup.setRacerFileName(file);
        
        // TODO: Need to determine when we can just do the following:
        // (The concern is that major [present/car id] changes require a reset.
        //setup.setRacerFileNameBasic(file);
      }
    }
  }
  
  /**
   * Update the name, contestType, and fabrication score from the file.
   */
  public void updateRacersFromFile() {
	    println("UPDATE FROM RACERS FILE" + CSV + myRacersFile.getAbsolutePath());

	    GPInputFile file = new GPInputFile();
	    List<GPRacer> cars = file.readCars(myRacersFile);

	    if (file.getReadCarsOK()) {
	        if ((cars == null) || (cars.size() <= 0)) {
	            println("LOG" + CSV + " No cars found (on update), possible error");
	            GrandPrixRace race = getRace();
	            if (race != null) {
	              race.showDialog("No cars found!  See log.");
	            }
	            cars = new ArrayList<GPRacer>();
	        }

	        for (GPRacer r : cars) {
	          if (r != null) {
	             String key = r.getID();
	             GPRacer cur = myCars.get(key);
	             if (cur != null) {
	                cur.setFabricationScore(r.getFabricationScore());
	                cur.setContestType(r.getContestType());
	                cur.setName(r.getName());
	            }
	          }
	        }
	    }
  }

  // ----------------------------------------------------------------------
  public boolean setHeats(File inFile) {
    printAllResults();

    println("GPManager - SET HEATS" + CSV + inFile.getAbsolutePath());

    boolean status = true;
    GPInputFile file = new GPInputFile();
    myFileHeats = file.readHeats(inFile);

    if (!file.getReadHeatsOK() || !verifyHeatList(myFileHeats)) {
      String fname = inFile.getAbsolutePath();
      println("LOG" + CSV + "Unable to read heat file" + CSV + " " + fname);

      GrandPrixRace race = getRace();
      if (race != null) {
        race.showDialog("Unable to read heat file! See log.");
      }
      status = false;
    }
    return status;
  }

public boolean applyHeats() {
    boolean status = true;

    int pos = 0;
    int num = 0;
    boolean done = false;
    myHeats.clear();

    while (!done) {
      done = true;
      for (String key : myClasses.keySet()) {
        List<GPRacer> cList = myClasses.get(key);
        int csize = cList.size();
        List<GPHeatData> cHeat = myFileHeats[csize];

        if (cHeat != null) {
          int hsize = cHeat.size();

          if (hsize > pos) {
            GPHeatData data = (GPHeatData) cHeat.get(pos);
            GPHeat h = new GPHeat(num, data, cList);
            myHeats.add(h);
            done = false;
            num++;
          }
        }
      }
      pos++;
    }

    GrandPrixRace race = getRace();
    if (race != null) {
      race.setHeatList(myHeats);
    }
    myCurrentHeat = 0;
    loadHeat();
    return status;
  }


  // ----------------------------------------------------------------------
  public void setHeat(int heat) {
    GPHeat gpHeat = getHeat(heat);
    if (gpHeat == null) {
      return;
    }
    println("SET TO HEAT NUM" + CSV + gpHeat.getNum());

    myCurrentHeat = heat;
    loadHeat();
  }

  public void redo() {
    prev();
    load(myCurrentHeat);
    clearResults();
  }

  public void load(int num) {
    myCurrentHeat = num;
    GPHeat heat = getHeat(myCurrentHeat);
    if (heat == null) return;

    for (int i=0; i<NUM_LANES; i++) {
      GPRacer lane = heat.getLane(i);
      if (lane != null) {
        lane.removeResult(heat.getNum());
      }
    }

    setHeat(myCurrentHeat);
  }


  public boolean verifyHeatList(List<GPHeatData>[] heatList) {
    boolean status = true;

    for (int i=1; i<heatList.length; i++) {
      List<GPHeatData> list = heatList[i];

      if (list != null) {
        int n = list.size();
        if (n <= 0) {
          println("LOG" + CSV + "Error; heat list for " + i + " racers has no heats.");
          status = false;
        }
        // println("LOG" + CSV + "Heat list for " + i + " racers has " + n + " heats.");

        int[] cars = new int[i];
        for (int j=0; j<i; j++) cars[j] = 0;

        for (GPHeatData heat : list) {
           int[] lane = heat.getLaneNums();
          if (heat.getValid()) {
            for (int k = 0; k < NUM_LANES; k++) {
              if (lane[k] >= 0) {
                cars[lane[k]]++;
              }
            }
          }
        }

        for (int j=0; j<i; j++) {
          if (cars[j] != cars[0]) {
            println("LOG" + CSV + "Heat " + i + " car " + j +
                    " unfair: " + cars[j] + " != " + cars[0]);
            status = false;
          }
        }
      } else {
        println("LOG" + CSV + "Error; no heats for " + i + " racers.");
        status = false;
      }
    }
    return status;
  }

  public void prev() {
    myCurrentHeat--;
    if (myCurrentHeat < 0) myCurrentHeat = 0;
    setHeat(myCurrentHeat);
  }

  public void next() {
    myCurrentHeat++;
    if (myCurrentHeat > myHeats.size()-1) myCurrentHeat--;
    setHeat(myCurrentHeat);
  }

  public void clearResults() {
    println("CLEAR RESULTS");
    myResultsFlag = false;

    GPHeat heat = getCurrentHeat();
    if (heat != null) heat.clearAllResults();
    showResults();
  }

  // TLM - Need to implement erase file.
  public void eraseResults() {
    myResultsFlag = false;

    GPHeat heat = getCurrentHeat();
    if (heat != null) heat.clearAllResults();
    showResults();
  }

  private static int fakeCnt = 0;

  public void fake() {
	String[] results = new String[1];
    myResultsFlag = false;
    String s = "A=1.234! B=2.345  C=3.456  D=4.567  E=0.000  F=0.000";

    fakeCnt++;
    if (fakeCnt == 2) s = "A=2.234  B=1.345! C=3.456  D=4.567  E=0.000  F=0.000";
    if (fakeCnt == 3) s = "A=4.234  B=3.345  C=1.456! D=4.567  E=0.000  F=0.000";
    if (fakeCnt == 4) s = "A=3.234  B=2.345  C=2.456  D=1.567! E=0.000  F=0.000";
    if (fakeCnt == 5) s = "A=2.434  B=2.145! C=2.656  D=3.567  E=0.000  F=0.000";
    if (fakeCnt == 6) s = "A=3.534  B=2.545  C=2.056! D=2.267  E=0.000  F=0.000";
    if (fakeCnt == 7) s = "A=3.634  B=2.745  C=2.256  D=1.767! E=0.000  F=0.000";
    if (fakeCnt == 8) s = "A=3.734  B=2.045! C=2.156  D=3.067  E=0.000  F=0.000";
    if (fakeCnt > 7) fakeCnt = 0;
    
    results[0] = s;
    setResults(TimerTypeEnum.FastTrack, results);
  }

  public GPHeat getCurrentHeat() {
    return getHeat(myCurrentHeat);
  }

  public List<GPHeat> getHeatList() {
    return new ArrayList<GPHeat>(myHeats);
  }

  public GPHeat getHeat(int num) {
    if ((myHeats == null) || (num >= myHeats.size())) {
        return myDefaultHeat;
    }
    return myHeats.get(num);
  }

  public GPHeat getNextHeat() {
    int heat = myCurrentHeat+1;
    if (heat > myHeats.size()-1) return null;
    return (GPHeat) myHeats.get(heat);
  }

  public void loadHeat() {
    GrandPrixRace race = getRace();
    if (race == null) {
      return;
    }

    race.setClass("");
    race.setNextClass("");

    GPHeat heat = getCurrentHeat();
    GPHeat next = getNextHeat();

    for (int i = 0; i < NUM_LANES; i++) {
      GPRacer racer  = (heat == null) ? null : heat.getLane(i);
      GPRacer nracer = (next == null) ? null : next.getLane(i);

      if (racer == null) {
        race.setCar(i, -1);
        race.setName(i, "", false);
      } else {
    	int num = racer.getHeatCount();
        race.setCar(i, racer.getCar());
        race.setName(i, racer.getName(), (num >= 3));
      }

      if (nracer != null) {
        race.setNextCar(i, nracer.getCar());
      } else {
        race.setNextCar(i, -1);
      }
    }

    if (next != null) {
      GPRacer lane0 = null;
      for (int i=0; i<NUM_LANES; i++) {
        if (lane0 != null) {
          break;
        } else {
          lane0 = next.getLane(i);
        }
      }

      if (lane0 != null) {
        race.setNextClass(lane0.getClassName());
      }
    }

    if (heat != null) {
      GPRacer lane0 = null;
      for (int i = 0; i < NUM_LANES; i++) {
        if (lane0 != null) {
          break;
        } else {
          lane0 = heat.getLane(i);
        }
      }

      if (lane0 != null) {
        race.setClass(lane0.getClassName());
      }
    }

    race.setHeat(myCurrentHeat);
    showResults();
  }

  public void setResults(TimerTypeEnum tt, String[] s) {
    myResultsFlag = false;
    GPHeat heat = getCurrentHeat();
    if (heat == null) return;
    int num = heat.getNum();

    for (int i=0; i<NUM_LANES; i++) {
      GPRacer lane = heat.getLane(i);
      if (lane != null) {
        int carNum = lane.getCar();
        String clas = lane.getClassName();
        GPResult result = new GPResult(num, i, carNum, clas);
        result.parseResults(tt, s);
        
        heat.setResult(result);
        println("SET RESULT" + CSV + result.toCSV());
      }
    }
    showResults();
  }

  public void showResults() {
    GrandPrixRace race = getRace();
    if (race == null) {
      return;
    }
    GPHeat heat = getCurrentHeat();

    for (int i=0; i<NUM_LANES; i++) {
      GPResult result = null;
      if (heat != null) result = heat.getResult(i);

      if ((result != null) && result.isValid()) {
        race.setPlace(i, result.getPlaceNumber());
        race.setTime(i, result.getTime());
      } else {
        race.clearResult(i);
      }
    }
  }

  public void printResults() {
    if (myHeats.size() <= 0) {
      return;
    }

    println("PRINT RESULTS");
    println("TITLE" + CSV + "Heat" + CSV + "Lane" + CSV + "Time" + CSV +
            "Place" + CSV + "Score" + CSV + "Class" + CSV + "Car");

    for (GPHeat heat : myHeats) {
      for (int j=0; j<NUM_LANES; j++) {
        GPResult result = heat.getResult(j);
        if (result != null) {
          println("RESULT" + CSV + result.toCSV());
        }
      }
    }
    println("END PRINT RESULTS");
  }

  public void printRacers() {
    if (myCars.size() <= 0) {
      return;
    }

    println("START PRINT RACERS");
    println("TITLE" + CSV + "Car" + CSV + "Name" + CSV + "Class" + CSV +
            "Score" + CSV + "Best Time" + CSV + "Average Time");

    for (GPRacer r : myCars.values()) {
      if ((r!=null) && r.getIsPresent()) {
        println("RACER" + CSV + r.toCSV());
      }
    }

    println("END PRINT RACERS");
  }

  public List<GPRacer> getCars(boolean presentOnly) {
	GPRacer[] arr = null;
	if (presentOnly) {
		List<GPRacer> rlist = new ArrayList<GPRacer>();
		for (GPRacer r : myCars.values()) {
			if (r.getIsPresent()) rlist.add(r);
		}
		arr = rlist.toArray(new GPRacer[0]);
	} else {
		arr = myCars.values().toArray(new GPRacer[0]);
	}

    Comparator<GPRacer> comp = new Comparator<GPRacer>() {
      public int compare(GPRacer o1, GPRacer o2) {
        if (o1 == o2)
          return 0;
        if (o1 == null)
          return -1;
        if (o2 == null)
          return 1;

        GPRacer r1 = o1;
        GPRacer r2 = o2;
        int x = r1.getCar() - r2.getCar();
        return (int)Math.signum(x);
      }

      public boolean equals(Object obj) {
        return false;
      }
    };
    Arrays.sort(arr, comp);

    return Arrays.asList(arr);
  }

//  public Collection<GPRacer> getCarList() {
//    return myCars.values();
//  }
//
//  public ArrayList<GrandPrixEntryPanel> getRacers() {
//    GrandPrixRacerPanel rp = getRacerPanel();
//    if (rp != null) {
//      return rp.getRacers();
//    }
//    return null;
//  }

  public void setFabricationPlacing() {
	if (myCars.size() == 0) {
      return;
    }

    Comparator<GPRacer> comp = new Comparator<GPRacer>() {
      public int compare(GPRacer o1, GPRacer o2) {
        if (o1 == o2) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        return o1.compareTo(o2, SortByEnum.RacerFabScore, true);
      }

      public boolean equals(Object obj) {
        return false;
      }
    };

    for (String entry : myClasses.keySet()) {
      List<GPRacer> list = myClasses.get(entry);
      if (list != null) {
        double lastScore = -999.9;
        int nextPlace = 0;
        int savePlace = nextPlace;
        GPRacer[] racers = list.toArray(new GPRacer[0]);
        Arrays.sort(racers, comp);
        for (int j=0; j<racers.length; j++) {
          GPRacer r = (GPRacer) racers[j];
          if ((r != null) && (r.getContestType() == ContestTypeEnum.DESIGN)) {
        	  double score = r.getFabricationScore();
        	  if (score > 0.0) {
          	    if (lastScore != score) {
                    nextPlace++;
        		    savePlace = nextPlace;
        		    r.setFabricationPlaceNumber(nextPlace);
        	    } else {
                    nextPlace++;
        		    r.setFabricationPlaceNumber(savePlace);
        	    }
            	lastScore = score;
        	} else {
        		r.setFabricationPlaceNumber(0);
        	}
          }
        }
        
        lastScore = -999.9;
        nextPlace = 0;
        savePlace = nextPlace;
        Arrays.sort(racers, comp);
        for (int j=0; j<racers.length; j++) {
          GPRacer r = (GPRacer) racers[j];
          if ((r != null) && (r.getContestType() == ContestTypeEnum.NOVELTY)) {
        	double score = r.getFabricationScore();
          	if (score > 0.0) {
          	    if (lastScore != score) {
                    nextPlace++;
        		    savePlace = nextPlace;
        		    r.setFabricationPlaceNumber(nextPlace);
        	    } else {
                    nextPlace++;
        		    r.setFabricationPlaceNumber(savePlace);
        	    }
            	lastScore = score;
        	} else {
        		r.setFabricationPlaceNumber(0);
        	}
          }
        }
      }
    }
  }

  public void setSpeedPlacing() {
	if (myCars.size() == 0) {
      return;
    }

    Comparator<GPRacer> comp = new Comparator<GPRacer>() {
      public int compare(GPRacer o1, GPRacer o2) {
        if (o1 == o2) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        return o1.compareTo(o2, SortByEnum.RacerSpeedScore, true);
      }

      public boolean equals(Object obj) {
        return false;
      }
    };

    for (String entry : myClasses.keySet()) {
      double lastScore = -999.9;
      int nextPlace = 0;
      int savePlace = nextPlace;
      List<GPRacer> list = myClasses.get(entry);
      if (list != null) {
    	GPRacer[] racers = list.toArray(new GPRacer[0]);
        Arrays.sort(racers, comp);
        for (int j=0; j<racers.length; j++) {
          GPRacer r = (GPRacer) racers[j];
          if (r != null) {
        	double score = r.getSpeedScore();
        	if (score > 0.0) {
        	  if (lastScore != score) {
                nextPlace++;
        		savePlace = nextPlace;
        		r.setSpeedPlaceNumber(nextPlace);
        	  } else {
                nextPlace++;
        		r.setSpeedPlaceNumber(savePlace);
        	  }
        	  lastScore = score;
        	} else {
      	      r.setSpeedPlaceNumber(0);
      	    }
          }
        }
      }
    }
  }

  public void setAwardPlacing() {
    if (myCars.size() == 0) {
      return;
    }
   
    setSpeedPlacing();
    setFabricationPlacing();
    
    for (String entry : myClasses.keySet()) {
      List<GPRacer> list = myClasses.get(entry);
	  for (GPRacer r : list) {
		  r.setAwardPlaceNumber(99);
		  r.setAwardPlaceType(ContestTypeEnum.NONE.toString());
	  }
	    
      for (int cp=1; cp<6; cp++) {
    	int cur_speed=1000;
    	int cur_design=1000;
    	int cur_novelty=1000;    
	    for (GPRacer r : list) {
	    	int sp = r.getSpeedPlaceNumber();
	    	int fp = r.getFabricationPlaceNumber();
	    	ContestTypeEnum ct = r.getContestType();
	    	if (sp > 0 && sp < cur_speed) cur_speed = sp;
	    	if (fp > 0 && fp < cur_design && ct == ContestTypeEnum.DESIGN) cur_design = fp;
	    	if (fp > 0 && fp < cur_novelty && ct == ContestTypeEnum.NOVELTY) cur_novelty = fp;
	    }
	    
	    for (GPRacer r : list) {
	      int p = r.getSpeedPlaceNumber();
	      if (p == cur_speed) {
		    r.setAwardPlaceType(ContestTypeEnum.SPEED.toString());
	        r.setAwardPlaceNumber(cp);
		    r.setSpeedPlaceNumber(0);
	        r.setFabricationPlaceNumber(0);
	      }
	    }

	    for (GPRacer r : list) {
	      if (r.getFabricationScore() > 0.0) {
		    int p = r.getFabricationPlaceNumber();
		    ContestTypeEnum ct = r.getContestType();
		      
		    if ((p == cur_design && ct == ContestTypeEnum.DESIGN) ||
		    	(p == cur_novelty && ct == ContestTypeEnum.NOVELTY)) {
		      r.setAwardPlaceType(r.getContestType().toString());
			  r.setAwardPlaceNumber(cp);
			  r.setFabricationPlaceNumber(0);
		      r.setSpeedPlaceNumber(0);
		    }
	      }
	    }
      }
    }
    
    setSpeedPlacing();
    setFabricationPlacing();
  }


  public void readProgressFile() {
    if (myOutFile != null) {
      readProgressFile(myOutFile);
    }
  }

  public void readProgressFile(File file) {
    if (file == null) {
      System.out.println("GPManager - No Progress!");
      return;
    }

    // If the ProgressFile is the same as the output file,
    // Turn off progress (so it doesn't loop infinitely).
    //
    PrintWriter outSave = myOut;
    System.out.println("GPManager - OUT: " + myOutFile);
    System.out.println("GPManager - PRO: " + file);
    if (myOutFile.equals(file)) {
      myOut = null;
      System.out.println("GPManager - PROGRESS FILE IS THE SAME");
    }

    clearResults();

    BufferedReader in = null;
    try {
      FileReader reader = new FileReader(file);
      in = new BufferedReader(reader);
      while (in.ready()) {
        String s = in.readLine();

        if (s != null) {
          int i = s.indexOf('#');
          if (i >= 0) {
            s = s.substring(0, i);
          }
          parseProgress(s.trim());
        }
      }
    } catch (FileNotFoundException ex) {
      System.out.println("GPManager - FILE NOT FOUND: " + file);
    } catch (IOException ioex) {
      System.out.println("IOException: " + ioex);
    } finally {
    	if (in != null) {
    		try {
    			in.close();
    		} catch (IOException ex) {
    			// Not much to do really
    		}
    	}
    }
    showResults();

    myOut = outSave;
    println("PROGRESS RELOAD");
  }

  public void parseProgress(String line) {
    if ((line != null) && !line.equals("")) {
      String[] token = line.split(CSV);

      if ((token != null) && (token.length > 1)) {
        String s = token[1].trim();

        if (s.equalsIgnoreCase("RESULT") ||
            s.equalsIgnoreCase("SET RESULT")) {

          GPResult result = new GPResult(token);
          int heatNum = result.getHeat();
          GPHeat heat = getHeat(heatNum);

          if (heat != null) {
            heat.setResult(result);
          }
        } else if (s.equalsIgnoreCase("CLEAR RESULTS")) {
          clearResults();
        } else if (s.equalsIgnoreCase("SET HEATS")) {
          if (token.length > 2) {
            String fname = token[2].trim();
            setHeats(new File(fname));
          }
        } else if (s.equalsIgnoreCase("SET RACERS FILE")) {
          if (token.length > 2) {
            String fname = token[2].trim();
            setRacers(fname);
            applyHeats();
          }
        }
      }
    }
  }

  @Override
  public void resetTimer() {
	  sendTimer("R");
	  next();
  }

  @Override
  public void sendTimer(String s) {
	if (myComm != null) {
		myComm.sendString(s);
	}	
  }
}