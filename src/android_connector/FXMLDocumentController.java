/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android_connector;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/*Quellen: 
 http://code.makery.ch/blog/javafx-2-dialogs/
 http://www.dreamincode.net/forums/topic/199219-how-to-implement-keylistener-for-jtable/
 http://www.itblogging.de/java/java-mysql-jdbc-tutorial/
 https://www.youtube.com/watch?v=ki77CLTHaNc
 http://javabeginners.de/Netzwerk/Netzwerkdaten_ermitteln.php
 https://wiki.byte-welt.net/wiki/JTable_dynamisch_Zeilen_hinzuf%C3%BCgen_und_entfernen
 https://coderanch.com/t/617128/GUI/java/JScrollPane-size
 */
/**
 *
 * @author Eric Ackermann
 */
public class FXMLDocumentController implements Initializable, KeyListener {

    /**
     * Verbinder zur Datenbank.
     */
    public Android_Connector andr;
    /**
     * Startet den jeweils nächsten Lauf.
     */
    @FXML
    private Button naechsterLauf;
    /**
     * Zeigt die Nummer des aktuellen Laufs an.
     */
    @FXML
    private Label aktLauf;

    /**
     * Getter für das ListView, das immer die aktuellen Laufzeiten anzeigt.
     *
     * @return
     */
    public ListView getListView() {
        return werte;
    }
    /**
     * Nummer des aktuellen Laufs in Computerzählweise.
     */
    public int lauf = 0;

    /**
     * Führt Aktionen durch, die beim Start des Wettkampfes nötig sind.
     *
     * @param cal Calendar, der die Startzeit enthält
     */
    public void start(Calendar cal) {
        startzeit = cal;
        //TextArea mit Stoppuhrfunktion anzeigen...
        stoppuhr.setVisible(true);
        //... und Stoppuhrprozess starten
        exec = Executors.newSingleThreadScheduledExecutor();
        clock = exec.scheduleAtFixedRate(TimerTask, 0, (int) 2000, TimeUnit.MILLISECONDS);
        //Wenn gestartet wurde, ist auch stoppen möglich
        stopp.setDisable(false);
        System.out.println("Startjahr: " + startzeit.get(Calendar.YEAR));
        System.out.println("Startmonat: " + (startzeit.get(Calendar.MONTH) + 1));
        System.out.println("Starttag: " + startzeit.get(Calendar.DATE));
        System.out.println("Startstunde: " + startzeit.get(Calendar.HOUR_OF_DAY));
        System.out.println("Startminute: " + startzeit.get(Calendar.MINUTE));
        System.out.println("Startsekunde: " + startzeit.get(Calendar.SECOND));
        //in Datenbank eintragen, dass und wann der Wettkampf gestartet wurde
        android.start_Messung(startzeit);
        gestartet = true;
    }

    /**
     * Führt Aktionen zum Beenden durch.
     */
    public void stop() {
        beendet = true;
        //letzter im Ziel --> Wettkampf beendet, Timer und Syncronisation ausschalten, Stoppbutton auf inaktiv schalten
        //Synchronisation bleibt an, um nachträglich z.B. Strafzeiten ändern zu können
        //result.cancel(false);
        clock.cancel(false);
        //Info im Zeitanzeiger, dass Wettkampf vorbei ist
        stoppuhr.setText("Der Wettkampf ist vorbei!");
        //Möglichkeit, nächsten Lauf zu starten
        if (this.lauf < 1) {
            naechsterLauf.setVisible(true);
        } else if (this.lauf == 1) {
            naechsterLauf.setVisible(true);
            naechsterLauf.setText("Wiederholungslauf starten!");

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Kategorie beendet");
            alert.setHeaderText("Alle Läufe dieser Kategorie sind durchgeführt worden.");
            alert.setContentText("Alle Läufe wurden von den eingeteilten Startern absolviert, die Werte sind gespeichert. Klicken Sie auf \"Nächste Kategorie starten\" zum Fortfahren.");
            alert.showAndWait();
            naechsterLauf.setVisible(true);
            naechsterLauf.setText("Nächste Kategorie starten!");

        }
    }

