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
    // Testing for CharacterProperties
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


}
