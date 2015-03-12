package me.gerbit.twitter.data;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchMetadata {
    private int mCount;
    private String mRefreshUrl;

    private SearchMetadata() {}

    public static SearchMetadata parse(final JSONObject obj) throws JSONException {
        final SearchMetadata m = new SearchMetadata();
        m.mCount = obj.getInt("count");
        m.mRefreshUrl = obj.getString("refresh_url");
        return m;
    }

    public int getCount() {
        return mCount;
    }

    public String getRefreshUrl() {
        return mRefreshUrl;
    }
}
