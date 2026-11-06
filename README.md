# Spawn Biome Picker

Choose which biome you spawn in when creating a new world.

**NeoForge and Fabric 1.21.1** · Singleplayer · [Modrinth](https://modrinth.com/mod/setspawnbiome) · [CurseForge](https://www.curseforge.com/minecraft/mc-mods/spawn-biome-picker)

## Features
- Searchable biome list on the **World** tab of the Create World screen
- Select several biomes at once (experimental)
- Configurable search radius
  - NeoForge: in-game config screen (Mods → Spawn Biome Picker → Config)
  - Fabric: edit `config/setspawnbiome.json`
- Translated into 13 languages

## Installation
1. Install NeoForge, or Fabric Loader and [Fabric API](https://modrinth.com/mod/fabric-api), for Minecraft 1.21.1
2. Put the mod jar into the `mods` folder
3. Create a new world, open the **World** tab and press **Spawn Biome**

No other libraries are needed.

## Notes
- The choice applies only when the world is created
- If the biome isn't found within the search radius, you spawn normally and get a chat message
- Cave biomes put you on the surface above them
- Singleplayer only: dedicated servers have no world creation screen

## Translations
Translations are welcome. Copy `en_us.json` from
`src/main/resources/assets/setspawnbiome/lang/` (inside the `neoforge` or `fabric` project folder), translate it and open a pull request. The lang files are the same in both projects, so please update both.

## Building
Each loader is a separate Gradle project:

- NeoForge: open `neoforge/SetSpawnBiome-1.21.1/` and run `./gradlew build`
- Fabric: open `fabric/spawn-biome-picker-1.21.1/` and run `./gradlew build`

The jar appears in `build/libs/` of the project you built.

## License
MIT, see `LICENSE`. Build template files are under the NeoForged MDK license, see `neoforge/SetSpawnBiome-1.21.1/TEMPLATE_LICENSE.txt`.