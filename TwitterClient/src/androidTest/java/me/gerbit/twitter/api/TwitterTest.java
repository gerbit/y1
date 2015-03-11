package me.gerbit.twitter.api;

import android.net.Uri;
import android.test.AndroidTestCase;

import org.json.JSONObject;

import java.io.IOException;

public class TwitterTest extends AndroidTestCase {
    public void testSearch() {
        Uri.Builder builder = new Uri.Builder();
        builder.appendQueryParameter("q", "#android");
        try {
            JSONObject json = Twitter.search(builder.build().toString());
            assertEquals(true, json.has("search_metadata"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
