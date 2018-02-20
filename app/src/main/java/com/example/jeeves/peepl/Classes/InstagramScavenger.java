package com.example.jeeves.peepl.Classes;

/**
 * Created by Jeeves on 10/30/2017.
 */

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Creates the asynchronous request for interacting with the Instagram API
 */

public class InstagramScavenger extends AsyncTask<String, Void, JSONObject> {
    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;

    /*
     * Given the HTTP request, grabs all of the data and all of the corresponding fields
     * associated with that request according to the Instagram API
     */
    @Override
    protected JSONObject doInBackground(String... params) {
        String stringUrl = params[0];
        String result = "";
        String inputLine;
        JSONObject jsonObject = null;
        try
        {
            URL myUrl = new URL(stringUrl);
            HttpURLConnection connection = (HttpURLConnection)
                    myUrl.openConnection();

            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.connect();

            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }

            result = stringBuilder.toString();
            jsonObject = new JSONObject(result);

            reader.close();
            streamReader.close();
        }
        catch (IOException | JSONException e)
        {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /*
     * Gets all of the picture text captions that the user has posted on their pictures
     */
    public ArrayList<String> getPostedTextFromPictures(JSONObject instagramData) throws JSONException {
        ArrayList<String> textFromPictures = new ArrayList<String>();

        //Get all the data fields from the JSONObject
        JSONArray instagramDataArray = instagramData.getJSONArray("data");

        for (int a=0; a<instagramDataArray.length(); a++)
            textFromPictures.add(instagramDataArray.getJSONObject(a).getJSONObject("caption").getString("text"));

        return textFromPictures;
    }
}



