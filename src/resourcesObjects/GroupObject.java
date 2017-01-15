package resourcesObjects;

public class GroupObject {
	private Integer id;
	private String name;
	private int preferences[][];

	public String getName() {
		return name;
	}

	public Integer getId() {
		return id;
	}

	public GroupObject(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public void setPreferences(int[][] preferences) {
		this.preferences = preferences;
	}

	public int[][] getPreferences() {
		return preferences;
	}

	public String getNameForJSON() {
		return "Grupa:" + id + ":" + name;
	}
}
