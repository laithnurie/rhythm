package com.laithlab.rhythm.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.laithlab.rhythm.R;

public class PlaylistSelectDialog extends DialogFragment implements
        OnItemClickListener {

    String[] listItems;
    ListView mylist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().setTitle("Choose Playlist");
        View view = inflater.inflate(R.layout.dialog_fragment, container, false);
        mylist = (ListView) view.findViewById(R.id.list);
        listItems = getArguments().getStringArray("playlistNames");

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, listItems);
        mylist.setAdapter(adapter);
        mylist.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();
        PlaylistCallback mListener = (PlaylistCallback) getActivity();
        mListener.playlistChosen(position);
    }
}
