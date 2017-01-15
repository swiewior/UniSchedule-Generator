package schedule;

import defaultPackage.Hour;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.*;
import resourcesObjects.*;

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
	 * 1 - room number
	 * 2 - hour
	 * 3 - day
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

		dayLoop:
		for (int k = 0; k < 5; k++) {
			hourLoop:
			for (int j = 0; j < 7; j++) {
				roomLoop:
				for (int i = 0; i < rooms.size(); i++) {
					if(scheduleArray[i][j][k] == 0)
						continue;

					idClass = scheduleArray[i][j][k];
					roomNumber = i;
					hourNumber = j;
					dayNumber = k;

					String day = DAYS[dayNumber];
					Hour hour = HOURS[hourNumber];
					RoomObject room = rooms.get(roomNumber);

					iterator = classes.listIterator();

					while (iterator.hasNext()) {
						classItem = iterator.next();
						if (classItem.getId() == idClass)
							break;
					}

					try {
						scheduleArrayList.add( new ScheduleObject (classItem, room, hour, day));
						LOG.log(Level.INFO, "Added class to scheduleArrayList");
					} catch (Exception e) {
						LOG.log(Level.WARNING, "Couldn't add class to scheduleArrayList", e);
					}
				}
			}
		}
	}
}
