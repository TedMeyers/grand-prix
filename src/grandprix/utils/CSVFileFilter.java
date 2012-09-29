/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/*
 * Copyright (c) 2002-2012 by Ted Meyers
 */
package grandprix.utils;

import grandprix.GPConstants;

import java.io.File;

/**
 * <p>Title: Grand Prix Timer</p>
 * <p>Description: </p>
 * @author T Meyers
 * @version 1.0
 */

public class CSVFileFilter extends javax.swing.filechooser.FileFilter implements GPConstants {
  public CSVFileFilter() {
  }
  public boolean accept(File pathname) {
    return ((pathname != null) && pathname.getName().endsWith(CSV_EXT) ||
            pathname.isDirectory());
  }

  public String getDescription() {
    return CSV_DESC;
  }
}
