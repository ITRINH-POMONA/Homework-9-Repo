import org.example.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ProxyTester {
    // Song test cases
    private final Song song = new Song(1, "Bohemian Rhapsody", "Queen", "A Night at the Opera", 354);

    @Test
    void constructorStoresAllFields() {
        assertNotNull(song);
        assertEquals(1, song.getSongID());
        assertEquals("Bohemian Rhapsody", song.getTitle());
        assertEquals("Queen", song.getArtist());
        assertEquals("A Night at the Opera", song.getAlbum());
        assertEquals(354, song.getDuration());
    }

    @Test
    void getSongIDReturnsId() {
        assertEquals(Integer.valueOf(1), song.getSongID());
    }

    @Test
    void getTitleReturnsTitle() {
        assertEquals("Bohemian Rhapsody", song.getTitle());
    }

    @Test
    void getArtistReturnsArtist() {
        assertEquals("Queen", song.getArtist());
    }

    @Test
    void getAlbumReturnsAlbum() {
        assertEquals("A Night at the Opera", song.getAlbum());
    }

    @Test
    void getDurationReturnsDuration() {
        assertEquals(354, song.getDuration());
    }

    @Test
    void toStringContainsAllFields() {
        assertEquals("Song{id=1, title='Bohemian Rhapsody', artist='Queen', "
                + "album='A Night at the Opera', duration=354s}", song.toString());
    }

}
