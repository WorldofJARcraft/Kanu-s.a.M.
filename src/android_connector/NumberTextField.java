/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android_connector;

import javafx.scene.control.TextField;
/**
 * Klasse für ein Textfeld, das nur Zahlen akzeptiert.
 * @author Quelle: http://blog.axxg.de/javafx-textfield-beschraenken/
 */ 
public class NumberTextField extends TextField {
    //bei Eingaben aller Art werden nur Zahlen akzeptiert
    @Override public void replaceText(int start, int end, String text) {
           if (text.matches("[0-9]") || text == "") {
               super.replaceText(start, end, text);
           }
       }
     
       @Override public void replaceSelection(String text) {
           if (text.matches("[0-9]") || text == "") {
               super.replaceSelection(text);
           }
       }
 
}