---
title: 'Windows 10 IoT Core unter OS X auf einer SD Karte installieren'
date: '2015-07-19'
tags:
    - Archive
---

Microsoft hält sich sehr bedeckt wenn es darum geht das neue Microsoft Windows 10 IoT Core unter non-Windows-Systemen zu installieren. In Zeiten von OpenSource, Github und Co. stellt sich dies allerdings kein all zu großes Problem da.

Der Entwickler [t0x0](https://github.com/t0x0) hat auf Github ein kleines Python-Skript namens [‘ffu2img’](https://github.com/t0x0/random/wiki/ffu2img) veröffentlicht. Im Begleittext hält sich t0x0 auch nicht mit Kritik an der Dokumentation zum Dateityp \*.ffu seitens Microsoft zurück. Zu Fairness: Alles noch Beta obwohl der Typ schon etliche Jahre auf dem Buckel hat.

Um nun mit eurem OS X ein Windows 10 IoT Core Image auf eine SD Karte zu bringen müsst ihr es wie auch unter Windows erst einmal herunterladen. Hierzu nicht den Weg aus einem [vorherigen Post](http://www.codebuddies.de/2015/07/19/startschuss-windows-10-iot-auf-dem-raspberry-pi-2-installieren/) auf codebuddies.de nehmen da dieser kein \*.img-File herunterlädt sondern nur ein allumfassendes MSI-Paket. Allerdings benötigen wir für OS X ein einfaches Image-File. Deswegen bitte direkt bei [Microsoft Connect](https://connect.microsoft.com/windowsembeddedIoT/Downloads/DownloadDetails.aspx?DownloadID=57782) herunterladen. Es hat den Stand von der BUILD 2015 Konferenz und könnte irgendwann veraltet sein.

Hat man das \*.zip von den nicht all zu schnellen Servern geladen und entpackt kopiert man das von Github geladene Python-Skript in das Selbige.

Anschließend öffnet man in diesem Ordner noch ein Terminal um zu allererst das \*.ffu-File in ein \*.img-File zu konvertieren. Dies passiert mit folgenden Aufruf des Skriptes: 
```
python ffu2img.py Flash.FFU windows-iot.IMG
```
 Hierbei kann als Name für die Originaldatei von \*\*\*Flash.FUU\*\*\* abweichen.

Ist dies abgeschlossen muss die SD-Karte noch vorbereitet werden. Hierzu sucht man sich mittels `df -h` das passende Device zum Kartenleser heraus und hängt alle dazugehörigen Petitionen (fortlaufende Nummer hinter dem Device-Namen) mittels `umount /dev/<name><partNum>` aus.

Sobald dies geschehen ist kann man mit dem guten alten dd-Kommando das Image 1:1 auf die SD-Karte schreiben. Hierzu führt man ein 

```dd bs=1M if=windows-iot.img of=/dev/<name>``` 

aus. Achtung: hier keine Partitionsnummer angeben!

Mit `Strg+T`könnt ihr während des kopieren mehr über die Datenrate sowie über die noch zu kopierende Datenmenge erfahren. Der Kopiervorgang kann sich ziemlich hinziehen – in der Ruhe liegt die Geduld.

Den erfolgreichen Abschluss der Operation erkennt man an einer erfolgreichen Logmeldung im Terminal. Nun kann die SD-Karte wie gehabt in den Pi eingesetzt werden und das System gestartet werden.