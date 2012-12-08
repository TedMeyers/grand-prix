/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/*
 * Copyright (c) 2002-2012 by Ted Meyers
 */
package grandprix.gui;

import grandprix.GPConstants;
import grandprix.GPHeat;
import grandprix.enums.TimerTypeEnum;
import grandprix.interfaces.GPActionInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * <p>Title: Grand Prix</p>
 * <p>Description: The is the main race panel </p>
 * @author T Meyers
 * @version 1.0
 */

public class GrandPrixRace extends JPanel implements GPConstants {
  private static final long serialVersionUID = -8565328190970245389L;
  
  private GridBagLayout myGridBag = null;
  private GridBagConstraints myConstr = null;
  private Font myFont = null;
  private Color myForegroundColor = Color.black;
  private boolean myIsBorder = true;
  private int myJustify = SwingConstants.LEFT;

  public static JButton myTestModeButton = null;
  
  private JLabel[] myCars = new JLabel[NUM_LANES];
  private JLabel[] myNames = new JLabel[NUM_LANES];
  private JLabel[] myPlaces = new JLabel[NUM_LANES];
  private JLabel[] myTimes = new JLabel[NUM_LANES];
  private JLabel[] myNextCars = new JLabel[NUM_LANES];

  private JLabel myClassName = new JLabel();
  private JLabel myNextClassName = new JLabel();
  private JComboBox<String> myHeat = new JComboBox<String>();

  private GPActionInterface myActions = null;

  private int myFontSize = FONT_SIZE;
  public static boolean myTestModeEnabled = TEST_MODE_ENABLED;

  public GrandPrixRace() {
    this(null);
  }

  public GrandPrixRace(GPActionInterface redo) {
    this(24, redo);
  }

  public GrandPrixRace(int fontSize, GPActionInterface redo) {
    super();

    initialize(fontSize);
    setRedo(redo);
  }

  public void setRedo(GPActionInterface redo) {
    myActions = redo;
  }

  public void setClass(String name) {
    String str = " " + name;
    myClassName.setText(str);
  }

  public void setNextClass(String name) {
    String str = " " + name;
    myNextClassName.setText(str);
  }

  public void setNextCar(int lane, int car) {
    String str = "" + car;
    if (car<0) str = "-";
    myNextCars[lane].setText(str);
  }

  public void setTime(int lane, double time) {
    String str = " " + time;
    myTimes[lane].setText(str);
  }


  public void setPlace(int lane, int place) {
    String str = "4th";
    if (place <= 1) str = "1st";
    if (place == 2) str = "2nd";
    if (place == 3) str = "3rd";
    myPlaces[lane].setText(str);
    // System.out.println("Set place: " + lane + " = " + place);
 }

  public void setCar(int lane, int num) {
    String str = " " + num;
    if (num < 0) str = " ";
    myCars[lane].setText(str);
  }

  public void setName(int lane, String name, boolean isLast) {
    String str = " " + name;
    myNames[lane].setText(str);
    if (isLast) {
    	myNames[lane].setForeground(Color.RED);
    } else {
    	myNames[lane].setForeground(Color.BLACK);
    }
  }

  public void setHeat(int heat) {
    if ((heat >= 0) && (heat < myHeat.getItemCount())) {
        GPActionInterface tmp = myActions;
        myActions = null;
        myHeat.setSelectedIndex(heat);
        myActions = tmp;
    }
  }

  public void showDialog(String msg) {
    JOptionPane.showMessageDialog(this, msg);
  }

  private JLabel addComp(JPanel panel, JLabel comp, int x, int y) {
    comp.setHorizontalAlignment(myJustify);
    addComp(panel, (JComponent) comp, x, y);

    return comp;
  }

  private JComponent addComp(JPanel panel, JComponent comp, int x, int y) {
    myConstr.gridx = x;
    myConstr.gridy = y;

    comp.setForeground(myForegroundColor);
    comp.setFont(myFont);

    myGridBag.setConstraints(comp, myConstr);
    // if (myIsBorder) comp.setBorder(BorderFactory.createEtchedBorder());
    if (myIsBorder) comp.setBorder(BorderFactory.createLineBorder(Color.black));

    panel.add(comp);

    return comp;
  }

  public void clear() {
    for (int i=0;i<NUM_LANES; i++) {
      myCars[i].setText("");
      myNames[i].setText("");
      myPlaces[i].setText("");
      myTimes[i].setText("");
      myNextCars[i].setText("");
    }

    setClass("");
    setNextClass("");
  }

