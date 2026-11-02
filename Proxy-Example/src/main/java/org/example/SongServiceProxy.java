package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongServiceProxy implements SongService{
    private final SongService realService;
    private final Map<Integer, Song> idCache = new HashMap<>();
    private final Map<String, List<Song>> titleCache = new HashMap<>();
    private final Map<String, List<Song>> albumCache = new HashMap<>();

    public SongServiceProxy(SongService realService) {
        this.realService = realService;
    }

    @Override
    public Song searchById(Integer songID) {
        Song cached = idCache.get(songID);
        if (cached != null) return cached;

        Song song = realService.searchById(songID);
        if (song != null) idCache.put(songID, song);
        return song;
    }

    @Override
    public List<Song> searchByTitle(String title) {
        List<Song> cached = titleCache.get(title);
        if (cached != null) return new ArrayList<>(cached);

        List<Song> songs = realService.searchByTitle(title);
        if (!songs.isEmpty()) {
            titleCache.put(title, new ArrayList<>(songs));
            cacheById(songs);
        }
        return songs;
    }

    @Override
    public List<Song> searchByAlbum(String album) {
        List<Song> cached = albumCache.get(album);
        if (cached != null) return new ArrayList<>(cached);

        List<Song> songs = realService.searchByAlbum(album);
        if (!songs.isEmpty()) {
            albumCache.put(album, new ArrayList<>(songs));
            cacheById(songs);
        }
        return songs;
    }

    // Songs returned by any search can also serve later searchById calls
    private void cacheById(List<Song> songs) {
        for (Song s : songs) idCache.put(s.getSongID(), s);
    }

}
