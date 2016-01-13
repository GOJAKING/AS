package com.autstudent.autschedular;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.autstudent.autschedular.Helper.Converter;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.List;



/**
 * Created by wilzo on 26/12/2015.
 */
public class StreamArrayAdapter extends ArrayAdapter {
    private Context activity;
    private List<ParseObject> objects;


    public StreamArrayAdapter(Context context, int resource, List<ParseObject> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.objects= objects;
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder view;
        if (rowView == null) {
            // Get a new instance of the row layout view
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.custom_paper_row_layout, null);
            // Hold the view objects in an object, that way the don't need to be "re- finded"
            view = new ViewHolder();
            view.title = (TextView) rowView.findViewById(R.id.row_paper_title);
            view.details = (TextView) rowView.findViewById(R.id.row_paper_details);
            view.stream_code = (TextView) rowView.findViewById(R.id.stream_num);
            rowView.setTag(view);
        } else {
            view = (ViewHolder) rowView.getTag();
        }
        /** Set data to your Views. */
        ParseObject paper = objects.get(position).getParseObject("paper");
        view.title.setText(paper.get("paper_code").toString() + " - " + paper.get("paper_title").toString());
        view.stream_code.setText(objects.get(position).get("stream_code").toString());
        ParseRelation<ParseObject> parseRelation = objects.get(position).getRelation("stream");
        ParseQuery <ParseObject>parseQuery = parseRelation.getQuery();
        try{
            List<ParseObject> list = parseQuery.find();
            String details = "";
            boolean first = true;
            for (ParseObject o : list) {
                if(!first){
                    details+="\n";
                }
                else{
                    first = false;
                }
                details += Converter.getDate(Integer.parseInt(o.get("day").toString())) + " " + o.get("start").toString() + " - " + o.get("end").toString()+"\n \t" + o.get("room").toString();
            }
            view.details.setText(details);
        }
        catch (ParseException e){
            e.printStackTrace();
        }

        return rowView;
    }


    protected static class ViewHolder {
        protected TextView title;
        protected TextView details;
        protected TextView stream_code;
    }
}
