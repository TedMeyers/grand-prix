/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/*
 * Copyright (c) 2002-2012 by Ted Meyers
 */
package grandprix.gui;

import grandprix.GPConstants;
import grandprix.GPHeat;
import grandprix.GPManager;
import grandprix.GPRacer;
import grandprix.utils.CSVFileFilter;
import grandprix.utils.TableSorter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.RepaintManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;


/**
 * <p>Title: Grand Prix Timer</p>
 * <p>Description: </p>
 * @author T Meyers
 * @version 1.0
 */

public class GrandPrixTableReportPanel extends JPanel implements GPConstants, Printable {
  private static final long serialVersionUID = -3518399286412896094L;
  
  private GPManager myGPManager;
  private JFileChooser myFileChooser;

  private JTable myRacerTable;
  private Object[][] myRacerValues;

  private Object[][] myScheduleValues;
  private JTable myScheduleTable;

  private Object[][] myResultsValues;
  private JTable myResultsTable;

  private JTable[] myTables;
  private JTabbedPane myTabPane;


  public GrandPrixTableReportPanel(GPManager man) {
    super(new BorderLayout());
    myGPManager = man;

    myFileChooser = new JFileChooser();
    myFileChooser.setFileFilter(new CSVFileFilter());

    myTables = new JTable[3];

    createRacerTable();
    createScheduleTable();
    createResultsTable();

    JScrollPane rsp = new JScrollPane(myRacerTable);
    JScrollPane ssp = new JScrollPane(myScheduleTable);
    JScrollPane tsp = new JScrollPane(myResultsTable);
    myTables[0] = myRacerTable;
    myTables[1] = myScheduleTable;
    myTables[2] = myResultsTable;

    myTabPane = new JTabbedPane();
    myTabPane.addTab("Racers", rsp);
    myTabPane.addTab("Schedule", ssp);
    myTabPane.addTab("Results", tsp);

    myTabPane.addChangeListener( new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        myTabPane.getSelectedIndex();
      }
    });

    add(myTabPane, BorderLayout.CENTER);

    JButton printButton = new JButton("Print...");
    printButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        print();
      }
    });

    JButton saveAsButton = new JButton("Save As...");
    saveAsButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveAs();
      }
    });

    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.add(printButton);
    buttonPanel.add(saveAsButton);
    buttonPanel.add(new JLabel("<ctrl>-click to multisort"));
    add(BorderLayout.SOUTH, buttonPanel);
  }

  private void createRacerTable(){
    myRacerValues = new Object[1][RR_COL_SIZE];
    TableModel tm = new AbstractTableModel() {
     static final long serialVersionUID = 1230051;
     public int getRowCount() {
        return myRacerValues.length;
      }
      public int getColumnCount() {
        return myRacerValues[0].length;
      }
      public Object getValueAt(int row, int col) {
        return myRacerValues[row][col];
      }
      public String getColumnName(int column) {
        return RACER_COL_NAMES[column];
      }
      public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
      }
    };
    TableSorter sort = new TableSorter(tm);
    myRacerTable = new JTable(sort);
    sort.setTableHeader(myRacerTable.getTableHeader());
  }

  private void createScheduleTable(){
    myScheduleValues = new Object[1][SR_COL_SIZE];
    TableModel tm = new AbstractTableModel() {
      static final long serialVersionUID = 1235002;
      public int getRowCount() {
        return myScheduleValues.length;
      }
      public int getColumnCount() {
        return myScheduleValues[0].length;
      }
      public Object getValueAt(int row, int col) {
        return myScheduleValues[row][col];
      }
      public String getColumnName(int column) {
        return SCHEDULE_COL_NAMES[column];
      }
      public Class<?> getColumnClass(int c) {
         return getValueAt(0, c).getClass();
      }
    };
    TableSorter sort = new TableSorter(tm);
    myScheduleTable = new JTable(sort);
    sort.setTableHeader(myScheduleTable.getTableHeader());
  }

  private void createResultsTable(){
    myResultsValues = new Object[1][TR_COL_SIZE];
    TableModel tm = new AbstractTableModel() {
      static final long serialVersionUID = 1230053;
      public int getRowCount() {
        return myResultsValues.length;
      }
      public int getColumnCount() {
        return myResultsValues[0].length;
      }
      public Object getValueAt(int row, int col) {
        return myResultsValues[row][col];
      }
      public String getColumnName(int column) {
        return RESULTS_COL_NAMES[column];
      }
      public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
      }
    };
    TableSorter sort = new TableSorter(tm);
    myResultsTable = new JTable(sort);
    sort.setTableHeader(myResultsTable.getTableHeader());
  }

  public void update() {
    updateRacerTable();
    updateScheduleTable();
    updateResultsTable();
  }

  public void updateRacerTable() {
    if (myGPManager == null) {
      return;
    }
    myGPManager.setAwardPlacing();

    Collection<GPRacer> racers = myGPManager.getCars(false);
    if (racers == null) {
      return;
    }

    myRacerValues = new Object[racers.size()][RR_COL_SIZE];

    int j = 0;
    for (GPRacer r : racers) {
      if (r != null) {
        myRacerValues[j][RR_NUMBER_IDX] = new Integer(r.getCar());
        myRacerValues[j][RR_NAME_IDX] = r.getName();
        myRacerValues[j][RR_FAB_SCORE_IDX] = new Double(r.getFabricationScore());
        myRacerValues[j][RR_CLUB_IDX] = r.getClassName();
        myRacerValues[j][RR_CONTEST_IDX] = r.getContestType();
        myRacerValues[j][RR_PRESENT_IDX] = new Boolean(r.getIsPresent());
        j++;
      }
    }
    myRacerTable.revalidate();
  }

  public void updateScheduleTable() {
    if (myGPManager == null) {
      return;
    }

    java.util.List<GPHeat> heats = myGPManager.getHeatList();
    if (heats == null) {
      return;
    }

    int size = heats.size();
    myScheduleValues = new Object[size][SR_COL_SIZE];
    
    int j = 0;
    for (GPHeat r : heats) {
      if (r != null) {
        myScheduleValues[j][SR_NUMBER_IDX] = new Integer(r.getNum()+1);
        myScheduleValues[j][SR_LANE_0_IDX] = getCarNum(r, 0);
        myScheduleValues[j][SR_LANE_1_IDX] = getCarNum(r, 1);
        myScheduleValues[j][SR_LANE_2_IDX] = getCarNum(r, 2);
        myScheduleValues[j][SR_LANE_3_IDX] = getCarNum(r, 3);
        myScheduleValues[j][SR_CLUB_IDX] = r.getClassName();
        j++;
      }
    }
    myScheduleTable.revalidate();
  }

  public void updateResultsTable() {
    if (myGPManager == null) {
      return;
    }
    Collection<GPRacer> racers = myGPManager.getCars(true);
    if (racers == null) {
      return;
    }

    ArrayList<GPRacer> list = new ArrayList<GPRacer>();
    for (GPRacer r : racers) {
      if ((r != null) && r.getIsPresent()) {
        list.add(r);
      }
    }

    myResultsValues = new Object[list.size()][TR_COL_SIZE];
    int j = 0;
    for (GPRacer r : list) {
      if (r != null) {
        int n = r.getAwardPlaceNumber();
        String placeNum = "~";
        String placeType = "~";
        if ((n > 0) && (n <= 4)) {
        	placeNum = ("" + n);
        	placeType = r.getAwardPlaceType();
        }

        myResultsValues[j][TR_NUMBER_IDX] = new Integer(r.getCar());
        myResultsValues[j][TR_NAME_IDX] = r.getName();
        myResultsValues[j][TR_PLACE_NUM_IDX] = placeNum;
        myResultsValues[j][TR_PLACE_TYPE_IDX] = placeType;
        myResultsValues[j][TR_SPEED_SCORE_IDX] = new Double(r.getSpeedScore());
        myResultsValues[j][TR_AVE_IDX] = new Double(r.getMeanTime());
        myResultsValues[j][TR_BEST_IDX] = new Double(r.getBestTime());
        myResultsValues[j][TR_CLUB_IDX] = r.getClassName();
        String s = String.format("%s:%06.2f", r.getContestType().toString().charAt(0), r.getFabricationScore());
        myResultsValues[j][TR_DS_IDX] = s;
        j++;
      }
    }
   myResultsTable.revalidate();
  }

  protected Object getCarNum(GPHeat heat, int lane) {
    GPRacer r = heat.getLane(lane);
    if (r != null) {
      return "" + (r.getCar());
    }
    return "";
  }

  public void saveAs() {
    int returnVal = myFileChooser.showOpenDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      //String fileName = myFileChooser.getSelectedFile().getAbsolutePath();
      myGPManager.getRace().showDialog("Not yet implemented, sorry.");
    }
  }

  public void print() {
    PrinterJob printJob = PrinterJob.getPrinterJob();
    printJob.setPrintable(this);

    if (printJob.printDialog())
      try {
        double scale = 1.0;
        int i = myTabPane.getSelectedIndex();
        String name = TABLE_REPORTS_TITLES[i];

        MessageFormat head = new MessageFormat(name);
        printJob.setPrintable(new TableReport(myTables[i],scale,head, null));
        printJob.print();
      }
      catch (PrinterException pe) {
        System.out.println("GPTableReportPanel - Error printing: " + pe);
      }
  }

  public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws
      PrinterException {
    if (pageIndex > 0) {
      return (NO_SUCH_PAGE);
    }
    else {
      Graphics2D g2d = (Graphics2D) g;
      g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      Font f = new Font("Monospaced",Font.PLAIN,8);
      g2d.setFont(f);
      int i = myTabPane.getSelectedIndex();

      // Turn off double buffering
      RepaintManager currentManager = RepaintManager.currentManager(myTables[i]);
      currentManager.setDoubleBufferingEnabled(false);
      myTables[i].paint(g2d);

      // Turn double buffering back on
      currentManager.setDoubleBufferingEnabled(true);
      return (PAGE_EXISTS);
    }
  }

  private class TableReport implements Printable {
    private JTable table;
    private JTableHeader header;
    private TableColumnModel colModel;
    private int totalColWidth;
    private MessageFormat headerFormat;
    private MessageFormat footerFormat;
    private int last = -1;
    private int row = 0;
    private int col = 0;
    private final Rectangle clip = new Rectangle(0, 0, 0, 0);
    private final Rectangle hclip = new Rectangle(0, 0, 0, 0);
    private final Rectangle tempRect = new Rectangle(0, 0, 0, 0);
    private static final int H_F_SPACE = 8;
    private static final float HEADER_FONT_SIZE = 18.0f;
    private static final float FOOTER_FONT_SIZE = 12.0f;
    private Font headerFont;
    private Font footerFont;
    private double scalingFactor = 1.0D;

    public TableReport(JTable table,
                       double scalingFactor,
                       MessageFormat headerFormat,
                       MessageFormat footerFormat) {
      this.table = table;
      header = table.getTableHeader();
      colModel = table.getColumnModel();
      totalColWidth = colModel.getTotalColumnWidth();
      if (header != null) {
        hclip.height = header.getHeight();
      }
      this.scalingFactor = scalingFactor;
      this.headerFormat = headerFormat;
      this.footerFormat = footerFormat;
      headerFont = table.getFont().deriveFont(Font.BOLD,
                                              HEADER_FONT_SIZE);
      footerFont = table.getFont().deriveFont(Font.PLAIN,
                                              FOOTER_FONT_SIZE);
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws
        PrinterException {
      final int imgWidth = (int) pageFormat.getImageableWidth();
      final int imgHeight = (int) pageFormat.getImageableHeight();
      if (imgWidth <= 0) {
        throw new PrinterException("Width of printable area is too small.");
      }
      Object[] pageNumber = new Object[] {
          new Integer(pageIndex + 1)};
      String headerText = null;
      if (headerFormat != null) {
        headerText = headerFormat.format(pageNumber);
      }
      String footerText = null;
      if (footerFormat != null) {
        footerText = footerFormat.format(pageNumber);
      }
      Rectangle2D hRect = null;
      Rectangle2D fRect = null;
      int headerTextSpace = 0;
      int footerTextSpace = 0;
      int availableSpace = imgHeight;
      if (headerText != null) {
        graphics.setFont(headerFont);
        hRect = graphics.getFontMetrics().getStringBounds(headerText,
            graphics);
        headerTextSpace = (int) Math.ceil(hRect.getHeight());
        availableSpace -= headerTextSpace + H_F_SPACE;
      }
      if (footerText != null) {
        graphics.setFont(footerFont);
        fRect = graphics.getFontMetrics().getStringBounds(footerText,
            graphics);
        footerTextSpace = (int) Math.ceil(fRect.getHeight());
        availableSpace -= footerTextSpace + H_F_SPACE;
      }
      if (availableSpace <= 0) {
        throw new PrinterException("Height of printable area is too small.");
      }
      double sf = scalingFactor;
      if (totalColWidth > imgWidth) {
        sf = (double) imgWidth / (double) totalColWidth;
      }
      while (last < pageIndex) {
        if (row >= table.getRowCount() && col == 0) {
          return NO_SUCH_PAGE;
        }
        int scaledWidth = (int) (imgWidth / sf);
        int scaledHeight = (int) ( (availableSpace - hclip.height) / sf);
        findNextClip(scaledWidth, scaledHeight);
        last++;
      }
      Graphics2D g2d = (Graphics2D) graphics;
      g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      AffineTransform oldTrans;
      if (footerText != null) {
        oldTrans = g2d.getTransform();
        g2d.translate(0, imgHeight - footerTextSpace);
        printText(g2d, footerText, fRect, footerFont, imgWidth);
        g2d.setTransform(oldTrans);
      }
      if (headerText != null) {
        printText(g2d, headerText, hRect, headerFont, imgWidth);
        g2d.translate(0, headerTextSpace + H_F_SPACE);
      }
      tempRect.x = 0;
      tempRect.y = 0;
      tempRect.width = imgWidth;
      tempRect.height = availableSpace;
      g2d.clip(tempRect);
      if (sf != 1.0D) {
        g2d.scale(sf, sf);
      }
      else {
        int diff = (imgWidth - clip.width) / 2;
        g2d.translate(diff, 0);
      }
      oldTrans = g2d.getTransform();
      Shape oldClip = g2d.getClip();
      if (header != null) {
        hclip.x = clip.x;
        hclip.width = clip.width;
        g2d.translate( -hclip.x, 0);
        g2d.clip(hclip);
        header.print(g2d);
        g2d.setTransform(oldTrans);
        g2d.setClip(oldClip);
        g2d.translate(0, hclip.height);
      }
      g2d.translate( -clip.x, -clip.y);
      g2d.clip(clip);
      table.print(g2d);
      g2d.setTransform(oldTrans);
      g2d.setClip(oldClip);
      g2d.setColor(Color.BLACK);
      g2d.drawRect(0, 0, clip.width, hclip.height + clip.height);
      return PAGE_EXISTS;
    }

    private void printText(Graphics2D g2d,
                           String text,
                           Rectangle2D rect,
                           Font font,
                           int imgWidth) {
      int tx;
      if (rect.getWidth() < imgWidth) {
        tx = (int) ( (imgWidth - rect.getWidth()) / 2);
      }
      else if (table.getComponentOrientation().isLeftToRight()) {
        tx = 0;
      }
      else {
        tx = - (int) (Math.ceil(rect.getWidth()) - imgWidth);
      }
      int ty = (int) Math.ceil(Math.abs(rect.getY()));
      g2d.setColor(Color.BLACK);
      g2d.setFont(font);
      g2d.drawString(text, tx, ty);
    }

    @SuppressWarnings("unused")
	private void findNextClip(int pw, int ph) {
      final boolean ltr = table.getComponentOrientation().isLeftToRight();
      if (col == 0) {
        if (ltr) {
          clip.x = 0;
        }
        else {
          clip.x = totalColWidth;
        }
        clip.y += clip.height;
        clip.width = 0;
        clip.height = 0;
        int rowCount = table.getRowCount();
        int rowHeight = table.getRowHeight(row);
        do {
          clip.height += rowHeight;
          if (++row >= rowCount) {
            break;
          }
          rowHeight = table.getRowHeight(row);
        }
        while (clip.height + rowHeight <= ph);
      }
      if (true) {
        clip.x = 0;
        clip.width = totalColWidth;
        return;
      }
      if (ltr) {
        clip.x += clip.width;
      }
      clip.width = 0;
      int colCount = table.getColumnCount();
      int colWidth = colModel.getColumn(col).getWidth();
      do {
        clip.width += colWidth;
        if (!ltr) {
          clip.x -= colWidth;
        }
        if (++col >= colCount) {
          col = 0;
          break;
        }
        colWidth = colModel.getColumn(col).getWidth();
      }
      while (clip.width + colWidth <= pw);
    }
  }
}
