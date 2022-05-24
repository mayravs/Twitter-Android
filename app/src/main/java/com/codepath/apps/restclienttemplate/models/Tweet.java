package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {
    public String body;
    public String createdAt;
    public User user;
    public String mediaUrl;
    public boolean retweeted;
    public boolean favorited;
    public long id;
    public int favoriteCount;
    public int retweetCount;

    public Tweet(){}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        if(jsonObject.has("full_text")) {
            tweet.body = jsonObject.getString("full_text");
        } else {
            tweet.body = jsonObject.getString("text");
        }
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.mediaUrl = getMediaUrl(jsonObject.getJSONObject("entities"));
        tweet.id = jsonObject.getLong("id");
        tweet.favoriteCount = jsonObject.getInt("favorite_count");
        tweet.retweetCount = jsonObject.getInt("retweet_count");

        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i=0; i< jsonArray.length(); i++){
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public static String getMediaUrl(JSONObject jsonObject) throws JSONException {
        JSONArray media = jsonObject.has("media") ? jsonObject.getJSONArray("media") : null;
        String mediaUrl = "";
        if (media != null) {
            mediaUrl =  media.getJSONObject(0).getString("media_url_https");
        }
        Log.d("Tweet.java", "media URL found: " + mediaUrl);
        return mediaUrl;
    }

}
