/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android_connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


 
public class MySQLConnection_old {
 
  private static Connection conn = null;
 
  // Hostname
  private static String dbHost = "127.0.0.1";
 
  // Port -- Standard: 3306
  private static String dbPort = "3306";
 
  // Datenbankname
  private static String database = "android_connect";
 
  // Datenbankuser
  private static String dbUser = "root";
 
  // Datenbankpasswort
  private static String dbPassword = "";
 
  private MySQLConnection_old() {
    try {
 
      // Datenbanktreiber für ODBC Schnittstellen laden.
      // Für verschiedene ODBC-Datenbanken muss dieser Treiber
      // nur einmal geladen werden.
      Class.forName("com.mysql.jdbc.Driver");
 
      // Verbindung zur ODBC-Datenbank 'sakila' herstellen.
      // Es wird die JDBC-ODBC-Brücke verwendet.
      conn = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":"
          + dbPort + "/" + database + "?" + "user=" + dbUser + "&"
          + "password=" + dbPassword);
    } catch (ClassNotFoundException e) {
      System.out.println("Treiber nicht gefunden");
    } catch (SQLException e) {
      System.out.println("Connect nicht moeglich");
    }
  }
 
  private static Connection getInstance()
  {
    if(conn == null)
      new MySQLConnection_old();
    return conn;
  }
 
  /**
   * Schreibt die Namensliste in die Konsole
   */
  public static void printNameList()
  {
    conn = getInstance();
 
    if(conn != null)
    {
      // Anfrage-Statement erzeugen.
      Statement query;
      try {
        query = conn.createStatement();
 
        // Ergebnistabelle erzeugen und abholen.
        String sql = "SELECT * FROM namen "
            + "ORDER BY Startnummer";
        ResultSet result = query.executeQuery(sql);
 
        // Ergebnissätze durchfahren.
        while (result.next()) {
          String first_name = result.getString("Namen"); // Alternativ: result.getString(1);
          String last_name = result.getString("Startnummer"); // Alternativ: result.getString(2);
          String name = last_name + ", " + first_name;
          System.out.println(name);
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
 
  /**
   * Fügt einen neuen Datensatz hinzu 
   */
  public static void insertName(String firstName, String lastName)
  {
    conn = getInstance();
 
    if(conn != null)
    {
      try {
 
        // Insert-Statement erzeugen (Fragezeichen werden später ersetzt).
        String sql = "INSERT INTO actor(first_name, last_name) " +
                     "VALUES(?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        // Erstes Fragezeichen durch "firstName" Parameter ersetzen
        preparedStatement.setString(1, firstName);
        // Zweites Fragezeichen durch "lastName" Parameter ersetzen
        preparedStatement.setString(2, lastName);
        // SQL ausführen.
        preparedStatement.executeUpdate();
 
        // Es wird der letzte Datensatz abgefragt
        String lastActor = "SELECT actor_id, first_name, last_name " +
                           "FROM actor " +
                           "ORDER BY actor_id DESC LIMIT 1";
        ResultSet result = preparedStatement.executeQuery(lastActor);
 
        // Wenn ein Datensatz gefunden wurde, wird auf diesen zugegriffen 
        if(result.next())
        {
          System.out.println("(" + result.getInt(1) + ")" + 
              result.getString(2) + " " + 
              result.getString(3));
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
 
  /**
   * Aktualisiert den Datensatz mit der übergebenen actorId 
   */
  public static void updateName(String firstName, String lastName, int actorId)
  {
    conn = getInstance();
 
    if(conn != null)
    {
      try {
 
        String querySql = "SELECT actor_id, first_name, last_name " +
                          "FROM actor " +
                          "WHERE actor_id = ?";
         
        // PreparedStatement erzeugen.
        PreparedStatement preparedQueryStatement = conn.prepareStatement(querySql);
        preparedQueryStatement.setInt(1, actorId);
        ResultSet result = preparedQueryStatement.executeQuery();
 
        if(result.next())
        {
          // Vorher
          System.out.println("VORHER: (" + result.getInt(1) + ")" + 
                                           result.getString(2) + " " + 
                                           result.getString(3));
        }
 
        // Ergebnistabelle erzeugen und abholen.
        String updateSql = "UPDATE actor " +
                           "SET first_name = ?, last_name = ? " +
                           "WHERE actor_id = ?";
        PreparedStatement preparedUpdateStatement = conn.prepareStatement(updateSql);
        // Erstes Fragezeichen durch "firstName" Parameter ersetzen
        preparedUpdateStatement.setString(1, firstName);
        // Zweites Fragezeichen durch "lastName" Parameter ersetzen
        preparedUpdateStatement.setString(2, lastName);
        // Drittes Fragezeichen durch "actorId" Parameter ersetzen
        preparedUpdateStatement.setInt(3, actorId);
        // SQL ausführen
        preparedUpdateStatement.executeUpdate();
         
        // Es wird der letzte Datensatz abgefragt
        result = preparedQueryStatement.executeQuery();
 
        if(result.next())
        {
          System.out.println("NACHHER: (" + result.getInt(1) + ")" + 
                                            result.getString(2) + " " + 
                                            result.getString(3));
        }
 
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
}
