package com.google;

import java.util.*;
import java.util.stream.Collectors;

public class VideoPlayer {

  private final VideoLibrary videoLibrary;

  // Added attributes to help complete tasks. "playingVideo" tracks whether or not there's a video playing, if there is one then "videoPlaying"
  // tracks which video is playing and playlistLibrary keeps track of all of the playlists.
  boolean playingVideo;
  Video videoPlaying = null;
  // The key is always going to be the lowercase version of the name so that any mutation of the playlist name doesn't create a duplicate.
  HashMap<String, VideoPlaylist> playlistLibrary = new HashMap<>();

  boolean playingPlaylist;
  VideoPlaylist playlistPlaying = null;
  int locationInPlaylist = 0;

  public VideoPlayer() {
    this.videoLibrary = new VideoLibrary();
    this.playingVideo = false;
    this.playingPlaylist = false;
  }

  public void numberOfVideos() {
    System.out.printf("%s videos in the library%n", videoLibrary.getVideos().size());
  }

  public void showAllVideos() {
    List<Video> videoList = videoLibrary.getVideos();
    // Sorting the list of videos using the Comparator class with the video title as the comparison.
    videoList.sort(Comparator.comparing(Video::getTitle));
    System.out.println("Here's a list of all available videos:");

    // Iterating through the list of videos using a "For-Each" loop.
    for (Video v : videoList) {
      System.out.println(v.getFormattedVideoString());
    }
  }

  public void playVideo(String videoId) {
    if (!videoExists(videoId))
      System.out.println("Cannot play video: Video does not exist");
    else {
      // Checking that the video hasn't been flagged before playing it
      if (videoLibrary.getVideo(videoId).flagged){
        System.out.println("Cannot play video: Video is currently flagged (reason: " + videoLibrary.getVideo(videoId).flaggedReason + ")");
      }
      else {
        // If there's a video playing then stop it.
        if (playingVideo) {
          System.out.println("Stopping video: " + videoPlaying.getTitle());
          playingVideo = false;
          videoPlaying = null;
        }

        // Start playing the requested video
        System.out.println("Playing video: " + videoLibrary.getVideo(videoId).getTitle());
        playingVideo = true;
        videoPlaying = videoLibrary.getVideo(videoId);
        videoLibrary.getVideo(videoId).paused = false;
      }
      }

  }

  public void stopVideo() {
    // Only stop video if there's a video to stop
    if (playingVideo) {
      System.out.println("Stopping video: " + videoPlaying.getTitle());
      videoPlaying.paused = false;
      playingVideo = false;
      videoPlaying = null;
    }
   else {
     System.out.println("Cannot stop video: No video is currently playing");
    }
  }

  public void playRandomVideo() {
    // Uses the Random class to generate a random number
    Random rand = new Random();
    // Uses streams and filter method to filter out videos that have been flagged
    List<Video> videoList = videoLibrary.getVideos().stream().filter(x -> !x.flagged).collect(Collectors.toList());
    if (!videoList.isEmpty()) {
      // Only generates a random number in the range of the available videos
      playVideo(videoList.get(rand.nextInt(videoList.size())).getVideoId());
    }
    else System.out.println("No videos available");
  }

  public void pauseVideo() {
    if (playingVideo) {
      if (videoPlaying.paused) System.out.println("Video already paused: " + videoPlaying.getTitle());
      else {
        System.out.println("Pausing video: " + videoPlaying.getTitle());
        videoPlaying.paused = true;
      }
    }
    else System.out.println("Cannot pause video: No video is currently playing");

  }

  public void continueVideo() {
    if (playingVideo) {
      // Only continue if the vide playing has been paused
      if (videoPlaying.paused){
        System.out.println("Continuing video: " + videoPlaying.getTitle());
        videoPlaying.paused = false;
      }
      else System.out.println("Cannot continue video: Video is not paused");
    }
    else System.out.println("Cannot continue video: No video is currently playing");

  }

  public void showPlaying() {
    if (playingVideo) {
      // Adding the "- PAUSED" to the end of the formatted string if the video playing has been paused
      if (videoPlaying.paused) System.out.println("Currently playing: " + videoPlaying.getFormattedVideoString() + " - PAUSED");
      else System.out.println("Currently playing: " + videoPlaying.getFormattedVideoString());
    }
    else System.out.println("No video is currently playing");
  }

  public void createPlaylist(String playlistName) {
    // If the playlist doesn't already exist then create it.
    if (!playlistExists(playlistName)) {
      playlistLibrary.put(playlistName.toLowerCase(), new VideoPlaylist(playlistName));
      System.out.println("Successfully created new playlist: " + playlistName);
    }
    else {
      System.out.println("Cannot create playlist: A playlist with the same name already exists");
    }
  }

