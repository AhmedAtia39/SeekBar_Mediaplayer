package com.example.ahmed.seek_bar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by AHMED on 21/05/2018.
 */

public class Adapter extends BaseAdapter {

    ArrayList<listitemFromPhone> lis = new ArrayList<>();
    Context context;

    public Adapter(ArrayList<listitemFromPhone> lis, Context context) {
        this.lis = lis;
        this.context = context;
    }

    @Override
    public int getCount() {
        return lis.size();
    }

    @Override
    public Object getItem(int position) {
        return lis.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_row_fromphone, parent, false);
        //final ImageView img = (ImageView) view.findViewById(R.id.imageView);
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvAlbum = view.findViewById(R.id.tvAlbum);
        TextView tvArtist = view.findViewById(R.id.tvArtist);
        TextView tvTitle = view.findViewById(R.id.tvTitle);

        tvName.setText(lis.get(i).name);
        tvAlbum.setText(lis.get(i).album);
        tvArtist.setText(lis.get(i).artist);
        tvTitle.setText(lis.get(i).Title);

        return view;
    }
}

