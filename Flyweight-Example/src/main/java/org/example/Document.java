package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Document {
    private final List<StyledCharacter> characters = new ArrayList<>();

    public void addCharacter(char c, String font, int size, String color) {
        characters.add(new StyledCharacter(c, CharacterPropertiesFactory.getProperties(font, size, color)));
    }

    public void editCharacter(int index, char c, String font, int size, String color) {
        characters.set(index, new StyledCharacter(c, CharacterPropertiesFactory.getProperties(font, size, color)));
    }

    public void removeCharacter(int index) {
        characters.remove(index);
    }

    public int length() { return characters.size(); }

    public String getText() {
        StringBuilder sb = new StringBuilder();
        for (StyledCharacter sc : characters) sb.append(sc.getValue());
        return sb.toString();
    }

    public void display() {
        for (StyledCharacter sc : characters) System.out.println("  " + sc);
    }

    public void save(String filename) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            for (StyledCharacter sc : characters) {
                CharacterProperties p = sc.getProperties();
                out.println((int) sc.getValue() + "," + p.getFont() + "," + p.getSize() + "," + p.getColor());
            }
        }
    }

    public void load(String filename) throws IOException {
        characters.clear();
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                addCharacter((char) Integer.parseInt(parts[0]), parts[1],
                        Integer.parseInt(parts[2]), parts[3]);
            }
        }
    }
}
