package me.gerbit.twitter.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import me.gerbit.twitter.api.oauth.OAuth;
import me.gerbit.twitter.util.Utils;

public class Twitter {

    private static final String SEARCH_RESOURCE = "https://api.twitter.com/1.1/search/tweets.json";

    public static JSONObject search(SearchQuery query) throws IOException {
        return search(query.toString());
    }

    public static JSONObject search(String query) throws IOException {
        JSONObject ret = null;
        HttpsURLConnection connection = null;

        try {
            String bearerToken = OAuth.requestBearerToken();
            URL url = new URL(SEARCH_RESOURCE + query);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", TwitterConfig.AGENT);
            connection.setRequestProperty("Authorization", "Bearer " + bearerToken);
            connection.setUseCaches(false);

            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                ret = new JSONObject(Utils.read(connection.getInputStream()));
            }
        } catch  (MalformedURLException e) {
            throw new IOException("Invalid endpoint URL specified.", e);
        } catch (JSONException e) {
            throw new IOException("Invalid JSON response.", e);
        } catch (ProtocolException e) {
            throw new IOException("Protocol", e);
        } catch (IOException e) {
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return ret;
    }
}
