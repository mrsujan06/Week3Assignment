package com.test.week3assignment.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.test.week3assignment.R;

/**
 * Created by Sujan on 03/03/2018.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;

    public CustomInfoWindowAdapter(Context context) {
        this.mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window , null);

    }

    private void bind(Marker marker , View view){
        String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.title);

        if(!title.equals("")){
            tvTitle.setText(title);
        }

        String snippet = marker.getSnippet();
        TextView tvSnippet = view.findViewById(R.id.snippet);

        if(!snippet.equals("")){
            tvSnippet.setText(snippet);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        bind(marker , mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        bind(marker , mWindow);
        return mWindow;
    }


}
