---
title: 'Mit PowerShell das Google Books Archive bereinigen'
date: '2020-02-15'
tags:
    - Archive
---

Auch wenn ich den Syntax von [PowerShell (Core)](https://docs.microsoft.com/en-us/powershell/scripting/overview?view=powershell-7) noch immer sehr gewöhnungsbedürftig finde, setze ich es doch immer vermehrter ein.  
  
Diesmal ging es darum, dass ich mein [Google Play Books](https://play.google.com/books) Archive (Takeaway) nach OneDrive kopieren wollte. Hierbei geht es um selbst hochgeladene Bücher zum Beispiel aus [humblebundle.com](https://www.humblebundle.com/) Käufen. Die Krux an der Sache ist, dass im Archive jedes Ebook in einem eigenen Ordner mit einer Beschreibungs-Html-Datei liegt. Für mich, der nur die einzelnen Bücher haben möchte stellte das bei über 200 Dateien ein kleines Problem da.  
  
Ein Nerd weiß sich zu behelfen – Automation ftw! Okay, natürlich googelte ich und stoß auf [einen Stackover Eintrag welcher genau dies tat](https://superuser.com/questions/1263485/how-to-copy-all-files-by-specified-extension-to-another-location-recursively). Community-Leben ist eben toll und lehrreich.  
  
Bit dem folgenden Befehl kopierte ich rekursiv alle Dateien mit der Endung *\*.epub* und *\*.pdf* aus dem Archiv in einen bereitgestellten OneDrive Ordner.

```
Get-ChildItem -Path "C:\Users\Tobias\Downloads\Takeout\Google Play Bücher\*" -Include <em>.epub,</em>.pdf -Recurse | Copy-Item -Destination "C:\Users\Tobias\OneDrive\Books"
```

Natürlich ist das ein sehr generischer Befehl. Dieser kann alles von nach egal wo kopieren. Ideal für meine kleine PowerShell Snippet Sammlung!