    /**
     * Fragt den User, welche Startnummern den einen Lauf wiederholen dürfen.
     *
     * @param werte Array, das die Starter enthält. Erster Index: willkürliche
     * Zahl des Starters, zweiter Index: Attribut(0=Startnummer, 1=Name,
     * 2=Kategorie)
     * @return
     */
    List<String[]> getWiederholer(String[][] werte) {
        List<String[]> ret = new ArrayList<>();
// Create the custom dialog.
        Dialog<Object> dialog = new Dialog<>();
        dialog.setTitle("Auswahl der Starter für den Wiederholungslauf");
        dialog.setResizable(true);
        dialog.getDialogPane().setPrefSize(480, 320);
        dialog.setHeaderText("Bitte wählen Sie die Checkboxen der Starter aus, die den Wiederholungslauf absolvieren müssen, und  wählen Sie in den CheckBoxen dieser Starter,"
                + " welchen Lauf der jeweilige Wiederholungslauf ersetzt!");

// Set the icon (must be included in the project).
//dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));
// Set the button types.
        ButtonType loginButtonType = new ButtonType("Bestätigen", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        CheckBox[] selections = new CheckBox[werte.length];
        ComboBox[] boxes = new ComboBox[werte.length];
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("1");
        items.add("2");
        GridPane pane = new GridPane();
        for (int i = 0; i < werte.length; i++) {
            selections[i] = new CheckBox("Startnummer " + werte[i][0] + " (" + werte[i][1] + ")?   ");
            boxes[i] = new ComboBox(items);
            //standardmäßig die 1 voranstellen
            boxes[i].getSelectionModel().selectFirst();
            pane.add(selections[i], 1, (i + 1));
            pane.add(boxes[i], 2, (i + 1));
        }
        /*ColumnConstraints column1 = new ColumnConstraints();
column1.setPercentWidth(50);
pane.getColumnConstraints().add(column1);*/

        dialog.getDialogPane().setContent(pane);
        dialog.showAndWait();
        for (int i = 0; i < selections.length; i++) {
            if (selections[i].isSelected()) {
                String[] akt = {werte[i][0], werte[i][1], boxes[i].getValue().toString()};
                ret.add(akt);
                android.ersetzenEintragen(boxes[i].getSelectionModel().getSelectedItem().toString(), werte[i][0]);
            }
        }
// Create the username and password labels and fields.
//
        return ret;
    }
    /**
     * Button "Daten anzeigen"
     */
    @FXML
    private Button button;
    /**
     * Label "IP_ADRESSE"
     */
    @FXML
    private Label IP;
    /**
     * Choicebox für Wahl der Stationen
     */
    @FXML
    private ChoiceBox<String> wahl_stationen;
    /**
     * enthält die Zeiten
     */
    @FXML
    private ListView<String> werte;
    /**
     * Checkbox, die über automatisches Synchronisieren entscheidet
     */
    @FXML
    private CheckBox sync;
    /**
     * Startet die Zeitmessung
     */
    @FXML
    private Button start;
    /**
     * Zeigt die Zeit seit Start an.
     */
    @FXML
    private TextArea stoppuhr;
    /**
     * Button, der Lauf einer Startnummer anhält.
     */
    @FXML
    private Button stopp;

    /**
     * Getter für den Stopptbutton
     *
     * @return den Stopptbutton als Objekt der Klasse Button
     */
    public Button getStoppButton() {
        return stopp;
    }

    /**
     * Getter für den Startbutton
     *
     * @return den Startbutton als Objekt der Klasse Button
     */
    public Button getStartButton() {
        return start;
    }
    @FXML
    private TextField aktualisierung;

    /**
     * Aktion, die ausgeführt wird, wenn der Button "Anzeigen" angeklickt wird
     *
     * @param event
     */
    @FXML
    private void handleButtonAction(ActionEvent event) {
        //aus der ComboBox auslesen, von welcher Station die Messwerte ausgewählt werden sollen
        int station = wahl_stationen.getSelectionModel().getSelectedIndex();
        //Zeiten aus der DB auslesen lassen, ListView "werte" übergeben, damit Zeiten in dieses geschrieben werden können
        android.zeitenauslesen(tore, startnummern, werte, startzeit);
    }
    /**
     * Instanz der Klasse, welche eine Verbindung mit der Datenbank herstellt
     */
    public MySQLConnection android;
    /**
     * Anzahl der Stationen
     */
    public int stationen;
    /**
     * Tabelle, die zur Eingabe der Namen und Startnummern eingesetzt wird
     */
    JTable table;
    JDialog dialog;
    /**
     * Speichert für jedes Tor, welcher Messstation dieses zugeordnet ist.
     * Nummer des Tores: Position im Array; Standardwert: -1
     */
    public int[] zuordnung_Tore;
    /**
     * Speichert die Gesamtzahl der Messtore.
     */
    public int tore;
    /**
     * Speichert die Zahl der Starter.
     */
    public int startnummern = 0;
    /**
     * Speichert alle Startnummern.
     */
    public List<Integer> starter;
    /**
     * Starter und Startnummern; erste Spalte: Startnummer, zweite Spalte: Name
     */
    public String[][] values;
    private boolean prüfe_Internet;

    private String messstationen, messtore;
    private boolean protokoll;
    private String protokollpfad;
    private ConfigWindowController conf;
    public String kategorie;
    /**
     * Wie gestartet werden soll.
     */
    public String startmodus;
    /**
     * Speichert die genauen Strafzeiten der Starter an den Toren. Erster Index:
     * Startnummer, zweiter Index: Tor
     */
    public int[][] strafen;

    /**
     * Bereitet das Hauptfenster auf die Ausführung vor.
     *
     * @param prüfe_Internet true, wenn die Netzwerkverbindung vor Programmstart
     * geprüft werden soll, sonst false
     * @param port Port der Datenbank
     * @param host Host der Datenbank
     * @param db Datenbankname
     * @param user Benutzername der Anmeldung
     * @param pw Passwort für die Anmeldung
     * @param messstationen Zahl der Messstationen
     * @param tore zahl der Messtore
     * @param werte Startnummern und Namen
     * @param protokoll true, wenn eine Zwischenprotokollierung gewünscht ist,
     * sonst false
     * @param protokollpfad Pfad des Verzeichnisses für Protokolldateien
     * @param lauf Zahl des aktuellen Laufes in Computerzählweise
     * @param ersterStart true beim ersten Aufruf des Programms. Dann werden
     * Initialisierungen vorgenommen. Sonst nicht.
     * @param stage Fenster, in dem das Programm läuft.
     * @param conf Konfigurationsfenster, zu dem später zurückgekehrt werden
     * soll
     * @param kategorie Kategorie, die gerade läuft
     * @param andr Hauptklasse der Anwendung, die Programm gestartet hat
     * @param startmodus wie gestartet werden soll
     */
    public void init(boolean prüfe_Internet, String port, String host, String db, String user, String pw, String messstationen, String tore, String[][] werte, boolean protokoll, String protokollpfad, int lauf, boolean ersterStart, Stage stage, ConfigWindowController conf, String kategorie, Android_Connector andr, String startmodus) {
        setValues(andr);
        this.startmodus = startmodus;
        this.stage = stage;
        this.kategorie = kategorie;
        this.prüfe_Internet = prüfe_Internet;
        this.messstationen = messstationen;
        this.messtore = tore;
        this.protokoll = protokoll;
        this.protokollpfad = protokollpfad;
        this.conf = conf;
        if (lauf < 2) {
            aktLauf.setText("aktueller Lauf: " + (lauf + 1));
        } else {
            aktLauf.setText("aktueller Lauf: Wiederholungslauf");
        }
        values = werte;
        this.lauf = lauf;
        starter = new ArrayList<>();
        JOptionPane pane;
        //pane wird JDialog zugewiesen, um es nach dem Anzeigen noch kontrollieren zu können
        //dialog = pane.createDialog(null, "Konfiguration");
        //Anzeigen des Panes
        //dialog.setVisible(true);
        //neue Instanz von MySQLConnection anlegen, welche alle Verbindungen mit der Datenbank erledigt
        //nötige Werte werden aus dem Konfigurationsfenster eingelesen
        android = new MySQLConnection(host, port, db, user, pw, this);
        andr.andro = android;
        //Tabelle "Namen" anlegen
        if (ersterStart) {
            android.nameninitialisieren();
        }
        //Namen und Startnummern aus der Tabelle auslesen und zeilenweise in die MySQL-Datenbank schreiben
        for (String[] werte1 : werte) {
            if (ersterStart) {
                android.nameeinfügen(werte1[0], werte1[1]);
            }
            starter.add(new Integer(werte1[0]));
            startnummern++;
        }
        //Zahl der Stationen ermitteln...
        stationen = Integer.parseInt(messstationen);
        //... und in die Tabelle für allgemeine Dinge schreiben, damit sie der App bekannt ist
        if (ersterStart) {
            android.allgemeineTabelleanlegen(stationen + "");
        }
        //Zuordnung der einzelnen Tore zu den Messstationen
        this.tore = Integer.parseInt(tore);
        if (ersterStart) {
            //Vorbereiten der Tabellen für die Tore
            for (int i = 0; i < this.tore; i++) {
                android.messpunktanlegen("Messstation_" + i);
            }
            //Zuordnungstabelle der Tore initialisieren
            this.zuordnung_Tore = new int[this.tore];
            //Array mit CheckBoxen initialisieren, welche ein Tor einer Messstation zuordnen
            JCheckBox[] box = new JCheckBox[this.tore];
            //Arrays initialisieren
            for (int i = 0; i < this.tore; i++) {
                this.zuordnung_Tore[i] = -1;
                box[i] = new JCheckBox("Tor " + (i + 1));
                box[i].setSelected(false);
            }
            //Für jede Station auswählen, welche Tore zugeordnet sind
            for (int i = 0; i < stationen; i++) {
                //Liste von Objekten anlegen, die für die aktuelle Messstation den Inhalt des JOptionPanes an den User enthält
                List<Object> nachricht = new ArrayList<>();
                //Nachricht mit Erklärung
                nachricht.add("Bitte wählen Sie die Tore aus, die Messtation " + (i + 1) + " von " + stationen + " zugeordnet sind.");
                //speichert, ob noch Tore unzugeordnet sind
                boolean tore_übrig = false;
                for (int j = 0; j < this.tore; j++) {
                    //nachricht alle unzugeordneten Tore hinzufügen
                    if (this.zuordnung_Tore[j] == -1) {
                        nachricht.add(box[j]);
                        tore_übrig = true;
                    }
                }
                //Falls noch Tore übrig sind...
                if (tore_übrig) {
                    //... diese vom User zuordnen lassen
                    String zugeordnete_Tore = "";
                    //Liste muss in Array umgewandelt werden
                    Object[] out = nachricht.toArray();
                    //JOptionPane mit Abfrage erzeugen und über Dialog anzeigen
                    pane = new JOptionPane(out,
                            JOptionPane.INFORMATION_MESSAGE,
                            JOptionPane.DEFAULT_OPTION);
                    //pane wird JDialog zugewiesen, um es nach dem Anzeigen noch kontrollieren zu können
                    dialog = pane.createDialog(null, "Zuordnung der Tore zu den Messstationen");
                    //Anzeigen des Panes
                    dialog.setVisible(true);
                    //Zuordnung der Tore speichern
                    for (int j = 0; j < this.tore; j++) {
                        //prüfen, ob aktuelles Tor eben für diese Messstation zugeordnet wurde
                        if (box[j].isSelected() && this.zuordnung_Tore[j] == -1) {
                            //Zuordnung global ...
                            this.zuordnung_Tore[j] = i;
                            //... und lokal für Weitergabe speichern
                            zugeordnete_Tore += j + "|";
                        }
                    }
                    //letzten, unnötigen "|" entfernen...
                    zugeordnete_Tore = zugeordnete_Tore.substring(0, zugeordnete_Tore.length() - 1);
                    //... und Zuordnung in DB speichern
                    android.toreZuordnen("Messstation_" + i + "_Tore", zugeordnete_Tore);
                }
            }
        }

        //neue Instanz der Klasse NetworkUtil anlegen, um... 
        NetworkUtil nu = new NetworkUtil();
        //... die IP-Adresse zu ermitteln
        //Annahme, die IP sei nicht gültig
        boolean ip_gültig = false;
        //CheckBox deaktiviert Prüfung der Internetverbindung
        if (!prüfe_Internet) {
            ip_gültig = true;
        }

        //solange sie nicht gilt...
        while (!ip_gültig) {
            //... prüfen, ob die IP gleich der des Localhosts ist --> dann kein Netzwerk verbunden, ...
            if (!nu.getOwnerIp().equals("127.0.0.1")) {
                //... sonst: Netzwerk verbunden, IP gültig
                ip_gültig = true;
            } else {
                //Nachricht anzeigen
                JOptionPane.showMessageDialog(null, "Bitte verbinden Sie den PC mit einem Netzwerk und klicken Sie auf \"OK\"", "Kein Netzwerk verbunden",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        //IP für Smartphone-Verbindung auf der GUI anzeigen
        IP.setText(nu.getOwnerIp());
        //ComboBox für Auswahl der Station leeren
        wahl_stationen.getItems()
                .clear();
        //ComboBox ChangeListener zuordnen --> bei Auswahl einer anderen Station sofort Anzeige der Zeiten aktualisieren
        wahl_stationen.valueProperty()
                .addListener(new javafx.beans.value.ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue
                    ) {
                        //aus der ComboBox auslesen, von welcher Station die Messwerte ausgewählt werden sollen
                        int station = wahl_stationen.getSelectionModel().getSelectedIndex();
                        //Zeiten aus der DB auslesen lassen, ListView "werte" übergeben, damit Zeiten in dieses geschrieben werden können
                        if (gestartet) {
                            android.zeitenauslesen(FXMLDocumentController.this.tore, startnummern, FXMLDocumentController.this.werte, startzeit);
                        }
                    }
                }
                );
        //ComboBox mit Stationen füllen, damit diese später ausgewählt werden können
        for (int i = 0;
                i < this.tore;
                i++) {
            wahl_stationen.getItems().add("Tor " + (i + 1) + "");
        }

        //Station 1 auswählen
        wahl_stationen.getSelectionModel()
                .select(0);
        //größte Startnummer ermitteln und festhalten, da in verschiedenen Arrays für Größe wichtig
        startnummerMax = max(starter) + 1;
        //Arrays für Start- und Zielzeiten festlegen
        startzeiten = new Calendar[startnummerMax + 1];
        zielzeiten = new Calendar[startnummerMax + 1];
        //Start der Synchronisierung
        exec = Executors.newSingleThreadScheduledExecutor();
        //sekündlich Aktion von "TimerTask" ausführen (Darstellung mit DB sunchronisieren), Zuordnung zu result, um Ausführung stoppen zu können
        //Aktion läuft in eigenem Thread
        result = exec.scheduleAtFixedRate(SyncTask, 0, (int) 2000, TimeUnit.MILLISECONDS);
        //Pfad für Protokollierung übergeben, wenn Protokollierung aktiviert
        if (protokoll) {
            android.setPfad(protokollpfad);
        }
        if (!ersterStart) {
            android.messWerteLeeren(Integer.parseInt(tore));
        }
        android.laufEintragen(lauf);
        this.strafen = new int[startnummerMax + 1][Integer.parseInt(tore)];
    }

    @Override
    public void initialize(URL url, ResourceBundle rb
    ) {
        /*
        // TODO
        //Startnummern und Namen in MySQL-Tabelle "Namen" schreiben
        //Tabelle für die Eingabe generieren
        table = new JTable();
        //Tabelle eine Instanz von DefaultTableModel als Tabellenmodell zuordnen --> Dieses kann später wieder ausgelesen werden
        //Model enthält ein Object-Array für die Tabellenzellen und ein String-Array für die Tabellenköpfe
        table.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Startnummer", "Namen"}));
        //auslesen des DefaultTableModels --> wird für Hinzufügen und Entfernen von Zeilen benötigt
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        /**
         * Button, mit dem eine neue Zeile angelegt werden kann
         */

 /*JButton neue_Zeile = new JButton("Zeile hinzufügen");
        //ActionListener bestimmt Aktion bei Klick auf den Button: Zeile hinzufügen
        neue_Zeile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                //Hinzufügen oder Bearbeiten von Zeilen nur mit dem DefaultTabelModel möglich!
                model.addRow(new Object[2]);
                //neu hinzugefügte Felder initialisieren --> verhindert Nullpointer Exception, falls Felder lehr bleiben
                table.setValueAt("", table.getRowCount() - 1, 0);
                table.setValueAt("", table.getRowCount() - 1, 1);

            }
        });
        /**
         * Button, der hinzugefügte Zeile der Tabelle entfernt
         */
 /*JButton entferne_Zeile = new JButton("Zeile entfernen");
        //ActionListener erstellen
        entferne_Zeile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                //Wenn eine Zeile in der Tabelle ausgewählt ist...
                if (table.getSelectedRow() > 0) {
                    //... wird diese gelöscht, sonst...
                    model.removeRow(table.getSelectedRow());
                } //prüfen, ob es noch mehr als eine Zeile gibt...
                else if (table.getRowCount() - 1 > 0) {
                    //... und die zuletzt hinzugefügte Zeile löschen
                    model.removeRow(table.getRowCount() - 1);
                }

            }

        });
        /**
         * Button, der Startnummern automatisch aufsteigend vergibt
         */
 /*JButton auto_Startnummer = new JButton("Startnummern automatisch generieren");
        //ActionListener zuweisen
        auto_Startnummer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                //Zahl der Zeilen der Tabelle entspricht Zahl der Teilnehmer
                int teilnehmer = table.getRowCount();
                //Startnummern berechnen
                for (int i = 0; i < teilnehmer; i++) {
                    table.setValueAt("" + (i + 1), i, 0);
                }
            }
        });
        //Keine zeilenweise...
        table.setRowSelectionAllowed(false);
        //... oder Spaltenweise Auswahl, ...
        table.setColumnSelectionAllowed(false);
        //... sondern nur Auswahl einzelner Zellen
        table.setCellSelectionEnabled(true);
        //nur einfache Auswahl von Zellen möglich
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        //KeyListener der Tabelle hinzufügen; diese Klasse implementiert einen solchen
        //Hintergrund: Zellen der Tabelle werden erst mit eingegebenem Inhalt gefült, wenn man aus der Zelle heraus klickt, dies ist aber unpraktisch,
        //da es zum Beispiel beim letzten Namen schnell vergessen wird
        //--> Idee: KeyListener wird immer ausgelöst, wenn ein Buchstabe in die Tabelle geschrieben wird --> Eingabe wird sofort gespeichert
        table.addKeyListener(this);
        //neues ScrollPane erstellen, dem die Tabelle zugeordnet wird --> nur dann Anzeige der Spaltenbeschriftungen, außerdem durch kann Tabelle so
        //unendlich groß werden, während man im ScrollPane an ihr entlang scrollen kann
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(100, 200));
        //Labels mit Beschriftungen, die später in das Konfigurationsfenster eingebaut werden sollen
        JLabel tabelle_Info = new JLabel("Bitte tragen Sie in die nachfolgende Tabelle die Namen und Startnummern der Teilnehmer ein!");
        JLabel Nummer_Messstationen = new JLabel("Bitte geben Sie die Zahl der Messstationen ein.");
        JLabel Nummer_Tore = new JLabel("Bitte geben Sie die Zahl der Tore ein.");
        JLabel host_Config = new JLabel("Geben Sie bitte die Konfigurationswerte für den MySQL-Server ein, falls Sie diese verändert haben!");
        //Initialisierung mit den Standardwerten der Textfelder für die notwendigen Eingaben
        JTextField messstationen = new JTextField("1");
        JTextField abfr_tore = new JTextField("2");
        JTextField host = new JTextField("localhost");
        JTextField port = new JTextField("3306");
        JTextField db = new JTextField("android_connect");
        JTextField user = new JTextField("root");
        //CheckBox, mit der abgefragt wird, ob auf eine Internetverbindung geprüft werden soll
        JCheckBox prüfe_Internet = new JCheckBox("Netzwerkverbindung vor Programmstart prüfen?");
        prüfe_Internet.setSelected(false);
        //Verwendung eines JPasswordFields --> verdeckte Eingabe des SQL-Passworts --> Datenschutz
        JPasswordField pw = new JPasswordField("");
        //setzt Im Konfigurationsfenster anzuzeigenden Content zusammen --> von oben nach unten
        Object[] message = {tabelle_Info, scroll, neue_Zeile, entferne_Zeile, auto_Startnummer, Nummer_Messstationen, messstationen, Nummer_Tore, abfr_tore, host_Config, "Hostname: ", host,
            "Port: ", port, "Datenbank: ", db, "Benutzername: ", user, "Passwort: ", pw, prüfe_Internet};
        //Initialisierung eines JOptionPanes, das als Basis für das Konfigurationsfenster dient, mit allen anzuzeigenden Inhalten
         */
    }
    /**
     * Zeitgeber für die Synchronisation
     */
    ScheduledExecutorService exec;
    /**
     * ermöglich Beenden der automatischen Synchronisation
     */
    public ScheduledFuture<?> result;
    /**
     * der Code, der die Synchronisierung durchführt und sekündlich ausgeführt
     * wird
     */
    Runnable SyncTask = new Runnable() {
        /**
         * Prozess für die Synchronisation
         */
        public void run() {
            //verbindet diesen Synchronisationsthread mit dem JavaFX-Mainthread --> Zugriff auf GUI
            Platform.runLater(new Runnable() {
                public void run() {
                    //neue Instanz der Klasse NetworkUtil anlegen, um... 
                    NetworkUtil nu = new NetworkUtil();
                    //IP für Smartphone-Verbindung auf der GUI anzeigen
                    IP.setText(nu.getOwnerIp());
                    //jede Sekunde für die gewählte Station...
                    int station = wahl_stationen.getSelectionModel().getSelectedIndex();
                    //... aus der Datenbank die Zeiteintragungen auslesen
                    android.zeitenauslesen(tore, startnummern, werte, startzeit);

                }
            });
        }
    };
    /**
     * Zeitgeber für die Stoppuhr
     */
    ScheduledExecutorService executor;
    /**
     * ermöglich Beenden der automatischen Zeitanzeige
     */
    public ScheduledFuture<?> clock;
    /**
     * der Code, der die Stoppuhr anzeigt und zehntelsekündlich ausgeführt wird
     */
    Runnable TimerTask = new Runnable() {
        /**
         * Prozess für die Synchronisation
         */
        public void run() {
            //verbindet diesen Thread mit dem JavaFX-Mainthread --> Zugriff auf GUI
            Platform.runLater(new Runnable() {
                public void run() {
                    //aus Timestamp Datum machen, Timestamp wird gebildet aus Differenz der Timestamps vom Start und aktuell
                    Date d = new Date(System.currentTimeMillis() - startzeit.getTimeInMillis());
                    //Calendar initialisieren...
                    Calendar c = Calendar.getInstance();
                    //... und diesem den Timestamp als Datum übergeben
                    c.setTime(d);
                    //vergangene Jahre ermitteln; 1970 abziehen, da dies der Minimalwert ist
                    int year = c.get(Calendar.YEAR) - 1970;
                    //vergangene Monate ermitteln
                    int month = c.get(Calendar.MONTH);
                    //vergangene Tage ermitteln, 1 abziehen, da immer 1 zu groß
                    int date = c.get(Calendar.DATE) - 1;
                    //vergangene Stunden ermitteln, 1 abziehen, da imer 1 zu groß
                    int hour = c.get(Calendar.HOUR_OF_DAY) - 1;
                    //vergangene Minuten, Sekunden, Milisekunden ermitteln
                    int minute = c.get(Calendar.MINUTE);
                    int second = c.get(Calendar.SECOND);
                    int millis = c.get(Calendar.MILLISECOND);
                    //String für Zeitangabe initialisieren
                    String zeitString = "Zeit seit Start:\n";
                    //Falls ein Wert nicht 0 ist, diesen aufführen
                    //Werte erhalten eigene Zeile im TextArea
                    if (year != 0) {
                        zeitString += year + " Jahre,\n";
                    }
                    if (month != 0) {
                        zeitString += month + " Monate,\n";
                    }
                    if (date != 0) {
                        zeitString += date + " Tage,\n";
                    }
                    if (hour != 0) {
                        zeitString += hour + ":";
                    }
                    if (minute != 0) {
                        zeitString += minute + ":";
                    }
                    if (second != 0) {
                        zeitString += second + ",";
                    }
                    if (millis != 0) {
                        zeitString += millis + " . ";
                    }
                    if (beendet) {
                        zeitString = "Wettkampf beendet!";
                    }
                    //erzeugten Ausgabetext in einer Konstante speichern --> Variablen können nicht vom Mainthread aus abgefragt werden
                    final String ausgabe = zeitString;
                    Platform.runLater(new Runnable() {
                        public void run() {
                            //auf GUI-Thread zugreifen und dort die Anzeige aktualisieren
                            stoppuhr.setText(ausgabe);
                        }
                    });
                }
            });
        }
    };

    /**
     * schaltet automatische Synchronisation an und aus; standardmäßig: an
     *
     * @param event Aktivierung/Deaktivierung der zuständigen CheckBox auf der
     * GUI
     */
    @FXML
    private void syncen(ActionEvent event) {
        //Start bei ausgewählter Checkbox
        if (sync.isSelected() && gestartet) {
            result = exec.scheduleAtFixedRate(SyncTask, 0, (int) Double.parseDouble(aktualisierung.getText()), TimeUnit.MILLISECONDS);
        } //Abbruch bei abgewählter CheckBox
        else {
            result.cancel(false);
        }
    }

    /**
     * Implementierung des KeyListeners der Tabelle
     *
     * @param e der eingegebene Buchstabe
     */
    @Override
    public void keyTyped(KeyEvent e) {
        //ausgewählte Spalte...
        int y = table.getSelectedColumn();
        //... und Zeile ermitteln
        int x = table.getSelectedRow();
        //ist deren Inhalt nicht null (hat sie einen Inhalt)...
        if (table.getValueAt(x, y) != null) {
            //... wird diesem der eingegebene Buchstabe hinzugefügt, ...
            table.setValueAt(table.getValueAt(x, y).toString() + e.getKeyChar(), x, y);
        } else {
            //... sonst steht er als Inhalt in der leeren Zelle
            table.setValueAt(e.getKeyChar(), x, y);
        }
    }

    //bei Eingabe eines Buchstabens in die Tabelle werden alle drei Events ausgelöst, es ist aber nur eine Aktion erforderlich --> andere Methoden
    //machen einfach nichts
    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    /**
     * Speichert die Startzeit des Wettbewerbs, um später Zeitdifferenzen
     * ausrechnen zu können. Startzeit ist Startzeit der ersten Startnummer.
     */
    public Calendar startzeit = null;
    /**
     * Speichert, ob der Wettkampf bereits gestartet wurde.
     */
    public boolean gestartet = false;
    /**
     * Speichert die größte Startnummer.
     *
     */
    public int startnummerMax = 0;
    /**
     * Speichert die Startzeiten der Teilnehmer. Indizes im Array entsprechen
     * jew. Startnummern
     */
    public Calendar[] startzeiten;
    /**
     * False, wenn noch nicht alle Startnummern gestartet sind, sonst true.
     */
    public boolean alleGestartet = false;

    /**
     * Wird ausgelöst, wenn der Benutzer auf den Button "Start!" klickt. Startet
     * die Zeitmessung
     *
     * @param event
     */
    @FXML
    private void starten(ActionEvent event) {
        Calendar temp = Calendar.getInstance();
        //prüfen, ob alle gestartet sind
        List<Integer> gestartete = alleGestartet();
        gestartet = true;
        //Aktionen beim ersten Start
        if (gestartete.size() == startnummern) //Startzeit für Zeitenermittlung festhalten und in Java.util.Calendar speichern, wenn die erste Startnummer startet, sonst nicht        
        {
            startzeit = temp;

            //TextArea mit Stoppuhrfunktion anzeigen...
            stoppuhr.setVisible(true);
            //... und Stoppuhrprozess starten
            exec = Executors.newSingleThreadScheduledExecutor();
            clock = exec.scheduleAtFixedRate(TimerTask, 0, (int) Double.parseDouble(aktualisierung.getText()), TimeUnit.MILLISECONDS);
            //Startbutton verbergen --> Starten nur einmal möglich
            //start.setVisible(false);
            //Wenn gestartet wurde, ist auch stoppen möglich
            stopp.setDisable(false);
            System.out.println("Startjahr: " + startzeit.get(Calendar.YEAR));
            System.out.println("Startmonat: " + (startzeit.get(Calendar.MONTH) + 1));
            System.out.println("Starttag: " + startzeit.get(Calendar.DATE));
            System.out.println("Startstunde: " + startzeit.get(Calendar.HOUR_OF_DAY));
            System.out.println("Startminute: " + startzeit.get(Calendar.MINUTE));
            System.out.println("Startsekunde: " + startzeit.get(Calendar.SECOND));
            //in Datenbank eintragen, dass und wann der Wettkampf gestartet wurde
            android.start_Messung(startzeit);
            //Zeitgeber initialisieren bereits geschehen
            //exec = Executors.newSingleThreadScheduledExecutor();
            //sekündlich Aktion von "TimerTask" ausführen (Darstellung mit DB sunchronisieren), Zuordnung zu result, um Ausführung stoppen zu können
            //Aktion läuft in eigenem Thread
            //result = exec.scheduleAtFixedRate(SyncTask, 0, (int) Double.parseDouble(aktualisierung.getText()), TimeUnit.MILLISECONDS);

        }
        if (!gestartete.isEmpty()) {
            //JComboBox mit möglichen Startnummern füllen und in JOptionPane anzeigen, wenn noch nicht alle Startnummern gestartet sind
            JComboBox verbleibendeStarter = new JComboBox(gestartete.toArray());
            verbleibendeStarter.setSelectedIndex(0);
            Object[] message = {"Bitte wählen Sie die gestartete Startnummer aus.", verbleibendeStarter};
            JOptionPane.showMessageDialog(null, message, "Startnummer?", JOptionPane.DEFAULT_OPTION);
            Integer gew = gestartete.get(verbleibendeStarter.getSelectedIndex());
            startzeiten[gew] = temp;
            //gerade eine Startnummer gestartet --> kann Ziel erreichen
            stopp.setDisable(false);
        }
        //sonst: Startbutton inaktiv schalten
        if (gestartete.size() == 1) {
            start.setDisable(true);
            alleGestartet = true;
        }
    }

    /**
     * Ermittelt die höchste Startnummer
     */
    private int max(List<Integer> starter) {
        int ret = starter.get(0);
        for (int i = 1; i < starter.size(); i++) {
            if (starter.get(i) > ret) {
                ret = starter.get(i);
            }
        }
        return ret;
    }
    /**
     * Speichert die Zeiten, zu denen die jeweiligen Startnummern die Ziellinie
     * erreicht haben. Dabei ist der Index im Array die Startnummer.
     */
    public Calendar[] zielzeiten;
    /**
     * Speichert, ob der Wettkampf bereits beendet ist. True= beendet, false =
     * läuft noch.
     */
    public boolean beendet = false;

    /**
     * Aktion, die bei Klick auf Stop-Button ausgelöst wird
     *
     * @param event Klick
     */
    @FXML
    private void stoppen(ActionEvent event) {
        //Zeit des Klicks ermitteln
        Calendar temp = Calendar.getInstance();
        /**
         * Speichert alle Startnummern, welche gestoppt werden können
         */
        List<Integer> gestoppte = alleGestoppt();
        //prüfen, ob Startnummern da sind, die man stoppen kann
        if (!gestoppte.isEmpty()) {
            //JComboBox mit möglichen Startnummern füllen und in JOptionPane anzeigen --> User wählt zu stoppende Startnummer aus
            JComboBox verbleibendeStarter = new JComboBox(gestoppte.toArray());
            verbleibendeStarter.setSelectedIndex(0);
            //JOptionPane zusammensetzen und aufrufen
            Object[] message = {"Bitte wählen Sie die Startnummer aus, die das Ziel erreicht hat.", verbleibendeStarter};
            JOptionPane.showMessageDialog(null, message, "Startnummer?", JOptionPane.DEFAULT_OPTION);
            //ermitteln, welche Startnummer jetzt im Ziel ist...
            Integer gew = gestoppte.get(verbleibendeStarter.getSelectedIndex());
            //... und dieser die gewünschte Zeit zuweisen
            zielzeiten[gew] = temp;
        }
        //nur eine stoppbare Startnummer --> wurde gerade gestoppt --> keine stoppbaren übrig --> Stopp- Button deaktivieren
        if (gestoppte.size() == 1) {
            stopp.setDisable(true);
        }
        //alle Startnummern sind unterwegs und alle wurden gestoppt --> alle im Ziel, Wettkampf ist vorbei
        if (gestoppte.size() == 1 && alleGestartet) {
            beendet = true;
            //letzter im Ziel --> Wettkampf beendet, Timer und Syncronisation ausschalten, Stoppbutton auf inaktiv schalten
            result.cancel(false);
            clock.cancel(false);
            //Info im Zeitanzeiger, dass Wettkampf vorbei ist
            this.stoppuhr.setText("Der Wettkampf ist vorbei!");
            android.werteFesthalten(lauf, kategorie);
            if (this.lauf < 1) {
                naechsterLauf.setVisible(true);
            } else if (this.lauf == 1) {
                naechsterLauf.setVisible(true);
                naechsterLauf.setText("Wiederholungslauf starten!");

            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Kategorie beendet");
                alert.setHeaderText("Alle Läufe dieser Kategorie sind durchgeführt worden.");
                alert.setContentText("Alle Läufe wurden von den eingeteilten Startern absolviert, die Werte sind gespeichert. Klicken Sie auf \"Nächste Kategorie starten\" zum Fortfahren.");
                alert.showAndWait();
                naechsterLauf.setVisible(true);
                naechsterLauf.setText("Nächste Kategorie starten!");
            }
        }
    }

    /**
     * Geht das Array mit den Startzeiten durch und prüft, ob und welche
     * Startnummern noch nicht gestartet sind.
     *
     * @return alle Startnummern, die noch nicht gestartet sind.
     */
    public List<Integer> alleGestartet() {
        //ArrayList anlegen, die alle ausstehenden Startnummern enthält.
        List<Integer> ret = new ArrayList<>();
        //Array der Starter durchgehen und bei jedem schauen, ob es eine Startzeit gibt
        for (int i = 0; i < starter.size(); i++) {
            if (startzeiten[starter.get(i)] == null) {
                //Startnummer den nicht gestarteten hinzufügen, wenn dafür keine Startzeit gefunden wird
                ret.add(starter.get(i));
            }
        }
        return ret;
    }

    /**
     * Konvertiert eine Liste von Integers in ein int-Array.
     *
     * @param gestartete Liste mit Werten
     * @return Werte als Array in selber Sortierung
     */
    public int[] toIntArray(List<Integer> gestartete) {
        int[] ret = new int[gestartete.size()];
        for (int i = 0; i < gestartete.size(); i++) {
            ret[i] = gestartete.get(i);
        }
        return ret;
    }

    /**
     * Ermittelt alle Startnummern, die angehalten werden können.
     *
     * @return ArrayList aller beendbaren Startnummern
     */
    public List<Integer> alleGestoppt() {
        List<Integer> ret = new ArrayList<>();
        //Alle Startnummern ermitteln, die bereits gestartet, aber noch nicht im Ziel sind --> die noch stoppen können...
        for (int i = 0; i < starter.size(); i++) {
            if (startzeiten[starter.get(i)] != null && zielzeiten[starter.get(i)] == null) {
                //... und sie Rückgabe-Array hinzufügen
                ret.add(starter.get(i));
            }
        }
        return ret;
    }

    void setValues(Android_Connector aThis) {
        this.andr = aThis;
        andr.tore = this.tore;
        andr.andro = android;
        andr.doc = this;
    }
    public Stage stage;

    @FXML
    private void naechsterLauf(ActionEvent event) {
        boolean weiter = true;
        //ggf. geänderte Werte noch einmal speichern
        android.werteFesthalten(lauf, kategorie);
        String[][] startnummern;
        if (startmodus.equals(ConfigWindowController.alle_hintereinander)) {
            /*Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Protokollieren?");
            alert.setHeaderText("Protokoll des aktuellen Laufs erstellen?");
            alert.setContentText("Möchten Sie den aktuellen Lauf in einer Excel-Tabelle protokollieren?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                
            }*/
            conf.printResults(false);
        }
        if (lauf == 2) {
            weiter = false;
            if (result != null) {
                result.cancel(false);
            }
            if (clock != null) {
                clock.cancel(false);
            }
            //ConfigWindowController.xampp_stopp(ConfigWindowController.xampp);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ConfigWindow.fxml"));
            Parent root = null;
            try {
                root = fxmlLoader.load();
            } catch (IOException ex) {
                android.showExceptionDialog(ex, "Fehler", "Internes Problem ist aufgetreten", "Es ist ein interner Fehler aufgetreten. Bitte laden Sie ggf. eine neue Kopie der Datei!", false);
            }
            //Controller des Fensters bekommen...
            ConfigWindowController conf = (ConfigWindowController) fxmlLoader.getController();
            //... und ihm die Stage übergeben --> braucht dieser zur Anzeige von Dialogen
            conf.primaryStage = stage;
            conf.reinit(this.conf);
            //Fenster zusammensetzen und anzeigen
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            //beim Schließen des Fensters auch alles aus der DB löschen
            stage.setOnCloseRequest(
                    new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    //fortfahren, indem neue View geladen wird
                    event.consume();
                    //Laden des zweiten Fensters
                    FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
                    Parent root2 = null;
                    //Laden
                    try {
                        root2 = fxmlLoader2.load();
                    } catch (IOException ex) {
                        Logger.getLogger(Android_Connector.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //Konfiguration für den Smartphone-Verbindungsaufbau ändern
                    conf.overridePHPConfig(conf.pfad_xampp.getText());
                    //XAMPP starten
                    conf.xampp_start(conf.pfad_xampp.getText());
                    //wenn gewünscht, neue Verbindung herstellen und DB leeren
                    if (conf.db_leeren.isSelected()) {
                        new MySQLConnection(conf.host.getText(), conf.port.getText(), conf.db.getText(), conf.user.getText(), conf.pw.getText(), null).reset(false);
                    }
                    //Controller des zweiten Fensters laden...
                    FXMLDocumentController docu = (FXMLDocumentController) fxmlLoader2.getController();
                    //... und die Initialisierung mit den Konfigurationswerten des ersten Fensters aufrufen
                    String kategorie = conf.getSelectedKategorie();
                    if (kategorie != null) {
                        if (conf.protokoll.isSelected()) {
                            conf.writeConfig(conf.pfad.getText(), kategorie);
                        }
                        docu.init(conf.checkNetwork.isSelected(), conf.port.getText(), conf.host.getText(), conf.db.getText(), conf.user.getText(), conf.pw.getText(), conf.messstationen.getText(), conf.messtore.getText(), conf.getWerte(kategorie), conf.protokoll.isSelected(), conf.pfad.getText(), 0, true, stage, conf, kategorie, andr, startmodus);
                        //Startnummern übernehmen
                        //docu.setValues(andr);
                        //aktuelle Stage übergeben, um auch Dialoge anzeigen zu können
                        docu.stage = stage;
                        //Fenster zusammensetzen und anzeigen
                        Scene scene2 = new Scene(root2);
                        stage.setScene(scene2);
                        stage.show();
                        //beim Schließen des Fensters auch alles aus der DB löschen
                        stage.setOnCloseRequest((WindowEvent event1) -> {
                            //User fragen, ob er wirklich beenden will
                            int entscheidung = JOptionPane.showConfirmDialog(null, "Wenn Sie das Programm beenden, sind sämtliche"
                                    + " Zeiten verloren. Möchten Sie wirklich fortfahren?", "Wirklich beenden?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                            //wenn User nicht beenden will, Programm fortsetzen
                            if (entscheidung != JOptionPane.YES_OPTION) {
                                event1.consume();
                                //sonst: alles aus DB löschen, Threads beenden, Programm beenden, XAMPP stoppen
                            } else {
                                //ggf. neue Ausgabedatei erzeugen, die aktuellste Werte speichert
                                android.werteFesthalten(lauf, kategorie);
                                android.reset(true);
                                //ConfigWindowController.xampp_stopp(ConfigWindowController.xampp);
                                if (result != null) {
                                    result.cancel(false);
                                }
                                if (clock != null) {
                                    clock.cancel(false);
                                }
                            }
                        });
                    }
                }
            });
        }
        if (lauf == 1) //auswählen, wer noch einmal starten darf
        {
            List<String[]> neuStarter = getWiederholer(values);
            startnummern = neuStarter.toArray(new String[neuStarter.size()][3]);
            //keine Wiederholer --> instant nächste Kategorie starten!
            if (neuStarter.isEmpty()) {
                weiter = false;
                android.werteFesthalten(lauf, kategorie);
                if (result != null) {
                    result.cancel(false);
                }
                if (clock != null) {
                    clock.cancel(false);
                }
                //ConfigWindowController.xampp_stopp(ConfigWindowController.xampp);
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ConfigWindow.fxml"));
                Parent root = null;
                try {
                    root = fxmlLoader.load();
                } catch (IOException ex) {
                    android.showExceptionDialog(ex, "Fehler", "Internes Problem ist aufgetreten", "Es ist ein interner Fehler aufgetreten. Bitte laden Sie ggf. eine neue Kopie der Datei!", false);
                }
                //Controller des Fensters bekommen...
                ConfigWindowController conf = (ConfigWindowController) fxmlLoader.getController();
                //... und ihm die Stage übergeben --> braucht dieser zur Anzeige von Dialogen
                conf.primaryStage = stage;
                conf.reinit(this.conf);
                //Fenster zusammensetzen und anzeigen
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

                //beim Schließen des Fensters auch alles aus der DB löschen
                stage.setOnCloseRequest(
                        new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        //fortfahren, indem neue View geladen wird
                        event.consume();
                        //Laden des zweiten Fensters
                        FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
                        Parent root2 = null;
                        //Laden
                        try {
                            root2 = fxmlLoader2.load();
                        } catch (IOException ex) {
                            Logger.getLogger(Android_Connector.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        //Konfiguration für den Smartphone-Verbindungsaufbau ändern
                        conf.overridePHPConfig(conf.pfad_xampp.getText());
                        //XAMPP starten
                        conf.xampp_start(conf.pfad_xampp.getText());
                        //wenn gewünscht, neue Verbindung herstellen und DB leeren
                        if (conf.db_leeren.isSelected()) {
                            new MySQLConnection(conf.host.getText(), conf.port.getText(), conf.db.getText(), conf.user.getText(), conf.pw.getText(), null).reset(false);
                        }
                        //Controller des zweiten Fensters laden...
                        FXMLDocumentController docu = (FXMLDocumentController) fxmlLoader2.getController();
                        //... und die Initialisierung mit den Konfigurationswerten des ersten Fensters aufrufen
                        String kategorie = conf.getSelectedKategorie();
                        if (kategorie != null) {
                            docu.init(conf.checkNetwork.isSelected(), conf.port.getText(), conf.host.getText(), conf.db.getText(), conf.user.getText(), conf.pw.getText(), conf.messstationen.getText(), conf.messtore.getText(), conf.getWerte(kategorie), conf.protokoll.isSelected(), conf.pfad.getText(), 0, true, stage, conf, kategorie, andr, startmodus);
                            //Startnummern übernehmen
                            //aktuelle Stage übergeben, um auch Dialoge anzeigen zu können
                            docu.stage = stage;
                            //Fenster zusammensetzen und anzeigen
                            Scene scene2 = new Scene(root2);
                            stage.setScene(scene2);
                            stage.show();
                            //beim Schließen des Fensters auch alles aus der DB löschen
                            stage.setOnCloseRequest((WindowEvent event1) -> {
                                //User fragen, ob er wirklich beenden will
                                int entscheidung = JOptionPane.showConfirmDialog(null, "Wenn Sie das Programm beenden, sind sämtliche"
                                        + " Zeiten verloren. Möchten Sie wirklich fortfahren?", "Wirklich beenden?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                                //wenn User nicht beenden will, Programm fortsetzen
                                if (entscheidung != JOptionPane.YES_OPTION) {
                                    event1.consume();
                                    //sonst: alles aus DB löschen, Threads beenden, Programm beenden, XAMPP stoppen
                                } else {
                                    //ggf. neue Ausgabedatei erzeugen, die aktuellste Werte speichert
                                    android.werteFesthalten(lauf, kategorie);
                                    android.reset(true);
                                    //ConfigWindowController.xampp_stopp(ConfigWindowController.xampp);
                                    if (result != null) {
                                        result.cancel(false);
                                    }
                                    if (clock != null) {
                                        clock.cancel(false);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        } else {
            startnummern = this.values;
        }
        if (weiter) {
            FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
            Parent root2 = null;
            try {
                root2 = fxmlLoader2.load();
            } catch (IOException ex) {
                Logger.getLogger(Android_Connector.class.getName()).log(Level.SEVERE, null, ex);
            }
            FXMLDocumentController docu = (FXMLDocumentController) fxmlLoader2.getController();
            docu.init(prüfe_Internet, android.dbPort, android.dbHost, android.database, android.dbUser, android.dbPassword, messstationen, messtore, startnummern, protokoll, protokollpfad, lauf + 1, false, stage, conf, kategorie, andr, startmodus);
            //docu.setValues(null);
            Scene scene2 = new Scene(root2);
            stage.setScene(scene2);
            stage.show();
            //beim Schließen des Fensters auch alles aus der DB löschen
            stage.setOnCloseRequest((WindowEvent event1) -> {
                //User fragen, ob er wirklich beenden will
                int entscheidung = JOptionPane.showConfirmDialog(null, "Wenn Sie das Programm beenden, sind sämtliche"
                        + " Zeiten verloren. Möchten Sie wirklich fortfahren?", "Wirklich beenden?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                //wenn User nicht beenden will, Programm fortsetzen
                if (entscheidung != JOptionPane.YES_OPTION) {
                    event1.consume();
                    //sonst: alles aus DB löschen, Threads beenden, Programm beenden, XAMPP stoppen
                } else {
                    android.werteFesthalten(lauf + 1, kategorie);
                    android.reset(true);
                    //ConfigWindowController.xampp_stopp(ConfigWindowController.xampp);
                    if (result != null) {
                        result.cancel(false);
                    }
                    if (clock != null) {
                        clock.cancel(false);
                    }
                }
            });
        }

    }
    /**
     * Ändert die Synchronisierungsrate.
     * @param event 
     */
    @FXML
    private void changeRate(ActionEvent event) {
        result.cancel(false);
        result = exec.scheduleAtFixedRate(SyncTask, Integer.parseInt(aktualisierung.getText()), Integer.parseInt(aktualisierung.getText()), TimeUnit.MILLISECONDS);
    }
}
