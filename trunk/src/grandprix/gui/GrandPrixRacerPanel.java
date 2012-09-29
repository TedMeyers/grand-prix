/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/*
 * Copyright (c) 2002-2012 by Ted Meyers
 */
package grandprix.gui;

import grandprix.GPConstants;
import grandprix.GPManager;
import grandprix.GPRacer;
import grandprix.enums.SortByEnum;
import grandprix.utils.CSVFileFilter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


/**
 * <p>Title: Grand Prix Timer</p>
 * <p>Description: </p>
 * @author T Meyers
 * @version 1.0
 */

public class GrandPrixRacerPanel extends JPanel implements GPConstants {
  private static final long serialVersionUID = -6260771571507714582L;
  
  private JPanel myEntryPanel = null;
  private GridBagLayout myEntryLayout = null;
  private GridBagConstraints myConstraints = null;
  private JButton[] mySortButton;
  private boolean myChangesNeedSaving;

  private String[] myClasses = new String[0];    // Array of names (of entry class)

  private HashMap<String, GrandPrixEntryPanel> myRacers = 
	  new HashMap<String, GrandPrixEntryPanel>();
  private List<GrandPrixEntryPanel> mySortedRacers = new ArrayList<GrandPrixEntryPanel>();
  
  private GPManager myGPManager;

