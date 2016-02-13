package com.laithlab.core.adapter;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laithlab.core.R;
import com.laithlab.core.dto.SongDTO;
import com.laithlab.core.utils.ContentType;
import com.laithlab.core.utils.MusicDataUtility;

import java.util.List;

public class SongListAdapter extends SelectableAdapter<SongListAdapter.ViewHolder> {

    private List<SongDTO> songs;
    private final ContentType contentType;
    private ClickListener clickListener;

    public SongListAdapter(List<SongDTO> songs, ClickListener clickListener, ContentType contentType) {
        this.songs = songs;
        this.clickListener = clickListener;
        this.contentType = contentType;
    }

    @Override
    public SongListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView;

        if(contentType == ContentType.PLAYLIST){
            contactView = inflater.inflate(R.layout.remove_song_list_item, parent, false);
        } else {
            contactView = inflater.inflate(R.layout.add_song_list_item, parent, false);
        }

        return new ViewHolder(contactView, clickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.songTitle.setText(songs.get(position).getSongTitle());
        holder.songDuration.setText(MusicDataUtility.secondsToTimer(songs.get(position).getSongDuration()));

        ObjectAnimator animX;
        if (isSelected(position)) {
            animX = ObjectAnimator.ofFloat(holder.rowView, "x", 150f);
        } else {
            animX = ObjectAnimator.ofFloat(holder.rowView, "x", 0f);
        }
        animX.setDuration(250);
        animX.start();
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void updateSongs(List<SongDTO> songs){
        this.songs = songs;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView songTitle;
        public TextView songDuration;
        public View rowView;
        private ClickListener clickListener;

        public ViewHolder(View v, ClickListener clickListener) {
            super(v);
            songTitle = (TextView) v.findViewById(R.id.txt_song_item_title);
            songDuration = (TextView) v.findViewById(R.id.txt_song_item_duration);
            rowView = v.findViewById(R.id.row_view);
            this.clickListener = clickListener;
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onItemClicked(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return clickListener != null && clickListener.onItemLongClicked(getLayoutPosition());
        }
    }

    public interface ClickListener {
        void onItemClicked(int position);

        boolean onItemLongClicked(int position);
    }
}