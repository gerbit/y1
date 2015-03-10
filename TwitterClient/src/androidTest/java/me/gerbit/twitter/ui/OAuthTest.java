package me.gerbit.twitter.ui;

import android.test.AndroidTestCase;

import java.io.IOException;

import me.gerbit.twitter.oauth.apponly.OAuth;
import me.gerbit.twitter.oauth.apponly.OAuthConfig;

public class OAuthTest extends AndroidTestCase {

    public void testRequestBearerToken() {
        try {
            String bearer = OAuth.requestBearerToken(OAuthConfig.OAUTH_APPONLY_URL);
            assertEquals(false, bearer.isEmpty());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

}
