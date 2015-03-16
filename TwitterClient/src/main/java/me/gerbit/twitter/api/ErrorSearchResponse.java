package me.gerbit.twitter.api;

import java.util.List;

import me.gerbit.twitter.data.SearchMetadata;
import me.gerbit.twitter.data.Tweet;

public final class ErrorSearchResponse implements SearchResponse {

    private final Error mError;

    ErrorSearchResponse(final Error err) {
        mError = err;
    }

    @Override
    public SearchMetadata getSearchMetadata() {
        return null;
    }

    @Override
    public List<Tweet> getTweetList() {
        return null;
    }

    @Override
    public boolean isError() {
        return true;
    }

    @Override
    public Error getError() {
        return mError;
    }
}
