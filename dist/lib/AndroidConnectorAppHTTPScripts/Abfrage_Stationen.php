<?php
//Quelle: http://stefan-draeger-software.de/blog/android-app-mit-mysql-datenbank-verbinden/

require_once("Konfiguration.php");
//Datenbankverbindung aufbauen
$connection = mysqli_connect($host,$user,$passwort,$datenbank);
//prüfen, ob Fehler aufgetreten sind
if (mysqli_connect_errno()) {
    echo mysql_errno($connection) . ": " . mysql_error($connection). "\n";
    die();
}
//Zahl der Messstationen auslesen
getMessstationencount($connection);

//liest aus der Tabelle "allgemein" die Zahl der Messstationen aus
function getMessstationencount ($connection) {
  //Abfrage formulieren
  $sqlStmt = "SELECT Wert FROM `allgemein` WHERE `Attribut` = 'Zahl_Stationen'";
  //Abfrage vorbereiten
  $result =  mysqli_query($connection,$sqlStmt);
  //wenn Ergebnisse...
  if ($result = $connection->query($sqlStmt)) {
  		//... dann die Zahl dr Messstationen ausgeben (Zahl in Spalte "Wert" der ersten und einzigen gefundenen Zeile)
      echo $result->fetch_assoc()["Wert"];
      
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