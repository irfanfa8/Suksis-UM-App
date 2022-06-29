package com.example.suksisumapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class LetterList extends ArrayAdapter<Letter> {
    private Activity context;
    List<Letter> letters;

    public LetterList(Activity context, List<Letter> letters) {
        super(context, R.layout.layout_letter_list, letters);
        this.context = context;
        this.letters = letters;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_letter_list, null, true);

        TextView textViewFullname = (TextView) listViewItem.findViewById(R.id.textViewFullname);
        TextView textViewTitle = (TextView) listViewItem.findViewById(R.id.textViewReason);
        TextView textViewDate = (TextView) listViewItem.findViewById(R.id.textViewDate);

        Letter letter = letters.get(position);
        textViewFullname.setText(letter.getLetterName());
        textViewTitle.setText(letter.getLetterTitle());
        textViewDate.setText(letter.getLetterDate());

        return listViewItem;
    }

}

