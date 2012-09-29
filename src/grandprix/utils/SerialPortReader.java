/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/*
 * Copyright (c) 2002-2012 by Ted Meyers
 */
package grandprix.utils;
/*
 * @(#)SerialPortReader 7/03
 *
 */

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;


/**
 * Class declaration
 */
public class SerialPortReader  {

  private InputStream myInputStream = null;
  private SerialPort mySerialPort = null;


  /**
   *
   * @return a input stream for serial port.
   */
  public InputStream getInputStream()
  {
    return myInputStream;
  }

  /**
   * @return The serialPort.
   */
  public SerialPort getSerialPort() {
    return mySerialPort;
  }


  /**
   * -------------------------------------------------------------
   */
  @SuppressWarnings("unchecked")
public static Enumeration<CommPortIdentifier> getPortList()
  {
    return (Enumeration<CommPortIdentifier>)
    	CommPortIdentifier.getPortIdentifiers();
  }


  /**
   * -------------------------------------------------------------
   */
  public static CommPortIdentifier findPortId(String portName) {
    CommPortIdentifier portId = null;
    Enumeration<CommPortIdentifier> portList = getPortList();

    while (portList.hasMoreElements() && (portId == null)) {
      CommPortIdentifier id = (CommPortIdentifier) portList.nextElement();
      if (id.getPortType() == CommPortIdentifier.PORT_SERIAL) {
        if (id.getName().equals(portName)) {
          portId = id;
          break;
        }
      }
    }
    if (portId == null) {
      System.out.println("port " + portName + " not found.");
    }
    return portId;
  }


  /**
   * Constructor declaration
   */
  public SerialPortReader(String name, String portName) throws Exception {
    int timeout = 2000;
    int baud   = 9600;
    int bits   = SerialPort.DATABITS_8;
    int stop   = SerialPort.STOPBITS_1;
    int parity = SerialPort.PARITY_NONE;

    try {
      CommPortIdentifier portId = findPortId(portName);

      mySerialPort = (SerialPort) portId.open(name, timeout);
      mySerialPort.setSerialPortParams(baud, bits, stop, parity);
      //mySerialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_OUT);
      mySerialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

      myInputStream = mySerialPort.getInputStream();

    } catch (UnsupportedCommOperationException e) {
    } catch (IOException e) {
    } catch (PortInUseException e) {}

    if (mySerialPort == null) {
      System.out.println("Unable to open serial port");
      throw new Exception();
    }
  }


/**
 * Add an event listener.
 */
  public void addEventListener(SerialPortEventListener listener)
  {
    try {
        mySerialPort.addEventListener(listener);
    }
    catch (TooManyListenersException e) {}
  }
}
