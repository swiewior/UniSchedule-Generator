import org.json.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Objects;

public class JSONReader {

	ArrayList<GroupObject> groups;
	ArrayList<ProfessorObject> professors;
	ListIterator<GroupObject> groupsIterator;
	ListIterator<ProfessorObject> professorsIterator;

	String preferences[][];
	String name;

	static final String[] JSONDAYS = {
			"poniedzialek",
			"wtorek",
			"sroda",
			"czwartek",
			"piatek"
	};

	public JSONReader(ArrayList<GroupObject> groups,
										ArrayList<ProfessorObject> professors) {
		this.groups = groups;
		this.professors = professors;

		JSONObject file = new JSONObject("preferencje.json");
		ReadJSON(file);
		AssignPreferences();
	}

	private void ReadJSON(JSONObject file) {
		name = file.getString("czyje preferencje");

		JSONObject schedulePreferences = file.getJSONObject("preferencje planu");
		// Day Loop
		for (int i = 0; i < 5; i++)
		{
			JSONObject day = schedulePreferences.getJSONObject(JSONDAYS[i]);

			// Hour Loop
			for (int j = 0; j < 7; j++) {
				if (!Objects.equals(day.getString("sala" + i + 1), "null"))
					preferences[i][j] = day.getString("sala" + i + 1);
			}
		}
	}

	private void AssignPreferences() {
		GroupObject groupItem;
		ProfessorObject professorItem;

		groupsIterator = groups.listIterator();
		while (groupsIterator.hasNext()) {
			groupItem = groupsIterator.next();
			if (Objects.equals(groupItem.getName(), name)) {
				groupItem.setPreferences(preferences);
				break;
			}
		}

		professorsIterator = professors.listIterator();
		while (professorsIterator.hasNext()) {
			professorItem = professorsIterator.next();
			if (Objects.equals(professorItem.getName(), name)) {
				professorItem.setPreferences(preferences);
				break;
			}
		}
	}

	void test() {
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader("preferencje.json"));   //test.json to plik do odczytu
			JSONObject timetable = (JSONObject) obj;      //timetable to glony obiekt

			JSONObject monday = timetable.getJSONObject("poniedzialek");    //Obiekt poniedzialkowy


			String czyje_preferencje = (String) timetable.get("czyje preferencje");
			String godzina1Monday = (String) monday.get("godzina1");
			String sala1Monday = (String) monday.get("sala1");

			System.out.println("Sala1: " + sala1Monday + "\n" + "Godzina1 : " +
					godzina1Monday + "\nCzyje preferencje: " + czyje_preferencje);

		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

}
