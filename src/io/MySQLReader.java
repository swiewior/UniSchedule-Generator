package io;

import resourcesobjects.CourseObject;
import resourcesobjects.GroupObject;
import resourcesobjects.ProfessorObject;
import resourcesobjects.RoomObject;
import resourcesobjects.ClassObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Decodes MySQL Database with resources from 1st group
 */
public class MySQLReader {
  private Connection connection;
  private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );

  public MySQLReader() {
    init();
  }

  /**
   * Initialize connection
   */
  private void init(){
    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, "", ex);
      return;
    }
    String url = "jdbc:mysql://localhost:3306/io";
    try {
      connection = DriverManager.getConnection(url, "root", "");
    } catch (SQLException ex) {
      LOG.log(Level.SEVERE, "Couldn't connect with database", ex);
      return;
    }
    LOG.log(Level.INFO, "Connected with database");
  }

  public void readGropus(ArrayList<GroupObject> list){
    GroupObject groupObject;


      final String query = "SELECT A.*, B.nazwa_kierunku"
          + " FROM grupy A"
          + " INNER JOIN kierunek B"
          + " ON (A.kierunek = B.ID);";
      try (Statement st = connection.createStatement()) {
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
          Integer id = rs.getInt("ID");
          String name = rs.getString("nazwa");

          groupObject = new GroupObject(id, name);
          list.add(groupObject);
        }
        LOG.log(Level.INFO, "Finished reading groups");
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
        LOG.log(Level.INFO, "Finished reading professors");
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
                  rs.getString("sala")
              )
          );
        }
      }
      LOG.log(Level.INFO, "Finished reading rooms");
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
        LOG.log(Level.INFO, "Finished reading subjects");
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
                  rs.getInt("przedmiot"),
                  rs.getInt("prowadzacy"),
                  rs.getInt("grupa")
              )
          );
        }
        LOG.log(Level.INFO, "Loaded classes");
      }
    } catch (SQLException e) {
      LOG.log(Level.WARNING, "", e);
    }
  }


}