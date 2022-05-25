package com.example.martin.android_kehitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    private List<Integer> ids = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);

        if (id == -1) {
            return;
        }

        new FetchSportData().execute(3, this, id);
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
            JSONObject gameData = data.getJSONObject("gameData");
            JSONObject teams = gameData.getJSONObject("teams");
            JSONObject away = teams.getJSONObject("away");
            JSONObject home = teams.getJSONObject("home");
            JSONObject game = gameData.getJSONObject("game");
            JSONObject datetime = gameData.getJSONObject("datetime");

            int id = game.getInt("pk");

            new DownloadImageTask((ImageView) findViewById(R.id.imageView3))
                    .execute("http://nhl.bamcontent.com/images/arena/scoreboard/30@2x.jpg");

            getSupportActionBar().setTitle(away.getString("name") + " @ " + home.getString("name"));

            final TextView awayTeamTextView = (TextView) findViewById(R.id.textView7);
            final TextView homeTeamTextView = (TextView) findViewById(R.id.textView10);
            final TextView awayTeamGoalsTextView = (TextView) findViewById(R.id.textView12);
            final TextView homeTeamGoalsTextView = (TextView) findViewById(R.id.textView13);
            final TextView abstractGameStateTextView = (TextView) findViewById(R.id.textView14);
            final TextView startTextView = (TextView) findViewById(R.id.textView16);
            final TextView endTextView = (TextView) findViewById(R.id.textView17);

            awayTeamTextView.setText(away.getString("name"));
            homeTeamTextView.setText(home.getString("name"));

            JSONObject teams = data.getJSONObject("liveData").getJSONObject("boxscore").getJSONObject("teams");

            awayTeamGoalsTextView.setText(Integer.toString(teams.getJSONObject("away").getJSONObject("teamStats").getJSONObject("teamSkaterStats").getInt("goals")));
            homeTeamGoalsTextView.setText(Integer.toString(teams.getJSONObject("home").getJSONObject("teamStats").getJSONObject("teamSkaterStats").getInt("goals")));
            abstractGameStateTextView.setText(gameData.getJSONObject("status").getString("abstractGameState"));

            startTextView.setText("Start: " + datetime.getString("dateTime").replace("T", " ").replace("Z", ""));

            if (datetime.has("endDateTime")) {
                endTextView.setVisibility(View.VISIBLE);
                endTextView.setText("End: " + datetime.getString("endDateTime").replace("T", " ").replace("Z", ""));
            } else {
                endTextView.setVisibility(View.GONE);
            }

            final ListView listView = (ListView) findViewById(R.id.listView);
            final List<String> titles = new ArrayList<>();
            JSONObject players = gameData.getJSONObject("players");
            Iterator<?> keys = players.keys();

            ids.clear();

            while (keys.hasNext()) {
                String key = (String) keys.next();

                if (players.get(key) instanceof JSONObject) {
                    JSONObject player = players.getJSONObject(key);
                    String fullName = player.getString("fullName");

                    titles.add(fullName);
                    ids.add(player.getInt("id"));
                }
            }

            if (titles.size() == 0){
                final TextView textView8 = (TextView) findViewById(R.id.textView15);

                textView8.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
            } else {
                final ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, titles) {
                    @Override
                    public View getView (int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView textView = (TextView) view.findViewById(android.R.id.text1);

                        textView.setTextColor(Color.BLACK);
                        return view;
                    }
                };

                listView.setAdapter(adapter);
            }



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
