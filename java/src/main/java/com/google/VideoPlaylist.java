package com.google;

import java.util.ArrayList;
import java.util.List;

/** A class used to represent a Playlist */
class VideoPlaylist {

    private final String name;
    private List<Video> videos = new ArrayList<>();

    public VideoPlaylist(String name) {
        this.name = name;
    }

    // Returns the name of the playlist
    public String getName() {
        return this.name;
    }

    // Returns the list of videos in the playlist
    public List<Video> getVideos(){
        return this.videos;
    }

    // Adds a video to the playlist
    public void addToPlaylist(Video video){
        this.videos.add(video);
    }

    // Removes a video from the playlist. It does this by returning a new list without the video mentioned.
    public void removeFromPlaylist(Video video){
        List<Video> newVideoList = new ArrayList<>();
        for (Video v : videos) {
            if (!v.equals(video)) newVideoList.add(v);
        }
        this.videos = newVideoList;
    }

    // Clears all videos in the playlist by initialising the list to a new empty list.
    public void clearPlaylist() {
        this.videos = new ArrayList<>();
    }
}
