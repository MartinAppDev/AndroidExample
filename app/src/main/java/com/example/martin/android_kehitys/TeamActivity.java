package com.example.martin.android_kehitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TeamActivity extends AppCompatActivity {
    private List<Integer> ids = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);

        if (id == -1) {
            return;
        }

        new FetchSportData().execute(4, this, id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setData(JSONObject data) {
        try {
            JSONObject team = data.getJSONArray("teams").getJSONObject(0);

            int id = team.getInt("id");

            final WebView webView = (WebView) findViewById(R.id.webView);

            webView.loadUrl("http://www-league.nhlstatic.com/builds/site-core/51dbade9eea13fff7ef3aabe952ebedba1ef5432_1488385427/images/logos/team/current/team-" + id + "-light.svg");

            getSupportActionBar().setTitle(team.getString("name"));

            final TextView offcialSiteTextView = (TextView) findViewById(R.id.firstYearOfPlayTextView);
            final TextView firstYearOfPlayTextView = (TextView) findViewById(R.id.textView8);

            offcialSiteTextView.setText(team.getString("officialSiteUrl"));
            firstYearOfPlayTextView.setText("First year of play: " + team.getString("firstYearOfPlay"));

            final List<String> titles = new ArrayList<>();
            final ListView listView = (ListView) findViewById(R.id.listView);

            JSONObject rosterObject = team.getJSONObject("roster");
            JSONArray rosterArray = rosterObject.getJSONArray("roster");

            ids.clear();

            for (int j = rosterArray.length() - 1; j >= 0; j--) {
                JSONObject person = rosterArray.getJSONObject(j).getJSONObject("person");
                String name = person.getString("fullName");

                titles.add(name);

                int person_id = person.getInt("id");

                ids.add(person_id);
        (   }

            final ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, titles) {
                @Override
                public View getView (int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView offcialSiteTextView = (TextView) view.findViewById(android.R.id.text1);

                    offcialSiteTextView.setTextColor(Color.BLACK);
                    return view;
                }
            };

            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView parentView, View childView,
                                        int position, long id) {
                    Intent screen = new Intent(getApplicationContext(), PlayerActivity.class);

                    screen.putExtra("id", ids.get(position));
                    startActivity(screen);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
