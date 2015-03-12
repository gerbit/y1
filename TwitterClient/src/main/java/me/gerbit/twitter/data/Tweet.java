package me.gerbit.twitter.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Tweet {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);

    private int mId;

    private Date mCreatedAt;
    private String mText;
    private int mFavoriteCount;
    private int mRetweetCount;
    private User mUser;

    private Tweet() {
    }

    public static Tweet parse(final JSONObject obj) throws JSONException {
        final Tweet tweet = new Tweet();
        tweet.mId = obj.getInt("id");
        tweet.mCreatedAt = Tweet.parseDate(obj.getString("created_at"));
        tweet.mText = obj.getString("text");
        tweet.mFavoriteCount = obj.getInt("favorite_count");
        tweet.mRetweetCount = obj.getInt("retweet_count");
        tweet.mUser = User.parse(obj.getJSONObject("user"));
        return tweet;
    }

    private static Date parseDate(final String twitterDate) throws JSONException {
        final Date date;
        try {
            date = DATE_FORMAT.parse(twitterDate);
        } catch (ParseException e) {
            throw new JSONException(e.getMessage());
        }
        return date;
    }

    public int getId() {
        return mId;
    }

    public Date getCreatedAt() {
        return mCreatedAt;
    }

    public String getText() {
        return mText;
    }

    public int getFavoriteCount() {
        return mFavoriteCount;
    }

    public int getRetweetCount() {
        return mRetweetCount;
    }

    public User getUser() {
        return mUser;
    }
}
