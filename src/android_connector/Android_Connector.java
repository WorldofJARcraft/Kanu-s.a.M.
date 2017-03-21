/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android_connector;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.swing.JOptionPane;

/**
 *
 * @author Eric
 */
public class Android_Connector extends Application {
    public Android_Connector andr=this;
    public int tore;
    public MySQLConnection andro;
    public FXMLDocumentController doc;

    @Override
    public void start(Stage stage) throws Exception {
        //hübscheres Stylesheet
        setUserAgentStylesheet(STYLESHEET_MODENA);
        //Icon und Titel laden
        stage.getIcons().add(new Image(Android_Connector.class.getResourceAsStream( "lib/Logo.png" )));
        stage.setTitle("Kanu s.a.M Desktop Version 0.9");
        //Laden der Anzeige des ersten Fensters
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ConfigWindow.fxml"));
        Parent root = fxmlLoader.load();
        //Controller des Fensters bekommen...
        ConfigWindowController conf = (ConfigWindowController) fxmlLoader.getController();
        //... und ihm die Stage übergeben --> braucht dieser zur Anzeige von Dialogen
        conf.primaryStage = stage;
        conf.andr = this;
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
                if(conf.db_leeren.isSelected()){
                    new MySQLConnection(conf.host.getText(),conf.port.getText(),conf.db.getText(),conf.user.getText(),conf.pw.getText(), doc).reset(true);
                }
                //Controller des zweiten Fensters laden...
                FXMLDocumentController docu = (FXMLDocumentController) fxmlLoader2.getController();
                //... und die Initialisierung mit den Konfigurationswerten des ersten Fensters aufrufen
                String kategorie = conf.getSelectedKategorie();
                //Config-Datei speichern, wenn Protokollierung aktiv
                host = conf.host.getText();
                port = conf.port.getText();
                db = conf.db.getText();
                user=conf.user.getText();
                pw = conf.pw.getText();
                if(conf.protokoll.isSelected())
                conf.writeConfig(conf.pfad.getText(),kategorie);
                (new MySQLConnection(conf.host.getText(), conf.port.getText(), conf.db.getText(), conf.user.getText(), conf.pw.getText(), doc)).auswertungAnlegen(conf.getAllWerte());
                docu.init(conf.checkNetwork.isSelected(), conf.port.getText(), conf.host.getText(), conf.db.getText(), conf.user.getText(), conf.pw.getText(), conf.messstationen.getText(), conf.messtore.getText(), conf.getWerte(kategorie), conf.protokoll.isSelected(), conf.pfad.getText(),0,true, stage,conf,kategorie, conf.andr, conf.startmodus.getSelectionModel().getSelectedItem());
                //Startnummerb übernehmen
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
                        andro.werteFesthalten(doc.lauf,doc.kategorie);
                        andro.reset(true);
                        ConfigWindowController.xampp_stopp(ConfigWindowController.xampp);
                        if (doc.result != null) {
                            doc.result.cancel(false);
                        }
                        if (doc.clock != null) {
                            doc.clock.cancel(false);
                        }
                    }
                });
            }
        });
    }
    public static String host, port, db, user, pw;
    /**
     * Wird beim Beenden des Programms aufgerufen. Stoppt XAMPP. Löscht die Datenbank vollständig.
     */
    @Override
    public  void stop(){
        (new MySQLConnection(host, port, db, user, pw, doc)).reset(true);
        ConfigWindowController.xampp_stopp(ConfigWindowController.xampp);
    } 
    /**
     * Rettungsanker, der Programm startet, sollte FX es nicht schon so tun.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        //Beenden des Programms am Ende
        System.exit(0);
    }

}
