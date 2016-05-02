package com.laithlab.rhythm.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.laithlab.rhythm.adapter.AlbumGridAdapter;
import com.laithlab.rhythm.converter.DTOConverter;
import com.laithlab.rhythm.customview.GridAutoFitLayoutManager;
import com.laithlab.rhythm.dto.ArtistDTO;
import com.laithlab.rhythm.dto.MusicContent;
import com.laithlab.rhythm.utils.ContentType;
import com.laithlab.rhythm.utils.MusicDataUtility;
import com.laithlab.rhythm.utils.ViewUtils;
import com.laithlab.rhythm.R;

public class ArtistActivity extends RhythmActivity implements AlbumGridAdapter.ClickListener {

    private static final java.lang.String ARTIST_ID_PARAM = "artistId";
    private static final java.lang.String ARTIST_PARAM = "artist";
    private DrawerLayout drawerLayout;
    private RecyclerView albumGrid;

    public static Intent getIntent(Context context, String artistId) {
        Intent artistActivity = new Intent(context, ArtistActivity.class);
        artistActivity.putExtra(ARTIST_ID_PARAM, artistId);
        return artistActivity;
    }

    public static Intent getIntent(Context context, ArtistDTO artist) {
        Intent artistActivity = new Intent(context, ArtistActivity.class);
        artistActivity.putExtra(ARTIST_PARAM, artist);
        return artistActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        ArtistDTO currentArtist = extras.getParcelable(ARTIST_PARAM);
        if (currentArtist == null) {
            currentArtist = DTOConverter.getArtistDTO(MusicDataUtility.getArtistById(extras.getString(ARTIST_ID_PARAM), this));
        }

        TextView currentArtistTitle = (TextView) findViewById(R.id.current_artist_title);
        currentArtistTitle.setText(currentArtist.getArtistName());

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.color_primary));

        GridAutoFitLayoutManager gridLayoutManager = new GridAutoFitLayoutManager(this, 300);
        albumGrid = (RecyclerView) findViewById(R.id.album_grid);
        albumGrid.setLayoutManager(gridLayoutManager);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            if(currentArtist.getAlbums() != null && currentArtist.getAlbums().size() >0){
                albumGrid.setAdapter(new AlbumGridAdapter(currentArtist.getAlbums(), this));
                findViewById(R.id.no_albums_added).setVisibility(View.GONE);
            } else {
                findViewById(R.id.no_albums_added).setVisibility(View.VISIBLE);
            }
        }

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
        MusicContent musicContent = new MusicContent();
        musicContent.setContentType(ContentType.ALBUM);
        musicContent.setPlaylistName(((AlbumGridAdapter) albumGrid.getAdapter()).getItem(position).getAlbumTitle());
        musicContent.setId(((AlbumGridAdapter) albumGrid.getAdapter()).getItem(position).getId());

        Intent intent = new Intent(ArtistActivity.this, PlaylistActivity.class);
        intent.putExtra("musicContent", musicContent);
        startActivity(intent);
    }
}
