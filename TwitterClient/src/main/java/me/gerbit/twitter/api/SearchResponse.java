package me.gerbit.twitter.api;

import java.util.List;

import me.gerbit.twitter.data.SearchMetadata;
import me.gerbit.twitter.data.Tweet;

public interface SearchResponse {
    SearchMetadata getSearchMetadata();
    List<Tweet> getTweetList();
    boolean isError();
    Error getError();
}
