package me.gerbit.twitter.api;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import me.gerbit.twitter.api.oauth.OAuth;
import me.gerbit.twitter.util.Utils;

public class TwitterSearch {

    private static final String TAG = TwitterSearch.class.getSimpleName();

    private static final int SEARCH_QUERY = 0;

    private static final String SEARCH_RESOURCE = "https://api.twitter.com/1.1/search/tweets.json";

    private Handler mHtHandler;

    public TwitterSearch(final String name) {
        final HandlerThread thread = new HandlerThread(name);
        thread.start();

        mHtHandler = new Handler(thread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SEARCH_QUERY:
                        try {
                            QueryHolder holder = (QueryHolder) msg.obj;
                            SearchResponse response = search(holder.query);
                            SearchCallback callback = holder.callback;
                            if (callback != null) {
                                if (!response.isError()) {
                                    holder.callback.onQueryComplete(response);
                                } else {
                                    holder.callback.onError(response.getError().getErrorCode(),
                                            response.getError().getErrorMessage());
                                }
                            }
                        } catch (IOException e) {
                            Log.w(TAG, e);
                        }
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };
    }

    public void quit() {
        mHtHandler.getLooper().quit();
    }

    public void search(SearchQuery q, SearchCallback callback) {
        mHtHandler.obtainMessage(SEARCH_QUERY, new QueryHolder(q.toString(), callback)).sendToTarget();
    }

    public void search(String q, SearchCallback callback) {
        mHtHandler.obtainMessage(SEARCH_QUERY, new QueryHolder(q.toString(), callback)).sendToTarget();
    }

    SearchResponse search(SearchQuery query) throws IOException {
        return search(query.toString());
    }

    SearchResponse search(String query) throws IOException {
        SearchResponse ret = null;
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
                JSONObject obj = new JSONObject(Utils.read(connection.getInputStream()));
                ret = OkSearchResponse.parse(obj);
            } else {
                Log.e(TAG, "Response " + responseCode);
                ret = new ErrorSearchResponse(new Error(responseCode, connection.getResponseMessage()));
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

    private static class QueryHolder {
        public String query;
        public SearchCallback callback;

        public QueryHolder(String query, SearchCallback callback) {
            this.query = query;
            this.callback = callback;
        }
    }
}
