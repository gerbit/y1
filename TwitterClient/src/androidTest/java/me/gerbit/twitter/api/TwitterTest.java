package me.gerbit.twitter.api;

import android.test.AndroidTestCase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.gerbit.twitter.data.Tweet;

public class TwitterTest extends AndroidTestCase {

    public void testTweet() {
        String tweetRaw = "{\"metadata\":{\"result_type\":\"recent\",\"iso_language_code\":\"ru\"},\"created_at\":\"Sat Mar 14 15:36:59 +0000 2015\",\"id\":576768997917601793,\"id_str\":\"576768997917601793\",\"text\":\"\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD \uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD - 2 706 \uFFFD\uFFFD\uFFFD! \uFFFD\uFFFD \uFFFD\uFFFD\uFFFD\uFFFD \uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD \uFFFD\uFFFD\uFFFD\uFFFD \uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD!  http://t.co/lfWB80q5ry #android, #androidgames, #gameinsight\",\"source\":\"<a href=\\\"http://bit.ly/tribez_itw\\\" rel=\\\"nofollow\\\">The Tribez for Android</a>\",\"truncated\":false,\"in_reply_to_status_id\":null,\"in_reply_to_status_id_str\":null,\"in_reply_to_user_id\":null,\"in_reply_to_user_id_str\":null,\"in_reply_to_screen_name\":null,\"user\":{\"id\":936832190,\"id_str\":\"936832190\",\"name\":\"\uFFFD\uFFFD\uFFFD\uFFFD\",\"screen_name\":\"yurystadnik\",\"location\":\"\",\"profile_location\":null,\"description\":\"\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD \uFFFD\uFFFD\uFFFD-\uFFFD-\uFFFD\uFFFD\uFFFD\uFFFD, \uFFFD\uFFFD\uFFFD\uFFFD. \uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD \uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD, RuFollowBack.\",\"url\":null,\"entities\":{\"description\":{\"urls\":[]}},\"protected\":false,\"followers_count\":36,\"friends_count\":38,\"listed_count\":9,\"created_at\":\"Fri Nov 09 12:51:21 +0000 2012\",\"favourites_count\":0,\"utc_offset\":null,\"time_zone\":null,\"geo_enabled\":false,\"verified\":false,\"statuses_count\":5792,\"lang\":\"ru\",\"contributors_enabled\":false,\"is_translator\":false,\"is_translation_enabled\":false,\"profile_background_color\":\"C0DEED\",\"profile_background_image_url\":\"http://abs.twimg.com/images/themes/theme1/bg.png\",\"profile_background_image_url_https\":\"https://abs.twimg.com/images/themes/theme1/bg.png\",\"profile_background_tile\":false,\"profile_image_url\":\"http://pbs.twimg.com/profile_images/2826921015/9eee74e6990b7ad866971d2b014413a3_normal.png\",\"profile_image_url_https\":\"https://pbs.twimg.com/profile_images/2826921015/9eee74e6990b7ad866971d2b014413a3_normal.png\",\"profile_link_color\":\"0084B4\",\"profile_sidebar_border_color\":\"C0DEED\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"profile_text_color\":\"333333\",\"profile_use_background_image\":true,\"default_profile\":true,\"default_profile_image\":false,\"following\":null,\"follow_request_sent\":null,\"notifications\":null},\"geo\":null,\"coordinates\":null,\"place\":null,\"contributors\":null,\"retweet_count\":0,\"favorite_count\":0,\"entities\":{\"hashtags\":[{\"text\":\"android\",\"indices\":[80,88]},{\"text\":\"androidgames\",\"indices\":[90,103]},{\"text\":\"gameinsight\",\"indices\":[105,117]}],\"symbols\":[],\"user_mentions\":[],\"urls\":[{\"url\":\"http://t.co/lfWB80q5ry\",\"expanded_url\":\"http://gigam.es/imtw_Tribez\",\"display_url\":\"gigam.es/imtw_Tribez\",\"indices\":[57,79]}]},\"favorited\":false,\"retweeted\":false,\"possibly_sensitive\":false,\"lang\":\"ru\"}";

        try {
            JSONObject tweetJson = new JSONObject(tweetRaw);
            Tweet t = Tweet.parse(tweetJson);
            assertEquals(tweetJson.getLong("id"), t.getId());
            assertEquals(tweetJson.getString("text"), t.getText());

            assertEquals(tweetJson.getJSONObject("user").getLong("id"), t.getUser().getId());
            assertEquals(tweetJson.getJSONObject("user").getString("name"), t.getUser().getName());
            assertEquals(tweetJson.getJSONObject("user").getString("screen_name"), t.getUser().getScreenName());
        } catch (JSONException e) {
            fail(e.getMessage());
        }
    }

    public void testSearch() {
        SearchQuery query = SearchQuery.builder("#android")
                .lang("ru")
                .resultType(SearchQuery.ResultType.RECENT)
                .build();
        OkSearchResponse response = null;
        try {
            TwitterSearch twitterSearch = new TwitterSearch("1");
            response = twitterSearch.search(query);
            assertNotNull(response.getTweetList());
            assertNotNull(response.getSearchMetadata());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
