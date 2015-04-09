package com.nancio.cookie;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by nancio on 19/01/15.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private Game.UpgradeManager.Upgrade ups[];
    private int state;

    public ImageAdapter(Context context, int getStates){
        mContext = context;
        state = getStates;
        if(getStates==1) ups = Game.UpgradeManager.getUnlocked();
        else ups = Game.UpgradeManager.getBought();
    }

    @Override
    public int getCount() {
        return ups.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView == null){
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(30, 30));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }else imageView = (ImageView)convertView;

        final int i = position;

        if(state==1) {

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) mContext).upgradeClick(ups[i]);
                }
            });
        }

        imageView.setImageResource(ups[position].image);
        return imageView;
    }
}
