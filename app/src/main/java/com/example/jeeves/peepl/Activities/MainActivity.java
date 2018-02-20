package com.example.jeeves.peepl.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jeeves.peepl.Classes.InstagramScavenger;

import com.example.jeeves.peepl.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureLayout();
    }

    private void configureLayout() {

        ImageView imageView = findViewById(R.id.beef);
        imageView.setBackgroundColor(Color.rgb(101, 160, 172));

        TextView textView = findViewById(R.id.peepl_text);
        Typeface myCustomFont = Typeface.createFromAsset(getAssets(), "fonts/CrossTown.ttf");
        textView.setTypeface(myCustomFont);

        ImageButton searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Search.class));
            }
        });
    }

    protected List<String> sendInstagramApiRequest() {


        String requestUrl = "https://api.instagram.com/v1/users/1255194646/media/recent/?access_token=1255194646.e985a2a.8e6d85e2e2c24b1cbe6f7a0036486349";
        InstagramScavenger instagramScavenger = new InstagramScavenger();

        JSONObject instagramData;
        List<String> userPictureText;
        try
        {
            instagramData = instagramScavenger.execute(requestUrl).get();
            userPictureText = instagramScavenger.getPostedTextFromPictures(instagramData);
            return userPictureText;
        }
        catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}