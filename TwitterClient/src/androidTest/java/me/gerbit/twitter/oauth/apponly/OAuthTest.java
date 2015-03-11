package me.gerbit.twitter.oauth.apponly;

import android.test.AndroidTestCase;

import java.io.IOException;

import me.gerbit.twitter.api.oauth.OAuth;

public class OAuthTest extends AndroidTestCase {

    public void testRequestBearerToken() {
        try {
            String bearer = OAuth.requestBearerToken();
            assertEquals(false, bearer.isEmpty());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

}
