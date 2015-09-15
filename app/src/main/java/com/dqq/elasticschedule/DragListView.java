package com.dqq.elasticschedule;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class DragListView extends ListView {
    
    private ImageView drag_image_view;
    private int dragSrcPosition;
    private int drag_position;
    
    private int drag_point;
    private int drag_offset;
    
    private WindowManager window_manager;
    private WindowManager.LayoutParams window_params;
    
    private int scale_touch_slop;
    private int up_scroll_bounce;
    private int down_scroll_bounce;
    private ArrayList<DragListViewListener> listeners = new ArrayList<DragListViewListener>();

    private GestureDetector gesture_detector;

    public interface DragListViewListener {
        void OnDragFinish(int sourcepos, int targetpos);
        boolean IsPositionDragable(int position);
    }
    public void AddListener(DragListViewListener listener) {
        listeners.add(listener);
    }
    private void NotifyItemPositionChanged(int sourcepos, int targetpos) {
        for (DragListViewListener ls : listeners) {
            ls.OnDragFinish(sourcepos, targetpos);
        }
    }
    private boolean IsPositionDragable(int position) {
        boolean ret = true;
        for (DragListViewListener ls : listeners)
            if (!ls.IsPositionDragable(position)) {
                ret = false;
                break;
            }
        return ret;
    }
    
    public DragListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        scale_touch_slop = ViewConfiguration.get(context).getScaledTouchSlop();
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_DOWN){
            int x = (int)ev.getX();
            int y = (int)ev.getY();
            
            dragSrcPosition = drag_position = pointToPosition(x, y);
            if(drag_position ==AdapterView.INVALID_POSITION || !IsPositionDragable(drag_position)){
                return super.onInterceptTouchEvent(ev);
            }

            View itemView = (View) getChildAt(drag_position -getFirstVisiblePosition());
            drag_point = y - itemView.getTop();
            drag_offset = (int) (ev.getRawY() - y);

            if(itemView != null && x > itemView.getLeft() - 20){
                up_scroll_bounce = Math.min(y - scale_touch_slop, getHeight() / 3);
                down_scroll_bounce = Math.max(y + scale_touch_slop, getHeight() * 2 / 3);

                itemView.setBackgroundColor(CustomAdapter.ActiveColor());
                itemView.setDrawingCacheEnabled(true);
                Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
                itemView.setDrawingCacheEnabled(false);
                startDrag(bm, y);
            }
            return false;
         }
         return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(drag_image_view !=null&& drag_position !=INVALID_POSITION){
            int action = ev.getAction();
            switch(action){
                case MotionEvent.ACTION_UP:
                    int upY = (int)ev.getY();
                    stopDrag();
                    onDrop(upY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int moveY = (int)ev.getY();
                    onDrag(moveY);
                    break;
                default:break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    public void startDrag(Bitmap bm ,int y){
        stopDrag();
        
        window_params = new WindowManager.LayoutParams();
        window_params.gravity = Gravity.TOP;
        window_params.x = 0;
        window_params.y = y - drag_point + drag_offset;
        window_params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        window_params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window_params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        window_params.format = PixelFormat.TRANSLUCENT;
        window_params.windowAnimations = 0;

        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(bm);
        window_manager = (WindowManager)getContext().getSystemService("window");
        window_manager.addView(imageView, window_params);
        drag_image_view = imageView;
    }

    public void stopDrag(){
        if(drag_image_view !=null){
            window_manager.removeView(drag_image_view);
            drag_image_view = null;
        }
    }

    public void onDrag(int y) {
        if(drag_image_view !=null){
            window_params.alpha = 0.6f;
            window_params.y = y - drag_point + drag_offset;
            window_manager.updateViewLayout(drag_image_view, window_params);
        }
        int tempPosition = pointToPosition(0, y);
        int oldpos = drag_position;
        if(tempPosition != INVALID_POSITION && IsPositionDragable(tempPosition)) {
            drag_position = tempPosition;
        }

        int scrollHeight = 0;
        if(y < up_scroll_bounce) {
            scrollHeight = 8;
        } else if (y > down_scroll_bounce) {
            scrollHeight = -8;
        }
        
        if(scrollHeight != 0){
            setSelectionFromTop(drag_position, getChildAt(drag_position -getFirstVisiblePosition()).getTop()+scrollHeight);
        }
        if(drag_position != oldpos && drag_position >= 0 && drag_position < getAdapter().getCount() && IsPositionDragable(drag_position)) {
            ArrayAdapter adapter = (ArrayAdapter)getAdapter();
            String dragItem = (String)adapter.getItem(oldpos);
            adapter.remove(dragItem);
            adapter.insert(dragItem, drag_position);
        }
    }

    public void onDrop(int y){
        int oldpos = drag_position;
        int tempPosition = pointToPosition(0, y);
        if (tempPosition == INVALID_POSITION && y < getChildAt(1).getTop()) {
            tempPosition = 0;
        }
        if(tempPosition != INVALID_POSITION && IsPositionDragable(tempPosition)) {
            drag_position = tempPosition;
        }

        if(drag_position >= 0 && drag_position < getAdapter().getCount() && IsPositionDragable(drag_position)) {
            ArrayAdapter adapter = (ArrayAdapter)getAdapter();
            String dragItem = (String)adapter.getItem(oldpos);
            adapter.remove(dragItem);
            adapter.insert(dragItem, drag_position);
            Toast.makeText(getContext(), dragItem, Toast.LENGTH_SHORT).show();
            NotifyItemPositionChanged(dragSrcPosition, drag_position);
        } else if (oldpos == drag_position) {
            ArrayAdapter adapter = (ArrayAdapter)getAdapter();
            adapter.notifyDataSetInvalidated();
        }

        View itemView = getChildAt(dragSrcPosition);
        //itemView.setBackgroundColor(dragSrcPosition % 2 == 1? CustomAdapter.OddColor() : CustomAdapter.EvenColor());
    }
}
