package com.laithlab.core.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.laithlab.core.R;
import com.laithlab.core.activity.BrowseActivity;
import com.laithlab.core.activity.PlaylistGridActivity;
import com.laithlab.core.activity.SwipePlayerActivity;

public class ViewUtils {
    public static void drawerClickListener(final Activity activity) {

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                if (i == R.id.browse_drawer_item) {
                    Intent browse = new Intent(activity, BrowseActivity.class);
                    activity.startActivity(browse);
                } else if (i == R.id.playlists_drawer_item) {
                    Intent playlists = new Intent(activity, PlaylistGridActivity.class);
                    activity.startActivity(playlists);
                } else if (i == R.id.now_playing_drawer_item) {
                    Intent playerIntent = new Intent(activity, SwipePlayerActivity.class);
                    playerIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(playerIntent);
                } else if (i == R.id.about_drawer_item) {
                    Toast.makeText(activity, "about", Toast.LENGTH_SHORT).show();
                }
            }
        };

        activity.findViewById(R.id.browse_drawer_item).setOnClickListener(clickListener);
        activity.findViewById(R.id.playlists_drawer_item).setOnClickListener(clickListener);
        activity.findViewById(R.id.now_playing_drawer_item).setOnClickListener(clickListener);
        activity.findViewById(R.id.settings_drawer_item).setOnClickListener(clickListener);
        activity.findViewById(R.id.about_drawer_item).setOnClickListener(clickListener);

        activity.findViewById(R.id.now_playing_drawer_item).setVisibility(PlayBackUtil.getMediaPlayer() != null ? View.VISIBLE : View.GONE);
    }
}
