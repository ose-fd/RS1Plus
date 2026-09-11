<p align="center">
  <img src="images/logo.png" alt="RS1+ Logo" width="200">
</p>

## RS1+

RS1+ backports features from Refined Storage 2 to Refined Storage 1 v1.12.4 and adds panel variants of RS1's core devices.

## Backported from RS2

- Configuration Card: Copy and paste device configurations onto other devices of the same type. This includes redstone modes, filters, upgrades, and more.

- Clear Crafting Grid on Close: Automatically clears the crafting grid when closing it. This feature is disabled by default and can be enabled in the configuration.

- Wrench Support: Use a wrench on Refined Storage blocks to dismantle them. Wrenches from other mods should also work as long as they use the `#forge:wrenches` or `#forge:tools/wrench` tag.

## Panels

Panel variants of Refined Storage 1's devices that sit almost flat against any surface, including ceilings and floors, instead of sticking out.

The following panels are currently available:

- Grid Panel
- Crafting Grid Panel
- Fluid Grid Panel
- Pattern Grid Panel
- Crafter Manager Panel
- Crafting Monitor Panel
- Storage Monitor Panel

Panels connect to the network only through their back face.

Except for the Storage Monitor Panel, all panels are available in all 16 dye colors. Right-click a panel with a dye to recolor it.

You can switch between the panel and regular models at any time using a crafting table.

## Building

This project uses Gradle and includes the Gradle Wrapper.

### Windows

Run:

```bash
gradlew.bat build
Linux / macOS

Run:

./gradlew build

The compiled mod will be available in:

build/libs/
Development

To work on the project, open it in IntelliJ IDEA as a Gradle project.

The project includes the Gradle Wrapper, so you do not need to install Gradle manually.

Requirements
Minecraft 1.20.1
Minecraft Forge
Refined Storage 1 v1.12.4
Java
Version

1.0.0 - Initial Release

Credits

This project is based on and extends Refined Storage 1.

Features have been backported from Refined Storage 2 where applicable.

License

See LICENSE.txt for license information.