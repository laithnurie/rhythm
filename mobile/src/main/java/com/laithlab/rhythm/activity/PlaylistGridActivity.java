package com.laithlab.rhythm.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.laithlab.rhythm.R;
import com.laithlab.rhythm.adapter.PlaylistGridAdapter;
import com.laithlab.rhythm.customview.GridAutoFitLayoutManager;
import com.laithlab.rhythm.db.Playlist;
import com.laithlab.rhythm.dto.MusicContent;
import com.laithlab.rhythm.utils.ContentType;
import com.laithlab.rhythm.utils.DialogHelper;
import com.laithlab.rhythm.utils.MusicDataUtility;
import com.laithlab.rhythm.utils.ViewUtils;

import java.util.List;

public class PlaylistGridActivity extends AppCompatActivity implements PlaylistGridAdapter.ClickListener {

    private DrawerLayout drawerLayout;
    private PlaylistGridAdapter playlistGridAdapter;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_grid);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.color_primary));

        List<Playlist> playlists = MusicDataUtility.getPlayists(this);
        playlistGridAdapter = new PlaylistGridAdapter(playlists, this);
        RecyclerView playlistGridView = (RecyclerView) findViewById(R.id.playist_grid);
        GridAutoFitLayoutManager gridLayoutManager = new GridAutoFitLayoutManager(this, 300);
        playlistGridView.setLayoutManager(gridLayoutManager);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            if (playlists.size() > 0){
                playlistGridView.setAdapter(playlistGridAdapter);
                findViewById(R.id.no_playlists_added).setVisibility(View.GONE);
            } else {
                findViewById(R.id.no_playlists_added).setVisibility(View.VISIBLE);
            }
        }

        FloatingActionButton addPlaylist = (FloatingActionButton) findViewById(R.id.add_playlist);
        addPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.showAddPlaylistDialog(PlaylistGridActivity.this);
            }
        });

        ViewUtils.drawerClickListener(this);
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
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        } else {
            MusicContent musicContent = new MusicContent();
            musicContent.setContentType(ContentType.PLAYLIST);
            musicContent.setPlaylistName(playlistGridAdapter.getItem(position).getPlaylistName());
            musicContent.setId(playlistGridAdapter.getItem(position).getId());

            Intent intent = new Intent(PlaylistGridActivity.this, PlaylistActivity.class);
            intent.putExtra("musicContent", musicContent);
            startActivity(intent);
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
        playlistGridAdapter.toggleSelection(position);
        int count = playlistGridAdapter.getSelectedItemCount();

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
            mode.getMenuInflater().inflate(R.menu.playlist_selection_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int i = item.getItemId();
            if (i == R.id.delete_playlist_menu_item) {
                deletePlaylist();
                actionMode.finish();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            playlistGridAdapter.clearSelection();
        }
    }

    private void deletePlaylist() {
        final List<Integer> selectedPlaylists = playlistGridAdapter.getSelectedItems();
        for (int j = selectedPlaylists.size() - 1; j >= 0; j--) {
            MusicDataUtility.deletePlaylist(playlistGridAdapter
                    .getItem(selectedPlaylists.get(j)).getId(), PlaylistGridActivity.this);
        }
        playlistGridAdapter.notifyDataSetChanged();
    }
}

