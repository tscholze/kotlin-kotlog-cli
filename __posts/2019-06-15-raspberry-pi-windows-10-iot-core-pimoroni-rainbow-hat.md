---
title: 'Raspberry Pi + Windows 10 IoT Core + Pimoroni Rainbow HAT = <3'
date: '2019-06-15'
tags:
    - Archive
---

**tl;dr**  
Nachdem ich letztens mein erstes [Windows 10 IoT Core](https://developer.microsoft.com/en-us/windows/iot) Projekt in Version 1.0 abgeschlossen habe, war diesmal nun der etwas kompliziertere [Pimoroni Rainbow HAT](https://shop.pimoroni.com/products/rainbow-hat-for-android-things) an der Reihe.

**Motivation**  
Ursprünglich war dieser HAT ein Demo-Kit um mit Android Things experimentieren zu können. Da dieses aber nun mehr oder weniger tot ist habe ich eine weitere Verwendung für das Stück Technik gesucht. Schließlich wurde ich bei einem Windows 10 IoT Port “fündig.  
  
**Features des HATs**  
Der Rainbow HAT für den Raspberry Pi verfügt über folgende Fähigkeiten:

- Große rot, grün und blaue LEDs unterhalb den Buttons
- Drei A,B,C Buttons
- Piezo Vibrationselement
- APA102 Baustein um sieben Farb-LEDs anzusteuern
- HT16K33 Baustein um vier 14-Segment anzeigen zu bespielen.
- BMP280 Baustein für die Temperatur- und Luftdrucksensoren

![](assets/ms-pi-rainbow.jpg)

**Lessons learned**  
Zu den Sensoren muss man sagen, dass diese nur einen Trend oder Indiz für die wirklichen Werte liefern können. So ist der Temperatursensor nur wenige Millimeter von der sich aufheizenden Prozessor des Pis entfernt. Somit sind die Messungen (leicht) verfälscht.  
  
Nichtsdestotrotz kann man hierbei super den Umgang mit GPIOs-, I2C- und PWM-Controllern erlernen.  
  
Ein Großteil der Logik basiert auf der originalen [Python-Implementierung](https://github.com/pimoroni/rainbow-hat/tree/master/library) von Pimoroni.

**Quelltext**  
Die mit vielen Kommentaren versehenen Quelltexte zur App liegen wie immer auf meinem GitHub Profil im Repository *[dotnet-iot-homebear-rainbow](https://github.com/tscholze/dotnet-iot-homebear-blinkt)*. Als auch als [Hackster.io Projekt](https://www.hackster.io/tscholze/homebear-rainbow-windows-10-iot-core-pimoroni-rainbowhat-f1d2dc) zu finden.