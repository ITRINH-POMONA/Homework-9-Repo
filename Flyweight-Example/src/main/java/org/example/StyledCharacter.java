package org.example;

public class StyledCharacter {
    private final char value;
    private final CharacterProperties properties;

    public StyledCharacter(char value, CharacterProperties properties) {
        this.value = value;
        this.properties = properties;
    }

    public char getValue()                     { return value; }
    public CharacterProperties getProperties() { return properties; }

    @Override
    public String toString() {
        return value + " [" + properties + "]";
    }


}
