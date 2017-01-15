package schedule;

import defaultpackage.Hour;
import resourcesobjects.ClassObject;
import resourcesobjects.RoomObject;


/**
 * Object that contains information about single class assigned to room, hour and day.
 */
public class ScheduleObject {
  private ClassObject classObject;
  private String room;
  private Hour hour;
  private String day;

  ScheduleObject(ClassObject classObject, RoomObject room, Hour hour,
                 String day) {
    this.classObject = classObject;
    this.room = room.getName();
    this.hour = hour;
    this.day = day;
  }

  public ClassObject getClassObject() {
    return classObject;
  }

  public String getRoom() {
    return room;
  }

  public Hour getHour() {
    return hour;
  }

  public String getDay() {
    return day;
  }
}
