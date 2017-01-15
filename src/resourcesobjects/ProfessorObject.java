package resourcesobjects;

public class ProfessorObject {
  private int id;
  private String name;
  private String surname;
  private int preferences[][];


  public ProfessorObject(int id, String name, String surname) {
    this.id = id;
    this.name = name;
    this.surname = surname;
  }

  public int getId() {
    return id;
  }

  String getName() {
    return name;
  }

  String getSurname() {
    return surname;
  }

  public void setPreferences(int[][] preferences) {
    this.preferences = preferences;
  }

  public int[][] getPreferences() {
    return preferences;
  }

  public String getFullName() {
    return name + " " + surname;
  }

  public String getNameForJSON() {
    return "Nauczyciel:" + id + ":" + name + " " + surname;
  }
}