  public void addVideoToPlaylist(String playlistName, String videoId) {
    if (playlistExists(playlistName)) {
      if (!videoExists(videoId)) System.out.println("Cannot add video to " + playlistName + ": Video does not exist");
      else {
        if (!videoLibrary.getVideo(videoId).flagged) {
          if (playlistLibrary.get(playlistName.toLowerCase()).getVideos().stream().noneMatch(z -> z.equals(videoLibrary.getVideo(videoId)))) {
            playlistLibrary.get(playlistName.toLowerCase()).addToPlaylist(videoLibrary.getVideo(videoId));
            System.out.println("Added video to " + playlistName + ": " + videoLibrary.getVideo(videoId).getTitle());
          }

          else System.out.println("Cannot add video to " + playlistName + ": Video already added");

        }
        else System.out.println("Cannot add video to " + playlistName + ": Video is currently flagged (reason: " + videoLibrary.getVideo(videoId).flaggedReason + ")");
      }
    }
    else {
      System.out.println("Cannot add video to " + playlistName + ": Playlist does not exist");

      System.out.println("Would you like to create the playlist '" + playlistName + "'? If yes, type 'y' otherwise we will assume you don't want to.");
      Scanner sc = new Scanner(System.in);
      String input = sc.nextLine();
      if (input.equals("y")) {
        createPlaylist(playlistName);
        addVideoToPlaylist(playlistName, videoId);
      }

    }
  }

  public void showAllPlaylists() {
    if (playlistLibrary.isEmpty()) System.out.println("No playlists exist yet");
    else {
      List<VideoPlaylist> playlists = new ArrayList<>(playlistLibrary.values());
      playlists.sort(Comparator.comparing(VideoPlaylist::getName));

      System.out.println("Showing all playlists:");
      for (VideoPlaylist vp : playlists) {
        String numberOfVideos = "";
        if (vp.getVideos().size() == 1) numberOfVideos = " (1 video)";
        else {
          numberOfVideos = " (" + vp.getVideos().size() + " videos)";
        }
        System.out.println(vp.getName() + numberOfVideos);
      }
    }
  }

  public void showPlaylist(String playlistName) {
    if (playlistExists(playlistName)) {
      System.out.println("Showing playlist: " + playlistName);
      if (playlistLibrary.get(playlistName.toLowerCase()).getVideos().isEmpty()) System.out.println("No videos here yet");
      else {
        for (Video v : playlistLibrary.get(playlistName.toLowerCase()).getVideos()) {
          System.out.println(v.getFormattedVideoString());
        }
      }
    }
    else {
      System.out.println("Cannot show playlist " + playlistName + ": Playlist does not exist");
    }
  }

  public void removeFromPlaylist(String playlistName, String videoId) {
    if (playlistExists(playlistName)) {
      if (videoLibrary.getVideos().stream().noneMatch(x -> x.getVideoId().equals(videoId))) System.out.println("Cannot remove video from " + playlistName + ": Video does not exist");
      else {
        videoLibrary.getVideos().stream()
                .filter(y -> y.getVideoId().equals(videoId))
                .forEach(y -> {
                  if (playlistLibrary.get(playlistName.toLowerCase()).getVideos().stream().anyMatch(z -> z.equals(y))) {
                    playlistLibrary.get(playlistName.toLowerCase()).removeFromPlaylist(y);
                    System.out.println("Removed video from " + playlistName + ": " + y.getTitle());
                  }

                  else {
                    System.out.println("Cannot remove video from " + playlistName + ": Video is not in playlist");
                  }});
      }
    }
    else {
      System.out.println("Cannot remove video from " + playlistName + ": Playlist does not exist");
    }
  }

  public void clearPlaylist(String playlistName) {
    if (playlistExists(playlistName)) {
      playlistLibrary.get(playlistName.toLowerCase()).clearPlaylist();
      System.out.println("Successfully removed all videos from " + playlistName);
    }
    else System.out.println("Cannot clear playlist " + playlistName +": Playlist does not exist");
  }

  public void deletePlaylist(String playlistName) {
    if (playlistExists(playlistName)) {
      playlistLibrary.remove(playlistName.toLowerCase());
      System.out.println("Deleted playlist: " + playlistName);
    }
    else System.out.println("Cannot delete playlist " + playlistName + ": Playlist does not exist");
  }

  public void searchVideos(String searchTerm) {
    List<Video> suggestions = videoLibrary.getVideos().stream().filter(y -> !y.flagged)
            .filter(x -> x.getTitle().toLowerCase().contains(searchTerm.toLowerCase())).collect(Collectors.toList());
    if (suggestions.isEmpty()) {
      System.out.println("No search results for " + searchTerm);
    }
    else {
      suggestions.sort(Comparator.comparing(Video::getTitle));
      System.out.println("Here are the results for " + searchTerm +":");
      for (int i = 0; i < suggestions.size(); i++) {
        System.out.println((i+1) + ") " + suggestions.get(i).getFormattedVideoString());
      }
      System.out.println("Would you like to play any of the above? If yes, specify the number of the video. " +
              "\nIf your answer is not a valid number, we will assume it's a no.");
      Scanner sc = new Scanner(System.in);
      try {
        int input = sc.nextInt();
        if ((input - 1) < suggestions.size() && (input - 1) >= 0) {
          playVideo(suggestions.get(input - 1).getVideoId());
        }
      }
      catch (Exception e){
      }
    }
  }

