import org.json.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Objects;

public class JSONReader {

	GroupObject groupItem;
	ProfessorObject professorItem;

	ArrayList<GroupObject> groups;
	ArrayList<ProfessorObject> professors;
	ArrayList<RoomObject> rooms;
	ArrayList<String> fileNames;

	ListIterator<GroupObject> groupsIterator;
	ListIterator<ProfessorObject> professorsIterator;
	ListIterator<RoomObject> roomsIterator;
	ListIterator<String> fileNamesIterator;

	static final String[] JSON_DAYS = {
			"poniedzialek",
			"wtorek",
			"sroda",
			"czwartek",
			"piatek"
	};

	public JSONReader(ArrayList<GroupObject> groups,
										ArrayList<ProfessorObject> professors,
										ArrayList<RoomObject> rooms) {
		this.groups = groups;
		this.professors = professors;
		this.rooms = rooms;

		getFileNames();

		fileNamesIterator = fileNames.listIterator();
		while (fileNamesIterator.hasNext()) {
			readJSON(fileNamesIterator.next());
		}
	}

	private void getFileNames() {
		fileNames = new ArrayList<>();

		groupsIterator = groups.listIterator();
		while (groupsIterator.hasNext()) {
			Integer groupId = groupsIterator.next().getId();
			fileNames.add("grupa" + groupId.toString());
		}

		professorsIterator = professors.listIterator();
		while (professorsIterator.hasNext()) {
			Integer professorId = professorsIterator.next().getId();
			fileNames.add("prowadzacy" + professorId.toString());
		}
	}

	private void readJSON(String fileName) {
		RoomObject roomItem;
		JSONObject file = new JSONObject(fileName);
		String name = file.getString("czyje preferencje");
		int preferences[][] = new int[7][5];

		// Day Loop
		for (int j = 0; j < 5; j++) {
			JSONObject day = file.getJSONObject(JSON_DAYS[j]);

			// Hour Loop
			for (int i = 0; i < 7; i++) {
				preferences[i][j] = -1; // fill preferences with -1 by default

				String room = day.getString("sala" + i+1);
				if (!Objects.equals(room, "null")) {

					// get ArrayList index of room and assign to preferences
					roomsIterator = rooms.listIterator();
					while (roomsIterator.hasNext()) {
						roomItem = roomsIterator.next();
						if (Objects.equals(roomItem.getName(), room)) {
							preferences[i][j] = rooms.indexOf(roomItem);
							break;
						}
					}
				}
			}
		}
		assignPreferences(name, preferences);
	}

	private void assignPreferences(String name, int preferences[][]) {
		GroupObject groupItem;
		ProfessorObject professorItem;

		// Find group and assign preferences
		groupsIterator = groups.listIterator();
		while (groupsIterator.hasNext()) {
			groupItem = groupsIterator.next();
			if (Objects.equals(groupItem.getName(), name)) {
				groupItem.setPreferences(preferences);
				break;
			}
		}

		// Find professor and assign preferences
		professorsIterator = professors.listIterator();
		while (professorsIterator.hasNext()) {
			professorItem = professorsIterator.next();
			if (Objects.equals(professorItem.getFullName(), name)) {
				professorItem.setPreferences(preferences);
				break;
			}
		}
	}

}
