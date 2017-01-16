/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android_connector;

import static android_connector.Android_Connector.db;
import static android_connector.Android_Connector.host;
import static android_connector.Android_Connector.port;
import static android_connector.Android_Connector.pw;
import static android_connector.Android_Connector.user;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javax.swing.JOptionPane;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * FXML Controller class
 *
 * @author Eric
 */
public class ConfigWindowController implements Initializable {

    Android_Connector andr;
    /**
     * Tabellenspalte, die alle Startnummern speichert
     */
    @FXML
    public TableColumn startnummer;
    /**
     * Tabellenspalte, die alle Namen speichert
     */
    @FXML
    public TableColumn name;
    /**
     * Tabellenspalte, die alle Kategorien enthält. Aktuell funktionslos.
     */
    @FXML
    public TableColumn kategorie;
    /**
     * Tabelle auf dem Screen
     */
    @FXML
    public TableView<Person> tabelle;
    /**
     * Entält alle Starter, die von der Klasse Person repräsentiert werden.
     */
    public ObservableList<Person> personData;
    /**
     * Fügt der tabelle eine neue Zeile hinzu.
     */
    @FXML
    public Button neueZeile;
    /**
     * Entfernt eine Tabellenzeile.
     */
    @FXML
    public Button zeileEntfernen;
    /**
     * Füllt die Spalte startnummern mit fortlaufenden Startnummern.
     */
    @FXML
    private Button startnummernGenerieren;
    /**
     * Zahl der Messstationen. Hier kann nur eine Zahl eingegeben werden.
     */
    @FXML
    public TextField messstationen;
    /**
     * Zahl der Messtore. Hier kann nur eine Zahl eingegeben werden.
     */
    @FXML
    public TextField messtore;
    /**
     * Port zur Anfrage an Datenbank. Standardmäßig 3306. Hier kann nur eine
     * Zahl eingegeben werden.
     */
    @FXML
    public TextField port;
    /**
     * Hostname der Datenbank. Eigentlich immer localhost.
     */
    @FXML
    public TextField host;
    /**
     * Benutzername der Datenbank. Auf Windows standardmäßig root
     */
    @FXML
    public TextField user;
    /**
     * Passwort für Anmeldung. Auf Windows standardmäßig leer
     */
    @FXML
    public PasswordField pw;
    /**
     * Datenbankname. Standardmäßig android_connect.
     */
    @FXML
    public TextField db;
    /**
     * isSelected ist true, wenn (später) die Netzwerkvrbindung vor Start
     * getestet werden soll, sonst false.
     */
    @FXML
    public CheckBox checkNetwork;
    /**
     * Zahl der Starter.
     */
    public int starter = 0;
    /**
     * isSelected ist true, wenn später Protokolle gespeichert werden sollen,
     * sonst false
     */
    @FXML
    public CheckBox protokoll;
    /**
     * Information an User, hier den Speicherort einzugeben
     */
    @FXML
    private Label info_Ort;
    /**
     * Pfad des Ordners, in dem die Protokolle landen sollen
     */
    @FXML
    public TextField pfad;
    /**
     * Ruft DirectoryChooser auf, um Pfad zu gewünschtem Vezeichnis zu ermitteln
     */
    @FXML
    private Button pfadSuche;
    /**
     * Schließt das aktuelle Fenster
     */
    @FXML
    private Button schliessen;
    /**
     * True, wenn die Datanbank automatisch geleert werden soll, sonst false.
     */
    @FXML
    public CheckBox db_leeren;
    /**
     * Button, der einen DirectoryChooser anzeigt, in welchem man den
     * Speicherort von XAMPP auswählt.
     */
    @FXML
    private Button xampp_suchen;
    /**
     * Hier wird der Pfad zu XAMPP eingegeben oder vom DirectoryChooser aus
     * eingetragen.
     */
    @FXML
    public TextField pfad_xampp;
    /**
     * Liest eine Excel-Datei ein.
     */
    @FXML
    private Button excelReader;
    /**
     * Schließt das Fenster ohne Aufruf der weiteren Fuktionalitäten.
     */
    @FXML
    private Button schliessenButton;
    /**
     * Stellt bei Klick einen alten Zustand wieder her.
     */
    @FXML
    private Button restoreButton;

    /**
     * Stellt einen alten Zustand wieder her.
     *
     * @param oldValues Instanz dieser Klasse, die zur Wiederherstellung
     * verwendet wird
     */
    public void reinit(ConfigWindowController oldValues) {
        //alle wichtigen Werte aus der alten Instanz nehmen und in den Entsprechungen der neuen Instanz speichern
        //Übernahme der Werte über Getter und Setter, da per Wertzuweisung nicht funktioniert
        this.checkNetwork.setSelected(oldValues.checkNetwork.isSelected());
        this.db.setText(oldValues.db.getText());
        //Datenbank muss geleert werden, da sonst alte Werte mit hereinpfuschen
        this.host.setText(oldValues.host.getText());
        this.messstationen.setText(oldValues.messstationen.getText());
        this.messtore.setText(oldValues.messtore.getText());
        this.name.setText(oldValues.name.getText());
        this.personData = oldValues.personData;
        this.pfad.setText(oldValues.pfad.getText());
        this.pfad_xampp.setText(oldValues.pfad_xampp.getText());
        this.port.setText(oldValues.port.getText());
        this.primaryStage = oldValues.primaryStage;
        this.protokoll.setSelected(oldValues.protokoll.isSelected());
        if (oldValues.protokoll.isSelected()) {
            //Einblenden der Oberflächenelemente
            info_Ort.setVisible(true);
            pfad.setVisible(true);
            pfadSuche.setVisible(true);
        }
        this.pw.setText(oldValues.pw.getText());
        this.starter = oldValues.starter;
        this.user.setText(oldValues.user.getText());
        this.andr = oldValues.andr;
        this.kategorien = oldValues.kategorien;
        //Zurücksetzen der DB
        (verbinder = new MySQLConnection(host.getText(), port.getText(), db.getText(), user.getText(), pw.getText(), null)).reset(false);
        //Tabelle ihre Werte zuweisen...
        tabelle.setItems(personData);
        //... und sie (hart) aktualisieren
        refresh_table(tabelle);
        alteWerteÜbernommen = true;
    }
    /**
     * Speichert, ob die Initialisierung bereits durch die Wertübernehme von
     * einer alten Instanz geregelt wurde, dann true, sonst false.
     */
    private boolean alteWerteÜbernommen = false;

