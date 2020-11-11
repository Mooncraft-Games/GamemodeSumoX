package net.mooncraftgames.mantle.gamemodesumox;

import java.util.Optional;

public class SumoUtil {

    public static Optional<Float> StringToFloat(String in) {
        try {
            return Optional.of(Float.parseFloat(in));
        } catch (Exception err){
            return Optional.empty();
        }
    }

    public static Optional<Integer> StringToInt(String in) {
        try {
            return Optional.of(Integer.parseInt(in));
        } catch (Exception err){
            return Optional.empty();
        }
    }

}
