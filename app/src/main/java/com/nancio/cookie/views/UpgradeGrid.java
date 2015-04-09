package com.nancio.cookie.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.nancio.cookie.Game;
import com.nancio.cookie.MainActivity;

/**
 * Created by nancio on 4/02/15.
 */
public class UpgradeGrid extends GridView {

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
                //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
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


    public UpgradeGrid(Context context) {
        super(context);
    }

    public UpgradeGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        //this.setAdapter(new ImageAdapter(context)));
    }

    public UpgradeGrid(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    public void update(){
    }
}
