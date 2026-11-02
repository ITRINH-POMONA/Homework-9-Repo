import org.example.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FlyweightTester {

    // Test Cases for CharacterProperties
    private final CharacterProperties props = new CharacterProperties("Arial", 12, "Red");
    @Test
    void constructorStoresValues() {
        assertNotNull(props);
        assertEquals("Arial", props.getFont());
        assertEquals(12, props.getSize());
        assertEquals("Red", props.getColor());
    }

    @Test
    void getFontReturnsFont() {
        assertEquals("Arial", props.getFont());
    }

    @Test
    void getSizeReturnsSize() {
        assertEquals(12, props.getSize());
    }

    @Test
    void getColorReturnsColor() {
        assertEquals("Red", props.getColor());
    }

    @Test
    void toStringFormatsAllFields() {
        assertEquals("Arial, 12, Red", props.toString());
    }

    // Test cases for CharacterPropertiesFactory
    @Test
    void getPropertiesCreatesObjectWithRequestedValues() {
        CharacterProperties p = CharacterPropertiesFactory.getProperties("FactoryFontA", 101, "Teal");
        assertEquals("FactoryFontA", p.getFont());
        assertEquals(101, p.getSize());
        assertEquals("Teal", p.getColor());
    }

    @Test
    void getPropertiesReturnsSameInstanceForSameCombination() {
        CharacterProperties p1 = CharacterPropertiesFactory.getProperties("FactoryFontB", 102, "Pink");
        CharacterProperties p2 = CharacterPropertiesFactory.getProperties("FactoryFontB", 102, "Pink");
        assertSame(p1, p2);
    }

    @Test
    void getPropertiesReturnsDifferentInstancesForDifferentCombinations() {
        CharacterProperties base  = CharacterPropertiesFactory.getProperties("FactoryFontC", 103, "Gray");
        CharacterProperties font  = CharacterPropertiesFactory.getProperties("FactoryFontC2", 103, "Gray");
        CharacterProperties size  = CharacterPropertiesFactory.getProperties("FactoryFontC", 104, "Gray");
        CharacterProperties color = CharacterPropertiesFactory.getProperties("FactoryFontC", 103, "Gold");
        assertNotSame(base, font);
        assertNotSame(base, size);
        assertNotSame(base, color);
    }

    @Test
    void getPoolSizeGrowsOnlyForNewCombinations() {
        int before = CharacterPropertiesFactory.getPoolSize();

        CharacterPropertiesFactory.getProperties("FactoryFontD", 105, "Cyan");
        assertEquals(before + 1, CharacterPropertiesFactory.getPoolSize());

        CharacterPropertiesFactory.getProperties("FactoryFontD", 105, "Cyan");
        assertEquals(before + 1, CharacterPropertiesFactory.getPoolSize());

        CharacterPropertiesFactory.getProperties("FactoryFontD", 106, "Cyan");
        assertEquals(before + 2, CharacterPropertiesFactory.getPoolSize());
    }

    // Test cases for Styled Character, using variable props from previous tests
    private final StyledCharacter sc = new StyledCharacter('X', props);

    @Test
    void constructorStoresValueAndProperties() {
        assertEquals('X', sc.getValue());
        assertSame(props, sc.getProperties());
    }

    @Test
    void getValueReturnsCharacter() {
        assertEquals('X', sc.getValue());
    }

    @Test
    void getPropertiesReturnsSharedFlyweight() {
        StyledCharacter other = new StyledCharacter('Y', props);
        assertSame(sc.getProperties(), other.getProperties());
    }

    @Test
    void toStringShowsCharacterAndProperties() {
        assertEquals("X [Arial, 12, Red]", sc.toString());
    }

    // Test cases for Document Class
    private Document doc;

    @BeforeEach
    void setUp() {
        doc = new Document();
    }

    // addCharacter, length, and getText

    @Test
    void newDocumentIsEmpty() {
        assertEquals(0, doc.length());
        assertEquals("", doc.getText());
    }

    @Test
    void addCharacterAppendsInOrder() {
        doc.addCharacter('H', "Arial", 12, "Red");
        doc.addCharacter('i', "Calibri", 14, "Blue");
        assertEquals(2, doc.length());
        assertEquals("Hi", doc.getText());
    }

    @Test
    void addCharacterSharesFlyweightsForSameProperties() {
        int before = CharacterPropertiesFactory.getPoolSize();
        doc.addCharacter('a', "DocFontShared", 201, "Lime");
        doc.addCharacter('b', "DocFontShared", 201, "Lime");
        doc.addCharacter('c', "DocFontShared", 201, "Lime");
        assertEquals(3, doc.length());
        assertEquals(before + 1, CharacterPropertiesFactory.getPoolSize());
    }

    @Test
    void lengthReflectsAddsAndRemoves() {
        doc.addCharacter('a', "Arial", 12, "Red");
        doc.addCharacter('b', "Arial", 12, "Red");
        assertEquals(2, doc.length());
        doc.removeCharacter(0);
        assertEquals(1, doc.length());
    }

    @Test
    void getTextReturnsAllCharacters() {
        for (char c : "HelloWorld".toCharArray()) doc.addCharacter(c, "Arial", 12, "Red");
        assertEquals("HelloWorld", doc.getText());
    }

    // editCharacter test cases

    @Test
    void editCharacterReplacesCharacterAndFormatting() {
        doc.addCharacter('a', "Arial", 12, "Red");
        doc.addCharacter('b', "Arial", 12, "Red");
        doc.editCharacter(1, 'Z', "Verdana", 16, "Black");
        assertEquals("aZ", doc.getText());
    }

    @Test
    void editCharacterUsesSharedFlyweight() {
        doc.addCharacter('a', "Arial", 12, "Red");
        int before = CharacterPropertiesFactory.getPoolSize();
        doc.editCharacter(0, 'b', "Arial", 12, "Red");   // existing combination
        assertEquals(before, CharacterPropertiesFactory.getPoolSize());
    }

    @Test
    void editCharacterWithInvalidIndexThrows() {
        assertThrows(IndexOutOfBoundsException.class,
                () -> doc.editCharacter(0, 'x', "Arial", 12, "Red"));
        doc.addCharacter('a', "Arial", 12, "Red");
        assertThrows(IndexOutOfBoundsException.class,
                () -> doc.editCharacter(5, 'x', "Arial", 12, "Red"));
    }

    // removeCharacter test cases

    @Test
    void removeCharacterRemovesAtIndex() {
        for (char c : "abc".toCharArray()) doc.addCharacter(c, "Arial", 12, "Red");
        doc.removeCharacter(1);
        assertEquals("ac", doc.getText());
    }

    @Test
    void removeCharacterWithInvalidIndexThrows() {
        assertThrows(IndexOutOfBoundsException.class, () -> doc.removeCharacter(0));
    }

    // display test cases

    @Test
    void displayPrintsOneLinePerCharacter() {
        doc.addCharacter('H', "Arial", 12, "Red");
        doc.addCharacter('i', "Calibri", 14, "Blue");

        PrintStream original = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            doc.display();
        } finally {
            System.setOut(original);
        }

        String nl = System.lineSeparator();
        assertEquals("  H [Arial, 12, Red]" + nl + "  i [Calibri, 14, Blue]" + nl, captured.toString());
    }

    @Test
    void displayOnEmptyDocumentPrintsNothing() {
        PrintStream original = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            doc.display();
        } finally {
            System.setOut(original);
        }
        assertEquals("", captured.toString());
    }

    // save test cases

    @Test
    void saveWritesOneLinePerCharacter(@TempDir Path dir) throws IOException {
        doc.addCharacter('H', "Arial", 12, "Red");
        doc.addCharacter(',', "Verdana", 16, "Black");   // comma is safe because we save the char code
        Path file = dir.resolve("doc.txt");

        doc.save(file.toString());

        List<String> lines = Files.readAllLines(file);
        assertEquals(2, lines.size());
        assertEquals("72,Arial,12,Red", lines.get(0));
        assertEquals("44,Verdana,16,Black", lines.get(1));
    }

    @Test
    void saveEmptyDocumentCreatesEmptyFile(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("empty.txt");
        doc.save(file.toString());
        assertTrue(Files.exists(file));
        assertEquals(0, Files.size(file));
    }

    // load test cases

    @Test
    void loadRestoresSavedDocument(@TempDir Path dir) throws IOException {
        for (char c : "HelloWorldCS5800".toCharArray()) doc.addCharacter(c, "Arial", 12, "Red");
        Path file = dir.resolve("doc.txt");
        doc.save(file.toString());

        Document loaded = new Document();
        loaded.load(file.toString());

        assertEquals("HelloWorldCS5800", loaded.getText());
        assertEquals(doc.length(), loaded.length());
    }

    @Test
    void loadPreservesSpecialCharacters(@TempDir Path dir) throws IOException {
        doc.addCharacter(',', "Arial", 12, "Red");
        doc.addCharacter(' ', "Arial", 12, "Red");
        doc.addCharacter('\n', "Arial", 12, "Red");
        Path file = dir.resolve("special.txt");
        doc.save(file.toString());

        Document loaded = new Document();
        loaded.load(file.toString());

        assertEquals(", \n", loaded.getText());
    }

    @Test
    void loadReplacesExistingContent(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("doc.txt");
        Files.write(file, List.of("65,Arial,12,Red", "66,Calibri,14,Blue"));

        doc.addCharacter('z', "Arial", 12, "Red");
        doc.load(file.toString());

        assertEquals("AB", doc.getText());
        assertEquals(2, doc.length());
    }

    @Test
    void loadSkipsEmptyLines(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("blank.txt");
        Files.write(file, List.of("65,Arial,12,Red", "", "66,Arial,12,Red"));

        doc.load(file.toString());

        assertEquals("AB", doc.getText());
    }

    @Test
    void loadReusesFlyweights(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("doc.txt");
        Files.write(file, List.of("65,LoadFont,301,Mint", "66,LoadFont,301,Mint", "67,LoadFont,301,Mint"));
        int before = CharacterPropertiesFactory.getPoolSize();

        doc.load(file.toString());

        assertEquals(3, doc.length());
        assertEquals(before + 1, CharacterPropertiesFactory.getPoolSize());
    }

    @Test
    void loadMissingFileThrowsIOException(@TempDir Path dir) {
        Path missing = dir.resolve("does-not-exist.txt");
        assertThrows(IOException.class, () -> doc.load(missing.toString()));
    }


}
