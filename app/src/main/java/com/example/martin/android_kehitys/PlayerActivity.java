package com.example.martin.android_kehitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class PlayerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);

        if (id == -1) {
            return;
        }

        new FetchSportData().execute(5, this, id);
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
            final JSONObject person = data.getJSONArray("people").getJSONObject(0);
            final JSONObject primaryPosition = person.getJSONObject("primaryPosition");
            final JSONObject currentTeam = person.getJSONObject("currentTeam");

            int id = person.getInt("id");

            new DownloadImageTask((ImageView) findViewById(R.id.imageView2))
                    .execute("https://nhl.bamcontent.com/images/headshots/current/168x168/" + id + ".jpg");

            getSupportActionBar().setTitle(person.getString("fullName"));

            final TextView infoTextView = (TextView) findViewById(R.id.textView3);
            final TextView bornTextView = (TextView) findViewById(R.id.textView4);
            final TextView birthPlaceTextView = (TextView) findViewById(R.id.textView5);
            final TextView textView4 = (TextView) findViewById(R.id.textView6);
            final Button button = (Button) findViewById(R.id.button);

            infoTextView.setText(primaryPosition.getString("code") + " | " + person.getString("height") + " | " + person.getString("weight") + " lb | Age: " + person.getString("currentAge"));
            bornTextView.setText("Born: " + person.getString("birthDate"));
            birthPlaceTextView.setText("Birthplace: " + person.getString("birthCity"));
            textView4.setText("Shoots: " + (person.getString("shootsCatches").equalsIgnoreCase("l") ? "Left" : "Right"));

            button.setText(currentTeam.getString("name"));

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent screen = new Intent(getApplicationContext(), TeamActivity.class);

                    try {
                        screen.putExtra("id", currentTeam.getInt("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    startActivity(screen);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
