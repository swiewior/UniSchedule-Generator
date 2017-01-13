public class GroupObject {
	private Integer id;
	private String name;
	private String year;
	private String subject;
	private int preferences[][];

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public GroupObject(Integer id, String name, String year, String subject) {
		this.id = id;
		this.name = name;
		this.year = year;
		this.subject = subject;
	}

	public void setPreferences(int[][] preferences) {
		this.preferences = preferences;
	}

	public int[][] getPreferences() {
		return preferences;
	}
}
