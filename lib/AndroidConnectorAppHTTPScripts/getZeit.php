<?php
//Quelle: http://stefan-draeger-software.de/blog/android-app-mit-mysql-datenbank-verbinden/

//Ausgabe der aktuellen Zeit
echo getMicrotime();

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
?>