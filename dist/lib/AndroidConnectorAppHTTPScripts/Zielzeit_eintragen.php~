<?php
//Quelle: http://stefan-draeger-software.de/blog/android-app-mit-mysql-datenbank-verbinden/

//Konfiguration einlesen
require_once("Konfiguration.php");
//Datenbankverbindung aufbauen
$connection = mysqli_connect($host,$user,$passwort,$datenbank);
//auf Fehler prüfen
if (mysqli_connect_errno()) {
    echo mysql_errno($connection) . ": " . mysql_error($connection). "\n";
    die();
}
//Zielzeit übergebener Startnummer eintragen
zeiteintragen($connection);

//trägt die aktuelle Systemzeit als Zielzeit für die übergebene Startnummer ein.
function zeiteintragen ($connection) {
	//Tabelle schon initialisiert --> immer schon Wert da (NULL), also Update-Befehl zusammensetzten...
	$sqlStmt = "UPDATE `namen` SET `Zielzeit` = '".getMicrotime()."' WHERE `namen`.`Startnummer` = '".$_GET['startnummer']."';";
	//... und ausführen
	$result =  mysqli_query($connection,$sqlStmt);
	//Verbindung schließen
	closeConnection($connection); 
}
//gibt die aktuelle Zeit seit dem 01.01.1970 als Timestamp im Format "Sekunden.Millisekunden" zurück
function getMicrotime()
{
	 //Präzision erhöhen, aber: erst ab PHP 5 möglich
    if (version_compare(PHP_VERSION, '5.0.0', '<'))
    {
        return array_sum(explode(' ', microtime()));
    }
    return microtime(true);
}  

//Verbindung schließen.
function closeConnection($connection){
  mysqli_close($connection);
}
?>