    /**
     * Wird bei der Initialisierung des Fensters aufgerufen.
     *
     * @param url Ort der Controller-Datei
     * @param rb Ressourcen der Datei
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //neue Liste von Startern anlegen, falls alte Liste noch nicht gelesen wurde
        if (!alteWerteÜbernommen) {
            personData = FXCollections.observableArrayList(); // TODO
        }
        //Spalten der Tabelle zuweisen, welches Attribut der Speicherklasse Person sie auslesen sollen
        startnummer.setCellValueFactory(new PropertyValueFactory<>("Startnummer"));
        name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        kategorie.setCellValueFactory(new PropertyValueFactory<>("Kategorie"));
        //Standardperson den Startern hinzufügen...
        //personData.add(e);
        //... und ihre Zahl erhöhen
        //starter++;
        //Tabelle die Items zuweisen --> Darstellung
        tabelle.setItems(personData);
        //Quelle: http://www.naturalborncoder.com/java/javafx/2012/04/26/complete-javafx-2-editable-table-example/ wurde im folgenden übernommen
        //Ziel: Tabelle, in die man eingeben kann
        //bei Enter oder Doppelklick auf Tabellenzelle: Anlegen eines neuen Textfeldes, in das eingegeben werden kann und das über der Zelle liegt
        Callback<TableColumn, TableCell> cellFactory = new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn p) {
                return new EditingCell();
            }
        };
        //diese Aktion wird allen Spalten zugewiesen
        startnummer.setCellFactory(cellFactory);
        //bei Enter in der Eingabe: Übernahme des eingegebenen Wertes in die Tabellenzelle
        startnummer.setOnEditCommit(new EventHandler<CellEditEvent<Person, String>>() {
            @Override
            public void handle(CellEditEvent<Person, String> t) {
                ((Person) t.getTableView().getItems().get(t.getTablePosition().getRow())).setStartnummer(t.getNewValue());
            }
        });
        //Übernahme von Namen...
        name.setCellFactory(cellFactory);
        name.setOnEditCommit(new EventHandler<CellEditEvent<Person, String>>() {
            @Override
            public void handle(CellEditEvent<Person, String> t) {
                ((Person) t.getTableView().getItems().get(t.getTablePosition().getRow())).setName(t.getNewValue());
            }
        });
        //... und Kategorie
        kategorie.setCellFactory(cellFactory);
        kategorie.setOnEditCommit(new EventHandler<CellEditEvent<Person, String>>() {
            @Override
            public void handle(CellEditEvent<Person, String> t) {
                ((Person) t.getTableView().getItems().get(t.getTablePosition().getRow())).setKategorie(t.getNewValue());
            }
        });
    }

    /**
     * Aktion, die bei Klick auf den Button "neue Zeile" ausgeführt wird: Zeile
     * der Tabelle hinzufügen
     *
     * @param event der Klick
     */
    @FXML
    private void neueZeile(ActionEvent event) {
        //neuer Starter hinzugefügt
        starter++;
        //markierte Zeile herausfinden --> danach Zeile einfügen durch Anlegen einer leeren Person
        Person selected = tabelle.getSelectionModel().getSelectedItem();
        if (selected != null) {
            //nach markierten Person Zeile einfügen
            personData.add(personData.indexOf(selected) + 1, new Person());
        } else {
            //standardmäßig am Ende Zeile anfügen
            personData.add(new Person());
        }
        //geänderte Daten der Tabelle zuweisen
        tabelle.setItems(personData);
    }

    /**
     * Aktion, die bei Klick auf den Button "Zeile entfernen" ausgeführt wird:
     * eine Zeile der Tabelle entfernen.
     *
     * @param event der Klick
     */
    @FXML
    private void zeile_Entfernen(ActionEvent event) {
        //Zahl der Starter senken
        starter--;
        //gewählte Person ermitteln (entspricht gewählter Zeile)
        Person selected = tabelle.getSelectionModel().getSelectedItem();
        if (selected != null) {
            //wenn vorhanden: diese entfernen
            personData.remove(selected);
        } else {
            //sonst: letzte Zeile entfernen
            personData.remove(personData.size() - 1);
        }
        //Änderungen speichern
        tabelle.setItems(personData);
    }

    /**
     * Weist allen Startern automatisch nach Reihenfolge eine Startnummer zu.
     *
     * @param event der Klick auf den Button
     */
    @FXML
    private void startnummern_Generieren(ActionEvent event) {
        //alle Starter durchgehen, jedem als Startnummer seine Zeilennummer zuweisen
        for (int i = 0; i < personData.size(); i++) {
            personData.get(i).setStartnummer("" + (i + 1));
        }
        //Änderungen speichern...
        tabelle.setItems(personData);
        //... und die Tabelle aktualisieren
        refresh_table(tabelle);
    }

    /**
     * Aktualisiert die Anzeige einer Tabelle durch aus- und wiedereinblenden
     * aller Zellen. Quelle:
     * http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
     *
     * @param table die zu aktualisierende Tabelle
     */
    public static void refresh_table(TableView table) {
        for (int i = 0; i < table.getColumns().size(); i++) {
            ((TableColumn) (table.getColumns().get(i))).setVisible(false);
            ((TableColumn) (table.getColumns().get(i))).setVisible(true);
        }
    }

