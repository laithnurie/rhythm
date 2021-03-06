package com.laithlab.rhythm.service;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;

import com.laithlab.rhythm.R;
import com.laithlab.rhythm.activity.SwipePlayerActivity;
import com.laithlab.rhythm.dto.SongDTO;
import com.laithlab.rhythm.utils.BitmapUtils;
import com.laithlab.rhythm.utils.MusicDataUtility;
import com.laithlab.rhythm.utils.PlayBackUtil;
import com.laithlab.rhythm.utils.PlayMode;
import com.laithlab.rhythm.utils.RhythmSong;

import java.util.List;
import timber.log.Timber;

public class MediaPlayerService extends Service {

    private static int NOTIFICATION_ID = 17;

    private MediaPlayer mMediaPlayer;
    private MediaSessionCompat mSession;
    private MediaControllerCompat mController;
    private List<SongDTO> songDTOs;
    private int currentPosition;
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

        currentPosition = PlayBackUtil.getCurrentSongPosition();
        rhythmSong = MusicDataUtility.getSongMeta(songDTOs.get(currentPosition).getSongLocation());

        if (action.equalsIgnoreCase(Constants.ACTION_PLAY)) {
            mController.getTransportControls().play();
        } else if (action.equalsIgnoreCase(Constants.ACTION_PAUSE)) {
            mController.getTransportControls().pause();
        } else if (action.equalsIgnoreCase(Constants.ACTION_PREVIOUS)) {
            mController.getTransportControls().skipToPrevious();
        } else if (action.equalsIgnoreCase(Constants.ACTION_NEXT)) {
            mController.getTransportControls().skipToNext();
        } else if (action.equalsIgnoreCase(Constants.ACTION_STOP)) {
            mController.getTransportControls().stop();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    private NotificationCompat.Action generateAction(int icon, String intentAction) {
        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new NotificationCompat.Action.Builder(icon, intentAction, pendingIntent).build();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void buildNotification(NotificationCompat.Action action) {
        NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();
        style.setMediaSession(mSession.getSessionToken());

        Intent playerIntent = new Intent(getApplicationContext(), SwipePlayerActivity.class);
        playerIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                playerIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
        intent.setAction(Constants.ACTION_STOP);
        PendingIntent deleteIntent = PendingIntent.getService(getApplicationContext(), 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        byte[] imageData = rhythmSong.getImageData();
        if (imageData != null) {
            builder.setLargeIcon(getAlbumArt(imageData));
        }

        builder.setContentTitle(rhythmSong.getTrackTitle())
                .setSmallIcon(R.drawable.ic_play_arrow_white_24dp)
                .setContentText(rhythmSong.getArtistTitle())
                .setDeleteIntent(deleteIntent)
                .setContentIntent(contentIntent)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(false)
                .setStyle(style);

        builder.addAction(generateAction(R.drawable.ic_previous_arrow_white_24dp, Constants.ACTION_PREVIOUS));
        builder.addAction(action);
        builder.addAction(generateAction(R.drawable.ic_next_arrow_white_24dp, Constants.ACTION_NEXT));
        style.setShowActionsInCompactView(0, 1, 2);

        if(action.getTitle().equals(Constants.ACTION_PAUSE)){
            builder.setOngoing(true).setPriority(Notification.PRIORITY_HIGH);
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initMediaSessions();

        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initMediaSessions() {
        mMediaPlayer = PlayBackUtil.getMediaPlayer();

        mSession = new MediaSessionCompat(getApplicationContext(), "simple player session");
        try {
            mController = new MediaControllerCompat(getApplicationContext(), mSession.getSessionToken());
        } catch (RemoteException e) {
            Timber.e(e, "initMediaSessions");
        }

        mSession.setCallback(
                new MediaSessionCompat.Callback() {
                    @Override
                    public void onPlay() {
                        super.onPlay();
                        mMediaPlayer.start();
                        buildNotification(generateAction(R.drawable.ic_pause_white_24dp, Constants.ACTION_PAUSE));
                    }

                    @Override
                    public void onPause() {
                        super.onPause();
                        mMediaPlayer.pause();
                        buildNotification(generateAction(R.drawable.ic_play_arrow_white_24dp, Constants.ACTION_PLAY));
                    }

                    @Override
                    public void onSkipToNext() {
                        super.onSkipToNext();
                        int lastIndex = songDTOs.size() - 1;
                        if (currentPosition == lastIndex) {
                            currentPosition = 0;
                            if (PlayBackUtil.getCurrentPlayMode() != PlayMode.ALL_REPEAT
                                    || PlayBackUtil.getCurrentPlayMode() != PlayMode.SHUFFLE_REPEAT) {
                                mMediaPlayer.stop();
                                buildNotification(generateAction(R.drawable.ic_play_arrow_white_24dp, Constants.ACTION_PLAY));
                                PlayBackUtil.setCurrentSongPosition(currentPosition);
                                return;
                            }
                        } else {
                            currentPosition++;
                        }
                        reInitialiseMediaSession(currentPosition);

                        rhythmSong = MusicDataUtility.getSongMeta(songDTOs.get(currentPosition).getSongLocation());

                        buildNotification(generateAction(R.drawable.ic_pause_white_24dp, Constants.ACTION_PAUSE));

                        Intent nextIntent = new Intent(Constants.PLAYER);
                        nextIntent.putExtra("player_command", "next");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(nextIntent);
                    }

                    @Override
                    public void onSkipToPrevious() {
                        super.onSkipToPrevious();
                        //Change media here
                        if (currentPosition == 0) {
                            currentPosition = songDTOs.size() - 1;
                        } else {
                            currentPosition--;
                        }
                        reInitialiseMediaSession(currentPosition);

                        rhythmSong = MusicDataUtility.getSongMeta(songDTOs.get(currentPosition).getSongLocation());

                        buildNotification(generateAction(R.drawable.ic_pause_white_24dp, Constants.ACTION_PAUSE));

                        Intent previousIntent = new Intent(Constants.PLAYER);
                        previousIntent.putExtra("player_command", "previous");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(previousIntent);
                    }

                    @Override
                    public void onStop() {
                        super.onStop();
                        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(NOTIFICATION_ID);
                        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
                        mMediaPlayer.pause();
                        stopService(intent);
                    }
                }
        );
    }

    private void reInitialiseMediaSession(int currentPosition) {
        PlayBackUtil.setCurrentSongPosition(currentPosition);
        mMediaPlayer = PlayBackUtil.setMediaPlayer(getApplicationContext(),
                songDTOs.get(currentPosition).getSongLocation());
        mMediaPlayer.start();
        initMediaSessions();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onUnbind(Intent intent) {
        mSession.release();
        cancelNotification();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        cancelNotification();
        mSession.release();
    }

    private void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        cancelNotification();
        super.onTaskRemoved(rootIntent);
    }

    private Bitmap getAlbumArt(byte[] imageData) {
        Resources res = getResources();
        int height = (int) res.getDimension(android.R.dimen.notification_large_icon_height);
        int width = (int) res.getDimension(android.R.dimen.notification_large_icon_width);
        Bitmap largeIcon = BitmapUtils.decodeSampledBitmapFromResource(imageData, 200, 200);
        return Bitmap.createScaledBitmap(largeIcon, width, height, false);
    }
}