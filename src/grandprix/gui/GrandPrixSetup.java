/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/*
 * Copyright (c) 2002-2012 by Ted Meyers
 */
package grandprix.gui;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import grandprix.GPConstants;
import grandprix.GPManager;
import grandprix.enums.TimerTypeEnum;
import grandprix.interfaces.CommInterface;
import grandprix.utils.CSVFileFilter;
import grandprix.utils.SerialPortReader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;


/**
 * <p>Title: Grand Prix Timer</p>
 * <p>Description: </p>
 * @author T Meyers
 * @version 1.0
 */

public class GrandPrixSetup extends JPanel implements SerialPortEventListener, CommInterface, GPConstants {
  private static final long serialVersionUID = 2084151810137207417L;

  private String[] myLastResults = null;

  private JFileChooser myFileChooser = null;

  private SerialPortReader mySerial = null;
  private BufferedReader myBufreader = null;

  private int myNumLines = 0;
  private JTextArea myText = null;
  private JComboBox<String> myCombo = null;
  private JButton myConnect = null;
  
  private JButton mySendL = null;
  private JButton mySendS = null;
  private JButton mySendR = null;

  private JButton myOutFile = null;
  private JLabel myOutFileName = null;

  private JButton myRacerFile = null;
  private JLabel myRacerFileName = null;

  private JButton myHeatFile = null;
  private JLabel myHeatFileName = null;

  private JComboBox<String> myAutoAdvance = null;

  private JButton myLoad = null;
  private JButton myResults = null;
  private JButton myClearResults = null;
  private JButton myEraseResults = null;

  private JButton myReadProgress = null;
  private JButton myDebugOn = null;

  private GPManager myGPManager = null;

