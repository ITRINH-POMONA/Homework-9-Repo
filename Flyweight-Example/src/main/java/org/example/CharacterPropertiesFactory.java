package org.example;
import java.util.HashMap;
import java.util.Map;

public class CharacterPropertiesFactory {
    private static final Map<String, CharacterProperties> pool = new HashMap<>();

    private CharacterPropertiesFactory() {}

    public static CharacterProperties getProperties(String font, int size, String color) {
        String key = font + "|" + size + "|" + color;
        return pool.computeIfAbsent(key, k -> new CharacterProperties(font, size, color));
    }

    public static int getPoolSize() {
        return pool.size();
    }

}
