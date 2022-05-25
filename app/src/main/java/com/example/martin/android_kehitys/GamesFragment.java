package com.example.martin.android_kehitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GamesFragment extends Fragment {
    private List<Integer> ids = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_games, container, false);
        Bundle args = getArguments();

        new FetchSportData().execute(0, this);

        final EditText textView = (EditText) rootView.findViewById(R.id.editText3);
        final EditText textView2 = (EditText) rootView.findViewById(R.id.editText4);
        final ListView listView = (ListView) rootView.findViewById(R.id.listView);
        final GamesFragment fragment = this;

        textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    DateDialog dateDialog = new DateDialog(view, fragment);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();

                    dateDialog.show(ft, "DatePicker");
                }
            }
        });

        textView2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    DateDialog dateDialog = new DateDialog(view, fragment);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();

                    dateDialog.show(ft, "DatePicker");
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parentView, View childView,
                                    int position, long id) {
                Intent screen = new Intent(getContext(), GameActivity.class);

                screen.putExtra("id", ids.get(position));
                startActivity(screen);
            }
        });

        return rootView;
    }

    public void update() {
        final EditText textView = (EditText) getView().findViewById(R.id.editText3);
        final EditText textView2 = (EditText) getView().findViewById(R.id.editText4);

        if (!textView.getText().toString().isEmpty() && !textView2.getText().toString().isEmpty()) {
            new FetchSportData().execute(0, this, textView.getText().toString(), textView2.getText().toString());
        }
    }

    public void setData(JSONObject data) {
        ids.clear();

        try {
            final List<String> titles = new ArrayList<>();
            final List<String> subtitles = new ArrayList<>();

            JSONArray dates = data.getJSONArray("dates");

            for (int i = dates.length() - 1; i >= 0; i--) {
                JSONObject date = dates.getJSONObject(i);
                JSONArray games = date.getJSONArray("games");

                for (int j = games.length() - 1; j >= 0; j--) {
                    JSONObject game = games.getJSONObject(j);
                    JSONObject teams = game.getJSONObject("teams");
                    JSONObject away = teams.getJSONObject("away");
                    JSONObject team = away.getJSONObject("team");

                    String name = team.getString("name");

                    away = teams.getJSONObject("home");
                    team = away.getJSONObject("team");

                    titles.add(name + " @ " + name);

                    String time = game.getString("gameDate");

                    subtitles.add(time.replace("T", " ").replace("Z", ""));

                    int id = game.getInt("gamePk");

                    ids.add(id);
                }
            }

            final ListView listView = (ListView) getView().findViewById(R.id.listView);

            final ArrayAdapter adapter = new ArrayAdapter(getView().getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, titles) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView titleTextView = (TextView) view.findViewById(android.R.id.text1);
                    TextView subtitleTextView = (TextView) view.findViewById(android.R.id.text2);

                    titleTextView.setText(titles.get(position));
                    subtitleTextView.setText(subtitles.get(position));
                    return view;
                }
            };

            listView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}