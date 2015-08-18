package com.dqq.elasticschedule;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by ZhengJiefu on 2015/8/18.
 */
public class CustomAdapter extends ArrayAdapter<String>{
    public CustomAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position ,View convertView,ViewGroup parent){
        View view = super.getView(position, convertView, parent);
        int colorPos = position % 2;
        if(1 == colorPos)
            view.setBackgroundColor(Color.argb(255, 220, 220, 150));
        else
            view.setBackgroundColor(Color.argb(255, 150, 220, 150));
        return view;
    }
}
