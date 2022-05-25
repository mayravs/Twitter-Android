package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.fragments.ReplyDialogFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class TweetDetailActivity extends AppCompatActivity {
    public static final String TAG = "TweetDetailsActivity";

    Tweet tweet;
    ImageView ivProfileImage;
    ImageView ivTweetImage;
    ImageButton btnRetweet;
    ImageButton btnFavorite;
    ImageButton btnReply;
    TextView tvName;
    TextView tvScreenName;
    TextView tvCreatedAt;
    TextView tvBody;
    TextView tvFavoriteCount;
    TextView tvRetweetCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        ivProfileImage = findViewById(R.id.ivProfile);
        ivTweetImage = findViewById(R.id.ivTweetImage);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnReply = findViewById(R.id.btnReply);
        btnRetweet = findViewById(R.id.btnRetweet);
        tvBody = findViewById(R.id.tvBody);
        tvName = findViewById(R.id.tvName);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvFavoriteCount = findViewById(R.id.tvFavoriteCount);
        tvRetweetCount = findViewById(R.id.tvRetweetCount);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        tvBody.setText(tweet.body);
        tvName.setText(tweet.user.name);
        tvRetweetCount.setText(tweet.retweetCount+"");
        tvFavoriteCount.setText(tweet.favoriteCount+"");
        tvScreenName.setText("@" + tweet.user.screenName + " -- ");
        tvCreatedAt.setText(tweet.time);
        Glide.with(this).load(tweet.user.profileImageUrl).transform(new RoundedCorners(100)).into(ivProfileImage);

        if (tweet.retweeted) {
            btnRetweet.setImageResource(R.drawable.ic_vector_retweet);
            btnRetweet.setColorFilter(Color.parseColor("#17BF63"));
        }
        if (tweet.favorited) {
            btnFavorite.setImageResource(R.drawable.ic_vector_heart);
            btnFavorite.setColorFilter(Color.parseColor("#ffe0245e"));
        }

        Log.d(TAG, tweet.mediaUrl + "");
        if (tweet.mediaUrl != "") {
            ivTweetImage.setVisibility(View.VISIBLE);
            Log.d(TAG, "loading media");
            Glide.with(this).load(tweet.mediaUrl).transform(new RoundedCorners(70)).into(ivTweetImage);
        } else {
            ivTweetImage.setVisibility(View.GONE);
        }

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("Tweet", Parcels.wrap(tweet));
                FragmentManager fm = (TweetDetailActivity.this).getSupportFragmentManager();
                ReplyDialogFragment replyTweetDialogFragment = ReplyDialogFragment.newInstance("Some Title");
                replyTweetDialogFragment.setArguments(bundle);
                replyTweetDialogFragment.show(fm, "fragment_reply_dialog");
            }
        });

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TwitterClient client = TwitterApp.getRestClient(TweetDetailActivity.this);

                if (!tweet.favorited) {
                    btnFavorite.setImageResource(R.drawable.ic_vector_heart);
                    btnFavorite.setColorFilter(Color.parseColor("#ffe0245e"));
                    tweet.favoriteCount += 1;
                    tvFavoriteCount.setText((tweet.favoriteCount+""));
                    tweet.favorited = !tweet.favorited;
                    client.favoriteTweet(tweet, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "Tweet favorited");
                        }
                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "Failed to favorite tweet");
                        }
                    });
                } else {
                    btnFavorite.setImageResource(R.drawable.ic_vector_heart_stroke);
                    btnFavorite.setColorFilter(Color.parseColor("#ffccd6dd"));
                    tweet.favoriteCount -= 1;
                    tvFavoriteCount.setText((tweet.favoriteCount+""));
                    tweet.favorited = !tweet.favorited;
                    client.unFavoriteTweet(tweet, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "Tweet favorited");
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "Failed to favorite tweet");
                        }
                    });
                }
            }
        });

        btnRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TwitterClient client = TwitterApp.getRestClient(TweetDetailActivity.this);

                if (!tweet.retweeted) {
                    btnRetweet.setImageResource(R.drawable.ic_vector_retweet);
                    btnRetweet.setColorFilter(Color.parseColor("#17BF63"));
                    tweet.retweetCount += 1;
                    tvRetweetCount.setText((tweet.retweetCount+""));
                    tweet.retweeted = !tweet.retweeted;
                    client.retweet(tweet, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "Tweet was retweeted");
                        }
                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "Failed to retweet");
                        }
                    });
                } else {
                    btnRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                    btnRetweet.setColorFilter(Color.parseColor("#ffccd6dd"));
                    tweet.retweetCount -= 1;
                    tvRetweetCount.setText((tweet.retweetCount+""));
                    tweet.retweeted = !tweet.retweeted;
                    client.unRetweet(tweet, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "Tweet was unRetweeted");
                        }
                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "Failed to unRetweet");
                        }
                    });
                }
            }
        });
    }
}