package io;

import defaultpackage.Hour;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import resourcesobjects.GroupObject;
import resourcesobjects.ProfessorObject;
import resourcesobjects.RoomObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class JSONReaderTest {

	final String[] JSON_DAYS = {
			"poniedzialek",	// 0
			"wtorek",				// 1
			"sroda",				// 2
			"czwartek",			// 3
			"piatek"				// 4
	};

	final Hour[] HOURS = {
			new Hour("8:00"),		// 0
			new Hour("9:45"),		// 1
			new Hour("11:30"),	// 2
			new Hour("13:15"),	// 3
			new Hour("15:00"),	// 4
			new Hour("16:45"),	// 5
			new Hour("18:30"),	// 6
	};

	ArrayList<GroupObject> groups;
	ArrayList<ProfessorObject> professors;
	ArrayList<RoomObject> rooms;

	private void writeToFile(JSONObject timetable, String fileName) {
		FileWriter file;
		try {
			file = new FileWriter(fileName + ".json");
			file.write(timetable.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteFile (String filename) {
		try {
			File file = new File(filename);
			if(file.delete()) {
				System.out.println(file.getName() + " is deleted!");
			} else {
				System.out.println(filename + " is not deleted.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Before
	public void setUp() throws Exception {
		deleteFile("Grupa1.json");
		deleteFile("Nauczyciel1.json");
	}

	/**
	 * Testing Constructor of JSONReader class
	 * - File does not exist
	 */
	@Test
	public void JSONReaderConstructorTest1 () {
		// Prepare ArrayLists
		groups = new ArrayList<>();
		GroupObject group = new GroupObject(1, "CA1");
		groups.add(group);

		professors = new ArrayList<>();
		ProfessorObject professor = new ProfessorObject(1, "Jan", "Kowalski");
		professors.add(professor);


		rooms = new ArrayList<>();
		RoomObject room = new RoomObject("101");
		rooms.add(room);

		try {
			new JSONReader(groups, professors, rooms);
		} catch (Exception e) {
			fail(e.toString());
		}

		if (group.getPreferences() != null) {
			fail ("Preferences of the group should be null");
		}
		if (professor.getPreferences() != null) {
			fail ("Preferences of the professor should be null");
		}
	}

	/**
	 * Testing Constructor of JSONReader class
	 * - Files with correct names exists
	 * - File format is NOT JSON
	 */
	@Test
	public void JSONReaderConstructorTest2 () {
		// Prepare ArrayLists
		groups = new ArrayList<>();
		GroupObject group = new GroupObject(1, "CA1");
		groups.add(group);

		professors = new ArrayList<>();
		ProfessorObject professor = new ProfessorObject(1, "Jan", "Kowalski");
		professors.add(professor);


		rooms = new ArrayList<>();
		RoomObject room = new RoomObject("101");
		rooms.add(room);

		File file;
		// Prepare file
		try {
			PrintWriter pw = new PrintWriter(file = new File("Grupa1.json"));
			StringBuilder sb = new StringBuilder();
			sb.append("czyje preferencje: ").append(group.getNameForJSON());
			sb.append(",");
			sb.append("1");
			sb.append(",");
			sb.append(room.getName());
			pw.write(sb.toString());
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
		  ByteArrayOutputStream baos = new ByteArrayOutputStream();
		  PrintStream ps = new PrintStream(baos);
			System.setOut(ps);
			new JSONReader(groups, professors, rooms);
			String content = new String (baos.toByteArray(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			fail(e.toString());
		}

		if (group.getPreferences() != null) {
			fail ("Preferences of the group should be null");
		}
		if (professor.getPreferences() != null) {
			fail ("Preferences of the professor should be null");
		}
	}

	/**
	 * Testing Constructor of JSONReader class
	 * - File with correct name exists
	 * - File format is JSON
	 * - Files contains incorrect data
	 */
	@Test
	public void JSONReaderConstructorTest3 () {
		// Prepare ArrayLists
		groups = new ArrayList<>();
		GroupObject group = new GroupObject(1, "CA1");
		groups.add(group);

		professors = new ArrayList<>();
		ProfessorObject professor = new ProfessorObject(1, "Jan", "Kowalski");
		professors.add(professor);

		rooms = new ArrayList<>();
		RoomObject room = new RoomObject("101");
		rooms.add(room);

		// Prepare JSON file
		JSONObject timetable = new JSONObject();
		JSONObject nextDayObject;
		timetable.put("czyje preferencje", group.getNameForJSON());
		for (int day = 0; day < 5; day++) {
			nextDayObject = new JSONObject();
			for (int hour = 0; hour < 7; hour++) {
				nextDayObject.put("godzina" + (hour + 1), HOURS[hour].getStart());
				if (hour == 0 && day == 0) {
					nextDayObject.put("sala" + (hour + 1), "666");
				} else {
					nextDayObject.put("sala" + (hour + 1), "Brak zajec");
				}
			}
			timetable.put(JSON_DAYS[day], nextDayObject);
		}
		writeToFile(timetable, "Grupa1");

		// Prepare JSON file
		timetable = new JSONObject();
		timetable.put("czyje preferencje", professor.getNameForJSON());
		for (int day = 0; day < 5; day++) {
			nextDayObject = new JSONObject();

			for (int hour = 0; hour < 7; hour++) {
				nextDayObject.put("godzina" + (hour + 1), HOURS[hour].getStart());
				if (hour == 1 && day == 0) {
					nextDayObject.put("sala" + (hour + 1), "543");
				} else {
					nextDayObject.put("sala" + (hour + 1), "Brak zajec");
				}
			}
			timetable.put(JSON_DAYS[day], nextDayObject);
		}
		writeToFile(timetable, "Nauczyciel1");

		try {
			new JSONReader(groups, professors, rooms);
		} catch (Exception e) {
			fail(e.toString());
		}



		int[][] preferences = group.getPreferences();
		if (preferences == null) {
			fail ("Preferences of the group should not be null");
		}
		for (int dayIndex = 0; dayIndex < 5; dayIndex++) {
			for (int hourIndex = 0; hourIndex < 7; hourIndex++) {
				if ((preferences[hourIndex][dayIndex]) != -1) {
					fail("Preferences of the group should not be assigned");
				}
			}
		}

		preferences = professor.getPreferences();
		if (preferences == null) {
			fail ("Preferences of the professor should not be null");
		}
		for (int dayIndex = 0; dayIndex < 5; dayIndex++) {
			for (int hourIndex = 0; hourIndex < 7; hourIndex++) {
				if ((preferences[hourIndex][dayIndex]) != -1) {
					fail("Preferences of the group should not be assigned");
				}
			}
		}
	}

	/**
	 * Testing Constructor of JSONReader class
	 * - File with correct name exists
	 * - File format is JSON
	 * - Files contains correct data to read
	 */
	@Test
	public void JSONReaderConstructorTest4 () {
		// Prepare ArrayLists
		groups = new ArrayList<>();
		GroupObject group = new GroupObject(1, "CA1");
		groups.add(group);

		professors = new ArrayList<>();
		ProfessorObject professor = new ProfessorObject(1, "Jan", "Kowalski");
		professors.add(professor);

		rooms = new ArrayList<>();
		RoomObject room = new RoomObject("101");
		rooms.add(room);

		// Prepare JSON file
		JSONObject timetable = new JSONObject();
		JSONObject nextDayObject;
		timetable.put("czyje preferencje", group.getNameForJSON());
		for (int day = 0; day < 5; day++) {
			nextDayObject = new JSONObject();
			for (int hour = 0; hour < 7; hour++) {
				nextDayObject.put("godzina" + (hour + 1), HOURS[hour].getStart());
				if (hour == 0 && day == 0) {
					nextDayObject.put("sala" + (hour + 1), room.getName());
				} else {
					nextDayObject.put("sala" + (hour + 1), "Brak zajec");
				}
			}
			timetable.put(JSON_DAYS[day], nextDayObject);
		}
		writeToFile(timetable, "Grupa1");

		// Prepare JSON file
		timetable = new JSONObject();
		timetable.put("czyje preferencje", professor.getNameForJSON());
		for (int day = 0; day < 5; day++) {
			nextDayObject = new JSONObject ();

			for (int hour = 0; hour < 7; hour++) {
				nextDayObject.put("godzina" + (hour + 1), HOURS[hour].getStart());
				if (hour == 1 && day == 0) {
					nextDayObject.put("sala" + (hour + 1), room.getName());
				} else {
					nextDayObject.put("sala" + (hour + 1), "Brak zajec");
				}
			}
			timetable.put(JSON_DAYS[day], nextDayObject);
		}
		writeToFile(timetable, "Nauczyciel1");

		try {
			new JSONReader(groups, professors, rooms);
		} catch (Exception e) {
			fail(e.toString());
		}

		int[][] preferences = group.getPreferences();
		if (preferences == null) {
			fail ("Preferences of the group should not be null");
		}
		if ((preferences[0][0]) != 0) {
			fail("Group's preference should be 0");
		}

		preferences = professor.getPreferences();
		if (preferences == null) {
			fail ("Preferences of the group should not be null");
		}
		if ((preferences[1][0]) != 0) {
			fail("Professor's preference should be 0");
		}

	}
}