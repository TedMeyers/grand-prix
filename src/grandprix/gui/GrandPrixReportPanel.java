/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/*
 * Copyright (c) 2002-2012 by Ted Meyers
 */
package grandprix.gui;

/**
 * DEPRECATED!!!
 * @author T Meyers
 * @version 1.0
 */
public class GrandPrixReportPanel { // extends JPanel implements GPConstants, Printable {
//  private static final long serialVersionUID = -3667008908611425637L;
//
//  private JEditorPane myEntryPanel = null;
//  private JComboBox mySort = null;
//
//  private GPManager myGPManager;
//
//  private SortByEnum myRacerSort1;
//  private SortByEnum myScheduleSort1;
//  private SortByEnum myResultsSort1;
//  private SortByEnum myRacerSort2;
//  private SortByEnum myScheduleSort2;
//  private SortByEnum myResultsSort2;
//
//  private transient SortByEnum mySortIndex;
//  private transient boolean mySortOff;
//
//  private ReportTypeEnum myLastReport;

//  public GrandPrixReportPanel(GPManager man) {
//    super(new BorderLayout());
//
//    myGPManager = man;
//    myLastReport = ReportTypeEnum.NONE;
//
//    myRacerSort1 = SortByEnum.None;
//    myScheduleSort1 = SortByEnum.None;
//    myResultsSort1 = SortByEnum.None;
//
//    myRacerSort2 = SortByEnum.None;
//    myScheduleSort2 = SortByEnum.None;
//    myResultsSort2 = SortByEnum.None;
//
//    myEntryPanel = new JEditorPane("text/html","");
//    JScrollPane scroll = new JScrollPane(myEntryPanel);
//
//    clear();
//
//    JButton printButton = new JButton("Print...");
//    printButton.addActionListener(new java.awt.event.ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        print();
//      }
//    });
//
//    JButton saveButton = new JButton("Save...");
//    saveButton.addActionListener(new java.awt.event.ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        save();
//      }
//    });
//
//    JButton racerButton = new JButton("Racer Report");
//    racerButton.addActionListener(new java.awt.event.ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        racerReport();
//      }
//    });
//
//    JButton schedButton = new JButton("Schedule Report");
//    schedButton.addActionListener(new java.awt.event.ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        schedReport();
//      }
//    });
//
//    JButton resButton = new JButton("Results Report");
//    resButton.addActionListener(new java.awt.event.ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        resReport();
//      }
//    });
//
//    JLabel label = new JLabel("Sort by ");
//    mySort = new JComboBox();
//    mySort.addItemListener(new ItemListener() {
//        boolean ignore = false;
//        public void itemStateChanged(ItemEvent e) {
//          if (!ignore) {
//            ignore = true;
//            if ( (e != null) && (e.getStateChange() == ItemEvent.SELECTED)) {
//              sortChanged();
//            }
//            ignore = false;
//          }
//        }
//    });
//
//    JPanel buttonPanel = new JPanel(new FlowLayout());
//    buttonPanel.add(label);
//    buttonPanel.add(mySort);
//    buttonPanel.add(racerButton);
//    buttonPanel.add(schedButton);
//    buttonPanel.add(resButton);
//    buttonPanel.add(printButton);
//    buttonPanel.add(saveButton);
//
//    add(BorderLayout.CENTER, scroll);
//    add(BorderLayout.SOUTH, buttonPanel);
//  }

//  public void update() {
//    if (myLastReport == ReportTypeEnum.NONE) {
//      clear();
//    } else if (myLastReport == ReportTypeEnum.RACER) {
//      racerReport();
//    } else if (myLastReport == ReportTypeEnum.SCHEDULE) {
//      schedReport();
//    } else if (myLastReport == ReportTypeEnum.RESULTS) {
//      resReport();
//    }
//  }
//
//  public void clear() {
//    if (mySort != null) {
//      mySort.removeAllItems();
//    }
//    set("No report specified");
//  }
//
//  public void set(String text) {
//    myEntryPanel.setText(text);
//  }
//
//  public void sortChanged() {
//    if (mySortOff) {
//      return;
//    }
//    SortByEnum sortby = SortByEnum.None;
//    if (mySort != null) {
//    	sortby = SortByEnum.getByName(mySort.getSelectedItem().toString());
//    }
//
//    if (myLastReport == ReportTypeEnum.RACER) {
//      myRacerSort2 = myRacerSort1;
//      myRacerSort1 = sortby;
//    } else if (myLastReport == ReportTypeEnum.SCHEDULE) {
//      myScheduleSort2 = myScheduleSort1;
//      myScheduleSort1 = sortby;
//    } else if (myLastReport == ReportTypeEnum.RESULTS) {
//      myResultsSort2 = myResultsSort1;
//      myResultsSort1 = sortby;
//    }
//    update();
//  }
//
//  private void setSortMenu(String[] arr, ReportTypeEnum reportNum, SortByEnum sortby) {
//    if ((mySort != null) && (myLastReport != reportNum)) {
//      myLastReport = reportNum;
//      mySortOff = true;
//      mySort.removeAllItems();
//      for (int i=0; i<arr.length; i++) {
//        mySort.addItem(arr[i]);
//      }
//      mySort.setSelectedItem(sortby.toString());
//      mySortOff = false;
//    }
//    set("");
//  }

//private void racerSort(GPRacer[] arr, SortByEnum index) {
//    mySortIndex = index;
//    if (mySortIndex!=SortByEnum.RacerNumber && mySortIndex!=SortByEnum.RacerName &&
//    	mySortIndex!=SortByEnum.RacerClass && mySortIndex!=SortByEnum.RacerPresent) {
//    	mySortIndex = SortByEnum.RacerNumber;
//    }
//
//    Comparator<GPRacer> comp = new Comparator<GPRacer>() {
//      public int compare(GPRacer o1, GPRacer o2) {
//        if (o1 == o2) return 0;
//        if (o1 == null) return -1;
//        if (o2 == null) return 1;
//        return o1.compareTo(o2, mySortIndex, true);
//      }
//      public boolean equals(Object obj) {
//        return false;
//      }
//    };
//    Arrays.sort(arr, comp);
//  }

//  private void schedSort(GPHeat[] arr, SortByEnum sortby) {
//    mySortIndex = sortby;
//    if (mySortIndex!=SortByEnum.HeatNumber && mySortIndex!=SortByEnum.RacerClass) {
//    	mySortIndex = SortByEnum.HeatNumber;
//    }
//
//   Comparator<GPHeat> comp = new Comparator<GPHeat>() {
//     public int compare(GPHeat o1, GPHeat o2) {
//       if (o1 == o2) return 0;
//       if (o1 == null) return -1;
//       if (o2 == null) return 1;
//       return o1.compareTo(o2, mySortIndex, true);
//     }
//     public boolean equals(Object obj) {
//       return false;
//     }
//   };
//   Arrays.sort(arr, comp);
// }
//
//  public void schedReport() {
//	String[] sort = {
//	  SortByEnum.HeatNumber.toString(),
//	  SortByEnum.RacerClass.toString(),
//	};
//    setSortMenu(sort, ReportTypeEnum.SCHEDULE, myScheduleSort1);
//
//    if (myGPManager == null) {
//      return;
//    }
//    java.util.List<GPHeat> heats = myGPManager.getHeatList();
//    if (heats == null) {
//      return;
//    }
//
//    GPHeat[] arr = heats.toArray(new GPHeat[0]);
//    schedSort(arr, myScheduleSort2);
//    schedSort(arr, myScheduleSort1);
//
//    String text = "";
//    text += "<font size=\"5\">\n";
//    text += "<table border=\"1\" cellpadding=\"1\" width=\"50%\">\n";
//    text += "<caption> Race Schedule </caption>\n";
//    text += "<th align=\"left\"> Number </th>";
//    text += "<th align=\"center\"> " + LANE_DISP_NAMES[0] + "</th>";
//    text += "<th align=\"center\"> " + LANE_DISP_NAMES[1] + "</th>";
//    text += "<th align=\"center\"> " + LANE_DISP_NAMES[2] + "</th>";
//    text += "<th align=\"center\"> " + LANE_DISP_NAMES[3] + "</th>";
//    text += "<th align=\"center\"> Club </th>";
//
//    for (int i=0; i<arr.length; i++) {
//      GPHeat heat = arr[i];
//      if (heat != null) {
//        text += heat.toHTMLString();
//      }
//    }
//
//    text += "</table>\n";
//    text += "</font>\n";
//
//    set(text);
//  }

//  public void racerReport() {
//	String[] sort = {
//	  SortByEnum.RacerNumber.toString(),
//	  SortByEnum.RacerName.toString(),
//	  SortByEnum.RacerClass.toString(),
//	  SortByEnum.RacerPresent.toString(),
//	};
//    setSortMenu(sort, ReportTypeEnum.RACER, myRacerSort1);
//
//    if (myGPManager == null) {
//      return;
//    }
//
//	Collection<GPRacer> racers = myGPManager.getCars(true);
//    if (racers == null) {
//      return;
//    }
//
//    GPRacer[] arr = racers.toArray(new GPRacer[0]);
//    racerSort(arr, myRacerSort2);
//    racerSort(arr, myRacerSort1);
//
//    String text = "";
//    text += "<font size=\"5\">\n";
//    text += "<table border=\"0\" cellpadding=\"0\" width=\"80%\">\n";
//    text += "<caption> Race Entries </caption>\n";
//    text += "<tr>";
//    text += "<th align=left> Number </th>";
//    text += "<th align=left> Name </th>";
//    text += "<th align=left> Club </th>";
//    text += "<th align=center> Present </th>";
//    text += "</tr>\n";
//
//    for (int j = 0; j < arr.length; j++) {
//      GPRacer r = arr[j];
//      if (r != null) {
//        text += r.toHTMLEntryString();
//      }
//    }
//    text += "</table>\n";
//    text += "</font>\n";
//
//    set(text);
//  }

//  public void resReport() {
//    String[] sort = {
//    	SortByEnum.RacerNumber.toString(),
//    	SortByEnum.RacerName.toString(),
//    	SortByEnum.RacerPlaceNumber.toString(),
//    	SortByEnum.RacerPlaceType.toString(),
//    	SortByEnum.RacerSpeedScore.toString(),
//    	SortByEnum.RacerAveTime.toString(),
//    	SortByEnum.RacerBestTime.toString(),
//    	SortByEnum.RacerClass.toString()
//    };
//    setSortMenu(sort, ReportTypeEnum.RESULTS, myResultsSort1);
//
//    if (myGPManager == null) {
//      return;
//    }
//
//    java.util.List<GPRacer> racers = myGPManager.getCars(true);
//    if (racers == null) {
//      return;
//    }
//    myGPManager.setAwardPlacing();
//
//    GPRacer[] arr = racers.toArray(new GPRacer[0]);
//    resultSort(arr, myResultsSort2);
//    resultSort(arr, myResultsSort1);
//
//    String text = "";
//    text += "<font size=\"5\">\n";
//    text += "<table border=\"1\" cellpadding=\"2\" width=\"80%\">\n";
//    text += "<caption> Results </caption>\n";
//    text += "<tr>";
//    text += "<th> Number </th>";
//    text += "<th> Name </th>";
//    text += "<th> Place </th>";
//    text += "<th> Score </th>";
//    text += "<th> Average </th>";
//    text += "<th> Best </th>";
//    text += "<th> Club </th>";
//    text += "</tr>\n";
//
//    for (int j = 0; j < arr.length; j++) {
//      GPRacer r = arr[j];
//      if ((r != null) && r.getIsPresent()) {
//        text += r.toHTMLResultsString();
//      }
//    }
//    text += "</table>\n";
//    text += "</font>\n";
//
//    set(text);
//  }
//
//  public void save() {
//    // TLM - Not Implemented
//  }

// public void print() {
//   PrinterJob printJob = PrinterJob.getPrinterJob();
//   printJob.setPrintable(this);
//
//   if (printJob.printDialog())
//     try {
//       printJob.print();
//     }
//     catch (PrinterException pe) {
//       System.out.println("Error printing: " + pe);
//     }
// }

// public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
//   if (pageIndex > 0) {
//    return(NO_SUCH_PAGE);
//  } else {
//    Graphics2D g2d = (Graphics2D)g;
//    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
//    // Turn off double buffering
//    RepaintManager currentManager = RepaintManager.currentManager(myEntryPanel);
//    currentManager.setDoubleBufferingEnabled(false);
//    myEntryPanel.paint(g2d);
//    // Turn double buffering back on
//    currentManager.setDoubleBufferingEnabled(true);
//    return(PAGE_EXISTS);
//  }
// }
}
