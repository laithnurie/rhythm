package com.laithlab.core.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.laithlab.core.R;
import com.laithlab.core.db.Playlist;
import com.laithlab.core.utils.MusicDataUtility;

import java.util.List;

public class PlaylistGridAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private List<Playlist> playlists;

    public PlaylistGridAdapter(Context context, List<Playlist> playlists) {
        this.playlists = playlists;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            convertView = inflater.inflate(R.layout.grid_item, parent, false);

            holder = new ViewHolder();
            holder.gridItemImage = (ImageView) convertView.findViewById(R.id.grid_image);
            holder.gridItemImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.gridItemTitle = (TextView) convertView.findViewById(R.id.grid_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.gridItemImage.setImageResource(R.drawable.ic_play_arrow_white);
        if (playlists.get(position).getCoverPath() != null && !playlists.get(position).getCoverPath().isEmpty()) {
            byte[] imageData = MusicDataUtility.getImageData(playlists.get(position).getCoverPath());
            if (imageData != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                holder.gridItemImage.setImageBitmap(bmp);
            }
        }

        holder.gridItemTitle.setText(playlists.get(position).getPlaylistName());
        return convertView;
    }

    @Override
    public int getCount() {
        return playlists.size();
    }

    @Override
    public Playlist getItem(int position) {
        return playlists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private static class ViewHolder {
        ImageView gridItemImage;
        TextView gridItemTitle;
    }

}