    /**
     * Liest die Werte der Tabelle aus und gibt diese als String-Array zurück.
     *
     * @return Array, das die Tabelle repräsentiert. Erster Index ist die
     * Zeilennummer. Zweiter Index ist dabei die Spaltennummer. Dabei steht 0
     * für die Startnummer und 1 für den Namen.
     * @param kategorie Kategorie, deren Starter ermittelt werden sollen
     */
    String[][] getWerte(String kategorie) {
        //zur Sicherheit: Werte aus der Tabelle erneut auslesen
        personData = tabelle.getItems();
        int starter_kategorie = 0;
        for (int i = 0; i < personData.size(); i++) {
            if (personData.get(i).getKategorie().equals(kategorie)) {
                starter_kategorie++;
            }
        }
        //neues Array anlegen, das die Werte später speichert
        String[][] werte = new String[starter_kategorie][2];
        //Tabelleninhalte durchgehen und in das Array einordnen wie oben beschrieben, solange sie zur gewünschten Kategorie gehören
        int zaehler = 0;
        for (int i = 0; i < personData.size(); i++) {
            if (personData.get(i).getKategorie().equals(kategorie)) {
                werte[zaehler][0] = personData.get(i).getStartnummer();
                werte[zaehler][1] = personData.get(i).getName();
                zaehler++;
            }
        }
        //Rückgabe aller Werte
        return werte;
    }

    /**
     * Liest die Werte der Tabelle aus und gibt diese als String-Array zurück.
     *
     * @return Array, das die Tabelle repräsentiert. Erster Index ist die
     * Zeilennummer. Zweiter Index ist dabei die Spaltennummer. Dabei steht 0
     * für die Startnummer und 1 für den Namen. 2 ist die Kategorie.
     */
    String[][] getAllWerte() {
        //zur Sicherheit: Werte aus der Tabelle erneut auslesen
        personData = tabelle.getItems();
        int starter_kategorie = 0;
        for (int i = 0; i < personData.size(); i++) {
            starter_kategorie++;
        }
        //neues Array anlegen, das die Werte später speichert
        String[][] werte = new String[starter_kategorie][3];
        //Tabelleninhalte durchgehen und in das Array einordnen wie oben beschrieben, solange sie zur gewünschten Kategorie gehören
        int zaehler = 0;
        for (int i = 0; i < personData.size(); i++) {
            werte[zaehler][0] = personData.get(i).getStartnummer();
            werte[zaehler][1] = personData.get(i).getName();
            werte[zaehler][2] = personData.get(i).getKategorie();
            zaehler++;
        }
        //Rückgabe aller Werte
        return werte;
    }

    /**
     * Aktion, die bei Klick auf die Checkbox, die die Protokollspeicherung
     * abfragt, ausgeführt wird.
     *
     * @param event der Klick
     */
    @FXML
    private void pfadeingabe(ActionEvent event) {
        //Label mit Anweisung, TextField zur Pfadeingabe und Button zur Pfadauswahl einblenden, wenn die Checkbox ausgewählt ist, sonst ausblenden
        if (protokoll.isSelected()) {
            info_Ort.setVisible(true);
            pfad.setVisible(true);
            pfadSuche.setVisible(true);
        } else {
            info_Ort.setVisible(false);
            pfad.setVisible(false);
            pfadSuche.setVisible(false);
        }
    }
    /**
     * Fenster, in dem die GUI angezeigt wird. Wird vom Controller gesetzt.
     */
    public Stage primaryStage;

    /**
     * Aktion, die bei Klick auf Button "Durchsuchen" aufgerufen wird: öffnen
     * eines DirectoryChoosers zur Auswahl eines Ortes zur Protokollspeicherung.
     *
     * @param event der Klick
     * @author http://stackoverflow.com/questions/9375938/javafx-filechooser
     */
    @FXML
    private void durchsuchen(ActionEvent event) {
        //DirectoryChooser anlegen, der nur Ordner auswählt
        DirectoryChooser chooser = new DirectoryChooser();
        //Titel und Startverzeichnis setzen
        chooser.setTitle("Speicherort für die Protokolldateien: ");
        File defaultDirectory = new File(System.getProperty("user.home"));
        chooser.setInitialDirectory(defaultDirectory);
        //Abfrage durchführen, die einen Ordner liefert. Muss an das Fenster, das ihn gestartet hat, angeheftet sein
        File selectedDirectory = chooser.showDialog(primaryStage);
        if (selectedDirectory != null) {
            //Pfad im Textfeld anzeigen, wenn Verzeichnis gewählt
            pfad.setText(selectedDirectory.getAbsolutePath());
        }
    }

    /**
     * Klick auf Button löst die Aktion aus, die beim Schließen des Fensters
     * abläuft.
     *
     * @author Quelle:
     * http://stackoverflow.com/questions/24483686/how-to-force-javafx-application-close-request-programmatically
     * @param event Klick auf den Button
     */
    @FXML
    private void close(ActionEvent event) {
        primaryStage.fireEvent(
                new WindowEvent(
                        primaryStage,
                        WindowEvent.WINDOW_CLOSE_REQUEST
                )
        );
    }
    /**
     * Temporäre MySQLConnection ohne großen Sinn.
     */
    MySQLConnection verbinder;
    /**
     * Speichert den Identifizierer des aktuellen Betriebssystems.
     */
    private static final String OS = System.getProperty("os.name").toLowerCase();

    /**
     * Startet XAMPP als Kommandozeilenanwendung. Nur unter Windows möglich.
     * Wenn nicht Windows, dann Fehlermeldung.
     *
     * @author Quelle:
     * http://www.textpattern.net/wiki/?title=Using_XAMPP_(Apache-MySQL-PHP-Perl)_for_Windows
     * @param pfad Der Pfad zum XAMPP-Verzeichnis
     */
    public void xampp_start(String pfad) {
        //Aufruf zusammennsetzen: Pfad und Startdatei
        //vorher prüfen, ob Windows vorliegt --> geht sonst nicht so
        if (OS.contains("win")) {
            String command = "\"" + pfad + "\\xampp_start.exe" + "\"";
            xampp = pfad;
            try {
                //Prozess anlegen, der die exe startet
                Process p = Runtime.getRuntime().exec(command);
                //InputStream zum Einlesen der Eingabe
                InputStream is = p.getInputStream();
                int c;
                while ((c = is.read()) != -1) {
                    //Kontrollausgabe der Konsolenantwort
                    System.out.print((char) c);
                }
                //Fehler fangen
            } catch (IOException ex) {
                //schöne Fehlermeldung ausgeben
                verbinder = new MySQLConnection(new FXMLDocumentController());
                verbinder.showExceptionDialog(ex, "Fehler", "XAMPP konnte aufgrund eines Fehlers nicht gestartet werden.",
                        "Bitte geben Sie bei einer Beschwerde an den Entwickler folgende Fehlermeldung an:", false);

            }
            //Nicht Windows --> Abbruch, da Konsolenaufruf so nur bei Windows funktioniert
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Bitte XAMPP starten!");
            alert.setHeaderText("Automatischer Start von XAMPP nicht möglich, bitte manuell starten!");
            alert.setContentText("Ein automatischer Start von XAMPP ist nur unter Windows möglich. Bitte starten Sie XAMPP manuell!");
            alert.showAndWait();
        }
    }
    /**
     * Speicherordner der XAMPP-Installation.
     */
    static String xampp = "";

