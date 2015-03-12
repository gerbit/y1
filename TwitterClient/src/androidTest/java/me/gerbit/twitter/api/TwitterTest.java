package me.gerbit.twitter.api;

import android.net.Uri;
import android.test.AndroidTestCase;

import org.json.JSONObject;

import java.io.IOException;

public class TwitterTest extends AndroidTestCase {
    public void testSearch() {
        SearchQuery query = SearchQuery.builder("#android")
                .lang("ru")
                .resultType(SearchQuery.ResultType.RECENT)
                .build();
        try {
            JSONObject json = Twitter.search(query.toString());
            assertEquals(true, json.has("search_metadata"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
