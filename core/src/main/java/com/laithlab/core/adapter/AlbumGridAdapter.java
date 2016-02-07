package com.laithlab.core.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.laithlab.core.R;
import com.laithlab.core.dto.AlbumDTO;
import com.laithlab.core.utils.LRUCache;
import com.laithlab.core.utils.MusicDataUtility;

import java.util.List;

public class AlbumGridAdapter extends SelectableAdapter<AlbumGridAdapter.ViewHolder> {

    private List<AlbumDTO> albums;
    private ClickListener clickListener;

    public AlbumGridAdapter(List<AlbumDTO> albums, ClickListener clickListener) {
        this.albums = albums;
        this.clickListener = clickListener;
    }

    @Override
    public AlbumGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(contactView, clickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.gridItemTitle.setText(albums.get(position).getAlbumTitle());
        if (albums.get(position).getCoverPath() != null && !albums.get(position).getCoverPath().isEmpty()) {
            if (LRUCache.getInstance().get(albums.get(position).getCoverPath()) != null) {
                holder.gridItemImage.setImageBitmap(LRUCache.getInstance()
                        .get(albums.get(position).getCoverPath()));
            } else {
                byte[] imageData = MusicDataUtility.getImageData(albums.get(position).getCoverPath());
                if (imageData != null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                    holder.gridItemImage.setImageBitmap(bmp);
                    LRUCache.getInstance().put(albums.get(position).getCoverPath(), bmp);
                }
            }
        } else {
            holder.gridItemImage.setImageResource(R.drawable.ic_play_arrow_white);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public AlbumDTO getItem(int position) {
        return albums.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView gridItemImage;
        TextView gridItemTitle;
        private ClickListener clickListener;

        public ViewHolder(View itemView, ClickListener clickListener) {
            super(itemView);
            gridItemImage = (ImageView) itemView.findViewById(R.id.grid_image);
            gridItemTitle = (TextView) itemView.findViewById(R.id.grid_title);
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClicked(getAdapterPosition());
            }
        }
    }

    public interface ClickListener {
        void onItemClicked(int position);
    }

}
