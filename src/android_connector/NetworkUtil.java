/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android_connector;

import java.net.InetAddress; 
import java.net.NetworkInterface; 
import java.net.SocketException; 
import java.net.UnknownHostException; 
//übernommen von http://javabeginners.de/Netzwerk/Netzwerkdaten_ermitteln.php
/**
 * eine Klasse, deren Methoden Details über die Netzwerkkonfiguration auslesesen
 * @author javabeginners.de
 */
public class NetworkUtil { 
    /**
     * Liest die MAC-Adressen aller verbundenen Geräte aus.
     */
    public void printAllOwnerMacs() { 
        InetAddress[] ias; 
        try { 
            ias = InetAddress.getAllByName(getOwnerHostName()); 
            if (ias != null) 
                for (InetAddress ia : ias) { 
                    System.out.println(ia.getHostAddress()); 
                } 
        } catch (UnknownHostException e) { 
            System.err.println("Unbekannter Hostname"); 
        } 
    } 
    /**
     * Liest die Hostadresse des localhosts aus.
     * @return die Hostadresse
     */
    public String getOwnerHostName() { 
        try { 
            return InetAddress.getLocalHost().getHostName(); 
        } catch (UnknownHostException e) { 
            e.printStackTrace(); 
        } 
        return null; 
    } 
    /**
     * Liest den Computernamen des lokalen Rechners aus.
     * @return den Comutername
     */
    public String getOwnerNetworkDeviceName() { 
        try { 
            NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress 
                    .getLocalHost()); 
            if (ni != null) 
                return ni.getDisplayName(); 
        } catch (SocketException e) { 
            e.printStackTrace(); 
        } catch (UnknownHostException e) { 
            e.printStackTrace(); 
        } 
        return null; 
    } 
    /**
     * Liest die eigene MAC-Adresse aus
     * @return 
     */
    public String getOwnerMac() { 
        try { 
            NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress 
                    .getLocalHost()); 
            byte[] hwa = ni.getHardwareAddress(); 
            if (hwa == null) 
                return null; 
            String mac = ""; 
            for (int i = 0; i < hwa.length; i++) { 
                mac += String.format("%x:", hwa[i]); 
            } 
            if (mac.length() > 0 && !ni.isLoopback()) { 
                return mac.toLowerCase().substring(0, mac.length() - 1); 
            } 
        } catch (SocketException e) { 
            e.printStackTrace(); 
        } catch (UnknownHostException e) { 
            e.printStackTrace(); 
        } 
        return null; 
    } 
    /**
     * Ermittelt die eigene Netzwerkadresse.
     * @return die Netzwerkadresse
     */
    public String getOwnerIp() { 
        try { 
            return InetAddress.getLocalHost().getHostAddress(); 
        } catch (UnknownHostException e) { 
            e.printStackTrace(); 
        } 
        return null; 
    }
} 
