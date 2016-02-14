package com.laithlab.rhythm.adapter;


import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.laithlab.rhythm.R;
import com.laithlab.rhythm.activity.PlaylistActivity;
import com.laithlab.rhythm.activity.ArtistActivity;
import com.laithlab.rhythm.activity.SwipePlayerActivity;
import com.laithlab.rhythm.converter.DTOConverter;
import com.laithlab.rhythm.dto.SearchResult;
import com.laithlab.rhythm.dto.SongDTO;
import com.laithlab.rhythm.utils.MusicDataUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final List<SearchResult> originalSearchResults;
    private List<SearchResult> currentSearchResults;
    private MusicFilter musicFilter;

    private static final int TYPE_SONG = 0;
    private static final int TYPE_ALBUM = 1;
    private static final int TYPE_ARTIST = 2;
    private static final int TYPE_HEADER = 3;

    public SearchAdapter(List<SearchResult> searchResults) {
        this.originalSearchResults = searchResults;
        this.currentSearchResults = searchResults;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;
        if (currentSearchResults.get(position).getResultType() == SearchResult.ResultType.ARTIST) {
            viewType = TYPE_ARTIST;
        } else if (currentSearchResults.get(position).getResultType() == SearchResult.ResultType.ALBUM) {
            viewType = TYPE_ALBUM;
        } else if (currentSearchResults.get(position).getResultType() == SearchResult.ResultType.SONG) {
            viewType = TYPE_SONG;
        } else {
            viewType = TYPE_HEADER;
        }
        return viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_SONG:
                ViewGroup vSong = (ViewGroup) mInflater.inflate(R.layout.search_song_item, parent, false);
                return new SongViewHolder(vSong);
            case TYPE_ALBUM:
                ViewGroup vAlbum = (ViewGroup) mInflater.inflate(R.layout.search_album_item, parent, false);
                return new AlbumViewHolder(vAlbum);
            case TYPE_ARTIST:
                ViewGroup vArtist = (ViewGroup) mInflater.inflate(R.layout.search_artist_item, parent, false);
                return new ArtistViewHolder(vArtist);
            case TYPE_HEADER:
                ViewGroup vHeader = (ViewGroup) mInflater.inflate(R.layout.search_header, parent, false);
                return new HeaderViewHolder(vHeader);
            default:
                ViewGroup vDefault = (ViewGroup) mInflater.inflate(R.layout.search_song_item, parent, false);
                return new SongViewHolder(vDefault);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (currentSearchResults.get(position).getResultType()) {
            case SONG:
                SongViewHolder songViewHolder = (SongViewHolder) holder;
                songViewHolder.songTitle.setText(currentSearchResults.get(position).getMainTitle());
                songViewHolder.songDuration.setText(currentSearchResults.get(position).getSubTitle());
                break;

            case ALBUM:
                AlbumViewHolder albumViewHolder = (AlbumViewHolder) holder;
                albumViewHolder.albumTitle.setText(currentSearchResults.get(position).getMainTitle());
                albumViewHolder.artistTitle.setText(currentSearchResults.get(position).getSubTitle());
                break;

            case ARTIST:
                ArtistViewHolder artistViewHolder = (ArtistViewHolder) holder;
                artistViewHolder.artistName.setText(currentSearchResults.get(position).getMainTitle());
                artistViewHolder.artistDetails.setText(currentSearchResults.get(position).getSubTitle());
                break;
            case HEADER:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.headerText.setText(currentSearchResults.get(position).getMainTitle());
                break;
        }

    }

    @Override
    public int getItemCount() {
        return currentSearchResults.size();
    }

    @Override
    public Filter getFilter() {
        if (musicFilter == null) {
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
                filterResults.values = originalSearchResults;
                filterResults.count = originalSearchResults.size();
            } else {
                ArrayList<SearchResult> filteredSongList = new ArrayList<>();
                for (SearchResult result : originalSearchResults) {
                    if (result.getMainTitle().toLowerCase().contains(constraint.toString().toLowerCase())
                            || result.getResultType() == SearchResult.ResultType.HEADER) {
                        filteredSongList.add(result);
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
                currentSearchResults = (ArrayList<SearchResult>) results.values;
                SearchAdapter.this.notifyDataSetChanged();
            } else {
                SearchAdapter.this.notifyDataSetChanged();
            }
        }
    }


    public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView songTitle;
        public TextView songDuration;

        public SongViewHolder(View v) {
            super(v);
            songTitle = (TextView) v.findViewById(R.id.txt_song_item_title);
            songDuration = (TextView) v.findViewById(R.id.txt_song_item_duration);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            List<SongDTO> singleList = new ArrayList<>(Collections.singletonList(
                    DTOConverter.getSongDTO(MusicDataUtility.getSongById(
                            currentSearchResults.get(getLayoutPosition()).getId(), v.getContext()))
            ));

            Intent playerActivity = new Intent(v.getContext(), SwipePlayerActivity.class);
            playerActivity.putParcelableArrayListExtra(SwipePlayerActivity.SONGS_PARAM,
                    (ArrayList<? extends Parcelable>) singleList);
            playerActivity.putExtra(SwipePlayerActivity.SONG_POSITION_PARAM, 0);
            v.getContext().startActivity(playerActivity);
        }
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView albumTitle;
        public TextView artistTitle;

        public AlbumViewHolder(View v) {
            super(v);
            albumTitle = (TextView) v.findViewById(R.id.txt_album_title);
            artistTitle = (TextView) v.findViewById(R.id.txt_album_artist);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            v.getContext().startActivity(PlaylistActivity.getIntent(v.getContext(), currentSearchResults.get(getLayoutPosition()).getId()));
        }
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView artistName;
        public TextView artistDetails;

        public ArtistViewHolder(View v) {
            super(v);
            artistName = (TextView) v.findViewById(R.id.txt_artist_name);
            artistDetails = (TextView) v.findViewById(R.id.txt_artist_details);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            v.getContext().startActivity(ArtistActivity.getIntent(v.getContext(), currentSearchResults.get(getLayoutPosition()).getId()));
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView headerText;

        public HeaderViewHolder(View v) {
            super(v);
            headerText = (TextView) v.findViewById(R.id.txt_header);
        }
    }
}