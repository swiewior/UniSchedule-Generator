package schedule;

import resourcesObjects.ClassObject;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Algorithm {
	private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );

	ArrayList<ClassObject> classes;

	/**
	 * ScheduleObjectObject (Array of class ID from Database)
	 * 1 - room number
	 * 2 - hour
	 * 3 - day
	 */
	int scheduleArray[][][];

	/**
	 * Number of rooms
	 */
	int n;

	public Algorithm(ArrayList<ClassObject> classes, int[][][] scheduleArray, int n) {
		this.classes = classes;
		this.scheduleArray = scheduleArray;
		this.n = n;

		handlePreferences();
		algorithm();
	}

	/**
	 * Assign preferences to schedule
	 * First, Professors' preferences
	 * Next, Groups' preferences
	 */
	void handlePreferences() {
		assignProfessorPreferences();
		assignGroupPreferences();

		LOG.log(Level.INFO, "Finished assigning preferences");
	}

	void assignProfessorPreferences() {
		ListIterator<ClassObject> iterator = classes.listIterator();

		classLoop:
		while(iterator.hasNext()) {
			ClassObject classItem = iterator.next();

			//check if this class is already assigned from preferences
			if(classItem.isAssigned())
				continue;

			int[][] professorPreferences = classItem.getProfessor().getPreferences();

			if (professorPreferences == null)
				continue;

			if(assignPreference(professorPreferences, classItem))
				LOG.log(Level.INFO,
						"applied preference of professor: "
								+ classItem.getProfessor().getFullName()
								+ " to class id: " + classItem.getId());
			else
				LOG.log(Level.WARNING,
						"Unable to assign preference of group: "
								+ classItem.getGroup().getName()
								+ " to class id: " + classItem.getId());

		}
	}

	void assignGroupPreferences() {
		ListIterator<ClassObject> iterator = classes.listIterator();

		classLoop:
		while (iterator.hasNext()) {
			ClassObject classItem = iterator.next();

			//check if this class is already assigned from preferences
			if(classItem.isAssigned())
				continue;

			int[][] groupPreferences = classItem.getGroup().getPreferences();

			if (groupPreferences == null)
				continue;

			if(assignPreference(groupPreferences, classItem))
				LOG.log(Level.INFO,
						"applied preference of group: "
								+ classItem.getGroup().getName()
								+ " to class id: " + classItem.getId());
			else
				LOG.log(Level.WARNING,
						"Unable to assign preference of group: "
								+ classItem.getGroup().getName()
								+ " to class id: " + classItem.getId());
		}
	}

	boolean assignPreference(int[][] preferences,
													 ClassObject classItem) {
		int roomIndex;

		for (int j = 0; j < 5; j++)
			for (int i = 0; i < 7; i++)
				if ((roomIndex = preferences[i][j]) != -1) {
					if (scheduleArray[roomIndex][i][j] != 0)
						continue;

					boolean conflict = checkConflicts(classItem, i, j);
					if (conflict)
						return false;

					// assign class to scheduleArrayList and go to next class
					scheduleArray[roomIndex][i][j] = classItem.getId();
					// set as assigned
					classItem.setAssigned(true);
					// delete assigned preference from preferences table
					preferences[i][j] = -1;
					return true;
				}
		return false;
	}

	/**
	 * Assigns classes to the 3D resources relay (room, hour and day)
	 */
	void algorithm() {
		// i - room number
		// j - hour
		// k - day

		ListIterator<ClassObject> iterator = classes.listIterator();

		classLoop:
		while(iterator.hasNext()) {
			ClassObject classItem = iterator.next();

			//check if this class is already assigned from preferences
			if(classItem.isAssigned())
				continue;

			dayLoop:
			for (int k = 0; k < 5; k++) {
				hourLoop:
				for (int j = 0; j < 7; j++) {
					roomLoop:
					for (int i = 0; i < n; i++) {

						if (scheduleArray[i][j][k] != 0)
							continue;

						boolean conflict = checkConflicts(classItem, j, k);
						if (conflict)
							continue hourLoop;

						// assign class to scheduleArrayList and go to next class
						scheduleArray[i][j][k] = classItem.getId();
						//set as assigned
						classItem.setAssigned(true);
						continue classLoop;
					}
				}
			}
			LOG.log(Level.WARNING,
					"Unable to assign class to the schedule", classItem);
		}
		LOG.log(Level.INFO, "Assigned classes to the schedule");
	}

	/**
	 * Checks if Professor or Group has classes in other room at the same time
	 * @param classItem is class object to check
	 * @param hour is an hour when we check conflicts
	 * @param day is a day when method checks for conflicts
	 * @return returns true if conflict exists or false if not
	 */
	Boolean checkConflicts(ClassObject classItem, int hour, int day) {
		ClassObject controlClass = null;

		// for efery room at the same time:
		for (int it = 0; it < n; it++) {
			if(scheduleArray[it][hour][day] == 0)
				continue;

			// get control class id form schedule
			int controlClassId = scheduleArray[it][hour][day];

			// find control class
			for (ClassObject classObject : classes)
				if(classObject.getId() == controlClassId) {
					controlClass = classObject;
					break;
				}

			// check if professor or group of control class is already assigned
			assert controlClass != null;
			if ((controlClass.getProfessorId() == classItem.getProfessorId())
					|| (controlClass.getGroupId() == classItem.getGroupId()))
				return true;
		}
		return false;
	}
}
