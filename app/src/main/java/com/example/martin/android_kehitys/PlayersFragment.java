package com.example.martin.android_kehitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlayersFragment extends Fragment {
    private List<Integer> ids = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_players, container, false);
        Bundle args = getArguments();

        new FetchSportData().execute(2, this);

        final ListView listView = (ListView) rootView.findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parentView, View childView,
                                    int position, long id) {
                Intent screen = new Intent(getContext(), PlayerActivity.class);

                screen.putExtra("id", ids.get(position));
                startActivity(screen);
            }
        });

        return rootView;
    }

    public void setData(JSONObject data) {
        ids.clear();

        try {
            final List<String> titles = new ArrayList<>();

            JSONArray teams = data.getJSONArray("teams");

            for (int i = teams.length() - 1; i >= 0; i--) {
                JSONObject teamObject = teams.getJSONObject(i);
                JSONObject roster = teamObject.getJSONObject("roster");
                JSONArray rosterArray = roster.getJSONArray("roster");

                for (int j = rosterArray.length() - 1; j >= 0; j--) {
                    JSONObject person = rosterArray.getJSONObject(j).getJSONObject("person");
                    String name = person.getString("fullName");

                    titles.add(name);

                    int id = person.getInt("id");

                    ids.add(id);
                }
            }

            final ListView listView = (ListView) getView().findViewById(R.id.listView);

            final ArrayAdapter adapter = new ArrayAdapter(getView().getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, titles) {
                @Override
                public View getView (int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView textView = (TextView) view.findViewById(android.R.id.text1);

                    textView.setTextColor(Color.BLACK);
                    return view;
                }
            };

            listView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}