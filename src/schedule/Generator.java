package schedule;

import defaultpackage.Hour;
import io.CSVWriter;
import io.JSONReader;
import io.MySQLReader;
import resourcesobjects.ClassObject;
import resourcesobjects.GroupObject;
import resourcesobjects.CourseObject;
import resourcesobjects.ProfessorObject;
import resourcesobjects.RoomObject;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generator is the most important class in project.
 * Gathers information which are important to generate te schedule
 * Calls algorithm to assign classes to the 3D dependency array
 * Decodes 3D dependency to the Schedule Array List
 * Exports Schedule outside the project.
 */
public class Generator {
  private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );

  private static final String[] DAYS = {
      "Poniedziałek",
      "Wtorek",
      "Środa",
      "Czwartek",
      "Piątek"
  };

  private static final Hour[] HOURS = {
      new Hour("8:00"),
      new Hour("9:45"),
      new Hour("11:30"),
      new Hour("13:15"),
      new Hour("15:00"),
      new Hour("16:45"),
      new Hour("18:30"),
  };

  private ArrayList<CourseObject> courses;
  private ArrayList<GroupObject> groups;
  private ArrayList<ProfessorObject> professors;
  private ArrayList<RoomObject> rooms;
  private ArrayList<ClassObject> classes;
  private ArrayList<ScheduleObject> scheduleArrayList;

  /**
   * ScheduleObjectObject (Array of class ID from Database)
   * scheduleArray[room no.][hour][day]
   */
  private int scheduleArray[][][];

  public Generator() {
    courses = new ArrayList<>();
    groups = new ArrayList<>();
    professors = new ArrayList<>();
    rooms = new ArrayList<>();
    classes = new ArrayList<>();

    // Read MySQL Database and write available resources
    readResources();

    // Read JSON files and write preferences to objects
    new JSONReader(groups, professors, rooms);

    //Number of rooms
    int n = rooms.size();

    // 7 - hours per day
    // 5 - days per week
    scheduleArray = new int[n][7][5];

    new Algorithm(classes, scheduleArray, n);

    generate();

    try {
      new CSVWriter(scheduleArrayList);
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "", e);
    }

    LOG.log(Level.INFO, "Algorithm finished without errors");
  }

  private void readResources() {
    MySQLReader mySQLParser = new MySQLReader();
    mySQLParser.readCourses(courses);
    mySQLParser.readGropus(groups);
    mySQLParser.readProfessors(professors);
    mySQLParser.readRooms(rooms);
    mySQLParser.readClasses(classes);

    for (ClassObject aClass : classes)
      aClass.resolveClassObject(courses, groups, professors);
    LOG.log(Level.INFO, "Finished reading resources");
  }

  /**
   * Generates scheduleArrayList
   * Resolves keys in scheduleArray, finds their objects and puts in
   * to ScheduleObjectObject Array List
   */
  private void generate() {
    int idClass, roomNumber, hourNumber, dayNumber;
    ListIterator<ClassObject> iterator;
    ClassObject classItem = null;
    scheduleArrayList = new ArrayList<>();

    for (int dayIndex = 0; dayIndex < 5; dayIndex++) {
      for (int hourIndex = 0; hourIndex < 7; hourIndex++) {
        for (int roomIndex = 0; roomIndex < rooms.size(); roomIndex++) {
          if (scheduleArray[roomIndex][hourIndex][dayIndex] == 0) {
            continue;
          }

          idClass = scheduleArray[roomIndex][hourIndex][dayIndex];
          roomNumber = roomIndex;
          hourNumber = hourIndex;
          dayNumber = dayIndex;

          String day = DAYS[dayNumber];
          Hour hour = HOURS[hourNumber];
          RoomObject room = rooms.get(roomNumber);

          iterator = classes.listIterator();

          while (iterator.hasNext()) {
            classItem = iterator.next();
            if (classItem.getId() == idClass) {
              break;
            }
          }

          try {
            scheduleArrayList.add( new ScheduleObject (classItem,
                room, hour, day));
            LOG.log(Level.INFO, "Added class to scheduleArrayList");
          } catch (Exception e) {
            LOG.log(Level.WARNING, "Couldn't add class to scheduleArrayList", e);
          }
        }
      }
    }
  }
}
