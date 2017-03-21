<?php
//Quelle: http://stefan-draeger-software.de/blog/android-app-mit-mysql-datenbank-verbinden/ und http://dreamworker.de/showthread.php?27953-MySQL-ORDER-BY-Sortierung-der-Zahlen-nat%C3%BCrlich
//Konfiguration.php einlesen, enthält die nötigen Verbindungswerte!
require_once("Konfiguration.php");
//Datenbankverbindung aufbauen
$connection = mysqli_connect($host,$user,$passwort,$datenbank);
//prüfen, ob Verbindungsfehler aufgetreten sind
if (mysqli_connect_errno()) {
    echo mysql_errno($connection) . ": " . mysql_error($connection). "\n";
    die();
}
//Aufruf der Funktion, die nun die Laufnummer ermitteln wird
getLauf($connection);

/*
* Ermittelt die Nummer des aktuellen Laufs und gibt sie auf den Bildschirm unformatiert aus.
*/
function getLauf ($connection) {
  //Abfrage formulieren
  $sqlStmt = "SELECT Zeitpunkt FROM `messstation_".$_GET['tor']."` WHERE `messstation_".$_GET['tor']."`.`Startnummer` = '".$_GET['startnummer']."'";
  //Abfrage vorbereiten
  $result =  mysqli_query($connection,$sqlStmt);
  //wenn Ergebnisse...
  if ($result = $connection->query($sqlStmt)) {
  		//... dann die Zahl des Laufs ausgeben (Zahl in Spalte "Wert" der ersten und einzigen gefundenen Zeile, da Schlüssel nur 1 mal da)
      echo $result->fetch_assoc()["Zeitpunkt"];
  // Das Objekt wieder freigeben.
  //Ergebnisse leeren
$result->free();

  }
//Verbindung schließen
  closeConnection($connection);
}
//Verbindung schließen.
function closeConnection($connection){
  mysqli_close($connection);
}
?>