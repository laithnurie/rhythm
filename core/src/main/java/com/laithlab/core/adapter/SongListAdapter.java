package com.laithlab.core.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laithlab.core.R;
import com.laithlab.core.activity.SwipePlayerActivity;
import com.laithlab.core.dto.SongDTO;
import com.laithlab.core.utils.MusicDataUtility;

import java.util.ArrayList;
import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {
    private Context context;
    private final List<SongDTO> songs;

    public SongListAdapter(Context context, List<SongDTO> songs) {
        this.context = context;
        this.songs = songs;
    }

    @Override
    public SongListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.song_list_item, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.songTitle.setText(songs.get(position).getSongTitle());
        holder.songDuration.setText(MusicDataUtility.secondsToTimer(songs.get(position).getSongDuration()));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView songTitle;
        public TextView songDuration;

        public ViewHolder(View v) {
            super(v);
            songTitle = (TextView) v.findViewById(R.id.txt_song_item_title);
            songDuration = (TextView) v.findViewById(R.id.txt_song_item_duration);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent playerActivity = new Intent(context, SwipePlayerActivity.class);
            playerActivity.putParcelableArrayListExtra("songs", (ArrayList<? extends Parcelable>) songs);
            playerActivity.putExtra("songPosition", getLayoutPosition());
            context.startActivity(playerActivity);
        }
    }
}