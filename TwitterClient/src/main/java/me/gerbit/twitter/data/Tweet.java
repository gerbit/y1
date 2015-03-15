package me.gerbit.twitter.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class Tweet {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);

    private static final String ID_FIELD = "id";
    private static final String CREATED_AT_FIELD = "created_at";
    private static final String TEXT_FIELD = "text";
    private static final String FAVORITE_COUNT_FIELD = "favorite_count";
    private static final String RETWEET_COUNT_FIELD = "retweet_count";
    private static final String USER_FIELD = "user";

    private final long mId;
    private final Date mCreatedAt;
    private final String mText;
    private final int mFavoriteCount;
    private final int mRetweetCount;
    private final User mUser;

    private Tweet(final long id,
                  final Date date,
                  final String text,
                  final int favCount,
                  final int retwCount,
                  final User user) {
        mId = id;
        mCreatedAt = date;
        mText = text;
        mFavoriteCount = favCount;
        mRetweetCount = retwCount;
        mUser = user;
    }

    public static Tweet parse(final JSONObject obj) throws JSONException {
        return new Tweet(
            obj.getLong(ID_FIELD),
            parseDate(obj.getString(CREATED_AT_FIELD)),
            obj.getString(TEXT_FIELD),
            obj.getInt(FAVORITE_COUNT_FIELD),
            obj.getInt(RETWEET_COUNT_FIELD),
            User.parse(obj.getJSONObject(USER_FIELD)));
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

    public long getId() {
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
