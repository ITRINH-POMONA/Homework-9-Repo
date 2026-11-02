package org.example;

import java.util.Arrays;
import java.util.List;

public class Driver {
    public static void main(String[] args) {
        List<Song> library = Arrays.asList(
                new Song(1, "Bohemian Rhapsody", "Queen", "A Night at the Opera", 354),
                new Song(2, "Love of My Life", "Queen", "A Night at the Opera", 219),
                new Song(3, "Hotel California", "Eagles", "Hotel California", 391),
                new Song(4, "Take It Easy", "Eagles", "Eagles", 212),
                new Song(5, "Billie Jean", "Michael Jackson", "Thriller", 294),
                new Song(6, "Thriller", "Michael Jackson", "Thriller", 357),
                new Song(7, "Imagine", "John Lennon", "Imagine", 183)
        );

        SongService server = new SongServiceImpl(library);
        SongService proxy = new SongServiceProxy(server);

        System.out.println("SearchById:");
        timed("searchById(3)  [1st call]", () -> proxy.searchById(3));
        timed("searchById(3)  [2nd call]", () -> proxy.searchById(3));
        timed("searchById(99) [not found]", () -> proxy.searchById(99));

        System.out.println("\nsearchByTitle:");
        timed("searchByTitle(\"Imagine\") [1st call]", () -> proxy.searchByTitle("Imagine"));
        timed("searchByTitle(\"Imagine\") [2nd call]", () -> proxy.searchByTitle("Imagine"));

        System.out.println("\nsearchByAlbum:");
        timed("searchByAlbum(\"Thriller\") [1st call]", () -> proxy.searchByAlbum("Thriller"));
        timed("searchByAlbum(\"Thriller\") [2nd call]", () -> proxy.searchByAlbum("Thriller"));

        System.out.println("\nSearch results also fill the ID cache:");
        timed("searchById(5) [cached by the album search]", () -> proxy.searchById(5));

        System.out.println("\nSame requests directly on the real server (no cache):");
        timed("server.searchById(3)  [1st call]", () -> server.searchById(3));
        timed("server.searchById(3)  [2nd call]", () -> server.searchById(3));
    }

    //  Runs a search, then prints its result and how long it took
    private static void timed(String label, java.util.function.Supplier<Object> search) {
        long start = System.nanoTime();
        Object result = search.get();
        long ms = (System.nanoTime() - start) / 1_000_000;
        System.out.println(label + " -> " + ms + " ms");
        System.out.println("    " + result);
    }
}
