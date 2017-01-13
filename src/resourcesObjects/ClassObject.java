package resourcesObjects;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassObject {
	private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );

	private int id;
	private int durationId;
	private int courseId;
	private int professorId;
	private int groupId;

	CourseObject course;
	GroupObject group;
	ProfessorObject professor;

	ListIterator<CourseObject> courseIterator;
	ListIterator<GroupObject> groupIterator;
	ListIterator<ProfessorObject> professorIterator;

	public ClassObject(int id, int durationId, int courseId, int professorId, int groupId) {
		this.id = id;
		this.durationId = durationId;
		this.courseId = courseId;
		this.professorId = professorId;
		this.groupId = groupId;
	}

	public void resolveClassObject(ArrayList<CourseObject> courses,
													ArrayList<GroupObject> groups,
													ArrayList<ProfessorObject> professors) {

		courseIterator = courses.listIterator();
		groupIterator = groups.listIterator();
		professorIterator = professors.listIterator();

		while(courseIterator.hasNext()) {
			this.course = courseIterator.next();
			if (course.getId() == courseId) break;
		}

		while(groupIterator.hasNext()) {
			this.group = groupIterator.next();
			if (group.getId() == groupId) break;
		}

		while(professorIterator.hasNext()) {
			this.professor = professorIterator.next();
			if (professor.getId() == professorId) break;
		}

		LOG.log(Level.INFO, "Zajęcia odczytano z powodzeniem");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDurationId() {
		return durationId;
	}

	public void setDurationId(int durationId) {
		this.durationId = durationId;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public int getProfessorId() {
		return professorId;
	}

	public void setProfessorId(int professorId) {
		this.professorId = professorId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String professorToString(){
		return professor.getName() + " " + professor.getSurname();
	}

	public String groupToString() {
		return group.getName();
	}

	public String courseToString(){
		return course.getName();
	}


	public CourseObject getCourse() {
		return course;
	}

	public GroupObject getGroup() {
		return group;
	}

	public ProfessorObject getProfessor() {
		return professor;
	}
}
