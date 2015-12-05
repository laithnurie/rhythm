package com.laithlab.core.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.laithlab.core.R;
import com.laithlab.core.activity.SwipePlayerActivity;
import com.laithlab.core.dto.SongDTO;
import com.laithlab.core.utils.MusicDataUtility;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements Filterable {
    private Context context;
    private final List<SongDTO> originalSongsList;
    private List<SongDTO> currentSongsList;
    private MusicFilter musicFilter;

    public SearchAdapter(Context context, List<SongDTO> songs) {
        this.context = context;
        this.originalSongsList = songs;
        this.currentSongsList = songs;
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.song_list_item, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.songTitle.setText(currentSongsList.get(position).getSongTitle());
        holder.songDuration.setText(MusicDataUtility.secondsToTimer(currentSongsList.get(position).getSongDuration()));
    }

    @Override
    public int getItemCount() {
        return currentSongsList.size();
    }

    @Override
    public Filter getFilter() {
        if(musicFilter == null){
            musicFilter = new MusicFilter();
        }
        return musicFilter;
    }

    private class MusicFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults filterResults = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                filterResults.values = originalSongsList;
                filterResults.count = originalSongsList.size();
            } else {
                ArrayList<SongDTO> filteredSongList = new ArrayList<SongDTO>();
                for (SongDTO song : originalSongsList) {
                    if (song.getSongTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredSongList.add(song);
                    }
                }
                filterResults.values = filteredSongList;
                filterResults.count = filteredSongList.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.count > 0) {
                currentSongsList = (ArrayList<SongDTO>) results.values;
                SearchAdapter.this.notifyDataSetChanged();
            } else {
                SearchAdapter.this.notifyDataSetChanged();
            }
        }
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
            playerActivity.putParcelableArrayListExtra("songs", (ArrayList<? extends Parcelable>) currentSongsList);
            playerActivity.putExtra("songPosition", getLayoutPosition());
            context.startActivity(playerActivity);
        }
    }
}