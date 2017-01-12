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

	public Generator() {
		courses = new ArrayList<>();
		groups = new ArrayList<>();
		professors = new ArrayList<>();
		rooms = new ArrayList<>();

		classes = new ArrayList<>();

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

	// TODO
	void readPreferences() {
		new JSONReader(groups, professors);

		ListIterator<ClassObject> iterator = classes.listIterator();

		while(iterator.hasNext()) {
			ProfessorObject professorItem = iterator.next();
			String preferences[][] = professorItem.getPreferences();

			// Days Loop
			for (int i = 0; i < 5; i++) {
				// Hour Loop
				for (int j = 0; j < 7; j++)
				if (preferences[i][j] != null)
					if (scheduleArray[preferences[i][j]][j][i])


				}
			}
		}
	}

	void algorithm() {
		//int i = 0;	// room number
		//int j = 0;	// hour
		//int k = 0;	// day
		int n = rooms.size();	// number of rooms

		// Schedule
		int scheduleArray[][][] = new int[n][7][5];
		/*for(int i = 0; i < n; i++)
			for(int j = 0; j < 7; j++)
				Arrays.fill(scheduleArray[i][j], -1);*/

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

						// for efery room
						for (int it = 0; it < classes.size() &&
								(scheduleArray[it][j][k] != 0); it++) {
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

						// assign class to scheduleArrayList and goto next class
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
	 * Resolves keys in scheduleArray, finds appropiate objects and
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
