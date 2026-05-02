# Reconnect Button

Client-side Fabric mod for Minecraft 1.21.11 that adds a `Reconnect` button to the disconnect screen.

## Features

- Adds a reconnect button under the vanilla server-list button.
- Remembers the last server when Minecraft starts connecting.
- Reconnects using the vanilla `ConnectScreen` flow.
- Prevents duplicate reconnect buttons on the disconnect screen.

## Requirements

- Minecraft 1.21.11
- Fabric Loader 0.19.2 or newer
- Fabric API 0.141.3+1.21.11
- Java 21

## Build

```powershell
.\gradlew.bat build
```

The compiled mod jar will be in:

```text
build/libs/Reconnect_Button-1.jar
```
