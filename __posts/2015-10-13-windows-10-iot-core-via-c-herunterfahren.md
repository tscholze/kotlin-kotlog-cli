---
title: 'Windows 10 IoT Core via C# herunterfahren'
date: '2015-10-13'
tags:
    - Archive
---

Diese Frage wurde im Technet Forum oft gestellt und meistens mit den Worten “Stecker ziehen” beantwortet. Das dies auch anders geht, zeigt der bekannte, deutsche Technet’er Koopakiller aka Tom Lambert (MVP, MCC, [Technet](https://social.msdn.microsoft.com/profile/koopakiller/), [Twitter](https://twitter.com/Koopakiller100ton)) in [seinem detaillierten Blogpost](http://code-13.net/Content/Blog/1252).

Generell ist es aber nicht schwer. Neben dem neuen Package Namespace

```
xmlns:iot="http://schemas.microsoft.com/appx/manifest/iot/windows10"

```

muss nur noch etwas an den Fähigkeiten der App geschraubt werden

```
<Capabilities>
    <Capability Name="internetClient" />
    <iot:Capability Name="systemManagement"/>
</Capabilities>

```

und schon kann man mit nur einer Zeile Quelltext das System zeitgesteuert ausschalten oder weniger drastisch neustarten.

```
ShutdownManager.BeginShutdown(ShutdownKind.Shutdown, TimeSpan.Zero);

```