  public void clearResult(int lane) {
    myPlaces[lane].setText("");
    myTimes[lane].setText("");
  }

  public void clearResults() {
    for (int i=0;i<NUM_LANES; i++) {
      myPlaces[i].setText("");
      myTimes[i].setText("");
    }
  }

  public void setHeatList(java.util.List<GPHeat> heats) {
    myHeat.removeAllItems();
    for (GPHeat heat : heats) {
      myHeat.addItem(" " + (heat.getNum()+1) + " ");
    }
  }

  public void initialize() {
    initialize(myFontSize);
  }

  public void initialize(int fontSize) {
    this.removeAll();
    myFontSize = fontSize;

    for (int i=0; i<NUM_LANES; i++) {
      myCars[i] = new JLabel();
      myNames[i] = new JLabel();
      myPlaces[i] = new JLabel();
      myTimes[i] = new JLabel();
      myNextCars[i] = new JLabel();

      Dimension d = myTimes[i].getSize();
      d.width = 140;
      myTimes[i].setMinimumSize(d);
   }

    myFont = getFont().deriveFont(Font.BOLD, myFontSize);

    JPanel mainPanel = new JPanel();
    myGridBag = new GridBagLayout();
    myConstr = new GridBagConstraints();
    mainPanel.setLayout(myGridBag);

    int rel = GridBagConstraints.RELATIVE;
    myConstr.fill = GridBagConstraints.BOTH;
    myConstr.weightx = 1.0;
    myConstr.weighty = 1.0;
    myConstr.gridwidth = 1;
    myConstr.gridheight = 1;
    myConstr.gridx = 0;
    myConstr.gridy = 0;
    myConstr.ipadx = 0;
    myConstr.ipady = 0;
    myConstr.insets = new Insets(1,1,0,1);

    myConstr.weightx = 1.0;
    myConstr.gridwidth = 2;
    myForegroundColor = Color.red;
    myIsBorder = false;
    //addComp(mainPanel, new JLabel("Class: "), 0, 0);
    myConstr.gridwidth = 3;
    addComp(mainPanel, myClassName, rel, 0);

    myConstr.gridwidth = 1;
    myConstr.weightx = 1.0;
    myJustify = SwingConstants.CENTER;
    myForegroundColor = Color.blue;
    myIsBorder = false;
    addComp(mainPanel, new JLabel ("Lane"), rel, 1);
    myIsBorder = true;
    myForegroundColor = Color.black;
    addComp(mainPanel, new JLabel (LANE_DISP_NAMES[0]), rel, 2);
    addComp(mainPanel, new JLabel (LANE_DISP_NAMES[1]), rel, 3);
    addComp(mainPanel, new JLabel (LANE_DISP_NAMES[2]), rel, 4);
    addComp(mainPanel, new JLabel (LANE_DISP_NAMES[3]), rel, 5);

    myConstr.weightx = 1.0;
    myJustify = SwingConstants.LEFT;
    myForegroundColor = Color.blue;
    myIsBorder = false;
    addComp(mainPanel, new JLabel (" Car"), rel, 1);
    myIsBorder = true;
    myForegroundColor = Color.black;
    addComp(mainPanel, myCars[0], rel, 2);
    addComp(mainPanel, myCars[1], rel, 3);
    addComp(mainPanel, myCars[2], rel, 4);
    addComp(mainPanel, myCars[3], rel, 5);

    myConstr.weightx = 100.0;
    myJustify = SwingConstants.LEFT;
    myForegroundColor = Color.blue;
    myIsBorder = false;
    addComp(mainPanel, new JLabel (" Name "), rel, 1);
    myIsBorder = true;
    myForegroundColor = Color.black;
    addComp(mainPanel, myNames[0], rel, 2);
    addComp(mainPanel, myNames[1], rel, 3);
    addComp(mainPanel, myNames[2], rel, 4);
    addComp(mainPanel, myNames[3], rel, 5);

    myConstr.weightx = 1.0;
    myForegroundColor = Color.blue;
    myJustify = SwingConstants.CENTER;
    myIsBorder = false;
    addComp(mainPanel, new JLabel ("Place"), rel, 1);
    myIsBorder = true;
    myForegroundColor = Color.black;
    addComp(mainPanel, myPlaces[0], rel, 2);
    addComp(mainPanel, myPlaces[1], rel, 3);
    addComp(mainPanel, myPlaces[2], rel, 4);
    addComp(mainPanel, myPlaces[3], rel, 5);

    myConstr.weightx = 10.0;
    myJustify = SwingConstants.LEFT;
    myForegroundColor = Color.blue;
    myIsBorder = false;
    addComp(mainPanel, new JLabel (" Time "), rel, 1);
    myIsBorder = true;
    myForegroundColor = Color.black;
    addComp(mainPanel, myTimes[0], rel, 2);
    addComp(mainPanel, myTimes[1], rel, 3);
    addComp(mainPanel, myTimes[2], rel, 4);
    addComp(mainPanel, myTimes[3], rel, 5);

    myIsBorder = false;
    myConstr.gridwidth = 7;
    myForegroundColor = Color.gray;
    myJustify = SwingConstants.CENTER;
    addComp(mainPanel, new JLabel(" ~ ~ ~ "), rel, 6);

    myIsBorder = false;
    myConstr.weightx = 1.0;
    myConstr.gridwidth = 2;
    myForegroundColor = Color.blue;
    myJustify = SwingConstants.RIGHT;
    addComp(mainPanel, new JLabel ("Next Race:"), rel, 7);

    myIsBorder = false;
    myConstr.gridwidth = 1;
    JPanel panel = new JPanel(myGridBag);
    addComp(mainPanel, panel, rel, 7);

    myIsBorder = false;
    myConstr.gridwidth = 2;
    myJustify = SwingConstants.CENTER;
    addComp(mainPanel, myNextClassName, rel, 7);

    myIsBorder = false;
    myConstr.weightx = 1.0;
    myConstr.gridwidth = 1;
    myJustify = SwingConstants.CENTER;
    addComp(panel, myNextCars[0], rel, 0);
    addComp(panel, myNextCars[1], rel, 0);
    addComp(panel, myNextCars[2], rel, 0);
    addComp(panel, myNextCars[3], rel, 0);


    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    myForegroundColor = Color.black;
    addComp(buttonPanel, new JLabel("Race #: "), 0, 0);

    setHeatList(new ArrayList<GPHeat>());
    myHeat.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (myActions != null) {
          int num = myHeat.getSelectedIndex();
          myActions.load((num >=0)?num:0);
        }
      }
    });
    myHeat.setFont(myFont);
    buttonPanel.add(myHeat);

    JButton btn = new JButton("<");
    btn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (myActions != null)
          myActions.prev();
      }
    });
    btn.setFont(myFont);
    buttonPanel.add(btn);

    btn = new JButton(">");
    btn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (myActions != null)
          myActions.next();
      }
    });
    btn.setFont(myFont);
    buttonPanel.add(btn);

    btn = new JButton("Clear");
    btn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (myActions != null)
          myActions.clearResults();
      }
    });
    btn.setFont(myFont);
    buttonPanel.add(btn);
    
    if (TIMER_TYPE == TimerTypeEnum.RaceMaster) {
	    btn = new JButton("*");
	    btn.addActionListener(new java.awt.event.ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        if (myActions != null)
	          myActions.resetTimer();
	      }
	    });
	    btn.setFont(myFont);
	    buttonPanel.add(btn);
	    
	    btn = new JButton("R");
	    btn.addActionListener(new java.awt.event.ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        if (myActions != null)
	          myActions.sendTimer("R");
	      }
	    });
	    btn.setFont(myFont);
	    buttonPanel.add(btn);

	    btn = new JButton("S");
	    btn.addActionListener(new java.awt.event.ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        if (myActions != null)
	            myActions.sendTimer("S");
	      }
	    });
	    btn.setFont(myFont);
	    buttonPanel.add(btn);
    } else if (TIMER_TYPE == TimerTypeEnum.FastTrack) {
	    btn = new JButton("Force");
	    btn.addActionListener(new java.awt.event.ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        if (myActions != null)
	            myActions.sendTimer("RA");  // Force results
	      }
	    });
	    btn.setFont(myFont);
	    buttonPanel.add(btn);    	
    }

    btn = new JButton("Test");
    btn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (myActions != null)
          myActions.fake();
      }
    });
    btn.setFont(myFont);
    buttonPanel.add(btn);
   	btn.setVisible(myTestModeEnabled);
   	myTestModeButton = btn;

    buttonPanel.setBorder(BorderFactory.createEtchedBorder()); //(Color.black));

    setLayout(new BorderLayout());
    add(mainPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
  }
}