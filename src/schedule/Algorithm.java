package schedule;

import resourcesObjects.ClassObject;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Algorithm {
	private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );

	private ArrayList<ClassObject> classes;

	/**
	 * ScheduleObjectObject (Array of class ID from Database)
	 * 1 - room number
	 * 2 - hour
	 * 3 - day
	 */
	private int scheduleArray[][][];

	/**
	 * Number of rooms
	 */
	private int n;

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
	private void handlePreferences() {
		assignProfessorPreferences();
		assignGroupPreferences();

		LOG.log(Level.INFO, "Finished assigning preferences");
	}

	private void assignProfessorPreferences() {
		for (ClassObject classItem : classes) {
			//check if this class is already assigned from preferences
			if (classItem.isAssigned())
				continue;

			int[][] professorPreferences = classItem.getProfessor().getPreferences();

			if (professorPreferences == null)
				continue;

			if (assignPreference(professorPreferences, classItem))
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

	private void assignGroupPreferences() {
		for (ClassObject classItem : classes) {
			//check if this class is already assigned from preferences
			if (classItem.isAssigned())
				continue;

			int[][] groupPreferences = classItem.getGroup().getPreferences();

			if (groupPreferences == null)
				continue;

			if (assignPreference(groupPreferences, classItem))
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

	private boolean assignPreference(int[][] preferences,
																	 ClassObject classItem) {
		int roomIndex;

		for (int dayIndex = 0; dayIndex < 5; dayIndex++)
			for (int hourIndex = 0; hourIndex < 7; hourIndex++)
				if ((roomIndex = preferences[hourIndex][dayIndex]) != -1) {
					if (scheduleArray[roomIndex][hourIndex][dayIndex] != 0)
						continue;

					boolean conflict = checkConflicts(classItem, hourIndex, dayIndex);
					if (conflict)
						return false;

					// assign class to scheduleArrayList and go to next class
					scheduleArray[roomIndex][hourIndex][dayIndex] = classItem.getId();
					// set as assigned
					classItem.setAssigned(true);
					// delete assigned preference from preferences table
					preferences[hourIndex][dayIndex] = -1;
					return true;
				}
		return false;
	}

	/**
	 * Assigns classes to the 3D resources relay (room, hour and day)
	 */
	private void algorithm() {
		ListIterator<ClassObject> iterator = classes.listIterator();

		classLoop:
		while(iterator.hasNext()) {
			ClassObject classItem = iterator.next();

			//check if this class is already assigned from preferences
			if(classItem.isAssigned())
				continue;

			dayLoop:
			for (int dayIndex = 0; dayIndex < 5; dayIndex++) {
				hourLoop:
				for (int hourIndex = 0; hourIndex < 7; hourIndex++) {
					roomLoop:
					for (int roomIndex = 0; roomIndex < n; roomIndex++) {

						if (scheduleArray[roomIndex][hourIndex][dayIndex] != 0)
							continue;

						boolean conflict = checkConflicts(classItem, hourIndex, dayIndex);
						if (conflict)
							continue hourLoop;

						// assign class to scheduleArrayList and go to next class
						scheduleArray[roomIndex][hourIndex][dayIndex] = classItem.getId();
						// set as assigned
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
	 * @param hourIndex is an hour when we check conflicts
	 * @param dayIndex is a day when method checks for conflicts
	 * @return returns true if conflict exists or false if not
	 */
	private Boolean checkConflicts(ClassObject classItem, int hourIndex, int dayIndex) {
		ClassObject controlClass = null;

		// for efery room at the same time:
		for (int roomIndex = 0; roomIndex < n; roomIndex++) {
			if(scheduleArray[roomIndex][hourIndex][dayIndex] == 0)
				continue;

			// get control class id form schedule
			int controlClassId = scheduleArray[roomIndex][hourIndex][dayIndex];

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
