# Spawn Biome Picker

Choose which biome you spawn in when creating a new world.

**NeoForge 1.21.1** · Singleplayer · [Modrinth](https://modrinth.com/mod/setspawnbiome)

## Features
- Searchable biome list on the **World** tab of the Create World screen
- Select several biomes at once (experimental)
- Configurable search radius, with an in-game config screen (Mods → Spawn Biome Picker → Config)
- Translated into 13 languages

## Installation
1. Install NeoForge for Minecraft 1.21.1
2. Put the mod jar into the `mods` folder
3. Create a new world, open the **World** tab and press **Spawn Biome**

## Notes
- The choice applies only when the world is created
- If the biome isn't found within the search radius, you spawn normally and get a chat message
- Cave biomes put you on the surface above them

## Translations
Translations are welcome. Copy `en_us.json` from
`src/main/resources/assets/setspawnbiome/lang/`, translate it and open a pull request.

## Building
### ./gradlew build
The jar appears in `build/libs/`.

## License
MIT, see `LICENSE`. Build template files are under the NeoForged MDK license, see `TEMPLATE_LICENSE.txt`.