    /**
     * Kopiert einen Ordner mit allen Dateien und Unterordnern in einen anderen
     * Ordner. Existierende Dateien werden überschrieben.
     *
     * @author Quelle:
     * http://bukkitfaq.de/forum/index.php?thread/977-ordner-kopieren-in-java/
     * @param from zu kopierender Ordner
     * @param to Zielordner
     * @throws IOException beim Auftreten irgend welcher Schreibfehler oder
     * Lesefehler
     */
    private void copyFilesInDirectory(File from, File to) throws IOException {
        //prüfen, ob Zielverzeichnis existiert --> wenn nicht, dann anlegen
        if (!to.exists()) {
            to.mkdirs();
        }
        //alle untergeordneten Elemente des Quellordners durchgehen...
        for (File file : from.listFiles()) {
            //... handelt es sich um einen Ordner, dann rekursiver Aufruf der Prozedur, um ihn zu kopieren
            if (file.isDirectory()) {
                copyFilesInDirectory(file, new File(to.getAbsolutePath() + file.getName()));
                //sonst Datei an Ziel kopieren, automatisches Überschreiben
            } else {
                File n = new File(to.getAbsolutePath() + "/" + file.getName());
                Files.copy(file.toPath(), n.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    /**
     * Kopiert eine einzelne Datei in einen Ordner. Überschreibt existierende
     * Dateien.
     *
     * @author Quelle:
     * http://bukkitfaq.de/forum/index.php?thread/977-ordner-kopieren-in-java/
     * @param file die zu kopierende Datei
     * @param to Zielordner
     * @throws IOException bei Schreibproblemen
     *
     */
    private void copyFileToDirectory(File file, File to) throws IOException {
        //Zielordner ggf. anlegen
        if (!to.exists()) {
            to.mkdirs();
        }
        //Kopieren der Datei
        File n = new File(to.getAbsolutePath() + "/" + file.getName());
        Files.copy(file.toPath(), n.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Überschreibt die PHP-Konfigurationsdatei mit den tatsächlichen
     * Verbindungswerten des Servers. Kopiert außerdem alle für den Betrieb
     * nötigen Scripte in den htdocs-Unterordner.
     *
     * @param pfad Pfad zur XAMPP-Installation bzw. Ordner public_html (Linux)
     */
    public void overridePHPConfig(String pfad) {
        //Schreiber initialisieren
        FileWriter fw = null;
        try {
            String res = ConfigWindowController.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            res = res.substring(1, res.length()-15);
            System.out.println("Pfad: "+res);
            //Scripte liegen unter src in JAR --> Kopieren dieser mit Methode.
            copyFilesInDirectory(new File(res+"lib/AndroidConnectorAppHTTPScripts"), new File(pfad + "/htdocs/AndroidConnectorAppHTTPScripts"));
            /**
             * Enthält den Text der PHP-Konfigurationsdatei unter Verwendung der
             * in diesem Fenster eingegebenen Konfigurationswerte.
             */
            String file = "<?php\n"
                    + "//Konfiguration.php (diese Datei) enthält die Konfiguration der MySQL-Verbindung.\n"
                    + "//Wenn Sie einen der folgenden Werte verändert haben, müssen Sie diesen hier anpassen, damit die App ordnungsgemäß ausgeführt werden kann!\n"
                    + "\n"
                    + "//Der Hostname. Lassen Sie ihn unverändert, die zugehörige IP-Adresse wird automatisch ermittelt!\n"
                    + "$host = \"" + host.getText() + "\";\n"
                    + "\n"
                    + "//Der Benutzername für die MySQL-Datenbank. Standardmäßig \"root\".\n"
                    + "$user = \"" + user.getText() + "\";\n"
                    + "\n"
                    + "//Das Passwort für eben jene Datenbank. Unter Windows standardmäßig leer.\n"
                    + "$passwort = \"" + pw.getText() + "\";\n"
                    + "\n"
                    + "//Der von Ihnen vergebene Name für die Datenbank. Standardmäßig \"android_connect\";\n"
                    + "$datenbank = \"" + db.getText() + "\";\n"
                    + "?>";
            //FileWriter anlegen, der die Datei dann schreibt
            fw = new FileWriter(pfad + "\\htdocs\\AndroidConnectorAppHTTPScripts\\Konfiguration.php");
            //schreiben und beenden
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(file);
            bw.close();
            //Fehler fangen und Exception ausgeben
        } catch (IOException ex) {
            verbinder = new MySQLConnection(new FXMLDocumentController());
            verbinder.showExceptionDialog(ex, "Fehler", "Die PHP-Konfigurationsdatei konnte nicht überschrieben werden!",
                    "Die Konfigurationsdatei für den internen PHP-Verbindungsaufbau unter \"" + pfad + "\\htdocs\\AndroidConnectorAppHTTPScripts\\Konfiguration.php" + "\" konnte nicht überschrieben werden."
                    + "Bitte ändern Sie ggf. die Konfiguration mit einem Texteditor!", false);
        } catch (URISyntaxException ex) {
            Logger.getLogger(ConfigWindowController.class.getName()).log(Level.SEVERE, null, ex);
        } //sauber beenden
        catch(NullPointerException e){
            
        }
        finally {
            try {
                if(fw!=null)
                fw.close();
            } catch (IOException ex) {
                verbinder = new MySQLConnection(new FXMLDocumentController());
                verbinder.showExceptionDialog(ex, "Fehler", "Internes Schreibproblem!",
                        "Bitte die Werte der PHP-Konfigurationsdatei unter \"" + pfad + "\\htdocs\\AndroidConnectorAppHTTPScripts\\Konfiguration.php" + "\" überprüfen."
                        + "Ändern Sie ggf. die Konfiguration mit einem Texteditor!", false);
            }
        }
    }

    /**
     * Stoppt XAMPP als Kommandozeilenanwendung. Nur unter Windows möglich. Wenn
     * nicht Windows, dann Fehlermeldung.
     *
     * @author Quelle:
     * http://www.textpattern.net/wiki/?title=Using_XAMPP_(Apache-MySQL-PHP-Perl)_for_Windows
     * @param pfad Der Pfad zum XAMPP-Verzeichnis
     */
    public static void xampp_stopp(String pfad) {
        //Aufruf zusammennsetzen: Pfad und Startdatei
        //vorher prüfen, ob Windows vorliegt --> geht sonst nicht so
        if (OS.contains("win")) {
            String command = "\"" + pfad + "\\xampp_stop.exe" + "\"";
            try {
                //Prozess anlegen, der die exe startet
                Process p = Runtime.getRuntime().exec(command);
                //InputStream zum Einlesen der Eingabe
                InputStream is = p.getInputStream();
                int c;
                while ((c = is.read()) != -1) {
                    //Kontrollausgabe
                    System.out.print((char) c);
                }
                //Fehler fangen
            } catch (IOException ex) {
                (new MySQLConnection(new FXMLDocumentController())).showExceptionDialog(ex, "Fehler", "XAMPP konnte aufgrund eines Fehlers nicht gestoppt werden.",
                        "Bitte geben Sie bei einer Beschwerde an den Entwickler folgende Fehlermeldung an:", false);

            }
            //nicht Windows --> Fehler
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Bitte XAMPP beenden!");
            alert.setHeaderText("Automatisches Ende von XAMPP nicht möglich, bitte manuell starten!");
            alert.setContentText("Ein automatisches Beenden von XAMPP ist nur unter Windows möglich. Bitte stoppen Sie XAMPP manuell!");
            alert.showAndWait();
        }
    }

    /**
     * Öffnet einen DirectoryChooser, der dann den Installationsordner von XAMPP
     * erfragt.
     *
     * @param event
     */
    @FXML
    private void such_xampp(ActionEvent event) {
        //DirectoryChooser anlegen, der nur Ordner auswählt
        DirectoryChooser chooser = new DirectoryChooser();
        //Titel und Startverzeichnis setzen
        chooser.setTitle("Installationsordner von XAMPP: ");
        File defaultDirectory = new File(System.getProperty("user.home"));
        chooser.setInitialDirectory(defaultDirectory);
        //Abfrage durchführen, die einen Ordner liefert. Muss an das Fenster, das ihn gestartet hat, angeheftet sein
        File selectedDirectory = chooser.showDialog(primaryStage);
        if (selectedDirectory != null) {
            //Pfad im Textfeld anzeigen, wenn Verzeichnis gewählt
            pfad_xampp.setText(selectedDirectory.getAbsolutePath());
        }
    }

    /**
     * Liest eine Excel-Datei ein und entnimmt dieser die Werte für Startnummer,
     * Name und Kategorie. Fügt die Werte anschließend der Tabelle hinzu.
     *
     * @param event Klick auf Button, der die Methode aufruft
     */
    @FXML
    private void readExcel(ActionEvent event) {
        //FileChooser einsetzen, um Starterplan einzulesen
        FileChooser fc = new FileChooser();
        //standardmäßig im Home-Verzeichnis starten
        fc.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        //Alle-Dateien-Filter entfernen
        fc.setSelectedExtensionFilter(null);
        //FileFilter für Exceldateien hinzufügen
        //nur "alte" Excel-Dateien können gelesen werden!
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Microsoft Excel 1997-2003 Dokument (.xls)", "*.xls")
        );
        //Dateien einlesen
        File returnVal = fc.showOpenDialog(primaryStage);
        //prüfen, ob Datei zurückgegeben --> eine gewählt; muss aber nicht existieren
        if (returnVal != null) {
            //ExcelReader anlegen, der Datei dann in den Speicher vergewaltigt.
            ExcelReader reader = null;
            /**
             * Speichert, ob es beim Laden Probleme gab. True, wenn alles gut
             * ist, sonst false.
             */
            boolean geladen = true;
            //Versuch, Datei zu laden
            try {
                reader = new ExcelReader(returnVal.getAbsolutePath());
            } catch (IOException ex) {
                //Exception fangen, die durch Ladefehler entsteht, und diese mit ExceptionDialog anzeigen
                new MySQLConnection(null).showExceptionDialog(ex, "Lesefehler", "Datei konnte nicht gelesen werden.", "Die Datei " + returnVal.getAbsolutePath()
                        + " konnte nicht gelesen werden. Bitte geben Sie bei einer Beschwerde an den Entwickler folgende Fehlermeldung an: ", false);
                //Fehler aufgetreten
                geladen = false;
            }
            //prüfen, ob Fehler
            if (geladen) {
                //Datei geladen --> ExcelReader nicht null, kann referiert werden
                Workbook wb = reader.getWorkBook();
                //Datei hat nur 1 Tabelle --> direkt die nehmen
                if (wb.getNumberOfSheets() == 1) {
                    reader.setSheet(0);
                } else {
                    //mehrere Tabellen --> User muss wählen, welche er will
                    List<String> items = new ArrayList<>();
                    //Nahmen aller Tabellen in Liste speichern
                    for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                        items.add(wb.getSheetName(i));
                    }
                    //ChoiceDialog anzeigen, der die Auswahl ermöglicht
                    ChoiceDialog<String> dialog = new ChoiceDialog<>(items.get(0), items);
                    dialog.setTitle("Tabelle auswählen");
                    dialog.setHeaderText("Bitte Tabelle auswählen!");
                    dialog.setContentText("In der übergebenen Datei wurden " + wb.getNumberOfSheets() + " Tabellen gefunden. Bitte wählen Sie die gewünschte Tabelle aus!");

                    Optional<String> result = dialog.showAndWait();
                    // The Java 8 way to get the response value (with lambda expression).
                    /**
                     * Finale Kopie des readers, der von Lambda adressiert
                     * werden kann.
                     */
                    final ExcelReader temp = reader;
                    result.ifPresent(letter -> {
                        //Tabelle laden, die gewählt wurde.
                        temp.setSheet(wb.getSheet(letter));
                    });
                    //Änderungen übernehmen
                    reader = temp;
                }
                //erste drei Spalten durchgehen, erste Zeile überspringen (Beschriftung), danach erste Spalte als Startnummer, zweite als Nahme, dritte als Kategorie interpretieren
                for (int i = 1; i < reader.getSheet().getLastRowNum() + 1; i++) {
                    //Person-Objekt bilden, das Werte der Person in den Zeilen enthält
                    //Startnummern werden als Doubles gelesen, Umwandlung in int
                    Person e = new Person((new Double(reader.getCellValueAt(i, 0))).intValue() + "", reader.getCellValueAt(i, 1), reader.getCellValueAt(i, 2));
                    //Starter den Personen hinzufügen ...
                    personData.add(e);
                    //... und ihre Zahl erhöhen
                    starter++;
                }
                //geänderte Starter in Tabelle übernehmen
                tabelle.setItems(personData);
            }
        }
    }

    /**
     * Aktion beim Klick auf den mit "Abbrechen" beschrifteten Button. Schließt
     * das Fenster einfach.
     *
     * @param event der Klick
     */
    @FXML
    private void abort(ActionEvent event) {
        //Hauptklasse alle Konfigurationswerte übergeben, um die DB in jedem Fall leeren zu können
        xampp = pfad_xampp.getText();
        xampp_start(xampp);
        Android_Connector.host = host.getText();
        Android_Connector.port = port.getText();
        Android_Connector.db = db.getText();
        Android_Connector.user = user.getText();
        Android_Connector.pw = pw.getText();
        primaryStage.close();
    }

    /**
     * Ermittelt alle in der Tabelle angegebenen Kategorien. Dabei werden
     * gleiche Angaben jeweils als Kategorie gelesen.
     *
     * @return List of String, welche die Namen aller Kategorien enthält
     */
    public List<String> getKategorien() {
        //Durchgehen aller Starter, hinzufügen zur Liste, Rückgabe der Liste
        List<String> ret = new ArrayList<>();
        for (Person e : personData) {
            if (!ret.contains(e.getKategorie())) {
                ret.add(e.getKategorie());
            }
        }
        return ret;
    }
    List<String> kategorien = new ArrayList<>();

    /**
     * Gibt die ausgewählte Kategorie zurück, die jetzt starten soll
     *
     * @return gewählte Kategorie, wenn noch mehrere Kategorien verfügbar sind,
     * sonst die erste Kategorie, wenn noch eine da ist, sonst null
     */
    public String getSelectedKategorie() {
        //Ermitteln der Kategorien beim ersten Fensteraufruf --> danach nicht mehr, um keinen Doppelstart zu ermöglichen
        List<String> items = getKategorien();
        //Prüfen, ob das nicht der erste Aufruf des Fensters ist und keine Kategorien mehr offen sind
        if (kategorien.isEmpty() && alteWerteÜbernommen) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Veranstaltung beendet");
            alert.setHeaderText("Alle Läufe aller Kategorien sind durchgeführt worden.");
            alert.setContentText("Alle Läufe wurden von den eingeteilten Startern absolviert, die Werte sind gespeichert. Sie werden zur Auswertung umgeleitet!");
            alert.showAndWait();
            printResults();
            return null;
        }
        if (kategorien.isEmpty()) {
            kategorien = items;
        }
        //temporäre Kopie anlegen
        items = kategorien;
        String ret = null;

        //keine Kategorien mehr da --> Auswertung
        //hier prüfen, da später 1 Wert gelöscht wird
        //ChoiceDialog anzeigen, der die Auswahl ermöglicht wenn mahrere mögliche Kategorien noch nicht bearbeitet sind
        if (items.size() >= 1) {
            ChoiceDialog<String> dialog = new ChoiceDialog<>(items.get(0), items);
            dialog.setTitle("Kategorie?");
            dialog.setHeaderText("Bitte Kategorie auswählen!");
            dialog.setContentText("Bitte wählen Sie aus der folgenden Liste die Kategorie aus, die starten soll!");

            Optional<String> result = dialog.showAndWait();
            // The Java 8 way to get the response value (with lambda expression).
            final StringBuilder tmp = new StringBuilder();
            result.ifPresent(letter -> {
                //Tabelle laden, die gewählt wurde.
                tmp.append(letter);
            });
            ret = tmp.toString();
            //Entfernen der gewählten Kategorie --> kann nur einmal aufgerufen werden
            kategorien.remove(ret);
            //nur noch eine Kategorie übrig --> Auswahl dieser
        } else if (!items.isEmpty()) {
            ret = items.get(0);
            kategorien.remove(ret);
        }
        return ret;
    }

    /**
     * Speichert alle Werte, die nötig sind, um das Fenster nach einem Absturz
     * wiederherstellen zu können.
     *
     * @param pfad Pfad, in dem die Sicherungskopien landen
     * @param aktuelle_Kategorie Kategorie, die gerade gestartet wurde
     */
    public void writeConfig(String pfad, String aktuelle_Kategorie) {
        /**
         * Inhalt der Konfigurations-Datei. Diese speichert alle wichtigen
         * Konfigurationseinstellungen (dem String beim Anlegen bereits
         * übergeben), die Kategorien nebst aktuell gestarteter Kategorie und
         * alle Starter mit Startnummern und Kategorie.
         */
        String save = "Messstationen:" + messstationen.getText() + "\n"
                + "Messtore:" + messtore.getText() + "\n"
                + "Host:" + host.getText() + "\n"
                + "Port:" + port.getText() + "\n"
                + "DB:" + db.getText() + "\n"
                + "clearDB:" + db_leeren.isSelected() + "\n"
                + "User:" + user.getText() + "\n"
                + "Passwort:" + pw.getText() + "\n"
                + "xampp:" + pfad_xampp.getText() + "\n"
                + "checkNetwork:" + checkNetwork.isSelected() + "\n"
                + "Sicherung:" + protokoll.isSelected() + "\n"
                + "Protokollort:" + this.pfad.getText() + "\n"
                + "aktuelle Kategorie:" + aktuelle_Kategorie + "\n";
        //alle verbleibenden Kategorien durchgehen und anhängen
        for (String kategorie : kategorien) {
            save += "Kategorie:" + kategorie + "\n";
        }
        //alle Werte der Starter ermitteln und druch | getrennt anhängen
        String[][] tableValues = getAllWerte();
        for (String[] tableRow : tableValues) {
            save += "Starter:" + tableRow[0] + "|" + tableRow[1] + "|" + tableRow[2] + "\n";
        }
        //in Protokollordner speichern, wenn ein solcher spezifiziert
        if (pfad != null && !pfad.isEmpty()) {
            //Dateiname ist "Config"
            File file = new File(pfad + "\\Config.txt");
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
            //Schreiber der Datei
            PrintWriter pWriter = null;
            //Datei schreiben
            try {
                pWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));
                pWriter.println(save);
                //Fehler fangen, Fehlermeldungen ausgeben
            } catch (IOException ioe) {
                MySQLConnection.staticExceptionDialog(ioe, "Fehler", "Schreibfehler", "Die Datei konnte nicht geschrieben werden!");
            } //sauberes Beenden
            finally {
                if (pWriter != null) {
                    pWriter.flush();
                    pWriter.close();
                }
            }
        }
    }

    @FXML
    private void restore(ActionEvent event) {
        //DirectoryChooser anlegen, der nur Ordner auswählt
        DirectoryChooser chooser = new DirectoryChooser();
        //Titel und Startverzeichnis setzen
        chooser.setTitle("Speicherort der Protokolldateien: ");
        File defaultDirectory = new File(System.getProperty("user.home"));
        chooser.setInitialDirectory(defaultDirectory);
        //Abfrage durchführen, die einen Ordner liefert. Muss an das Fenster, das ihn gestartet hat, angeheftet sein
        File selectedDirectory = chooser.showDialog(primaryStage);
        String aktuelleKategorie = "";
        kategorien = new ArrayList<>();
        if (selectedDirectory != null) {
            //Pfad im Textfeld anzeigen, wenn Verzeichnis gewählt
            pfad.setText(selectedDirectory.getAbsolutePath());
            try {
                File file = new File(pfad.getText() + "/Config.txt");
                BufferedReader in = new BufferedReader(new FileReader(file));
                personData = FXCollections.observableArrayList(); // TODO
                String line = null;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("Messstationen:")) {
                        messstationen.setText(line.substring(14, line.length()));
                    }
                    if (line.startsWith("Messtore:")) {
                        messtore.setText(line.substring(9, line.length()));
                    }
                    if (line.startsWith("Host:")) {
                        host.setText(line.substring(5, line.length()));
                    }
                    if (line.startsWith("Port:")) {
                        port.setText(line.substring(5, line.length()));
                    }
                    if (line.startsWith("DB:")) {
                        db.setText(line.substring(3, line.length()));
                    }
                    if (line.startsWith("clearDB:")) {
                        db_leeren.setSelected(Boolean.parseBoolean(line.substring(8, line.length())));
                    }
                    if (line.startsWith("User:")) {
                        user.setText(line.substring(5, line.length()));
                    }
                    if (line.startsWith("Passwort:")) {
                        pw.setText(line.substring(9, line.length()));
                    }
                    if (line.startsWith("xampp:")) {
                        pfad_xampp.setText(line.substring(6, line.length()));
                    }
                    if (line.startsWith("checkNetwork:")) {
                        checkNetwork.setSelected(Boolean.parseBoolean(line.substring(13, line.length())));
                    }
                    if (line.startsWith("Sicherung:")) {
                        protokoll.setSelected(Boolean.parseBoolean(line.substring(10, line.length())));
                        info_Ort.setVisible(true);
                        pfad.setVisible(true);
                        pfadSuche.setVisible(true);
                    }
                    if (line.startsWith("Protokollort:")) {
                        pfad.setText(line.substring(13, line.length()));
                    }
                    if (line.startsWith("aktuelle Kategorie:")) {
                        aktuelleKategorie = line.substring(19, line.length());
                        kategorien.add(aktuelleKategorie);
                    }
                    if (line.startsWith("Kategorie:")) {
                        kategorien.add(line.substring(10, line.length()));
                    }
                    if (line.startsWith("Starter:")) {
                        line = line.substring(8, line.length());

                        //Standard-Person anlegen
                        String sn, name, kategorie;
                        sn = line.substring(0, line.indexOf("|"));
                        line = line.substring(line.indexOf("|") + 1);
                        name = line.substring(0, line.indexOf("|"));
                        line = line.substring(line.indexOf("|") + 1);
                        kategorie = line;
                        Person e = new Person(sn, name, kategorie);
                        //zuweisen, welches Attribut von Person die Spalten auslesen sollen
                        //Standardperson den Startern hinzufügen...
                        personData.add(e);
                        //... und ihre Zahl erhöhen
                        starter++;
                        tabelle.setItems(personData);
                        refresh_table(tabelle);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Speichert die Ergebnisse der Läufe als Excel-Tabelle.
     *
     * @author Quelle:
     * http://viralpatel.net/blogs/java-read-write-excel-file-apache-poi/
     *
     */
    private void printResults() {
        /**
         * Zahl der Messtore.
         */
        int tore = Integer.parseInt(messtore.getText());
        /**
         * Gibt die gespeicherten Werte der DB an.
         * Jedes Unterarray repräsentiert einen Datensatz. Der zweite Index ist bei 0 die Startnummer, 1 der Name, 2 die Kategorie, 3 Lauf 1, 4 Lauf 2.
         */
        String[][] werte = (new MySQLConnection(host.getText(), port.getText(), db.getText(), user.getText(), pw.getText(), null)).getAuswertung(starter, tore);
        //FileChooser einsetzen, um Speicherort zu ermitteln
        FileChooser fc = new FileChooser();
        //Titel anzeigen
        fc.setTitle("Speicherort für die Auswertung:");
        //standardmäßig im Home-Verzeichnis starten
        fc.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        //Alle-Dateien-Filter entfernen
        fc.setSelectedExtensionFilter(null);
        //FileFilter für Exceldateien hinzufügen
        //nur "alte" Excel-Dateien können geschrieben werden!
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Microsoft Excel 1997-2003 Dokument (.xls)", "*.xls")
        );
        //Dateien einlesen
        File returnVal = fc.showSaveDialog(primaryStage);
        //prüfen, ob Datei zurückgegeben --> eine gewählt; muss aber nicht existieren
        if (returnVal != null) {
            /**
             * Repräsentation der Excel-Datei.
             */
            HSSFWorkbook workbook = new HSSFWorkbook();
            /**
             * Alle Kategorien, die angegeben sind.
             */
            List<String> kategorien = getKategorien();
            //alle Kategorien durchgehen, für alle eine Mappe der DB füllen.
            for (String kategorie : kategorien) {
                /**
                 * Repräsentiert die Mappe, in der die Daten landen.
                 */
                HSSFSheet sheet;
                //Prüfen, ob die Länge des Kategorienamens zwischen 1 und 31 liegt --> sonst ungültig
                if (!kategorie.isEmpty()&&kategorie.length()<31) {
                    //wenn ja: Kategoriename ist Mappenname
                    sheet = workbook.createSheet(kategorie);
                    //sonst: Standardname
                } else {
                    sheet = workbook.createSheet("namenlose Kategorie");
                }

                Map<String, List<String>> data = new HashMap<String, List<String>>();
                List<String> label = new ArrayList<>();
                label.add("Startnummer");
                label.add("Name");
                label.add("Kategorie");
                label.add("reine Laufzeit- Lauf 1");
                label.add("Gesamtstrafen- Lauf 1");
                label.add("Laufzeit insgesamt- Lauf 1");
                for (int i = 0; i < tore; i++) {
                    label.add("Strafe Tor " + (i + 1));
                }
                label.add("reine Laufzeit- Lauf 2");
                label.add("Gesamtstrafen- Lauf 2");
                label.add("Laufzeit insgesamt- Lauf 2");
                for (int i = 0; i < tore; i++) {
                    label.add("Strafe Tor " + (i + 1));
                }
                data.put("0", label);

                for (int i = 0; i < werte.length; i++) {
                    //data.put(""+(i+2), werte[i]);
                    if (werte[i][2].equals(kategorie)) {
                        List<String> angaben = new ArrayList<>();
                        angaben.add(werte[i][0]);
                        angaben.add(werte[i][1]);
                        angaben.add(werte[i][2]);
                        String lauf1 = werte[i][3];
                        angaben.addAll(extractList(lauf1));
                        angaben.addAll(extractList(werte[i][4]));
                        data.put("" + (i + 1), angaben);
                    }
                }
                Set<String> keyset = data.keySet();
                int rownum = 0;
                for (String key : keyset) {
                    Row row = sheet.createRow(rownum++);
                    List<String> objArr = data.get(key);
                    int cellnum = 0;
                    for (Object obj : objArr) {
                        Cell cell = row.createCell(cellnum++);
                        if (obj instanceof Date) {
                            cell.setCellValue((Date) obj);
                        } else if (obj instanceof Boolean) {
                            cell.setCellValue((Boolean) obj);
                        } else if (obj instanceof String) {
                            cell.setCellValue((String) obj);
                        } else if (obj instanceof Double) {
                            cell.setCellValue((Double) obj);
                        }
                    }
                }
                for (int i = 0; i < 9 + 2 * tore; i++) {
                    sheet.autoSizeColumn(i);
                }
            }
            try {
                FileOutputStream out
                        = new FileOutputStream(returnVal);
                workbook.write(out);
                out.close();
                System.out.println("Excel written successfully..");

            } catch (FileNotFoundException e) {
                MySQLConnection.staticExceptionDialog(e, "Schreibfehler", "Auswertung konnte nicht geschrieben werden", "Die Excel-Datei mit der Auswertung konnte nicht geschrieben werden. Bitte versuchen Sie es erneut!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Programmende");
        alert.setHeaderText("Der Programmablauf ist beendet.");
        alert.setContentText("Das Programm hat seine Aufgabe erfüllt und wird nun beendet. Vielen Dank für die Benutzung des Softwaresystems!");
        alert.showAndWait();
        Platform.exit();
    }

    /**
     * Verwandelt einen übergebenen String im Format "Laufzeit
     * pur|Strafen|Laufzeit gesamt|Tor: 0|Strafe: 0|usw. in eine Liste um, die
     * als Tabellenzeile funktioniert.
     *
     * @param lauf1 String im beschriebenen Format.
     * @return Liste, an deren erster Position die reine Laufzeit, der zweiten
     * Position die Gesamtstrafen, der dritten Position die Gesamtzeit und
     * danach die
     */
    private List<String> extractList(String lauf1) {
        List<String> ret = new ArrayList<>();
        String[] teile = lauf1.split("\\|");
        ret.add(teile[0]);
        ret.add(teile[1]);
        ret.add(teile[2]);
        for (int i = 3; i <= teile.length - 1; i++) {
            if (teile[i].startsWith("Tor: ")) {
                ret.add(teile[i + 1].substring(8));
            }
        }
        return ret;
    }
}
