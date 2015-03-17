package me.gerbit.twitter.data;

import org.json.JSONException;
import org.json.JSONObject;

public final class SearchMetadata {

    private static final String REFRESH_URL_FIELD = "refresh_url";
    private static final String NEXT_RESULTS_FIELD = "next_results";
    private static final String QUERY_FIELD = "query";

    private final String mRefreshUrl;
    private final String mNextResultsUrl;
    private final String mQuery;

    private SearchMetadata(final String refreshUrl,
                           final String nextResults,
                           final String query) {
        mRefreshUrl = refreshUrl;
        mNextResultsUrl = nextResults;
        mQuery = query;
    }

    public static SearchMetadata parse(final JSONObject obj) throws JSONException {
        return new SearchMetadata(
                obj.getString(REFRESH_URL_FIELD),
                obj.has(NEXT_RESULTS_FIELD) ? obj.getString(NEXT_RESULTS_FIELD) : "",
                obj.getString(QUERY_FIELD));
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

    public String getQuery() {
        return mQuery;
    }
}