  public void searchVideosWithTag(String videoTag) {
    List<Video> suggestions = videoLibrary.getVideos().stream().filter(y -> !y.flagged)
            .filter(x -> x.getTags().contains(videoTag.toLowerCase())).collect(Collectors.toList());
    if (suggestions.isEmpty()) {
      System.out.println("No search results for " + videoTag);
    }
    else {
      suggestions.sort(Comparator.comparing(Video::getTitle));
      System.out.println("Here are the results for " + videoTag +":");
      for (int i = 0; i < suggestions.size(); i++) {
        System.out.println((i+1) + ") " + suggestions.get(i).getFormattedVideoString());
      }
      System.out.println("Would you like to play any of the above? If yes, specify the number of the video. " +
              "\nIf your answer is not a valid number, we will assume it's a no.");
      Scanner sc = new Scanner(System.in);
      try {
        int input = sc.nextInt();
        if ((input - 1) < suggestions.size() && (input - 1) >= 0) {
          playVideo(suggestions.get(input - 1).getVideoId());
        }
      }
      catch (Exception e){
      }
    }
  }

  public void flagVideo(String videoId) {
    if (videoExists(videoId)) {
      if (videoLibrary.getVideo(videoId).flagged) System.out.println("Cannot flag video: Video is already flagged");
      else {
        videoLibrary.getVideo(videoId).flagged = true;
        videoLibrary.getVideo(videoId).flaggedReason = "Not supplied";
        if (playingVideo && videoPlaying.equals(videoLibrary.getVideo(videoId))) stopVideo();
        System.out.println("Successfully flagged video: " + videoLibrary.getVideo(videoId).getTitle() + " (reason: Not supplied)");
      }

    }
    else System.out.println("Cannot flag video: Video does not exist");
  }

  public void flagVideo(String videoId, String reason) {
    if (videoExists(videoId)) {
      if (videoLibrary.getVideo(videoId).flagged) System.out.println("Cannot flag video: Video is already flagged");
      else {
        videoLibrary.getVideo(videoId).flagged = true;
        videoLibrary.getVideo(videoId).flaggedReason = reason;
        if (playingVideo && videoPlaying.equals(videoLibrary.getVideo(videoId))) stopVideo();
        System.out.println("Successfully flagged video: " + videoLibrary.getVideo(videoId).getTitle() + " (reason: " + reason + ")");
      }

    }
    else System.out.println("Cannot flag video: Video does not exist");
  }

  public void allowVideo(String videoId) {
    if (videoExists(videoId)) {
      if (videoLibrary.getVideo(videoId).flagged){
        videoLibrary.getVideo(videoId).flagged = false;
        System.out.println("Successfully removed flag from video: " + videoLibrary.getVideo(videoId).getTitle());
      }
      else System.out.println("Cannot remove flag from video: Video is not flagged");
    }
    else System.out.println("Cannot remove flag from video: Video does not exist");
  }

  // Two methods to check the existence of videos/playlists as the same line of code is used multiple times. Adheres to DRY code.
  public boolean videoExists(String videoId){
    // Using streams to see if any of the elements in the list of videos matches the videId
    if (videoLibrary.getVideos().stream().anyMatch(y -> y.getVideoId().equals(videoId))) return true;
    else return false;
  }

  public boolean playlistExists(String playlistName) {
    // Using the HashMap method containsKey to check if the playlist exists
    if (playlistLibrary.containsKey(playlistName.toLowerCase())) return true;
    else return false;
  }

  // Added method to solve the idea of playing a playlist in the want more section
  public void playPlaylist(String playlistName){
    if (playlistExists(playlistName)){
      if (playingPlaylist) {
        System.out.println("Stopping playlist: " + playlistName);
        playingPlaylist = false;
        playlistPlaying = null;
      }
      System.out.println("Playing playlist: " + playlistName);
      playingPlaylist = true;
      playlistPlaying = playlistLibrary.get(playlistName.toLowerCase());
      playVideo(playlistPlaying.getVideos().get(locationInPlaylist).getVideoId());
      locationInPlaylist += 1;
    }
    else System.out.println("Cannot play playlist '" + playlistName + "': Playlist does not exist");
  }

  // Plays the next video in the playlist
  public void nextVideo() {
    if (locationInPlaylist == playlistPlaying.getVideos().size()) {
      System.out.println("You are at the end of the playlist");

      System.out.println("Would you like to replay the playlist? If yes, type 'y' otherwise we will assume you don't want to.");
      Scanner sc = new Scanner(System.in);
      String input = sc.nextLine();
      if (input.equals("y")) {
        locationInPlaylist = 0;
        playPlaylist(playlistPlaying.getName().toLowerCase());
      }
      else {
        System.out.println("Stopping playlist: " + playlistPlaying.getName());
        playingPlaylist = false;
        playlistPlaying = null;
      }
    }
      else {
        playVideo(playlistPlaying.getVideos().get(locationInPlaylist).getVideoId());
        locationInPlaylist += 1;
      }
  }

  // Shows the current playlist playing
  public void showPlaylist(){
    if (playingPlaylist) {
      System.out.println("Currently playing from: " + playlistPlaying.getName());
    }
    else System.out.println("No playlist is currently playing");
  }
}