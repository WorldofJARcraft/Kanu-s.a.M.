/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android_connector;

/**
 *
 * @author Eric Ackermann
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javax.swing.JOptionPane;


/*
 Quellen:
 http://stackoverflow.com/questions/11839246/how-to-convert-timestamp-to-date-in-java
 http://groglogs.blogspot.de/2012/07/java-calendar-date-to-timestamp.html
 http://stackoverflow.com/questions/14980899/how-to-set-time-to-24-hour-format-in-calendar
 http://stackoverflow.com/questions/11839246/how-to-convert-timestamp-to-date-in-java
 http://stackoverflow.com/questions/136419/get-integer-value-of-the-current-year-in-java
 */
/**
 * @author Eric Ackermann Klasse, die eine Verbindung zu einer MySQL-Datenbank
 * herstellen und Werte ein- und ausgeben kann
 *
 */
public class MySQLConnection {

    /**
     * Verbindung zur Datenbank
     */
    private Connection conn = null;

    /**
     * Hostname
     */
    public String dbHost = "localhost";
    /**
     * MySQL-Port
     */
    public String dbPort = "3306";

    /**
     * Datenbankname
     */
    public String database = "android_connect";

    /**
     * Datenbankuser
     */
    public String dbUser = "root";

    /**
     * Datenbankpasswort
     */
    public String dbPassword = "";
    /**
     * Instanz des Controllers, der diese Klasse geschaffen hat. Zum Zugriff auf
     * bestimmte interne Attribute wichtig.
     */
    private FXMLDocumentController doc;

    /**
     * Leerer Konstruktor der Klasse. Nutzt Standardwerte für die Konfiguration
     * und tut eigentlich nichts. Zum Zugriff auf bestimmte unabhängige Methoden
     * gedacht.
     *
     * @param doc ein FXMLDocumentController
     */
    public MySQLConnection(FXMLDocumentController doc) {
        this.doc = doc;
    }

