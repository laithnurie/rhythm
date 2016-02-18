package com.laithlab.rhythm.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.laithlab.rhythm.R;
import com.laithlab.rhythm.customview.CircularSeekBar;
import com.laithlab.rhythm.customview.CustomAnimUtil;
import com.laithlab.rhythm.dto.SongDTO;
import com.laithlab.rhythm.service.Constants;
import com.laithlab.rhythm.service.MediaPlayerService;
import com.laithlab.rhythm.utils.BitmapUtils;
import com.laithlab.rhythm.utils.MusicDataUtility;
import com.laithlab.rhythm.utils.PlayBackUtil;
import com.laithlab.rhythm.utils.RhythmSong;
import com.laithlab.rhythm.utils.TimeFormatUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class SongFragment extends Fragment implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener {
    private static final String SONG_PARAM = "song";
    private static final String SONG_POSITION_PARAM = "songPosition";

    private SongFragmentCallback mListener;
    private SongDTO song;
    private RhythmSong rhythmSong;
    private int songPosition;
    private MediaPlayer mediaPlayer;

    private TextView track;
    private CircularSeekBar trackProgress;
    private CircleImageView albumCover;
    private TextView txtDuration;
    private ImageView playButton;
    private int vibrantColor;
    private Handler handler;

    private boolean beenDrawn = false;


    public static SongFragment newInstance(SongDTO song, int position) {
        SongFragment fragment = new SongFragment();
        Bundle args = new Bundle();
        args.putParcelable(SONG_PARAM, song);
        args.putInt(SONG_POSITION_PARAM, position);
        fragment.setArguments(args);
        return fragment;
    }

    public SongFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().size() > 0) {
            song = getArguments().getParcelable(SONG_PARAM);
            songPosition = getArguments().getInt(SONG_POSITION_PARAM);
            rhythmSong = MusicDataUtility.getSongMeta(song.getSongLocation());
        }
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_song, container, false);
        beenDrawn = true;
        track = (TextView) rootView.findViewById(R.id.txt_track);
        txtDuration = (TextView) rootView.findViewById(R.id.txt_duration);
        playButton = (ImageView) rootView.findViewById(R.id.play_button);

        trackProgress = (CircularSeekBar) rootView.findViewById(R.id.track_progress);
        trackProgress.setMax(100);

        trackProgress.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null) {
                    float currentDuration = (((float) circularSeekBar.getProgress() / 100) * mediaPlayer.getDuration());
                    updateDuration(milliSecondsToTimer((long) currentDuration), TimeFormatUtils.secondsToTimer(mediaPlayer.getDuration() / 1000));
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                mediaPlayer.start();
                startTimer();
                int currentMill = (int) (((float) seekBar.getProgress() / 100) * mediaPlayer.getDuration());
                mediaPlayer.seekTo(currentMill);
                playButton.setImageResource(R.drawable.ic_pause_white);
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
                stopTimer();
            }
        });

        albumCover = (CircleImageView) rootView.findViewById(R.id.album_cover);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    playButton.setImageResource(R.drawable.ic_play_arrow_white);
                    mediaPlayer.pause();
                    stopTimer();
                    playerNotification(Constants.ACTION_PAUSE);
                } else {
                    playButton.setImageResource(R.drawable.ic_pause_white);
                    CustomAnimUtil.overShootAnimation(albumCover);
                    mediaPlayer.start();
                    startTimer();
                    playerNotification(Constants.ACTION_PLAY);
                }
            }
        });
        removePlayerListeners();
        setPlayerListeners();
        if (mediaPlayer != null && mediaPlayer.isPlaying() && rhythmSong.getSongLocation().equals(PlayBackUtil.getCurrentSong().getSongLocation())) {
            startTimer();
        }

        updatePlayerUI();
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (SongFragmentCallback) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mediaPlayer == null) {
                if (mListener.songChangedFromNotification()) {
                    mediaPlayer = PlayBackUtil.getMediaPlayer();
                    mListener.resetChangedSongFromNotification();
                } else {
                    if (PlayBackUtil.getCurrentSong() != null && rhythmSong.getSongLocation()
                            .equals(PlayBackUtil.getCurrentSong().getSongLocation())) {
                        mediaPlayer = PlayBackUtil.getMediaPlayer();
                        startTimer();

                    } else {
                        PlayBackUtil.setCurrentSong(rhythmSong);
                        mediaPlayer = PlayBackUtil.setMediaPlayerOne(getContext()
                                .getApplicationContext(), rhythmSong.getSongLocation());
                    }
                }
                removePlayerListeners();
                setPlayerListeners();
            }

            if (beenDrawn) {
                trackProgress.setProgress(0);
                updateDuration("0:00", milliSecondsToTimer(mediaPlayer.getDuration()));
            }
            mListener.setToolBarText(rhythmSong.getArtistTitle(), rhythmSong.getAlbumTitle());
            mListener.changePlayerStyle(vibrantColor, songPosition);
            PlayBackUtil.setCurrentSong(rhythmSong);
        } else {
            if (mediaPlayer != null) {
                stopTimer();
                removePlayerListeners();
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }

    private void setPlayerListeners() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnInfoListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setScreenOnWhilePlaying(false);
        }
    }

    private void removePlayerListeners() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnErrorListener(null);
            mediaPlayer.setOnInfoListener(null);
            mediaPlayer.setOnPreparedListener(null);
            mediaPlayer.setScreenOnWhilePlaying(false);
            stopTimer();
        }
    }

    @Override
    public void onPrepared(final MediaPlayer mp) {
        updateDuration("0:00", milliSecondsToTimer(mp.getDuration()));
        startTimer();
        mp.start();
        playerNotification(Constants.ACTION_PLAY);
        updateSongCount();
    }

    private void updateSongCount() {
        MusicDataUtility.updateSongCount(song.getId(), getContext());
    }

    private void playerNotification(String action) {
        Intent intent = new Intent(getContext().getApplicationContext(), MediaPlayerService.class);
        intent.setAction(action);
        intent.putExtra(SONG_PARAM, rhythmSong);
        intent.putExtra(SONG_POSITION_PARAM, songPosition);
        getActivity().startService(intent);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        removePlayerListeners();
        mediaPlayer = PlayBackUtil.setMediaPlayerOne(getContext().getApplicationContext(),
                rhythmSong.getSongLocation());
        setPlayerListeners();
        return false;
    }


    private void startTimer() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopTimer();
                trackProgress.setProgress(0);
                updateDuration("0:00", milliSecondsToTimer(mediaPlayer.getDuration()));
                playButton.setImageResource(R.drawable.ic_play_arrow_white);
                mediaPlayer.pause();

                switch (PlayBackUtil.getCurrentPlayMode()) {
                    case SHUFFLE_REPEAT:
                    case ALL_REPEAT:
                        playNext();
                        break;
                    case NONE:
                        if (songPosition != PlayBackUtil.getCurrentSongPosition()) {
                            playNext();
                        }
                        break;
                    case SINGLE_REPEAT:
                        startTimer();
                        mediaPlayer.start();
                        break;
                }
            }
        });
        handler.postDelayed(mRunnable, 500);
    }

    private void playNext() {
        if (mListener != null) {
            mListener.playNext();
        } else {
            Intent intent = new Intent(getContext().getApplicationContext(), MediaPlayerService.class);
            intent.setAction(Constants.ACTION_NEXT);
            getContext().getApplicationContext().startService(intent);
        }
    }

    private void stopTimer() {
        if (handler != null) {
            handler.removeCallbacks(mRunnable);
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && beenDrawn) {
                if (!mediaPlayer.isPlaying() || !trackProgress.isPressed()) {
                    final int currentProgress = (int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                trackProgress.setProgress(currentProgress);
                                if (mediaPlayer != null) {
                                    playButton.setImageResource(mediaPlayer.isPlaying()
                                            ? R.drawable.ic_pause_white : R.drawable.ic_play_arrow_white);
                                    updateDuration(milliSecondsToTimer(mediaPlayer.getCurrentPosition()),
                                            milliSecondsToTimer(mediaPlayer.getDuration()));
                                }
                            }
                        });
                    }
                }
            }
            handler.postDelayed(mRunnable, 500);
        }
    };

    private void updateDuration(String currentDuration, String totalDuration) {
        if (getActivity() != null) {
            txtDuration.setText(getActivity().getResources().getString(R.string.duration_format, currentDuration, totalDuration));
        }
    }

    private void updatePlayerUI() {
        updateDuration("0:00", milliSecondsToTimer(rhythmSong.getDuration()));
        track.setText(rhythmSong.getTrackTitle());
        if (rhythmSong.getImageData() != null) {
            final Bitmap bmp = BitmapUtils.decodeSampledBitmapFromResource(rhythmSong.getImageData(), 200, 200);
            albumCover.setImageBitmap(bmp);
            Palette.Swatch vibrantSwatch = Palette.generate(bmp).getLightVibrantSwatch();
            if (vibrantSwatch != null) {
                vibrantColor = vibrantSwatch.getRgb();
                changePlayerStyle(vibrantSwatch.getRgb());
                mListener.changePlayerStyle(vibrantColor, songPosition);
            } else {
                vibrantColor = getResources().getColor(R.color.color_primary);
                mListener.changePlayerStyle(vibrantColor, songPosition);
            }
        }
    }

    private void changePlayerStyle(int vibrantColor) {
        track.setTextColor(vibrantColor);
        txtDuration.setTextColor(vibrantColor);
        trackProgress.setCircleProgressColor(vibrantColor);
        trackProgress.setPointerColor(vibrantColor);
        String alphaColor = Integer.toHexString(vibrantColor);
        if (alphaColor.length() > 6) {
            alphaColor = "#88" + alphaColor.substring(2);
        } else {
            alphaColor = "#88" + alphaColor;

        }
        trackProgress.setPointerAlphaOnTouch(Color.parseColor(alphaColor));
        trackProgress.setPointerHaloColor(Color.parseColor(alphaColor));
    }

    private String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString;

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }
}
