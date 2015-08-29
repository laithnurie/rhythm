package com.laithlab.core.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.laithlab.core.R;
import com.laithlab.core.db.Album;
import com.laithlab.core.dto.AlbumDTO;
import com.squareup.picasso.Picasso;
import io.realm.RealmList;

import java.util.List;

public class AlbumGridAdapter extends BaseAdapter {

	private final LayoutInflater inflater;
	private Context context;
	private List<AlbumDTO> albums;

	public AlbumGridAdapter(Context context, List<AlbumDTO> albums) {
		this.context = context;
		this.albums = albums;
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

		//		Logic to upload image to grid
		//		Picasso.with(context).load(artists.get(position).getArtistImageUrl()).into(holder.gridItemImage);
		Picasso.with(context).load("http://artwork-cdn.7static.com/static/img/sleeveart/00/009/559/0000955983_200.jpg")
				.into(holder.gridItemImage);

		holder.gridItemTitle.setText(albums.get(position).getAlbumTitle());
		return convertView;
	}

	@Override
	public int getCount() {
		return albums.size();
	}

	@Override
	public AlbumDTO getItem(int position) {
		return albums.get(position);
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
