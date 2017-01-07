public class Schedule {
	ClassObject classObject;
	String room;
	Hour hour;
	String day;

	public Schedule(ClassObject classObject, RoomObject room, Hour hour,
									String day) {
		this.classObject = classObject;
		this.room = room.getName();
		this.hour = hour;
		this.day = day;
	}

	public ClassObject getClassObject() {
		return classObject;
	}

	public String getRoom() {
		return room;
	}

	public Hour getHour() {
		return hour;
	}

	public String getDay() {
		return day;
	}
}
