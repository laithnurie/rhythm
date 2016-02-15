package com.laithlab.rhythm.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.laithlab.rhythm.R;
import com.laithlab.rhythm.db.Playlist;
import com.laithlab.rhythm.utils.BitmapUtils;
import com.laithlab.rhythm.utils.LRUCache;
import com.laithlab.rhythm.utils.MusicDataUtility;

import java.util.List;

public class PlaylistGridAdapter extends SelectableAdapter<PlaylistGridAdapter.ViewHolder> {

    private List<Playlist> playlists;
    private ClickListener clickListener;

    public PlaylistGridAdapter(List<Playlist> playlists, ClickListener clickListener) {
        this.playlists = playlists;
        this.clickListener = clickListener;
    }

    @Override
    public PlaylistGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(contactView, clickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.gridItemTitle.setText(playlists.get(position).getPlaylistName());
        if (playlists.get(position).getCoverPath() != null && !playlists.get(position).getCoverPath().isEmpty()) {
            if (LRUCache.getInstance().get(playlists.get(position).getCoverPath()) != null) {
                holder.gridItemImage.setImageBitmap(LRUCache.getInstance()
                        .get(playlists.get(position).getCoverPath()));
            } else {
                byte[] imageData = MusicDataUtility.getImageData(playlists.get(position).getCoverPath());
                if (imageData != null) {
                    final Bitmap bmp = BitmapUtils.decodeSampledBitmapFromResource(imageData, 200, 200);
                    holder.gridItemImage.setImageBitmap(bmp);
                    LRUCache.getInstance().put(playlists.get(position).getCoverPath(), bmp);
                }
            }
        } else {
            holder.gridItemImage.setImageResource(R.drawable.ic_play_arrow_white);
        }

        if(isSelected(position)){
            holder.gridItemTitle.setBackgroundResource(R.color.red);
        } else {
            holder.gridItemTitle.setBackgroundResource(R.color.transparent_black);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public Playlist getItem(int position) {
        return playlists.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ImageView gridItemImage;
        private TextView gridItemTitle;
        private ClickListener clickListener;

        public ViewHolder(View itemView, ClickListener clickListener) {
            super(itemView);
            gridItemImage = (ImageView) itemView.findViewById(R.id.grid_image);
            gridItemTitle = (TextView) itemView.findViewById(R.id.grid_title);
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClicked(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            return clickListener != null && clickListener.onItemLongClicked(getLayoutPosition());
        }
    }

    public interface ClickListener {
        void onItemClicked(int position);

        boolean onItemLongClicked(int position);
    }

}
