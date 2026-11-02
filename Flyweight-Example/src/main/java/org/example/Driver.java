package org.example;

import java.io.IOException;

public class Driver {
    public static void main(String[] args) throws IOException {
        Document doc = new Document();

        String text = "HelloWorldCS5800";
        // 4 distinct property combinations (font, size, color)
        String[] fonts  = {"Arial",  "Calibri", "Verdana", "Arial"};
        int[] sizes     = {12,       14,        16,        16};
        String[] colors = {"Red",    "Blue",    "Black",   "Blue"};

        for (int i = 0; i < text.length(); i++) {
            int v = i % 4;
            doc.addCharacter(text.charAt(i), fonts[v], sizes[v], colors[v]);
        }

        System.out.println("Original document: " + doc.getText());
        doc.display();
        System.out.println("Characters: " + doc.length()
                + " | Flyweight objects created: " + CharacterPropertiesFactory.getPoolSize());

        // Edit example: change the first character's formatting
        doc.editCharacter(0, 'H', "Verdana", 16, "Black");

        // Save and load
        String file = "document.txt";
        doc.save(file);
        System.out.println("\nSaved to " + file);

        Document loaded = new Document();
        loaded.load(file);
        System.out.println("Loaded document: " + loaded.getText());
        loaded.display();
        System.out.println("Characters: " + loaded.length()
                + " | Flyweight objects in pool: " + CharacterPropertiesFactory.getPoolSize());

    }
}
