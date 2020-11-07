package net.mooncraftgames.mantle.gamemodesumox;

import net.mooncraftgames.mantle.gamemodesumox.powerup.PowerUp;
import net.mooncraftgames.mantle.gamemodesumox.powerup.PowerUpBlindness;
import net.mooncraftgames.mantle.gamemodesumox.powerup.PowerUpImmunity;
import net.mooncraftgames.mantle.gamemodesumox.powerup.PowerUpLeap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SumoXConstants {

    /** 3 minutes base.*/
    public static final int BASE_TIMER_LEGNTH = 180;

    /** The proportion of the game that should have enhanced */
    public static final float BASE_TIMER_PANIC_ZONE = 1f / 3;

    /** The base knockback value. */
    public static final float KNOCKBACK_BASE = 0.6f;

    /** base * (1.05^finals_time) = compound 5% boost every second. */
    public static final float PANIC_KNOCKBACK_MULTIPLIER = 1.05f;

    /** Time in seconds to respawn. */
    public static final int DEFAULT_RESPAWN_SECONDS = 5;

    /** Lives before a player cannot respawn. */
    public static final int DEFAULT_LIVES = 3;

    /** The minimum amount of time between power up spawns. */
    public static final int DEFAULT_MIN_POWERUP_SPAWN_TIME = 20;

    /** The amount of extra time that can be added on between powerup spawns. */
    public static final int DEFAULT_VARIATION_POWERUP_SPAWN_TIME = 20;

    public static final int POWERUP_ANIMATE_TICK_INTERVAL = 2;
    public static final float POWERUP_ANIMATE_TICK_SPEED = 4.5f;

    // -- POWER-UPS --

    public static final float POWERUP_LEAP_STRENGTH = 1.5f;


    // -- ANTI-CAMP --
    //TODO: Decide on one method or intergrate them all.

    /** Does not apply to tripping, a player that doesn't get hit takes more knockback the longer they run. */
    public static final float UNTOUCHED_FRAGILE_MULTIPLIER = 1.01f;
    /**Cap the amount of knockback that can be taken */
    public static final float UNTOUCHED_FRAGILE_CAP = 1f;

    /** In seconds, how much time must pass in order to let people "trip" (aka random knockback).*/
    public static final int UNTOUCHED_TRIP_THRESHOLD = 40;
    /** A*/
    public static final float UNTOUCHED_TRIP_FINAL_MULTIPLIER = 0.5f;

    public static final List<Class<? extends PowerUp>> AVAILABLE_POWER_UPS;


    static {
        ArrayList<Class<? extends PowerUp>> puss = new ArrayList<>();

        puss.add(PowerUpBlindness.class);
        puss.add(PowerUpImmunity.class);
        puss.add(PowerUpLeap.class);

        AVAILABLE_POWER_UPS = Collections.unmodifiableList(puss);
    }

}
