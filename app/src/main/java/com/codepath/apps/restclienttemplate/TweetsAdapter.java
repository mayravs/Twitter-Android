package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;


public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    Context context;
    List<Tweet> tweets;
    // pass in context and list of tweets

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // for each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get the data at position
        Tweet tweet = tweets.get(position);
        // bind the tweet with the viewholder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // define a viewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {

        public static final String TAG = "TweetsAdapter";
        private static final int SECOND_MILLIS = 1000;
        private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

        ImageView ivProfileImage;
        TextView tvScreenName;
        TextView tvBody;
        TextView tvCreatedAt;
        TextView tvName;
        TextView tvFavoriteCount;
        TextView tvRetweetCount;
        ImageView ivTweetImage;
        ImageButton btnFavorite;
        ImageButton btnRetweet;
        ImageButton btnReply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfile);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvName = itemView.findViewById(R.id.tvName);
            ivTweetImage = itemView.findViewById(R.id.ivTweetImage);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnRetweet = itemView.findViewById(R.id.btnRetweet);
            btnReply = itemView.findViewById(R.id.btnReply);
            tvFavoriteCount = itemView.findViewById(R.id.tvFavoriteCount);
            tvRetweetCount = itemView.findViewById(R.id.tvRetweetCount);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvName.setText(tweet.user.name);
            tvRetweetCount.setText(tweet.retweetCount+"");
            tvFavoriteCount.setText(tweet.favoriteCount+"");
            tvScreenName.setText("@" + tweet.user.screenName + " -- ");
            tvCreatedAt.setText(getRelativeTimeAgo(tweet.createdAt));
            Glide.with(context).load(tweet.user.profileImageUrl).transform(new RoundedCorners(100)).into(ivProfileImage);

            if (tweet.retweeted) {
                btnRetweet.setImageResource(R.drawable.ic_vector_retweet);
                btnRetweet.setColorFilter(Color.parseColor("#17BF63"));
            }
            if (tweet.favorited) {
                btnFavorite.setImageResource(R.drawable.ic_vector_heart);
                btnFavorite.setColorFilter(Color.parseColor("#ffe0245e"));
            }

            Log.d("TweetsAdapter", tweet.mediaUrl + "");
            if (tweet.mediaUrl != "") {
                ivTweetImage.setVisibility(View.VISIBLE);
                Log.d("TweetsAdapter", "loading media");
                Glide.with(context).load(tweet.mediaUrl).transform(new RoundedCorners(70)).into(ivTweetImage);
            } else {
                ivTweetImage.setVisibility(View.GONE);
            }

            btnReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Replying to tweet");
                }
            });

            btnFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TwitterClient client = TwitterApp.getRestClient(itemView.getContext());

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
                    TwitterClient client = TwitterApp.getRestClient(itemView.getContext());

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

        public String getRelativeTimeAgo(String rawJsonDate) {
            String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
            SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            sf.setLenient(true);

            try {
                long time = sf.parse(rawJsonDate).getTime();
                long now = System.currentTimeMillis();

                final long diff = now - time;
                if (diff < MINUTE_MILLIS) {
                    return "just now";
                } else if (diff < 2 * MINUTE_MILLIS) {
                    return "a minute ago";
                } else if (diff < 50 * MINUTE_MILLIS) {
                    return diff / MINUTE_MILLIS + "m";
                } else if (diff < 90 * MINUTE_MILLIS) {
                    return "an hour ago";
                } else if (diff < 24 * HOUR_MILLIS) {
                    return diff / HOUR_MILLIS + "h";
                } else if (diff < 48 * HOUR_MILLIS) {
                    return "yesterday";
                } else {
                    return diff / DAY_MILLIS + "d";
                }
            } catch (ParseException e) {
                Log.i("TweetsAdapter", "getRelativeTimeAgo failed");
                e.printStackTrace();
            }

            return "";
        }
    }


    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }
}
