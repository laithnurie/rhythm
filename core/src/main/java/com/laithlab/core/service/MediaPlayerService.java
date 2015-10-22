package com.laithlab.core.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.laithlab.core.R;
import com.laithlab.core.activity.SwipePlayerActivity;
import com.laithlab.core.db.Song;
import com.laithlab.core.dto.SongDTO;
import com.laithlab.core.utils.MusicUtility;
import com.laithlab.core.utils.PlayBackUtil;
import com.laithlab.core.utils.RhythmSong;

import java.util.List;


public class MediaPlayerService extends Service {

    private static final String SONG_PARAM = "song";
    private static final String SONG_POSITION_PARAM = "songPosition";
    public static final int NOTIFICATION_ID = 104;

    private MediaPlayer mediaPlayer;
    private List<SongDTO> songDTOs;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer = PlayBackUtil.getMediaPlayer();

        if (mediaPlayer != null) {
            handleIntent(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("NewApi")
    private void handleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        String action = intent.getAction();
        int currentPosition = intent.getIntExtra(SONG_POSITION_PARAM, 0);
        Log.v("lnln", action + " - " + currentPosition);
        songDTOs = PlayBackUtil.getCurrentPlayList();
        for(int i = 0; i<songDTOs.size(); i++){
            Log.v("lnln", "song position - " + i + " - " +songDTOs.get(i).getSongTitle());
        }
        if (songDTOs != null && !songDTOs.isEmpty()) {
            RhythmSong rhythmSong = MusicUtility.getSongMeta(songDTOs.get(currentPosition).getSongLocation());
            Log.v("lnln", rhythmSong.toString());
            if (action.equalsIgnoreCase(Constants.ACTION_PLAY)) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
                setNotificationPlayer(false, rhythmSong, currentPosition);
            } else if (action.equalsIgnoreCase(Constants.ACTION_PAUSE)) {
                setNotificationPlayer(true, rhythmSong, currentPosition);
                mediaPlayer.pause();
            } else if (action.equalsIgnoreCase(Constants.ACTION_NEXT)) {
                setNotificationPlayer(false, rhythmSong, currentPosition);
                Intent nextIntent = new Intent("player");
                nextIntent.putExtra("player_command", "next");
                LocalBroadcastManager.getInstance(this).sendBroadcast(nextIntent);

            } else if (action.equalsIgnoreCase(Constants.ACTION_PREVIOUS)) {
                setNotificationPlayer(false, rhythmSong, currentPosition);
                Intent previousIntent = new Intent("player");
                previousIntent.putExtra("player_command", "previous");
                LocalBroadcastManager.getInstance(this).sendBroadcast(previousIntent);
            }
        }
    }

    private void setNotificationPlayer(boolean pause, RhythmSong rhythmSong, int currentPosition) {
        Intent playIntent;
        if (pause) {
            playIntent = getMediaIntent(currentPosition, Constants.ACTION_PLAY);
        } else{
            playIntent = getMediaIntent(currentPosition, Constants.ACTION_PAUSE);
        }

        Notification notificationCompat = createBuiderNotificationRemovable(rhythmSong).build();
        RemoteViews notiLayoutBig = new RemoteViews(getPackageName(), R.layout.notification_layout);

        notiLayoutBig.setOnClickPendingIntent(R.id.noti_play_button,
                PendingIntent.getService(this, 0, playIntent, 0));

        int nextPosition;
        if (currentPosition == songDTOs.size() - 1) {
            nextPosition = 0;
        } else {
            nextPosition = currentPosition + 1;
        }
        Log.v("lnln","new next - " + nextPosition);

        Intent nextIntent = getMediaIntent(nextPosition, Constants.ACTION_NEXT);
        notiLayoutBig.setOnClickPendingIntent(R.id.noti_next_button,
                PendingIntent.getService(this, 0, nextIntent, 0));

        int previousPosition;
        if (currentPosition == 0) {
            previousPosition = songDTOs.size() - 1;
        } else {
            previousPosition = currentPosition - 1;
        }
        Log.v("lnln","new previous - " + previousPosition);

        Intent previousIntent = getMediaIntent(previousPosition, Constants.ACTION_PREVIOUS);
        notiLayoutBig.setOnClickPendingIntent(R.id.noti_prev_button,
                PendingIntent.getService(this, 0, previousIntent, 0));

        if (Build.VERSION.SDK_INT >= 16) {
            notificationCompat.bigContentView = notiLayoutBig;
            notificationCompat.bigContentView.setImageViewResource(R.id.noti_play_button,
                    pause ? R.drawable.ic_play_arrow_white : R.drawable.ic_pause_white);

            if (rhythmSong != null) {
                notificationCompat.bigContentView.setTextViewText(R.id.noti_song_name, rhythmSong.getTrackTitle());
                notificationCompat.bigContentView.setTextViewText(R.id.noti_song_artist, rhythmSong.getArtistTitle());
                notificationCompat.bigContentView.setTextViewText(R.id.noti_song_album, rhythmSong.getAlbumTitle());
                byte[] imageData = rhythmSong.getImageData();
                if (imageData != null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                    notificationCompat.bigContentView.setImageViewBitmap(R.id.noti_album_art, bmp);
                } else {
                    notificationCompat.bigContentView.setImageViewResource(R.id.noti_album_art, R.drawable.ic_play_arrow_white);
                }
            }
        }
        notificationCompat.priority = Notification.PRIORITY_MAX;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, notificationCompat);
    }

    private Intent getMediaIntent(int position, String action) {
        Intent pendingIntent = new Intent(this, MediaPlayerService.class);
        pendingIntent.putExtra(SONG_POSITION_PARAM, position);
        pendingIntent.setAction(action);
        return pendingIntent;
    }

    private NotificationCompat.Builder createBuiderNotificationRemovable(RhythmSong rhythmSong) {
        Intent notificationIntent = new Intent(this, SwipePlayerActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuild = new NotificationCompat.Builder(this);
        byte[] imageData = rhythmSong.getImageData();
        if (imageData != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            notificationBuild.setLargeIcon(bmp);
        }
        return notificationBuild.setOngoing(false)
                .setContentTitle("Rhythm")
                .setContentText(rhythmSong.getArtistTitle() + " - " + rhythmSong.getTrackTitle())
                .setSmallIcon(R.drawable.ic_play_arrow_white)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(contentIntent);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.release();
        mediaPlayer.reset();
        mediaPlayer = null;
        return super.onUnbind(intent);
    }
}