  public GrandPrixSetup(GPManager man) {
    super(new BorderLayout());
    
    myLastResults = new String[16];

    myFileChooser = new JFileChooser();
    myFileChooser.setFileFilter(new CSVFileFilter());

    myGPManager = man;

    myNumLines = 0;
    myText = new JTextArea();
    myCombo = new JComboBox<String>();
    myConnect = new JButton("Connect");
    
    mySendL = new JButton("L");
    mySendS = new JButton("S");
    mySendR = new JButton("R");

    myOutFile = new JButton("Set Output File...");
    myOutFileName = new JLabel("");

    myRacerFile = new JButton("Set Racer File...");
    myRacerFileName = new JLabel("");

    myHeatFile = new JButton("Set Heat File...");
    myHeatFileName = new JLabel("");

    myAutoAdvance = new JComboBox<String>();
    myAutoAdvance.addItem("Off");
    myAutoAdvance.addItem("On");

    myLoad = new JButton("Reload Results");
    myResults = new JButton("Dump Results");
    myClearResults = new JButton("Clear Results");
    myEraseResults = new JButton("Erase Results");
    myReadProgress = new JButton("Read Progress...");
    myDebugOn = new JButton("Debug");

    JPanel connectionPanel = new JPanel(new BorderLayout());

    String dname = myFileChooser.getCurrentDirectory().getAbsolutePath();
    dname += File.separator + "GrandPrix";
    File outFile = new File(dname + File.separator + "Results.csv");
    setFileName(outFile);
    File heatFile = new File(dname + File.separator + "Heats_all.csv");
    setHeatFileName(heatFile);

    Enumeration<CommPortIdentifier> en = SerialPortReader.getPortList();
    while (en.hasMoreElements()) {
      CommPortIdentifier id = en.nextElement();
      String s = id.getName();
      if (id.getPortType() == CommPortIdentifier.PORT_SERIAL) {
        myCombo.addItem(s);
      }
    }

    man.setAutoAdvance(false);

    myText.setEnabled(false);
    myText.setBorder(BorderFactory.createLineBorder(Color.black));
    connectionPanel.add(myText, BorderLayout.CENTER);

    JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	panel1.add(myCombo);
	panel1.add(myConnect);

    if (TIMER_TYPE == TimerTypeEnum.RaceMaster) {
    	panel1.add(mySendL);
    	panel1.add(mySendS);
    	panel1.add(mySendR);
    }
    if (TIMER_TYPE == TimerTypeEnum.FastTrack) {
    	panel1.add(createSendButton("RM", "RM"));	// Report Mode [#lanes in reverse mode, 
    												//              maskedLanes (x6), 
    												//				lanes reversed (=1), 
    												//				eliminator mode (=1)
    												//              new data results mode (=1)]   
    	panel1.add(createSendButton("N0", "N0"));	// Old results format
    	panel1.add(createSendButton("N1", "N1"));	// New results format
    	panel1.add(createSendButton("RL0", "RL0")); // Normal lane order
    	panel1.add(createSendButton("RL4", "RL4")); // Reverse 4 lanes
    	panel1.add(createSendButton("RF", "RF"));	// Return features enabled
    	panel1.add(createSendButton("RS", "RS"));	// Report serial number
    	panel1.add(createSendButton("RA", "RA"));	// Force results
    	
    	//panel1.add(createSendButton("LE", "LE"));	// Set eliminator mode
    	//panel1.add(createSendButton("RE", "RE"));	// Reset eliminator mode (normal mode)
    	// "MA" - "MF": Mask lanes
    	// "MG": Unmask (enable) all lanes
    	// "LF": Load features - do not use!
    	// "LR": Reset Laser Gate
    	// "LXA" - "LXO": Auto reset timing (C=6, O=25)
    	// "LXP": Auto reset disable
    	// "PCXX": Not used with K1 timer
    	
    }
	connectionPanel.add(panel1, BorderLayout.NORTH);

    JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel2.add(myOutFile);
    panel2.add(myOutFileName);

    JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel3.add(myRacerFile);
    panel3.add(myRacerFileName);

    JPanel panel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel4.add(myHeatFile);
    panel4.add(myHeatFileName);

    JPanel panel5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel5.add(new JLabel("Auto Advance"));
    panel5.add(myAutoAdvance);
    panel5.add(myLoad);
    panel5.add(myResults);
    panel5.add(myClearResults);
    panel5.add(myEraseResults);
    panel5.add(myReadProgress);
    panel5.add(myDebugOn);

    JPanel filePanel = new JPanel(new BorderLayout());
    filePanel.add(panel2, BorderLayout.NORTH);
    filePanel.add(panel3, BorderLayout.CENTER);
    filePanel.add(panel4, BorderLayout.SOUTH);

    JPanel buttonPanel = new JPanel(new BorderLayout());
    buttonPanel.add(filePanel, BorderLayout.NORTH);
    buttonPanel.add(panel5, BorderLayout.SOUTH);

    add(connectionPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.NORTH);

    myConnect.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        connect_actionPerformed(e);
      }
    });

    mySendL.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        send_actionPerformed(e, "L");
      }
    });

    mySendS.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        send_actionPerformed(e, "S");
      }
    });

    mySendR.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        send_actionPerformed(e, "R");
      }
    });

    myLoad.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        load_actionPerformed(e);
      }
    });

    myResults.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        result_actionPerformed(e);
      }
    });

    myClearResults.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        clearResults_actionPerformed(e);
      }
    });

    myEraseResults.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        eraseResults_actionPerformed(e);
      }
    });

    myReadProgress.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        readProgress_actionPerformed(e);
      }
    });

    myDebugOn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        debug_actionPerformed(e);
      }
    });

    myOutFile.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        outFile_actionPerformed(e);
      }
    });

    myRacerFile.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        racerFile_actionPerformed(e);
      }
    });

    myHeatFile.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        heatFile_actionPerformed(e);
      }
    });

    myAutoAdvance.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        autoAdvance_actionPerformed(e);
      }
    });

  }

  private JButton createSendButton(String name, final String sendString) {
  	JButton btn = new JButton(name);
	btn.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(ActionEvent e) {
			send_actionPerformed(e, sendString);
	    }
	});
	 return btn; 
  }
  
  public void send_actionPerformed(ActionEvent e, String s) {
    sendString(s);
  }

  public void connect_actionPerformed(ActionEvent e) {
    connect( (String) myCombo.getSelectedItem());
  }

  public void load_actionPerformed(ActionEvent e) {
    reload();
  }

  public void result_actionPerformed(ActionEvent e) {
    myGPManager.printAllResults();
  }

  public void eraseResults_actionPerformed(ActionEvent e) {
    myGPManager.getRace().showDialog("Results Erased -- Not yet implemented, sorry.");

    // TODO: Implement erase results...
/*
    String msg = "This will erase the results log file, continue?";
    String title = "Erase Results";
    int opt = JOptionPane.YES_NO_OPTION;
    int ans = JOptionPane.showConfirmDialog(null, msg, title, opt);

    if (ans == JOptionPane.YES_OPTION) {
      myGPManager.eraseResults();
      myGPManager.getRace().showDialog("Results Erased.");
    }
 */
  }

  public void clearResults_actionPerformed(ActionEvent e) {
    String msg = "This will reset all results, continue?";
    String title = "Clear Results";
    int opt = JOptionPane.YES_NO_OPTION;
    int ans = JOptionPane.showConfirmDialog(null, msg, title, opt);

    if (ans == JOptionPane.YES_OPTION) {
      myGPManager.clearResults();
      myGPManager.getRace().showDialog("Results Cleared.");
    }
  }

  public void readProgress_actionPerformed(ActionEvent e) {
    String msg = "This action will overwrite results, continue?";
    String msg2 = "Progress file should not be the same as the output file.\n" +
        "You should first select a new output file before continuing.  Continue anyway?";
    String title = "Load results from progress file.";

    if (confirm(msg, title)) {
      int returnVal = myFileChooser.showOpenDialog(this);

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = myFileChooser.getSelectedFile();
        String fName = file.getAbsolutePath();
        String outName = myOutFileName.getText();

        if (fName.equals(outName) && confirm(msg2, title)) {
          myGPManager.readProgressFile(file);
        } else if (!fName.equals(outName)) {
          myGPManager.readProgressFile(file);
        }
      }
    }
  }

  public void debug_actionPerformed(ActionEvent e) {
	GrandPrixRace.myTestModeEnabled = !GrandPrixRace.myTestModeEnabled;  // Toggle
    myGPManager.getRace().showDialog("Test Mode = " +  GrandPrixRace.myTestModeEnabled);
    //myGPManager.getRace().initialize();
    GrandPrixRace.myTestModeButton.setVisible(GrandPrixRace.myTestModeEnabled);
  }

  public void autoAdvance_actionPerformed(ActionEvent e) {
    String s = (String) myAutoAdvance.getSelectedItem();
    boolean state = ("on".equalsIgnoreCase(s));
    myGPManager.setAutoAdvance(state);
  }

  public void outFile_actionPerformed(ActionEvent e) {
    int returnVal = myFileChooser.showOpenDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      setFileName(myFileChooser.getSelectedFile());
    }
  }

  public void racerFile_actionPerformed(ActionEvent e) {
    int returnVal = myFileChooser.showOpenDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      setRacerFileName(myFileChooser.getSelectedFile());
      setFileName();
    }
  }

  public void heatFile_actionPerformed(ActionEvent e) {
    int returnVal = myFileChooser.showOpenDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      setHeatFileName(myFileChooser.getSelectedFile());
      setFileName();
    }
  }

  public boolean confirm(String msg, String title) {
    int opt = JOptionPane.YES_NO_OPTION;
    int ans = JOptionPane.showConfirmDialog(null, msg, title, opt);

    return (ans == JOptionPane.YES_OPTION);
  }

  public void setFileName() {
    File file = new File(myOutFileName.getText());
    myGPManager.setOutFile(file);
  }

  public void setFileName(File file) {
    if (file != null) {
      myOutFileName.setText(file.getAbsolutePath());
      myGPManager.setOutFile(file);
    }
  }

  public void setRacerFileNameBasic(File file) {
	  if ((file != null) && (myRacerFileName != null)) {
		  myRacerFileName.setText(file.getAbsolutePath());
	  }
  }
  
  public void setRacerFileName(File file) {
    if ((file != null) && (myGPManager != null)) {
      myRacerFileName.setText(file.getAbsolutePath());
      boolean status = myGPManager.setRacers(file);
      if (!status) {
        myRacerFileName.setText("");
        return;
      }
      if ("".equals(myHeatFileName.getText())) {
        File heat = myGPManager.getHeatFile(file);
        if (heat != null) setHeatFileName(heat);
      }
      myGPManager.applyHeats();
    }
  }

  public void setHeatFileName(File file) {
    if (file != null) {
      myHeatFileName.setText(file.getAbsolutePath());
      boolean status = myGPManager.setHeats(file);
      if (!status) myHeatFileName.setText("");
    }
  }

  public void reset() {
    myGPManager.printAllResults();
    myGPManager.initialize();
    myGPManager.clearResults();
    setFileName();
  }

  public void reload() {
    File oFile = new File(myOutFileName.getText());
    File rFile = new File(myRacerFileName.getText());
    File hFile = new File(myHeatFileName.getText());

    reset();
    if (oFile != null) myGPManager.setOutFile(oFile);
    if (rFile != null) myGPManager.setRacers(rFile);
    if (hFile != null) myGPManager.setHeats(hFile);
    if (hFile != null) myGPManager.applyHeats();
  }

  public void connect(String portName) {
    if (mySerial != null) {
      mySerial.getSerialPort().close();
    }
    try {
      mySerial = new SerialPortReader("GrandPrix", portName);
      mySerial.addEventListener(this);
      mySerial.getSerialPort().notifyOnDataAvailable(true);

      InputStream is = mySerial.getInputStream();
      InputStreamReader isr = new InputStreamReader(is);
      myBufreader = new BufferedReader(isr);
      System.out.println("Connected to " + portName);
      
      if (TIMER_TYPE == TimerTypeEnum.RaceMaster) {
    	  resetTimerRM();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  
  public void sendString(String s) {
    try {
    	System.out.println("Sending: [" + s + "]");
    	OutputStream os = mySerial.getSerialPort().getOutputStream();
		byte[] buf = s.getBytes();
		os.write(buf);
    } catch (Exception ex) {
    	ex.printStackTrace();
	}
  }

  /**
   */
  public void serialEvent(SerialPortEvent event) {
    switch (event.getEventType()) {
      case SerialPortEvent.BI:
      case SerialPortEvent.OE:
      case SerialPortEvent.FE:
      case SerialPortEvent.PE:
      case SerialPortEvent.CD:
      case SerialPortEvent.CTS:
      case SerialPortEvent.DSR:
      case SerialPortEvent.RI:
      case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
        break;

      case SerialPortEvent.DATA_AVAILABLE:
    	switch (TIMER_TYPE) {
    	  case FastTrack:
    		readResultsFT();
    		break;
    	  case RaceMaster:
    		readResultsRM();
    		break;
    	}
        break;
    }
  }
  
  private String RACE_MASTER_HEADER = ".*Race Master Race Result Report.*";
  
  /**
   * RaceMaster Timer
   * 
   *  "Race Master Race Result Report"
   *  "First   Place Single Lane Number:    2    Time in Seconds:    2.6074"
   *  "Second  Place Single Lane Number:    1    Time in Seconds:    2.7066"
   *  "Third   Place Single Lane Number:    4    Time in Seconds:    8.2706"
   *  "Fourth  Place Single Lane Number:    3    Time in Seconds:    9.3154"
   *  or
   *  "First   Place Multiple Lane Numbers: 3 4    Time in Seconds:    1.6189"
   */
  public synchronized void readResultsRM() {
    try {
      while (myBufreader.ready()) {
    	  // Hopefully, read the Header line
	      String line = "";
	      boolean done = false;
	      while (!done) {
	    	  String c = "" + (char)myBufreader.read();
	    	  if (c.matches("[a-zA-Z0-9 ]")) {
	    		  line += c;
	    		  done = line.matches(RACE_MASTER_HEADER);
	    	  }
	      }
    	  
    	  myText.setText("");
	      myText.append("\n");
	      myText.append(line);
	      System.out.println("Read: [" + line + "]");

	      if (myLastResults[0] != null) myGPManager.advance();
	      clearLastResults();
//	      myGPManager.setResults(TimerType.RaceMaster, myLastResults);
	      
	      boolean finished = false;
		  int laneCount = 0;
		  while (!finished) {
    		  line = "";
		      done = false;
		      while (!done && !finished) {
		    	  String c = "" + (char)myBufreader.read();
		    	  if (c.matches("[a-zA-Z0-9.: ]")) line += c;
	    		  done = line.matches(".*Place.*Number.*Second.*\\d\\.\\d\\d\\d\\d");
	    		  finished = line.matches(RACE_MASTER_HEADER);
		      }
		      if (!finished && (line.length() > 0)) {
			      myText.append("\n");
			      myText.append(line);
			      System.out.println("Read: [" + line + "]");
			      
			      String[] section = line.split(":");
			      if (section.length == 3) {
			    	  int place = 4;
			    	  String placeStr = section[0].trim();
			    	  if (placeStr.startsWith("First")) {
			    		  place = 1;
			    	  } else if (placeStr.startsWith("Second")) {
			    		  place = 2;
			    	  } else if (placeStr.startsWith("Third")) {
			    		  place = 3;
			    	  }
			    	  String[] lanes = section[1].split("\\s");
			    	  for (String lane : lanes) {
			    		  lane = lane.trim();
			    		  if (lane.matches("\\d")) {
						      int len = line.length();
						      String time = line.substring(len-8, len);
			    			  String result = place + ":" + lane + ":" + time;
			    			  myLastResults[laneCount++] = result;
			    			  finished = (laneCount >= GPConstants.NUM_LANES);
			    			  myGPManager.setResults(TimerTypeEnum.RaceMaster, myLastResults);

//						    	  System.out.println("Found lane: " + lane + 
//						    			  " for " + laneCount + " lanes. [" + result + "]");
			    		  }
			    	  }
			      }
		      } else {
	 	    	  resetTimerRM();  // Try again...
		    	  break;
		      }
		  }
//		  if (finished) {
//			  myGPManager.setResults(TimerType.RaceMaster, myLastResults);
//		  }     
      }
    } catch (IOException ioex) {
    	System.out.println("Breaking out: " + ioex);
    }
  }
  
  /**
   * FastTrack timer
   */
  public void readResultsFT() {
	clearLastResults();
	  
    try {
      String line = "";
      while (true) {
    	  if (myBufreader.ready()) {
    		  int c = myBufreader.read();
    		  if (c == '\n') {
    			  break;
    		  } else if (c == '\r') {
     			  break;
    		  } else if (c == '@') {
    			  myGPManager.advance();
    			  addLine(""+(char)c);
    		  } else {
    			  line += (char)c;
    		  }
    	  } else {
   			  sleep(50);
    	  }
      }
      
      line = line.trim();      
      if (line.length() > 0) {
    	addLine(line);
        if (line.length() > 36) {
        	char first = line.charAt(0);
        	if (first == 'A') {
        		myLastResults[0] = line;
        		myGPManager.setResults(TimerTypeEnum.FastTrack, myLastResults);
        	}
        }
      }
      if (myBufreader.ready()) { 
    	  readResultsFT();
      }
    } catch (IOException e) {
    	System.out.println("IOException reading: " + e);
    }
  }
  
  private void addLine(String line) {
      if (line.length() > 0) {
          System.out.println("READ: [" + line + "]");
      	if (myNumLines > 7) {
      		myNumLines = 0;
      		myText.setText("");
      	} else {
      		myText.append("\n");
      	}
          myText.append(line);
          myNumLines++;
      }	  
  }
  
  private void sleep(long millis) {
	  try {
		  Thread.sleep(millis);
	  } catch (InterruptedException iex) {
	  }
  }
  
  private void clearLastResults() {
	  for(int i=0; i<myLastResults.length; i++) {
		  myLastResults[i] = null;
	  }
  }
  
  public void resetTimerRM() throws IOException {
	  // Try again...
	  sleep(100);
	  clearInputStream();
	  sendString("L");
	  sleep(100);
  }
  
  public void clearInputStream() throws IOException {
	  while (myBufreader.ready()) {
		  char[] cbuf = new char[1024];
		  myBufreader.read(cbuf);
	  }
  }
}