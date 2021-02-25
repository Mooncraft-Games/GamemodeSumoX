# GamemodeSumoX: A Minecraft(Nukkit) Minigame
![Badge: Java](https://img.shields.io/badge/Java-8-red?style=for-the-badge)
![Dependency: NGAPI](https://img.shields.io/badge/Depend-NGAPI:_1.2-orange?style=for-the-badge)
[![Dependency: ScoreboardAPI](https://img.shields.io/badge/Depend-ScoreboardAPI:_1.0-green?style=for-the-badge)](https://github.com/LucGamesYT/ScoreboardAPI)

*Note: There's a modified ScoreboardAPI Jar found in the `libs` folder. This jar does not include nukkit within it.*

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

|Name            |Auto-Activate? | Summary                                                                                 |
|----------------|---------------|-----------------------------------------------------------------------------------------|
|Blindness       | ✔             |Makes the user blind for 10 seconds.                                                     |
|Immunity        | ✔             |Makes the user immune to KB for 7 seconds.                                               |
|KB Aura (Kaboom)| ❌            |Knocks any players within a 4 block radius of the user away from the user.               |
|Leap            | ❌            |Propels the user quickly in the direction they are facing (Always moving upward with Y.) |
|Recall          | ❌            |Rewinds user to their position 5 seconds ago. Kits can modify this time.                 |


### Kits

Kits were another upgrade that saw an upgrade from the original Sumo gamemode. In SumoX, 4 kits were implemented, each introducing a new approach to playing the game. Here's a list of each kit and their costs:

|Name     |Cost (Coins) |Summary                                                                                      |
|---------|-------------|---------------------------------------------------------------------------------------------|
|Slapper  |0 (Default)  |1.2x multiplier on attacks. Otherwise the simplest kit of the game.                          |
|Runner   |500          |1.5x quicker, takes 1.6x more knockback, and deals 0.8x knockback with hits. Weak but quick. |
|Timelord |1000         |More likely to get recalls with an extended length of 10 seconds for recalls. Takes 1.2x more knockback and deals 0.9x knockback with hits. A recovery-focused kit.|
|Archer   |1000         |Armed with a bow which deals knockback on arrow hits along with an 3x chance of getting a leap powerup. Melee KB dealt by the user is 0.6x the base. Furthermore, the kit takes 1.2x more KB.|


### Map Properties

Within the MapID file of every map, properties can be set within sections such as `strings`, `integers`, `floats`, and `switches`. Here are a few supported properties which can be edited on a per-map basis.

|Property                 |Type    |Default Value |Description                                                 |
|-------------------------|--------|--------------|------------------------------------------------------------|
|`game_length`            |Integer |180           |Sets the length of the games timer.                         |
|`respawn_seconds`        |Integer |5             |Sets the time to respawn after a life-loss.                 |
|`lives`                  |Integer |3             |Sets the amount of lives a player spawns with.              |
|`min_powerup_spawn_time` |Integer |20            |The minimum amount of seconds between powerup respawns.     |
|`var_powerup_spawn_time` |Integer |20            |The amount of seconds the powerup respawn time can vary by. |
|`base_game_speed`        |Float   |1.0           |A multiplier that changes how quick a player can move.      |


### Panic Mode + Other components
Panic Mode is an event that triggers within the last third of a sumo game (Last 60 seconds if using default timer value) in order to make the difficulty harder. As it progresses, it gradually increases the knockback recieved by all players by 1.02x every second. This leads to eliminations being dealt in 1 hit by the end of the game! 

Furthermore, there are constants for unimplemented anti-cheese measures such as:
 - **Tripping:** Players take random knockback after a while if they're not hitting other people.
 - **Fragile:** Players will take more KB if they run, thus increasing their timer is discouraged.

These concepts were omitted from the final game as they were a bit to aggressive and proceeded by the current Panic Mode event. In the case that people find that players are dragging out game timers, it may be an idea to try implementing these measures as a last resort. Feel free to make a pull request if so. 
