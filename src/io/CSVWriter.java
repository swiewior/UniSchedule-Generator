package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import schedule.ScheduleObject;

/**
 * Write generated schedule to CSV format requested by 4th group
 */
public class CSVWriter {
  private PrintWriter pw;
  private StringBuilder sb;
  private ArrayList<ScheduleObject> list;

  public CSVWriter(ArrayList<ScheduleObject> list) throws FileNotFoundException,
      NullPointerException {
    if (list == null) {
      throw new NullPointerException("list is empty");
    }

    this.list = list;
    pw = new PrintWriter(new File("plan.txt"));
    sb = new StringBuilder();

    // Header (optional)
    //sb.append("course,room,professor,day,start_hour,90,group_name");
    //sb.append(System.lineSeparator());

    buildCSV();
    writeToFile();
  }

  private void buildCSV(){
    for (ScheduleObject scheduleItem : list) {
      if (sb.length() != 0) {
        sb.append(",");
        sb.append(System.lineSeparator());
      }
      sb.append(scheduleItem.getClassObject().courseToString());
      sb.append(',');
      sb.append(scheduleItem.getRoom());
      sb.append(',');
      sb.append(scheduleItem.getClassObject().professorToString());
      sb.append(',');
      sb.append(scheduleItem.getDay());
      sb.append(',');
      sb.append(scheduleItem.getHour().getStart());
      sb.append(',');
      sb.append("90"); // duration
      sb.append(',');
      sb.append(scheduleItem.getClassObject().groupToString());
    }
  }

  private void writeToFile(){
    pw.write(sb.toString());
    pw.close();
  }
}
