package me.gerbit.twitter.data;

import org.json.JSONException;
import org.json.JSONObject;

public final class SearchMetadata {

    private static final String REFRESH_URL_FIELD = "refresh_url";
    private static final String NEXT_RESULTS_FIELD = "next_results";
    
    private final String mRefreshUrl;
    private final String mNextResultsUrl;

    private SearchMetadata(final String refreshUrl,
                           final String nextResults) {
        mRefreshUrl = refreshUrl;
        mNextResultsUrl = nextResults;
    }

    public static SearchMetadata parse(final JSONObject obj) throws JSONException {
        return new SearchMetadata(
                obj.getString(REFRESH_URL_FIELD),
                obj.has(NEXT_RESULTS_FIELD) ? obj.getString(NEXT_RESULTS_FIELD) : "");
    }

    public String getRefreshUrl() {
        return mRefreshUrl;
    }

    public String getNextResultsUrl() {
        return mNextResultsUrl;
    }

    public boolean hasNextResults() {
        return !mNextResultsUrl.isEmpty();
    }

}
