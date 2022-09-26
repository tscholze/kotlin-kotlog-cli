---
title: 'Twitch Team zu Markdown Converter in Python'
date: '2019-03-30'
tags:
    - Archive
---

**tl;dr**  
Ich habe diese Woche zum ersten mal mehr als eine Zeile Python geschrieben. Herausgekommen ist ein kleines Script welches alle Mitglieder eines Tiwtch Teams in eine Markdown-Datei ausgibt. Siehe [GitHub Repository.](https://github.com/tscholze/python-twitch-teams-to-markdown-converter)

**Hintergrund**  
Für mich als Entwickler im Umfeld von hart-typisierten Sprachen wie *Java*, *Swift*, *C#* stellen die interpretierten Artgenossen wie *JavaScript* oder *Python* immer sehr hippi-haft ungewohnte Welt dar.  
  
Da man aber nicht immer alles kategorisch ablehnen soll, habe ich mich diese Woche etwas näher mit *Python* (und *virtuelenv*) beschäftigt.

**Script**  
Da es mir immer leichter fällt Dinge anzusehen und zu lernen wenn ich einen Mehrwert im Ergebnis sehe, habe ich mich entschlossen, einen kleinen Konvertierer von Twtich-API-Daten zu Markdown zu schreiben.  
  
Das Script liest von der Twitch API ([Docs](https://dev.twitch.tv/docs/)) Informationen zu einem Team aus und wandelt diese mit Hilfe von Templates in eine Markdown-Datei um.  
  
Die Quelltexte dazu liegen wie immer auf meinem GitHub Profil im Repository *[python-twitch-teams-to-markdown-converter](https://github.com/tscholze/python-twitch-teams-to-markdown-converter)*.

**Beispielstabelle für Team: LiveCoders**
![](https://github.com/tscholze/python-twitch-teams-to-markdown-converter/blob/master/example.png?raw=true)