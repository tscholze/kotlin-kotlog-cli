---
title: 'Mit PowerShell + GitHub einen statischen Blog basteln'
date: '2020-06-13'
tags:
    - Archive
---

Ich wollte schon länger einmal mit GitHub Actions und PowerShell herum experimentieren. Nach etlichen Gedanken fand ich einen kleinen Usecase und somit ein kleines [Projekt zu Stande (GitHub)](https://github.com/tscholze/powershell-github-issue-blogger) womit ich beide verbinden kann.

**Um was geht es?**  
Es geht darum, dass ich mittels GitHub Issues, GitHub Actions und GitHub Pages unter der zu Hilfenahme von PowerShell eine Art Proof-Of-Concept erstellen wollte wie man damit einen statischen Blog basteln kann.

![](assets/ps-github-blog-1.png)

**Ablauf**  
Kurz gesagt, der User erstellt ein neues Issue welches den Inhalt des entsprechenden Blog-Beitrages enthält. Dies löst eine GitHub Action auf welche den bisherigen Blog auschecked, ein PowerShell Skript ausführt und die Änderungen wieder einchecked.

![](assets/ps-github-blog-2.png)

**Veröffentlichung**  
Diese Blog-Dateien liegen in einem von GitHub für Pages verwendeten Order und werden so mit gleich öffentlich zugänglich gemacht.

![](assets/ps-github-blog-3.png)

**Mehr**  
Den kompletten Artikel könnt ihr wie so oft bei [Dr. Windows lesen](https://www.drwindows.de/news/wir-basteln-uns-ein-blog-mit-github-issues-actions-pages-und-powershell). Der [Quelltext liegt auf GitHub](https://github.com/tscholze/powershell-github-issue-blogger) für euch bereit.