    /**
     * Hauptkonstruktor der Klasse
     *
     * @param host Datenbankhost (Standard: "localhost")
     * @param port Datenbankport (Standard: "3306")
     * @param db Datenbankname (Standard: "android_connect"
     * @param user Datenbankbenutzername (Standard: "root")
     * @param password Datenbankpasswort (Standard: leer)
     */
    MySQLConnection(String host, String port, String db, String user, String password, FXMLDocumentController doc) {
        try {
            //Verbindungsdaten speichern
            this.dbHost = host;
            this.dbPort = port;
            this.database = db;
            this.dbUser = user;
            this.dbPassword = password;
            // Datenbanktreiber für ODBC Schnittstellen aus der Library laden.
            // Für verschiedene ODBC-Datenbanken muss dieser Treiber
            // nur einmal geladen werden
            //Resultat: im java.sql.DriverManager wird (statisch) der geladene Treiber als Datenbanktreiber hinterlegt,
            //dafür verantwortlich: Befehl im static_Teil der Treiberdefinition, der bei Zugriff auf die Klasse ausgeführt wird
            //Treiber wird nur gefunden, wennlib-Unterordner da ist!
            Class.forName("com.mysql.jdbc.Driver");
            // Verbindung zur ODBC-Datenbank android_connect herstellen und für Verwendung speichern.
            // Es wird die JDBC-ODBC-Brücke verwendet.
            conn = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":"
                    + dbPort + "/" + database + "?" + "user=" + dbUser + "&"
                    + "password=" + dbPassword);
            this.doc = doc;
        } catch (ClassNotFoundException e) {

            //aufgerufen, wenn Treiber nicht gefunden wird
            //Ausgabe einer Fehlermeldung mittels Alert, dabei wird auf die Fläche ein TextArea gelegt, in das der Stacktrace der Exception geladen wird.
            /*showExceptionDialog(e, "Fehler", "Nötige Bibliothek konnte nicht geladen werden!", "Bitte stellen Sie sicher, dass der Ordner \"lib\" im selben Ordner wie die ausführbare .jar-Datei dieses Programms liegt"
                    + " und eine Datei namens \"mysql-connector-java-5.1.38-bin.jar\" enthält und verwenden Sie ggf. eine neue Kopie dieses Programms!", false);
             */ } catch (SQLException e) {
            //aufgerufen, wenn keine Verbindung zur DB möglich
            /*showExceptionDialog(e, "Fehler", "Verbindungsaufbau zur Datenbank nicht möglich!", "Bitte prüfen Sie, ob der Apache- und MySQL-Server gestartet sind und korrekt laufen sowie die "
                    + "korrekten Konfigurationswerte für den Verbindungsaufbau angegeben sind und starten Sie das Programm neu!", false);*/
        }
    }

    /**
     * Gibt einen Fehlermeldungsdialog auf die grafische Benutzeroberfläche aus.
     * Dazu wird ein Objekt der Klasse Alert angelegt, welches im ausblendbaren
     * Bereich ein TextArea enthält. In das wird ein Fehlerdialog geschrieben.
     *
     * @author http://code.makery.ch/blog/javafx-dialogs-official/
     * @param e Exception, deren Stacktrace gedruckt werden soll.
     * @param titel Fenstertitel
     * @param header prägnante Information im oberen Teil des Dialogs
     * @param content Inhalt mit spezifischerer Fehlermeldung
     * @param deleteTable true, wenn dem Nutzer angeboten werden soll, die
     * Datenbank komplett zu löschen, sonst false
     */
    public void showExceptionDialog(Exception e, String titel, String header, String content, boolean deleteTable) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titel);
        alert.setHeaderText(header);
        alert.setContentText(content);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Geben Sie bei einer Fehlermeldung an den Entwickler bitte folgenden Text an: ");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
        if (deleteTable) {
            //Bestätigung anzeigen, dass die Datenbank geleert werden soll.
            alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Datenbank leeren");
            alert.setHeaderText("Klicken Sie OK, um die Datenbank zu leeren!");
            alert.setContentText("Mindestens eine benötigte Tabelle in der Datenbank existiert. Möchten Sie sie leeren? Das Programm muss dann neu gestartet werden."
                    + " Bei Klick auf Abbruch wird das Programm beendet, und Sie müssen die Tabellen manuell löschen. Dies ist zu empfehlen, wenn eine andere Instanz des Programms läuft.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                // ... user chose OK
                reset(true);
            } else {
                // ... user chose CANCEL or closed the dialog
                System.exit(0);
            }
        }
    }

    /**
     * Zeigt einen schönen Fehlermeldungsdialog mit den übergebenen Werten an.
     * Static, kann also von überall aus aufgerufen werden.
     *
     * @param e die Exception, die geworfen wurde
     * @param titel der Titel des Dialogs
     * @param header die Beschriftung unter dem Titel
     * @param content der eigentliche Fehlermeldungstext
     */
    public static void staticExceptionDialog(Exception e, String titel, String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titel);
        alert.setHeaderText(header);
        alert.setContentText(content);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Geben Sie bei einer Fehlermeldung an den Entwickler bitte folgenden Text an: ");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    /**
     * weist "conn" eine Verbindung zur Datenbank zu, wenn keine besteht
     *
     * @return Verbindung zur Standard-Datenbank
     */
    private Connection getInstance() {
        //prüfen, ob conn null ist --> keine Verbindung besteht
        if (conn == null) {
            //wenn ja, Konstruktor aufrufen --> conn wird dort eine Verbindung zugewiesen
            final MySQLConnection mySQLConnection = new MySQLConnection(dbHost, dbPort, database, dbUser, dbPassword, doc);
        }
        //Rückgabe der Verbindung
        return conn;
    }

    /**
     * Legt eine Tabelle für eine Messstation mit dem übergebenen Nanem an
     *
     * @param name Name der Tabelle
     */
    public void messpunktanlegen(String name) {
        //ggf. Verbindung zur DB herstellen
        conn = getInstance();
        //setzt SQL-Abfrage an Datenbank zusammen
        //Tabellen für die Messstationen bestehen aus 2 Spalten: der Startnummer als Primärschlüssel und dem Timestamp des Eintreffens dieser
        String sql = "CREATE TABLE " + name + "(\n"
                + "Startnummer VARCHAR(50) PRIMARY KEY UNIQUE KEY, Zeitpunkt INT\n"
                + ");";
        //führt SQL-Abfrage aus
        Statement query;
        try {
            //Statement aus der Verbindung zur Datenbank ableiten
            query = conn.createStatement();
            //Statement ausführen --> "execute" für Schreibzugriffe auf die Datenbank
            boolean result = query.execute(sql);
        } catch (SQLException e) {
            //aufgerufen bei SQL-Fehlern
            showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Die Tabelle \"" + name + "\" konnte nicht angelegt werden. Bitte prüfen Sie, ob diese existiert, löschen sie, und starten das Programm neu!", true);
        }
    }

    /**
     * legt die Tabelle "allgemein" an und speichert dort als ersten Wert die
     * Zahl der Stationen
     *
     * @param Anzahl der Stationen
     */
    public void allgemeineTabelleanlegen(String Anzahl) {
        //ggf. Verbindung zur DB herstellen
        conn = getInstance();
        //Zugriffsbefehl zur Erstellung der Tabelle
        String sql = "CREATE TABLE allgemein("
                + "Attribut VARCHAR(50) PRIMARY KEY UNIQUE KEY, Wert TEXT"
                + ");";
        //Befehl ausführen
        Statement query;
        try {
            query = conn.createStatement();
            boolean result = query.execute(sql);
        } catch (SQLException e) {
            //SQL-Fehler ist aufgetreten
            showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Die Tabelle \"allgemein\" konnte nicht angelegt werden. Bitte prüfen Sie, ob diese existiert, löschen sie, und starten das Programm neu!", true);
        }
        //Zugriffsbefehl auf Tabelle "allgemein" zum Eintragen der Zahl der Stationen zusammensetzen--> wird von der App für die Anmeldung an die Stationen gebraucht
        sql = "INSERT INTO `" + database + "`.`allgemein` (`Attribut`, `Wert`) VALUES ('Zahl_Stationen', '" + Anzahl + "');";
        //Befehl ausführen
        try {
            query = conn.createStatement();
            boolean result = query.execute(sql);
        } catch (SQLException e) {
            //SQL-Fehler ist aufgetreten
            showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.", true);
        }
        //Attribut Lauf aktualisieren
        try {
            //Statement Verbindung zuweisen
            query = conn.createStatement();
            // SQL-Befehl zusammensetzen...
            sql = "INSERT INTO `" + database + "`.`allgemein` (`Attribut`, `Wert`) VALUES ('lauf', '0');";
            //... und mittels execute ausführen
            boolean result = query.execute(sql);
        } catch (SQLException e) {
            //SQL-Fehler
            showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.", true);
        }
        //anfangs: Zeiten eintragen verhindern
        try {
            //Statement Verbindung zuweisen
            query = conn.createStatement();
            // SQL-Befehl zusammensetzen...
            sql = "INSERT INTO `" + database + "`.`allgemein` (`Attribut`, `Wert`) VALUES ('gestartet', 'false');";
            //... und mittels execute ausführen
            boolean result = query.execute(sql);
        } catch (SQLException e) {
            //SQL-Fehler
            showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.", true);
        }
    }

    /**
     * Legt eine Tabelle für die Auswertung an, die alle Starter enthält.
     *
     * @param werte alle Starter (bisher) als zweidimensioanles String-Array;
     * erster Index: fortlaufende Nummer, am 2. Index bei 0 Startnummer des
     * Starters, bei 1 dessen Name, bei 2 dessen Kategorie
     */
    public void auswertungAnlegen(String[][] werte) {
        //ggf. Verbindung zur DB herstellen
        conn = getInstance();
        //setzt SQL-Abfrage an Datenbank zusammen
        //Tabellen für die Messstationen bestehen aus 2 Spalten: der Startnummer als Primärschlüssel und dem Timestamp des Eintreffens dieser
        String sql = "CREATE TABLE auswertung("
                + "Namen TEXT, Startnummer VARCHAR(50) PRIMARY KEY UNIQUE KEY, Kategorie VARCHAR(50), Lauf_1 TEXT NULL, Lauf_2 TEXT NULL, Lauf_Wiederholung TEXT NULL, Wiederholung_ersetzt TINYINT NUll"
                + ");";
        //führt SQL-Abfrage aus
        Statement query;
        try {
            //Statement aus der Verbindung zur Datenbank ableiten
            query = conn.createStatement();
            //Statement ausführen --> "execute" für Schreibzugriffe auf die Datenbank
            boolean result = query.execute(sql);
        } catch (SQLException e) {
            //aufgerufen bei SQL-Fehlern
            showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Die Tabelle \"auswertung\" konnte nicht angelegt werden. Bitte prüfen Sie, ob diese existiert, löschen sie, und starten das Programm neu!", true);
        }
        for (String[] zeile : werte) {
            String anfr = "INSERT INTO `auswertung` (`Namen`, `Startnummer`, `Kategorie`, `Lauf_1`, `Lauf_2`, `Lauf_Wiederholung`, `Wiederholung_ersetzt`) VALUES ('" + zeile[1] + "', '" + zeile[0] + "', '" + zeile[2] + "', '', '', NULL, NULL);";
            System.out.println(anfr);
            try {
                //Statement aus der Verbindung zur Datenbank ableiten
                query = conn.createStatement();
                //Statement ausführen --> "execute" für Schreibzugriffe auf die Datenbank
                boolean result = query.execute(anfr);
            } catch (SQLException e) {
                //aufgerufen bei SQL-Fehlern
                //showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Die Tabelle \"auswertung\" konnte nicht angelegt werden. Bitte prüfen Sie, ob diese existiert, löschen sie, und starten das Programm neu!", true);
            }
        }
    }

    /**
     * Leert alle Messtore von Einträgen und entfernt Start- und Zielzeiten aus
     * der Namenstabelle. Setzt auch gestartet- und Startzeit- Werte zurück.
     *
     * @param tore Zahl der Messtore
     */
    public void messWerteLeeren(int tore) {
        conn = getInstance();
        //wenn verbindung besteht...
        if (conn != null) {
            //... Anfrage-Statement erzeugen.
            Statement query;
            for (int i = 0; i < doc.tore; i++) //Tabelle "namen" löschen
            {
                try {
                    //Erzeugung eines Statements
                    query = conn.createStatement();
                    //SQL-Befehl zusammensetzen...
                    String sql = "TRUNCATE `messstation_" + i + "`";
                    //... und ausführen
                    boolean result = query.execute(sql);

                } catch (SQLException e) {
                    //showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.", false);
                    //SQL-Fehler
                }
            }
            try {
                //Erzeugung eines Statements
                query = conn.createStatement();
                //SQL-Befehl zusammensetzen...
                String sql = "UPDATE `namen` SET `Startzeit` = NULL;";

                //... und ausführen
                boolean result = query.execute(sql);

            } catch (SQLException e) {
                //showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.", false);
                //SQL-Fehler
            }
            try {
                //Erzeugung eines Statements
                query = conn.createStatement();
                //SQL-Befehl zusammensetzen...
                String sql
                        = "UPDATE `namen` SET `Zielzeit` = NULL;\n";
                //... und ausführen
                boolean result = query.execute(sql);

            } catch (SQLException e) {
                //showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.", false);
                //SQL-Fehler
            }
            try {
                //Erzeugung eines Statements
                query = conn.createStatement();
                //SQL-Befehl zusammensetzen...
                String sql
                        = "UPDATE `allgemein` SET `Wert` = 'false' WHERE `allgemein`.`Attribut` = 'gestartet';\n";
                //... und ausführen
                boolean result = query.execute(sql);

            } catch (SQLException e) {
                //showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.", false);
                //SQL-Fehler
            }
            try {
                //Erzeugung eines Statements
                query = conn.createStatement();
                //SQL-Befehl zusammensetzen...
                String sql
                        = "DELETE FROM `allgemein` WHERE `allgemein`.`Attribut` = 'Startzeit'";
                //... und ausführen
                boolean result = query.execute(sql);

            } catch (SQLException e) {
                //showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.", false);
                //SQL-Fehler
            }
        }
    }

    /**
     * Fügt in die Tabelle "allgemein" der Datenbank die Zuordnung der Tore zu
     * einer Messstation ein.
     *
     * @param messstation "Messstation_0_Tore", "Messstation_1_Tore" usw.
     * @param tore z.B. "0|1|2"
     */
    public void toreZuordnen(String messstation, String tore) {

        conn = getInstance();
        //fügt in die allgemeine Tabelle die Tore ein, die der übergebenen Messstation zugeordnet sind
        String sql = "INSERT INTO `allgemein` (`Attribut`, `Wert`) VALUES ('" + messstation + "', '" + tore + "');";
        //Statement zur Ausführung
        Statement query;
        try {
            //Befehl ausführen
            query = conn.createStatement();
            boolean result = query.execute(sql);
        } catch (SQLException e) {
            //SQL-Fehler ist aufgetreten
            showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.", false);
        }
    }

    /**
     * Legt die Tabelle in der Datenbank an, in der Namen und Startnummern
     * stehen
     */
    public void nameninitialisieren() {
        //ggf. Verbindung zur DB herstellen
        conn = getInstance();
        //Befehl zur Erzeugung der Tabelle besrtehend aus Startnummer (Primärschlüssel) und Name 
        String sql = "CREATE TABLE namen("
                + "Namen TEXT, Startnummer VARCHAR(50) PRIMARY KEY UNIQUE KEY, Startzeit TEXT NULL, Zielzeit TEXT NULL, Lauf_1 TEXT NULL, Lauf_2 TEXT NULL, Lauf_Wiederholung TEXT NULL, Wiederholung_ersetzt INT NUll"
                + ");";
        //Statement zur Ausführung
        Statement query;
        try {
            //Befehl ausführen
            query = conn.createStatement();
            boolean result = query.execute(sql);
        } catch (SQLException e) {
            //SQL-Fehler ist aufgetreten
            showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Die Tabelle \"namen\" konnte nicht angelegt werden. Bitte prüfen Sie, ob diese existiert, löschen sie, und starten das Programm neu!", false);
        }
    }

    /**
     * Schreibt einen Namen und eine zugehörige Startnummer in die Tabelle
     * "namen".
     *
     * @param startnummer
     * @param name
     */
    public void nameeinfügen(String startnummer, String name) {
        //ggf. Verbindung zur DB herstellen
        conn = getInstance();
        //wenn Verbindung besteht...
        if (conn != null) {
            // ... Anfrage-Statement erzeugen.
            Statement query;
            try {
                //Statement Verbindung zuweisen
                query = conn.createStatement();
                // SQL-Befehl zusammensetzen...
                String sql = "INSERT INTO `" + database + "`.`namen` (`Namen`, `Startnummer`) VALUES ('" + name + "', '" + startnummer + "');";
                //... und mittels execute ausführen
                boolean result = query.execute(sql);
            } catch (SQLException e) {
                //SQL-Fehler
                showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.", false);
            }
        }
    }

    /**
     * Löscht alle erstellten Tabellen für eine erneute Verwendung aus der
     * Datenbank. Wird bei Programmende ausgeführt.
     *
     * @param auswertungLoeschen True, wenn die Tabelle "auswertung" gelöscht
     * werden soll, sonst false.
     */
    public void reset(boolean auswertungLoeschen) {
        //ggf. Verbindung herstellen
        conn = getInstance();
        //wenn verbindung besteht...
        if (conn != null) {
            //... Anfrage-Statement erzeugen.
            Statement query;
            //Tabelle "namen" löschen
            try {
                //Erzeugung eines Statements
                query = conn.createStatement();
                //SQL-Befehl zusammensetzen...
                String sql = "DROP TABLE namen";
                //... und ausführen
                boolean result = query.execute(sql);

            } catch (SQLException e) {
                //showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.", false);
                //SQL-Fehler
            }
            if (auswertungLoeschen) {
                //Tabelle "auswertung" löschen
                try {
                    //Erzeugung eines Statements
                    query = conn.createStatement();
                    //SQL-Befehl zusammensetzen...
                    String sql = "DROP TABLE auswertung";
                    //... und ausführen
                    boolean result = query.execute(sql);

                } catch (SQLException e) {
                    //showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.", false);
                    //SQL-Fehler
                }
            }
            //Tabelle "allgemein" löschen
            try {
                query = conn.createStatement();

                // Befehl zusammensetzen und ausführen
                String sql = "DROP TABLE allgemein";
                boolean result = query.execute(sql);

            } catch (SQLException e) {
                //SQL-Fehler
                //showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.", false);
            }
            //Tabellen der Messstationen einzeln löschen
            boolean fehler = false;
            for (int i = 0; !fehler; i++) {
                //jeweils...
                try {
                    //Statement vorbereiten,...
                    query = conn.createStatement();
                    //... jeweiligen Löschbefehl formulieren...
                    String sql = "DROP TABLE Messstation_" + i + ";";
                    //... und ausführen
                    boolean result = query.execute(sql);
                } catch (SQLException e) {
                    //SQL-Fehler
                    //solange die Nummer der Tabelle erhöhen, bis nicht mehr vorhanden --> dann alle gelöscht
                    fehler = true;
                    //showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.");
                }
            }
        }

    }

    /**
     * Liest die Startnummern und Zeiten für eine bestimmte Station aus.
     *
     * @param tore Anzahl der Messtore
     * @param starter Zahl der Startnummern
     * @param werte ListView, in dem die Werte dann dargestellt werden --> wird
     * @param startzeit Calendar, dessen Zeit der Startzeit entspricht der
     * Einfachheit halber übergeben
     */
    public void zeitenauslesen(int tore, int starter, ListView<String> werte, Calendar startzeit) {
        //ggf. verbindung herstellen
        conn = getInstance();
        //bei bestehender Verbindung...
        if (conn != null) {
            //prüfen, ob Startzeiten eingetragen sind
            startzeitenauslesen();
            zielzeitenauslesen();
            if (doc.gestartet) {
                // ... Anfrage-Statement erzeugen, ...
                Statement query;
                //Array anlegen, das die Gesamtstrafen aller Startnummern enthält; +1: einmal, da max größten Index ermittelt
                int[] strafen = new int[doc.startnummerMax + 1];
                try {
                    for (int i = 0; i < tore; i++) {
                        //... ihm eine Verbindung zuweisen, ...
                        query = conn.createStatement();
                        // die Abfrage generieren...
                        String sql = "SELECT * FROM `messstation_" + i + "` ORDER BY `messstation_" + i + "`.`Zeitpunkt` ASC ";
                        //und durchführen; dafür: Verwendung von executeQuery, was ein ResulSet mit den gefundenen Datenbankeinträgen zurück gibt
                        ResultSet result = query.executeQuery(sql);
                        //für jede Zeile der Ergebnisse...
                        while (result.next()) {
                            //... Startnummer...
                            int startnummer = Integer.parseInt(result.getString("Startnummer"));
                            //... und Strafzeit ermitteln ... 
                            String stamp = result.getString("Zeitpunkt");
                            //... und dann die Strafzeit im vorgesehenen Array speichern
                            int millis = Integer.parseInt(stamp);
                            doc.strafen[startnummer][i] = millis;
                            strafen[startnummer] += millis;

                        }

                    }
                } catch (SQLException e) {
                    //SQL-Fehler
                    //keine Fehlermeldung zeigen, weil sonst unendlich viele Fenster aufgehen
                    //showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.");
                }
                //speichert Zeilenwerte für das ListView
                ObservableList<String> items = FXCollections.observableArrayList();
                //Array,  das die Startnummern enthält
                //Aufgabe: Zuordnung der Startnummern zu den Strafen --> gleicher Index beider Arrays ergibt Startnummer und ihre Strafzeit
                int[] startnummern = new int[doc.startnummerMax + 1];
                for (Integer starter1 : doc.starter) {
                    startnummern[starter1] = starter1;
                }
                //sortieren: Starter mit kleinster Strafzeit hoch
                sortieren(strafen, startnummern);
                //einzeln Zeiten der Starter ausgeben
                for (int i = 0; i < doc.startnummerMax + 1; i++) {
                    //prüfen, ob aktuelle Startnummer existiert und bereits gestartet ist
                    if (doc.starter.contains(startnummern[i]) && doc.startzeiten[startnummern[i]] != null) {
                        /**
                         * Gibt true an, wenn die Startnummer bereits im Ziel
                         * ist, sonst false.
                         */
                        boolean fertig = false;
                        //aktuelle Zeit ermitteln...
                        Calendar cal;
                        //prüfen, ob bereits Stoppzeit besteht, dann diese nehmen
                        if (doc.zielzeiten[startnummern[i]] != null) {
                            //Neues Calendar-Objekt erzeugen, diesem die Zielzeit zuweisen --> damit set-Methode später nicht die echte Zielzeit ändert
                            cal = Calendar.getInstance();
                            cal.setTimeInMillis((doc.zielzeiten[startnummern[i]]).getTimeInMillis());
                            fertig = true;
                            //sonst: aktuelle Zeit nehmen
                        } else {
                            cal = Calendar.getInstance();
                        }
                        /**
                         * Speichert reine Fahrtzeit der Startnummer ohne
                         * Strafen
                         */
                        String reine_Zeit = differenz(cal, doc.startzeiten[startnummern[i]]);
                        long tmp = cal.getTimeInMillis();
                        //... und Strafzeit des aktuellen Starters aufaddieren...
                        cal.setTimeInMillis(strafen[i] * 1000 + cal.getTimeInMillis());
                        long tmp2 = cal.getTimeInMillis();
                        //Differenz zur Ursprungszeit ermitteln ...
                        /**
                         * Speichert die aktuelle Zeit der Startnummer mit
                         * Strafen
                         */
                        String gesamtZeit = differenz(cal, doc.startzeiten[startnummern[i]]);
                        //ermitteln, ob die Startnummer im Ziel ist
                        String status = "Status: unterwegs";
                        if (fertig) {
                            status = "Status: im Ziel";
                        }
                        //... und die Zeile für das ListView zusammensetzen
                        String zeile = "Startnummer: " + (startnummern[i]) + " aktuelle Zeit: " + reine_Zeit + ", Strafen: " + strafen[i] + " Sekunden, Gesamtzeit: " + gesamtZeit + "; " + status;
                        //Zeile der Liste hinzufügen
                        items.add(zeile);
                    }
                }
                items = werteSortieren(items);
                //Elemente des ListView sind jetzt die ermittelten Zeilen
                werte.setItems(items);
            }
        }
    }

    /**
     * Liest aus der Tabelle "namen" die jeweiligen Startzeiten der Startnummern
     * aus, konvertiert diese in Objekte der Klasse Calendar und speichert sie
     * in dem vorgesehenen Array. Schaltet auch Button-Zustände um und löst
     * Beginn- und Schlussmethoden aus.
     */
    public void startzeitenauslesen() {
        //ggf. verbindung herstellen
        conn = getInstance();
        //bei bestehender Verbindung...
        if (conn != null) {
            // ... Anfrage-Statement erzeugen, ...
            Statement query;
            List<Integer> starter = doc.starter;
            try {
                for (int i = 0; i < starter.size(); i++) {
                    query = conn.createStatement();
                    // die Abfrage generieren...
                    String sql = "SELECT Startzeit FROM `namen` WHERE `namen`.`Startnummer` = '" + starter.get(i) + "';";
                    //System.out.println(sql);
                    //und durchführen; dafür: Verwendung von executeQuery, was ein ResulSet mit den gefundenen Datenbankeinträgen zurück gibt
                    ResultSet result = query.executeQuery(sql);
                    //für jede Zeile der Ergebnisse...
                    while (result.next()) {
                        //... Startnummer...
                        //String sn = result.getString("Startnummer"); 
                        //int startnummer = Integer.parseInt(sn);
                        //... und Strafzeit ermitteln ... 
                        String stamp = result.getString("Startzeit");
                        //prüfen, ob Startzeit vorhanden für spez. Startnummer
                        if (stamp != null) {
                            //PHP-Zeit zerlegen...
                            //...: erster Teil ist Timestamp in Sekunden, letzter Teil ist Zahl der Milli-
                            //Sekunden, letzte Ziffer ist Komma
                            long zeit = Long.parseLong(stamp);
                            //Calendar initialisieren...
                            Calendar cal = Calendar.getInstance();
                            //... und Zeit zuweisen
                            cal.setTimeInMillis(zeit);
                            //bei "erstem" Start einer Startnummer kann diese auch gestoppt werden --> 
                            //Stoppbutton aktivieren, wenn noch kein Eintrag vorhanden
                            if (doc.startzeiten[starter.get(i)] == null) {
                                doc.getStoppButton().setDisable(false);
                            }
                            //Startzeit speichern
                            doc.startzeiten[starter.get(i)] = cal;
                            //wenn noch nicht gestartet, dann Startprozedur aufrufen
                            if (doc.startzeit == null) {
                                doc.start(cal);
                            }
                            //wenn keine Startnummern verbleiben, Startbutton deaktivieren und speichern,
                            //dass alle gestartet sind
                            if (doc.alleGestartet().isEmpty()) {
                                doc.getStartButton().setDisable(true);
                                doc.alleGestartet = true;
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                //SQL-Fehler nicht zeigen, da ewig viele Fenster entstehen
                //showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.");
            }
        }
    }
    boolean bereitsgestoppt = false;

    /**
     * Liest aus der Tabelle "namen" die jeweiligen Zielzeiten der Startnummern
     * aus, konvertiert diese in Objekte der Klasse Calendar und speichert sie
     * in dem vorgesehenen Array. Schaltet auch Button-Zustände um und löst
     * Beginn- und Schlussmethoden aus.
     */
    public void zielzeitenauslesen() {
        //ggf. verbindung herstellen
        conn = getInstance();
        //bei bestehender Verbindung...
        if (conn != null) {
            // ... Anfrage-Statement erzeugen, ...
            Statement query;
            List<Integer> starter = doc.starter;
            try {
                for (int i = 0; i < starter.size(); i++) {
                    query = conn.createStatement();
                    // die Abfrage generieren...
                    String sql = "SELECT Zielzeit FROM `namen` WHERE `namen`.`Startnummer` = '" + starter.get(i) + "';";
                    //System.out.println(sql);
                    //und durchführen; dafür: Verwendung von executeQuery, was ein ResulSet mit den gefundenen Datenbankeinträgen zurück gibt
                    ResultSet result = query.executeQuery(sql);
                    //für jede Zeile der Ergebnisse...
                    while (result.next()) {
                        //... die Start- bzw. Zielzeit ermitteln
                        String stamp = result.getString("Zielzeit");
                        //Prüfen, ob vorhanden (standardmäßig: null)
                        if (stamp != null) {
                            //Startzeit ist Java-Timestamp --> als solchen verwenden
                            long zeit = Long.parseLong(stamp);
                            //Zeit als Calendar-Objekt speichern
                            Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(zeit);
                            //Zielzeit im vorgesehenen Array speichern
                            doc.zielzeiten[starter.get(i)] = cal;
                            //keine stoppbare Startnummer mehr da --> Stoppbutton ausblenden
                            if (doc.alleGestoppt().isEmpty()) {
                                doc.getStoppButton().setDisable(true);
                            }
                            //alle gestartet und im Ziel --> Ende, entsprechende Methode aufrufen
                            if (doc.alleGestartet && doc.alleGestoppt().isEmpty()) {
                                //Stoppmethoden müssen nur 1 mal aufgerufen werden
                                if (!werteFestgehalten) {
                                    werteFestgehalten = true;
                                    doc.stop();
                                    werteFesthalten(doc.lauf, doc.kategorie);
                                }
                            }

                        }
                    }
                }
            } catch (SQLException e) {
                //SQL-Fehler keine Warnmeldung, da oft pro Sekunde aufgerufen
                //showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.");
            }
        }
    }

    /**
     * Setzt das "gestartet"-Attribut in der Datenbank auf "true" --> Eintragung
     * von Zeiten möglich, speichert die Startzeit --> Smartphones zeigen
     * aktuelle Zeit an.
     */
    public void start_Messung(Calendar startzeit) {
        //ggf. Verbindung zur DB herstellen
        conn = getInstance();
        //wenn Verbindung besteht...
        if (conn != null) {
            // ... Anfrage-Statement erzeugen.
            Statement query;
            try {
                //Statement Verbindung zuweisen
                query = conn.createStatement();
                // SQL-Befehl zusammensetzen...
                String sql = "UPDATE `allgemein` SET `Wert` = 'true' WHERE `allgemein`.`Attribut` = 'gestartet';";
                //... und mittels execute ausführen
                boolean result = query.execute(sql);
            } catch (SQLException e) {
                //SQL-Fehler
                showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.", false);
            }
            //Startzeit speichern
            try {
                //Statement Verbindung zuweisen
                query = conn.createStatement();
                // SQL-Befehl zusammensetzen...
                String sql = "INSERT INTO `" + database + "`.`allgemein` (`Attribut`, `Wert`) VALUES ('Startzeit', '" + startzeit.getTime().getTime() + "');";
                //... und mittels execute ausführen
                boolean result = query.execute(sql);
            } catch (SQLException e) {
                //SQL-Fehler
                showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.", false);
            }
        }
    }

    /**
     * Berechnet die Differenz zwischen zwei Zeitpunkten.
     *
     * @param stamp MySQL-Timestamp
     * @param startzeit Timestamp in Millisekunden
     * @return
     */
    private String differenz(Calendar cal, Calendar startzeit) {
        String ret = "";
        /*String jahr = stamp.substring(0, stamp.indexOf("-"));
         int year = Integer.parseInt(jahr);
         System.out.println("Eintreffendes Jahr: "+year);
         stamp = stamp.substring(stamp.indexOf("-") + 1);
         String monat = stamp.substring(0, stamp.indexOf("-"));
         int month = Integer.parseInt(monat);
         System.out.println("Eintreffender Monat: "+month);
         stamp = stamp.substring(stamp.indexOf("-") + 1);
         String tag = stamp.substring(0, stamp.indexOf(" "));
         int day = Integer.parseInt(tag);
         System.out.println("Eintreffender Tag: "+day);
         stamp = stamp.substring(stamp.indexOf(" ") + 1);
         String stunde = stamp.substring(0, stamp.indexOf(":"));
         int hour = Integer.parseInt(stunde);
         System.out.println("Eintreffende Stunde: "+hour);
         stamp = stamp.substring(stamp.indexOf(":") + 1);
         String minute = stamp.substring(0, stamp.indexOf(":"));
         System.out.println("Eintreffende Minute: "+minute);
         int min = Integer.parseInt(minute);
         stamp = stamp.substring(stamp.indexOf(":") + 1);
         String sekunde = stamp;
         double second = Double.parseDouble(sekunde);
         System.out.println("Eintreffende Sekunde: "+second);
         double zeit = second * 1000 + min * 60 * 1000 + hour * 60 * 60 * 1000 + day * 24 * 60 * 60 * 1000;
         */

 /*double djahre = Math.abs(cal.get(Calendar.YEAR)) - Math.abs(startzeit.get(Calendar.YEAR));
         if (djahre != 0) {
         //ret += djahre + " Jahre, ";
         }
         double dmonate = Math.abs(cal.get(Calendar.MONTH) - 1) - Math.abs(startzeit.get(Calendar.MONTH) - 1);
         if (dmonate != 0) {
         //ret += dmonate + " Monate, ";
         }
         double dtage = Math.abs(cal.get(Calendar.DATE)) - Math.abs(startzeit.get(Calendar.DATE));
         if (dtage != 0) {
         //ret += (dtage) + " Tage, ";
         }
         double dstunden = Math.abs(cal.get(Calendar.HOUR_OF_DAY)) - Math.abs(startzeit.get(Calendar.HOUR_OF_DAY));
         if (dstunden != 0) {
         //ret += (dstunden) + " Stunden, ";
         }
         double dminuten = Math.abs(cal.get(Calendar.MINUTE)) - Math.abs(startzeit.get(Calendar.MINUTE));
         if (dminuten != 0) {
         //ret += (dminuten) + " Minuten, ";
         }
         double dsekunden = Math.abs(cal.get(Calendar.SECOND)) - Math.abs(startzeit.get(Calendar.SECOND));
         //ret += (dsekunden) + " Sekunden. ";*/
        //Differenz der Zeiten in Millisekunden ermitteln...
        long diff = cal.getTimeInMillis() - startzeit.getTimeInMillis();
        //... und diese in neuem Calendar speichern
        Date date = new Date(diff);
        Calendar result = new GregorianCalendar();
        result.setTime(date);
        //Einzelwerte ausgeben...
        /*System.out.println("Jahresdiff: " + (result.get(Calendar.YEAR) - 1970));
        System.out.println("Monatsdiff: " + (result.get(Calendar.MONTH) - 1));
        System.out.println("Tagesdiff: " + (result.get(Calendar.DAY_OF_MONTH) - 1));
        System.out.println("Stundendiff: " + (result.get(Calendar.HOUR_OF_DAY) - 1));
        System.out.println("Minutendiff: " + result.get(Calendar.MINUTE));
        System.out.println("Sekundendiff: " + result.get(Calendar.SECOND));
        System.out.println("Millidiff: " + result.get(Calendar.MILLISECOND));*/
        //System.out.println("aktuelle Zeit: "+cal.getTimeInMillis());
        //... und Bestandteile der Differenz der Rückgabe hinzufügen
        if (result.get(Calendar.YEAR) - 1970 > 0) {
            ret += (result.get(Calendar.YEAR) - 1970) + " Jahre, ";
        }
        if ((result.get(Calendar.MONTH) - 1) > 0) {
            ret += ((result.get(Calendar.MONTH) - 1)) + " Monate, ";
        }
        if ((result.get(Calendar.DAY_OF_MONTH) - 1) > 0) {
            ret += ((result.get(Calendar.DAY_OF_MONTH) - 1)) + " Tage, ";
        }
        if ((result.get(Calendar.HOUR_OF_DAY) - 1) > 0) {
            ret += (result.get(Calendar.HOUR_OF_DAY) - 1) + ":";
        }
        //Minuten-, Sekunden- und Millisekundenunterschiede werden zwecks der Einheitlichkeit der Anzeige immer ausgegeben
        //if (result.get(Calendar.MINUTE) > 0) {
        ret += result.get(Calendar.MINUTE) + ":";
        //}
        //if (result.get(Calendar.SECOND) > 0) {
        ret += result.get(Calendar.SECOND) + ",";
        //}
        //if (result.get(Calendar.MILLISECOND) > 0) {
        ret += result.get(Calendar.MILLISECOND) + "";
        //}
        return ret;
    }

    /**
     * Sortiert ein Array, welches die Strafsekunden der Starter enthält. Ordnet
     * dabei die Startnummern in einem zweiten Array mit um. Sortierverfahren:
     * Bubblesort.
     *
     * @param strafen Array mit den Strafsekunden
     * @param startnummern Array mit den Startnummern
     */
    private void sortieren(int[] strafen, int[] startnummern) {
        //Sortieren von strafen mittels Bubblesort, gleiche Tauschoperationen in startnummern
        for (int i = 0; i < strafen.length; i++) {
            for (int j = 0; j < strafen.length - 1; j++) {
                if (j > 0) {
                    if (strafen[j] > strafen[j + 1]) {
                        int hilf = strafen[j];
                        strafen[j] = strafen[j + 1];
                        strafen[j + 1] = hilf;
                        hilf = startnummern[j];
                        startnummern[j] = startnummern[j + 1];
                        startnummern[j + 1] = hilf;
                    }
                }
            }
        }
    }
    /**
     * Systemabhängiger Zeilenumbruch.
     */
    public String LINE_SEPARATOR = System.getProperty("line.separator");
    /**
     * Speichert, ob die Methode "werteFesthalten" bereits aufgerufen wurde.
     */
    private boolean werteFestgehalten = false;

    /**
     * Speichert die Werte des aktuellen Laufs für die Startnummern in der
     * Datenbank zur späteren Auswertung. Erweiterung: zusätzliches Speichern
     * eines Textprotokolls.
     *
     * @param lauf Nummer des aktuellen Laufs
     * @param kategorie kategorie, in der die Starter gerade angetreten sind
     */
    public void werteFesthalten(int lauf, String kategorie) {
        System.out.println("Lauf in Menschenzählweise: " + (lauf + 1));
        /**
         * Speichert den Text der Protokolldatei.
         */
        String datei = "Alle Werte sind im Format Startnummer|reine Laufzeit|Gesamtstrafen|Gesamtzeit|Tor Nummer 1|Strafzeit bei Tor 1| usw. gespeichert.";
        //ggf. Verbindung zur DB herstellen
        conn = getInstance();
        //wenn Verbindung besteht...
        if (conn != null) {
            FileWriter fw = null;
            try {
                fw = new FileWriter("boo.txt");
                // ... Anfrage-Statement erzeugen.
                Statement query;
                try {
                    //Statement Verbindung zuweisen
                    query = conn.createStatement();
                    /**
                     * Abfrage an die DB.
                     */
                    String sql;
                    //Alle Starter durchgehen...
                    for (int i = 0; i < doc.starter.size(); i++) {
                        String Strafen = "";
                        for (int j = 0; j < doc.tore; j++) {
                            Strafen += "Tor: " + (j + 1) + "|Strafe: " + doc.strafen[doc.starter.get(i)][j] + "|";
                        }

                        if (lauf < 2) //... für jeden SQL-Befehl zusammensetzen...
                        {
                            sql = "UPDATE `namen` SET `Lauf_" + (lauf + 1) + "` = '" + wert(doc.starter.get(i)) + "|" + Strafen + "' WHERE `namen`.`Startnummer` = '" + doc.starter.get(i) + "';\n";
                        } //... und mittels execute ausführen
                        else {
                            sql = "UPDATE `namen` SET `Lauf_Wiederholung` = '" + wert(doc.starter.get(i)) + "|" + Strafen + "' WHERE `namen`.`Startnummer` = '" + doc.starter.get(i) + "';\n";
                        }
                        System.out.print(sql);
                        boolean result = query.execute(sql);
                        if (lauf < 2) {
                            sql = "UPDATE `auswertung` SET `Lauf_" + (lauf + 1) + "` = '" + wert(doc.starter.get(i)) + "|" + Strafen + "' WHERE `auswertung`.`Startnummer` = '" + doc.starter.get(i) + "'";
                        } else {
                            sql = "UPDATE `auswertung` SET `Lauf_Wiederholung` = '" + wert(doc.starter.get(i)) + "|" + Strafen + "' WHERE `auswertung`.`Startnummer` = '" + doc.starter.get(i) + "'";
                        }
                        System.out.print(sql);
                        result = query.execute(sql);
                        //Zudem Werte in String speichern, der ggf. später in Protokolldatei landet
                        datei += LINE_SEPARATOR + doc.starter.get(i) + "|" + wert(doc.starter.get(i)) + "|" + Strafen;
                    }
                } catch (SQLException e) {
                    //SQL-Fehler
                    showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.", false);
                }
                //wenn Protokollierung gewünscht...
                if (pfad != null && !pfad.isEmpty()) {
                    //standardmäßig Dateinamen zusammensetzen als Lauf_ und Nummer des Laufes
                    File file = new File(pfad + "\\Kategorie_" + kategorie + "_Lauf_" + (lauf + 1) + ".txt");
                    while (file.exists()) {
                        //Datei existiert --> fragen, ob überschrieben werden soll
                        int dec = JOptionPane.showConfirmDialog(null, "Datei " + file.getAbsolutePath() + " existiert bereits. Möchten Sie sie überschreiben?", "Datei existiert", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        //sonst: neuen Dateinamen einlesen, Datei zuweisen, erneut prüfen
                        if (dec != JOptionPane.YES_OPTION) {
                            String name = JOptionPane.showInputDialog(null, "Bitte geben Sie einen anderen Dateinamen (keinen Pfad) ein!", "neuer Dateiname", JOptionPane.OK_OPTION);
                            file = new File(pfad + "\\" + name + ".txt");
                        } //wenn ja: Schleife einfach verlassen
                        else {
                            break;
                        }
                    }
                    //... den Protokollstring in einer Datei im gewählten Ordner speichern, die die Nummer des Laufes im Dateinamen hat
                    fw = new FileWriter(file);
                    try (BufferedWriter bw = new BufferedWriter(fw)) {
                        bw.write(datei);
                    }
                }
                //Schreibexceptions fangen, Fehlermeldung ausgeben
            } catch (IOException ex) {
                showExceptionDialog(ex, "Fehler", "Schreibfehler", "Die Datei konnte nicht geschrieben werden!", false);
            } finally {
                try {
                    fw.close();
                } catch (IOException ex) {
                    showExceptionDialog(ex, "Fehler", "Schreibfehler", "Internes Schreibproblem! Bitte Ausgabedatei überprüfen!", false);
                }
            }
        }
    }
    /**
     * Pfad des Speicherverzeichnisses für Protokolldateien
     */
    private String pfad = null;

    /**
     * Setter für pfad
     *
     * @param wert Pfad des Verzeichnisses zur Speicherung von Protokolldateien
     */
    public void setPfad(String wert) {
        pfad = wert;
    }

    /**
     * Ermittelt die Angabe von Laufzeit, Strafen, Gesamtzeit für eine bestimmte
     * Startnummer.
     *
     * @param get Startnummer als int-Zahl
     * @return Zeiten im Format "reine Laufzeit|Strafen in Sekunden|Gesamtzeit",
     * wenn Startnummer enthalten, sonst leerer String
     */
    private String wert(Integer get) {
        //ListView mit Einträgen ermitteln
        ListView view = doc.getListView();
        //anfangs leere Rückgabe
        String ret = "";
        //alle Einträge (damit alle Startnummern) durchgehen
        for (Object item : view.getItems()) {
            //prüfen, ob der jeweilige Wert auch sicher ein String ist --> Sicherheit
            if (item instanceof String) {
                /**
                 * Aktuelle Zeile des ListViews
                 */
                String zeile = (String) item;
                /**
                 * Enthält Teil, der Startnummer angibt.
                 */
                String sn = zeile.substring(zeile.indexOf("Startnummer: ") + 13, zeile.indexOf("aktuelle Zeit:") - 1);
                //prüfen, ob die Zeile die der gesuchten Startnummer ist.
                if (Integer.parseInt(sn) == get) {
                    //Laufzeit ermitteln...
                    zeile = zeile.substring(zeile.indexOf("aktuelle Zeit: ") + 15, zeile.indexOf("; Status:"));
                    //... und Rückgabe anhängen
                    ret += zeile.substring(0, zeile.indexOf(", Strafen:")) + "|";
                    //Strafen ermitteln...
                    zeile = zeile.substring(zeile.indexOf("Strafen: ") + 9);
                    //... und anhängen
                    ret += zeile.substring(0, zeile.indexOf("Sekunden") - 1) + "|";
                    //Gesamtzeit anhängen
                    zeile = zeile.substring(zeile.indexOf("Gesamtzeit: ") + 12);
                    ret += zeile;
                }
            }
        }
        //Rückgabe der Werte
        return ret;
    }

    /**
     * Trägt die Zahl des aktuellen Laufs in die Tabelle "allgemein" der DB ein.
     * Dafür muss dort der Schlüssel "lauf" initialisiert sein.
     *
     * @param lauf Zahl des aktuellen laufs in Computerzählweise
     */
    public void laufEintragen(int lauf) {
        //ggf. Verbindung zur DB herstellen
        conn = getInstance();
        //wenn Verbindung besteht...
        if (conn != null) {
            // ... Anfrage-Statement erzeugen.
            Statement query;
            try {
                //Statement Verbindung zuweisen
                query = conn.createStatement();
                // SQL-Befehl zusammensetzen...
                String sql = "UPDATE `allgemein` SET `Wert` = '" + lauf + "' WHERE `allgemein`.`Attribut` = 'lauf';";
                //... und mittels execute ausführen
                boolean result = query.execute(sql);
            } catch (SQLException e) {
                //SQL-Fehler
                showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.", false);
            }
        }
    }

    /**
     * Trägt ein, welcher Lauf für welche Startnummer durch den
     * Wiederholungslauf ersetzt werden soll.
     *
     * @param lauf Zahl des aktuellen Laufs in Computerzählweise
     * @param startnummer Startnummer
     */
    public void ersetzenEintragen(String lauf, String startnummer) {
        //ggf. Verbindung zur DB herstellen
        conn = getInstance();
        //wenn Verbindung besteht...
        if (conn != null) {
            // ... Anfrage-Statement erzeugen.
            Statement query;
            try {
                //Statement Verbindung zuweisen
                query = conn.createStatement();
                // SQL-Befehl zusammensetzen...
                String sql = "UPDATE `namen` SET `Wiederholung_ersetzt` = '" + lauf + "' WHERE `namen`.`Startnummer` = '" + startnummer + "';\n";
                System.out.println(sql);
                //... und mittels execute ausführen
                boolean result = query.execute(sql);
                sql = "UPDATE `auswertung` SET `Wiederholung_ersetzt` = '" + lauf + "' WHERE `auswertung`.`Startnummer` = '" + startnummer + "';";
                query.execute(sql);
            } catch (SQLException e) {
                //SQL-Fehler
                showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.", false);
            }
        }
    }

    /**
     * Gibt ein Array zurück, das die Werte der Starter enthält. Die
     * Wiederholungsläufe werden dabei berücksichtigt.
     *
     * @param starter Zahl der Starter
     * @return Array mit den Werten. Erster Index: laufende Nummer. Zweiter
     * Index: 0 für Startnummer, 1 für Name, 2 für Kategorie, 3 für Lauf 1, 4
     * für Lauf 2
     */
    public String[][] getAuswertung(int starter, int tore) {
        String[][] ret = new String[starter][5];
        conn = getInstance();
        int i = 0;
        try {
            Statement query = conn.createStatement();
            // die Abfrage generieren...
            String sql = "SELECT * FROM `auswertung`";
            //System.out.println(sql);
            //und durchführen; dafür: Verwendung von executeQuery, was ein ResulSet mit den gefundenen Datenbankeinträgen zurück gibt
            ResultSet result = query.executeQuery(sql);
            //für jede Zeile der Ergebnisse...
            while (result.next()) {
                //... Startnummer...
                //String sn = result.getString("Startnummer"); 
                //int startnummer = Integer.parseInt(sn);
                //... und Strafzeit ermitteln ... 
                ret[i][1] = result.getString("Namen");
                ret[i][0] = result.getString("Startnummer");
                ret[i][2] = result.getString("Kategorie");
                if (result.getString("Wiederholung_ersetzt") == null) {
                    ret[i][3] = result.getString("Lauf_1");
                    ret[i][4] = result.getString("Lauf_2");
                } else {
                    if (result.getString("Wiederholung_ersetzt").equals("1")) {
                        ret[i][3] = result.getString("Lauf_Wiederholung");
                        ret[i][4] = result.getString("Lauf_2");
                    } else {
                        ret[i][4] = result.getString("Lauf_Wiederholung");
                        ret[i][3] = result.getString("Lauf_1");
                    }
                }
                //prüfen, ob Startzeit vorhanden für spez. Startnummer
                i++;
            }

        } catch (SQLException e) {
            //SQL-Fehler nicht zeigen, da ewig viele Fenster entstehen
            //showExceptionDialog(e, "Fehler", "Zugriff auf Datenbank fehlgeschlagen", "Der Zugriff auf die Datenbank ist bei einer Abfrage fehlgeschlagen.");
        }
        return ret;
    }
    /**
     * Sortiert die Startnummernwerte nach Größe.
     * @param items eine Liste aus der Anzeige
     * @return sortierte Liste
     */
    private ObservableList<String> werteSortieren(ObservableList<String> items) {
        //Prüfen, ob Elemente da sind
        if (items.size()>1) {
            //Sortieren der Liste mit Bubblesort
            for (int i = 0; i < items.size(); i++) {
                for (int j = 0; j < items.size() - 1; j++) {
                    String wert1 = items.get(j);
                    String wert2 = items.get(j + 1);
                    Integer sn1 = new Integer(wert1.substring(wert1.indexOf("Startnummer: ") + 13, wert1.indexOf("aktuelle Zeit:") - 1));
                    Integer sn2 = new Integer(wert2.substring(wert2.indexOf("Startnummer: ") + 13, wert2.indexOf("aktuelle Zeit:") - 1));
                    if (sn1 > sn2) {
                        items.set(j, wert2);
                        items.set(j+1, wert1);
                    }
                }
            }
        }
        return items;
    }
}
