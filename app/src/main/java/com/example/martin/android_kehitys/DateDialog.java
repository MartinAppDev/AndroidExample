package com.example.martin.android_kehitys;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

@SuppressLint("ValidFragment")
public class DateDialog extends DialogFragment
        implements android.app.DatePickerDialog.OnDateSetListener {

    private final EditText editText;
    private final GamesFragment fragment;

    public DateDialog(View view, GamesFragment fragment) {
        editText = (EditText) view;
        this.fragment = (GamesFragment) fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new android.app.DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        month++;

        String date = year + "-" + (month < 10 ? "0" : "") + month + "-" + (day < 10 ? "0" : "") + day;

        editText.setText(date);
        fragment.update();
    }
}