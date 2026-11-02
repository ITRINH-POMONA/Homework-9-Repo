import org.example.*;
import org.junit.jupiter.api.Test;
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

}
