/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/*
 * Copyright (c) 2002-2012 by Ted Meyers
 */
package grandprix.gui;

import grandprix.GPConstants;
import grandprix.GPManager;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * <p>Title: Grand Prix Frame</p>
 * <p>Description: This is the frame that holds the tab panels:
 * Race, Setup, Racers, Reports </p>
 * @author T Meyers
 * @version 1.0
 */
public class GrandPrixFrame extends JFrame implements GPConstants {
  private static final long serialVersionUID = 5017557557523509152L;
  
  private JPanel myContentPane = null;
  private JFileChooser myFileChooser = null;

  private Component myCurrentTabPanel = null;

  private JMenuBar jMenuBar1 = new JMenuBar();
  private JMenu jMenuFile = new JMenu();
  //private JMenuItem jMenuFileOpenRacers = new JMenuItem();
  //private JMenuItem jMenuFileOpenHeats = new JMenuItem();
  private JMenuItem jMenuFileExit = new JMenuItem();
  private JMenu jMenuHelp = new JMenu();
  private JMenuItem jMenuHelpAbout = new JMenuItem();

  private GrandPrixSetup myGrandPrixSetup = null;
  private GrandPrixRace myGrandPrixRace = null;
  private GrandPrixRacerPanel myGrandPrixRacerPanel = null;
  private GrandPrixTableReportPanel myReportPanel = null;
  private GPManager myRaceManager = null;

  // Construct the frame
  public GrandPrixFrame() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);

    myFileChooser = new JFileChooser(new File(""));
    myFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

    myRaceManager = new GPManager(this);

    myGrandPrixRace = new GrandPrixRace(FONT_SIZE, myRaceManager);
    myGrandPrixSetup = new GrandPrixSetup(myRaceManager);
    myGrandPrixRacerPanel = new GrandPrixRacerPanel(myRaceManager);
    myReportPanel = new GrandPrixTableReportPanel(myRaceManager);
    myRaceManager.setComm(myGrandPrixSetup);

    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Component initialization
  private void jbInit() throws Exception {
    myContentPane = (JPanel) this.getContentPane();

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab("Race", myGrandPrixRace);
    tabbedPane.addTab("Setup", myGrandPrixSetup);
    tabbedPane.addTab("Racers", myGrandPrixRacerPanel);
    tabbedPane.addTab("Reports", myReportPanel);

    myCurrentTabPanel = null;

    tabbedPane.addChangeListener( new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JTabbedPane tp = (JTabbedPane) e.getSource();
        if ((tp != null) && (tp.getSelectedComponent() == myReportPanel)) {
            myReportPanel.update();
        }
        if ((tp != null) && (myCurrentTabPanel == myGrandPrixRacerPanel)) {
          myGrandPrixRacerPanel.leavePanel();
          myReportPanel.update();
        }
        if (tp != null) {
          myCurrentTabPanel = tp.getSelectedComponent();
        }
      }
    });

    BorderLayout borderLayout1 = new BorderLayout();
    myContentPane.setLayout(borderLayout1);
    myContentPane.add(tabbedPane, BorderLayout.CENTER);

    this.setTitle("Welcome to the GrandPrix");
    this.setJMenuBar(jMenuBar1);
    this.setSize(new Dimension(800, 400));

    jMenuFile.setText("File");
    //jMenuFileOpenRacers.setText("Open Racers");
    //jMenuFileOpenRacers.addActionListener(new java.awt.event.ActionListener() {
    //  public void actionPerformed(ActionEvent e) {
    //    jMenuFileOpenRacers_actionPerformed(e);
    //  }
    //});
    //jMenuFile.add(jMenuFileOpenRacers);

    //jMenuFileOpenHeats.setText("Open Heats");
    //jMenuFileOpenHeats.addActionListener(new java.awt.event.ActionListener() {
    // public void actionPerformed(ActionEvent e) {
    //    jMenuFileOpenHeats_actionPerformed(e);
    // }
    //});
    //jMenuFile.add(jMenuFileOpenHeats);

    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileExit_actionPerformed(e);
      }
    });
    jMenuFile.add(jMenuFileExit);

    jMenuHelpAbout.setText("About");
    jMenuHelpAbout.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuHelpAbout_actionPerformed(e);
      }
    });
    jMenuHelp.add(jMenuHelpAbout);
    jMenuBar1.add(jMenuFile);
    jMenuBar1.add(jMenuHelp);
  }


  public GrandPrixRacerPanel getRacerPanel() {
    return myGrandPrixRacerPanel;
  }

  public GrandPrixSetup getSetup() {
    return myGrandPrixSetup;
  }

  public GrandPrixRace getRace() {
    return myGrandPrixRace;
  }

  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }

  public void dispose()
  {
    myRaceManager.exit();
    super.dispose();
    //System.exit(0);
  }

  //File | Exit action performed
  public void jMenuFileExit_actionPerformed(ActionEvent e) {
    dispose();
  }

  //File | Open Racers action performed
  public void jMenuFileOpenRacers_actionPerformed(ActionEvent e) {
    int returnVal = myFileChooser.showOpenDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = myFileChooser.getSelectedFile();
      myRaceManager.setRacers(file);
    }
  }

  //File | Open Heats action performed
  public void jMenuFileOpenHeats_actionPerformed(ActionEvent e) {
    int returnVal = myFileChooser.showOpenDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = myFileChooser.getSelectedFile();
      myRaceManager.setHeats(file);
      myRaceManager.applyHeats();
    }
  }

  //Help | About action performed
  public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
  }
}
