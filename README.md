# Space Tower Defense

A space-themed tower defense game written in Java (Swing). Place towers along the path, upgrade them, and stop the waves of balloons before they get through.

## Download and play (Windows)

Go to the [latest release](../../releases/latest) and download one of these:

- **`SpaceTowerDefense-<version>-setup.exe`** (recommended): run it to install the game.
  - It installs for your user account only, so no admin rights are needed.
  - It adds a Start Menu entry and offers a desktop shortcut.
  - To uninstall it, go to **Settings → Apps**.
- **`SpaceTowerDefense-<version>-windows.zip`** (portable): unzip it anywhere and run `Space Tower Defense.exe`. Nothing gets installed.

Both downloads include their own copy of Java, so you don't need Java installed.

> **"Windows protected your PC"?** The game isn't code-signed, so Windows SmartScreen may warn you the first time you run it. Click **More info → Run anyway**.

## How to play

1. Click **Play** on the title screen. Then either start a new game by picking a difficulty and a map, or load a saved game with its 6-digit code.
2. Pick towers from the side bar and click on the map to place them. Towers can't go on the path or on obstacles.
3. Click a tower to upgrade it.
4. Click the wave button to send the next wave. You earn cash for every wave you clear.
5. If a balloon reaches the end of the path, you lose lives. When your lives reach 0, the game is over.

The whole game is played with the mouse.

### Difficulties

| Difficulty | Lives | Cash | Notes |
|---|---|---|---|
| Easy | 200 | 400 | |
| Medium | 100 | 300 | |
| Hard | 50 | 200 | Starts at wave 3 |
| Sandbox | Unlimited | Unlimited | Spawn any balloon or wave, and try any tower or upgrade. Games aren't saved. |

There are four built-in maps, plus a **random map** option that generates a new layout each time. It can produce some strange maps.

### Towers

| Tower | What it does |
|---|---|
| Shooter | A basic tower that shoots balloons. |
| Minigunner | Always shoots toward your mouse cursor. Its shots are blocked by obstacles. |
| Caster | A magic tower that grows stronger with upgrades. |
| Flyer | Launches a plane that shoots in many directions and can fire over obstacles. |
| Generator | Earns extra cash. |
| Booster | Boosts nearby towers once upgraded. |
| Deflator | Slows down balloons in its range. |

Each tower has more upgrades and details in the in-game Towers menu.

### Saving and loading

- Every new game (except Sandbox) gets a 6-digit **game code**, shown in the top-right corner while you play.
- Your progress is saved automatically after each wave. Write down your code, then enter it on the **Play** screen to continue later.
- Saves are stored in `%USERPROFILE%\.space_td\saves\` (one file per code). To move your saves to another computer, copy that folder.
- When you lose, that game's save is deleted.

## Building from source

You'll need [JDK 21](https://adoptium.net/) or newer. Gradle downloads itself through the included wrapper, so you don't need to install it.

```
.\gradlew run               # play the game
.\gradlew build             # build a runnable jar: build\libs\space_td.jar
java -jar build\libs\space_td.jar
```

To build the Windows downloads:

```
.\gradlew packageZip        # portable zip: build\distributions\
.\gradlew packageInstaller  # installer: build\jpackage\installer\ (requires WiX Toolset 3)
```

The jar should also run on macOS and Linux with Java 21 installed (`java -jar space_td.jar`), but only Windows has been tested.

## Credits

- **Developer:** Logesh Mahendran
- **Contributors:** Kishan Mahendran (most waves), Colson Puchala (helper)
- **Ideas:** Harjan Anand, Skye Trusler
- **Inspiration:** Bloons Tower Defense 6 by Ninja Kiwi. This is a fan-made project and isn't affiliated with or endorsed by Ninja Kiwi.
- **Layout library:** [TableLayout](https://mvnrepository.com/artifact/tablelayout/TableLayout) by Daniel Barbalace

### Music

- "Galactic Rap" (title screen) and "Ether Vox" (in-game) by Kevin MacLeod ([incompetech.com](https://incompetech.com))
- Licensed under [Creative Commons: By Attribution 4.0](http://creativecommons.org/licenses/by/4.0/)

### Assets

All of the game's other assets, including the artwork, sprites, icons and sound effects, were made by Logesh Mahendran.
