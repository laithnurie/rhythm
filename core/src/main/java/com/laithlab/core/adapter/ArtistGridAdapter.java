package com.laithlab.core.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.laithlab.core.R;
import com.laithlab.core.db.Artist;
import com.laithlab.core.dto.ArtistDTO;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArtistGridAdapter extends BaseAdapter {

	private final LayoutInflater inflater;
	private Context context;
	private List<ArtistDTO> artists;

	public ArtistGridAdapter(Context context, List<ArtistDTO> artists) {
		this.context = context;
		this.artists = artists;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null || convertView.getTag() == null) {
			convertView = inflater.inflate(R.layout.grid_item, parent, false);

			holder = new ViewHolder();
			holder.gridItemImage = (ImageView) convertView.findViewById(R.id.grid_image);
			holder.gridItemImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
			holder.gridItemTitle = (TextView) convertView.findViewById(R.id.grid_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ArtistDTO artist = artists.get(position);
		if(artist.getArtistImageUrl() != null && !artist.getArtistImageUrl().isEmpty()){
			Picasso.with(context).load(artist.getArtistImageUrl())
					.into(holder.gridItemImage);
		}

		holder.gridItemTitle.setText(artists.get(position).getArtistName());
		return convertView;
	}

	@Override
	public int getCount() {
		return artists.size();
	}

	@Override
	public ArtistDTO getItem(int position) {
		return artists.get(position);
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
