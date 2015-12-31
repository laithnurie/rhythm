package com.laithlab.core.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.laithlab.core.R;
import com.laithlab.core.adapter.SongListAdapter;
import com.laithlab.core.converter.DTOConverter;
import com.laithlab.core.db.Playlist;
import com.laithlab.core.db.Song;
import com.laithlab.core.dto.AlbumDTO;
import com.laithlab.core.utils.MusicDataUtility;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class AlbumActivity extends AppCompatActivity implements SongListAdapter.ClickListener {

    private static String ALBUM_ID_PARAM = "albumId";
    private DrawerLayout drawerLayout;
    private SongListAdapter songListAdapter;
    private AlbumDTO currentAlbum;

    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;

    public static Intent getIntent(Context context, String id) {
        Intent artistActivity = new Intent(context, AlbumActivity.class);
        artistActivity.putExtra(ALBUM_ID_PARAM, id);
        return artistActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Bundle extras = getIntent().getExtras();
        currentAlbum = extras.getParcelable("album");
        if (currentAlbum == null) {
            currentAlbum = DTOConverter.getAlbumDTO(MusicDataUtility.getAlbumById(extras.getString(ALBUM_ID_PARAM), this));
        }

        TextView albumTitle = (TextView) findViewById(R.id.txt_album);
        albumTitle.setText(currentAlbum.getAlbumTitle());

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.color_primary));
        View tiltedView = findViewById(R.id.tilted_view);
        tiltedView.setPivotX(0f);
        tiltedView.setPivotY(0f);
        tiltedView.setRotation(-5f);

        RecyclerView songList = (RecyclerView) findViewById(R.id.rv_songs_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        songList.setLayoutManager(layoutManager);
        songListAdapter = new SongListAdapter(currentAlbum.getSongs(), this);
        songList.setAdapter(songListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_browse, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        } else if (i == R.id.search_menu_item) {
            startActivity(SearchActivity.getIntent(this));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        } else {
            Intent playerActivity = new Intent(this, SwipePlayerActivity.class);
            playerActivity.putParcelableArrayListExtra("songs", (ArrayList<? extends Parcelable>) currentAlbum.getSongs());
            playerActivity.putExtra("songPosition", position);
            startActivity(playerActivity);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);
        return true;
    }

    private void toggleSelection(int position) {
        songListAdapter.toggleSelection(position);
        int count = songListAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.song_selection_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int i = item.getItemId();
            if (i == R.id.add_to_playlist_menu_item) {
                List<Song> songs = MusicDataUtility.getAlbumById(currentAlbum.getId(), AlbumActivity.this).getSongs();
                Realm realm = Realm.getInstance(AlbumActivity.this);
                realm.beginTransaction();
                Playlist playlistSelected = realm.where(Playlist.class)
                        .contains("playlistName", "laith")
                        .findFirst();
                List<Integer> selectedSongs = songListAdapter.getSelectedItems();
                for (int j = 0; j < selectedSongs.size(); j++) {
                    playlistSelected.getSongs().add(songs.get(selectedSongs.get(j)));
                }
                realm.commitTransaction();
                mode.finish();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            songListAdapter.clearSelection();
            actionMode = null;
        }
    }

}
