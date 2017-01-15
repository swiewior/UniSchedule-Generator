package resourcesObjects;

public class CourseObject {
	private int id;
	private String name;

	public CourseObject(int id, String name) {
		this.id = id;
		this.name = name;
	}

	int getId() {
		return id;
	}

	String getName() {
		return name;
	}
}
