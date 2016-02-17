package com.laithlab.rhythm.activity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ProgressBar;

import com.laithlab.rhythm.R;
import com.laithlab.rhythm.adapter.ArtistGridAdapter;
import com.laithlab.rhythm.converter.DTOConverter;
import com.laithlab.rhythm.customview.GridAutoFitLayoutManager;
import com.laithlab.rhythm.db.Artist;
import com.laithlab.rhythm.utils.DialogHelper;
import com.laithlab.rhythm.utils.MusicDBProgressCallBack;
import com.laithlab.rhythm.utils.MusicDataUtility;
import com.laithlab.rhythm.utils.ViewUtils;

import java.util.List;

public class BrowseActivity extends AppCompatActivity implements MusicDBProgressCallBack, ArtistGridAdapter.ClickListener {

    private static final int REQUEST_READ_STORAGE = 1;
    private DrawerLayout drawerLayout;
    private RecyclerView browseGrid;
    private Context context;
    private View loadingContainer;

    private SharedPreferences sharedPreferences;
    private ArtistGridAdapter artistGridAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        context = this;
        sharedPreferences = context.getSharedPreferences("com.laithlab.rhythm", Context.MODE_PRIVATE);

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
        loadingContainer = findViewById(R.id.loadingContainer);
        ProgressBar loadingProgess = (ProgressBar) findViewById(R.id.loadingProgess);
        loadingProgess.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        List<Artist> artists = MusicDataUtility.allArtists(this);
        browseGrid = (RecyclerView) findViewById(R.id.browse_grid);
        GridAutoFitLayoutManager gridLayoutManager = new GridAutoFitLayoutManager(this, 300);
        browseGrid.setLayoutManager(gridLayoutManager);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            if (artists != null && 0 < artists.size()) {
                artistGridAdapter = new ArtistGridAdapter(DTOConverter.getArtistList(artists), this);
                browseGrid.setAdapter(artistGridAdapter);
            }
        }
        ViewUtils.drawerClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateDb(this);
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
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void updateDb(final MusicDBProgressCallBack callBack) {
        boolean firstTimeLaunched = sharedPreferences.getBoolean(getString(R.string.first_time_pref_key), true);
        if (firstTimeLaunched) {
            loadingContainer.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.first_time_pref_key), false);
            editor.apply();
        } else {
            loadingContainer.setVisibility(View.GONE);
        }
        checkStoragePermission(callBack);
    }

    private void checkStoragePermission(final MusicDBProgressCallBack callBack) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE);
        } else {
            // permission has been granted, continue as usual
            new Thread(new Runnable() {
                public void run() {
                    MusicDataUtility.updateMusicDB(context);
                    callBack.finishedDBUpdate();
                }
            }).start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_STORAGE) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                loadingContainer.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    public void run() {
                        MusicDataUtility.updateMusicDB(context);
                        BrowseActivity.this.finishedDBUpdate();
                    }
                }).start();
            } else {
                // Permission was denied or request was cancelled
                DialogHelper.showPermissionDialog(this);
                loadingContainer.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void finishedDBUpdate() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(browseGrid.getAdapter() == null){
                    artistGridAdapter = new ArtistGridAdapter(DTOConverter.getArtistList(MusicDataUtility.allArtists(context)), BrowseActivity.this);
                    browseGrid.setAdapter(artistGridAdapter);
                } else {
                    artistGridAdapter.updateData(DTOConverter.getArtistList(MusicDataUtility.allArtists(context)));
                }
                loadingContainer.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onItemClicked(int position) {
        startActivity(ArtistActivity.getIntent(BrowseActivity.this, artistGridAdapter.getItem(position)));
    }
}
