package com.autstudent.autschedular;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by wilzo on 11/01/2016.
 */
public class AddClass extends AddActivity.PlaceholderFragment {

    private View rootView;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_class_add, container, false);
        this.rootView = rootView;
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        RefreshClassList refreshClassList = new RefreshClassList();
        refreshClassList.execute();
    }

    private class RefreshClassList extends AsyncTask<Void, Void, Void> {

        private View loadingView;
        private View listView;
        private List<ParseObject> objectLists;
        private StreamArrayAdapter streamArrayAdapter;

        @Override
        protected void onPreExecute() {
            loadingView = rootView.findViewById(R.id.loading_class);
            listView = rootView.findViewById(R.id.add_class_list);
        }

        @Override
        protected Void doInBackground(Void... params) {
            ParseUser user = ParseUser.getCurrentUser();
            ParseRelation<ParseObject> relation = user.getRelation("Class");
            ParseQuery<ParseObject> query = relation.getQuery();
            query.include("paper");
            try {
                objectLists = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            streamArrayAdapter = new StreamArrayAdapter(getActivity(), R.layout.custom_paper_row_layout, objectLists);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ListView ls = (ListView) getActivity().findViewById(R.id.add_class_list);
            ls.setAdapter(streamArrayAdapter);
            loadingView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            ls.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int pos, long id) {
                    final int position = pos;
                    final String items[] = {"Delete","Cancel"};
                    AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                    ab.setTitle("Options");
                    ab.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface d, int choice) {
                            if (choice == 0) {
                                try {
                                    showProgressDialog();
                                    ParseUser user = ParseUser.getCurrentUser();
                                    ParseRelation<ParseObject> classRelation = user.getRelation("Class");
                                    classRelation.remove(objectLists.get(position));
                                    user.save();
                                    RefreshClassList refreshClassList = new RefreshClassList();
                                    refreshClassList.execute();
                                    hideProgressDialog();
                                }
                                catch (ParseException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    ab.show();
                    return true;
                }
            });
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Deleting");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}
