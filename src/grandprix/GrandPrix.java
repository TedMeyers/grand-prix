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
import grandprix.gui.GrandPrixFrame;

import javax.swing.UIManager;


public class GrandPrix {

  public boolean packFrame = false;

  // Construct the application
  public GrandPrix() {
    GrandPrixFrame frame = new GrandPrixFrame();

    //Pack frames that have useful preferred size info, e.g. from their layout
    //Validate frames that have preset sizes
    if (packFrame) frame.pack();
    else frame.validate();

    frame.setVisible(true);
  }


  // Main method
  static public void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
      System.out.println("ERROR: " + e);
      e.printStackTrace();
    }
    new GrandPrix();
  }

}
