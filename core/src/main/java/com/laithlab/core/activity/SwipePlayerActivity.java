package com.laithlab.core.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;
import com.laithlab.core.R;
import com.laithlab.core.converter.DTOConverter;
import com.laithlab.core.db.Song;
import com.laithlab.core.dto.AlbumDTO;
import com.laithlab.core.dto.SongDTO;
import com.laithlab.core.fragment.SongFragment;
import com.laithlab.core.fragment.SongFragmentListener;
import com.laithlab.core.service.SendToDataLayerThread;
import com.laithlab.core.utils.MusicDataUtility;
import com.laithlab.core.utils.PlayBackUtil;
import com.laithlab.core.utils.RhythmSong;

import io.realm.Realm;
import io.realm.RealmResults;

import java.util.List;

public class SwipePlayerActivity extends AppCompatActivity implements SongFragmentListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private View tiltedView;
    private TextView artist;
    private TextView album;
    private ViewPager viewPager;

    private boolean isWearConnected = false;
    private GoogleApiClient googleClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_player);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("player"));

        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tiltedView = findViewById(R.id.tilted_view);
        tiltedView.setPivotX(0f);
        tiltedView.setPivotY(0f);
        tiltedView.setRotation(-5f);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.color_primary));
        viewPager = (ViewPager) findViewById(R.id.pager);

        artist = (TextView) findViewById(R.id.txt_artist);
        album = (TextView) findViewById(R.id.txt_album);

        Bundle extras = getIntent().getExtras();
        final List<SongDTO> songsList;
        int songPosition;
        if (extras != null) {
            songPosition = extras.getInt("songPosition");
            songsList = extras.getParcelableArrayList("songs");
            PlayBackUtil.setPlayList(songsList);
            PlayBackUtil.setCurrentSongPosition(songPosition);
        } else {
            songsList = PlayBackUtil.getCurrentPlayList();
            songPosition = PlayBackUtil.getCurrentSongPosition();
        }

        populateSongs(songsList, songPosition);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                PlayBackUtil.setCurrentSongPosition(position);
                if (isWearConnected) {
                    String WEARABLE_DATA_PATH = "/wearable_data";
                    SongDTO currentSong = songsList.get(position);
                    RhythmSong rhythmSong = MusicDataUtility.getSongMeta(currentSong.getSongLocation());
                    DataMap dataMap = new DataMap();
                    dataMap.putString("song_title", rhythmSong.getTrackTitle());
                    dataMap.putByteArray("song_cover", rhythmSong.getImageData());
                    //Requires a new thread to avoid blocking the UI
                    new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap, googleClient).start();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void populateSongs(List<SongDTO> songsList, int songPosition) {
        viewPager.setAdapter(new SongFragmentPager(this.getSupportFragmentManager(),
                songsList));
        if (songPosition > 0) {
            viewPager.setCurrentItem(songPosition, true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleClient.connect();
    }

    @Override
    protected void onStop() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        populateSongs(PlayBackUtil.getCurrentPlayList(), PlayBackUtil.getCurrentSongPosition());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void changePlayerStyle(int vibrantColor, int songPosition) {
        if (songPosition == viewPager.getCurrentItem()) {
            if (vibrantColor == 0) {
                vibrantColor = getResources().getColor(R.color.color_primary);
            }
            toolbar.setBackgroundColor(vibrantColor);
            tiltedView.setBackgroundColor(vibrantColor);
        }
    }

    @Override
    public void setToolBarText(String artistTitle, String albumTitle) {
        artist.setText(artistTitle);
        album.setText(albumTitle);
    }

    @Override
    public void onConnected(Bundle bundle) {
        isWearConnected = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        isWearConnected = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        isWearConnected = false;
    }

    public class SongFragmentPager extends FragmentStatePagerAdapter {

        private List<SongDTO> songDTOs;

        public SongFragmentPager(FragmentManager fm, List<SongDTO> songDTOs) {
            super(fm);
            this.songDTOs = songDTOs;
        }


        @Override
        public Fragment getItem(int position) {
            return SongFragment.newInstance(songDTOs.get(position), position);
        }

        @Override
        public int getCount() {
            return songDTOs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String command = intent.getStringExtra("player_command");
            int currentSongIndex = viewPager.getCurrentItem();
            int lastSongIndex = viewPager.getAdapter().getCount() - 1;

            switch (command) {
                case "next":
                    if (currentSongIndex == lastSongIndex) {
                        viewPager.setCurrentItem(0);
                    } else {
                        viewPager.setCurrentItem(currentSongIndex + 1);
                    }

                    Toast.makeText(context, "next", Toast.LENGTH_LONG).show();
                    break;
                case "previous":
                    if (currentSongIndex == 0) {
                        viewPager.setCurrentItem(lastSongIndex);
                    } else {
                        viewPager.setCurrentItem(currentSongIndex - 1);
                    }
                    Toast.makeText(context, "previous", Toast.LENGTH_LONG).show();
                    break;
            }

        }
    };

}
