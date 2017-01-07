import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CSVParser {
	PrintWriter pw;
	StringBuilder sb;
	ArrayList<Schedule> list;

	public CSVParser(ArrayList<Schedule> list) throws FileNotFoundException,
			NullPointerException {
		if (list == null)
			throw new NullPointerException("list is empty");

		this.list = list;
		pw = new PrintWriter(new File("plan.txt"));
		sb = new StringBuilder();
		//sb.append("course,room,professor,day,start_hour,90,group_name");
		//sb.append(System.lineSeparator());

		readSchedule();
		writeToFile();
	}

	void readSchedule(){

		for (Schedule scheduleItem : list) {
			sb.append(scheduleItem.getClassObject().courseToString());
			sb.append(',');
			sb.append(scheduleItem.getRoom());
			sb.append(',');
			sb.append(scheduleItem.getClassObject().professorToString());
			sb.append(',');
			sb.append(scheduleItem.getDay());
			sb.append(',');
			sb.append(scheduleItem.getHour().getStart());
			sb.append(',');
			sb.append("90");
			sb.append(',');
			sb.append(scheduleItem.getClassObject().groupToString());
			sb.append(System.lineSeparator());
		}
	}

	void writeToFile(){
		pw.write(sb.toString());
		pw.close();
	}

}
