import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
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
	ArrayList<Schedule> schedule;

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
			csvParser = new CSVParser(schedule);
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
		// TODO
	}

	void algorithm() {
		//int i = 0;	// room number
		//int j = 0;	// hour
		//int k = 0;	// day
		int n = rooms.size();	// number of rooms

		// Schedule
		int plan_sal[][][] = new int[n][7][5];
		for(int i = 0; i < n; i++)
			for(int j = 0; j < 7; j++)
				Arrays.fill(plan_sal[i][j], -1);

		ListIterator<ClassObject> iterator = classes.listIterator();
		ClassObject controlClass = null;

		classLoop:
		while(iterator.hasNext()) {
			ClassObject classItem = iterator.next();

			if (!iterator.hasPrevious())
				plan_sal[0][0][0] = classItem.getId();

			dayLoop:
			for (int k = 0; k < 5; k++) {
				hourLoop:
				for (int j = 0; j < 7; j++) {
					roomLoop:
					for (int i = 0; i < n; i++) {

						if (plan_sal[i][j][k] != -1)
							continue;

						for (int it = 0; it < classes.size() &&
								(plan_sal[it][j][k] != -1); it++) {
							int controlClassId = plan_sal[it][j][k];

							for (ClassObject co : classes)
								if(co.getId() == controlClassId) {
									controlClass = co;
									break;
								}

							if ((controlClass.getProfessorId() == classItem.getProfessorId())
									|| (controlClass.getGroupId() == classItem.getGroupId()))
								continue roomLoop;
						}

						plan_sal[i][j][k] = classItem.getId();
						continue classLoop;

					}
				}
			}
			//LOG.log(Level.WARNING, "Nie dodano zajęcia do planu", classItem);
		}
		LOG.log(Level.INFO, "Zakończono umieszczanie zajęć w planie");
		generate(plan_sal);
	}

	void generate(int plan_sal[][][]) {
		int idClass, roomNumber, hourNumber, dayNumber;
		ListIterator<ClassObject> iterator;
		ClassObject classItem = null;
		schedule = new ArrayList<>();

		dayLoop:
		for (int k = 0; k < 5; k++) {
			hourLoop:
			for (int j = 0; j < 7; j++) {
				roomLoop:
				for (int i = 0; (i < rooms.size()) && (plan_sal[i][j][k] != -1); i++) {
					idClass = plan_sal[i][j][k];
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
						schedule.add( new Schedule (classItem, room, hour, day));
						LOG.log(Level.INFO, "Added class to schedule");
					} catch (Exception e) {
						LOG.log(Level.WARNING, "Couldn't add class to schedule", e);
					}
				}
			}
		}

	}

}
