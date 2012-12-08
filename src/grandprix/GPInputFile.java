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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class GPInputFile implements GPConstants  {
  private boolean myIsReadCarsOK;
  private boolean myIsReadHeatsOK;

  public GPInputFile() {
  }

  public boolean getReadCarsOK()
  {
    return myIsReadCarsOK;
  }

  public boolean getReadHeatsOK()
  {
    return myIsReadHeatsOK;
  }

  public ArrayList<GPRacer> readCars(String fname) {
    try {
      return readCars(new FileReader(fname));
    } catch (FileNotFoundException ex) {
      System.out.println("FILE NOT FOUND: " + fname);
      return new ArrayList<GPRacer>();
    }
  }

  public ArrayList<GPRacer> readCars(File file) {
    try {
      return readCars(new FileReader(file));
    } catch (FileNotFoundException ex) {
      System.out.println("FILE NOT FOUND: " + file);
      return new ArrayList<GPRacer>();
    }
  }

  //
  // Returns HashMap of className (String) to List of GPRacers.
  //
  public ArrayList<GPRacer> readCars(FileReader reader) {
    myIsReadCarsOK = true;
    ArrayList<GPRacer> cars = new ArrayList<GPRacer>();

    try {
      BufferedReader in = new BufferedReader(reader);

      while (in.ready()) {
        String s = in.readLine();

        if (s != null) {
          int i = s.indexOf(GPFILE_COMMENT_CHAR);
          if (i >= 0) {
            s = s.substring(0, i);
          }
          s = s.trim();
          boolean isComment = s.startsWith("!");

          if (!"".equals(s) && !isComment) {
            GPRacer racer = new GPRacer(s);
            if (racer.getValid()) {
              cars.add(racer);
            } else {
              myIsReadCarsOK = false;
            }
          }
        }
      }
      in.close();

    } catch (Exception ex) {
      myIsReadCarsOK = false;
    }

    return cars;
  }

  @SuppressWarnings("unchecked")
public List<GPHeatData>[] readHeats(String fname) {
    try {
      return readHeats(new FileReader(fname));
    } catch (FileNotFoundException ex) {
      System.out.println("FILE NOT FOUND: " + fname);
      return
        (List<GPHeatData>[]) Array.newInstance((new ArrayList<GPHeatData>()).getClass(), 0);
    }
  }

  @SuppressWarnings("unchecked")
  public List<GPHeatData>[] readHeats(File file) {
    try {
      return readHeats(new FileReader(file));
    } catch (FileNotFoundException ex) {
      System.out.println("FILE NOT FOUND: " + file);
      return //new List<GPHeatData>[0];
        (List<GPHeatData>[]) Array.newInstance((new ArrayList<GPHeatData>()).getClass(), 0);
    }
  }

  // Returns an array of lists of heats.
  // Array is indexed by number of cars.
  // So, heats for 2 cars would be at index 2,
  // 3 cars at index 3, etc.
  //
  @SuppressWarnings("unchecked")
public List<GPHeatData>[] readHeats(FileReader reader) {
    myIsReadHeatsOK = true;
    List<GPHeatData> test = new ArrayList<GPHeatData>();
    List<GPHeatData>[] heatList = //new ArrayList<GPHeatData>[MAX_HEAT_SETS];
      (List<GPHeatData>[]) Array.newInstance(test.getClass(), MAX_HEAT_SETS);
    int cur = 0;

    for (int i=0; i<MAX_HEAT_SETS; i++) {
      heatList[i] = null;
    }

    try {
      BufferedReader in = new BufferedReader(reader);

      while (in.ready()) {
        String s = in.readLine();

        if (s != null) {
          int i = s.indexOf(GPFILE_COMMENT_CHAR);
          if (i >= 0) {
            s = s.substring(0, i);
          }
          s = s.trim();

          if (!"".equals(s)) {
            StringTokenizer tok = new StringTokenizer(s, CSV);

            if (tok.countTokens() == 1) {
              cur = Integer.parseInt(tok.nextToken().trim());
              if ((cur < 1) || (cur > 256)) {
                cur = 0;
                System.out.println("Possible error reading Heat Set: " + s);
              }
              if (heatList[cur] == null) {
                heatList[cur] = new ArrayList<GPHeatData>();
              }
            } else {
              GPHeatData h = new GPHeatData(s);
              if (h.getValid()) {
                heatList[cur].add(h);
              } else {
                myIsReadHeatsOK = false;
              }
            }
          }
        }
      }
      in.close();
    } catch (IOException ex) {
      myIsReadHeatsOK = false;
      System.out.println("ERROR reading heat file!");
    }

    return heatList;
  }
}