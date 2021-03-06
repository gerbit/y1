package me.gerbit.twitter.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.gerbit.twitter.data.SearchMetadata;
import me.gerbit.twitter.data.Tweet;

public final class OkSearchResponse implements SearchResponse {

    private static final String SEARCH_METADATA_FIELD = "search_metadata";

    private static final String STATUSES_FIELD = "statuses";

    private final List<Tweet> mTweetList;

    private SearchMetadata mSearchMetadata;

    private OkSearchResponse(SearchMetadata metadata, List<Tweet> tweets) {
        mTweetList = tweets;
        mSearchMetadata = metadata;
    }

    public static OkSearchResponse parse(final JSONObject obj) throws JSONException {
        final JSONArray tweetsArray = obj.getJSONArray(STATUSES_FIELD);
        final int count = tweetsArray.length();
        final List<Tweet> tweets = new ArrayList<Tweet>(count);
        for (int i = 0; i < count; i++) {
            tweets.add(Tweet.parse(tweetsArray.getJSONObject(i)));
        }
        return new OkSearchResponse(
                SearchMetadata.parse(obj.getJSONObject(SEARCH_METADATA_FIELD)),
                tweets);
    }

    public SearchMetadata getSearchMetadata() {
        return mSearchMetadata;
    }

    public List<Tweet> getTweetList() {
        return Collections.unmodifiableList(mTweetList);
    }

    @Override
    public boolean isError() {
        return false;
    }

    @Override
    public Error getError() {
        return null;
    }
}
