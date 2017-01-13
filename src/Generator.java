import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Generator {
	private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );
	public static final String[] DAYS = {
			"Poniedziałek",
			"Wtorek",
			"Środa",
			"Czwartek",
			"Piątek"
	};

	public static final Hour[] HOURS = {
			new Hour("8:00", "9:30"),
			new Hour("9:45", "11:15"),
			new Hour("11:30", "13:00"),
			new Hour("13:15", "14:45"),
			new Hour("15:00", "16:30"),
			new Hour("16:45", "18:15"),
			new Hour("18:30", "20:00"),
	};

	MySQLParser mySQLParser;
	CSVParser csvParser;
	ArrayList<CourseObject> courses;
	ArrayList<GroupObject> groups;
	ArrayList<ProfessorObject> professors;
	ArrayList<RoomObject> rooms;
	ArrayList<ClassObject> classes;
	ArrayList<Schedule> scheduleArrayList;

	/**
	 * Schedule (Array of class ID from Database)
	 * 1 - room number
	 * 2 - hour
	 * 3 - day
	 */
	int scheduleArray[][][];

	/**
	 * Number of rooms
	 */
	int n;

	public Generator() {
		courses = new ArrayList<>();
		groups = new ArrayList<>();
		professors = new ArrayList<>();
		rooms = new ArrayList<>();

		classes = new ArrayList<>();

		n = rooms.size();
		// 7 - hours per day
		// 5 - days per week
		scheduleArray = new int[n][7][5];

		readResources();
		readPreferences();
		algorithm();

		try {
			csvParser = new CSVParser(scheduleArrayList);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "", e);
		}

		LOG.log(Level.INFO, "Zakończono działanie algorytmu");
	}

	void readResources() {
		mySQLParser = new MySQLParser();
		mySQLParser.readCourses(courses);
		mySQLParser.readGropus(groups);
		mySQLParser.readProfessors(professors);
		mySQLParser.readRooms(rooms);
		mySQLParser.readClasses(classes);

		for (ClassObject aClass : classes)
			aClass.resolveClassObject(courses, groups, professors);
		LOG.log(Level.INFO, "Zakończono wczytywanie zasobów");
	}

	void readPreferences() {
		new JSONReader(groups, professors, rooms);

		ListIterator<ClassObject> iterator = classes.listIterator();
		ClassObject controlClass = null;
		int roomIndex;

		classLoop:
		while(iterator.hasNext()) {
			ClassObject classItem = iterator.next();

			int[][] professorPreferences = classItem.getProfessor().getPreferences();

			for(int j = 0; j < 5; j++)
				for (int i = 0; i < 7; i++)
					if ((roomIndex = professorPreferences[i][j]) != -1) {
						if (scheduleArray[roomIndex][i][j] != 0)
							continue;

						// RESOLVE CONFLICTS
						// for efery room at the same time:
						for (int it = 0; it < classes.size() &&
								(scheduleArray[it][i][j] != 0); it++) {
							// get control class id form schedule
							int controlClassId = scheduleArray[it][i][j];

							// find control class
							for (ClassObject classObject : classes)
								if(classObject.getId() == controlClassId) {
									controlClass = classObject;
									break;
								}

							// check if professor or group of control class is already assigned
							assert controlClass != null;
							if ((controlClass.getProfessorId() == classItem.getProfessorId())
									|| (controlClass.getGroupId() == classItem.getGroupId()))
								continue classLoop;
						}

						// assign class to scheduleArrayList and go to next class
						scheduleArray[roomIndex][i][j] = classItem.getId();
						continue classLoop;
					}
				}

		iterator = classes.listIterator();
		classLoop:
		while(iterator.hasNext()) {
			ClassObject classItem = iterator.next();

			int[][] groupPreferences = classItem.getGroup().getPreferences();

			for(int j = 0; j < 5; j++)
				for (int i = 0; i < 7; i++)
					if ((roomIndex = groupPreferences[i][j]) != -1) {
						if (scheduleArray[roomIndex][i][j] != 0)
							continue;

						// RESOLVE CONFLICTS
						// for efery room at the same time:
						for (int it = 0; it < classes.size() &&
								(scheduleArray[it][i][j] != 0); it++) {
							// get control class id form schedule
							int controlClassId = scheduleArray[it][i][j];

							// find control class
							for (ClassObject classObject : classes)
								if(classObject.getId() == controlClassId) {
									controlClass = classObject;
									break;
								}

							// check if professor or group of control class is already assigned
							assert controlClass != null;
							if ((controlClass.getProfessorId() == classItem.getProfessorId())
									|| (controlClass.getGroupId() == classItem.getGroupId()))
								continue classLoop;
						}

						// assign class to scheduleArrayList and go to next class
						scheduleArray[roomIndex][i][j] = classItem.getId();
						continue classLoop;
					}
		}
			}

	/**
	 * Assigns classes to the 3D resources relay (room, hour and day)
	 */
	void algorithm() {
		// i - room number
		// j - hour
		// k - day
		int n = rooms.size();	// number of rooms

		// Schedule
		// 7 - hours per day
		// 5 - days per week
		int scheduleArray[][][] = new int[n][7][5];

		ListIterator<ClassObject> iterator = classes.listIterator();
		ClassObject controlClass = null;

		classLoop:
		while(iterator.hasNext()) {
			ClassObject classItem = iterator.next();

			if (!iterator.hasPrevious())
				scheduleArray[0][0][0] = classItem.getId();

			dayLoop:
			for (int k = 0; k < 5; k++) {
				hourLoop:
				for (int j = 0; j < 7; j++) {
					roomLoop:
					for (int i = 0; i < n; i++) {

						if (scheduleArray[i][j][k] != 0)
							continue;

						// RESOLVE CONFLICTS
						// for efery room at the same time:
						for (int it = 0; it < classes.size() &&
								(scheduleArray[it][j][k] != 0); it++) {
							// get control class id form schedule
							int controlClassId = scheduleArray[it][j][k];

							// find control class
							for (ClassObject classObject : classes)
								if(classObject.getId() == controlClassId) {
									controlClass = classObject;
									break;
								}

								// check if professor or group of control class is already assigned
							assert controlClass != null;
							if ((controlClass.getProfessorId() == classItem.getProfessorId())
									|| (controlClass.getGroupId() == classItem.getGroupId()))
								continue roomLoop;
						}

						// assign class to scheduleArrayList and go to next class
						scheduleArray[i][j][k] = classItem.getId();
						continue classLoop;
					}
				}
			}
			//LOG.log(Level.WARNING, "Nie dodano zajęcia do planu", classItem);
		}
		LOG.log(Level.INFO, "Zakończono umieszczanie zajęć w planie");
		generate(scheduleArray);
	}

	/**
	 * Generates scheduleArrayList
	 * Resolves keys in scheduleArray, finds their objects and puts in
	 * to Schedule Array List
	 * @param scheduleArray is array of 3D dependency created in alghoritm.
	 */
	void generate(int scheduleArray[][][]) {
		int idClass, roomNumber, hourNumber, dayNumber;
		ListIterator<ClassObject> iterator;
		ClassObject classItem = null;
		scheduleArrayList = new ArrayList<>();

		dayLoop:
		for (int k = 0; k < 5; k++) {
			hourLoop:
			for (int j = 0; j < 7; j++) {
				roomLoop:
				for (int i = 0; (i < rooms.size()) && (scheduleArray[i][j][k] != 0); i++) {
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
						scheduleArrayList.add( new Schedule (classItem, room, hour, day));
						LOG.log(Level.INFO, "Added class to scheduleArrayList");
					} catch (Exception e) {
						LOG.log(Level.WARNING, "Couldn't add class to scheduleArrayList", e);
					}
				}
			}
		}
	}
}
