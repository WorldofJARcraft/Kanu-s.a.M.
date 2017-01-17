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
//übergebene Zeit eintragen
zeiteintragen($connection);

//trägt die aktuelle Zeit für die übergebene Startnummer in die gewählte Station ein.
function zeiteintragen ($connection) {
	//zuerst prüfen, ob Zeit schon vorhanden
	
	//Abfrage formulieren...
	//genaue Tabelle und einzutragende Startnummer werden per GET in der Adresse übergeben und hier eingesetzt
	//auslesen, ob für Startnummer schon Zeit eingetragen ist
	$sqlStmt = "SELECT * FROM `android_connect`.`messstation_".$_GET['station']."` WHERE `Startnummer` = ".$_GET['startnummer'].";";
	$result =  mysqli_query($connection,$sqlStmt);
	$nichtVorhanden = true;
	if ($result = $connection->query($sqlStmt)) {
  		//für jede Zeile der Datenbankausgabe...
		while ($row = $result->fetch_assoc()) {
			  //die Startnummer ermitteln...
    	    $id = $row["Startnummer"];
    	    //String muss null sein, wenn Startnummer noch nicht eingetragen --> ist er es nicht, ist ein Zeiteintrag also vorhanden
    	    if($id != null){
				$nichtVorhanden = false;    	    
    	    }  
      }	
	}
	//Insert- befehl funktioniert nur, wenn noch kein Eintrag für die entsprechende Startnummer vorliegt
	if($nichtVorhanden){
	//Abfrage formulieren...
	//genaue Tabelle und einzutragende Startnummer werden per GET in der Adresse übergeben und hier eingesetzt
	$sqlStmt = "INSERT INTO `android_connect`.`messstation_".$_GET['station']."` (`Startnummer`, `Zeitpunkt`) VALUES ('".$_GET['startnummer']."',".$_GET['strafe'].");";
	//... und ausführen
	$result =  mysqli_query($connection,$sqlStmt);
	//keine Ergebnisse, die zu betrachten wären
}
//Update-Befehl zum Überschreiben
else {
	//genaue Tabelle und einzutragende Startnummer werden per GET in der Adresse übergeben und hier eingesetzt
	$sqlStmt = "UPDATE `messstation_".$_GET['station']."` SET `Zeitpunkt` = '".$_GET['strafe']."' WHERE `messstation_".$_GET['station']."`.`Startnummer` = '".$_GET['startnummer']."';";
	//... und ausführen
	$result =  mysqli_query($connection,$sqlStmt);
}
	//Verbindung schließen
	closeConnection($connection);
  
}
  

//Verbindung schließen.
function closeConnection($connection){
  mysqli_close($connection);
}
?>