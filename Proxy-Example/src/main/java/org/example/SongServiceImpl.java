package org.example;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class SongServiceImpl implements SongService{
    private final Map<Integer, Song> database = new LinkedHashMap<>();

    public SongServiceImpl(List<Song> songs) {
        for (Song s : songs) {
            database.put(s.getSongID(), s);
        }
    }

    @Override
    public Song searchById(Integer songID) {
        simulateLatency();
        return database.get(songID);
    }

    @Override
    public List<Song> searchByTitle(String title) {
        simulateLatency();
        List<Song> results = new ArrayList<>();
        for (Song s : database.values()) {
            if (s.getTitle().equals(title)) results.add(s);
        }
        return results;
    }

    @Override
    public List<Song> searchByAlbum(String album) {
        simulateLatency();
        List<Song> results = new ArrayList<>();
        for (Song s : database.values()) {
            if (s.getAlbum().equals(album)) results.add(s);
        }
        return results;
    }

    private void simulateLatency() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {}
    }
}
