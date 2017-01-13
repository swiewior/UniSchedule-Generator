package io;

import resourcesObjects.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQLReader {
	Connection connection;
	private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );

	public MySQLReader(Connection connection) {
		this.connection = connection;
		init();
	}

	public MySQLReader() {
		init();
	}

	private void init(){
		//Inicjalizacja Bazy Danych
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "", ex);
			return;
		}
		String url = "jdbc:mysql://mysql.agh.edu.pl:3306/wwydryc1";
		try {
			connection = DriverManager.getConnection(url, "wwydryc1", "V8XwxsFN");
		} catch (SQLException ex) {
			LOG.log(Level.SEVERE, "Nie połączono z bazą danych", ex);
			return;
		}
		LOG.log(Level.INFO, "Połączono z bazą danych");
	}

	public void readGropus(ArrayList<GroupObject> list){
		GroupObject groupObject;

		try {
			final String query = "SELECT A.*, B.nazwa_kierunku"
					+ " FROM grupy A"
					+ " INNER JOIN kierunek B"
					+ " ON (A.kierunek = B.ID);";
			try (Statement st = connection.createStatement()) {
				ResultSet rs = st.executeQuery(query);
				while (rs.next())
				{
					Integer id = rs.getInt("ID");
					String name = rs.getString("nazwa");
					String year = rs.getString("rok_rozp");
					String course = rs.getString("kierunek");

					groupObject = new GroupObject(id, name, year, course);
					list.add(groupObject);
				}
				LOG.log(Level.INFO, "Odczytano grupy");
			}
		} catch (SQLException e) {
			LOG.log(Level.WARNING, "", e);
		}
	}

	public void readProfessors(ArrayList<ProfessorObject> list){
		try {
			final String query = "SELECT * FROM prowadzacy;";
			try (Statement st = connection.createStatement()) {
				ResultSet rs = st.executeQuery(query);
				while (rs.next()) {
					list.add(
							new ProfessorObject(
									rs.getInt("ID"),
									rs.getString("imie"),
									rs.getString("nazwisko")
							)
					);
				}
				LOG.log(Level.INFO, "Odczytano profesorów");
			}
		} catch (SQLException e) {
			LOG.log(Level.WARNING, "", e);
		}
	}

	public void readRooms(ArrayList<RoomObject> list){
		try {
			final String query = "SELECT * FROM sale;";
			try (Statement st = connection.createStatement()) {
				ResultSet rs = st.executeQuery(query);
				while (rs.next()) {
					list.add(
							new RoomObject(
									rs.getInt("ID"),
									rs.getString("sala")
							)
					);
				}
			}
			LOG.log(Level.INFO, "Odczytano pokoje");
		} catch (SQLException e) {
			LOG.log(Level.WARNING, "", e);
		}
	}

	public void readCourses(ArrayList<CourseObject> list){
		try {
			final String query = "SELECT * FROM przedmiot;";
			try (Statement st = connection.createStatement()) {
				ResultSet rs = st.executeQuery(query);
				while (rs.next()) {
					list.add(
						new CourseObject(
								rs.getInt("ID"),
								rs.getString("nazwa")
						)
					);
				}
				LOG.log(Level.INFO, "Odczytano przedmioty");
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "", e);
		}
	}

	public void readClasses(ArrayList<ClassObject> list){
		try {
			final String query = "SELECT * FROM zajecia;";
			try (Statement st = connection.createStatement()) {
				ResultSet rs = st.executeQuery(query);
				while (rs.next()) {
					list.add(
							new ClassObject(
									rs.getInt("ID"),
									rs.getInt("lgodzin"),
									rs.getInt("przedmiot"),
									rs.getInt("prowadzacy"),
									rs.getInt("grupa")
							)
					);
				}
				LOG.log(Level.INFO, "Pobrano zajęcia");
			}
		} catch (SQLException e) {
			LOG.log(Level.WARNING, "", e);
		}
	}


}