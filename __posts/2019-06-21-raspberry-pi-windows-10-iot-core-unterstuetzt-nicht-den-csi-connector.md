---
title: 'Raspberry Pi + Windows 10 IoT Core unterstützt nicht den CSI Connector'
date: '2019-06-21'
tags:
    - Archive
---

**tl;dr**  
Diese Woche musste ich lernen, dass wohl doch nicht alles Plug-and-Play bei Windows als auch beim Pi ist. Die offizielle Kamera wird nicht unterstützt.

**Wieso, weshalb?**  
Nachdem ich lange versucht habe mittels den [UWP Beispielen von Microsoft](https://github.com/microsoft/Windows-universal-samples/tree/master/Samples/CameraStarterKit) die offizielle Raspberry Pi Kamera via dem [CSI](https://en.wikipedia.org/wiki/Camera_Serial_Interface)-Port anzusprechen, habe ich dich einmal auf SO nachgefragt wie es denn eigentlich richtig geht und wieso überhaupt das fehlschlägt.

![](assets/rpi-cis-connecter.jpg)
Basierend auf [Wikipedia Bild](https://de.wikipedia.org/wiki/Raspberry_Pi#/media/Datei:Raspberry_Pi_2_Model_B_v1.1_top_new_(bg_cut_out).jpg):

**Stackoverflow to the rescue** Das Stockoverflow Mitglied [*stormbolt*](https://raspberrypi.stackexchange.com/users/105250/stormbolter) hatte eine erklärend Antwort auf meine [Frage](https://raspberrypi.stackexchange.com/questions/99850/using-raspberry-pi-camera-csi-with-windows-10-iot-core?noredirect=1#comment164459_99850) im Raspberry Pi Bereich gehabt. Diese ist gleichzeitig demotivierend jedoch lässt diese mich auch zuversichtlich in die Zukunft blicken lässt:

> Is my understanding the main problem was the lack of open source drivers, and the unwillingness of Broadcom to release propietary drivers for windows IoT. Now that there are open source drivers for the CSI and DSI ports, it should be possible to port them to windows, if someone with the technical knowledge steps up to the task.

Stackoverflow [Post](https://raspberrypi.stackexchange.com/questions/99850/using-raspberry-pi-camera-csi-with-windows-10-iot-core?noredirect=1#comment164459_99850)

**Es is ja nicht das einzige …** 
Mal sehen was zu erst kommen wird, Raspberry Pi 4 oder CSI Support in Windows 10 IoT Core.