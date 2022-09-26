---
title: 'Google Firebase Cloud Messaging Pager mit Android Things'
date: '2018-01-02'
tags:
    - Archive
---

Ich habe einmal meinen Weihnachtsurlaub genutzt um bisschen mit meinem Raspberry Pi und dem darauf installierten [Android Things](https://developer.android.com/things/index.html) zu spielen.

Als kleines, Ein-Tages-Projekt kam dort eine App heraus welche [Google Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging/) (FCM) Mitteilungen auf dem Sieben-Segement-Display des [Rainbow HATs](https://shop.pimoroni.com/products/rainbow-hat-for-android-things) anzeigt.

[![Schema](https://github.com/tscholze/java-android-things-firebase-pager/raw/master/docs/scheme.png)](https://github.com/tscholze/java-android-things-firebase-pager/raw/master/docs/scheme.png)

Hier wurde mir bewusst wie einfach ein Push Service unter Android funktionieren kann. Unter iOS ist dies absolut kein leichter Schritt da es Zertifikate, Registrierungen und Co. verlangt – also alles nicht gerade Maker- und Hobby-freundlich.

Weitere Informationen und der Source ist auf [Github.com](https://github.com/tscholze/java-android-things-firebase-pager) verfügbar.