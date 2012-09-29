/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/*
 * Copyright (c) 2002-2012 by Ted Meyers
 */
package grandprix.gui;

import grandprix.GPConstants;
import grandprix.GPRacer;
import grandprix.enums.ContestTypeEnum;
import grandprix.enums.SortByEnum;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * <p>Title: Grand Prix Entry</p>
 * <p>Description: </p>
 * @author T Meyers
 * @version 1.0
 */

public class GrandPrixEntryPanel extends JPanel implements GPConstants, ActionListener {
  private static final long serialVersionUID = -4170500777483956441L;
  
  public static final ContestTypeEnum[] CONTESTS = 
  	{ContestTypeEnum.NONE, ContestTypeEnum.NOVELTY, ContestTypeEnum.DESIGN};
  
  private JTextField myCar = null;
  private JTextField myName = null;
  private JTextField myFabricationScore = null;
  private JComboBox<Object> myClassName = null;
  private JCheckBox myEntered = null;
  private JComboBox<?> myContest = null;
  //private JCheckBox myIsNovelty = null;

  private GPRacer myRacer;


  public GrandPrixEntryPanel() {
    this(null);
  }

  public GrandPrixEntryPanel(Object[] classes) {
    this(new GPRacer(), classes);
  }

  public GrandPrixEntryPanel(GPRacer racer, Object[] classes) {
    super(new FlowLayout());
    initialize(classes, CONTESTS);
    if (racer != null) setRacer(racer);
  }

  private void initialize(Object[] classes, Object[] contests) {
    if (classes == null) classes = TEST_CLASSES;

    myCar = new JTextField("", 6);
    myName = new JTextField("", 30);
    myFabricationScore = new JTextField("", 8);
    myEntered = new JCheckBox();
    myClassName = new JComboBox<Object>(classes);
    myContest = new JComboBox<Object>(contests);
    //myIsNovelty = new JCheckBox();

    myEntered.addActionListener(this);
    myContest.addActionListener(this);
    myClassName.addActionListener(this);

    add(new JLabel("Present: "));
    add(myEntered);
    add(new JLabel("Contest: "));
    add(myContest);
    add(new JLabel(" Num: "));
    add(myCar);
    add(new JLabel(" Name: "));
    add(myName);
    add(new JLabel(" Score: "));
    add(myFabricationScore);
    add(new JLabel(" Class: "));
    add(myClassName);

    add(new JButton("Delete"));
  }

  public void actionPerformed(ActionEvent evt) {
  }

  public GPRacer getRacer() {
    return myRacer;
  }

  public void setRacer(GPRacer racer) {
	// TODO: why was this using a copy constructor???
    myRacer = racer; //new GPRacer(racer);
    updateGUI();
  }

  public void addClassName(Object item) {
    myClassName.addItem(item);
  }

  public JComponent getComponentAt(SortByEnum comp) {
    if (comp==SortByEnum.RacerNumber) {
      return myCar;
    } else if (comp==SortByEnum.RacerName) {
      return myName;
    } else if (comp==SortByEnum.RacerFabScore) {
      return myFabricationScore;
    } else if (comp==SortByEnum.RacerClass) {
      return myClassName;
    } else if (comp==SortByEnum.RacerPresent) {
      return myEntered;
    } else if (comp==SortByEnum.RacerContest) {
      return myContest;
    }
   return null;
  }

  public boolean updateRacer() {
	boolean isSame = true;
    if (myRacer != null) {
      String car = myCar.getText();
      String name = myName.getText();
      String cname = "" + myClassName.getSelectedItem();
      String fscore = myFabricationScore.getText();
      boolean isSel = myEntered.isSelected();
      ContestTypeEnum contest = (ContestTypeEnum) myContest.getSelectedItem();
      
      isSame = myRacer.update(car, name, cname, contest, fscore, isSel);
    }
    return isSame;
  }

  public void updateGUI() {
    if (myRacer != null) {
      myCar.setText("" + myRacer.getCar());
      myName.setText(myRacer.getName());
      double score = myRacer.getFabricationScore();
      if (score == 0.0) {
    	  myFabricationScore.setText("");
      } else {
    	  myFabricationScore.setText("" + score);
      }
      myEntered.setSelected(myRacer.getIsPresent());
      myContest.setSelectedItem(myRacer.getContestType());
      myClassName.setSelectedItem(myRacer.getClassName());
    }
    validate();
  }

  public int compareTo(Object o, SortByEnum sortby, boolean isForward) {
    int dir = (isForward)?1:-1;

    if (o.getClass() != getClass()) {
      return dir;
    }
    GrandPrixEntryPanel other = (GrandPrixEntryPanel) o;
    GPRacer gp1 = getRacer();
    GPRacer gp2 = other.getRacer();

    if (gp1 != null) {
      return gp1.compareTo(gp2, sortby, isForward);
    }
    return dir;
  }
//
//  public String toHTMLString() {
//    GPRacer racer = getRacer();
//    if (racer != null) {
//      return racer.toHTMLEntryString();
//    }
//    return "";
//  }
}