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
//Aufruf der Funktion, die nun alle Startnummern ermitteln wird
selectStartnummern($connection, getGestartet($connection));

//liest alle vorhandenen Startnummern aus der Datenbank ein und gibt diese aus
function selectStartnummern ($connection, $lauf) {
  //Abfrage zum Auslesen aller Startnummern
  if($lauf<2)
  $sqlStmt = "SELECT Startnummer FROM `namen` WHERE 1 ORDER BY CAST( `namen`.`Startnummer` AS SIGNED) ASC";
  else {
	  $sqlStmt = "SELECT Startnummer FROM `namen` WHERE `namen`.`Wiederholung_ersetzt` IS NOT NULL  ORDER BY CAST( `namen`.`Startnummer` AS SIGNED) ASC";
  }
  //Abfrage ausführen, Resultat in Variable result schreiben
  $result =  mysqli_query($connection,$sqlStmt);
  //falls Abfrage erfolgreich...
  if ($result = $connection->query($sqlStmt)) {
  	//für jede Zeile der Datenbankausgabe...
	while ($row = $result->fetch_assoc()) {
		  //die Startnummer ermitteln...
        $id = $row["Startnummer"];
        //und ausgeben.
        echo $id."|";  
      }
      //Ergebnisse leeren
$result->free();
//Verbindung schließen
  closeConnection($connection);
}
}

function getGestartet ($connection) {
  //Abfrage formulieren
  $sqlStmt = "SELECT Wert FROM `allgemein` WHERE `Attribut` = 'lauf'";
  //Abfrage vorbereiten
  $result =  mysqli_query($connection,$sqlStmt);
  //wenn Ergebnisse...
  if ($result = $connection->query($sqlStmt)) {
  		//... dann die Zahl dr Messstationen ausgeben (Zahl in Spalte "Wert" der ersten und einzigen gefundenen Zeile)
      //echo $result->fetch_assoc()["Wert"];
      return $result->fetch_assoc()["Wert"];
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