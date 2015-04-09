package com.nancio.cookie.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nancio.cookie.Game;
import com.nancio.cookie.R;

/**
 * Created by nancio on 4/01/15.
 */
public class BuildingView extends LinearLayout {
    public BuildingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater layoutInflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.building_layout, this);
        TypedArray a = context.getTheme().
                obtainStyledAttributes(attrs, R.styleable.BuildingView, 0, 0);

        int i = a.getInt(R.styleable.BuildingView_index, 0);

            ((ImageView) findViewById(R.id.building_image)).setImageResource(
                    a.getResourceId(R.styleable.BuildingView_image, 0));
            ((TextView)findViewById(R.id.building_name)).setText(a.getText(R.styleable.BuildingView_name));
            ((TextView)findViewById(R.id.building_cost)).setText(Game.getBuildingCost(i).getFormattedRounded());

        ((TextView)findViewById(R.id.building_n)).setText(Game.getBuildingN(i)+"");

    }

    public String getBuildingId(){
        return (String)((TextView)findViewById(R.id.building_name)).getText();
    }

    public void setCost(String c){
        ((TextView)findViewById(R.id.building_cost)).setText(c);
    }

    public void setN(int n){
        ((TextView)findViewById(R.id.building_n)).setText(n+"");
    }
}
