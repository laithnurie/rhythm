package com.laithlab.core.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.laithlab.core.R;
import com.laithlab.core.activity.BrowseActivity;
import com.laithlab.core.activity.PlaylistActivity;
import com.laithlab.core.activity.PlaylistGridActivity;
import com.laithlab.core.activity.RhythmPrefs;
import com.laithlab.core.activity.SwipePlayerActivity;
import com.laithlab.core.dto.MusicContent;

public class ViewUtils {
    public static void drawerClickListener(final Activity activity) {

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                if (i == R.id.browse_drawer_item) {
                    Intent browse = new Intent(activity, BrowseActivity.class);
                    activity.startActivity(browse);
                } else if (i == R.id.most_played_drawer_item) {
                    Intent mostPlayed = new Intent(activity, PlaylistActivity.class);
                    MusicContent musicContent = new MusicContent();
                    musicContent.setContentType(ContentType.MOST_PLAYED);
                    musicContent.setPlaylistName("Most Played");
                    mostPlayed.putExtra("musicContent", musicContent);
                    activity.startActivity(mostPlayed);
                } else if (i == R.id.last_played_drawer_item) {
                    Intent lastPlayed = new Intent(activity, PlaylistActivity.class);
                    MusicContent musicContent = new MusicContent();
                    musicContent.setContentType(ContentType.LAST_PLAYED);
                    musicContent.setPlaylistName("Last Played");
                    lastPlayed.putExtra("musicContent", musicContent);
                    activity.startActivity(lastPlayed);
                } else if (i == R.id.playlists_drawer_item) {
                    Intent playlists = new Intent(activity, PlaylistGridActivity.class);
                    activity.startActivity(playlists);
                } else if (i == R.id.now_playing_drawer_item) {
                    Intent playerIntent = new Intent(activity, SwipePlayerActivity.class);
                    playerIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(playerIntent);
                } else if (i == R.id.about_drawer_item) {
                    DialogHelper.aboutDialog(activity);
                } else if (i == R.id.settings_drawer_item){
                    Intent settings = new Intent(activity, RhythmPrefs.class);
                    activity.startActivity(settings);
                }
            }
        };

        activity.findViewById(R.id.browse_drawer_item).setOnClickListener(clickListener);
        activity.findViewById(R.id.most_played_drawer_item).setOnClickListener(clickListener);
        activity.findViewById(R.id.last_played_drawer_item).setOnClickListener(clickListener);
        activity.findViewById(R.id.playlists_drawer_item).setOnClickListener(clickListener);
        activity.findViewById(R.id.now_playing_drawer_item).setOnClickListener(clickListener);
        activity.findViewById(R.id.settings_drawer_item).setOnClickListener(clickListener);
        activity.findViewById(R.id.about_drawer_item).setOnClickListener(clickListener);
        activity.findViewById(R.id.settings_drawer_item).setOnClickListener(clickListener);

        activity.findViewById(R.id.now_playing_drawer_item).setVisibility(PlayBackUtil.getMediaPlayer() != null ? View.VISIBLE : View.GONE);
    }
}
