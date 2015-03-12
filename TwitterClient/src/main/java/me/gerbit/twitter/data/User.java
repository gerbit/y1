package me.gerbit.twitter.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class User {

    private int mId;
    private String mName;
    private String mScreenName;
    private URL mProfileImageUrl;

    private User() {

    }

    public static User parse(final JSONObject obj) throws JSONException {
        final User user = new User();
        user.mId = obj.getInt("id");
        user.mName = obj.getString("name");
        user.mScreenName = obj.getString("screen_name");
        try {
            user.mProfileImageUrl = new URL(obj.getString("profile_image_url"));
        } catch (MalformedURLException e) {
            throw new JSONException(e.getMessage());
        }
        return user;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getScreenName() {
        return mScreenName;
    }

    public URL getProfileImageUrl() {
        return mProfileImageUrl;
    }
}
