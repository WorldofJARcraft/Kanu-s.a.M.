/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android_connector;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class for a Person. Quelle:
 * @author http://code.makery.ch/library/javafx-8-tutorial/part2/ und http://stackoverflow.com/questions/13381067/simplestringproperty-and-simpleintegerproperty-tableview-javafx
 */
public class Person {
    /**
     * Startnummer des Starters. Wird als String gespeichert.
     */
    private String Startnummer;
    /**
     * Name des Starters.
     */
    private String Name;
    /**
     * Kategorie, in der der Starter antritt. Noch ohne Funktion. Kategorie wird vermutlich als String gespeichert. 
     */
    private String Kategorie;

    /**
     * Default constructor.
     */
    public Person() {
        this("", "", "");
    }

    /**
     * Constructor with some initial data.
     *
     * @param firstName Startnummer des Starters
     * @param lastName Name des Starters
     * @param kategorie dessen kategorie
     */
    public Person(String firstName, String lastName, String kategorie) {
        //Werte festhalten
        this.Startnummer = firstName;
        this.Name = lastName;
        this.Kategorie = kategorie;
    }
    /**
     * Gibt die Startnummer des Starters zurück.
     * @return Startnummer als String.
     */
    public String getStartnummer() {
        return Startnummer;
    }
    /**
     * Ändert die Startnummer auf den übergebenen Wert.
     * @param Startnummer die Startnummer, die übernommen werden soll.
     */
    public void setStartnummer(String Startnummer) {
        this.Startnummer = (Startnummer);
    }
    /**
     * Gibt den Namen des Starters zurück.
     * @return Name als String
     */
    public String getName() {
        return Name;
    }
    /**
     * Ändert den Namen des Starters auf den gegebenen Wert.
     * @param Name der neue Name des Starters
     */
    public void setName(String Name) {
        this.Name= Name;
    }
    /**
     * Gibt die Kategorie des Starters zurück.
     * @return Kategorie als String
     */
    public String getKategorie() {
        return Kategorie;
    }
    /**
     * Ändert die Kategorie auf den übergebenen Wert.
     * @param Kategorie der neue Wert
     */
    public void setKategorie(String Kategorie) {
        this.Kategorie=Kategorie;
    }

}
