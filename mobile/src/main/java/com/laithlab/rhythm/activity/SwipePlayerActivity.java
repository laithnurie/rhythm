package com.laithlab.rhythm.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;

import com.laithlab.rhythm.R;
import com.laithlab.rhythm.dto.SongDTO;
import com.laithlab.rhythm.fragment.SongFragment;
import com.laithlab.rhythm.fragment.SongFragmentCallback;
import com.laithlab.rhythm.service.Constants;
import com.laithlab.rhythm.service.SendToDataLayerThread;
import com.laithlab.rhythm.utils.MusicDataUtility;
import com.laithlab.rhythm.utils.PlayBackUtil;
import com.laithlab.rhythm.utils.PlayMode;
import com.laithlab.rhythm.utils.RhythmSong;
import com.laithlab.rhythm.utils.ViewUtils;

import java.util.Collections;
import java.util.List;

import static com.laithlab.rhythm.utils.PlayMode.*;

public class SwipePlayerActivity extends RhythmActivity implements SongFragmentCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private View tiltedView;
    private TextView artist;
    private TextView album;
    private ViewPager viewPager;
    private Menu menu;
    private View playerBackground;

    private boolean isWearConnected = false;
    private boolean changedSongFromNotification = false;
    public static String SONG_POSITION_PARAM = "songPosition";
    public static String SONGS_PARAM = "songs";
    private int songPosition;
    private List<SongDTO> songsList;

    private GoogleApiClient googleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_player);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(Constants.PLAYER));

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
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tiltedView = findViewById(R.id.tilted_view);
        playerBackground = findViewById(R.id.player_background);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.color_primary));
        viewPager = (ViewPager) findViewById(R.id.pager);

        artist = (TextView) findViewById(R.id.txt_artist);
        album = (TextView) findViewById(R.id.txt_album);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            songPosition = extras.getInt(SONG_POSITION_PARAM);
            songsList = extras.getParcelableArrayList(SONGS_PARAM);
            PlayBackUtil.setCurrentPlayList(songsList);
            PlayBackUtil.setCurrentSongPosition(songPosition);
        } else {
            songsList = PlayBackUtil.getCurrentPlayList();
            songPosition = PlayBackUtil.getCurrentSongPosition();
        }

        if (songsList != null && songsList.size() > 0) {
            populateSongs(songsList, songPosition);
        }
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

        ViewUtils.drawerClickListener(this);
    }

    private void populateSongs(List<SongDTO> songsList, int songPosition) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            viewPager.setAdapter(new SongFragmentPager(this.getSupportFragmentManager(),
                    songsList));
            if (songPosition > 0) {
                viewPager.setCurrentItem(songPosition, true);
            }
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
        this.menu = menu;
        updateMenu(PlayBackUtil.getCurrentPlayMode());
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
        } else if (i == R.id.repeat_mode) {
            updateMenu(PlayBackUtil.getUpdateCurrentPlayMode(REPEAT));
        } else if (i == R.id.shuffle_mode) {
            updateMenu(PlayBackUtil.getUpdateCurrentPlayMode(SHUFFLE));
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMenu(PlayMode playMode) {
        MenuItem shuffle = menu.findItem(R.id.shuffle_mode);
        MenuItem repeat = menu.findItem(R.id.repeat_mode);
        switch (playMode) {
            case NONE:
                shuffle.setIcon(R.drawable.ic_shuffle_grey_24dp);
                repeat.setIcon(R.drawable.ic_repeat_grey_24dp);
                break;
            case SHUFFLE:
                shuffle.setIcon(R.drawable.ic_shuffle_white_24dp);
                repeat.setIcon(R.drawable.ic_repeat_grey_24dp);
                shuffleSongs();
                break;
            case SINGLE_REPEAT:
                shuffle.setIcon(R.drawable.ic_shuffle_grey_24dp);
                repeat.setIcon(R.drawable.ic_repeat_one_white_24dp);
                break;
            case ALL_REPEAT:
                shuffle.setIcon(R.drawable.ic_shuffle_grey_24dp);
                repeat.setIcon(R.drawable.ic_repeat_white_24dp);
                break;
            case SHUFFLE_REPEAT:
                shuffle.setIcon(R.drawable.ic_shuffle_white_24dp);
                repeat.setIcon(R.drawable.ic_repeat_white_24dp);
                break;
        }
    }

    private void shuffleSongs() {
        SongDTO currentSong = songsList.get(songPosition);
        Collections.shuffle(songsList);
        songsList.remove(currentSong);
        songsList.add(0, currentSong);
        songPosition = 0;
        PlayBackUtil.setCurrentSongPosition(songPosition);
        PlayBackUtil.setCurrentPlayList(songsList);
        populateSongs(songsList, songPosition);
    }

    @Override
    public void changePlayerStyle(int vibrantColor, int backgroundColor, int songPosition) {
        if (songPosition == viewPager.getCurrentItem()) {
            if (vibrantColor == 0) {
                vibrantColor = getResources().getColor(R.color.color_primary);
            }
            if (backgroundColor == 0) {
                backgroundColor = getResources().getColor(R.color.color_primary_dark);
            }
            toolbar.setBackgroundColor(vibrantColor);
            tiltedView.setBackgroundColor(vibrantColor);
            playerBackground.setBackgroundColor(backgroundColor);
        }
        this.songPosition = songPosition;
    }

    @Override
    public void setToolBarText(String artistTitle, String albumTitle) {
        artist.setText(artistTitle);
        album.setText(albumTitle);
    }

    @Override
    public void resetChangedSongFromNotification() {
        changedSongFromNotification = false;
    }

    @Override
    public boolean songChangedFromNotification() {
        return changedSongFromNotification;
    }

    @Override
    public void playNext() {
        handleCommand("next");
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
            handleCommand(command);
            changedSongFromNotification = true;
        }
    };

    private void handleCommand(String command) {
        int currentSongIndex = viewPager.getCurrentItem();
        int lastSongIndex = viewPager.getAdapter().getCount() - 1;

        switch (command) {
            case "next":
                if (currentSongIndex == lastSongIndex) {
                    viewPager.setCurrentItem(0);
                } else {
                    viewPager.setCurrentItem(currentSongIndex + 1);
                }
                break;
            case "previous":
                if (currentSongIndex == 0) {
                    viewPager.setCurrentItem(lastSongIndex);
                } else {
                    viewPager.setCurrentItem(currentSongIndex - 1);
                }
                break;
        }
    }

}
