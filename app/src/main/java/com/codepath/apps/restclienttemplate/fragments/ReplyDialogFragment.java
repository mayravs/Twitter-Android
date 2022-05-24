package com.codepath.apps.restclienttemplate.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ReplyDialogFragment extends DialogFragment {

    private EditText etReply;
    private TextView tvRemain;
    private Button btnReplyNow;
    private TextView tvReplyTo;
    public static final int MAX_TWEET_LENGTH = 280;
    Tweet tweet;
    TwitterClient client;

    public ReplyDialogFragment() {}

    public static ReplyDialogFragment newInstance(String title) {
        ReplyDialogFragment frag = new ReplyDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        tweet = Parcels.unwrap(getArguments().getParcelable("Tweet"));
        return inflater.inflate(R.layout.fragment_reply_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvRemain = view.findViewById(R.id.tvRemain);
        btnReplyNow = view.findViewById(R.id.btnReplyNow);
        etReply = view.findViewById(R.id.etReplyTweet);
        tvReplyTo = view.findViewById(R.id.tvReplyTo);
        client = TwitterApp.getRestClient(getActivity());
        tvReplyTo.setText("Replying to @" + tweet.user.screenName);

        etReply.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (MAX_TWEET_LENGTH - editable.toString().length() < 0) {
                    tvRemain.setTextColor(Color.RED);
                }
                tvRemain.setText(MAX_TWEET_LENGTH - editable.toString().length() + "/140");
            }
        });


        btnReplyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetContent = etReply.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(getContext(), "Sorry, your tweet cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(getContext(), "Sorry, your tweet is too long", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Make an API call to Twitter to tweet
                TwitterClient client = TwitterApp.getRestClient(getContext());
                client.replyTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i("ReplyDialog", "OnSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i("ReplyDialog", "Published tweet says: " + tweet.body);
                            etReply.setText("");
                            dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e("ReplyDialog", "onFailure to publish Tweet", throwable);
                    }
                }, tweet);
            }
        });
    }
}