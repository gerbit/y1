package me.gerbit.twitter.api.oauth;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import me.gerbit.twitter.api.TwitterConfig;
import me.gerbit.twitter.util.Utils;

public class OAuth {

    private static String sBearerToken = null;

    /** encodes consumer key and secret into a specially encoded set of credentials */
    private static String encodeKeys(String consumerKey, String consumerSecret) {
        try {
            String encodedConsumerKey = URLEncoder.encode(consumerKey, "UTF-8");
            String encodedConsumerSecret = URLEncoder.encode(consumerSecret, "UTF-8");

            String fullKey = encodedConsumerKey + ":" + encodedConsumerSecret;
            String code = Base64.encodeToString(fullKey.getBytes("UTF-8"), Base64.NO_WRAP);
            return code;
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    // makes a request to the POST oauth2 / token endpoint to exchange these credentials for a bearer token
    public static String requestBearerToken() throws IOException {
        if (sBearerToken != null) {
            return sBearerToken;
        }
        HttpsURLConnection connection = null;

        try {
            URL url = new URL(TwitterConfig.OAUTH_APPONLY_URL);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", TwitterConfig.AGENT);
            connection.setRequestProperty("Authorization", "Basic " + encodeKeys(TwitterConfig.OAUTH_APPONLY_CONSUMER_KEY, TwitterConfig.OAUTH_APPONLY_CONSUMER_SECRET));
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            connection.setRequestProperty("Content-Length", "29");
            connection.setUseCaches(false);

            connection.connect();

            boolean res = Utils.write(connection.getOutputStream(), "grant_type=client_credentials");
            if (res) {
                JSONObject obj = new JSONObject(Utils.read(connection.getInputStream()));
                String tokenType = obj.getString("token_type");
                String token = obj.getString("access_token");
                sBearerToken = ((tokenType.equals("bearer")) && (token != null)) ? token : "";
                return sBearerToken;
            }
            return "";
        }
        catch (MalformedURLException e) {
            throw new IOException("Invalid endpoint URL specified.", e);
        } catch (JSONException e) {
            throw new IOException("Invalid JSON response.", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
