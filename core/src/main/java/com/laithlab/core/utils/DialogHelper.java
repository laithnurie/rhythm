package com.laithlab.core.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.laithlab.core.R;

public class DialogHelper {
    public static void showAddPlaylistDialog(final Context context){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.RhythmAlertDialog);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_add_playlist, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.playlist_input);

        dialogBuilder.setTitle("Add a Playlist");
        dialogBuilder.setMessage("Playlist Name:");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                MusicDataUtility.createPlaylist(edt.getText().toString(), context);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                dialog.cancel();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}
