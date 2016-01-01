package com.laithlab.core.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.laithlab.core.R;
import com.laithlab.core.adapter.PlaylistGridAdapter;
import com.laithlab.core.dto.MusicContent;
import com.laithlab.core.utils.ContentType;
import com.laithlab.core.utils.DialogHelper;
import com.laithlab.core.utils.MusicDataUtility;
import com.laithlab.core.utils.ViewUtils;


public class PlaylistGridActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

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
        View tiltedView = findViewById(R.id.tilted_view);
        tiltedView.setPivotX(0f);
        tiltedView.setPivotY(0f);
        tiltedView.setRotation(-5f);

        final PlaylistGridAdapter playlistGridAdapter = new PlaylistGridAdapter(this, MusicDataUtility.getPlayists(this));
        GridView playlistGridView = (GridView) findViewById(R.id.playist_grid);
        playlistGridView.setAdapter(playlistGridAdapter);
        playlistGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MusicContent musicContent = new MusicContent();
                musicContent.setContentType(ContentType.PLAYLIST);
                musicContent.setPlaylistName(playlistGridAdapter.getItem(position).getPlaylistName());
                musicContent.setId(playlistGridAdapter.getItem(position).getId());

                Intent intent = new Intent(PlaylistGridActivity.this, PlaylistActivity.class);
                intent.putExtra("musicContent", musicContent);
                startActivity(intent);

            }
        });

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

}

