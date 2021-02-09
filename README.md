# GamemodeSumoX: A Minecraft(Nukkit) Minigame
![Badge: Java](https://img.shields.io/badge/Java-8-red?style=for-the-badge)
![Dependency: Commons](https://img.shields.io/badge/Depend-NGAPI:_1.2-orange?style=for-the-badge)

A revised version of the original Sumo gamemode which was developed to test the original builds of NGAPI 1.0. This version adds major additions to gameplay such as powerups, a variety of kits, and "Panic Mode" to speed up the game if it drags on! The latest version of this game is now playable on the Mooncraft Games minigame server!

If you want to test it, you can find it in the duels lobby at:
**IP:** `pe.mooncraftskyblock.fun`
**Port:** `25348`

## How does the game work?

There are 2 registered gamemodes, each with a duplicate with touney mode enabled. Both the gamemodes use the same behaviours with the only difference being an altered player capacity (along with supported maps). All forms of damage are disabled and this game has a **Free-For-All** team layout.

The aim of the game is to knock other competing players out of the play-zone (Set by inverted deathboxes) by repeatedly hitting them using the floatier knockback of the game. Once the player is hit out of the play-zone, they must wait 5 seconds to respawn along with losing a life. Each player has 3 lives by default but this is configurable by the map however, in the case a player runs out of lives, they are permanently out of the game. The game continues until one of two objectives are met:

- The timer ran out (default 180 seconds | Map-Configurable!)
- All players except one had ran out of lives (Players leaving fall under this)

### Powerups

Furthermore, to add more spice to the game, powerups spots were added as point entities so that maps can specify where they want powerups to spawn. The powerup guardians would spawn every 20-40 seconds after a powerup spot had been left empty, after which players would need to punch a guardian to claim a powerup.

Powerups could either be forcefully activated on pickup or activated by an item given to the player on pickup. Here's a list of all the powerups currently found within SumoX 1.0:

|Name            |Auto-Activate? | Summary                          |
|----------------|---------------|----------------------------------|
|Blindness       | ✔             |Makes the user blind for 10 seconds.|
|Immunity        | ✔             |Makes the user immune to KB for 7 seconds.|
|KB Aura (Kaboom)| ❌            |Knocks any players within a 4 block radius of the user away from the user.|
|Leap            | ❌            |Propels the user quickly in the direction they are facing (Always moving upward with Y.) |
|Recall          | ❌            |Rewinds user to their position 5 seconds ago. Kits can modify this time. |
