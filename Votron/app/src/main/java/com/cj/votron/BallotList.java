package com.cj.votron;

/**
 * Created by gvamos on 8/8/14.
 * http://www.learn2crack.com/2013/10/android-custom-listview-images-text-example.html
 * "Android custom listview"
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class BallotList extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] candidates;
    private final Integer[] imageId;

    public BallotList(Activity context, String[] candidates, Integer[] scores) {
        super(context, R.layout.ballotlist, candidates);
        this.context = context;
        this.candidates = candidates;
        this.imageId = scores;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.ballotlist, null, true);
        TextView candidate = (TextView) rowView.findViewById(R.id.candidate);
        EditText score = (EditText) rowView.findViewById(R.id.score);
        candidate.setText(candidates[position]);
        score.setText(imageId.toString());
//        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}
