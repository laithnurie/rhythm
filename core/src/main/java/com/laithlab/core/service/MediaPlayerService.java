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
import android.widget.RemoteViews;
import com.laithlab.core.R;
import com.laithlab.core.activity.SwipePlayerActivity;
import com.laithlab.core.utils.PlayBackUtil;
import com.laithlab.core.utils.RhythmSong;


public class MediaPlayerService extends Service {

	private static final String SONG_PARAM = "song";

	private MediaPlayer m_objMediaPlayer;
	private Notification notificationCompat;
	private NotificationManagerCompat notificationManager;
	private RemoteViews notiLayoutBig;

	public static final int NOTIFICATION_ID = 104;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		m_objMediaPlayer = PlayBackUtil.getMediaPlayer();

		handleIntent(intent);
		return super.onStartCommand(intent, flags, startId);
	}

	@SuppressLint("NewApi")
	private void handleIntent(Intent intent) {
		if (intent == null || intent.getAction() == null)
			return;

		String action = intent.getAction();

		if (action.equalsIgnoreCase(Constants.ACTION_PLAY)) {
			if (!m_objMediaPlayer.isPlaying()) {
				m_objMediaPlayer.start();
			}
			setNotificationPlayer(false, intent);
		} else if (action.equalsIgnoreCase(Constants.ACTION_PAUSE)) {
			setNotificationPlayer(true, intent);
			m_objMediaPlayer.pause();
		}
//        createWearNotification();
    }

	private void setNotificationPlayer(boolean pause, Intent intent) {
		Intent pendingIntent = new Intent(this, MediaPlayerService.class);
		if (pause) {
			pendingIntent.setAction(Constants.ACTION_PLAY);
		} else {
			pendingIntent.setAction(Constants.ACTION_PAUSE);
		}

		notificationCompat = createBuiderNotificationRemovable(pause).build();
		notiLayoutBig = new RemoteViews(getPackageName(), R.layout.notification_layout);

		notiLayoutBig.setOnClickPendingIntent(R.id.noti_play_button,
				PendingIntent.getService(this, 0, pendingIntent, 0));
		if (Build.VERSION.SDK_INT >= 16) {
			notificationCompat.bigContentView = notiLayoutBig;
			notificationCompat.bigContentView.setImageViewResource(R.id.noti_play_button,
					pause ? R.drawable.ic_play_arrow_white : R.drawable.ic_pause_white);

			RhythmSong rhythmSong = intent.getParcelableExtra(SONG_PARAM);
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
		notificationManager = NotificationManagerCompat.from(this);
		startForeground(NOTIFICATION_ID, notificationCompat);
		notificationManager.notify(NOTIFICATION_ID, notificationCompat);
	}

	private NotificationCompat.Builder createBuiderNotificationRemovable(boolean pause) {
		Intent notificationIntent = new Intent(this, SwipePlayerActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		return new NotificationCompat.Builder(this)
				.setOngoing(false)
				.setSmallIcon(pause ? R.drawable.ic_pause_white : R.drawable.ic_play_arrow_white)
				.setContentIntent(contentIntent);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onUnbind(Intent intent) {
		m_objMediaPlayer.release();
		m_objMediaPlayer.reset();
		m_objMediaPlayer = null;
		return super.onUnbind(intent);
	}

    private void createWearNotification(){
        int notificationId = 001;
// Build intent for notification content
        Intent notificationIntent = new Intent(this, SwipePlayerActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_pause_white)
                        .setContentTitle("Wear notification")
                        .setContentText("Hello !")
                        .setPriority(Notification.PRIORITY_MAX)
                        .setContentIntent(viewPendingIntent);

// Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

// Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
