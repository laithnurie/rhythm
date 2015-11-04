package com.laithlab.core.service;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.Rating;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.laithlab.core.R;
import com.laithlab.core.dto.SongDTO;
import com.laithlab.core.utils.MusicDataUtility;
import com.laithlab.core.utils.PlayBackUtil;
import com.laithlab.core.utils.RhythmSong;

import java.util.List;


public class MediaPlayerServiceTwo extends Service {

    public static final String ACTION_PLAY = Constants.ACTION_PLAY;
    public static final String ACTION_PAUSE = Constants.ACTION_PAUSE;
    public static final String ACTION_REWIND = "action_rewind";
    public static final String ACTION_FAST_FORWARD = "action_fast_foward";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";
    private static final String SONG_POSITION_PARAM = "songPosition";


    private MediaPlayer mMediaPlayer;
    private MediaSessionManager mManager;
    private MediaSession mSession;
    private MediaController mController;
    private List<SongDTO> songDTOs;
    private RhythmSong rhythmSong;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void handleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        String action = intent.getAction();
        songDTOs = PlayBackUtil.getCurrentPlayList();

        int currentPosition = PlayBackUtil.getCurrentSongPosition();
        rhythmSong = MusicDataUtility.getSongMeta(songDTOs.get(currentPosition).getSongLocation());


        if (action.equalsIgnoreCase(ACTION_PLAY)) {
            mController.getTransportControls().play();
        } else if (action.equalsIgnoreCase(ACTION_PAUSE)) {
            mController.getTransportControls().pause();
        } else if (action.equalsIgnoreCase(ACTION_FAST_FORWARD)) {
            mController.getTransportControls().fastForward();
        } else if (action.equalsIgnoreCase(ACTION_REWIND)) {
            mController.getTransportControls().rewind();
        } else if (action.equalsIgnoreCase(ACTION_PREVIOUS)) {
            mController.getTransportControls().skipToPrevious();
        } else if (action.equalsIgnoreCase(ACTION_NEXT)) {
            mController.getTransportControls().skipToNext();
        } else if (action.equalsIgnoreCase(ACTION_STOP)) {
            mController.getTransportControls().stop();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    private Notification.Action generateAction(int icon, String title, String intentAction) {
        Intent intent = new Intent(getApplicationContext(), MediaPlayerServiceTwo.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new Notification.Action.Builder(icon, title, pendingIntent).build();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void buildNotification(Notification.Action action) {
        Notification.MediaStyle style = new Notification.MediaStyle();
        style.setMediaSession(mSession.getSessionToken());

        Intent intent = new Intent(getApplicationContext(), MediaPlayerServiceTwo.class);
        intent.setAction(ACTION_STOP);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);

        Notification.Builder builder = new Notification.Builder(this);
        byte[] imageData = rhythmSong.getImageData();
        if (imageData != null) {
            builder.setLargeIcon(getAlbumArt(imageData));
        }

        builder.setSmallIcon(R.drawable.ic_play_arrow_white);


        builder.setContentTitle(rhythmSong.getTrackTitle())
                .setContentText(rhythmSong.getArtistTitle())
                .setDeleteIntent(pendingIntent)
                .setUsesChronometer(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setStyle(style);

        builder.addAction(generateAction(R.drawable.ic_previous_arrow_white, "Previous", ACTION_PREVIOUS));
//        builder.addAction(generateAction(android.R.drawable.ic_media_rew, "Rewind", ACTION_REWIND));
        builder.addAction(action);
//        builder.addAction(generateAction(android.R.drawable.ic_media_ff, "Fast Foward", ACTION_FAST_FORWARD));
        builder.addAction(generateAction(R.drawable.ic_next_arrow_white, "Next", ACTION_NEXT));
//        style.setShowActionsInCompactView(0, 1, 2, 3, 4);
        style.setShowActionsInCompactView(0, 1, 2);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mManager == null) {
            initMediaSessions();
        }

        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initMediaSessions() {
        mMediaPlayer = PlayBackUtil.getMediaPlayer();

        mSession = new MediaSession(getApplicationContext(), "simple player session");
        mController = new MediaController(getApplicationContext(), mSession.getSessionToken());

        mSession.setCallback(new MediaSession.Callback() {
                                 @Override
                                 public void onPlay() {
                                     super.onPlay();
                                     mMediaPlayer.start();
                                     Log.e("MediaPlayerService", "onPlay");
                                     buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
                                 }

                                 @Override
                                 public void onPause() {
                                     super.onPause();
                                     mMediaPlayer.pause();
                                     Log.e("MediaPlayerService", "onPause");
                                     buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
                                 }

                                 @Override
                                 public void onSkipToNext() {
                                     super.onSkipToNext();
                                     Log.e("MediaPlayerService", "onSkipToNext");
                                     //Change media here
                                     buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
                                 }

                                 @Override
                                 public void onSkipToPrevious() {
                                     super.onSkipToPrevious();
                                     Log.e("MediaPlayerService", "onSkipToPrevious");
                                     //Change media here
                                     buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
                                 }

                                 @Override
                                 public void onFastForward() {
                                     super.onFastForward();
                                     Log.e("MediaPlayerService", "onFastForward");
                                     //Manipulate current media here
                                 }

                                 @Override
                                 public void onRewind() {
                                     super.onRewind();
                                     Log.e("MediaPlayerService", "onRewind");
                                     //Manipulate current media here
                                 }

                                 @Override
                                 public void onStop() {
                                     super.onStop();
                                     Log.e("MediaPlayerService", "onStop");
                                     //Stop media player here
                                     NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                     notificationManager.cancel(1);
                                     Intent intent = new Intent(getApplicationContext(), MediaPlayerServiceTwo.class);
                                     stopService(intent);
                                 }

                                 @Override
                                 public void onSeekTo(long pos) {
                                     super.onSeekTo(pos);
                                 }

                                 @Override
                                 public void onSetRating(Rating rating) {
                                     super.onSetRating(rating);
                                 }
                             }
        );
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onUnbind(Intent intent) {
        mSession.release();
        return super.onUnbind(intent);
    }

    private Bitmap getAlbumArt(byte[] imageData) {
        Resources res = getResources();
        int height = (int) res.getDimension(android.R.dimen.notification_large_icon_height);
        int width = (int) res.getDimension(android.R.dimen.notification_large_icon_width);
        Bitmap largeIcon = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        return Bitmap.createScaledBitmap(largeIcon, width, height, false);
    }
}