package com.laithlab.rhythm.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.laithlab.rhythm.R;
import com.laithlab.rhythm.db.Playlist;
import com.laithlab.rhythm.fragment.PlaylistAddDialog;
import com.laithlab.rhythm.fragment.PlaylistSelectDialog;

import java.util.List;
import timber.log.Timber;

public class DialogHelper {
    public static void showAddPlaylistDialog(AppCompatActivity activity){
        FragmentManager manager = activity.getSupportFragmentManager();
        PlaylistAddDialog dialog = new PlaylistAddDialog();
        dialog.show(manager, "dialog");
    }

    public static void aboutDialog(final Context context){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.RhythmAlertDialog);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View dialogView = inflater.inflate(R.layout.dialog_about_rhythm, null);
        PackageInfo pInfo;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            TextView versionNameCode = (TextView)dialogView.findViewById(R.id.version_name_code);
            versionNameCode.setText(context.getResources().getString(R.string.version_name_code,
                    pInfo.versionName, pInfo.versionCode));
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e, "packageInfo");
        }
        dialogView.findViewById(R.id.github_project).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://github.com/laithnurie/rhythm"));
                context.startActivity(i);
            }
        });
        dialogView.findViewById(R.id.design_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://dribbble.com/shots/2183234-Music-app-research"));
                context.startActivity(i);
            }
        });
        dialogBuilder.setView(dialogView);

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public static void showPermissionDialog(final Context context){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.RhythmAlertDialog);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View dialogView = inflater.inflate(R.layout.dialog_storage_permission, null);
        dialogBuilder.setView(dialogView);

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public static void resetMusicDataAlert(final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.RhythmAlertDialog);

        alertDialogBuilder.setTitle("Reset Music Data");
        alertDialogBuilder
                .setMessage("Including Most Played, Last Played and your Personal Playlists ?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MusicDataUtility.resetMusicStats(context);
                SharedPreferences sharedPreferences = context
                        .getSharedPreferences("com.laithlab.rhythm", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(context.getString(R.string.first_time_pref_key), true);
                editor.apply();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void addSongToPlaylist(AppCompatActivity activity){
        List<Playlist> playlists = MusicDataUtility.getPlayists(activity);

        if(playlists.size() == 0){
            showAddPlaylistDialog(activity);
        } else {
            String[] playlistNames = new String[playlists.size()];
            for (int i = 0; i < playlistNames.length; i++) {
                playlistNames[i] = playlists.get(i).getPlaylistName();
            }
            FragmentManager manager = activity.getSupportFragmentManager();
            PlaylistSelectDialog dialog = new PlaylistSelectDialog();
            Bundle args = new Bundle();
            args.putStringArray("playlistNames", playlistNames);
            dialog.setArguments(args);
            dialog.show(manager, "dialog");
        }
    }
}
