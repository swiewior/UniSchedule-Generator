package resourcesobjects;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassObject {
  private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );

  private int id;
  private int courseId;
  private int professorId;
  private int groupId;
  private boolean assigned;

  private CourseObject course;
  private GroupObject group;
  private ProfessorObject professor;

  public ClassObject(int id, int courseId, int professorId, int groupId) {
    this.id = id;
    this.courseId = courseId;
    this.professorId = professorId;
    this.groupId = groupId;
    this.assigned = false;
  }

  public void resolveClassObject(ArrayList<CourseObject> courses,
                          ArrayList<GroupObject> groups,
                          ArrayList<ProfessorObject> professors) {

    ListIterator<CourseObject> courseIterator = courses.listIterator();
    ListIterator<GroupObject> groupIterator = groups.listIterator();
    ListIterator<ProfessorObject> professorIterator = professors.listIterator();

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

  public int getProfessorId() {
    return professorId;
  }

  public int getGroupId() {
    return groupId;
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

  public GroupObject getGroup() {
    return group;
  }

  public ProfessorObject getProfessor() {
    return professor;
  }

  public boolean isAssigned() {
    return assigned;
  }

  public void setAssigned(boolean assigned) {
    this.assigned = assigned;
  }
}
