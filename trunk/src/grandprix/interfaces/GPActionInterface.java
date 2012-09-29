/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/*
 * Copyright (c) 2002-2012 by Ted Meyers
 */
package grandprix.interfaces;


/**
 * <p>Title: Grand Prix Timer</p>
 * <p>Description: </p>
 * @author T Meyers
 * @version 1.0
 */

public interface GPActionInterface extends RedoInterface {
  public void load(int i);
  public void prev();
  public void next();
  public void clearResults();
  public void resetTimer();
  public void sendTimer(String s);
  public void fake();
}