package com.example.jeeves.peepl.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jeeves.peepl.R;
import java.util.ArrayList;
import opennlp.tools.util.Span;

public class UserSummary extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_summary);
        populateActivity();
    }

    public void populateActivity()
    {
        Bundle bundle = getIntent().getBundleExtra("bundle");
        String name = bundle.getString("name");
        ArrayList<String> content = bundle.getStringArrayList("content");
        ArrayList<Span> entity = (ArrayList<Span>) bundle.getSerializable("entity");

        TextView nameField = findViewById(R.id.searched_name);
        nameField.setText(name);
        String[] contentItems = new String[content.size()];
        //Put the list of activities in the ListView
        for (int a=0; a<content.size(); a++)
        {
            contentItems[a] = content.get(a);
        }

        ListView listView = findViewById(R.id.activities_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(listView.getContext(), android.R.layout.simple_list_item_1, content);
        listView.setAdapter(adapter);
    }
}
