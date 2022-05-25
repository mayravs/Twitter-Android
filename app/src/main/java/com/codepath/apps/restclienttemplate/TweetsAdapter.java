package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.fragments.ReplyDialogFragment;
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public static final String TAG = "TweetsAdapter";

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
            itemView.setOnClickListener(this);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvName.setText(tweet.user.name);
            tvRetweetCount.setText(tweet.retweetCount+"");
            tvFavoriteCount.setText(tweet.favoriteCount+"");
            tvScreenName.setText("@" + tweet.user.screenName + " -- ");
            tvCreatedAt.setText(tweet.time);
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
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("Tweet", Parcels.wrap(tweet));
                    FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                    ReplyDialogFragment replyTweetDialogFragment = ReplyDialogFragment.newInstance("Some Title");
                    replyTweetDialogFragment.setArguments(bundle);
                    replyTweetDialogFragment.show(fm, "fragment_reply_dialog");
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

        @Override
        public void onClick(View view) {
            Log.d("TweetsAdapter", "in here");
            Tweet tweet = tweets.get(getAdapterPosition());
            Intent intent = new Intent(view.getContext(), TweetDetailActivity.class);
            intent.putExtra("tweet", Parcels.wrap(tweet));
            // animation for shared profile image
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) view.getContext(), (View) ivProfileImage, "profile");
            view.getContext().startActivity(intent, options.toBundle());
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
