package com.laithlab.rhythm.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.laithlab.rhythm.R;

public class PlaylistSelectDialog extends DialogFragment implements
        OnItemClickListener {

    private String[] listItems;
    private ListView mylist;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.RhythmAlertDialog);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_fragment, null);
        dialogBuilder.setView(dialogView);
        mylist = (ListView) dialogView.findViewById(R.id.list);
        listItems = getArguments().getStringArray("playlistNames");

        dialogBuilder.setTitle("Choose Playlist");

        return dialogBuilder.create();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.playlist_item_row, listItems);
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
