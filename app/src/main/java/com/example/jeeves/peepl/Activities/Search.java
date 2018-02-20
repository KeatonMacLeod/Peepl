package com.example.jeeves.peepl.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.example.jeeves.peepl.Classes.ContentEntityPair;
import com.example.jeeves.peepl.Classes.InstagramScavenger;
import com.example.jeeves.peepl.Classes.OpenNLPProcessorAsync;
import com.example.jeeves.peepl.Classes.OpenNLPTrainer;
import com.example.jeeves.peepl.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Search extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        configureButtons();
    }

    private void configureButtons() {

        ImageButton homeButton = findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SearchView searchBar = findViewById(R.id.search_bar);
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.setIconified(false);
            }
        });

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.toLowerCase().equals("keaton macleod"))
                {
                    searchBar.clearFocus(); //Prevent query from being executed twice
                    String requestUrl = "https://api.instagram.com/v1/users/1255194646/media/recent/?access_token=1255194646.e985a2a.8e6d85e2e2c24b1cbe6f7a0036486349";
                    InstagramScavenger instagramScavenger = new InstagramScavenger();

                    try
                    {
                        JSONObject instagramData = instagramScavenger.execute(requestUrl).get();
                        ArrayList<String> pictureText = instagramScavenger.getPostedTextFromPictures(instagramData);

//                        OpenNLPProcessor processor = new OpenNLPProcessor(Search.this, "en", "SPORT", "marked_page_content.train", "en-ner-sport.bin", pictureText);
//                        Thread processorThread = new Thread(processor);
//                        processorThread.start();
//                        processorThread.join();

                        OpenNLPTrainer openNLPTrainer = new OpenNLPTrainer(Search.this, "en", "SPORT", "marked_page_content.train", "en-ner-sport.bin", pictureText);
//                        for (int i = 0; i < 5; i++)
//                        {
//                            openNLPTrainer.trainModel();
//                        }

                        OpenNLPProcessorAsync processor = new OpenNLPProcessorAsync(Search.this, "en", "SPORT", "marked_page_content.train", "en-ner-sport.bin", pictureText);
                        processor.execute().get();

                        ContentEntityPair contentEntityPair = processor.getContentEntityPair();
                        //Make these run in background tasks so it doesn't run on Main thread and so it executes
                        //multiple NER's only taking the time of the slowest classification.
                        //TODO: Add in deterministic classifications for hashtags
                        //TODO: Add in deterministic classification for at_symbols
                        //TODO: Get timestamp references so you can see how "old" a given contentEntityPair is

                        Bundle bundle = new Bundle();
                        bundle.putString("name", query);
                        bundle.putSerializable("content", contentEntityPair.getContent());
                        bundle.putSerializable("entity", contentEntityPair.getEntities());

                        Intent intent = new Intent(Search.this, UserSummary.class);
                        intent.putExtra("bundle", bundle);

                        Search.this.startActivity(intent);
                    }
                    catch (InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    private ArrayList<String> jsonArrayToStringArray(JSONArray jsonArray) throws JSONException
    {
        ArrayList<String> list = new ArrayList<String>();
        for(int i = 0; i < jsonArray.length(); i++){
            list.add(jsonArray.getJSONObject(i).getString("name"));
        }

        return list;
    }
}
