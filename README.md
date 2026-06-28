![Logo](/screenshots/Logo.png)

# Cloud Client
Cloud Client is an open source Minecraft PvP client for 1.8.9 built on the Forge API. This is a revamped continuation of the original [Cloud Client](https://github.com/CloudClientDev) by DupliCAT, which has since been discontinued. All original credit goes to the original developers — this fork aims to keep the project alive and take it further.

## Downloading
- Download the mod from the Releases section here
- Download the mod using the [Installer](https://github.com/CloudClientDev/cloudinstaller)

## Screenshots
### TitleScreen
![TitleScreen](/screenshots/TitleScreen.png)
### HudEditor
![TitleScreen](/screenshots/HudEditor.png)
### ModMenu
![TitleScreen](/screenshots/ModMenu.png)

## Workspace Setup
1. Clone or download the repository using git or the zip download.
2. Open the `cloudclient` folder and copy the path.
3. Open a command prompt or terminal and navigate to that path.
```
cd C:\User\Desktop\cloudclient-main\<version>\cloudclient
```
4. Set up the workspace for your IDE:

**IntelliJ IDEA**
```
gradlew setupDecompWorkspace idea
```
**Eclipse**
```
gradlew setupDecompWorkspace eclipse
```
5. Open the project in your preferred IDE. Do not import it as a Gradle project.
6. To get Mixins working in a dev environment, add the following to your program arguments:
```
--tweakClass org.spongepowered.asm.launch.MixinTweaker --mixin mixins.cloudmc.json
```

## Building
1. Open the `cloudclient` folder and copy the path.
2. Open a command prompt or terminal and navigate to that path.
```
cd C:\User\Desktop\cloudclient-main\<version>\cloudclient
```
3. Run the build command:
```
gradlew build
```
The built jar will be at:
```
cloudclient\build\libs
```
4. Copy the `.jar` into your mods folder and launch Forge for 1.8.9.

## Contributing
Feel free to fork this project, make changes, and open a pull request to the development branch.

## License
This project is licensed under the GNU Lesser General Public License v3.0.

Permissions: Modification · Distribution · Private use

Conditions: License and copyright notice · State changes · Disclose source · Same license

## Credits
Original Cloud Client by [DupliCAT / CloudClientDev](https://github.com/CloudClientDev) — this project would not exist without their work.

This project also uses code from:
- [superblaubeere27](https://github.com/superblaubeere27) — Font Renderer
- [LaVache-FR](https://github.com/LaVache-FR) — AnimationUtil
- [Moulberry](https://github.com/Moulberry) — MotionBlur
