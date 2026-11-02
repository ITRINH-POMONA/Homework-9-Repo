import org.example.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    // SongServiceProxy Test Cases
    // quick stand-in for actual server, no sleep calls for faster testing + counts how often it is called
    private static class CountingService implements SongService {
        final List<Song> songs;
        int idCalls, titleCalls, albumCalls;

        CountingService(Song... songs) {
            this.songs = Arrays.asList(songs);
        }

        @Override
        public Song searchById(Integer songID) {
            idCalls++;
            for (Song s : songs) if (s.getSongID().equals(songID)) return s;
            return null;
        }

        @Override
        public List<Song> searchByTitle(String title) {
            titleCalls++;
            List<Song> results = new ArrayList<>();
            for (Song s : songs) if (s.getTitle().equals(title)) results.add(s);
            return results;
        }

        @Override
        public List<Song> searchByAlbum(String album) {
            albumCalls++;
            List<Song> results = new ArrayList<>();
            for (Song s : songs) if (s.getAlbum().equals(album)) results.add(s);
            return results;
        }
    }

    private Song bohemian;
    private Song loveOfMyLife;
    private Song imagine;
    private CountingService server;
    private CountingService server2;
    private SongServiceProxy proxy;

    @BeforeEach
    void setUp() {
        bohemian = new Song(1, "Bohemian Rhapsody", "Queen", "A Night at the Opera", 354);
        loveOfMyLife = new Song(2, "Love of My Life", "Queen", "A Night at the Opera", 219);
        imagine = new Song(3, "Imagine", "John Lennon", "Imagine", 183);
        server = new CountingService(bohemian, loveOfMyLife, imagine);
        server2 = new CountingService(bohemian, loveOfMyLife, imagine);
        proxy = new SongServiceProxy(server);
    }

    // Constructor test case
    @Test
    void constructorCreatesProxyThatDelegatesToRealService() {
        assertNotNull(proxy);
        assertSame(imagine, proxy.searchById(3));
        assertEquals(1, server.idCalls);
    }

    // searchByID test cases
    @Test
    void searchByIdDelegatesToServerOnCacheMiss() {
        Song result = proxy.searchById(1);
        assertSame(bohemian, result);
        assertEquals(1, server.idCalls);
    }

    @Test
    void searchByIdServesRepeatRequestsFromCache() {
        proxy.searchById(1);
        Song again = proxy.searchById(1);
        assertSame(bohemian, again);
        assertEquals(1, server.idCalls);
    }

    @Test
    void searchByIdCachesEachIdSeparately() {
        proxy.searchById(1);
        proxy.searchById(2);
        assertEquals(2, server.idCalls);
        proxy.searchById(1);
        proxy.searchById(2);
        assertEquals(2, server.idCalls);
    }

    @Test
    void searchByIdReturnsNullAndDoesNotCacheMisses() {
        assertNull(proxy.searchById(99));
        assertNull(proxy.searchById(99));
        assertEquals(2, server.idCalls);   // asked the server both times
    }

    // searchByTitle test cases
    @Test
    void searchByTitleDelegatesToServerOnCacheMiss() {
        List<Song> results = proxy.searchByTitle("Imagine");
        assertEquals(1, results.size());
        assertSame(imagine, results.get(0));
        assertEquals(1, server.titleCalls);
    }

    @Test
    void searchByTitleServesRepeatRequestsFromCache() {
        proxy.searchByTitle("Imagine");
        List<Song> again = proxy.searchByTitle("Imagine");
        assertEquals(1, again.size());
        assertSame(imagine, again.get(0));
        assertEquals(1, server.titleCalls);
    }

    @Test
    void searchByTitleDoesNotCacheEmptyResults() {
        assertTrue(proxy.searchByTitle("No Such Song").isEmpty());
        assertTrue(proxy.searchByTitle("No Such Song").isEmpty());
        assertEquals(2, server.titleCalls);
    }

    @Test
    void searchByTitleCacheIsProtectedFromCallerModification() {
        List<Song> first = proxy.searchByTitle("Imagine");
        first.clear();                                    // caller mutates the returned list
        List<Song> second = proxy.searchByTitle("Imagine");
        assertEquals(1, second.size());
        second.clear();
        assertEquals(1, proxy.searchByTitle("Imagine").size());
        assertEquals(1, server.titleCalls);
    }

    @Test
    void searchByTitlePopulatesIdCache() {
        proxy.searchByTitle("Imagine");
        Song byId = proxy.searchById(3);
        assertSame(imagine, byId);
        assertEquals(0, server.idCalls);                  // answered from the cache
    }

    // searchByAlbum test cases
    @Test
    void searchByAlbumDelegatesToServerOnCacheMiss() {
        List<Song> results = proxy.searchByAlbum("A Night at the Opera");
        assertEquals(2, results.size());
        assertTrue(results.contains(bohemian));
        assertTrue(results.contains(loveOfMyLife));
        assertEquals(1, server.albumCalls);
    }

    @Test
    void searchByAlbumServesRepeatRequestsFromCache() {
        proxy.searchByAlbum("A Night at the Opera");
        List<Song> again = proxy.searchByAlbum("A Night at the Opera");
        assertEquals(2, again.size());
        assertEquals(1, server.albumCalls);
    }

    @Test
    void searchByAlbumDoesNotCacheEmptyResults() {
        assertTrue(proxy.searchByAlbum("No Such Album").isEmpty());
        assertTrue(proxy.searchByAlbum("No Such Album").isEmpty());
        assertEquals(2, server.albumCalls);
    }

    @Test
    void searchByAlbumCacheIsProtectedFromCallerModification() {
        List<Song> first = proxy.searchByAlbum("A Night at the Opera");
        first.clear();
        assertEquals(2, proxy.searchByAlbum("A Night at the Opera").size());
        assertEquals(1, server.albumCalls);
    }

    @Test
    void searchByAlbumPopulatesIdCache() {
        proxy.searchByAlbum("A Night at the Opera");
        assertSame(bohemian, proxy.searchById(1));
        assertSame(loveOfMyLife, proxy.searchById(2));
        assertEquals(0, server.idCalls);
    }

    // SongServiceImpl test cases
    @Test
    void constructorLoadsAllSongsIntoDatabase() {
        assertSame(bohemian, server2.searchById(1));
        assertSame(loveOfMyLife, server2.searchById(2));
        assertSame(imagine, server2.searchById(3));
    }

    // searchById unit test
    @Test
    void searchByIdReturnsNullWhenNotFound() {
        assertNull(server2.searchById(99));
    }

    // searchByTitle unit tests
    @Test
    void searchByTitleReturnsAllSongsWithSameTitle() {
        Song duplicateTitle = new Song(4, "Imagine", "Some Cover Band", "Covers", 200);
        SongServiceImpl s = new SongServiceImpl(Arrays.asList(imagine, duplicateTitle));

        List<Song> results = s.searchByTitle("Imagine");

        assertEquals(2, results.size());
        assertTrue(results.contains(imagine));
        assertTrue(results.contains(duplicateTitle));
    }

    @Test
    void searchByTitleReturnsEmptyListWhenNotFound() {
        List<Song> results = server2.searchByTitle("No Such Song");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    // searchByAlbum unit test
    @Test
    void searchByAlbumReturnsEmptyListWhenNotFound() {
        List<Song> results = server2.searchByAlbum("No Such Album");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}
