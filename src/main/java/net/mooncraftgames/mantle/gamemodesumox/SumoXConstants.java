package net.mooncraftgames.mantle.gamemodesumox;

public final class SumoXConstants {

    /** 3 minutes base.*/
    public static final int BASE_TIMER_LEGNTH = 180;
    /** The proportion of the game that should have enhanced */
    public static final float BASE_TIMER_PANIC_ZONE = 1f / 3;

    /** The base knockback value. */
    public static final float KNOCKBACK_BASE = 0.4f;

    /** base * (1.05^finals_time) = compound 5% boost every second. */
    public static final float PANIC_KNOCKBACK_MULTIPLIER = 1.05f;

    /** Time in seconds to respawn. */
    public static final int DEFAULT_RESPAWN_SECONDS = 5;

    /** Lives before a player cannot respawn. */
    public static final int DEFAULT_LIVES = 3;


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

}
