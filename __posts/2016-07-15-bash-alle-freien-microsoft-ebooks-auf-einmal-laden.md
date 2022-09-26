---
title: 'Bash: Alle freien Microsoft eBooks auf einmal laden'
date: '2016-07-15'
tags:
    - Archive
---

Eric Lingman, seinerseits Director of the [Sale Excellence Blog](https://blogs.msdn.microsoft.com/mssmallbiz), hat nun in [einem Blogpost](https://blogs.msdn.microsoft.com/mssmallbiz/2016/07/10/free-thats-right-im-giving-away-millions-of-free-microsoft-ebooks-again-including-windows-10-office-365-office-2016-power-bi-azure-windows-8-1-office-2013-sharepoint-2016-sha/) eine viel Zahl an nun freien eBooks rund um Microsoft Technologien als Linkliste veröffentlicht.

![Download All eBooks](assets/ms-ebooks-laden.png)

Wer im stressigen Alltag nicht die Muse hat alles durchzusehen was man brauchen könnte sondern dies eher nachgelagert machen möchte hat Pech gehabt – es gibt laut [Blogpost](https://blogs.msdn.microsoft.com/mssmallbiz/2016/07/10/free-thats-right-im-giving-away-millions-of-free-microsoft-ebooks-again-including-windows-10-office-365-office-2016-power-bi-azure-windows-8-1-office-2013-sharepoint-2016-sha/) aus diversen Gründen keinen “Download all” Button.

Da nun aber selbst auch auf Windows eine Bash vorhanden ist, entsteht für Konsoleros nur der Zeitaufwand was es benötigt, drei Zeilen Bash-Befehle zu schreiben.

\#1. Wir laden die Linkliste

```
wget http://www.mssmallbiz.com/ericligman/Key_Shorts/MSFTFreeEbooks.txt
```

\#2. Wir laden alle verlinkten eBooks

```
wget -i MSFTFreeEbooks.txt
```

\#3. \*.pdf Suffix anhängen

```
for f in *; do mv "$f" "$f.pdf"; done
```

Nicht alle Links müssen funktionieren, so kann es durchaus sein, dass eure Bash, bzw. wget manchmal Fehler wirst. Der Großteil der Links funktionierte jedoch einwandfrei.

Quelle: [drwindows.de](http://www.drwindows.de/content/10552-uber-200-ebooks-microsoft-produkten-kostenlos.html)