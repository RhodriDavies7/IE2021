package com.google;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** A class used to represent a video. */
class Video {

  private final String title;
  private final String videoId;
  private final List<String> tags;

  public boolean paused;
  public boolean flagged;
  public String flaggedReason;

  Video(String title, String videoId, List<String> tags) {
    this.title = title;
    this.videoId = videoId;
    this.tags = Collections.unmodifiableList(tags);

    // A video won't begin paused (that makes it sound like it's playing, but it isn't), flagged (not flagged then no reason)
    this.paused = false;
    this.flagged = false;
    this.flaggedReason = "";

  }

  /** Returns the title of the video. */
  String getTitle() {
    return title;
  }

  /** Returns the video id of the video. */
  String getVideoId() {
    return videoId;
  }

  /** Returns a readonly collection of the tags of the video. */
  List<String> getTags() {
    return tags;
  }

  // Returns string of object in the format "title (video_id) [tags]", used in the SHOW_ALL_VIDEOS command. If the video has been flagged then
  // it returns extra information saying it's been flagged and for what reason
  String getFormattedVideoString() {
    // I used streams to join all tags in the list
    if (flagged) return this.getTitle() + " (" + this.getVideoId() +
            ") [" + this.getTags().stream().map(Object::toString).collect(Collectors.joining(" ")).toString() +
            "] - FLAGGED (reason: " + flaggedReason + ")";

    else return this.getTitle() + " (" + this.getVideoId() + ") [" + this.getTags().stream().map(Object::toString).collect(Collectors.joining(" ")).toString() + "]";
  };
}
