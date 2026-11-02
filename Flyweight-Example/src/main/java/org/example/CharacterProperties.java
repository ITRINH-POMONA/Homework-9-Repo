package org.example;

public final class CharacterProperties {
    private final String font;
    private final int size;
    private final String color;

    public CharacterProperties(String font, int size, String color) {
        this.font = font;
        this.size = size;
        this.color = color;
    }

    public String getFont()  { return font; }
    public int getSize()     { return size; }
    public String getColor() { return color; }

    @Override
    public String toString() {
        return font + ", " + size + ", " + color;
    }
}
