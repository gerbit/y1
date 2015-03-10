package me.gerbit.twitter.oauth.apponly;

import android.util.Base64;

import org.apache.http.HttpException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import me.gerbit.twitter.util.Utils;

public class OAuth {

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
    public static String requestBearerToken(String endPointUrl) throws IOException {
        HttpsURLConnection connection = null;

        try {
            URL url = new URL(endPointUrl);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Host", "api.twitter.com");
            connection.setRequestProperty("User-Agent", "GerbitTwitterClient");
//            connection.setRequestProperty("Authorization", "Basic TjlQYkg2T09WM1ZKNFFCdWd4MmFHQUNOVjpEdTdScmdKek9rc2hEc0RSNFBmaG4xT04wYWpXZ3U5 dzBqVlBRV3FxM3ZLUkxSU0NuZA==");
            connection.setRequestProperty("Authorization", "Basic " + encodeKeys(OAuthConfig.OAUTH_APPONLY_CONSUMER_KEY, OAuthConfig.OAUTH_APPONLY_CONSUMER_SECRET));
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            connection.setRequestProperty("Content-Length", "29");
            connection.setUseCaches(false);

            int res = Utils.write(connection, "grant_type=client_credentials");
            if (res == HttpsURLConnection.HTTP_OK) {
                JSONObject obj = new JSONObject(Utils.read(connection));
                String tokenType = obj.getString("token_type");
                String token = obj.getString("access_token");
                return ((tokenType.equals("bearer")) && (token != null)) ? token : "";
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
