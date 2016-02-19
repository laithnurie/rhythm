package com.laithlab.rhythm.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.laithlab.rhythm.R;
import com.laithlab.rhythm.dto.ArtistDTO;
import com.laithlab.rhythm.utils.BitmapUtils;
import com.laithlab.rhythm.utils.LRUCache;
import com.laithlab.rhythm.utils.MusicDataUtility;

import java.util.List;

public class ArtistGridAdapter extends SelectableAdapter<ArtistGridAdapter.ViewHolder> {

	private List<ArtistDTO> artists;
	private ClickListener clickListener;

	public ArtistGridAdapter(List<ArtistDTO> artists, ClickListener clickListener) {
		this.artists = artists;
		this.clickListener = clickListener;
	}

	@Override
	public ArtistGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);

		View contactView = inflater.inflate(R.layout.grid_item, parent, false);
		return new ViewHolder(contactView, clickListener);
	}

	@Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.gridItemTitle.setText(artists.get(position).getArtistName());
        if (artists.get(position).getCoverPath() != null && !artists.get(position).getCoverPath().isEmpty()) {
            if (LRUCache.getInstance().get(artists.get(position).getCoverPath()) != null) {
                holder.gridItemImage.setImageBitmap(LRUCache.getInstance()
						.get(artists.get(position).getCoverPath()));
            } else {
                byte[] imageData = MusicDataUtility.getImageData(artists.get(position).getCoverPath());
                if (imageData != null) {
                    final Bitmap bmp = BitmapUtils.decodeSampledBitmapFromResource(imageData, 200, 200);
                    holder.gridItemImage.setImageBitmap(bmp);
					LRUCache.getInstance().put(artists.get(position).getCoverPath(), bmp);
                }
            }
        } else {
            holder.gridItemImage.setImageResource(R.drawable.ic_vinyl_white_50dp);
        }
    }

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return artists.size();
	}

	public ArtistDTO getItem(int position) {
		return artists.get(position);
	}

	public void updateData(List<ArtistDTO> artists) {
		this.artists = artists;
		notifyDataSetChanged();
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
