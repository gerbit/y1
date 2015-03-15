package me.gerbit.twitter.data;

import org.json.JSONException;
import org.json.JSONObject;

public final class User {

    private final long mId;
    private final String mName;
    private final String mScreenName;
    private final String mProfileImageUrl;

    private User(final long id, final String name, final String screenName, final String profileImg) {
        mId = id;
        mName = name;
        mScreenName = screenName;
        mProfileImageUrl = profileImg;
    }

    public static User parse(final JSONObject obj) throws JSONException {
        return new User(obj.getLong("id"),
                obj.getString("name"),
                obj.getString("screen_name"),
                obj.getString("profile_image_url"));
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getScreenName() {
        return mScreenName;
    }

    public String getProfileImageUrl() {
        return mProfileImageUrl;
    }
}