  public GrandPrixRacerPanel(GPManager man) {
    super(new BorderLayout());

    myGPManager = man;
    myEntryLayout = new GridBagLayout();

    myConstraints = new GridBagConstraints();
    myConstraints.fill = GridBagConstraints.BOTH;
    myConstraints.anchor = GridBagConstraints.NORTHWEST;
    myConstraints.weightx = 1.0;
    myConstraints.weighty = 1.0;
    myConstraints.gridwidth = 1;
    myConstraints.gridheight = 1;
    myConstraints.gridx = 0;
    myConstraints.gridy = GridBagConstraints.RELATIVE;
    myConstraints.ipadx = 0;
    myConstraints.ipady = 0;
    myConstraints.insets = new Insets(1, 1, 1, 1);

    myEntryPanel = new JPanel(myEntryLayout);
    JScrollPane scroll = new JScrollPane(myEntryPanel);


	mySortButton = new JButton[RACER_PANEL_SORTS.length];
    for (int i=0; i<RACER_PANEL_SORTS.length; i++) {
      mySortButton[i] = new JButton(RACER_PANEL_SORTS[i].toString());

      final SortByEnum sortby = RACER_PANEL_SORTS[i];
      mySortButton[i].addActionListener(new java.awt.event.ActionListener() {
        boolean myIsFwd = false;

        public void actionPerformed(ActionEvent e) {
          myIsFwd = !myIsFwd;
          sort(sortby, myIsFwd);
        }
      });
    }

    JButton addEntryButton = new JButton("Add Entry");
    addEntryButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addRacer(new GPRacer());
        layoutPanel();
      }
    });

    JButton addClassButton = new JButton("Add Club...");
    addClassButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addClass();
      }
    });

    JButton updateButton = new JButton("Load");
    updateButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
    	  loadFromFile();
      }
    });

    JButton saveButton = new JButton("Save");
    saveButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        save();
      }
    });

    JButton saveAsButton = new JButton("Save As...");
    saveAsButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveAs();
      }
    });

    JButton openButton = new JButton("Open...");
    openButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        open();
      }
    });

    JButton deleteButton = new JButton("Delete...");
    deleteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
    	deleteRacer();
    	layoutPanel();
      }
    });

    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.add(addEntryButton);
    buttonPanel.add(addClassButton);
    buttonPanel.add(updateButton);
    buttonPanel.add(saveButton);
    buttonPanel.add(saveAsButton);
    buttonPanel.add(openButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(new JLabel("save not required for modifying score"));

    add(BorderLayout.CENTER, scroll);
    add(BorderLayout.SOUTH, buttonPanel);
    clear();
    myChangesNeedSaving = false;
  }
  
 public void setChangesNeedSaving(boolean value) {
	myChangesNeedSaving = value;
 }
  
 public void clear() {
    myRacers.clear();
    mySortedRacers.clear();
    myEntryPanel.removeAll();
    myChangesNeedSaving = true;
 }

 public ArrayList<GPRacer> getCars() {
   ArrayList<GPRacer> list = new ArrayList<GPRacer>();
   for (GrandPrixEntryPanel cur : mySortedRacers) {
     GPRacer r = cur.getRacer();
     if (r != null) {
       list.add(r);
     }
   }
   return list;
 }

 /**
  * Update racers from GUI
  * @return true if at least one update was made
  */
 public void updateCars() {
   boolean isSame = true;
   for (GrandPrixEntryPanel cur : myRacers.values()) {
     isSame &= cur.updateRacer();
   }
   myChangesNeedSaving |= !isSame;
 }

 public List<GrandPrixEntryPanel> getRacers() {
   return mySortedRacers;
 }

 public void update() {
   ArrayList<GPRacer> cars = getCars();
   if ((myGPManager != null) && (cars != null) && (cars.size() > 0)) {
     myGPManager.setRacers(cars);
   }
 }

 public void leavePanel() {
   updateCars();
   if (myChangesNeedSaving) {
	  JOptionPane.showMessageDialog(this, "Remember to save changes!");
//	  System.out.println("GPEntryPanel - UPDATED RACERS!");
//	   ArrayList<GPRacer> cars = getCars();
//	   if ((myGPManager != null) && (cars != null) && (cars.size() > 0)) {
//		 // TODO:  Set racers here?
//	     //myGPManager.setRacers(cars);
//	   }
   }
   
// TODO:  Save changes menu here?
//   System.out.println("GPEntryPanel - SAVING CHANGES!");
//   String msg = "Save changes (this will reset all results)?";
//   String title = "Save Racers";
//   int opt = JOptionPane.YES_NO_OPTION;
//   int ans = JOptionPane.showConfirmDialog(null, msg, title, opt);
//
//   if (ans == JOptionPane.YES_OPTION) {
//     baseSaveAs();
//   }
 }

 public boolean confirmOpen() {
   String msg = "This will reset all results, continue?";
   String title = "Open Racers";
   int opt = JOptionPane.YES_NO_OPTION;
   int ans = JOptionPane.showConfirmDialog(null, msg, title, opt);

   return (ans == JOptionPane.YES_OPTION);
 }

 public boolean confirmSave() {
   String msg = "This will reset all results, continue?";
   String title = "Save Racers";
   int opt = JOptionPane.YES_NO_OPTION;
   int ans = JOptionPane.showConfirmDialog(null, msg, title, opt);

   return (ans == JOptionPane.YES_OPTION);
 }
 
 public void loadFromFile() {
	 String msg = "Load racers from file (will not reset results), continue?";
	 String title = "Load Racers";
	 int opt = JOptionPane.YES_NO_OPTION;
	 int ans = JOptionPane.showConfirmDialog(null, msg, title, opt);
	 boolean confirmed = (ans == JOptionPane.YES_OPTION);
	 
	 if ((myGPManager != null) && confirmed) {
		 myGPManager.updateRacersFromFile();
		 for (GrandPrixEntryPanel cur : myRacers.values()) {
			 cur.updateGUI();
		 }
	     myChangesNeedSaving = false;
	 }
 }

 public void save() {
   if (confirmSave()) {
     baseSave();
   }
 }

 public void baseSave() {
   if (myGPManager != null) {
     update();
     myGPManager.saveRacers();
     myChangesNeedSaving = false;
   }
 }

 public void saveAs() {
   if (!confirmSave()) {
     return;
   }
   baseSaveAs();
 }

 public void baseSaveAs() {
   JFileChooser fileChooser = new JFileChooser();
   fileChooser.setFileFilter(new CSVFileFilter());
   int returnVal = fileChooser.showSaveDialog(this);

   if (returnVal == JFileChooser.APPROVE_OPTION) {
     File file = fileChooser.getSelectedFile();
     update();
     myGPManager.saveRacers(file);
     myChangesNeedSaving = false;
   }
 }

 public void open() {
   if (!confirmOpen()) {
     return;
   }
   baseOpen();
 }

  public void baseOpen() {
   JFileChooser fileChooser = new JFileChooser();
   fileChooser.setFileFilter(new CSVFileFilter());
   int returnVal = fileChooser.showOpenDialog(this);

   if (returnVal == JFileChooser.APPROVE_OPTION) {
     File file = fileChooser.getSelectedFile();
     myGPManager.openRacers(file);
     update();
     myChangesNeedSaving = false;
   }
 }

  public void addClass() {
   String str = JOptionPane.showInputDialog("Please input a club name");
   if ((str == null) || str.equals("")) {
       return;
   }
   ArrayList<String> list = new ArrayList<String>(Arrays.asList(myClasses));
   list.add(str);
   myClasses = list.toArray(new String[0]);
   for (GrandPrixEntryPanel entry : myRacers.values()) {
	   entry.addClassName(str);
   }
   myChangesNeedSaving = true;
 }

 /**
  * Must Call layout() after this.
  * @param racer
  */
  public void addRacer(GPRacer racer) {
	GrandPrixEntryPanel panel = new GrandPrixEntryPanel(racer, myClasses);
    myRacers.put(racer.getID(), panel);
    mySortedRacers.add(panel);
    myChangesNeedSaving = true;
  }

  public void deleteRacer() {
   String str = JOptionPane.showInputDialog("Enter car ID to delete");
   if ((str == null) || str.equals("")) {
     return;
   }
   GrandPrixEntryPanel entry = myRacers.remove(str);
   if (entry != null) mySortedRacers.remove(entry);
   myChangesNeedSaving = true;
  }

  public void layoutPanel() {
    myEntryPanel.removeAll();

    myConstraints.weighty = 0.0;
    myConstraints.gridwidth = 1;
    myConstraints.gridheight = 1;
    myConstraints.fill = GridBagConstraints.HORIZONTAL;

    myConstraints.gridx = 0;
    myConstraints.weightx = 0.1;
    myEntryPanel.add(new JLabel(""), myConstraints);
    myConstraints.weightx = 1.0;
    for (int i=0; i<RACER_PANEL_SORTS.length; i++) {
      myConstraints.gridx = i+1;
      myEntryPanel.add(mySortButton[i], myConstraints);
    }

    int j = 0;
    for (GrandPrixEntryPanel ep : mySortedRacers) {
      myConstraints.gridx = 0;
      myConstraints.weightx = 0.1;
      myEntryPanel.add(new JLabel(""+(++j)+"."), myConstraints);
      myConstraints.weightx = 1.0;
      for (int i=0; i<RACER_PANEL_SORTS.length; i++) {
        myConstraints.gridx = i+1;
        myEntryPanel.add(ep.getComponentAt(RACER_PANEL_SORTS[i]), myConstraints);
      }
    }

    myConstraints.gridx = 0;
    myConstraints.weighty = 1.0;
    myConstraints.gridheight = GridBagConstraints.REMAINDER;
    myConstraints.gridwidth = RACER_PANEL_SORTS.length+1;
    myConstraints.fill = GridBagConstraints.BOTH;

    myEntryPanel.add(new JPanel(), myConstraints);

    myEntryPanel.validate();
    validate();
  }

  public void sort(SortByEnum sortby, boolean isForward) {
	  GrandPrixEntryPanel[] arr = myRacers.values().toArray(new GrandPrixEntryPanel[0]);

    final SortByEnum sort = sortby;
    final boolean isFwd = isForward;
    Comparator<GrandPrixEntryPanel> comp = new Comparator<GrandPrixEntryPanel>() {
      public int compare(GrandPrixEntryPanel o1, GrandPrixEntryPanel o2){
        if (o1 == o2) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        return ((GrandPrixEntryPanel) o1).compareTo(o2,sort,isFwd);
      }
      public boolean equals(Object obj) {
        return false;
      }
    };

    Arrays.sort(arr, comp);
    mySortedRacers.clear();
    mySortedRacers.addAll(Arrays.asList(arr));
    layoutPanel();
  }

  public void setClasses(String[] classes) {
    myClasses = classes;
  }
}