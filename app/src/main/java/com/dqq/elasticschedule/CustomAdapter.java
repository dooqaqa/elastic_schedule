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
        drag_pos = -1;
        drag_source_pos = -1;
    }

    @Override
    public View getView(int position ,View convertView,ViewGroup parent){
        View view = super.getView(position, convertView, parent);
        if (drag_pos == position || drag_source_pos == position) {
            view.setBackgroundColor(Color.argb(255, 220, 150, 150));
        }
        int colorPos = position % 2;
        if(1 == colorPos)
            view.setBackgroundColor(Color.argb(255, 220, 220, 150));
        else
            view.setBackgroundColor(Color.argb(255, 150, 220, 150));
        return view;
    }
    public static int OddColor() {
        return odd_color;
    }
    public static int EvenColor() {
        return even_color;
    }
    public static int ActiveColor() {
        return active_color;
    }
    public void SetDragSourcePos(int pos) {
        drag_source_pos = pos;
    }
    public void SetDragPos(int pos) {
        drag_pos = pos;
    }
    public void ClearDragPos() {
        drag_pos = -1;
        drag_source_pos = -1;
    }
    static private int odd_color = Color.argb(255, 220, 220, 150);
    static private int even_color = Color.argb(255, 150, 220, 150);
    static private int active_color = Color.argb(255, 220, 150, 150);
    private int drag_source_pos;
    private int drag_pos;
}
