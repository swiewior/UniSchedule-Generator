package io;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import resourcesobjects.GroupObject;
import resourcesobjects.ProfessorObject;
import resourcesobjects.RoomObject;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Decode JSON file with preferences from 2nd group
 */
public class JSONReader {
  private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );

  private static final String[] JSON_DAYS = {
      "poniedzialek",
      "wtorek",
      "sroda",
      "czwartek",
      "piatek"
  };

  private ArrayList<GroupObject> groups;
  private ArrayList<ProfessorObject> professors;
  private ArrayList<RoomObject> rooms;
  private ArrayList<String> fileNames;

  private ListIterator<GroupObject> groupsIterator;
  private ListIterator<ProfessorObject> professorsIterator;

  public JSONReader(ArrayList<GroupObject> groups,
                    ArrayList<ProfessorObject> professors,
                    ArrayList<RoomObject> rooms) {
    this.groups = groups;
    this.professors = professors;
    this.rooms = rooms;

    getFileNames();

    for (String fileName : fileNames) {
      File file = new File(fileName);
      if (file.exists() && !file.isDirectory()) {
        try {
          readJSON(fileName);
        } catch (Exception e) {
          LOG.log(Level.WARNING, "", e);
        }
      } else {
        LOG.log(Level.WARNING, fileName + " doesn't exist");
      }
    }
  }

  private void getFileNames() {
    fileNames = new ArrayList<>();

    professorsIterator = professors.listIterator();
    while (professorsIterator.hasNext()) {
      Integer professorId = professorsIterator.next().getId();
      fileNames.add("Nauczyciel" + professorId.toString() + ".json");
    }

    groupsIterator = groups.listIterator();
    while (groupsIterator.hasNext()) {
      Integer groupId = groupsIterator.next().getId();
      fileNames.add("Grupa" + groupId.toString() + ".json");
    }
  }

  private void readJSON(String fileName)
      throws JSONException, ParseException, IOException {
    RoomObject roomItem;

    JSONParser parser = new JSONParser();

    Object obj = parser.parse(new FileReader(fileName));
    JSONObject jsonObject = (JSONObject) obj;

    String name = (String) jsonObject.get("czyje preferencje");

    int preferences[][] = new int[7][5];

    for (int dayIndex = 0; dayIndex < 5; dayIndex++) {

      JSONObject day = (JSONObject) jsonObject.get(JSON_DAYS[dayIndex]);

      for (int hourIndex = 0; hourIndex < 7; hourIndex++) {
        // fill preferences with -1 by default
        preferences[hourIndex][dayIndex] = -1;

        String room = (String)  day.get("sala" + String.valueOf(hourIndex + 1));
        if (!Objects.equals(room, "Brak zajec")) {

          // get ArrayList index of room and assign to preferences
          for (RoomObject room1 : rooms) {
            roomItem = room1;
            if (Objects.equals(roomItem.getName(), room)) {
              preferences[hourIndex][dayIndex] = rooms.indexOf(roomItem);
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

    // Find professor and assign preferences
    professorsIterator = professors.listIterator();
    while (professorsIterator.hasNext()) {
      professorItem = professorsIterator.next();
      if (Objects.equals(professorItem.getNameForJSON(), name)) {
        professorItem.setPreferences(preferences);
        break;
      }
    }

    // Find group and assign preferences
    groupsIterator = groups.listIterator();
    while (groupsIterator.hasNext()) {
      groupItem = groupsIterator.next();
      if (Objects.equals(groupItem.getNameForJSON(), name)) {
        groupItem.setPreferences(preferences);
        break;
      }
    }
  }
}